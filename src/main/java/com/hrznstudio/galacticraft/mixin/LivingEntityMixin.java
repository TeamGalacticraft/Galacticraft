package com.hrznstudio.galacticraft.mixin;

import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import com.hrznstudio.galacticraft.accessor.GCPlayerAccessor;
import com.hrznstudio.galacticraft.api.entity.EvolvedEntity;
import com.hrznstudio.galacticraft.api.space.CelestialBody;
import com.hrznstudio.galacticraft.entity.damage.GalacticraftDamageSource;
import com.hrznstudio.galacticraft.items.OxygenTankItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.Dimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    private int air;

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void baseTick(CallbackInfo ci) {
        try {
            if (((PlayerEntity) (Object) this).isCreative()) {
                air = 0;
                return;
            }
        } catch (ClassCastException ignore) {
        }
        air = ((LivingEntity) (Object) this).getBreath();
    }

    @Inject(method = "baseTick", at = @At("RETURN"))
    private void oxygenDamage(CallbackInfo ci) {
        Entity entity = (LivingEntity) (Object) this;
        if (entity.isAlive()) {
            if (entity.world.dimension instanceof CelestialBody) {
                if (!((CelestialBody) entity.world.dimension).hasOxygen()) {
                    entity.setBreath(air - 1);
                    if (entity.getBreath() == -20) {
                        entity.setBreath(0);
                        air = 0;
                        try {
                            SimpleFixedItemInv gearInventory = ((GCPlayerAccessor) entity).getGearInventory();
                            if (gearInventory.getInvStack(6).getItem() instanceof OxygenTankItem && ((gearInventory.getInvStack(6).getMaxDamage() - gearInventory.getInvStack(6).getDamage()) > 0)) {
                                gearInventory.getInvStack(6).setDamage(gearInventory.getInvStack(6).getDamage() + 1);
                                return;
                            } else if (gearInventory.getInvStack(7).getItem() instanceof OxygenTankItem && ((gearInventory.getInvStack(7).getMaxDamage() - gearInventory.getInvStack(7).getDamage()) > 0)) {
                                gearInventory.getInvStack(7).setDamage(gearInventory.getInvStack(7).getDamage() + 1);
                                return;
                            }
                        } catch (ClassCastException ignore) {
                            if (entity instanceof EvolvedEntity) {
                                return;
                            }
                        }
                        entity.damage(GalacticraftDamageSource.SUFFOCATION, 2.0F);
                    }
                }
            }
        }
    }

    @ModifyVariable(method = "travel", at = @At(value = "FIELD"), ordinal = 1, index = 11, name = "double_1")
    private double gravityEffect(double double_1) {
        if (((LivingEntity) (Object) this).world.getDimension() instanceof SpaceDimension) {
            if (double_1 < -((SpaceDimension) ((LivingEntity) (Object) this).world.getDimension()).getGravity() * 3.5) {
                double_1 += ((SpaceDimension) ((LivingEntity) (Object) this).world.getDimension()).getGravity();
            }
        }
        return double_1;
    }

    @ModifyVariable(method = "jump", at = @At(value = "FIELD"), ordinal = 0, index = 8, name = "float_2")
    private float gravityJumpEffect(float float_2) {
        if (((LivingEntity) (Object) this).world.getDimension() instanceof SpaceDimension) {
            if (float_2 > 0) {
                float_2 = float_2 + (((SpaceDimension) ((LivingEntity) (Object) this).world.getDimension()).getGravity() * 5);
            }
        }
        return float_2;
    }

}
