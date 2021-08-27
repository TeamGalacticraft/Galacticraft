/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.mixin.client;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.village.MoonVillagerType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.NoSuchElementException;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(VillagerResemblingModel.class)
@Environment(EnvType.CLIENT)
public abstract class VillagerResemblingModelMixin<T extends Entity> {
    @Shadow public abstract void setHatVisible(boolean visible);
    @Shadow @Final private ModelPart head;

    private @Unique ModelPart brain = null;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void getModelData_gc(ModelPart root, CallbackInfo ci) {
        try {
            this.brain = root.getChild(EntityModelPartNames.HEAD).getChild(Constant.ModelPartName.MOON_VILLAGER_BRAIN);
        } catch (NoSuchElementException ignore) {}
    }

    @Inject(method = "getModelData", at = @At(value = "RETURN"))
    private static void getModelData_gc(CallbackInfoReturnable<ModelData> cir) {
        cir.getReturnValue().getRoot().getChild(EntityModelPartNames.HEAD).addChild(Constant.ModelPartName.MOON_VILLAGER_BRAIN, ModelPartBuilder.create().uv(0, 38).cuboid(-5.0F, -16.0F, -5.0F, 10.0F, 8.0F, 10.0F), ModelTransform.NONE);
    }

    @Inject(method = "setAngles", at = @At(value = "RETURN"))
    private void getModelData_gc(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (entity instanceof VillagerEntity villager) {
            if (MoonVillagerType.MOON_VILLAGER_TYPE_REGISTRY.contains(villager.getVillagerData().getType())) {
                assert brain != null;

                this.setHatVisible(false);
                this.brain.visible = this.head.visible;
                this.brain.yaw = this.head.yaw;
                this.brain.pitch = this.head.pitch;
                this.brain.pivotX = this.head.pivotX;
                this.brain.pivotY = this.head.pivotY;
                this.brain.pivotZ = this.head.pivotZ;
                this.brain.roll = this.head.roll;
            }
        }
    }
}
