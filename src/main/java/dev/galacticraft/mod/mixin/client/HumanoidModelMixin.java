/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import dev.galacticraft.mod.content.entity.orbital.AdvancedVehicle;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import dev.galacticraft.mod.content.item.RocketItem;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends LivingEntity> {
    @Shadow
    @Final
    public ModelPart head;

    @Shadow
    @Final
    public ModelPart leftArm;

    @Shadow
    @Final
    public ModelPart rightArm;

    @Shadow
    @Final
    public ModelPart leftLeg;

    @Shadow
    @Final
    public ModelPart rightLeg;

    @Shadow
    @Final
    public ModelPart body;

    @Shadow
    @Final
    public ModelPart hat;

    @Shadow
    public HumanoidModel.ArmPose leftArmPose;

    @Shadow
    public HumanoidModel.ArmPose rightArmPose;

    @Inject(at = @At("HEAD"), method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V")
    private void standInRocketGC(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (((HumanoidModel<T>) (Object) this).riding && livingEntity.getVehicle() instanceof RocketEntity) {
            ((HumanoidModel<T>) (Object) this).riding = false;
        }
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getMainArm()Lnet/minecraft/world/entity/HumanoidArm;"))
    private void gc$modifyPlayerAnim(LivingEntity entity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        Holder<CelestialBody<?, ?>> holder = entity.level().galacticraft$getCelestialBody();
        if (holder != null && holder.value().gravity() < 0.8) {
            float angularSwingArm = Mth.cos(f * 0.1162F) * 2.0F * g;
            this.leftArm.xRot = angularSwingArm;
            this.rightArm.xRot = -angularSwingArm;

            float angularSwingLeg = Mth.cos(f * 0.1162F * 2.0F) * 1.4F * g;
            this.leftLeg.xRot = -angularSwingLeg;
            this.rightLeg.xRot = angularSwingLeg;
        }
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void gc$overridePlayerAnim(LivingEntity entity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        boolean mainHand = entity.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof RocketItem;
        boolean offHand = entity.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof RocketItem;
        if (mainHand || offHand) {
            if (entity.getVehicle() instanceof AdvancedVehicle) {
                Holder<CelestialBody<?, ?>> holder = entity.level().galacticraft$getCelestialBody();
                float angularSwingArm;
                if (holder != null && holder.value().gravity() < 0.8) {
                    angularSwingArm = Mth.cos(f * 0.1162F) * 2.0F * g;
                } else {
                    angularSwingArm = Mth.cos(f * 0.6662F) * g;
                }

                boolean rightHanded = entity.getMainArm() == HumanoidArm.RIGHT;
                if ((mainHand && rightHanded) || (offHand && !rightHanded)) {
                    this.rightArm.xRot = -angularSwingArm;
                    this.rightArm.zRot = 0.0F;
                    this.rightArm.yRot = 0.0F;
                    AnimationUtils.bobModelPart(this.rightArm, h, 1.0f);
                }
                if ((offHand && rightHanded) || (mainHand && !rightHanded)) {
                    this.leftArm.xRot = angularSwingArm;
                    this.leftArm.zRot = 0.0F;
                    this.leftArm.yRot = 0.0F;
                    AnimationUtils.bobModelPart(this.leftArm, h, -1.0f);
                }
            } else {
                // HANDS UP!
                float armXRot = Mth.PI + 0.3F;
                float armZRot = 0.1F * Mth.PI;
                this.leftArm.setRotation(armXRot, 0.0F, armZRot);
                this.rightArm.setRotation(armXRot, 0.0F, -armZRot);
            }
        }

        if (entity.isInCryoSleep()) {
            this.head.setRotation(45.0F, 0.0F, 0.0F);
            this.hat.copyFrom(this.head);
            this.body.resetPose();
            this.leftArm.resetPose();
            this.rightArm.resetPose();
            this.leftLeg.resetPose();
            this.rightLeg.resetPose();
        }
    }
}
