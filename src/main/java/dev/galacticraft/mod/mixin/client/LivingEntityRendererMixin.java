/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.galacticraft.mod.accessor.CryogenicAccessor;
import dev.galacticraft.mod.content.entity.RocketEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Shadow protected EntityModel<?> model;

    private static float sleepDirectionToRotationCryo(Direction direction) {
        return switch (direction) {
            default -> 0.0F;
            case EAST -> 270.0F;
            case SOUTH -> 180.0F;
            case WEST -> 90.0F;
        };
    }

    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasPose(Lnet/minecraft/world/entity/Pose;)Z"))
    private boolean gc$hasSleepPose(LivingEntity instance, Pose pose) {
        if (instance instanceof CryogenicAccessor player && player.isInCryoSleep())
            return false;
        return instance.hasPose(pose);
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getAttackAnim(Lnet/minecraft/world/entity/LivingEntity;F)F"))
    private void rotateToMatchRocket(LivingEntity entity, float f, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if (entity.isPassenger()) {
            if (entity.getVehicle() instanceof RocketEntity) {
                poseStack.mulPose(Axis.YN.rotationDegrees(entity.getVehicle().getViewYRot(tickDelta)));
                poseStack.translate(0, 0.5D, 0);
                poseStack.mulPose(Axis.XN.rotationDegrees(entity.getVehicle().getViewXRot(tickDelta)));
                poseStack.translate(0, -0.5D, 0);
            }
        }
    }

    @Inject(method = "setupRotations", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getBedOrientation()Lnet/minecraft/core/Direction;"), cancellable = true)
    private void galacticraft$renderCryoChamberPos(LivingEntity livingEntity, PoseStack poseStack, float f, float g, float h, CallbackInfo ci) {
        if (livingEntity instanceof CryogenicAccessor livingEntityAccessor && livingEntityAccessor.isInCryoSleep()) {
            Direction direction = livingEntity.getBedOrientation();
            float j = direction != null ? sleepDirectionToRotationCryo(direction) : g;
            poseStack.mulPose(Axis.YP.rotationDegrees(j));
            ci.cancel();
        }
    }
}
