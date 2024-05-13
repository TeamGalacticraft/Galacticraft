/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.content.block.entity.machine;

import com.google.common.annotations.VisibleForTesting;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.screen.CoalGeneratorMenu;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoalGeneratorBlockEntity extends MachineBlockEntity {
    public static final int CHARGE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    @VisibleForTesting
    public static final Object2IntMap<Item> FUEL_MAP = Util.make(new Object2IntArrayMap<>(3), (map) -> {
        map.defaultReturnValue(0);
        map.put(Items.COAL_BLOCK, 320 * 10);
        map.put(Items.COAL, 320);
        map.put(Items.CHARCOAL, 310);
    });

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
        super(GCMachineTypes.COAL_GENERATOR, pos, state);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        if (this.fuelLength == 0) {
            if (this.heat > 0) {
                this.setHeat(Math.max(0, this.heat - 0.02d));
            }
        }
        profiler.push("charge");
        this.drainPowerToStack(CHARGE_SLOT);
        profiler.pop();
    }

    @Override
    public @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        profiler.push("transaction");
        this.energyStorage().insert((long) (Galacticraft.CONFIG.coalGeneratorEnergyProductionRate() * this.heat));
        this.trySpreadEnergy(level, state);
        profiler.popPush("fuel_reset");
        if (this.fuelLength == 0) {
            if (!this.consumeFuel()) {
                if (this.heat > 0) {
                    return GCMachineStatuses.COOLING_DOWN;
                } else {
                    return GCMachineStatuses.NO_FUEL;
                }
            }
        }
        profiler.popPush("fuel_tick");
        if (++this.fuelTime >= this.fuelLength) {
            this.consumeFuel();
        }
        this.setHeat(Math.min(1, this.heat + 0.004));
        profiler.pop();

        if (this.energyStorage().isFull()) {
            return MachineStatuses.CAPACITOR_FULL;
        } else if (this.heat < 1.0) {
            return GCMachineStatuses.WARMING_UP;
        } else {
            return GCMachineStatuses.GENERATING;
        }
    }

    private boolean consumeFuel() {
        this.fuelTime = 0;
        this.fuelLength = 0;

        ItemResourceSlot slot = this.itemStorage().getSlot(INPUT_SLOT);
        if (slot.getModifications() != this.fuelSlotModCount) {
            this.fuelSlotModCount = slot.getModifications();
            int time = FUEL_MAP.getInt(slot.getResource());
            if (time > 0) {
                if (slot.consumeOne() != null) {
                    this.fuelLength = time;
                    return true;
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
        if (this.getSecurity().hasAccess(player)) return new CoalGeneratorMenu(syncId, (ServerPlayer) player, this);
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