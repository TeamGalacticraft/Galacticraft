/*
 * Copyright (c) 2019-2021 HRZN LTD
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

import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.impl.EmptyFixedItemInv;
import com.hrznstudio.galacticraft.accessor.GearInventoryProvider;
import com.hrznstudio.galacticraft.accessor.WorldOxygenAccessor;
import com.hrznstudio.galacticraft.api.entity.attribute.GalacticraftEntityAttributes;
import com.hrznstudio.galacticraft.attribute.oxygen.OxygenTank;
import com.hrznstudio.galacticraft.util.OxygenTankUtils;
import net.minecraft.core.Direction;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow protected abstract int getNextAirOnLand(int air);

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSubmergedIn(Lnet/minecraft/tag/Tag;)Z", ordinal = 0))
    private boolean checkOxygenAtmosphere_gcr(LivingEntity entity, Tag<Fluid> tag) {
        return entity.isEyeInFluid(tag) || !((WorldOxygenAccessor) this.level).isBreathable(entity.blockPosition().relative(Direction.UP, (int)Math.floor(entity.getEyeHeight(entity.getPose(), entity.getDimensions(entity.getPose())))));
    }

    @Inject(method = "getNextAirUnderwater", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getRespiration(Lnet/minecraft/entity/LivingEntity;)I"), cancellable = true)
    private void overrideOxygen_gcr(int air, CallbackInfoReturnable<Integer> ci) {
        AttributeInstance attribute = ((LivingEntity)(Object)this).getAttribute(GalacticraftEntityAttributes.CAN_BREATHE_IN_SPACE);
        if (attribute != null && attribute.getValue() >= 0.99D) {
            ci.setReturnValue(this.getNextAirOnLand(air));
        }

        FixedItemInv gearInv = ((GearInventoryProvider) this).getGearInv();
        if (gearInv != EmptyFixedItemInv.INSTANCE) {

            OxygenTank tank = OxygenTankUtils.getOxygenTank(gearInv.getSlot(6));
            if (tank.getAmount() > 0) {
                tank.setAmount(tank.getAmount() - 1);
                ci.setReturnValue(this.getNextAirOnLand(air));
            }
            tank = OxygenTankUtils.getOxygenTank(gearInv.getSlot(7));
            if (tank.getAmount() > 0) {
                tank.setAmount(tank.getAmount() - 1);
                ci.setReturnValue(this.getNextAirOnLand(air));
            }
        }
    }
}
