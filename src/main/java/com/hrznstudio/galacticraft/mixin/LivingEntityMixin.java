/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.mixin;

import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import com.hrznstudio.galacticraft.accessor.GCPlayerAccessor;
import com.hrznstudio.galacticraft.api.atmosphere.AtmosphericGas;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import com.hrznstudio.galacticraft.api.entity.EvolvedEntity;
import com.hrznstudio.galacticraft.entity.damage.GalacticraftDamageSource;
import com.hrznstudio.galacticraft.items.OxygenTankItem;
import com.hrznstudio.galacticraft.util.GravityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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
            if(CelestialBodyType.getByDimType(entity.world.dimension.getType()).isPresent()) {
                if (!CelestialBodyType.getByDimType(entity.world.dimension.getType()).get().getAtmosphere().getComposition().containsKey(AtmosphericGas.OXYGEN)) {
                    entity.setBreath(air - 1);
                    if (entity.getBreath() == -20) {
                        entity.setBreath(0);
                        air = 0;
                        try {
                            FullFixedItemInv gearInventory = ((GCPlayerAccessor) entity).getGearInventory();
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

    @ModifyVariable(method = "travel", at = @At(value = "FIELD"), ordinal = 0, name = "d")
    private double gravityEffect(double d) {
        return GravityUtil.getGravityForEntity(((LivingEntity)(Object)this));
    }
}
