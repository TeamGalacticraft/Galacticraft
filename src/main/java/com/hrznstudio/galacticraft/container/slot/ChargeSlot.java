package com.hrznstudio.galacticraft.container.slot;

import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ChargeSlot extends Slot {
    public ChargeSlot(Inventory inventory, int slotId, int x, int y) {
        super(inventory, slotId, x, y);
    }

    @Override
    public int getMaxStackAmount() {
        return 1;
    }

    @Override
    public boolean canInsert(ItemStack itemStack) {
        return GalacticraftEnergy.isEnergyItem(itemStack);
    }
}
