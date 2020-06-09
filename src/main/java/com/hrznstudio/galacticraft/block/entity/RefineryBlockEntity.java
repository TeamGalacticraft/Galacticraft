/*
 * Copyright (c) 2019 HRZN LTD
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
import alexiil.mc.lib.attributes.fluid.FluidProviderItem;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.misc.Ref;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Tickable;
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;
import team.reborn.energy.EnergyTier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RefineryBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable, EnergyStorage {

    private static final ItemFilter[] SLOT_FILTERS;

    static {
        SLOT_FILTERS = new ItemFilter[3];
        SLOT_FILTERS[0] = GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
        SLOT_FILTERS[1] = stack -> stack.getItem() instanceof FluidProviderItem;
        SLOT_FILTERS[2] = stack -> stack.getItem() instanceof FluidProviderItem;
    }

    private final SimpleFixedFluidInv fluidInv = new SimpleFixedFluidInv(2, FluidVolume.BUCKET * 10) {
        @Override
        public FluidFilter getFilterForTank(int tank) {
            if (tank == 0) {
                return fluidKey -> fluidKey.withAmount(FluidVolume.BUCKET).getRawFluid().matchesType(GalacticraftFluids.CRUDE_OIL);
            } else if (tank == 1) {
                return fluidKey -> fluidKey.withAmount(FluidVolume.BUCKET).getRawFluid().matchesType(GalacticraftFluids.CRUDE_OIL);
            } else {
                return fluidKey -> false;
            }
        }

        @Override
        public boolean isFluidValidForTank(int tank, FluidKey fluid) {
            if (tank == 0) {
                return fluid.withAmount(FluidVolume.BUCKET).getRawFluid().matchesType(GalacticraftFluids.CRUDE_OIL);
            } else if (tank == 1) {
                return fluid.withAmount(FluidVolume.BUCKET).getRawFluid().matchesType(GalacticraftFluids.CRUDE_OIL);
            } else {
                return false;
            }
        }
    };

    public RefineryStatus status = RefineryStatus.IDLE;

    public RefineryBlockEntity() {
        super(GalacticraftBlockEntities.REFINERY_TYPE);
    }

    @Override
    protected int getInvSize() {
        return 3;
    }

    public SimpleFixedFluidInv getFluidInv() {
        return fluidInv;
    }

    @Override
    protected ItemFilter getFilterForSlot(int slot) {
        return SLOT_FILTERS[slot];
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

        if (getInventory().getStack(1).getItem() instanceof FluidProviderItem) {
            Ref<ItemStack> ref = new Ref<>(getInventory().getStack(1));
            FluidVolume output = ((FluidProviderItem) getInventory().getStack(1).getItem()).drain(ref);
            if (output.getRawFluid().matchesType(GalacticraftFluids.CRUDE_OIL)) {
                this.fluidInv.getTank(0).insert(output);
                getInventory().setStack(1, ref.obj, Simulation.ACTION);
            }
        }

        if (getEnergyAttribute().getCurrentEnergy() <= 0) {
            status = RefineryStatus.NOT_ENOUGH_ENERGY;
            return;
        }

        if (!fluidInv.getInvFluid(0).isEmpty() && !(fluidInv.getInvFluid(1).getAmount() >= fluidInv.getMaxAmount(1))) {
            this.status = RefineryStatus.ACTIVE;
        } else {
            this.status = RefineryStatus.IDLE;
        }
        if (status == RefineryStatus.IDLE) {
            idleEnergyDecrement(false);
        }

        if (status == RefineryStatus.ACTIVE) {
            this.getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, Galacticraft.configManager.get().refineryEnergyConsumptionRate(), Simulation.ACTION); //x2 an average machine


            FluidVolume extracted = this.fluidInv.getTank(0).extract(1);
            this.fluidInv.getTank(1).insert(FluidVolume.create(GalacticraftFluids.FUEL, extracted.getAmount()));
        }

        if (getInventory().getStack(2).getItem() instanceof FluidProviderItem) {
            Ref<ItemStack> stackRef = new Ref<>(getInventory().getStack(2));
            Ref<FluidVolume> fluidRef = new Ref<>(fluidInv.getTank(1).attemptExtraction(ConstantFluidFilter.ANYTHING, FluidVolume.BUCKET, Simulation.ACTION));
            ((FluidProviderItem) getInventory().getStack(2).getItem()).fill(stackRef, fluidRef);
            if (stackRef.obj != getInventory().getStack(2)) {
                getInventory().setStack(2, stackRef.obj, Simulation.ACTION);
            }
            fluidInv.getTank(1).insert(fluidRef.obj);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.put("FluidInventory", fluidInv.toTag());
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        fluidInv.fromTag(tag.getCompound("FluidInventory"));
    }

    @Override
    public double getStored(EnergySide face) {
        return GalacticraftEnergy.convertToTR(this.getEnergyAttribute().getCurrentEnergy());
    }

    @Override
    public void setStored(double amount) {
        this.getEnergyAttribute().setCurrentEnergy(GalacticraftEnergy.convertFromTR(amount));
    }

    @Override
    public double getMaxStoredPower() {
        return GalacticraftEnergy.convertToTR(getEnergyAttribute().getMaxEnergy());
    }

    @Override
    public EnergyTier getTier() {
        return EnergyTier.MEDIUM;
    }

    @Override
    public int getEnergyUsagePerTick() {
        return GalacticraftEnergy.Values.T2_MACHINE_ENERGY_USAGE;
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