package com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel;

import alexiil.mc.lib.attributes.DefaultedAttribute;
import alexiil.mc.lib.attributes.SearchOptions;
import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.api.configurable.SideOptions;
import com.hrznstudio.galacticraft.blocks.machines.MachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.util.BlockOptionUtils;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.prospector.silk.util.ActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BasicSolarPanelBlockEntity extends MachineBlockEntity implements Tickable {
    private final List<Runnable> listeners = Lists.newArrayList();

    public BasicSolarPanelStatus status = BasicSolarPanelStatus.NIGHT;

    public SideOptions[] sideOptions = {SideOptions.BLANK, SideOptions.POWER_OUTPUT};
    public Map<Direction, SideOptions> selectedOptions = BlockOptionUtils.getDefaultSideOptions();

    public BasicSolarPanelBlockEntity() {
        super(GalacticraftBlockEntities.BASIC_SOLAR_PANEL_TYPE);
        //automatically mark dirty whenever the energy attribute is changed
        selectedOptions.put(Direction.SOUTH, SideOptions.POWER_OUTPUT);
    }

    @Override
    protected int getInvSize() {
        return 1;
    }

    @Override
    public void tick() {
        long time = world.getTimeOfDay();
        while (true) {
            if (time <= -1) {
                time += 24000;
                break;
            }
            time -= 24000;
        }

        if ((time > 250 && time < 12000)) {
            if (getEnergy().getCurrentEnergy() <= getEnergy().getMaxEnergy()) {
                status = BasicSolarPanelStatus.COLLECTING;
            } else {
                getEnergy().setCurrentEnergy(getEnergy().getMaxEnergy());
                status = BasicSolarPanelStatus.FULL;
            }
        } else if (world.isRaining() || world.isThundering()) {
            status = BasicSolarPanelStatus.RAINING;
        }

        if (time <= 250 || time >= 12000) {
            status = BasicSolarPanelStatus.NIGHT;
        }

        if (status == BasicSolarPanelStatus.COLLECTING) {
            if (time > 6000) {
                getEnergy().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) ((6000D - ((double) time - 6000D)) / 133.3333333333D), ActionType.PERFORM);
            } else {
                getEnergy().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, (int) (((double) time / 133.3333333333D)), ActionType.PERFORM);
            }
        }
        if (world.isClient) return;

        ItemStack battery = getInventory().getInvStack(0);
        if (GalacticraftEnergy.isEnergyItem(battery) && this.getEnergy().getCurrentEnergy() > 0) {
            if (GalacticraftEnergy.getBatteryEnergy(battery) < GalacticraftEnergy.getMaxBatteryEnergy(battery)) {
                getEnergy().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
                GalacticraftEnergy.incrementEnergy(battery, 1);
            }
        }

        for (Direction direction : Direction.values()) {
            if (selectedOptions.get(direction).equals(SideOptions.POWER_OUTPUT)) {
                EnergyAttribute energyAttribute = getNeighborAttribute(EnergyAttribute.ENERGY_ATTRIBUTE, direction);
                if (energyAttribute.canInsertEnergy()) {
                    this.getEnergy().setCurrentEnergy(energyAttribute.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM));
                }
            }
        }

    }

    public <T> T getNeighborAttribute(DefaultedAttribute<T> attr, Direction dir) {
        return attr.getFirst(getWorld(), getPos().offset(dir), SearchOptions.inDirection(dir));
    }

}