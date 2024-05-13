/*
 * Copyright (c) 2019-2024 Team Galacticraft
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
import dev.galacticraft.mod.village.MoonVillagerTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.NoSuchElementException;

@Mixin(VillagerModel.class)
@Environment(EnvType.CLIENT)
public abstract class VillagerModelMixin<T extends Entity> {
    @Shadow public abstract void hatVisible(boolean visible);
    @Shadow @Final private ModelPart head;

    private @Unique ModelPart brain = null;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void getModelData_gc(ModelPart root, CallbackInfo ci) {
        try {
            this.brain = root.getChild(PartNames.HEAD).getChild(Constant.ModelPartName.MOON_VILLAGER_BRAIN);
        } catch (NoSuchElementException ignore) {}
    }

    @Inject(method = "createBodyModel", at = @At(value = "RETURN"))
    private static void getModelData_gc(CallbackInfoReturnable<MeshDefinition> cir) {
        cir.getReturnValue().getRoot().getChild(PartNames.HEAD).addOrReplaceChild(Constant.ModelPartName.MOON_VILLAGER_BRAIN, CubeListBuilder.create().texOffs(0, 38).addBox(-5.0F, -16.0F, -5.0F, 10.0F, 8.0F, 10.0F), PartPose.ZERO);
    }

    @Inject(method = "setupAnim", at = @At(value = "RETURN"))
    private void getModelData_gc(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (entity instanceof Villager villager) {
            if (MoonVillagerTypes.MOON_VILLAGER_TYPE_REGISTRY.contains(villager.getVillagerData().getType())) {
                assert brain != null;

                this.hatVisible(false);
                this.brain.visible = this.head.visible;
                this.brain.yRot = this.head.yRot;
                this.brain.xRot = this.head.xRot;
                this.brain.x = this.head.x;
                this.brain.y = this.head.y;
                this.brain.z = this.head.z;
                this.brain.zRot = this.head.zRot;
            }
        }
    }
}
