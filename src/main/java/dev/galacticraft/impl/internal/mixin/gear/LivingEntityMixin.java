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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.galacticraft.api.accessor.GearInventoryProvider;
import dev.galacticraft.api.entity.attribute.GcApiEntityAttributes;
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.api.item.Accessory;
import dev.galacticraft.impl.internal.fabric.GalacticraftAPI;
import dev.galacticraft.impl.network.s2c.GearInvPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.block.special.CryogenicChamberBlock;
import dev.galacticraft.mod.content.block.special.CryogenicChamberPart;
import dev.galacticraft.mod.content.entity.damage.GCDamageTypes;
import dev.galacticraft.mod.content.entity.vehicle.LanderEntity;
import dev.galacticraft.mod.content.item.InfiniteOxygenTankItem;
import dev.galacticraft.mod.tag.GCFluidTags;
import dev.galacticraft.mod.tag.GCItemTags;
import dev.galacticraft.mod.world.inventory.GearInventory;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements GearInventoryProvider {

    @Unique
    @SuppressWarnings("WrongEntityDataParameterClass")
    private static final EntityDataAccessor<Boolean> DATA_HAS_MASK_ID = SynchedEntityData.defineId(
            LivingEntity.class, EntityDataSerializers.BOOLEAN
    );

    @Unique
    @SuppressWarnings("WrongEntityDataParameterClass")
    private static final EntityDataAccessor<Boolean> DATA_HAS_GEAR_ID = SynchedEntityData.defineId(
            LivingEntity.class, EntityDataSerializers.BOOLEAN
    );

    @Unique
    @SuppressWarnings("WrongEntityDataParameterClass")
    private static final EntityDataAccessor<String> DATA_TANK_1_SIZE_ID = SynchedEntityData.defineId(
            LivingEntity.class, EntityDataSerializers.STRING
    );

    @Unique
    @SuppressWarnings("WrongEntityDataParameterClass")
    private static final EntityDataAccessor<String> DATA_TANK_2_SIZE_ID = SynchedEntityData.defineId(
            LivingEntity.class, EntityDataSerializers.STRING
    );

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    private int lastHurtBySuffocationTimestamp;

    @Shadow
    protected abstract int increaseAirSupply(int air);

    @Shadow
    protected abstract int decreaseAirSupply(int air);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void gc$defineOxygenSetupData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(DATA_HAS_MASK_ID, false);
        builder.define(DATA_HAS_GEAR_ID, false);
        builder.define(DATA_TANK_1_SIZE_ID, "");
        builder.define(DATA_TANK_2_SIZE_ID, "");
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"), cancellable = true)
    protected void galacticraft$readOxygenSetupData(CompoundTag tag, CallbackInfo ci) {
        this.entityData.set(DATA_HAS_MASK_ID, tag.getBoolean(Constant.Nbt.HAS_MASK));
        this.entityData.set(DATA_HAS_GEAR_ID, tag.getBoolean(Constant.Nbt.HAS_GEAR));
        this.entityData.set(DATA_TANK_1_SIZE_ID, tag.getString(Constant.Nbt.OXYGEN_TANK_1));
        this.entityData.set(DATA_TANK_2_SIZE_ID, tag.getString(Constant.Nbt.OXYGEN_TANK_2));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"), cancellable = true)
    protected void galacticraft$addOxygenSetupData(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean(Constant.Nbt.HAS_MASK, this.galacticraft$hasMask());
        tag.putBoolean(Constant.Nbt.HAS_GEAR, this.galacticraft$hasGear());
        tag.putString(Constant.Nbt.OXYGEN_TANK_1, this.galacticraft$tankSize(0));
        tag.putString(Constant.Nbt.OXYGEN_TANK_2, this.galacticraft$tankSize(1));
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
    private void galacticraft_oxygenCheck(CallbackInfo ci) {
        LivingEntity entity = ((LivingEntity) (Object) this);
        if (entity.galacticraft$oxygenConsumptionRate() == 0) return;
        AttributeInstance attribute = entity.getAttribute(GcApiEntityAttributes.CAN_BREATHE_IN_SPACE);
        if (!entity.level().isBreathable(entity.getX(), entity.getEyeY(), entity.getZ()) && !(attribute != null && attribute.getValue() >= 0.99D)) {
            if (!entity.isEyeInFluid(GCFluidTags.NON_BREATHABLE) && !(entity instanceof Player player && player.getAbilities().invulnerable)) {
                entity.setAirSupply(this.decreaseAirSupply(entity.getAirSupply()));
                if (entity.getAirSupply() == -20) {
                    entity.setAirSupply(0);
                    this.lastHurtBySuffocationTimestamp = this.tickCount;
                    entity.hurt(new DamageSource(entity.level().registryAccess()
                            .registryOrThrow(Registries.DAMAGE_TYPE)
                            .getHolderOrThrow(GCDamageTypes.SUFFOCATION)), 2.0f);
                } else if (this.tickCount - this.lastHurtBySuffocationTimestamp > 20) {
                    // A small amount of depressurization damage
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
        if (entity.galacticraft$oxygenConsumptionRate() == 0) return false;
        if ((entity.getVehicle() instanceof LanderEntity) || (entity.getInBlockState().getBlock() instanceof CryogenicChamberBlock) || (entity.getInBlockState().getBlock() instanceof CryogenicChamberPart)) {
            this.lastHurtBySuffocationTimestamp = this.tickCount;
            return false;
        }
        return original || this.isEyeInFluid(GCFluidTags.NON_BREATHABLE) || !entity.level().isBreathable(entity.blockPosition().relative(Direction.UP, (int) Math.floor(this.getEyeHeight(entity.getPose()))));
    }

    @ModifyExpressionValue(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;canBreatheUnderwater()Z"))
    private boolean galacticraft_drowningDamage(boolean original) {
        // Return whether the player should take drowning damage
        return original || !this.isEyeInFluid(GCFluidTags.NON_BREATHABLE);
    }

    @Inject(method = "decreaseAirSupply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttribute(Lnet/minecraft/core/Holder;)Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;"), cancellable = true)
    private void galacticraft_modifyAirLevel(int air, CallbackInfoReturnable<Integer> cir) {
        LivingEntity entity = ((LivingEntity) (Object) this);
        long rate = entity.galacticraft$oxygenConsumptionRate();
        if (rate == 0) return;
        AttributeInstance attribute = entity.getAttribute(GcApiEntityAttributes.CAN_BREATHE_IN_SPACE);
        if (!this.isEyeInFluid(GCFluidTags.NON_BREATHABLE) && (
                (attribute != null && attribute.getValue() >= 0.99D) ||
                entity.level().isBreathable(entity.blockPosition().relative(Direction.UP, (int) Math.floor(entity.getEyeHeight(entity.getPose()))))
        )) {
            this.lastHurtBySuffocationTimestamp = this.tickCount;
            cir.setReturnValue(this.increaseAirSupply(air));
        } else if (this.galacticraft$hasMaskAndGear()) {
            InventoryStorage tankInv = InventoryStorage.of(this.galacticraft$getOxygenTanks(), null);
            for (int i = 1; i < tankInv.getSlotCount(); i++) {
                ItemStack stack = tankInv.getSlot(i).getResource().toStack();
                if (stack.getItem() instanceof InfiniteOxygenTankItem) {
                    this.lastHurtBySuffocationTimestamp = this.tickCount;
                    cir.setReturnValue(this.increaseAirSupply(air));
                    return;
                }
            }
            for (int i = 0; i < tankInv.getSlotCount(); i++) {
                Storage<FluidVariant> storage = ContainerItemContext.ofSingleSlot(tankInv.getSlot(i)).find(FluidStorage.ITEM);
                if (storage != null) {
                    try (Transaction transaction = Transaction.openOuter()) {
                        if (storage.extract(FluidVariant.of(Gases.OXYGEN), rate, transaction) > 0) {
                            transaction.commit();
                            this.lastHurtBySuffocationTimestamp = this.tickCount;
                            cir.setReturnValue(this.increaseAirSupply(air));
                            return;
                        }
                    }
                }
            }
        }
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

    @Inject(method = "canFreeze", at = @At(value = "HEAD"), cancellable = true)
    private void galacticraft_canFreezeThermalPadding(CallbackInfoReturnable<Boolean> cir) {
        Container inv = this.galacticraft$getThermalArmor();
        for (int slot = 0; slot < inv.getContainerSize(); slot++) {
            if (inv.getItem(slot).is(ItemTags.FREEZE_IMMUNE_WEARABLES)) {
                cir.setReturnValue(false);
                return;
            }
        }
    }

    @Override
    public void galacticraft$onEquipAccessory(ItemStack previous, ItemStack incoming) {
        if ((incoming.isEmpty() && previous.isEmpty()) || ItemStack.isSameItemSameComponents(previous, incoming) || this.firstTick) {
            return;
        }
        if (!this.level().isClientSide() && !this.isSpectator() && !this.isSilent() && incoming.getItem() instanceof Accessory accessory) {
            this.level().playSeededSound(null, this.getX(), this.getY(), this.getZ(), accessory.getEquipSound(), this.getSoundSource(), 1.0f, 1.0f, this.random.nextLong());
        }
    }

    @Override
    public SimpleContainer galacticraft_createGearInventory() {
        SimpleContainer inv = new GearInventory();
        inv.addListener((inventory) -> this.syncGearToClients(inventory));
        this.syncGearToClients(inv);
        return inv;
    }

    @Unique
    private void syncGearToClients(Container inventory) {
        if (this.level().isClientSide) {
            return;
        }

        this.entityData.set(DATA_HAS_MASK_ID, inventory.getItem(0).is(GCItemTags.OXYGEN_MASKS));
        this.entityData.set(DATA_HAS_GEAR_ID, inventory.getItem(1).is(GCItemTags.OXYGEN_GEAR));

        Container tankInv = this.galacticraft$getOxygenTanks();
        if (tankInv != null) {
            if (tankInv.getContainerSize() > 0) {
                ItemStack itemStack = tankInv.getItem(0);
                String tankSize = "";
                if (itemStack.is(GCItemTags.OXYGEN_TANKS)) {
                    tankSize = itemStack.getDescriptionId().replace("item.galacticraft.", "");
                }
                this.entityData.set(DATA_TANK_1_SIZE_ID, tankSize);
            }

            if (tankInv.getContainerSize() > 1) {
                ItemStack itemStack = tankInv.getItem(1);
                String tankSize = "";
                if (itemStack.is(GCItemTags.OXYGEN_TANKS)) {
                    tankSize = itemStack.getDescriptionId().replace("item.galacticraft.", "");
                }
                this.entityData.set(DATA_TANK_2_SIZE_ID, tankSize);
            }
        }

        ItemStack[] stacks = new ItemStack[inventory.getContainerSize()];
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            stacks[i] = inventory.getItem(i);
        }
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof ServerPlayer player) || player.connection != null) {
            GearInvPayload payload = new GearInvPayload(entity.getId(), stacks);

            Collection<ServerPlayer> tracking = PlayerLookup.tracking(entity);
            if (entity instanceof ServerPlayer player && !tracking.contains(player)) {
                ServerPlayNetworking.send(player, payload);
            }
            for (ServerPlayer remote : tracking) {
                ServerPlayNetworking.send(remote, payload);
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

    @Override
    public boolean galacticraft$hasMask() {
        return this.entityData.get(DATA_HAS_MASK_ID);
    }

    @Override
    public boolean galacticraft$hasGear() {
        return this.entityData.get(DATA_HAS_GEAR_ID);
    }

    @Override
    public String galacticraft$tankSize(int i) {
        switch (i) {
            case 0:
                return this.entityData.get(DATA_TANK_1_SIZE_ID);
            case 1:
                return this.entityData.get(DATA_TANK_2_SIZE_ID);
            default:
                return "";
        }
    }

    @Override
    public long galacticraft$oxygenConsumptionRate() {
        return Galacticraft.CONFIG.playerOxygenConsumptionRate();
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
