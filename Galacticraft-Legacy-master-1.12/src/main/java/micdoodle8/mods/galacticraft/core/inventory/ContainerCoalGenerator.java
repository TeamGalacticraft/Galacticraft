/*
 * Copyright (c) 2023 Team Galacticraft
 *
 * Licensed under the MIT license.
 * See LICENSE file in the project root for details.
 */

package micdoodle8.mods.galacticraft.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import micdoodle8.mods.galacticraft.core.tile.TileEntityCoalGenerator;

public class ContainerCoalGenerator extends Container
{

    private TileEntityCoalGenerator tileEntity;

    public ContainerCoalGenerator(InventoryPlayer inventoryPlayer, TileEntityCoalGenerator tileEntity)
    {
        this.tileEntity = tileEntity;
        this.addSlotToContainer(new SlotCoalGenerator(tileEntity, 0, 33, 34));
        int i;

        for (i = 0; i < 3; ++i)
        {
            for (int k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new Slot(inventoryPlayer, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer entityplayer)
    {
        super.onContainerClosed(entityplayer);
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer)
    {
        return this.tileEntity.isUsableByPlayer(entityPlayer);
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift
     * clicking.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int index)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack var4 = slot.getStack();
            stack = var4.copy();

            if (index != 0)
            {
                if (var4.getItem() == Items.COAL)
                {
                    if (!this.mergeItemStack(var4, 0, 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 28)
                {
                    if (!this.mergeItemStack(var4, 1, 28, false))
                    {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.mergeItemStack(var4, 28, 37, false))
                {
                    return ItemStack.EMPTY;
                }

            } else if (!this.mergeItemStack(var4, 1, 37, false))
            {
                return ItemStack.EMPTY;
            }

            if (var4.getCount() == 0)
            {
                slot.putStack(ItemStack.EMPTY);
            } else
            {
                slot.onSlotChanged();
            }

            if (var4.getCount() == stack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(entityPlayer, var4);
        }

        return stack;
    }
}
