package io.github.teamgalacticraft.galacticraft.container.slot;

import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class OxygenTankSlot extends Slot {
    public OxygenTankSlot(Inventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    @Override
    public int getMaxStackAmount() {
        return 1;
    }

    @Override
    public boolean canInsert(ItemStack itemStack) {
        return GalacticraftEnergy.isOxygenItem(itemStack);
    }
}