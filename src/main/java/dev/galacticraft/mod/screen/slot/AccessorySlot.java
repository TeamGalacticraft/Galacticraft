package dev.galacticraft.mod.screen.slot;

import dev.galacticraft.api.item.Accessory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class AccessorySlot extends Slot {
    public AccessorySlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.getItem() instanceof Accessory;
    }
}
