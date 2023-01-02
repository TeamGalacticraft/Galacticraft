/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.impl.internal.mixin;

import dev.galacticraft.api.accessor.GearInventoryProvider;
import dev.galacticraft.api.entity.attribute.GcApiEntityAttributes;
import dev.galacticraft.api.item.Accessory;
import dev.galacticraft.api.item.OxygenGear;
import dev.galacticraft.api.item.OxygenMask;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.internal.fabric.GalacticraftAPI;
import dev.galacticraft.machinelib.api.gas.Gases;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements GearInventoryProvider {
    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Shadow protected abstract int increaseAirSupply(int air);

    @ModifyConstant(method = "travel", constant = @Constant(doubleValue = 0.08))
    private double galacticraft_modifyGravity(double d) {
        return CelestialBody.getByDimension(this.level).map(celestialBodyType -> celestialBodyType.gravity() * 0.08d).orElse(0.08);
    }

    @ModifyConstant(method = "travel", constant = @Constant(doubleValue = 0.01))
    private double galacticraft_modifySlowFallingGravity(double d) {
        return CelestialBody.getByDimension(this.level).map(celestialBodyType -> celestialBodyType.gravity() * 0.01d).orElse(0.01);
    }

    @Shadow
    public abstract MobEffectInstance getEffect(MobEffect effect);

    @Inject(method = "calculateFallDamage", at = @At("HEAD"), cancellable = true)
    protected void galacticraft_modifyFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Integer> cir) {
        MobEffectInstance effectInstance = this.getEffect(MobEffects.JUMP);
        float ff = effectInstance == null ? 0.0F : (float) (effectInstance.getAmplifier() + 6);
        CelestialBody.getByDimension(this.level).ifPresent(celestialBodyType -> cir.setReturnValue((int) (Mth.ceil((fallDistance * celestialBodyType.gravity()) - 3.0F - ff) * damageMultiplier)));
    }

    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z", ordinal = 0))
    private boolean galacticraft_testForBreathability(LivingEntity entity, TagKey<Fluid> tag) {
        //noinspection ConstantConditions
        assert ((Object) entity) == this;
        return entity.isEyeInFluid(tag) || !this.level.isBreathable(entity.blockPosition().relative(Direction.UP, (int) Math.floor(this.getEyeHeight(entity.getPose(), entity.getDimensions(entity.getPose())))));
    }

    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void thistickAccessories(CallbackInfo ci) {
        LivingEntity thisEntity = ((LivingEntity) (Object) this);
        for (int i = 0; i < this.getAccessories().getContainerSize(); i++) {
            ItemStack stack = this.getAccessories().getItem(i);
            if (stack.getItem() instanceof Accessory accessory) {
                accessory.tick(thisEntity);
            }
        }
    }

    @Inject(method = "decreaseAirSupply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getRespiration(Lnet/minecraft/world/entity/LivingEntity;)I"), cancellable = true)
    private void galacticraft_modifyAirLevel(int air, CallbackInfoReturnable<Integer> ci) {
        AttributeInstance attribute = ((LivingEntity) (Object) this).getAttribute(GcApiEntityAttributes.CAN_BREATHE_IN_SPACE);
        if (attribute != null && attribute.getValue() >= 0.99D) {
            ci.setReturnValue(this.increaseAirSupply(air));
        }

        boolean mask = false;
        boolean gear = false;
        for (int i = 0; i < this.getAccessories().getContainerSize(); i++) {
            Item item = this.getAccessories().getItem(i).getItem();
            if (!mask && item instanceof OxygenMask) {
                mask = true;
                if (gear) break;
            } else if (!gear && item instanceof OxygenGear) {
                gear = true;
                if (mask) break;
            }
        }

        if (mask && gear) {
            Container tankInv = this.getOxygenTanks();
            for (int i = 0; i < tankInv.getContainerSize(); i++) {
                Storage<FluidVariant> storage = ContainerItemContext.withInitial(tankInv.getItem(i)).find(FluidStorage.ITEM);
                if (storage != null) {
                    try (Transaction transaction = Transaction.openOuter()) {
                        if (storage.extract(FluidVariant.of(Gases.OXYGEN), 1L, transaction) > 0) {
                            transaction.commit();
                            ci.setReturnValue(this.increaseAirSupply(air));
                            return;
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "dropEquipment", at = @At(value = "RETURN"))
    private void galacticraft_dropGearInventory(CallbackInfo ci) {
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            Container gearInv = this.getGearInv();
            for (int i = 0; i < gearInv.getContainerSize(); ++i) {
                ItemStack itemStack = gearInv.getItem(i);
                gearInv.setItem(i, ItemStack.EMPTY);
                if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack)) {
                    //noinspection ConstantConditions
                    if (((Object) this) instanceof Player player) {
                        player.drop(itemStack, true, false);
                    } else {
                        this.spawnAtLocation(itemStack);
                    }
                }
            }
        }
    }

    @Override
    public Container getGearInv() {
        return GalacticraftAPI.EMPTY_INV;
    }

    @Override
    public Container getOxygenTanks() {
        return GalacticraftAPI.EMPTY_INV;
    }

    @Override
    public Container getThermalArmor() {
        return GalacticraftAPI.EMPTY_INV;
    }

    @Override
    public Container getAccessories() {
        return GalacticraftAPI.EMPTY_INV;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void galacticraft_writeGearInventory(CompoundTag nbt, CallbackInfo ci) {
        this.writeGearToNbt(nbt);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void galacticraft_readGearInventory(CompoundTag tag, CallbackInfo ci) {
        this.readGearFromNbt(tag);
    }

    @Override
    public void writeGearToNbt(CompoundTag tag) {
    }

    @Override
    public void readGearFromNbt(CompoundTag tag) {
    }
}
