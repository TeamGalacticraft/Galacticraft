/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.mixin;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.impl.EmptyFixedItemInv;
import dev.galacticraft.mod.accessor.GearInventoryProvider;
import dev.galacticraft.mod.accessor.WorldOxygenAccessor;
import dev.galacticraft.mod.api.entity.attribute.GalacticraftEntityAttribute;
import dev.galacticraft.mod.attribute.oxygen.OxygenTank;
import dev.galacticraft.mod.util.OxygenTankUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements GearInventoryProvider {
    @Shadow protected abstract int getNextAirOnLand(int air);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSubmergedIn(Lnet/minecraft/tag/Tag;)Z", ordinal = 0))
    private boolean checkOxygenAtmosphere_gc(LivingEntity entity, Tag<Fluid> tag) {
        return entity.isSubmergedIn(tag) || !((WorldOxygenAccessor) this.world).isBreathable(entity.getBlockPos().offset(Direction.UP, (int)Math.floor(entity.getEyeHeight(entity.getPose(), entity.getDimensions(entity.getPose())))));
    }

    @Inject(method = "getNextAirUnderwater", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getRespiration(Lnet/minecraft/entity/LivingEntity;)I"), cancellable = true)
    private void overrideOxygen_gc(int air, CallbackInfoReturnable<Integer> ci) {
        EntityAttributeInstance attribute = ((LivingEntity) (Object) this).getAttributeInstance(GalacticraftEntityAttribute.CAN_BREATHE_IN_SPACE);
        if (attribute != null && attribute.getValue() >= 0.99D) {
            ci.setReturnValue(this.getNextAirOnLand(air));
        }

        FixedItemInv gearInv = this.getGearInv();
        if (gearInv.getSlotCount() >= 7) {
            OxygenTank tank = OxygenTankUtil.getOxygenTank(gearInv.getSlot(6));
            if (tank.getAmount() > 0) {
                tank.setAmount(tank.getAmount() - 1);
                ci.setReturnValue(this.getNextAirOnLand(air));
            }
            tank = OxygenTankUtil.getOxygenTank(gearInv.getSlot(7));
            if (tank.getAmount() > 0) {
                tank.setAmount(tank.getAmount() - 1);
                ci.setReturnValue(this.getNextAirOnLand(air));
            }
        }
    }

    @Inject(method = "dropInventory", at = @At(value = "RETURN"))
    private void dropGearInv(CallbackInfo ci) {
        if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            FixedItemInv gearInv = this.getGearInv();
            for(int i = 0; i < gearInv.getSlotCount(); ++i) {
                ItemStack itemStack = gearInv.extractStack(i, ConstantItemFilter.ANYTHING, ItemStack.EMPTY, Integer.MAX_VALUE, Simulation.ACTION);
                if (!itemStack.isEmpty() && EnchantmentHelper.hasVanishingCurse(itemStack)) {
                    if (((Object) this) instanceof PlayerEntity) {
                        ((PlayerEntity)(Object) this).dropItem(itemStack, true, false);
                    } else {
                        this.dropStack(itemStack);
                    }
                }
            }
        }
    }

    @Override
    public FixedItemInv getGearInv() {
        return EmptyFixedItemInv.INSTANCE;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void writeGear_gc(NbtCompound nbt, CallbackInfo ci) {
        this.writeGearToNbt(nbt);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readGear_gc(NbtCompound tag, CallbackInfo ci) {
        this.readGearFromNbt(tag);
    }

    @Override
    public NbtCompound writeGearToNbt(NbtCompound tag) {
        return tag;
    }

    @Override
    public void readGearFromNbt(NbtCompound tag) {
    }
}
