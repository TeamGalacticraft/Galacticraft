/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.impl.internal.mixin.gear;

import dev.galacticraft.api.accessor.GearInventoryProvider;
import dev.galacticraft.mod.content.entity.orbital.lander.LanderEntity;
import dev.galacticraft.api.entity.attribute.GcApiEntityAttributes;
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.api.item.Accessory;
import dev.galacticraft.api.item.OxygenGear;
import dev.galacticraft.api.item.OxygenMask;
import dev.galacticraft.mod.tag.GCTags;
import dev.galacticraft.impl.internal.fabric.GalacticraftAPI;
import dev.galacticraft.mod.content.block.special.CryogenicChamberBlock;
import dev.galacticraft.mod.content.block.special.CryogenicChamberPart;
import dev.galacticraft.mod.content.entity.damage.GCDamageTypes;
import dev.galacticraft.mod.Galacticraft;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements GearInventoryProvider {
    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    private int lastHurtBySuffocationTimestamp;

    @Shadow protected abstract int increaseAirSupply(int air);
    @Shadow protected abstract int decreaseAirSupply(int air);

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
    private void galacticraft_oxygenCheck(CallbackInfo ci) {
        LivingEntity entity = ((LivingEntity) (Object) this);
        AttributeInstance attribute = entity.getAttribute(GcApiEntityAttributes.CAN_BREATHE_IN_SPACE);
        if (!entity.level().isBreathable(entity.blockPosition().relative(Direction.UP, (int) Math.floor(entity.getEyeHeight(entity.getPose())))) && !(attribute != null && attribute.getValue() >= 0.99D)) {
            if (!entity.isEyeInFluid(GCTags.NON_BREATHABLE) && (!(entity instanceof Player player) || !player.getAbilities().invulnerable)) {
                entity.setAirSupply(this.decreaseAirSupply(entity.getAirSupply()));
                if (entity.getAirSupply() == -20) {
                    entity.setAirSupply(0);
                    entity.hurt(new DamageSource(entity.level().registryAccess()
                            .registryOrThrow(Registries.DAMAGE_TYPE)
                            .getHolderOrThrow(GCDamageTypes.SUFFOCATION)), 2.0f);
                } else if (this.tickCount - this.lastHurtBySuffocationTimestamp > 20) {
                    this.lastHurtBySuffocationTimestamp = this.tickCount;
                    entity.hurt(new DamageSource(entity.level().registryAccess()
                            .registryOrThrow(Registries.DAMAGE_TYPE)
                            .getHolderOrThrow(GCDamageTypes.SUFFOCATION)), 1.0f);
                }
            }
        }
    }

    @ModifyExpressionValue(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z", ordinal = 0))
    private boolean galacticraft_testForBreathability(boolean original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if ((entity.getVehicle() instanceof LanderEntity) || (entity.getInBlockState().getBlock() instanceof CryogenicChamberBlock) || (entity.getInBlockState().getBlock() instanceof CryogenicChamberPart)) {
            this.lastHurtBySuffocationTimestamp = this.tickCount;
            return false;
        }
        return original || this.isEyeInFluid(GCTags.NON_BREATHABLE) || !entity.level().isBreathable(entity.blockPosition().relative(Direction.UP, (int) Math.floor(this.getEyeHeight(entity.getPose()))));
    }

    @ModifyExpressionValue(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;canBreatheUnderwater()Z"))
    private boolean galacticraft_suffocationDamage(boolean original) {
        return original || !this.isEyeInFluid(GCTags.NON_BREATHABLE);
    }

    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void tickAccessories(CallbackInfo ci) {
        LivingEntity thisEntity = ((LivingEntity) (Object) this);
        for (int i = 0; i < this.galacticraft$getAccessories().getContainerSize(); i++) {
            ItemStack stack = this.galacticraft$getAccessories().getItem(i);
            if (stack.getItem() instanceof Accessory accessory) {
                accessory.tick(thisEntity);
            }
        }
    }

    @Inject(method = "decreaseAirSupply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttribute(Lnet/minecraft/core/Holder;)Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;"), cancellable = true)
    private void galacticraft_modifyAirLevel(int air, CallbackInfoReturnable<Integer> cir) {
        if (this.galacticraft$hasMaskAndGear()) {
            InventoryStorage tankInv = InventoryStorage.of(galacticraft$getOxygenTanks(), null);
            for (int i = 0; i < tankInv.getSlotCount(); i++) {
                Storage<FluidVariant> storage = ContainerItemContext.ofSingleSlot(tankInv.getSlot(i)).find(FluidStorage.ITEM);
                if (storage != null) {
                    try (Transaction transaction = Transaction.openOuter()) {
                        if (storage.extract(FluidVariant.of(Gases.OXYGEN), Galacticraft.CONFIG.playerOxygenConsuptionRate(), transaction) > 0) {
                            transaction.commit();
                            this.lastHurtBySuffocationTimestamp = this.tickCount;
                            cir.setReturnValue(this.increaseAirSupply(air));
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "dropEquipment", at = @At(value = "RETURN"))
    private void galacticraft_dropGearInventory(CallbackInfo ci) {
        if (!this.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            Container gearInv = this.galacticraft$getGearInv();
            for (int i = 0; i < gearInv.getContainerSize(); ++i) {
                ItemStack itemStack = gearInv.getItem(i);
                gearInv.setItem(i, ItemStack.EMPTY);
                if (!itemStack.isEmpty() && !EnchantmentHelper.has(itemStack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
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
    public Container galacticraft$getGearInv() {
        return GalacticraftAPI.EMPTY_INV;
    }

    @Override
    public Container galacticraft$getOxygenTanks() {
        return GalacticraftAPI.EMPTY_INV;
    }

    @Override
    public Container galacticraft$getThermalArmor() {
        return GalacticraftAPI.EMPTY_INV;
    }

    @Override
    public Container galacticraft$getAccessories() {
        return GalacticraftAPI.EMPTY_INV;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void galacticraft_writeGearInventory(CompoundTag nbt, CallbackInfo ci) {
        this.galacticraft$writeGearToNbt(nbt);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void galacticraft_readGearInventory(CompoundTag tag, CallbackInfo ci) {
        this.galacticraft$readGearFromNbt(tag);
    }

    @Override
    public void galacticraft$writeGearToNbt(CompoundTag tag) {
    }

    @Override
    public void galacticraft$readGearFromNbt(CompoundTag tag) {
    }
}
