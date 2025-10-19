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

package dev.galacticraft.mod.content.block.entity;

import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.block.special.CocoonBlock;
import dev.galacticraft.mod.screen.CocoonMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CocoonBlockEntity extends BlockEntity implements Container, MenuProvider {
    public static final int SLOT_COUNT = 15;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    public CocoonBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.COCOON, pos, state);
    }

    /* --------------------------- Persistence --------------------------- */

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, this.items, registries);
    }

    /* ------------------------- Container impl ------------------------- */

    @Override public int getContainerSize() { return items.size(); }
    @Override public boolean isEmpty() {
        for (ItemStack s : items) if (!s.isEmpty()) return false;
        return true;
    }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) {
        ItemStack res = ContainerHelper.removeItem(items, slot, amount);
        if (!res.isEmpty()) setChanged();
        return res;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) {
        ItemStack s = items.get(slot);
        if (s.isEmpty()) return ItemStack.EMPTY;
        items.set(slot, ItemStack.EMPTY);
        return s;
    }
    @Override public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > stack.getMaxStackSize()) stack.setCount(stack.getMaxStackSize());
        setChanged();
    }
    @Override public boolean stillValid(Player player) {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) return false;
        return player.distanceToSqr(
                this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() + 0.5,
                this.worldPosition.getZ() + 0.5) <= 64.0;
    }
    @Override public void clearContent() {
        for (int i = 0; i < items.size(); i++) items.set(i, ItemStack.EMPTY);
        setChanged();
    }

    /* ----------------------------- Insert ----------------------------- */
    /** Insert entire stack; returns leftover. Always calls setChanged() if anything moved. */
    public ItemStack insert(ItemStack in) {
        if (in.isEmpty()) return ItemStack.EMPTY;

        ItemStack remaining = in.copy();
        boolean changed = false;

        // 1) Merge with existing stacks (same item + components)
        for (int i = 0; i < items.size() && !remaining.isEmpty(); i++) {
            ItemStack cur = items.get(i);
            if (cur.isEmpty()) continue;
            if (!ItemStack.isSameItemSameComponents(cur, remaining)) continue;

            int max = cur.getMaxStackSize(); // don’t cap at 64; let the item decide
            int space = max - cur.getCount();
            if (space <= 0) continue;

            int move = Math.min(space, remaining.getCount());
            if (move > 0) {
                cur.grow(move);
                remaining.shrink(move);
                changed = true;
            }
        }

        // 2) Fill empties
        for (int i = 0; i < items.size() && !remaining.isEmpty(); i++) {
            ItemStack cur = items.get(i);
            if (!cur.isEmpty()) continue;

            int max = Math.min(remaining.getMaxStackSize(), remaining.getCount());
            if (max <= 0) break;

            ItemStack placed = remaining.copy();
            placed.setCount(max);
            items.set(i, placed);
            remaining.shrink(max);
            changed = true;
        }

        if (changed) setChanged();
        return remaining;
    }

    /* --------------------------- Absorption --------------------------- */

    public static void serverTick(Level level, BlockPos pos, BlockState state, CocoonBlockEntity be) {
        if (level.getGameTime() % 3 != 0) return; // absorb a bit faster

        Direction facing = state.getValue(CocoonBlock.FACING);
        AABB box = CocoonBlock.absorptionAabb(pos, facing);

        List<ItemEntity> list = level.getEntitiesOfClass(ItemEntity.class, box,
                e -> e.isAlive() && !e.getItem().isEmpty());

        boolean anyChange = false;
        for (ItemEntity e : list) {
            ItemStack before = e.getItem();
            if (before.isEmpty()) continue;

            ItemStack leftover = be.insert(before);
            if (leftover.isEmpty()) {
                e.discard();
                anyChange = true;
            } else if (leftover.getCount() != before.getCount()) {
                e.setItem(leftover);
                anyChange = true;
            }
        }
        if (anyChange) be.setChanged();
    }

    /* ---------------------------- Utilities --------------------------- */

    public void dropAllContents(Level level, BlockPos pos) {
        SimpleContainer temp = new SimpleContainer(items.size());
        for (int i = 0; i < items.size(); i++) temp.setItem(i, items.get(i));
        Containers.dropContents(level, pos, temp);
        clearContent();
    }

    /* ----------------------------- UI -------------------------------- */

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.galacticraft.cocoon");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInv, Player player) {
        return new CocoonMenu(syncId, playerInv, this, this.worldPosition);
    }
}