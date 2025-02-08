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

package dev.galacticraft.impl.internal.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public record MappedInventory(Container inventory, int... slots) implements Container {
    // apparently mixin does not like IASTORE opcodes, so this is the alternative.
    public static MappedInventory create(Container inventory, int i1) {
        return new MappedInventory(inventory, i1);
    }

    public static MappedInventory create(Container inventory, int i1, int i2) {
        return new MappedInventory(inventory, i1, i2);
    }

    public static MappedInventory create(Container inventory, int i1, int i2, int i3) {
        return new MappedInventory(inventory, i1, i2, i3);
    }

    public static MappedInventory create(Container inventory, int i1, int i2, int i3, int i4) {
        return new MappedInventory(inventory, i1, i2, i3, i4);
    }

    public static MappedInventory create(Container inventory, int i1, int i2, int i3, int i4, int i5) {
        return new MappedInventory(inventory, i1, i2, i3, i4, i5);
    }

    public static MappedInventory create(Container inventory, int i1, int i2, int i3, int i4, int i5, int i6) {
        return new MappedInventory(inventory, i1, i2, i3, i4, i5, i6);
    }

    @Override
    public int getContainerSize() {
        return this.slots.length;
    }

    @Override
    public boolean isEmpty() {
        for (int slot : slots) {
            if (!inventory.getItem(slot).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.inventory.getItem(this.slots[slot]);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return this.inventory.removeItem(this.slots[slot], amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return this.inventory.removeItemNoUpdate(this.slots[slot]);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.inventory.setItem(this.slots[slot], stack);
    }

    @Override
    public int getMaxStackSize() {
        return this.inventory.getMaxStackSize();
    }

    @Override
    public void setChanged() {
        this.inventory.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    @Override
    public void startOpen(Player player) {
        this.inventory.startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        this.inventory.stopOpen(player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return this.inventory.canPlaceItem(this.slots[slot], stack);
    }

    @Override
    public int countItem(Item item) {
        int count = 0;
        for (int slot : this.slots) {
            ItemStack stack = this.inventory.getItem(slot);
            if (stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    @Override
    public boolean hasAnyOf(Set<Item> items) {
        for (int slot : this.slots) {
            ItemStack stack = this.inventory.getItem(slot);
            if (items.contains(stack.getItem())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clearContent() {
        for (int slot : this.slots) {
            this.inventory.setItem(slot, ItemStack.EMPTY);
        }
    }
}
