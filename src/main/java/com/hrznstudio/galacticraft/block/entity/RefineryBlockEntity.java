/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.block.entity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.entity.MachineBlockEntity;
import com.hrznstudio.galacticraft.api.machine.MachineStatus;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.fluid.GalacticraftFluids;
import com.hrznstudio.galacticraft.screen.RefineryScreenHandler;
import com.hrznstudio.galacticraft.screen.slot.SlotType;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import com.hrznstudio.galacticraft.util.FluidUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Tickable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RefineryBlockEntity extends MachineBlockEntity implements Tickable {
    private static final FluidAmount MAX_CAPACITY = FluidAmount.ofWhole(8);
    public static final int OIL_TANK = 0;
    public static final int FUEL_TANK = 1;
    public static final int CHARGE_SLOT = 0;
    public static final int FLUID_INPUT_SLOT = 1;
    public static final int FLUID_OUTPUT_SLOT = 2;


    public RefineryBlockEntity() {
        super(GalacticraftBlockEntities.REFINERY_TYPE);
        this.getInventory().addSlot(CHARGE_SLOT, SlotType.CHARGE, EnergyUtils.IS_EXTRACTABLE, 8, 7);
        this.getInventory().addSlot(FLUID_INPUT_SLOT, SlotType.FLUID_TANK_INPUT, stack -> FluidUtils.canExtractFluids(stack, GalacticraftTags.OIL), 123, 7);
        this.getInventory().addSlot(FLUID_OUTPUT_SLOT, SlotType.FLUID_TANK_OUTPUT, stack -> FluidUtils.canInsertFluids(stack, GalacticraftFluids.FUEL), 153, 7);

        this.getFluidInv().addSlot(OIL_TANK, SlotType.OIL, Constants.Filter.OIL, 122, 28, 0);
        this.getFluidInv().addSlot(FUEL_TANK, SlotType.FUEL_OUT, Constants.Filter.FUEL, 152, 28, 0);
    }

    @Override
    public FluidAmount getFluidTankCapacity() {
        return MAX_CAPACITY;
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    protected MachineStatus getStatusById(int index) {
        return null;
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        this.attemptChargeFromStack(CHARGE_SLOT);

    }

    @Override
    public @NotNull MachineStatus updateStatus() {
        if (!this.hasEnergyToWork()) return Status.NOT_ENOUGH_ENERGY;
        if (this.getFluidInv().getInvFluid(OIL_TANK).getAmount_F().compareTo(FluidAmount.ZERO) <= 0) return Status.NOT_ENOUGH_FLUID;
        if (this.isTankFull(FUEL_TANK)) return Status.FULL;
        return Status.ACTIVE;
    }

    @Override
    public void tickWork() {
        this.getFluidInv().insertFluid(OIL_TANK, FluidUtils.extractFluid(this.getInventory().getSlot(FLUID_INPUT_SLOT), this.getFluidInv().getMaxAmount_F(OIL_TANK).sub(this.getFluidInv().getInvFluid(OIL_TANK).getAmount_F()), key -> GalacticraftTags.OIL.contains(key.getRawFluid())), Simulation.ACTION);
        this.getFluidInv().insertFluid(FUEL_TANK, FluidUtils.insertFluid(this.getInventory().getSlot(FLUID_OUTPUT_SLOT), this.getFluidInv().extractFluid(FUEL_TANK, null, null, FluidAmount.ONE, Simulation.ACTION)), Simulation.ACTION);

        if (this.getStatus().getType().isActive()) {
            FluidAmount amount = this.getFluidInv().extractFluid(OIL_TANK, key -> GalacticraftTags.OIL.contains(key.getRawFluid()), null, FluidAmount.of(5, 1000), Simulation.ACTION).getAmount_F();
            amount = this.getFluidInv().insertFluid(FUEL_TANK, FluidKeys.get(GalacticraftFluids.FUEL).withAmount(amount), Simulation.ACTION).getAmount_F();
            this.getFluidInv().insertFluid(OIL_TANK, this.getFluidInv().getInvFluid(OIL_TANK).getFluidKey().withAmount(amount), Simulation.ACTION);
        }
    }

    @Override
    public int getBaseEnergyConsumption() {
        return Galacticraft.CONFIG_MANAGER.get().refineryEnergyConsumptionRate();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (this.getSecurity().hasAccess(player)) return new RefineryScreenHandler(syncId, player, this);
        return null;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {
        /**
         * Refinery is active and is refining oil into fuel.
         */
        ACTIVE(new TranslatableText("ui.galacticraft-rewoven.machine.status.refining"), Formatting.GREEN, StatusType.WORKING),

        /**
         * Refinery has oil but the fuel tank is full.
         */
        FULL(new TranslatableText("ui.galacticraft-rewoven.machine.status.idle"), Formatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * The refinery is out of oil.
         */
        NOT_ENOUGH_FLUID(new TranslatableText("ui.galacticraft-rewoven.machine.status.not_enough_fluid"), Formatting.BLACK, StatusType.MISSING_FLUIDS),

        /**
         * The refinery is out of energy.
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machine.status.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY);

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