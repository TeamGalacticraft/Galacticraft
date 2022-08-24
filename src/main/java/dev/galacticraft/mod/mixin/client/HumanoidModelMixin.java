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

import dev.galacticraft.mod.entity.RocketEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
@Environment(EnvType.CLIENT)
public abstract class HumanoidModelMixin<T extends LivingEntity> {
    @Final @Shadow public ModelPart head;

    @Final @Shadow public ModelPart leftArm;

    @Final @Shadow public ModelPart rightArm;

    @Final @Shadow public ModelPart leftLeg;

    @Final
    @Shadow
    public ModelPart rightLeg;

    @Shadow @Final public ModelPart hat;

    @Shadow @Final public ModelPart body;

    @Inject(at = @At("HEAD"), method = "setupAnim")
    private void standInRocketGC(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (((HumanoidModel<T>) (Object) this).riding) {
            if (livingEntity.getVehicle() instanceof RocketEntity) {
                ((HumanoidModel<T>) (Object) this).riding = false;
            }
        }
    }

//    @Inject(method = "setAngles", at = @At(value = "TAIL"))
//    private void rotateToMatchRocket(T entity, float f, float g, float h, float i, float j, CallbackInfo ci) {
//        if (entity.hasVehicle()) {
//            if (entity.getVehicle() instanceof RocketEntity) {
//                GlStateManager.rotatef((entity.getVehicle().getYaw() - 180.0F) * -1.0F, 0.0F, 1.0F, 0.0F); //todo: what is this mess??
//                GlStateManager.rotatef(entity.getVehicle().getPitch() * -1.0F, 1.0F, 0.0F, 0.0F); //what was i thinking?
//            }
//        }
//    }

    @Inject(method = "setupAnim", at = @At(value = "RETURN"))
    private void rotateToMatchRocketHeadRender(T entity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (entity.isPassenger()) {
            if (entity.getVehicle() instanceof RocketEntity) {
                this.head.xRot = 0.0F;
                this.leftArm.xRot = 0.0F;
                this.leftArm.yRot = entity.getVehicle().getXRot() * -1.0F;
                this.rightArm.xRot = 0.0F;
                this.rightArm.yRot = entity.getVehicle().getXRot() * -1.0F;
                this.leftLeg.xRot = 0.0F;
                this.leftLeg.yRot = entity.getVehicle().getXRot() * -1.0F;
                this.rightLeg.xRot = 0.0F;
                this.rightLeg.yRot = entity.getVehicle().getXRot() * -1.0F;
                this.hat.xRot = 0.0F;
                this.hat.yRot = entity.getVehicle().getXRot() * -1.0F;
                this.body.xRot = 0.0F;
                this.body.yRot = entity.getVehicle().getXRot() * -1.0F;
            }
        }
    }
}