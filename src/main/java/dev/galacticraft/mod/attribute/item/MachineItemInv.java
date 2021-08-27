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

package dev.galacticraft.mod.attribute.item;

import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.screen.MachineScreenHandler;
import dev.galacticraft.mod.attribute.Automatable;
import dev.galacticraft.mod.screen.slot.AutoFilteredSlot;
import dev.galacticraft.mod.screen.slot.OutputSlot;
import dev.galacticraft.mod.screen.slot.SlotType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MachineItemInv extends FullFixedItemInv implements Automatable {
    private final SlotType[] slotTypes;
    private final ItemFilter[] filters;
    private final SlotFunction[] slotFunctions;

    public MachineItemInv(SlotType[] slotTypes, ItemFilter[] filters, SlotFunction[] slotFunctions) {
        super(filters.length);
        this.slotTypes = slotTypes;
        this.filters = filters;
        this.slotFunctions = slotFunctions;
    }

    @Override
    public ItemFilter getFilterForSlot(int slot) {
        if (slot < 0 || slot >= this.getSlotCount()) return ConstantItemFilter.NOTHING;
        return this.filters[slot];
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack item) {
        return this.getFilterForSlot(slot).matches(item);
    }

    public void createSlots(MachineScreenHandler<?> screenHandler) {
        SlotFunction function;
        for (int i = 0; i < getSlotCount(); i++) {
            function = slotFunctions[i];
            if (function != null) {
                screenHandler.addSlot(function.create(screenHandler.machine, i, screenHandler.player));
            }
        }
    }

    @Override
    public SlotType[] getTypes() {
        return this.slotTypes;
    }

    public static class Builder {
        private static final ItemFilter EMPTY_FILTER = ItemStack::isEmpty;
        private final List<SlotType> slotTypes = new ArrayList<>();
        private final List<ItemFilter> filters = new ArrayList<>();
        private final List<SlotFunction> slotFunctions = new ArrayList<>();

        private Builder() {}

        public static Builder create() {
            return new Builder();
        }

        public Builder addSlot(int index, SlotType type, ItemFilter filter, int x, int y) {
            this.slotFunctions.add(index, new DefaultSlotFunction(x, y));
            this.slotTypes.add(index, type);
            this.filters.add(index, filter.or(EMPTY_FILTER));
            return this;
        }

        public Builder addSlot(int index, SlotType type, ItemFilter filter, SlotFunction slotFunction) {
            this.slotFunctions.add(index, slotFunction);
            this.slotTypes.add(index, type);
            this.filters.add(index, filter.or(EMPTY_FILTER));
            return this;
        }

        public Builder addSlot(int index, SlotType type, int x, int y) {
            this.slotFunctions.add(index, new DefaultSlotFunction(x, y));
            this.slotTypes.add(index, type);
            this.filters.add(index, ConstantItemFilter.ANYTHING);
            return this;
        }

        public Builder addSlot(int index, SlotType type, SlotFunction slotFunction) {
            this.slotFunctions.add(index, slotFunction);
            this.slotTypes.add(index, type);
            this.filters.add(index, ConstantItemFilter.ANYTHING);
            return this;
        }

        public MachineItemInv build() {
            return new MachineItemInv(this.slotTypes.toArray(new SlotType[0]), this.filters.toArray(new ItemFilter[0]), this.slotFunctions.toArray(new SlotFunction[0]));
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "slotTypes=" + slotTypes +
                    ", filters=" + filters +
                    ", slotFunctions=" + slotFunctions +
                    '}';
        }
    }

    @FunctionalInterface
    public interface SlotFunction {
        Slot create(MachineBlockEntity machineBlockEntity, int index, PlayerEntity player);
    }

    private record DefaultSlotFunction(int x, int y) implements SlotFunction {
        @Override
        public Slot create(MachineBlockEntity machineBlockEntity, int index, PlayerEntity player) {
            return new AutoFilteredSlot(machineBlockEntity, index, this.x, this.y);
        }
    }

    public record OutputSlotFunction(int x, int y) implements SlotFunction {
        @Override
        public Slot create(MachineBlockEntity machineBlockEntity, int index, PlayerEntity player) {
            return new OutputSlot(machineBlockEntity.getWrappedInventory(), index, this.x, this.y);
        }
    }
}
