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

package com.hrznstudio.galacticraft.blocks.machines.coalgenerator;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.filter.AggregateItemFilter;
import alexiil.mc.lib.attributes.item.filter.ExactItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.configurable.SideOption;
import com.hrznstudio.galacticraft.api.wire.WireConnectionType;
import com.hrznstudio.galacticraft.api.wire.WireNetwork;
import com.hrznstudio.galacticraft.api.wire.WireUtils;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergyType;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.util.BlockOptionUtils;
import io.github.cottonmc.energy.api.EnergyAttribute;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CoalGeneratorBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable {

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

    public CoalGeneratorBlockEntity() {
        super(GalacticraftBlockEntities.COAL_GENERATOR_TYPE);
        //automatically mark dirty whenever the energy attribute is changed
        getLimitedInventory().getRule(0).disallowExtraction();
    }

    public static Map<Item, Pair<Integer, Integer>> createFuelTimeMap() {
        Map<Item, Pair<Integer, Integer>> map = Maps.newLinkedHashMap();
        map.put(Items.COAL, new Pair<>(1600, 3));
        map.put(Blocks.COAL_BLOCK.asItem(), new Pair<>(1600 * 9, 4));
        map.put(Items.CHARCOAL, new Pair<>(1600, 3));
        return map;
    }

    public static boolean canUseAsFuel(ItemStack itemStack) {
        return createFuelTimeMap().containsKey(itemStack.getItem());
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
        if (world.isClient || !enabled()) {
            return;
        }

        if (canUseAsFuel(getInventory().getInvStack(0)) && getEnergyAttribute().getCurrentEnergy() < getEnergyAttribute().getMaxEnergy() && (status == CoalGeneratorStatus.INACTIVE || status == CoalGeneratorStatus.IDLE)) {
            this.status = CoalGeneratorStatus.WARMING;

            this.fuelTimeMax = createFuelTimeMap().get(getInventory().getInvStack(0).getItem()).getLeft();
            this.fuelTimeCurrent = 0;
            this.fuelEnergyPerTick = createFuelTimeMap().get(this.getInventory().getInvStack(0).getItem()).getRight();

            ItemStack stack = getInventory().getInvStack(0).copy();
            stack.decrement(1);
            getInventory().forceSetInvStack(0, stack);
        }

        if (this.status == CoalGeneratorStatus.WARMING) {
            if (this.heat >= 1.0f) {
                this.status = CoalGeneratorStatus.ACTIVE;
            }
            this.heat += 0.005f; //10 secs of heating - 1/8th of the time is spent heating (in this case) when it comes to coal/charcoal
        }

        if (status == CoalGeneratorStatus.ACTIVE || this.status == CoalGeneratorStatus.WARMING) {
            fuelTimeCurrent++;
            getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) (fuelEnergyPerTick * heat), Simulation.ACTION);

            if (fuelTimeCurrent >= fuelTimeMax) {
                this.status = CoalGeneratorStatus.INACTIVE;
                this.fuelTimeCurrent = 0;
            }
        }

        trySpreadEnergy();
        attemptDrainPowerToStack(1);
    }
}