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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.api.ComponentHelper;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.cottonmc.component.fluid.TankComponentHelper;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.Fraction;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Tickable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RefineryBlockEntity extends ConfigurableMachineBlockEntity implements Tickable {
    private static final Predicate<ItemStack>[] SLOT_FILTERS;
    private static final Fraction MAX_CAPACITY = Fraction.ofWhole(8);
    public static final int OIL_TANK = 0;
    public static final int FUEL_TANK = 1;
    public static final int CHARGE_SLOT = 0;
    public static final int FLUID_INPUT_SLOT = 1;
    public static final int FLUID_OUTPUT_SLOT = 2;

    static {
        //noinspection unchecked
        SLOT_FILTERS = new Predicate[3];
        SLOT_FILTERS[CHARGE_SLOT] = GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        SLOT_FILTERS[FLUID_INPUT_SLOT] = stack -> {
            if (stack.getItem() instanceof BucketItem && (((BucketItem) stack.getItem()).fluid.isIn(GalacticraftTags.OIL) || stack.getItem() == Items.BUCKET)) return true;
            TankComponent component = ComponentHelper.TANK.getComponent(stack, "refinery-filter");
            if (component != null) return component.getContents(0).getFluid().equals(GalacticraftFluids.CRUDE_OIL);
            return false;
        };
        SLOT_FILTERS[FLUID_OUTPUT_SLOT] = stack -> {
            if (stack.getItem() instanceof BucketItem && (((BucketItem) stack.getItem()).fluid == Fluids.EMPTY || ((BucketItem) stack.getItem()).fluid.isIn(GalacticraftTags.FUEL))) return true;
            TankComponent component = ComponentHelper.TANK.getComponent(stack, "refinery-filter-e");
            if (component != null) {
                return component.getContents(0).isEmpty();
            }
            return false;
        };
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
    public Fraction getFluidTankMaxCapacity() {
        return MAX_CAPACITY;
    }

    @Override
    public List<SideOption> validSideOptions() {
        return ImmutableList.of(SideOption.DEFAULT, SideOption.POWER_INPUT, SideOption.FLUID_INPUT, SideOption.FLUID_OUTPUT);
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
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
        if (this.getFluidTank().getContents(OIL_TANK).getAmount().compareTo(Fraction.ZERO) <= 0) return Status.NOT_ENOUGH_FLUID;
        if (this.isTankFull(FUEL_TANK)) return Status.FULL;
        return Status.ACTIVE;
    }

    @Override
    public void tickWork() {
        TankComponent component = ComponentHelper.TANK.getComponent(this.getInventory().getStack(FLUID_INPUT_SLOT));
        if (component != null && component.getContents(0).getFluid().isIn(GalacticraftTags.OIL)) {
            component.insertFluid(0, this.getFluidTank().insertFluid(OIL_TANK, component.takeFluid(0, Fraction.ONE, ActionType.PERFORM), ActionType.PERFORM), ActionType.PERFORM);
        }
        if (this.getInventory().getStack(FLUID_INPUT_SLOT).getItem() instanceof BucketItem) {
            if (this.getFluidTank().insertFluid(new FluidVolume(GalacticraftFluids.CRUDE_OIL, Fraction.ONE), ActionType.TEST).isEmpty()) {
                if (((BucketItem) this.getInventory().getStack(FLUID_INPUT_SLOT).getItem()).fluid.isIn(GalacticraftTags.OIL)) {
                    this.getInventory().setStack(FLUID_INPUT_SLOT, new ItemStack(Items.BUCKET));
                    this.getFluidTank().insertFluid(OIL_TANK, new FluidVolume(GalacticraftFluids.CRUDE_OIL, Fraction.ONE), ActionType.PERFORM);
                }
            }
        }
        if (this.getStatus().getType().isActive()) {
            Fraction f = this.getFluidTank().takeFluid(OIL_TANK, Fraction.of(5, 1000), ActionType.PERFORM).getAmount();
            f = this.getFluidTank().insertFluid(FUEL_TANK, new FluidVolume(GalacticraftFluids.FUEL, f), ActionType.PERFORM).getAmount();
            this.getFluidTank().insertFluid(OIL_TANK, new FluidVolume(this.getFluidTank().getContents(OIL_TANK).getFluid(), f), ActionType.PERFORM);
        }
        component = ComponentHelper.TANK.getComponent(this.getInventory().getStack(FLUID_OUTPUT_SLOT));
        if (component != null && (component.getContents(0).getFluid().isIn(GalacticraftTags.FUEL) || component.getContents(0).isEmpty())) {
            this.getFluidTank().insertFluid(FUEL_TANK, component.insertFluid(0, this.getFluidTank().takeFluid(FUEL_TANK, Fraction.ONE, ActionType.PERFORM), ActionType.PERFORM), ActionType.PERFORM);
        }
        if (this.getInventory().getStack(FLUID_OUTPUT_SLOT).getItem() == Items.BUCKET) {
            if (this.getFluidTank().getContents(FUEL_TANK).getAmount().compareTo(Fraction.ONE) >= 0) {
                this.getFluidTank().takeFluid(FUEL_TANK, Fraction.ONE, ActionType.PERFORM);
                this.getInventory().setStack(FLUID_OUTPUT_SLOT, new ItemStack(GalacticraftItems.FUEL_BUCKET));
            }
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
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public boolean isAcceptableFluid(int tank, FluidVolume volume) {
        return (tank == OIL_TANK && volume.getFluid().isIn(GalacticraftTags.OIL)) || (tank == FUEL_TANK && volume.getFluid().isIn(GalacticraftTags.FUEL));
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    private enum Status implements MachineStatus {
        /**
         * Refinery is active and is refining oil into fuel.
         */
        ACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.refining"), Formatting.GREEN, StatusType.WORKING),

        /**
         * Refinery has oil but the fuel tank is full.
         */
        FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle"), Formatting.GOLD, StatusType.OUTPUT_FULL),

        /**
         * The refinery is out of oil.
         */
        NOT_ENOUGH_FLUID(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_fluid"), Formatting.BLACK, StatusType.MISSING_FLUIDS),

        /**
         * The refinery is out of energy.
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), Formatting.RED, StatusType.MISSING_ENERGY);

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