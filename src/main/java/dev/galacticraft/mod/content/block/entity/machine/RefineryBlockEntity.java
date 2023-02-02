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

package dev.galacticraft.mod.content.block.entity.machine;

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.GCMachineTypes;
import dev.galacticraft.mod.machine.GCMachineStatus;
import dev.galacticraft.mod.screen.GCMenuTypes;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class RefineryBlockEntity extends MachineBlockEntity {
    @VisibleForTesting
    public static final long MAX_CAPACITY = FluidUtil.bucketsToDroplets(8);
    public static final int OIL_TANK = 0;
    public static final int FUEL_TANK = 1;
    public static final int CHARGE_SLOT = 0;
    public static final int FLUID_INPUT_SLOT = 1;
    public static final int FLUID_OUTPUT_SLOT = 2;

    public RefineryBlockEntity(BlockPos pos, BlockState state) {
        super(GCMachineTypes.REFINERY, pos, state);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(world, pos, state, profiler);
        this.attemptChargeFromStack(chargeSlot);

        Storage<FluidVariant> storage = this.fluidInputSlot.getApi();
        if (storage != null) {
            FluidUtil.move(FluidVariant.of(GCFluids.CRUDE_OIL), storage, this.fluidStorage().getSlot(OIL_TANK), Long.MAX_VALUE, null);
        }
        storage = this.fluidOutputSlot.getApi();
        if (storage != null) {
            FluidUtil.move(FluidVariant.of(GCFluids.FUEL), this.fluidStorage().getSlot(FUEL_TANK), storage, Long.MAX_VALUE, null);
        }
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        if (this.fluidStorage().isEmpty(OIL_TANK)) return GCMachineStatus.MISSING_OIL;
        if (this.fluidStorage().isFull(FUEL_TANK)) return GCMachineStatus.FUEL_TANK_FULL;
        profiler.push("transaction");
        try (Transaction transaction = Transaction.openOuter()) {
            if (this.energyStorage().extract(Galacticraft.CONFIG_MANAGER.get().refineryEnergyConsumptionRate(), transaction) == Galacticraft.CONFIG_MANAGER.get().refineryEnergyConsumptionRate()) {
                long extracted;
                try (Transaction inner = Transaction.openNested(transaction)) {
                    extracted = this.fluidStorage().extract(OIL_TANK, FluidConstants.BUCKET / 20 / 5, inner).getAmount();
                }

                try (Transaction inner = Transaction.openNested(transaction)) {
                    long accepted = this.fluidStorage().insert(FUEL_TANK, FluidVariant.of(GCFluids.FUEL), extracted, inner);

                    if (this.fluidStorage().extract(OIL_TANK, accepted, inner).getAmount() == accepted) {
                        inner.commit();
                    }
                }
                transaction.commit();
                return GCMachineStatus.ACTIVE;
            } else {
                return MachineStatuses.NOT_ENOUGH_ENERGY;
            }
        } finally {
            profiler.pop();
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return SimpleMachineMenu.create(
                    syncId,
                    player,
                    this,
                    GCMenuTypes.REFINERY
            );
        }
        return null;
    }
}