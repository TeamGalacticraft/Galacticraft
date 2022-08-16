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

import dev.galacticraft.api.block.entity.MachineBlockEntity;
import dev.galacticraft.api.machine.MachineStatus;
import dev.galacticraft.api.machine.MachineStatuses;
import dev.galacticraft.api.machine.storage.MachineFluidStorage;
import dev.galacticraft.api.machine.storage.MachineItemStorage;
import dev.galacticraft.api.machine.storage.display.ItemSlotDisplay;
import dev.galacticraft.api.machine.storage.display.TankDisplay;
import dev.galacticraft.api.screen.SimpleMachineScreenHandler;
import dev.galacticraft.api.transfer.StateCachingStorageProvider;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.fluid.GalacticraftFluid;
import dev.galacticraft.mod.machine.GalacticraftMachineStatus;
import dev.galacticraft.mod.machine.storage.io.GalacticraftSlotTypes;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
public class RefineryBlockEntity extends MachineBlockEntity {
    private static final long MAX_CAPACITY = FluidUtil.bucketsToDroplets(8);
    public static final int OIL_TANK = 0;
    public static final int FUEL_TANK = 1;
    public static final int CHARGE_SLOT = 0;
    public static final int FLUID_INPUT_SLOT = 1;
    public static final int FLUID_OUTPUT_SLOT = 2;

    private final StateCachingStorageProvider<EnergyStorage> chargeSlot = StateCachingStorageProvider.create(this.itemStorage().getSlot(CHARGE_SLOT), EnergyStorage.ITEM);
    private final StateCachingStorageProvider<Storage<FluidVariant>> fluidInputSlot = StateCachingStorageProvider.create(this.itemStorage().getSlot(FLUID_INPUT_SLOT), FluidStorage.ITEM);
    private final StateCachingStorageProvider<Storage<FluidVariant>> fluidOutputSlot = StateCachingStorageProvider.create(this.itemStorage().getSlot(FLUID_OUTPUT_SLOT), FluidStorage.ITEM);


    public RefineryBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.REFINERY, pos, state);
    }

    @Override
    protected @NotNull MachineItemStorage createItemStorage() {
        return MachineItemStorage.Builder.create()
                .addSlot(GalacticraftSlotTypes.ENERGY_CHARGE, new ItemSlotDisplay(8, 7))
                .addSlot(GalacticraftSlotTypes.OIL_FILL, new ItemSlotDisplay(123, 7))
                .addSlot(GalacticraftSlotTypes.FUEL_DRAIN, new ItemSlotDisplay(153, 7))
                .build();
    }

    @Override
    protected @NotNull MachineFluidStorage createFluidStorage() {
        return MachineFluidStorage.Builder.create()
                .addTank(GalacticraftSlotTypes.OIL_INPUT, MAX_CAPACITY, new TankDisplay(122, 28, 48))
                .addTank(GalacticraftSlotTypes.FUEL_OUTPUT, MAX_CAPACITY, new TankDisplay(152, 28, 48))
                .build();
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state) {
        super.tickConstant(world, pos, state);
        this.attemptChargeFromStack(chargeSlot);

        Storage<FluidVariant> storage = this.fluidInputSlot.getStorage();
        if (storage != null) {
            FluidUtil.move(FluidVariant.of(GalacticraftFluid.CRUDE_OIL), storage, this.fluidStorage().getSlot(OIL_TANK), Long.MAX_VALUE, null);
        }
        storage = this.fluidOutputSlot.getStorage();
        if (storage != null) {
            FluidUtil.move(FluidVariant.of(GalacticraftFluid.FUEL), this.fluidStorage().getSlot(FUEL_TANK), storage, Long.MAX_VALUE, null);
        }
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel world, @NotNull BlockPos pos, @NotNull BlockState state) {
        if (this.fluidStorage().isEmpty(OIL_TANK)) return GalacticraftMachineStatus.MISSING_OIL;
        if (this.fluidStorage().isFull(FUEL_TANK)) return GalacticraftMachineStatus.FUEL_TANK_FULL;
        world.getProfiler().push("transaction");
        try (Transaction transaction = Transaction.openOuter()) {
            if (this.energyStorage().extract(Galacticraft.CONFIG_MANAGER.get().refineryEnergyConsumptionRate(), transaction) == Galacticraft.CONFIG_MANAGER.get().refineryEnergyConsumptionRate()) {
                long extracted;
                try (Transaction inner = Transaction.openNested(transaction)) {
                    extracted = this.fluidStorage().extract(OIL_TANK, FluidConstants.BUCKET / 20 / 5, inner).getAmount();
                }

                try (Transaction inner = Transaction.openNested(transaction)) {
                    long accepted = this.fluidStorage().insert(FUEL_TANK, FluidVariant.of(GalacticraftFluid.FUEL), extracted, inner);

                    if (this.fluidStorage().extract(OIL_TANK, accepted, inner).getAmount() == accepted) {
                        inner.commit();
                    }
                }
                transaction.commit();
                return GalacticraftMachineStatus.ACTIVE;
            } else {
                return MachineStatuses.NOT_ENOUGH_ENERGY;
            }
        } finally {
            world.getProfiler().pop();
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        if (this.getSecurity().hasAccess(player)) {
            return SimpleMachineScreenHandler.create(
                    syncId,
                    player,
                    this,
                    GalacticraftScreenHandlerType.REFINERY_HANDLER
            );
        }
        return null;
    }
}