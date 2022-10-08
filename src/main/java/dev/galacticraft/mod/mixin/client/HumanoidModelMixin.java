/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import dev.galacticraft.mod.accessor.LivingEntityAccessor;
import dev.galacticraft.mod.entity.RocketEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
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

    @Inject(at = @At("HEAD"), method = "setupAnim")
    private void standInRocketGC(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (((HumanoidModel<T>) (Object) this).riding) {
            if (livingEntity.getVehicle() instanceof RocketEntity) {
                ((HumanoidModel<T>) (Object) this).riding = false;
            }
        }
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void gc$setCryoSleepAnim(LivingEntity entity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (((LivingEntityAccessor)entity).isInCryoSleep()) { // TODO: possibly cleaner way of doing this?
            this.hat.xRot = 0;
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

        if (entity.isPassenger()) {
            if (entity.getVehicle() instanceof RocketEntity) {
//                this.head.xRot = 0.0F; TODO: rework this
//                this.leftArm.xRot = 0.0F;
//                this.leftArm.yRot = entity.getVehicle().getYRot() * -1.0F;
//                this.rightArm.xRot = 0.0F;
//                this.rightArm.yRot = entity.getVehicle().getYRot() * -1.0F;
//                this.leftLeg.xRot = 0.0F;
//                this.leftLeg.yRot = entity.getVehicle().getYRot() * -1.0F;
//                this.rightLeg.xRot = 0.0F;
//                this.rightLeg.yRot = entity.getVehicle().getYRot() * -1.0F;
//                this.hat.xRot = 0.0F;
//                this.hat.yRot = entity.getVehicle().getYRot() * -1.0F;
//                this.body.xRot = 0.0F;
//                this.body.yRot = entity.getVehicle().getYRot() * -1.0F;
            }
        }
    }
}
