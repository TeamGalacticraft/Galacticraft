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

package dev.galacticraft.mod.content.block.entity.machine;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Pair;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.filter.ResourceFilters;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineEnergyStorage;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.transfer.TransferType;
import dev.galacticraft.machinelib.api.util.EnergySource;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.block.machine.CoalGeneratorBlock;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.screen.CoalGeneratorMenu;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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

    private static final StorageSpec SPEC = StorageSpec.of(
            MachineItemStorage.spec(
                    ItemResourceSlot.builder(TransferType.PROCESSING)
                            .pos(8, 62)
                            .capacity(1)
                            .filter(ResourceFilters.CAN_INSERT_ENERGY)
                            .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.ENERGY)),
                    ItemResourceSlot.builder(TransferType.INPUT)
                            .pos(80, 44)
                            .filter((item, tag) -> CoalGeneratorBlockEntity.FUEL_MAP.containsKey(item))
            ),
            MachineEnergyStorage.spec(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    0,
                    Galacticraft.CONFIG.coalGeneratorEnergyProductionRate() * 2
            )
    );

    private final EnergySource energySource = new EnergySource(this);
    private int fuelLength = 0;
    private long fuelSlotModCount = -1;
    private int fuelTime = 0;
    private double heat = 0.0d;
    private boolean lit = false;
    private boolean curr = false;
    private boolean prev = false;

    /*
     * Energy stats:
     * T1 machine: uses 30gJ/t
     * Coal Generator: generates 120gJ/t (max heat)
     * At max heat 1 Coal generates 44,160gJ
     */

    public CoalGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.COAL_GENERATOR, pos, state, SPEC);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(level, pos, state, profiler);
        profiler.push("charge");
        this.drainPowerToSlot(CHARGE_SLOT);
        profiler.pop();

        if (this.fuelLength > 0) {
            if (this.shouldExtinguish(level, pos, state)) {
                this.fuelLength = 0;
                this.fuelTime = 0;
                RandomSource randomSource = level.getRandom();
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.25F, 2.6F + (randomSource.nextFloat() - randomSource.nextFloat()) * 0.8F);
                return;
            } else {
                this.setHeat(Math.min(1, this.heat + 0.004));
            }
        }

        boolean lit = this.fuelLength > 0;
        if (this.lit != lit || this.curr != this.prev) {
            this.lit = lit;
            this.prev = this.curr;
            level.setBlock(this.worldPosition, state.setValue(CoalGeneratorBlock.LIT, this.lit).setValue(CoalGeneratorBlock.HOT, this.prev), Block.UPDATE_CLIENTS);
        }

        profiler.push("cooling_tick");
        if (this.fuelLength == 0) {
            if (this.heat > 0) {
                this.setHeat(Math.max(0, this.heat - 0.02d));
                this.curr = this.heat > 0;
            }
        }
        profiler.pop();
    }

    @Override
    public @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        profiler.push("transaction");
        this.energyStorage().insert((long) (Galacticraft.CONFIG.coalGeneratorEnergyProductionRate() * this.heat));
        this.energySource.trySpreadEnergy(level, pos, state);
        profiler.popPush("fuel_reset");
        if (this.fuelLength == 0) {
            MachineStatus status = this.consumeFuel(level, pos, state);
            if (status != null) {
                return status;
            }
        }
        if (++this.fuelTime >= this.fuelLength) {
            this.consumeFuel(level, pos, state);
        }
        profiler.pop();

        this.curr = this.heat == 1.0;

        if (this.energyStorage().isFull()) {
            return MachineStatuses.CAPACITOR_FULL;
        } else if (this.heat < 1.0) {
            return GCMachineStatuses.WARMING_UP;
        } else {
            return GCMachineStatuses.GENERATING;
        }
    }

    @Override
    public void tickDisabled(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        if (this.fuelLength > 0 && ++this.fuelTime >= this.fuelLength) {
            this.fuelLength = 0;
            this.fuelTime = 0;
            this.curr = true;
        }
    }

    @Override
    public void updateActiveState(Level level, BlockPos pos, BlockState state, boolean active) {
        this.lit = this.fuelLength > 0;
        this.prev = this.curr;
        super.updateActiveState(level, pos, state.setValue(CoalGeneratorBlock.LIT, this.lit).setValue(CoalGeneratorBlock.HOT, this.curr), active);
    }

    private boolean shouldExtinguish(ServerLevel level, BlockPos pos, BlockState state) {
        return !level.galacticraft$isBreathable(pos.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING)))
                && !level.galacticraft$isBreathable(pos);
    }

    private MachineStatus consumeFuel(ServerLevel level, BlockPos pos, BlockState state) {
        this.fuelTime = 0;
        this.fuelLength = 0;

        if (!this.shouldExtinguish(level, pos, state)) {
            ItemResourceSlot slot = this.itemStorage().slot(INPUT_SLOT);
            if (slot.getModifications() != this.fuelSlotModCount) {
                this.fuelSlotModCount = slot.getModifications();
                int time = FUEL_MAP.getInt(slot.getResource());
                if (time > 0) {
                    if (slot.consumeOne() != null) {
                        this.fuelLength = time;
                        return null;
                    }
                }
            }
            return this.heat > 0 ? GCMachineStatuses.COOLING_DOWN : GCMachineStatuses.NO_FUEL;
        }
        return GCMachineStatuses.NOT_ENOUGH_OXYGEN;
    }

    public int getFuelLength() {
        return this.fuelLength;
    }

    public void setFuelLength(int fuelLength) {
        this.fuelLength = fuelLength;
    }

    @Override
    public @Nullable MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inv, Player player) {
        return new CoalGeneratorMenu(syncId, (ServerPlayer) player, this);
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
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        this.fuelLength = tag.getInt(Constant.Nbt.FUEL_LENGTH);
        this.fuelTime = tag.getInt(Constant.Nbt.FUEL_TIME);
        this.heat = tag.getDouble(Constant.Nbt.HEAT);

        this.lit = this.fuelLength > 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        tag.putInt(Constant.Nbt.FUEL_LENGTH, this.fuelLength);
        tag.putInt(Constant.Nbt.FUEL_TIME, this.fuelTime);
        tag.putDouble(Constant.Nbt.HEAT, this.heat);
    }
}