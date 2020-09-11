//package com.hrznstudio.galacticraft.component;
//
//import io.github.cottonmc.component.api.ActionType;
//import io.github.cottonmc.component.compat.vanilla.InventoryWrapper;
//import io.github.cottonmc.component.item.impl.SimpleInventoryComponent;
//import it.unimi.dsi.fastutil.ints.IntArrayList;
//import it.unimi.dsi.fastutil.ints.IntList;
//import net.minecraft.inventory.Inventory;
//import net.minecraft.inventory.SidedInventory;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.util.collection.DefaultedList;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.world.WorldAccess;
//
//import java.util.*;
//import java.util.function.Function;
//
//public class InventoryWrapperComponent extends SimpleInventoryComponent {
//    private final SimpleInventoryComponent parent;
//    private final IntList allowedSlots;
//
//    public InventoryWrapperComponent(SimpleInventoryComponent component, int[] allowed) {
//        super(component.getSize());
//        this.parent = component;
//        this.allowedSlots = IntArrayList.wrap(allowed);
//        this.allowedSlots.sort(Integer::compareTo);
//    }
//
//    @Override
//    public int getSize() {
//        return parent.getSize();
//    }
//
//    @Override
//    public List<ItemStack> getStacks() {
//        List<ItemStack> list = new ArrayList<>();
//        for (int i = 0; i < parent.getSize(); i++) {
//            if (allowedSlots.contains(i)) {
//                list.add(parent.getStack(i));
//            }
//        }
//        return list;
//    }
//
//    @Override
//    public DefaultedList<ItemStack> getMutableStacks() {
//        throw new UnsupportedOperationException(); //todo
//    }
//
//    @Override
//    public ItemStack getStack(int slot) {
//        return parent.getStack(allowedSlots.getInt(slot));
//    }
//
//    @Override
//    public boolean canInsert(int slot) {
//        return parent.canInsert(allowedSlots.getInt(slot));
//    }
//
//    @Override
//    public boolean canExtract(int slot) {
//        return parent.canExtract(allowedSlots.getInt(slot));
//    }
//
//    @Override
//    public ItemStack takeStack(int slot, int amount, ActionType action) {
//        return parent.takeStack(allowedSlots.getInt(slot), amount, action);
//    }
//
//    @Override
//    public ItemStack removeStack(int slot, ActionType action) {
//        return parent.removeStack(allowedSlots.getInt(slot), action);
//    }
//
//    @Override
//    public void setStack(int slot, ItemStack stack) {
//        parent.setStack(allowedSlots.getInt(slot), stack);
//    }
//
//    @Override
//    public ItemStack insertStack(int slot, ItemStack stack, ActionType action) {
//        return parent.insertStack(allowedSlots.getInt(slot), stack, action);
//    }
//
//    @Override
//    public ItemStack insertStack(ItemStack stack, ActionType action) {
//        for (int i = 0; i < parent.getSize(); i++) {
//            if (allowedSlots.contains(i)) {
//                stack = insertStack(i, stack, action);
//            }
//            if (stack.isEmpty()) return stack;
//        }
//        return stack;
//    }
//
//    @Override
//    public List<Runnable> getListeners() {
//        return parent.getListeners();
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return getStacks().isEmpty();
//    }
//
//    @Override
//    public void clear() {
//        for (int i : allowedSlots) {
//            parent.setStack(i, ItemStack.EMPTY);
//        }
//    }
//
//    @Override
//    public int getMaxStackSize(int slot) {
//        return parent.getMaxStackSize(allowedSlots.getInt(slot));
//    }
//
//    @Override
//    public boolean isAcceptableStack(int slot, ItemStack stack) {
//        return parent.isAcceptableStack(allowedSlots.getInt(slot), stack);
//    }
//
//    @Override
//    public int amountOf(Item item) {
//        return this.amountOf(Collections.singleton(item));
//    }
//
//    @Override
//    public int amountOf(Set<Item> items) {
//        int amount = 0;
//
//        for (ItemStack stack : this.getStacks()) {
//            if (items.contains(stack.getItem())) {
//                amount += stack.getCount();
//            }
//        }
//
//        return amount;
//    }
//
//    @Override
//    public boolean contains(Item item) {
//        return this.contains(Collections.singleton(item));
//    }
//
//    @Override
//    public boolean contains(Set<Item> items) {
//        for (ItemStack stack : getStacks()) {
//            if (items.contains(stack.getItem()) && stack.getCount() > 0) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    @Override
//    public Inventory asInventory() {
//        return InventoryWrapper.of(this);
//    }
//
//    @Override
//    public SidedInventory asLocalInventory(WorldAccess world, BlockPos pos) {
//        return null;
//    }
//
//    @Override
//    public void fromTag(CompoundTag tag) {
//        parent.fromTag(tag);
//    }
//
//    @Override
//    public CompoundTag toTag(CompoundTag tag) {
//        return parent.toTag(tag);
//    }
//}
