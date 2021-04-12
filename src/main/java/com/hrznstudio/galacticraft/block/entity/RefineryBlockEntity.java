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
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.screen.RefineryScreenHandler;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import com.hrznstudio.galacticraft.util.FluidUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.TickableBlockEntity;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RefineryBlockEntity extends ConfigurableMachineBlockEntity implements TickableBlockEntity {
    private static final ItemFilter[] SLOT_FILTERS;
    private static final FluidAmount MAX_CAPACITY = FluidAmount.ofWhole(8);
    public static final int OIL_TANK = 0;
    public static final int FUEL_TANK = 1;
    public static final int CHARGE_SLOT = 0;
    public static final int FLUID_INPUT_SLOT = 1;
    public static final int FLUID_OUTPUT_SLOT = 2;

    static {
        SLOT_FILTERS = new ItemFilter[3];
        SLOT_FILTERS[CHARGE_SLOT] = EnergyUtils.IS_EXTRACTABLE;
        SLOT_FILTERS[FLUID_INPUT_SLOT] = stack -> FluidUtils.canExtractFluids(stack, GalacticraftTags.OIL);
        SLOT_FILTERS[FLUID_OUTPUT_SLOT] = stack -> FluidUtils.canInsertFluids(stack, GalacticraftFluids.FUEL);
    }

    public RefineryBlockEntity() {
        super(GalacticraftBlockEntities.REFINERY_TYPE);
    }

    @Override
    public int getInventorySize() {
        return 3;
    }

    @Override
    public int getFluidTankSize() {
        return 2;
    }

    @Override
    public FluidAmount getFluidTankCapacity() {
        return MAX_CAPACITY;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT, SideOption.FLUID_INPUT, SideOption.FLUID_OUTPUT);
    }

    @Override
    public ItemFilter getFilterForSlot(int slot) {
        return SLOT_FILTERS[slot];
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
        if (this.getFluidTank().getInvFluid(OIL_TANK).getAmount_F().compareTo(FluidAmount.ZERO) <= 0) return Status.NOT_ENOUGH_FLUID;
        if (this.isTankFull(FUEL_TANK)) return Status.FULL;
        return Status.ACTIVE;
    }

    @Override
    public void tickWork() {
        this.getFluidTank().insertFluid(OIL_TANK, FluidUtils.extractFluid(this.getInventory().getSlot(FLUID_INPUT_SLOT), this.getFluidTank().getMaxAmount_F(OIL_TANK).sub(this.getFluidTank().getInvFluid(OIL_TANK).getAmount_F()), key -> GalacticraftTags.OIL.contains(key.getRawFluid())), Simulation.ACTION);
        this.getFluidTank().insertFluid(FUEL_TANK, FluidUtils.insertFluid(this.getInventory().getSlot(FLUID_OUTPUT_SLOT), this.getFluidTank().extractFluid(FUEL_TANK, null, null, FluidAmount.ONE, Simulation.ACTION)), Simulation.ACTION);

        if (this.getStatus().getType().isActive()) {
            FluidAmount amount = this.getFluidTank().extractFluid(OIL_TANK, key -> GalacticraftTags.OIL.contains(key.getRawFluid()), null, FluidAmount.of(5, 1000), Simulation.ACTION).getAmount_F();
            amount = this.getFluidTank().insertFluid(FUEL_TANK, FluidKeys.get(GalacticraftFluids.FUEL).withAmount(amount), Simulation.ACTION).getAmount_F();
            this.getFluidTank().insertFluid(OIL_TANK, this.getFluidTank().getInvFluid(OIL_TANK).getFluidKey().withAmount(amount), Simulation.ACTION);
        }
    }

    @Override
    public int getBaseEnergyConsumption() {
        return Galacticraft.configManager.get().refineryEnergyConsumptionRate();
    }

    @Override
    public boolean canPipeExtractFluid(int tank) {
        return tank == FUEL_TANK;
    }

    @Override
    public boolean canPipeInsertFluid(int tank) {
        return tank == OIL_TANK;
    }

    @Override
    public FluidFilter getFilterForTank(int tank) {
        if (tank == OIL_TANK) {
            return key -> GalacticraftTags.OIL.contains(key.getRawFluid());
        }
        return key -> GalacticraftTags.FUEL.contains(key.getRawFluid());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
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
        ACTIVE(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.refining"), ChatFormatting.GREEN, StatusType.WORKING),

        /**
         * Refinery has oil but the fuel tank is full.
         */
        FULL(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.idle"), ChatFormatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * The refinery is out of oil.
         */
        NOT_ENOUGH_FLUID(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.not_enough_fluid"), ChatFormatting.BLACK, StatusType.MISSING_FLUIDS),

        /**
         * The refinery is out of energy.
         */
        NOT_ENOUGH_ENERGY(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), ChatFormatting.RED, StatusType.MISSING_ENERGY);

        private final Component text;
        private final StatusType type;

        Status(TranslatableComponent text, ChatFormatting color, StatusType type) {
            this.type = type;
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        @Override
        public @NotNull Component getName() {
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