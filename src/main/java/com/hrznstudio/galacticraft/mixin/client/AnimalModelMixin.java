/*
 * Copyright (c) 2019-2021 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hrznstudio.galacticraft.mixin.client;

import com.hrznstudio.galacticraft.accessor.AnimalModelGearAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(AnimalModel.class)
@Environment(EnvType.CLIENT)
public abstract class AnimalModelMixin<E extends Entity> extends EntityModel<E> implements AnimalModelGearAccessor {
    @Shadow protected abstract Iterable<ModelPart> getHeadParts();

    @Shadow @Final private float childHeadYOffset;
    @Shadow @Final private float childHeadZOffset;
    @Shadow @Final private float invertedChildHeadScale;
    @Shadow @Final private boolean headScaled;
    @Unique
    private final List<ModelPart> maskModels = new ArrayList<>();

    @Inject(method = "render", at = @At("RETURN"))
    private void renderGearGC(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (getOxygenMaskModels().get(0).visible) {
            if (!child) {
                for (ModelPart oxygenMaskModel : getOxygenMaskModels()) {
                    oxygenMaskModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }
            } else {
                matrices.push();
                float g;
                if (this.headScaled) {
                    g = 1.5F / this.invertedChildHeadScale;
                    matrices.scale(g, g, g);
                }

                matrices.translate(0.0D, (this.childHeadYOffset / 16.0F), (this.childHeadZOffset / 16.0F));
                for (ModelPart oxygenMaskModel : getOxygenMaskModels()) {
                    oxygenMaskModel.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }
                matrices.pop();
            }
        }
    }

    @Override
    public List<ModelPart> getOxygenMaskModels() {
        if (maskModels.isEmpty()) {
            for (ModelPart head : this.getHeadParts()) {
                ModelPart modelPart = new ModelPart(this, 0, 0);
                modelPart.setPivot(head.pivotX, head.pivotY, head.pivotZ);
                modelPart.mirror = head.mirror;
                for (ModelPart.Cuboid cuboid : head.cuboids) {
                    modelPart.cuboids.add(new ModelPart.Cuboid(0, 0, cuboid.minX, cuboid.minY, cuboid.minZ, cuboid.maxX - cuboid.minX, cuboid.maxY - cuboid.minY, cuboid.maxZ - cuboid.minZ, 1.0F, 1.0F, 1.0F, head.mirror, textureWidth, textureHeight));
                }
                maskModels.add(modelPart);
            }
        }
        Iterator<ModelPart> headParts = this.getHeadParts().iterator();
        for (ModelPart modelPart : maskModels) {
            ModelPart head = headParts.next();
            modelPart.pitch = head.pitch;
            modelPart.yaw = head.yaw;
            modelPart.roll = head.roll;
        }
        return maskModels;
    }
}
