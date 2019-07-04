package com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.util.Tickable;

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BasicSolarPanelBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable {
    private final List<Runnable> listeners = Lists.newArrayList();

    public BasicSolarPanelStatus status = BasicSolarPanelStatus.NIGHT;

    public BasicSolarPanelBlockEntity() {
        super(GalacticraftBlockEntities.BASIC_SOLAR_PANEL_TYPE);
    }

    @Override
    protected int getInvSize() {
        return 1;
    }

    @Override
    protected ItemFilter getFilterForSlot(int slot) {
        return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
    }

    @Override
    public void tick() {
        if (world.isClient || !active()) {
            return;
        }
        long time = world.getTimeOfDay();
        while (true) {
            if (time <= -1) {
                time += 24000;
                break;
            }
            time -= 24000;
        }

        if (world.isRaining() || world.isThundering()) {
            status = BasicSolarPanelStatus.RAINING;
        } else if ((time > 250 && time < 12000)) {
            if (getEnergy().getCurrentEnergy() < getEnergy().getMaxEnergy()) {
                status = BasicSolarPanelStatus.COLLECTING;
            } else {
                status = BasicSolarPanelStatus.FULL;
            }
        } else {
            status = BasicSolarPanelStatus.NIGHT;
        }

        if (status == BasicSolarPanelStatus.COLLECTING) {
            if (time > 6000) {
                getEnergy().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) ((6000D - ((double) time - 6000D)) / 133.3333333333D), Simulation.ACTION);
            } else {
                getEnergy().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) (((double) time / 133.3333333333D)), Simulation.ACTION);
            }
        }

        attemptDrainPowerToStack(0);

        /*for (Direction direction : Direction.values()) {
            if (selectedOptions.get(direction).equals(SideOption.POWER_OUTPUT)) {
                EnergyAttribute energyAttribute = EnergyAttribute.ENERGY_ATTRIBUTE.getFirstFromNeighbour(this, direction);
                if (energyAttribute.canInsertEnergy()) {
                    this.getEnergy().setCurrentEnergy(energyAttribute.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, Simulation.ACTION));
                }
            }
        }*/

    }

    @Override
    protected int getBatteryTransferRate() {
        return 10;
    }
}