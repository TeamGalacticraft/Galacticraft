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

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.screen.SimpleMachineScreenHandler;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.slot.display.ItemSlotDisplay;
import dev.galacticraft.machinelib.api.transfer.CachingItemApiProvider;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.machine.storage.io.GalacticraftSlotGroups;
import dev.galacticraft.mod.screen.GCScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class EnergyStorageModuleBlockEntity extends MachineBlockEntity {
    public static final int CHARGE_TO_BATTERY_SLOT = 0;
    public static final int DRAIN_FROM_BATTERY_SLOT = 1;

    private final CachingItemApiProvider<EnergyStorage> batteryChargeSlot = CachingItemApiProvider.create(this.itemStorage().getSlot(CHARGE_TO_BATTERY_SLOT), EnergyStorage.ITEM);
    private final CachingItemApiProvider<EnergyStorage> batteryDrainSlot = CachingItemApiProvider.create(this.itemStorage().getSlot(DRAIN_FROM_BATTERY_SLOT), EnergyStorage.ITEM);

    public EnergyStorageModuleBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.ENERGY_STORAGE_MODULE, pos, state);
    }

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        return MachineItemStorage.Builder.create()
                .addSlot(GalacticraftSlotGroups.ENERGY_CHARGE, Constant.Filter.Item.CAN_EXTRACT_ENERGY, true, ItemSlotDisplay.create(102, 24))
                .addSlot(GalacticraftSlotGroups.ENERGY_DRAIN, Constant.Filter.Item.CAN_INSERT_ENERGY, true, ItemSlotDisplay.create(102, 48))
                .build();
    }

    @Override
    public long getEnergyCapacity() {
        return Galacticraft.CONFIG_MANAGER.get().energyStorageModuleStorageSize();
    }

    @Override
    public long getEnergyItemExtractionRate() {
        return super.getEnergyItemExtractionRate() * 2;
    }

    @Override
    public long getEnergyInsertionRate() {
        return super.getEnergyInsertionRate() * 2;
    }

    @Override
    public boolean canExposedExtractEnergy() {
        return true;
    }

    @Override
    public boolean canExposedInsertEnergy() {
        return true;
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        this.attemptChargeFromStack(this.batteryDrainSlot);
        this.attemptDrainPowerToStack(this.batteryChargeSlot);
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        this.trySpreadEnergy(world, state);
        return MachineStatus.INVALID;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return SimpleMachineScreenHandler.create(
                    syncId,
                    player,
                    this,
                    GCScreenHandlerType.ENERGY_STORAGE_MODULE_HANDLER
            );
        }
        return null;
    }
}
