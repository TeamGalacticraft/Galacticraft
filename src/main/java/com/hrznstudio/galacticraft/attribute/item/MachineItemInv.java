package com.hrznstudio.galacticraft.attribute.item;

import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import com.hrznstudio.galacticraft.api.block.entity.MachineBlockEntity;
import com.hrznstudio.galacticraft.attribute.Automatable;
import com.hrznstudio.galacticraft.screen.MachineScreenHandler;
import com.hrznstudio.galacticraft.screen.slot.AutoFilteredSlot;
import com.hrznstudio.galacticraft.screen.slot.SlotType;
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

    public void addSlot(SlotType type, ItemFilter filter, int x, int y) {
        this.positions.add(this.getSlotCount(), new DefaultSlotFunction(x, y));
        this.slotTypes.add(this.getSlotCount(), type);
        this.filters.add(this.getSlotCount(), filter);
    }

    public void addSlot(SlotType type, ItemFilter filter) {
        this.positions.add(this.getSlotCount(), null);
        this.slotTypes.add(this.getSlotCount(), type);
        this.filters.add(this.getSlotCount(), filter);
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

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public Slot create(MachineBlockEntity machineBlockEntity, int index, PlayerEntity player) {
            return new AutoFilteredSlot(machineBlockEntity, index, this.x, this.y);
        }
    }
}
