package io.github.teamgalacticraft.galacticraft.container.slot;

import io.github.teamgalacticraft.galacticraft.energy.GalacticraftEnergy;
import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
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
