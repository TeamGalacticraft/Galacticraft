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

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.GroupedItemInv;
import alexiil.mc.lib.attributes.item.ItemTransferable;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.GroupedItemInvFixedWrapper;
import alexiil.mc.lib.attributes.item.impl.ItemInvModificationTracker;
import alexiil.mc.lib.attributes.misc.Saveable;
import com.hrznstudio.galacticraft.api.block.entity.MachineBlockEntity;
import com.hrznstudio.galacticraft.attribute.Automatable;
import com.hrznstudio.galacticraft.screen.MachineScreenHandler;
import com.hrznstudio.galacticraft.screen.slot.AutoFilteredSlot;
import com.hrznstudio.galacticraft.screen.slot.OutputSlot;
import com.hrznstudio.galacticraft.screen.slot.SlotType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MachineItemInv implements FixedItemInv.CopyingFixedItemInv, ItemTransferable, Saveable, Automatable {
    private static final ItemFilter EMPTY_ONLY = ItemStack::isEmpty;
    private final List<SlotType> slotTypes = new ArrayList<>();
    private final List<ItemFilter> filters = new ArrayList<>();
    private final List<SlotFunction> positions = new ArrayList<>();
    private final List<ItemStack> items = new ArrayList<>();

    private final GroupedItemInv groupedInv = new GroupedItemInvFixedWrapper(this);
    private boolean modifiable = true;

    @Override
    public ItemFilter getFilterForSlot(int slot) {
        if (slot < 0 || slot >= this.getSlotCount()) return EMPTY_ONLY;
        return this.filters.get(slot);
    }

    @Override
    public int getSlotCount() {
        return this.getItems().size();
    }

    private @NotNull List<ItemStack> getItems() {
        this.modifiable = false;
        return this.items;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack item) {
        return this.getFilterForSlot(slot).matches(item);
    }

    @Override
    public boolean setInvStack(int slot, ItemStack to, Simulation simulation) {
        if (this.getFilterForSlot(slot).matches(to) && to.getCount() <= this.getMaxAmount(slot, to)) {
            if (simulation.isAction()) this.getItems().set(slot, to);
            return true;
        }
        return false;
    }

    @Override
    public GroupedItemInv getGroupedInv() {
        return this.groupedInv;
    }

    public void addSlot(int index, SlotType type, ItemFilter filter, int x, int y) {
        assert modifiable;
        assert this.getSlotCount() == index;
        this.positions.add(index, new DefaultSlotFunction(x, y));
        this.slotTypes.add(index, type);
        this.filters.add(index, filter.or(ItemStack::isEmpty));
        this.getItems().add(index, ItemStack.EMPTY);
        this.modifiable = true;
    }

    public void addSlot(int index, SlotType type, ItemFilter filter, SlotFunction function) {
        assert modifiable;
        assert this.getSlotCount() == index;
        this.positions.add(index, function);
        this.slotTypes.add(index, type);
        this.filters.add(index, filter.or(ItemStack::isEmpty));
        this.getItems().add(index, ItemStack.EMPTY);
        this.modifiable = true;
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

    @Override
    public ItemStack getUnmodifiableInvStack(int slot) {
        ItemStack stack = this.getItems().get(slot);
        ItemInvModificationTracker.trackNeverChanging(stack);
        return stack;
    }

    @Override
    public ItemStack attemptExtraction(ItemFilter itemFilter, int i, Simulation simulation) {
        return this.getGroupedInv().attemptExtraction(itemFilter, i, simulation);
    }

    @Override
    public ItemStack attemptInsertion(ItemStack itemStack, Simulation simulation) {
        return this.getGroupedInv().attemptInsertion(itemStack, simulation);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        ListTag slotsTag = new ListTag();
        for (ItemStack stack : this.getItems()) {
            ItemInvModificationTracker.trackNeverChanging(stack);
            if (stack.isEmpty()) {
                slotsTag.add(new CompoundTag());
            } else {
                slotsTag.add(stack.toTag(new CompoundTag()));
            }
        }
        tag.put("slots", slotsTag);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        ListTag slotsTag = tag.getList("slots", new CompoundTag().getType());
        for (int i = 0; i < slotsTag.size() && i < this.getItems().size(); i++) {
            this.getItems().set(i, ItemStack.fromTag(slotsTag.getCompound(i)));
        }
        for (int i = slotsTag.size(); i < this.getItems().size(); i++) {
            this.getItems().set(i, ItemStack.EMPTY);
        }
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
