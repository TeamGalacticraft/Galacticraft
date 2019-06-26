package com.hrznstudio.galacticraft.blocks.machines.coalgenerator;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.filter.AggregateItemFilter;
import alexiil.mc.lib.attributes.item.filter.ExactItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.configurable.SideOption;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergyType;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.util.BlockOptionUtils;
import io.github.cottonmc.energy.api.EnergyAttribute;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

    private final List<Runnable> listeners = Lists.newArrayList();
    public CoalGeneratorStatus status = CoalGeneratorStatus.INACTIVE;
    public int fuelTimeMax;
    public int fuelTimeCurrent;
    public int fuelEnergyPerTick;
    public SideOption[] sideOptions = {SideOption.BLANK, SideOption.POWER_OUTPUT};
    public Map<Direction, SideOption> selectedOptions = BlockOptionUtils.getDefaultSideOptions();
    private float heat = 0.0f;

    public CoalGeneratorBlockEntity() {
        super(GalacticraftBlockEntities.COAL_GENERATOR_TYPE);
        //automatically mark dirty whenever the energy attribute is changed
        selectedOptions.put(Direction.SOUTH, SideOption.POWER_OUTPUT);
        getLimitedInventory().getRule(0).disallowExtraction();
    }

    public static Map<Item, Integer> createFuelTimeMap() {
        Map<Item, Integer> map = Maps.newLinkedHashMap();
        map.put(Items.COAL, 1600);
        map.put(Blocks.COAL_BLOCK.asItem(), map.get(Items.COAL) * 9);
        map.put(Items.CHARCOAL, 1600);
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
        if (world.isClient || !isActive()) {
            return;
        }
        int prev = getEnergy().getCurrentEnergy();

        if (canUseAsFuel(getInventory().getInvStack(0)) && (status == CoalGeneratorStatus.INACTIVE || status == CoalGeneratorStatus.IDLE) && getEnergy().getCurrentEnergy() < getEnergy().getMaxEnergy()) {
            if (status == CoalGeneratorStatus.INACTIVE) {
                this.status = CoalGeneratorStatus.WARMING;
            } else {
                this.status = CoalGeneratorStatus.ACTIVE;
            }
            this.fuelTimeMax = 200;
            this.fuelTimeCurrent = 0;
            this.fuelEnergyPerTick = createFuelTimeMap().get(this.getInventory().getInvStack(0).getItem());

            getInventory().getSlot(0).extract(1);
        }

        if (this.status == CoalGeneratorStatus.WARMING) {
            if (this.heat >= 10.0f) {
                this.status = CoalGeneratorStatus.ACTIVE;
            }
            this.heat += 0.1f;
        }

        if (status == CoalGeneratorStatus.ACTIVE) {
            fuelTimeCurrent++;
            getEnergy().setCurrentEnergy(Math.min(getEnergy().getMaxEnergy(), getEnergy().getCurrentEnergy() + fuelEnergyPerTick));

            if (fuelTimeCurrent >= fuelTimeMax) {
                this.status = CoalGeneratorStatus.IDLE;
                this.fuelTimeCurrent = 0;
            }
        }

        for (Direction direction : Direction.values()) {
            if (selectedOptions.get(direction).equals(SideOption.POWER_OUTPUT)) {
                EnergyAttribute energyAttribute = EnergyAttribute.ENERGY_ATTRIBUTE.getFirstFromNeighbour(this, direction);
                if (energyAttribute.canInsertEnergy()) {
                    getEnergy().setCurrentEnergy(energyAttribute.insertEnergy(new GalacticraftEnergyType(), 1, Simulation.ACTION));
                }
            }
        }
        attemptDrainPowerToStack(1);
    }
}