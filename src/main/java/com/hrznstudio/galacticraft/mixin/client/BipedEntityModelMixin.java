package com.hrznstudio.galacticraft.mixin.client;

import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
@Environment(EnvType.CLIENT)
public abstract class BipedEntityModelMixin<T extends LivingEntity> {

    @Shadow
    public ModelPart head;

    @Shadow
    public ModelPart leftArm;

    @Shadow
    public ModelPart rightArm;

    @Shadow
    public ModelPart leftLeg;

    @Shadow
    public ModelPart rightLeg;

    @Shadow
    public ModelPart torso;

    @Shadow
    public ModelPart helmet;

    @Inject(at = @At("HEAD"), method = "setAngles")
    private void standInRocketGC(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (((BipedEntityModel<T>) (Object) this).riding) {
            if (livingEntity.getVehicle() instanceof RocketEntity) {
                ((BipedEntityModel<T>) (Object) this).riding = false;
            }
        }
    }

    @Inject(method = "setAngles", at = @At(value = "TAIL"))
    private void rotateToMatchRocket(T entity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (entity.hasVehicle()) {
            if (entity.getVehicle() instanceof RocketEntity) {
                GlStateManager.rotatef(((entity.getVehicle().yaw - 180.0F) * -1.0F), 0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(((entity.getVehicle().pitch) * -1.0F), 1.0F, 0.0F, 0.0F);
            }
        }
    }

    @Inject(method = "setAngles", at = @At(value = "TAIL"))
    private void rotateToMatchRocketHeadRender(T entity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (entity.hasVehicle()) {
            if (entity.getVehicle() instanceof RocketEntity) {
                this.head.pitch = 0.0F;
                this.leftArm.pitch = 0.0F;
                this.leftArm.yaw = ((entity.getVehicle().pitch) * -1.0F);
                this.rightArm.pitch = 0.0F;
                this.rightArm.yaw = ((entity.getVehicle().pitch) * -1.0F);
                this.leftLeg.pitch = 0.0F;
                this.leftLeg.yaw = ((entity.getVehicle().pitch) * -1.0F);
                this.rightLeg.pitch = 0.0F;
                this.rightLeg.yaw = ((entity.getVehicle().pitch) * -1.0F);
                this.helmet.pitch = 0.0F;
                this.helmet.yaw = ((entity.getVehicle().pitch) * -1.0F);
                this.torso.pitch = 0.0F;
                this.torso.yaw = ((entity.getVehicle().pitch) * -1.0F);
            }
        }
    }

}
