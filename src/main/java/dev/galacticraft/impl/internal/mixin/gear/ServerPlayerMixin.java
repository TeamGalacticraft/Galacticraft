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
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.impl.internal.inventory.MappedInventory;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCAccessorySlots;
import dev.galacticraft.mod.world.inventory.GearInventory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.galacticraft.mod.content.GCAccessorySlots.*;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements GearInventoryProvider {
    @Shadow
    public abstract ServerLevel serverLevel();

    private final @Unique SimpleContainer gearInv = this.galacticraft_createGearInventory();
    private final @Unique Container tankInv = MappedInventory.create(this.gearInv, OXYGEN_TANK_1_SLOT, OXYGEN_TANK_2_SLOT);
    private final @Unique Container thermalArmorInv = MappedInventory.create(this.gearInv, THERMAL_ARMOR_SLOT_START, THERMAL_ARMOR_SLOT_START + 1, THERMAL_ARMOR_SLOT_START + 2, THERMAL_ARMOR_SLOT_START + 3);
    private final @Unique Container accessoryInv = MappedInventory.create(this.gearInv, OXYGEN_MASK_SLOT, OXYGEN_GEAR_SLOT, ACCESSORY_SLOT_START, ACCESSORY_SLOT_START + 1, ACCESSORY_SLOT_START + 2, ACCESSORY_SLOT_START + 3);

    ServerPlayerMixin() {
        super(null, null, 0.0F, null);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void galacticraft_parrotOxygenCheck(CallbackInfo ci) {
        CompoundTag leftParrot = this.getShoulderEntityLeft();
        CompoundTag rightParrot = this.getShoulderEntityRight();
        if ((!leftParrot.isEmpty() || !rightParrot.isEmpty())
                && !this.level().galacticraft$isBreathable(this.getEyePosition())) {
            long rate = Galacticraft.CONFIG.parrotOxygenConsumptionRate();
            if (!leftParrot.isEmpty()) {
                SimpleContainer inv = new GearInventory();
                inv.fromTag(leftParrot.getList(Constant.Nbt.GEAR_INV, Tag.TAG_COMPOUND), this.registryAccess());
                InventoryStorage tankInv = InventoryStorage.of(inv, null);
                Storage<FluidVariant> storage = ContainerItemContext.ofSingleSlot(tankInv.getSlot(GCAccessorySlots.OXYGEN_TANK_1_SLOT)).find(FluidStorage.ITEM);
                if (storage != null) {
                    try (Transaction transaction = Transaction.openOuter()) {
                        if (storage.extract(FluidVariant.of(Gases.OXYGEN), rate, transaction) > 0) {
                            transaction.commit();
                            leftParrot.put(Constant.Nbt.GEAR_INV, inv.createTag(this.registryAccess()));
                            this.setShoulderEntityLeft(leftParrot);
                        } else {
                            this.galacticraft$respawnEntityOnShoulder(leftParrot);
                            this.setShoulderEntityLeft(new CompoundTag());
                        }
                    }
                }
            }
            if (!rightParrot.isEmpty()) {
                SimpleContainer inv = new GearInventory();
                inv.fromTag(rightParrot.getList(Constant.Nbt.GEAR_INV, Tag.TAG_COMPOUND), this.registryAccess());
                InventoryStorage tankInv = InventoryStorage.of(inv, null);
                Storage<FluidVariant> storage = ContainerItemContext.ofSingleSlot(tankInv.getSlot(GCAccessorySlots.OXYGEN_TANK_1_SLOT)).find(FluidStorage.ITEM);
                if (storage != null) {
                    try (Transaction transaction = Transaction.openOuter()) {
                        if (storage.extract(FluidVariant.of(Gases.OXYGEN), rate, transaction) > 0) {
                            transaction.commit();
                            rightParrot.put(Constant.Nbt.GEAR_INV, inv.createTag(this.registryAccess()));
                            this.setShoulderEntityRight(rightParrot);
                        } else {
                            this.galacticraft$respawnEntityOnShoulder(rightParrot);
                            this.setShoulderEntityRight(new CompoundTag());
                        }
                    }
                }
            }
        }
    }

    @Unique
    private void galacticraft$respawnEntityOnShoulder(CompoundTag compoundTag) {
        if (!this.level().isClientSide && !compoundTag.isEmpty()) {
            EntityType.create(compoundTag, this.level()).ifPresent(entity -> {
                if (entity instanceof TamableAnimal animal) {
                    animal.setOwnerUUID(this.uuid);
                }
                entity.setPos(this.getX(), this.getY() + 0.7D, this.getZ());
                ((ServerLevel) this.level()).addWithUUID(entity);
            });
        }
    }

    @Override
    public SimpleContainer galacticraft$getGearInv() {
        return this.gearInv;
    }

    @Override
    public Container galacticraft$getOxygenTanks() {
        return this.tankInv;
    }

    @Override
    public Container galacticraft$getThermalArmor() {
        return this.thermalArmorInv;
    }

    @Override
    public Container galacticraft$getAccessories() {
        return this.accessoryInv;
    }

    @Override
    public void galacticraft$writeGearToNbt(CompoundTag tag) {
        tag.put(Constant.Nbt.GEAR_INV, this.gearInv.createTag(this.serverLevel().registryAccess()));
    }

    @Override
    public void galacticraft$readGearFromNbt(CompoundTag tag) {
        this.gearInv.fromTag(tag.getList(Constant.Nbt.GEAR_INV, Tag.TAG_COMPOUND), this.serverLevel().registryAccess());
    }
}
