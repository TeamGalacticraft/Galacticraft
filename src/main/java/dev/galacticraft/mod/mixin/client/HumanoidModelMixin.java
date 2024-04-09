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

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import dev.galacticraft.mod.content.item.RocketItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Math;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends LivingEntity> {
    @Shadow @Final public ModelPart head;

    @Shadow @Final public ModelPart leftArm;

    @Shadow @Final public ModelPart rightArm;

    @Shadow @Final public ModelPart leftLeg;

    @Shadow @Final public ModelPart rightLeg;

    @Shadow @Final public ModelPart body;

    @Shadow @Final public ModelPart hat;

    @Shadow public HumanoidModel.ArmPose rightArmPose;

    @Inject(at = @At("HEAD"), method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V")
    private void standInRocketGC(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (((HumanoidModel<T>) (Object) this).riding) {
            if (livingEntity.getVehicle() instanceof RocketEntity) {
                ((HumanoidModel<T>) (Object) this).riding = false;
            }
        }
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void gc$modifyPlayerAnim(LivingEntity entity, float f, float g, float h, float i, float j, CallbackInfo ci) {

        CelestialBody.getByDimension(entity.level()).ifPresent(celestialBody -> {
            if (celestialBody.gravity() > .8)
                return;
            float speedModifier = 0.1162F * 2;

            final float floatPI = 3.1415927F;

            float angularSwingArm = Mth.cos(f * (speedModifier / 2));
            float rightMod = this.rightArmPose == HumanoidModel.ArmPose.ITEM ? 1 : 2;
            this.rightArm.xRot -= Mth.cos(f * 0.6662F + floatPI) * rightMod * g * 0.5F;
            this.leftArm.xRot -= Mth.cos(f * 0.6662F) * 2.0F * g * 0.5F;
            this.rightArm.xRot += -angularSwingArm * 4.0F * g * 0.5F;
            this.leftArm.xRot += angularSwingArm * 4.0F * g * 0.5F;
            this.leftLeg.xRot -= Mth.cos(f * 0.6662F + floatPI) * 1.4F * g;
            this.leftLeg.xRot += Mth.cos(f * 0.1162F * 2 + floatPI) * 1.4F * g;
            this.rightLeg.xRot -= Mth.cos(f * 0.6662F) * 1.4F * g;
            this.rightLeg.xRot += Mth.cos(f * 0.1162F * 2) * 1.4F * g;
        });

        if (entity.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof RocketItem || entity.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof RocketItem) {
            float armXRot = Mth.PI + 0.3F;
            this.leftArm.xRot = armXRot;
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = Mth.PI / 10.0F;

            this.rightArm.xRot = armXRot;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = (float) -Math.PI / 10.0F;
        }
        
        
        if (entity.isInCryoSleep()) { // TODO: possibly cleaner way of doing this?
            this.hat.xRot = 45F;
            this.hat.yRot = 0;
            this.head.xRot = 45F;
            this.head.yRot = 0;
            this.leftArm.xRot = 0;
            this.leftArm.yRot = 0;
            this.rightArm.xRot = 0;
            this.rightArm.yRot = 0;
            this.leftLeg.xRot = 0;
            this.leftLeg.yRot = 0;
            this.rightLeg.xRot = 0;
            this.rightLeg.yRot = 0;
            this.body.xRot = 0;
            this.body.yRot = 0;
        }
    }
}
