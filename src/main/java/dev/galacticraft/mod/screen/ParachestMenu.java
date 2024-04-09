/*
 * Copyright (c) 2019-2024 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.screen;

import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.block.special.ParaChestBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ParachestMenu extends AbstractContainerMenu {
    private final Container container;

    public ParachestMenu(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(syncId, playerInventory, getContainer(playerInventory.player.level(), buf));
    }

    public static Container getContainer(Level level, FriendlyByteBuf buf) {
        boolean isBe = buf.readBoolean();
        if (isBe)
            return GCBlockEntityTypes.PARACHEST.getBlockEntity(level, buf.readBlockPos());
        else
            return new SimpleContainer(buf.readVarInt());
    }

    public ParachestMenu(int syncId, Inventory playerInventory, Container container) {
        super(GCMenuTypes.PARACHEST, syncId);
        this.container = container;
        int numRows = (container.getContainerSize() - 3) / 9;
        this.container.startOpen(playerInventory.player);
        int i = (numRows - 4) * 18 + 19;
        // Player main inv
        for (int slotY = 0; slotY < 3; ++slotY) {
            for (int slotX = 0; slotX < 9; ++slotX) {
                this.addSlot(new Slot(playerInventory, slotX + (slotY + 1) * 9, 8 + slotX * 18, (numRows == 0 ? 116 : 118) + slotY * 18 + i));
            }
        }

        // Player hotbar
        for (int slotY = 0; slotY < 9; ++slotY) {
            this.addSlot(new Slot(playerInventory, slotY, 8 + slotY * 18, (numRows == 0 ? 174 : 176) + i));
        }

        for (int colum = 0; colum < numRows; ++colum) {
            for (int row = 0; row < 9; ++row) {
                this.addSlot(new Slot(this.container, row + colum * 9, 8 + row * 18, 18 + colum * 18));
            }
        }

        this.addSlot(new Slot(this.container, this.container.getContainerSize() - 3, 125, (numRows == 0 ? 24 : 26) + numRows * 18));
        this.addSlot(new Slot(this.container, this.container.getContainerSize() - 2, 125 + 18, (numRows == 0 ? 24 : 26) + numRows * 18));
        this.addSlot(new Slot(this.container, this.container.getContainerSize() - 1, 75, (numRows == 0 ? 24 : 26) + numRows * 18));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        final int size = this.slots.size();

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (slotIndex < this.container.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, size - 36, size, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    public Container getContainer() {
        return this.container;
    }
}
