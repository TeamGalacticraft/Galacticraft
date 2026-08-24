/*
 * Copyright (c) 2019-2026 Team Galacticraft
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
import dev.galacticraft.mod.screen.MagneticCraftingTableMenu;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("UnstableApiUsage")
public class MagneticCraftingTableBlockEntity extends BaseContainerBlockEntity implements CraftingContainer, WorldlyContainer, SidedStorageBlockEntity {
    public static final int CONTAINER_WIDTH = 3;
    public static final int CONTAINER_HEIGHT = 3;
    public static final int CONTAINER_SIZE = CONTAINER_WIDTH * CONTAINER_HEIGHT;
    private static final int[] SLOTS = IntStream.range(0, CONTAINER_SIZE).toArray();

    private final List<ContainerListener> listeners = new ArrayList<>();
    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    public MagneticCraftingTableBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.MAGNETIC_CRAFTING_TABLE, pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        super.loadAdditional(tag, registryLookup);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registryLookup);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        super.saveAdditional(tag, registryLookup);
        ContainerHelper.saveAllItems(tag, this.items, registryLookup);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.crafting");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int syncId, Inventory inventory) {
        return new MagneticCraftingTableMenu(syncId, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getWidth() {
        return CONTAINER_WIDTH;
    }

    @Override
    public int getHeight() {
        return CONTAINER_HEIGHT;
    }

    @Override
    public void fillStackedContents(StackedContents stackedContents) {
        for (ItemStack stack : this.items) {
            stackedContents.accountSimpleStack(stack);
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public @NotNull Storage<ItemVariant> getItemStorage(@Nullable Direction side) {
        return InventoryStorage.of(this, side);
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return this.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return false;
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        for (ContainerListener listener : List.copyOf(this.listeners)) {
            listener.containerChanged(this);
        }
    }

    public void addListener(ContainerListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ContainerListener listener) {
        this.listeners.remove(listener);
    }
}
