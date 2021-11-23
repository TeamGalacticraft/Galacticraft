/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import dev.galacticraft.api.fluid.FluidStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.fluid.GalacticraftFluid;
import dev.galacticraft.mod.lookup.storage.MachineFluidStorage;
import dev.galacticraft.mod.lookup.storage.MachineItemStorage;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.screen.slot.FluidTankSettings;
import dev.galacticraft.mod.screen.slot.SlotSettings;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public RefineryBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.REFINERY, pos, state);
    }

    @Override
    protected MachineItemStorage.Builder createInventory(MachineItemStorage.Builder builder) {
        builder.addSlot(SlotSettings.Builder.create(8, 7, SlotType.CHARGE).filter(Constant.Filter.Item.CAN_EXTRACT_ENERGY).build());
        builder.addSlot(SlotSettings.Builder.create(123, 7, SlotType.FLUID_TANK_IO).filter(Constant.Filter.Item.CAN_EXTRACT_OIL).build());
        builder.addSlot(SlotSettings.Builder.create(153, 7, SlotType.FLUID_TANK_IO).filter(Constant.Filter.Item.CAN_INSERT_FUEL).build());
        return builder;
    }

    @Override
    protected MachineFluidStorage.Builder createFluidInv(MachineFluidStorage.Builder builder) {
        builder.addTank(FluidTankSettings.Builder.create(122, 28, SlotType.OIL_IN).filter(Constant.Filter.Fluid.OIL).build());
        builder.addTank(FluidTankSettings.Builder.create(152, 28, SlotType.FUEL_OUT).filter(Constant.Filter.Fluid.FUEL).build());
        return builder;
    }

    @Override
    public long energyExtractionRate() {
        return 0;
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    protected void tickDisabled() {

    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);

        Storage<FluidVariant> storage = ContainerItemContext.ofSingleSlot(this.itemStorage().getSlot(FLUID_INPUT_SLOT)).find(FluidStorage.ITEM);
        if (storage != null) {
            FluidUtil.move(FluidVariant.of(GalacticraftFluid.CRUDE_OIL), storage, this.fluidInv().getTank(OIL_TANK), Long.MAX_VALUE, null);
        }
        storage = ContainerItemContext.ofSingleSlot(this.itemStorage().getSlot(FLUID_INPUT_SLOT)).find(FluidStorage.ITEM);
        if (storage != null) {
            FluidUtil.move(FluidVariant.of(GalacticraftFluid.FUEL), this.fluidInv().getTank(FUEL_TANK), storage, Long.MAX_VALUE, null);
        }
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (this.fluidInv().getStack(OIL_TANK).amount() == 0) return Status.NOT_ENOUGH_FLUID;
        if (this.isTankFull(FUEL_TANK)) return Status.FULL;
        return Status.ACTIVE;
    }

    @Override
    public void tickWork() {
        if (this.getStatus().getType().isActive()) {
            try (Transaction transaction = Transaction.openOuter()) {
                FluidVariant resource = this.fluidInv().getTank(OIL_TANK).getResource();
                long amount = this.fluidInv().extractFluid(OIL_TANK, 405L, transaction).amount();
                amount = this.fluidInv().insertFluid(FUEL_TANK, new FluidStack(FluidVariant.of(GalacticraftFluid.FUEL), amount), transaction).amount();
                this.fluidInv().insertFluid(OIL_TANK, new FluidStack(resource, amount), transaction).amount();
                transaction.commit();
            }
        }
    }

    @Override
    public long energyConsumption() {
        return Galacticraft.CONFIG_MANAGER.get().refineryEnergyConsumptionRate();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.security().hasAccess(player)) return GalacticraftScreenHandlerType.create(GalacticraftScreenHandlerType.REFINERY_HANDLER, syncId, inv, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
     */
    private enum Status implements MachineStatus {
        /**
         * Refinery is active and is refining oil into fuel.
         */
        ACTIVE(new TranslatableText("ui.galacticraft.machine.status.refining"), Formatting.GREEN, StatusType.WORKING),

        /**
         * Refinery has oil but the fuel tank is full.
         */
        FULL(new TranslatableText("ui.galacticraft.machine.status.idle"), Formatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * The refinery is out of oil.
         */
        NOT_ENOUGH_FLUID(new TranslatableText("ui.galacticraft.machine.status.not_enough_fluid"), Formatting.BLACK, StatusType.MISSING_FLUIDS),

        /**
         * The refinery is out of energy.
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft.machine.status.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY);

        private final Text text;
        private final StatusType type;

        Status(TranslatableText text, Formatting color, StatusType type) {
            this.type = type;
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        @Override
        public @NotNull Text getName() {
            return text;
        }

        @Override
        public @NotNull StatusType getType() {
            return type;
        }

        @Override
        public int getIndex() {
            return this.ordinal();
        }
    }
}