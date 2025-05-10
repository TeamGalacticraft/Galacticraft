/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import dev.galacticraft.mod.content.GCAccessorySlots;
import dev.galacticraft.mod.screen.slot.AccessorySlot;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class GCPlayerInventoryMenu extends AbstractContainerMenu {
    public static final int[] COLUMNS = {8, 80, 98};

    public final Container inventory;

    public final Player player;

    public GCPlayerInventoryMenu(int syncId, Inventory playerInventory, Player player) {
        super(GCMenuTypes.PLAYER_INV_GC, syncId);

        this.player = player;
        this.inventory = player.galacticraft$getGearInv();

        // Galacticraft inv
        for (int i = 0; i < 12; ++i) {
            this.addSlot(new AccessorySlot(inventory, player, i, COLUMNS[i / 4], 8 + (i % 4) * 18, GCAccessorySlots.SLOT_TAGS.get(i), GCAccessorySlots.SLOT_SPRITES.get(i)));
        }

        // Player main inv
        for (int slotY = 0; slotY < 3; ++slotY) {
            for (int slotX = 0; slotX < 9; ++slotX) {
                this.addSlot(new Slot(playerInventory, slotX + (slotY + 1) * 9, 8 + slotX * 18, 84 + slotY * 18));
            }
        }

        // Player hotbar
        for (int slotY = 0; slotY < 9; ++slotY) {
            this.addSlot(new Slot(playerInventory, slotY, 8 + slotY * 18, 142));
        }
    }

    public GCPlayerInventoryMenu(int syncId, Inventory inv) {
        this(syncId, inv, inv.player);
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getUUID().equals(this.player.getUUID());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotFrom = this.slots.get(index);
        if (slotFrom.hasItem()) {
            ItemStack stackFrom = slotFrom.getItem();
            stack = stackFrom.copy();

            // Index of Indexes :)
            // 0-3 (4): GC, thermal armor slots;
            // 4-5 (2): GC, oxygen tank slots;
            // 6-11 (6): GC, accessory slots;
            // 12-38 (27): MC, non-hotbar inventory slots;
            // 39-47 (9): MC, hotbar slots.
            if (index < 12) {
                if (!this.moveItemStackTo(stackFrom, 39, 48, false) &&
                    !this.moveItemStackTo(stackFrom, 12, 39, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 39) {
                if (!this.moveItemStackTo(stackFrom, 0, 12, false) &&
                    !this.moveItemStackTo(stackFrom, 39, 48, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 49) {
                if (!this.moveItemStackTo(stackFrom, 0, 12, false) &&
                    !this.moveItemStackTo(stackFrom, 12, 39, false)) {
                    return ItemStack.EMPTY;
                }
            }

            slotFrom.onQuickCraft(stackFrom, stack);

            if (stackFrom.isEmpty()) {
                slotFrom.set(ItemStack.EMPTY);
            } else {
                slotFrom.setChanged();
            }

            if (stackFrom.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slotFrom.onTake(player, stackFrom);
            if (index == 0) {
                player.drop(stackFrom, false);
            }
        }

        return stack;
    }
}
