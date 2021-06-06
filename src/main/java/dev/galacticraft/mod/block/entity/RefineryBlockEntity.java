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

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.attribute.fluid.MachineFluidInv;
import dev.galacticraft.mod.attribute.item.MachineItemInv;
import dev.galacticraft.mod.fluid.GalacticraftFluid;
import dev.galacticraft.mod.screen.GalacticraftScreenHandlerType;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.tag.GalacticraftTag;
import dev.galacticraft.mod.util.EnergyUtil;
import dev.galacticraft.mod.util.FluidUtil;
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
    private static final FluidAmount MAX_CAPACITY = FluidAmount.ofWhole(8);
    public static final int OIL_TANK = 0;
    public static final int FUEL_TANK = 1;
    public static final int CHARGE_SLOT = 0;
    public static final int FLUID_INPUT_SLOT = 1;
    public static final int FLUID_OUTPUT_SLOT = 2;

    public RefineryBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.REFINERY, pos, state);
    }

    @Override
    protected MachineItemInv.Builder createInventory(MachineItemInv.Builder builder) {
        builder.addSlot(CHARGE_SLOT, SlotType.CHARGE, EnergyUtil.IS_EXTRACTABLE, 8, 7);
        builder.addSlot(FLUID_INPUT_SLOT, SlotType.FLUID_TANK_IO, stack -> FluidUtil.canExtractFluids(stack, GalacticraftTag.OIL), 123, 7);
        builder.addSlot(FLUID_OUTPUT_SLOT, SlotType.FLUID_TANK_IO, stack -> FluidUtil.canInsertFluids(stack, GalacticraftFluid.FUEL), 153, 7);
        return builder;
    }

    @Override
    protected MachineFluidInv.Builder createFluidInv(MachineFluidInv.Builder builder) {
        builder.addTank(OIL_TANK, SlotType.OIL_IN, Constant.Filter.OIL, 122, 28, 1);
        builder.addTank(FUEL_TANK, SlotType.FUEL_OUT, Constant.Filter.FUEL, 152, 28, 1);
        return builder;
    }

    @Override
    public FluidAmount fluidInvCapacity() {
        return MAX_CAPACITY;
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return Status.values()[index];
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);
    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (this.fluidInv().getInvFluid(OIL_TANK).amount().compareTo(FluidAmount.ZERO) <= 0) return Status.NOT_ENOUGH_FLUID;
        if (this.isTankFull(FUEL_TANK)) return Status.FULL;
        return Status.ACTIVE;
    }

    @Override
    public void tickWork() {
        FluidVolumeUtil.move(FluidAttributes.EXTRACTABLE.getFirst(this.itemInv().getSlot(FLUID_INPUT_SLOT)), this.fluidInv().getTank(OIL_TANK));
        FluidVolumeUtil.move(this.fluidInv().getTank(FUEL_TANK), FluidAttributes.INSERTABLE.getFirst(this.itemInv().getSlot(FLUID_OUTPUT_SLOT)));

        if (this.getStatus().getType().isActive()) {
            FluidAmount amount = this.fluidInv().extractFluid(OIL_TANK, key -> GalacticraftTag.OIL.contains(key.getRawFluid()), null, FluidAmount.of(5, 1000), Simulation.ACTION).amount();
            amount = this.fluidInv().insertFluid(FUEL_TANK, FluidKeys.get(GalacticraftFluid.FUEL).withAmount(amount), Simulation.ACTION).amount();
            this.fluidInv().insertFluid(OIL_TANK, this.fluidInv().getInvFluid(OIL_TANK).getFluidKey().withAmount(amount), Simulation.ACTION);
        }
    }

    @Override
    public int getBaseEnergyConsumption() {
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
            return 0;
        }
    }
}