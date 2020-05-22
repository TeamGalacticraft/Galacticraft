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
import alexiil.mc.lib.attributes.item.filter.AggregateItemFilter;
import alexiil.mc.lib.attributes.item.filter.ExactItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.collect.Maps;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.Tickable;
import team.reborn.energy.EnergySide;
import team.reborn.energy.EnergyStorage;
import team.reborn.energy.EnergyTier;

import java.util.Map;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CoalGeneratorBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable, EnergyStorage {

    private static final ItemFilter[] SLOT_FILTERS = new ItemFilter[2];

    static {
        SLOT_FILTERS[0] = AggregateItemFilter.anyOf(createFuelTimeMap().keySet().stream().map(ExactItemFilter::new).toArray(ItemFilter[]::new));
        SLOT_FILTERS[1] = GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
    }

    public CoalGeneratorStatus status = CoalGeneratorStatus.INACTIVE;
    public int fuelTimeMax;
    public int fuelTimeCurrent;
    public int fuelEnergyPerTick;
    private float heat = 0.0f;

    /*
     * Energy stats:
     * T1 machine: uses 30gj/t
     * Coal Generator: generates 120gj/t
     */

    public CoalGeneratorBlockEntity() {
        super(GalacticraftBlockEntities.COAL_GENERATOR_TYPE);
        //automatically mark dirty whenever the energy attribute is changed
        getLimitedInventory().getRule(0).disallowExtraction();
    }

    public static Map<Item, Pair<Integer, Integer>> createFuelTimeMap() {
        Map<Item, Pair<Integer, Integer>> map = Maps.newLinkedHashMap(); //Time (in ticks), energy per tick
        map.put(Items.COAL, new Pair<>(320, 120)); //1 coal will power 4 T1 machines // 120gj/t over 320 ticks
        map.put(Blocks.COAL_BLOCK.asItem(), new Pair<>(320 * 10, 120)); //lasts longer
        map.put(Items.CHARCOAL, new Pair<>(320, 120));
        return map;
    }

    public static boolean canUseAsFuel(ItemStack itemStack) {
        return createFuelTimeMap().containsKey(itemStack.getItem());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public CoalGeneratorStatus getStatusForTooltip() {
        return status;
    }

    @Override
    protected int getInvSize() {
        return 2;
    }

    @Override
    protected ItemFilter getFilterForSlot(int slot) {
        return SLOT_FILTERS[slot];
    }

    @Override
    public void tick() {
        if (this.world.isClient || disabled()) {
            return;
        }

        if (status == CoalGeneratorStatus.IDLE || status == CoalGeneratorStatus.INACTIVE) {
            if (heat >= 1.0F) {
                heat -= 0.05F;
            } else {
                heat = 0;
            }
        }

        if (canUseAsFuel(getInventory().getStack(0)) && getEnergyAttribute().getCurrentEnergy() < getEnergyAttribute().getMaxEnergy() && (status == CoalGeneratorStatus.INACTIVE || status == CoalGeneratorStatus.IDLE)) {
            this.status = CoalGeneratorStatus.WARMING;

            this.fuelTimeMax = createFuelTimeMap().get(getInventory().getStack(0).getItem()).getLeft();
            this.fuelTimeCurrent = 0;
            this.fuelEnergyPerTick = createFuelTimeMap().get(this.getInventory().getStack(0).getItem()).getRight();

            ItemStack stack = getInventory().getStack(0).copy();
            stack.decrement(1);
            getInventory().setStack(0, stack, Simulation.ACTION);
        }

        if (this.status == CoalGeneratorStatus.WARMING) {
            if (this.heat >= 1.0f) {
                this.status = CoalGeneratorStatus.ACTIVE;
            }
            this.heat += 0.005f; //10 secs of heating - 1/8th of the time is spent heating (in this case) when it comes to coal/charcoal
        }

        if (status == CoalGeneratorStatus.ACTIVE || this.status == CoalGeneratorStatus.WARMING) {
            fuelTimeCurrent++;
            getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) (Galacticraft.configManager.get().coalGeneratorEnergyProductionRate() * heat), Simulation.ACTION);

            if (fuelTimeCurrent >= fuelTimeMax) {
                this.status = CoalGeneratorStatus.INACTIVE;
                this.fuelTimeCurrent = 0;
            }
        }

        trySpreadEnergy();
        attemptDrainPowerToStack(1);
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
        return 0;
    }

    /**
     * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
     */
    public enum CoalGeneratorStatus {
        /**
         * Generator is active and is generating energy.
         */
        ACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.active").setStyle(Style.EMPTY.withColor(Formatting.GREEN)).getString()),
        /**
         * Generator has fuel but buffer is full.
         */
        IDLE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle").setStyle(Style.EMPTY.withColor(Formatting.GOLD)).getString()),
        /**
         * The generator has no fuel.
         */
        INACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.inactive").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).getString()),
        /**
         * The generator is warming up.
         */
        WARMING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.warming").setStyle(Style.EMPTY.withColor(Formatting.GREEN)).getString());

        private final String name;

        CoalGeneratorStatus(String name) {
            this.name = name;
        }

        public static CoalGeneratorStatus get(int index) {
            if (index < 0) return ACTIVE;
            return CoalGeneratorStatus.values()[index % CoalGeneratorStatus.values().length];
        }

        @Override
        public String toString() {
            return name;
        }
    }
}