/*
 * Copyright (c) 2020 HRZN LTD
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

package com.hrznstudio.galacticraft.component;

import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.compat.vanilla.InventoryWrapper;
import io.github.cottonmc.component.compat.vanilla.SidedInventoryWrapper;
import io.github.cottonmc.component.item.InventoryComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubInventoryComponent implements InventoryComponent {
    private final InventoryComponent component;
    private final int[] slots;

    public SubInventoryComponent(InventoryComponent component, int[] slots) {
        this.component = component;
        this.slots = slots;
    }

    @Override
    public int getSize() {
        return slots.length;
    }

    @Override
    public boolean isEmpty() {
        for (int i : slots) {
            if (!component.getStack(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public List<ItemStack> getStacks() {
        List<ItemStack> stacks = new ArrayList<>(slots.length);
        for (int i : slots) {
            stacks.add(component.getStack(i));
        }
        return stacks;
    }

    @Override
    public DefaultedList<ItemStack> getMutableStacks() {
        DefaultedList<ItemStack> stacks = DefaultedList.ofSize(slots.length, ItemStack.EMPTY);
        DefaultedList<ItemStack> stacks1 = component.getMutableStacks();
        for (int i = 0; i < slots.length; i++) {
            stacks.set(i, stacks1.get(slots[i]));
        }
        return stacks;
    }

    @Override
    public ItemStack getStack(int slot) {
        return component.getStack(this.slots[slot]);
    }

    @Override
    public boolean canInsert(int slot) {
        return component.canInsert(this.slots[slot]);
    }

    @Override
    public boolean canExtract(int slot) {
        return component.canExtract(this.slots[slot]);
    }

    @Override
    public ItemStack takeStack(int slot, int amount, ActionType action) {
        return component.takeStack(this.slots[slot], amount, action);
    }

    @Override
    public ItemStack removeStack(int slot, ActionType action) {
        return component.removeStack(this.slots[slot], action);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot == 0) component.setStack(this.slots[slot], stack);
    }

    @Override
    public ItemStack insertStack(int slot, ItemStack stack, ActionType action) {
        return component.insertStack(this.slots[slot], stack, action);
    }

    @Override
    public ItemStack insertStack(ItemStack stack, ActionType action) {
        for (int i : slots) {
            stack = component.insertStack(i, stack, action);
            if (stack.isEmpty()) break;
        }
        return stack;
    }

    @Override
    public void clear() {
        for (int i : slots) {
            component.setStack(i, ItemStack.EMPTY);
        }
    }

    @Override
    public int getMaxStackSize(int slot) {
        return component.getMaxStackSize(this.slots[slot]);
    }

    @Override
    public boolean isAcceptableStack(int slot, ItemStack stack) {
        return component.isAcceptableStack(this.slots[slot], stack);
    }

    @Override
    public Inventory asInventory() {
        return (InventoryWrapper) () -> SubInventoryComponent.this;
    }

    @Nullable
    @Override
    public SidedInventory asLocalInventory(WorldAccess world, BlockPos pos) {
        return (SidedInventoryWrapper) dir -> SubInventoryComponent.this;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void listen(@NotNull Runnable listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Runnable> getListeners() {
        return Collections.emptyList();
    }
}
