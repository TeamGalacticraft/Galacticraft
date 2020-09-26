/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.block.entity;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import com.hrznstudio.galacticraft.tag.GalacticraftTags;
import io.github.cottonmc.component.api.ActionType;
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

import java.util.List;
import java.util.function.Predicate;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RefineryBlockEntity extends ConfigurableMachineBlockEntity implements Tickable {
    private static final Predicate<ItemStack>[] SLOT_FILTERS;

    static {
        SLOT_FILTERS = new Predicate[3];
        SLOT_FILTERS[0] = GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        SLOT_FILTERS[1] = stack -> {
            if (stack.getItem() instanceof BucketItem && ((BucketItem) stack.getItem()).fluid.isIn(GalacticraftTags.OIL) || stack.getItem() == Items.BUCKET) return true;
            TankComponent component = TankComponentHelper.INSTANCE.getComponent(stack, "refinery-filter");
            if (component != null) return component.getContents(0).getFluid().equals(GalacticraftFluids.CRUDE_OIL);
            return false;
        };
        SLOT_FILTERS[2] = stack -> {
            if (stack.getItem() instanceof BucketItem && (((BucketItem) stack.getItem()).fluid == Fluids.EMPTY || ((BucketItem) stack.getItem()).fluid.isIn(GalacticraftTags.FUEL))) return true;
            TankComponent component = TankComponentHelper.INSTANCE.getComponent(stack, "refinery-filter-e");
            if (component != null) {
                return component.getContents(0).isEmpty();
            }
            return false;
        };
    }

    public RefineryStatus status = RefineryStatus.IDLE;

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
        return Fraction.ofWhole(8);
    }

    @Override
    public List<SideOption> validSideOptions() {
        return Lists.asList(SideOption.DEFAULT, SideOption.POWER_INPUT, new SideOption[]{SideOption.FLUID_INPUT, SideOption.FLUID_OUTPUT});
    }

    @Override
    public Predicate<ItemStack> getFilterForSlot(int slot) {
        return SLOT_FILTERS[slot];
    }

    @Override
    public boolean canExtractEnergy() {
        return false;
    }

    @Override
    public boolean canInsertEnergy() {
        return true;
    }

    @Override
    public RefineryStatus getStatusForTooltip() {
        return status;
    }

    @Override
    public void tick() {
        if (world.isClient || disabled()) {
            if (disabled()) {
                idleEnergyDecrement(true);
            }
            return;
        }

        attemptChargeFromStack(0);
        trySpreadEnergy();

        if (this.getFluidTank().getContents(0).getAmount().compareTo(this.getFluidTank().getMaxCapacity(0)) < 0) {
            TankComponent component = TankComponentHelper.INSTANCE.getComponent(getInventory().getStack(1));
            if (component != null && component.getContents(0).getFluid().equals(GalacticraftFluids.CRUDE_OIL)) {
                Fraction needed = this.getFluidTank().getMaxCapacity(0).subtract(this.getFluidTank().getContents(0).getAmount());
                Fraction taken = component.takeFluid(0, needed, ActionType.PERFORM).getAmount();
                this.getFluidTank().insertFluid(0, new FluidVolume(GalacticraftFluids.CRUDE_OIL, taken), ActionType.PERFORM);
            } else {
                if (getInventory().getStack(1).getItem() instanceof BucketItem) {
                    Fluid fluid = ((BucketItem) getInventory().getStack(1).getItem()).fluid;
                    if (fluid.isIn(GalacticraftTags.OIL)) {
                        if (this.getFluidTank().getMaxCapacity(0).subtract(this.getFluidTank().getContents(0).getAmount()).doubleValue() >= 1.0D) {
                            getInventory().setStack(1, new ItemStack(Items.BUCKET));
                            getFluidTank().insertFluid(0, new FluidVolume(GalacticraftFluids.CRUDE_OIL, Fraction.ONE), ActionType.PERFORM);
                        }
                    }
                }
            }
        }

        if (!this.getFluidTank().getContents(1).isEmpty()) {
            ItemStack stack = getInventory().getStack(2);
            TankComponent component = TankComponentHelper.INSTANCE.getComponent(stack);
            if (component != null
                    && ((component.getContents(0).getFluid().equals(GalacticraftFluids.FUEL) && component.getContents(0).getAmount().compareTo(component.getMaxCapacity(0)) < 0)
                    || component.getContents(0).getFluid() == Fluids.EMPTY)) {
                Fraction needed = component.getMaxCapacity(0)
                        .subtract(component.getContents(0).getAmount());
                if (!needed.equals(Fraction.ZERO)) {
                    component.insertFluid(0, this.getFluidTank().takeFluid(1, needed, ActionType.PERFORM), ActionType.PERFORM);
                }
            } else {
                if (stack.getItem() instanceof BucketItem) {
                    if (((BucketItem) stack.getItem()).fluid == Fluids.EMPTY) {
                        if (this.getFluidTank().getContents(1).getAmount().compareTo(Fraction.ONE) >= 0) {
                            this.getFluidTank().takeFluid(1, Fraction.ONE, ActionType.PERFORM);
                            getInventory().setStack(2, new ItemStack(GalacticraftFluids.FUEL.getBucketItem()));
                        }
                    }
                }
            }
        }

        if (getCapacitor().getCurrentEnergy() <= 0) {
            status = RefineryStatus.NOT_ENOUGH_ENERGY;
            return;
        }

        if (!this.getFluidTank().getContents(0).isEmpty() && (this.getFluidTank().getContents(1).getAmount().compareTo(this.getFluidTank().getMaxCapacity(1))) < 0) {
            this.status = RefineryStatus.ACTIVE;
        } else {
            this.status = RefineryStatus.IDLE;
        }
        if (status == RefineryStatus.IDLE) {
            idleEnergyDecrement(false);
        }

        if (status == RefineryStatus.ACTIVE) {
            this.getCapacitor().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, getEnergyUsagePerTick(), ActionType.PERFORM); //x2 an average machine

            FluidVolume extracted = this.getFluidTank().takeFluid(0, Fraction.of(5, 1000), ActionType.PERFORM);
            this.getFluidTank().insertFluid(1, new FluidVolume(GalacticraftFluids.FUEL, extracted.getAmount()), ActionType.PERFORM);
        }
    }

    @Override
    public int getEnergyUsagePerTick() {
        return Galacticraft.configManager.get().refineryEnergyConsumptionRate();
    }

    @Override
    public boolean canHopperExtractItems(int slot) {
        return false;
    }

    @Override
    public boolean canHopperInsertItems(int slot) {
        return false;
    }

    @Override
    public boolean canExtractFluid(int tank) {
        return tank == 1;
    }

    @Override
    public boolean canInsertFluid(int tank) {
        return tank == 0;
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public boolean isAcceptableFluid(int tank, FluidVolume volume) {
        return (tank == 0 && volume.getFluid().isIn(GalacticraftTags.OIL)) || (tank == 1 && volume.getFluid().isIn(GalacticraftTags.FUEL));
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    public enum RefineryStatus implements MachineStatus {

        /**
         * Refinery is active and is refining oil into fuel.
         */
        ACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.refining"), Formatting.GREEN),

        /**
         * Refinery has oil but the fuel tank is full.
         */
        FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle"), Formatting.GOLD),

        /**
         * The refinery is out of oil.
         */
        IDLE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle"), Formatting.GRAY),

        /**
         * The refinery is out of oil.
         */
        NOT_ENOUGH_ENERGY(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_energy"), Formatting.RED);

        private final Text text;

        RefineryStatus(TranslatableText text, Formatting color) {
            this.text = text.setStyle(Style.EMPTY.withColor(color));
        }

        @Override
        public Text getText() {
            return text;
        }
    }
}