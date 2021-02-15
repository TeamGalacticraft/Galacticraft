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
import com.hrznstudio.galacticraft.accessor.GearInventoryProvider;
import com.hrznstudio.galacticraft.api.atmosphere.AtmosphericGas;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import com.hrznstudio.galacticraft.attribute.oxygen.OxygenTank;
import com.hrznstudio.galacticraft.util.OxygenTankUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow protected abstract int getNextAirOnLand(int air);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    private @Unique boolean oxygenCache;

    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSubmergedIn(Lnet/minecraft/tag/Tag;)Z", ordinal = 0))
    private boolean checkOxygenAtmosphere_gcr(Entity entity, Tag<Fluid> tag) {
        return entity.isSubmergedIn(tag) || !oxygenCache;
    }

    @Inject(method = "setWorld", at = @At("HEAD"))
    private void cacheOxygen_gcr(World world, CallbackInfo ci) {
        Optional<CelestialBodyType> optional = CelestialBodyType.getByDimType(world.getRegistryKey());
        oxygenCache = !optional.isPresent() || optional.get().getAtmosphere().getComposition().containsKey(AtmosphericGas.OXYGEN);
    }

    @Inject(method = "getNextAirUnderwater", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getRespiration(Lnet/minecraft/entity/LivingEntity;)I"), cancellable = true)
    private void disableRespiration_cacheGC(int air, CallbackInfoReturnable<Integer> ci) {
        FixedItemInv gearInv = ((GearInventoryProvider)this).getGearInv();

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

        if (!oxygenCache) ci.setReturnValue(air - 1);
    }
}
