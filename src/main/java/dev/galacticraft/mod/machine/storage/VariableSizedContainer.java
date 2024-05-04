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

package dev.galacticraft.mod.machine.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VariableSizedContainer implements Container {
    private int targetSize;
    private final ArrayList<ItemStack> stacks;
    private final List<Listener> listeners = new ArrayList<>();

    public VariableSizedContainer(int targetSize) {
        this.targetSize = targetSize;
        this.stacks = new ArrayList<>(this.targetSize);

        for (int i = 0; i < this.targetSize; i++) {
            this.stacks.add(ItemStack.EMPTY);
        }
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public int getTargetSize() {
        return targetSize;
    }

    public void resize(int size) {
        if (this.targetSize != size) {
            this.targetSize = size;
            this.stacks.ensureCapacity(size);
            if (this.stacks.size() > this.targetSize) {
//                for (int i = this.stacks.size() - 1; i >= this.max; i--) {
//                    if (this.stacks.get(i).isEmpty()) {
//                        this.stacks.remove(i);
//                    } else {
//                        break;
//                    }
//                }
            } else {
                while (this.stacks.size() < this.targetSize) {
                    this.stacks.add(ItemStack.EMPTY);
                }
            }
        }

        for (Listener listener : this.listeners) {
            listener.onSizeChanged();
        }
    }

    @Override
    public int getContainerSize() {
        return this.stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.stacks) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int i) {
        return this.stacks.get(i);
    }

    @Override
    public @NotNull ItemStack removeItem(int i, int j) {
        ItemStack itemStack = ContainerHelper.removeItem(this.stacks, i, j);
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }

        return itemStack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int i) {
        ItemStack itemStack = this.stacks.get(i);
        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.stacks.set(i, ItemStack.EMPTY);
            return itemStack;
        }
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        this.stacks.set(i, itemStack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setChanged() {
        if (this.stacks.size() > this.targetSize) {
            for (int i1 = this.stacks.size() - 1; i1 >= this.targetSize; i1--) {
                if (this.stacks.get(i1).isEmpty()) {
                    this.stacks.remove(i1);
                } else {
                    break;
                }
            }
        }
        for (Listener listener : this.listeners) {
            listener.onItemChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
        return i < this.targetSize && Container.super.canPlaceItem(i, itemStack);
    }

    @Override
    public boolean canTakeItem(Container container, int i, ItemStack itemStack) {
        return i > this.targetSize || Container.super.canTakeItem(container, i, itemStack);
    }

    @Override
    public void clearContent() {
        Collections.fill(this.stacks, ItemStack.EMPTY);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("TargetSize", this.targetSize);
        ListTag list = new ListTag();
        for (ItemStack stack : this.stacks) {
            CompoundTag item =new CompoundTag();
            stack.save(item);
            list.add(item);
        }
        tag.put("Items", list);
        return tag;
    }

    public void readTag(CompoundTag tag) {
        this.targetSize = tag.getInt("TargetSize");
        ListTag items = tag.getList("Items", Tag.TAG_COMPOUND);
        this.stacks.clear();
        this.stacks.ensureCapacity(items.size());
        for (int i = 0; i < items.size(); i++) {
            this.stacks.add(ItemStack.of(items.getCompound(i)));
        }
        while (this.stacks.size() < this.targetSize-1) {
            this.stacks.add(ItemStack.EMPTY);
        }
    }
    public interface Listener {
        void onSizeChanged();
        void onItemChanged();
    }
}
