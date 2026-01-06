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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.galacticraft.mod.content.entity.vehicle.RocketEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Shadow
    protected EntityModel<?> model;

    @Unique
    private static float sleepDirectionToRotationCryo(Direction direction) {
        return switch (direction) {
            default -> 0.0F;
            case EAST -> 270.0F;
            case SOUTH -> 180.0F;
            case WEST -> 90.0F;
        };
    }

    @WrapOperation(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasPose(Lnet/minecraft/world/entity/Pose;)Z"))
    private boolean gc$hasSleepPose(LivingEntity instance, Pose pose, Operation<Boolean> original) {
        return instance.isInCryoSleep() ? false : original.call(instance, pose);
    }

    @Inject(method = "setupRotations", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getBedOrientation()Lnet/minecraft/core/Direction;"), cancellable = true)
    private void galacticraft$renderCryoChamberPos(LivingEntity entity, PoseStack pose, float animationProgress, float bodyYaw, float tickDelta, float scale, CallbackInfo ci) {
        if (entity.isInCryoSleep()) {
            Direction direction = entity.getBedOrientation();
            float j = direction != null ? sleepDirectionToRotationCryo(direction) : bodyYaw;
            pose.translate(0, 0.82F, 0);
            pose.mulPose(Axis.YP.rotationDegrees(j));
            ci.cancel();
        }
    }

    @Inject(method = "setupRotations", at = @At("HEAD"))
    private void rotateToMatchRocket(LivingEntity entity, PoseStack pose, float animationProgress, float bodyYaw, float tickDelta, float scale, CallbackInfo ci) {
        if (entity.isPassenger() && entity.getVehicle() instanceof RocketEntity rocket) {
            double amplitude = switch(rocket.getLaunchStage()) {
                case IGNITED -> 0.1D;
                case LAUNCHED -> 0.04D;
                default -> 0.0D;
            };
            if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                amplitude *= 0.5D;
            }
            if (amplitude > 0.0D) {
                pose.translate((entity.level().random.nextDouble() - 0.5D) * amplitude, 0, (entity.level().random.nextDouble() - 0.5D) * amplitude);
            }

            Quaternionf rotation = new Quaternionf();
            rotation.rotateYXZ(-rocket.getViewYRot(tickDelta) * Mth.DEG_TO_RAD, rocket.getViewXRot(tickDelta) * Mth.DEG_TO_RAD, 0);
            rotation.mul(Axis.YP.rotationDegrees(rocket.getViewYRot(tickDelta)));
            pose.rotateAround(rotation, 0.0F, 0.5F, 0.0F);
        }
    }
}
