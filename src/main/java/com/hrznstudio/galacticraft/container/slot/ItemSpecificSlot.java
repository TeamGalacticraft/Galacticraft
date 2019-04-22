package com.hrznstudio.galacticraft.container.slot;

import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ItemSpecificSlot extends Slot {

    private List<Item> items;

    public ItemSpecificSlot(Inventory inventory, int slotId, int x, int y, Item... items) {
        super(inventory, slotId, x, y);
        this.items = Arrays.asList(items);
    }

    @Override
    public boolean canInsert(ItemStack itemStack) {
        return items.contains(itemStack.getItem());
    }
}
