/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

import micdoodle8.mods.galacticraft.core.tile.TileEntityCrafting;

public class SlotCraftingMemory extends SlotCrafting
{

    public TileEntityCrafting tileEntity;

    public SlotCraftingMemory(EntityPlayer player, InventoryCrafting craftingInventory, IInventory p_i45790_3_, int slotIndex, int xPosition, int yPosition, TileEntityCrafting tile)
    {
        super(player, craftingInventory, p_i45790_3_, slotIndex, xPosition, yPosition);
        this.tileEntity = tile;
    }

    @Override
    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
    {
        if (!stack.isEmpty())
            this.tileEntity.updateMemory(stack);
        return super.onTake(thePlayer, stack);
    }
}
