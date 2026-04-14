/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */
package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import micdoodle8.mods.galacticraft.core.util.ItemUtil;

public class SlotCoalGenerator extends Slot
{
    public SlotCoalGenerator(IInventory inventoryIn, int index, int xPosition, int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
    }

    //@noformat
    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return (
            ItemUtil.hasOreDictSuffix(stack, "coal") || 
            ItemUtil.hasOreDictSuffix(stack, "charcoal") || 
            stack.getItem() == Items.COAL
        ) && stack.getItem() != Item.getItemFromBlock(Blocks.COAL_ORE);
    }
}
