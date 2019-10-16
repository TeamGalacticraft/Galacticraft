package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> {

    @Inject(at=@At("HEAD"), method = "method_17087")
    private void standInRocketGC(T livingEntity_1, float float_1, float float_2, float float_3, float float_4, float float_5, float float_6, CallbackInfo ci) {
        if (((BipedEntityModel<T>) (Object) this).isRiding) { //cannot @Shadow isRiding
            if (livingEntity_1.getVehicle() instanceof RocketEntity) {
                ((BipedEntityModel<T>) (Object) this).isRiding = false;
            }
        }
    }

}
