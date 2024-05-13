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

import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.storage.slot.FluidResourceSlot;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OxygenCompressorBlockEntity extends MachineBlockEntity {
    public static final int CHARGE_SLOT = 0;
    public static final int OXYGEN_OUTPUT_SLOT = 1;
    public static final int OXYGEN_TANK = 0;
    public static final long MAX_OXYGEN = FluidUtil.bucketsToDroplets(50);

    public OxygenCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.OXYGEN_COMPRESSOR, pos, state);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        this.chargeFromStack(CHARGE_SLOT);
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        FluidResourceSlot oxygenStorage = this.fluidStorage().getSlot(OXYGEN_TANK);
        if (oxygenStorage.isEmpty()) return GCMachineStatuses.NOT_ENOUGH_OXYGEN;
        profiler.push("find_storage");
        Storage<FluidVariant> tank = this.itemStorage().getSlot(OXYGEN_OUTPUT_SLOT).find(FluidStorage.ITEM);
        profiler.pop();
        if (tank == null) return GCMachineStatuses.MISSING_OXYGEN_TANK;
        long space;
        try (Transaction transaction = Transaction.openOuter()) {
            space = tank.insert(FluidVariant.of(Gases.OXYGEN), Long.MAX_VALUE, transaction);
        }
        if (!tank.supportsInsertion() || space == 0) return GCMachineStatuses.OXYGEN_TANK_FULL;

        profiler.push("transaction");
        if (this.energyStorage().canExtract(Galacticraft.CONFIG.oxygenCompressorEnergyConsumptionRate())) {
            long available = oxygenStorage.extract(Gases.OXYGEN, space);
            if (available > 0) {
                this.energyStorage().extract(Galacticraft.CONFIG.oxygenCompressorEnergyConsumptionRate());
                try (Transaction transaction = Transaction.openOuter()) {
                    tank.insert(FluidVariant.of(Gases.OXYGEN), available, transaction);
                    transaction.commit();
                    profiler.pop();
                    return GCMachineStatuses.COMPRESSING;
                }
            } else {
                profiler.pop();
                return GCMachineStatuses.NOT_ENOUGH_OXYGEN;
            }
        } else {
            profiler.pop();
            return MachineStatuses.NOT_ENOUGH_ENERGY;
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return new MachineMenu<>(
                    syncId,
                    (ServerPlayer) player,
                    this
            );
        }
        return null;
    }
}