package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> {

    @Shadow public Cuboid head;

    @Shadow public Cuboid leftArm;

    @Shadow public Cuboid rightArm;

    @Shadow public Cuboid leftLeg;

    @Shadow public Cuboid rightLeg;

    @Shadow public Cuboid headwear;

    @Shadow public Cuboid body;

    @Inject(at=@At("HEAD"), method = "method_17087")
    private void standInRocketGC(T livingEntity_1, float float_1, float float_2, float float_3, float float_4, float float_5, float float_6, CallbackInfo ci) {
        if (((BipedEntityModel<T>) (Object) this).isRiding) {
            if (livingEntity_1.getVehicle() instanceof RocketEntity) {
                ((BipedEntityModel<T>) (Object) this).isRiding = false;
            }
        }
    }

    @Inject(method = "method_17088", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;pushMatrix()V", shift = At.Shift.AFTER, ordinal = 1))
    private void rotateToMatchRocket(T entity, float float_1, float float_2, float float_3, float float_4, float float_5, float float_6, CallbackInfo ci) {
        if (entity.hasVehicle()) {
            if (entity.getVehicle() instanceof RocketEntity) {
                GlStateManager.rotatef(((entity.getVehicle().yaw - 180.0F) * -1.0F), 0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(((entity.getVehicle().pitch) * -1.0F), 1.0F, 0.0F, 0.0F);
            }
        }
    }

    @Inject(method = "method_17088", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;pushMatrix()V", shift = At.Shift.AFTER, ordinal = 0))
    private void rotateToMatchRocketHeadRender(T entity, float float_1, float float_2, float float_3, float float_4, float float_5, float float_6, CallbackInfo ci) {
        if (entity.hasVehicle()) {
            if (entity.getVehicle() instanceof RocketEntity) {
                this.head.pitch = 0.0F;
                this.leftArm.pitch = 0.0F;
                this.leftArm.yaw = 0.0F;
                this.rightArm.pitch = 0.0F;
                this.rightArm.yaw = 0.0F;
                this.leftLeg.pitch = 0.0F;
                this.leftLeg.yaw = 0.0F;
                this.rightLeg.pitch = 0.0F;
                this.rightLeg.yaw = 0.0F;
                this.headwear.pitch = 0.0F;
                this.headwear.yaw = 0.0F;
                this.body.pitch = 0.0F;
                this.body.yaw = 0.0F;

                GlStateManager.rotatef(((entity.getVehicle().pitch) * -1.0F), 1.0F, 0.0F, 0.0F);
                //GlStateManager.translatef(x,y,z);
            }
        }
    }

}
