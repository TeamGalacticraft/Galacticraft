/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.block.entity;

import com.google.common.annotations.VisibleForTesting;
import dev.galacticraft.api.block.entity.MachineBlockEntity;
import dev.galacticraft.api.machine.MachineStatus;
import dev.galacticraft.api.machine.MachineStatuses;
import dev.galacticraft.api.machine.storage.MachineItemStorage;
import dev.galacticraft.api.machine.storage.StorageSlot;
import dev.galacticraft.api.machine.storage.display.ItemSlotDisplay;
import dev.galacticraft.api.machine.storage.io.ResourceFlow;
import dev.galacticraft.api.machine.storage.io.ResourceType;
import dev.galacticraft.api.machine.storage.io.SlotType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.machine.GalacticraftMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GalacticraftSlotTypes;
import dev.galacticraft.mod.screen.CoalGeneratorScreenHandler;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class CoalGeneratorBlockEntity extends MachineBlockEntity {
    @VisibleForTesting
    public static final Object2IntMap<Item> FUEL_MAP = Util.make(new Object2IntArrayMap<>(3), (map) -> {
        map.defaultReturnValue(-1);
        map.put(Items.COAL_BLOCK, 320 * 10);
        map.put(Items.COAL, 320);
        map.put(Items.CHARCOAL, 310);
    });

    public static final int CHARGE_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    private static final SlotType<Item, ItemVariant> COAL_INPUT = SlotType.create(new ResourceLocation(Constant.MOD_ID, "coal_input"), TextColor.fromRgb(0x000000), Component.translatable("slot_type.galacticraft.coal_input"), v -> FUEL_MAP.containsKey(v.getItem()), ResourceFlow.INPUT, ResourceType.ITEM);

    private int fuelLength = 0;
    private long fuelSlotModCount = -1;
    private int fuelTime = 0;
    private double heat = 0.0d;

    /*
     * Energy stats:
     * T1 machine: uses 30gj/t
     * Coal Generator: generates 120gj/t (max heat)
     */

    public CoalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.COAL_GENERATOR, pos, state);
    }

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        return MachineItemStorage.Builder.create()
                .addSlot(GalacticraftSlotTypes.ENERGY_CHARGE, new ItemSlotDisplay(8, 62))
                .addSlot(COAL_INPUT, new ItemSlotDisplay(71, 53))
                .build();
    }

    @Override
    public long getEnergyCapacity() {
        return Galacticraft.CONFIG_MANAGER.get().machineEnergyStorageSize();
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state) {
        super.tickConstant(world, pos, state);
        if (this.fuelLength == 0) {
            if (this.heat > 0) {
                this.setHeat(Math.max(0, this.heat - 0.02d));
            }
        }
        world.getProfiler().push("charge");
        this.attemptDrainPowerToStack(CHARGE_SLOT);
        world.getProfiler().pop();
    }

    @Override
    public @NotNull MachineStatus tick(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state) {
        world.getProfiler().push("transaction");
        try (Transaction transaction = Transaction.openOuter()) {
            this.energyStorage().insert((long) (Galacticraft.CONFIG_MANAGER.get().coalGeneratorEnergyProductionRate() * this.heat), transaction);
            transaction.commit();
        }
        this.trySpreadEnergy(world);
        world.getProfiler().popPush("fuel_reset");
        if (this.fuelLength == 0) {
            if (!this.consumeFuel()) {
                if (this.heat > 0) {
                    return GalacticraftMachineStatus.COOLING_DOWN;
                } else {
                    return GalacticraftMachineStatus.NO_FUEL;
                }
            }
        }
        world.getProfiler().popPush("fuel_tick");
        if (this.fuelTime++ >= this.fuelLength) {
            this.consumeFuel();
        }
        this.setHeat(Math.min(1, this.heat + 0.004));
        world.getProfiler().pop();

        if (this.energyStorage().isFull()) {
            return MachineStatuses.CAPACITOR_FULL;
        } else if (this.heat < 1.0) {
            return GalacticraftMachineStatus.WARMING_UP;
        } else {
            return GalacticraftMachineStatus.GENERATING;
        }
    }

    private boolean consumeFuel() {
        this.fuelTime = 0;
        this.fuelLength = 0;

        StorageSlot<Item, ItemVariant, ItemStack> slot = this.itemStorage().getSlot(FUEL_SLOT);
        if (slot.getModCount() != this.fuelSlotModCount) {
            this.fuelSlotModCount = slot.getModCount();
            ItemVariant resource = slot.getResource();
            if (resource.isBlank()) return false;
            try (Transaction transaction = Transaction.openOuter()) {
                if (slot.extract(resource, 1, transaction) == 1) {
                    if (FUEL_MAP.containsKey(resource.getItem())) {
                        this.fuelLength = FUEL_MAP.getInt(resource.getItem());
                        transaction.commit();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getFuelLength() {
        return this.fuelLength;
    }

    public void setFuelLength(int fuelLength) {
        this.fuelLength = fuelLength;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) return new CoalGeneratorScreenHandler(syncId, player, this);
        return null;
    }

    public double getHeat() {
        return this.heat;
    }

    @VisibleForTesting
    public void setHeat(double heat) {
        this.heat = heat;
    }

    public int getFuelTime() {
        return fuelTime;
    }

    public void setFuelTime(int value) {
        this.fuelTime = value;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.fuelLength = nbt.getInt(Constant.Nbt.FUEL_LENGTH);
        this.fuelTime = nbt.getInt(Constant.Nbt.FUEL_TIME);
        this.heat = nbt.getDouble(Constant.Nbt.HEAT);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(Constant.Nbt.FUEL_LENGTH, this.fuelLength);
        tag.putInt(Constant.Nbt.FUEL_TIME, this.fuelTime);
        tag.putDouble(Constant.Nbt.HEAT, this.heat);
    }
}