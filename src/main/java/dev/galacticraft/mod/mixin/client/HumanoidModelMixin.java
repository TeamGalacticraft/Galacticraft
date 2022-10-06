package dev.galacticraft.mod.mixin.client;

import dev.galacticraft.mod.accessor.LivingEntityAccessor;
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
public class HumanoidModelMixin {
    @Shadow @Final public ModelPart head;

    @Shadow @Final public ModelPart leftArm;

    @Shadow @Final public ModelPart rightArm;

    @Shadow @Final public ModelPart leftLeg;

    @Shadow @Final public ModelPart rightLeg;

    @Shadow @Final public ModelPart body;

    @Shadow @Final public ModelPart hat;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void gc$setCryoSleepAnim(LivingEntity livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (((LivingEntityAccessor)livingEntity).isInCryoSleep()) { // TODO: possibly cleaner way of doing this?
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
    }
}
