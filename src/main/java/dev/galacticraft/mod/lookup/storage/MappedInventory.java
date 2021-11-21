/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.lookup.storage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Set;
import java.util.stream.IntStream;

public record MappedInventory(int[] slots, Inventory delegate) implements Inventory {

    public static MappedInventory mappedRange(Inventory inventory, int start, int len) {
        return new MappedInventory(IntStream.range(start, len).toArray(), inventory);
    }

    @Override
    public int size() {
        return this.slots.length;
    }

    @Override
    public boolean isEmpty() {
        for (int slot : slots) {
            if (!delegate.getStack(slot).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.delegate.getStack(this.slots[slot]);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return this.delegate.removeStack(this.slots[slot], amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.delegate.removeStack(this.slots[slot]);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.delegate.setStack(this.slots[slot], stack);
    }

    @Override
    public int getMaxCountPerStack() {
        return this.delegate.getMaxCountPerStack();
    }

    @Override
    public void markDirty() {
        this.delegate.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.delegate.canPlayerUse(player);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        this.delegate.onOpen(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        this.delegate.onClose(player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return this.delegate.isValid(this.slots[slot], stack);
    }

    @Override
    public int count(Item item) {
        return this.delegate.count(item);
    }

    @Override
    public boolean containsAny(Set<Item> items) {
        return this.delegate.containsAny(items);
    }

    @Override
    public void clear() {
        for (int slot : this.slots) {
            this.delegate.setStack(slot, ItemStack.EMPTY);
        }
    }
}
