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

package dev.galacticraft.mod.screen;

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.MagneticCraftingTableBlockEntity;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MagneticCraftingTableMenu extends RecipeBookMenu<CraftingInput, CraftingRecipe> implements ContainerListener {
    public static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 1;
    private static final int CRAFT_SLOT_END = 10;
    private static final int INV_SLOT_START = 10;
    private static final int INV_SLOT_END = 37;
    private static final int USE_ROW_SLOT_START = 37;
    private static final int USE_ROW_SLOT_END = 46;

    private final CraftingContainer craftSlots;
    private final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;
    private boolean placingRecipe;

    public MagneticCraftingTableMenu(int syncId, Inventory inventory) {
        this(syncId, inventory, null, ContainerLevelAccess.NULL);
    }

    public MagneticCraftingTableMenu(int syncId, Inventory inventory, MagneticCraftingTableBlockEntity craftingTable) {
        this(syncId, inventory, craftingTable, ContainerLevelAccess.create(craftingTable.getLevel(), craftingTable.getBlockPos()));
    }

    private MagneticCraftingTableMenu(int syncId, Inventory inventory, @Nullable CraftingContainer craftSlots, ContainerLevelAccess access) {
        super(GCMenuTypes.MAGNETIC_CRAFTING_TABLE, syncId);
        this.access = access;
        this.player = inventory.player;
        this.craftSlots = craftSlots == null
                ? new TransientCraftingContainer(this, MagneticCraftingTableBlockEntity.CONTAINER_WIDTH, MagneticCraftingTableBlockEntity.CONTAINER_HEIGHT)
                : craftSlots;

        checkContainerSize(this.craftSlots, MagneticCraftingTableBlockEntity.CONTAINER_SIZE);
        this.craftSlots.startOpen(this.player);
        if (this.craftSlots instanceof MagneticCraftingTableBlockEntity craftingTable) {
            craftingTable.addListener(this);
        }

        this.addSlot(new ResultSlot(this.player, this.craftSlots, this.resultSlots, 0, 124, 35));
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 3; ++column) {
                this.addSlot(new Slot(this.craftSlots, column + row * 3, 30 + column * 18, 17 + row * 18));
            }
        }
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }

        this.access.execute((level, pos) -> slotChangedCraftingGrid(this, level, this.player, this.craftSlots, this.resultSlots, null, false));
    }

    private static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftingContainer, ResultContainer resultContainer, @Nullable RecipeHolder<CraftingRecipe> recipeHint, boolean sendPacket) {
        if (level.isClientSide) {
            return;
        }

        CraftingInput input = craftingContainer.asCraftInput();
        ServerPlayer serverPlayer = (ServerPlayer) player;
        ItemStack result = ItemStack.EMPTY;
        Optional<RecipeHolder<CraftingRecipe>> recipe = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level, recipeHint);
        if (recipe.isPresent()) {
            RecipeHolder<CraftingRecipe> recipeHolder = recipe.get();
            CraftingRecipe craftingRecipe = recipeHolder.value();
            if (resultContainer.setRecipeUsed(level, serverPlayer, recipeHolder)) {
                ItemStack assembled = craftingRecipe.assemble(input, level.registryAccess());
                if (assembled.isItemEnabled(level.enabledFeatures())) {
                    result = assembled;
                }
            }
        }

        resultContainer.setItem(0, result);
        if (sendPacket) {
            menu.setRemoteSlot(0, result);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, result));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        if (!this.placingRecipe) {
            this.access.execute((level, pos) -> slotChangedCraftingGrid(this, level, this.player, this.craftSlots, this.resultSlots, null, true));
        }
        super.slotsChanged(container);
    }

    @Override
    public void containerChanged(Container container) {
        this.slotsChanged(container);
    }

    @Override
    public void beginPlacingRecipe() {
        this.placingRecipe = true;
    }

    @Override
    public void finishPlacingRecipe(RecipeHolder<CraftingRecipe> recipe) {
        this.placingRecipe = false;
        this.access.execute((level, pos) -> slotChangedCraftingGrid(this, level, this.player, this.craftSlots, this.resultSlots, recipe, true));
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents stackedContents) {
        this.craftSlots.fillStackedContents(stackedContents);
    }

    @Override
    public void clearCraftingContent() {
        this.craftSlots.clearContent();
        this.resultSlots.clearContent();
    }

    @Override
    public boolean recipeMatches(RecipeHolder<CraftingRecipe> recipe) {
        return recipe.value().matches(this.craftSlots.asCraftInput(), this.player.level());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.craftSlots.stopOpen(player);
        if (this.craftSlots instanceof MagneticCraftingTableBlockEntity craftingTable) {
            craftingTable.removeListener(this);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, GCBlocks.MAGNETIC_CRAFTING_TABLE);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            original = stack.copy();
            if (index == RESULT_SLOT) {
                this.access.execute((level, pos) -> stack.getItem().onCraftedBy(stack, level, player));
                if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, original);
            } else if (index >= INV_SLOT_START && index < USE_ROW_SLOT_END
                    ? !this.moveItemStackTo(stack, CRAFT_SLOT_START, CRAFT_SLOT_END, false)
                    && (index < INV_SLOT_END
                    ? !this.moveItemStackTo(stack, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)
                    : !this.moveItemStackTo(stack, INV_SLOT_START, INV_SLOT_END, false))
                    : !this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stack.getCount() == original.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
            if (index == RESULT_SLOT) {
                player.drop(stack, false);
            }
        }
        return original;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public int getResultSlotIndex() {
        return RESULT_SLOT;
    }

    @Override
    public int getGridWidth() {
        return this.craftSlots.getWidth();
    }

    @Override
    public int getGridHeight() {
        return this.craftSlots.getHeight();
    }

    @Override
    public int getSize() {
        return CRAFT_SLOT_END;
    }

    @Override
    public @NotNull RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public boolean shouldMoveToInventory(int slot) {
        return slot != this.getResultSlotIndex();
    }
}
