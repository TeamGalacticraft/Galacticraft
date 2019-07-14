package com.hrznstudio.galacticraft.blocks.machines.energystoragemodule;

import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.util.Tickable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class EnergyStorageModuleBlockEntity extends ConfigurableElectricMachineBlockEntity implements Tickable {
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
        if (world.isClient || !active()) {
            return;
        }
        attemptChargeFromStack(DRAIN_BATTERY_SLOT);
        attemptDrainPowerToStack(CHARGE_BATTERY_SLOT);
    }
}
