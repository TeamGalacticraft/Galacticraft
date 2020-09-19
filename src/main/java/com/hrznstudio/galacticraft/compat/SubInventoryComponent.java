package com.hrznstudio.galacticraft.compat;

import com.hrznstudio.galacticraft.misc.ForwardingDefaultList;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.compat.vanilla.InventoryWrapper;
import io.github.cottonmc.component.item.InventoryComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SubInventoryComponent implements InventoryComponent {
    private final int[] slots;
    private final InventoryComponent parent;

    public SubInventoryComponent(InventoryComponent parent, int[] slots) {
        this.parent = parent;
        this.slots = slots;
    }

    @Override
    public int getSize() {
        return slots.length;
    }

    @Override
    public boolean isEmpty() {
        for (int i : slots) {
            if (!parent.getStack(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public List<ItemStack> getStacks() {
        List<ItemStack> stackList = new ArrayList<>(getSize());
        for (int i : slots) {
            stackList.add(parent.getStack(i));
        }
        return stackList;
    }

    @Override
    public DefaultedList<ItemStack> getMutableStacks() {
        return new ForwardingDefaultList<>(parent.getMutableStacks(), ItemStack.EMPTY, slots);
    }

    @Override
    public ItemStack getStack(int slot) {
        return parent.getStack(slots[slot]);
    }

    @Override
    public boolean canInsert(int slot) {
        return parent.canInsert(slots[slot]);
    }

    @Override
    public boolean canExtract(int slot) {
        return parent.canExtract(slots[slot]);
    }

    @Override
    public ItemStack takeStack(int slot, int amount, ActionType action) {
        return parent.takeStack(slots[slot], amount, action);
    }

    @Override
    public ItemStack removeStack(int slot, ActionType action) {
        return parent.removeStack(slots[slot], action);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        parent.setStack(slots[slot], stack);
    }

    @Override
    public ItemStack insertStack(int slot, ItemStack stack, ActionType action) {
        return parent.insertStack(slots[slot], stack, action);
    }

    @Override
    public ItemStack insertStack(ItemStack stack, ActionType action) {
        for (int i : slots) {
            stack = parent.insertStack(i, stack, action);
            if (stack.isEmpty()) return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public void clear() {
        for (int i : slots) {
            parent.setStack(i, ItemStack.EMPTY);
        }
    }

    @Override
    public int getMaxStackSize(int slot) {
        return parent.getMaxStackSize(slots[slot]);
    }

    @Override
    public boolean isAcceptableStack(int slot, ItemStack stack) {
        return parent.isAcceptableStack(slots[slot], stack);
    }

    @Override
    public int amountOf(Item item) {
        int amount = 0;
        for (ItemStack stack : getStacks()) {
            if (item.equals(stack.getItem())) {
                amount += stack.getCount();
            }
        }
        return amount;
    }

    @Override
    public int amountOf(Set<Item> items) {
        int amount = 0;
        for (ItemStack stack : getStacks()) {
            if (items.contains(stack.getItem())) {
                amount += stack.getCount();
            }
        }
        return amount;
    }

    @Override
    public boolean contains(Item item) {
        for (ItemStack stack : getStacks()) {
            if (item.equals(stack.getItem())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(Set<Item> items) {
        for (ItemStack stack : getStacks()) {
            if (items.contains(stack.getItem()) && stack.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Inventory asInventory() {
        return InventoryWrapper.of(this);
    }

    @Override
    public SidedInventory asLocalInventory(WorldAccess world, BlockPos pos) {
        return null;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.parent.fromTag(tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        return this.parent.toTag(tag);
    }

    @Override
    public List<Runnable> getListeners() {
        return parent.getListeners();
    }
}
