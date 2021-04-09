/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.attribute.item;

import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import com.hrznstudio.galacticraft.accessor.DefaultedListAccessor;
import com.hrznstudio.galacticraft.api.block.entity.MachineBlockEntity;
import com.hrznstudio.galacticraft.attribute.Automatable;
import com.hrznstudio.galacticraft.screen.MachineScreenHandler;
import com.hrznstudio.galacticraft.screen.slot.AutoFilteredSlot;
import com.hrznstudio.galacticraft.screen.slot.OutputSlot;
import com.hrznstudio.galacticraft.screen.slot.SlotType;
import com.hrznstudio.galacticraft.util.collection.ResizableDefaultedList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;

public class MachineItemInv extends FullFixedItemInv implements Automatable {
    private final List<SlotType> slotTypes = new ArrayList<>();
    private final List<ItemFilter> filters = new ArrayList<>();
    private final List<SlotFunction> positions = new ArrayList<>();

    public MachineItemInv() {
        super(0);
        ((DefaultedListAccessor<ItemStack>)this).setDefaultedList_gcr(new ResizableDefaultedList<>(new ArrayList<>(), ItemStack.EMPTY));
    }

    @Override
    public ItemFilter getFilterForSlot(int slot) {
        if (slot < 0 || slot >= this.getSlotCount()) return ConstantItemFilter.NOTHING;
        return this.filters.get(slot);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack item) {
        return this.getFilterForSlot(slot).matches(item);
    }

    public void addSlot(int index, SlotType type, ItemFilter filter, int x, int y) {
        assert this.getSlotCount() == index;
        this.positions.add(index, new DefaultSlotFunction(x, y));
        this.slotTypes.add(index, type);
        this.filters.add(index, filter);
        this.slots.add(index, ItemStack.EMPTY);
    }

    public void addSlot(int index, SlotType type, ItemFilter filter, SlotFunction function) {
        assert this.getSlotCount() == index;
        this.positions.add(index, function);
        this.slotTypes.add(index, type);
        this.filters.add(index, filter);
        this.slots.add(index, ItemStack.EMPTY);
    }

    public void createSlots(MachineScreenHandler<?> screenHandler) {
        SlotFunction function;
        for (int i = 0; i < getSlotCount(); i++) {
            function = positions.get(i);
            if (function != null) {
                screenHandler.addSlot(function.create(screenHandler.machine, i, screenHandler.player));
            }
        }
    }

    @Override
    public List<SlotType> getTypes() {
        return this.slotTypes;
    }

    @FunctionalInterface
    public interface SlotFunction {
        Slot create(MachineBlockEntity machineBlockEntity, int index, PlayerEntity player);
    }

    private static class DefaultSlotFunction implements SlotFunction {
        private final int x;
        private final int y;

        private DefaultSlotFunction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public Slot create(MachineBlockEntity machineBlockEntity, int index, PlayerEntity player) {
            return new AutoFilteredSlot(machineBlockEntity, index, this.x, this.y);
        }
    }

    public static class OutputSlotFunction implements SlotFunction {
        private final int x;
        private final int y;

        public OutputSlotFunction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public Slot create(MachineBlockEntity machineBlockEntity, int index, PlayerEntity player) {
            return new OutputSlot(machineBlockEntity.getWrappedInventory(), index, this.x, this.y);
        }
    }
}
