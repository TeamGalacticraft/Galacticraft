package com.hrznstudio.galacticraft.blocks.machines.energystoragemodule;

import com.hrznstudio.galacticraft.blocks.machines.MachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.util.Tickable;

import alexiil.mc.lib.attributes.item.filter.ItemFilter;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class EnergyStorageModuleBlockEntity extends MachineBlockEntity implements Tickable {
    public static int MAX_ENERGY = 60000;
    public static int CHARGE_BATTERY_SLOT = 0;
    public static int DRAIN_BATTERY_SLOT = 1;

    public EnergyStorageModuleBlockEntity() {
        super(GalacticraftBlockEntities.ENERGY_STORAGE_MODULE_TYPE);
    }

    @Override
    public int getMaxEnergy() {
        return MAX_ENERGY;
    }

    @Override
    protected int getInvSize() {
        return 2;
    }

    @Override
    protected ItemFilter getFilterForSlot(int slot) {
        return GalacticraftEnergy.ENERGY_HOLDER_ITEM_FILTER;
    }

    @Override
    protected int getBatteryTransferRate() {
        return 5;
    }

    @Override
    public void tick() {
        if (world.isClient) {
            return;
        }
        attemptChargeFromStack(DRAIN_BATTERY_SLOT);
        attemptDrainPowerToStack(CHARGE_BATTERY_SLOT);
    }
}
