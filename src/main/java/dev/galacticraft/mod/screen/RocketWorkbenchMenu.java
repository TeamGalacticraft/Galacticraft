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

package dev.galacticraft.mod.screen;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.RocketPrefabs;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCRocketParts;
import dev.galacticraft.mod.content.block.entity.RocketWorkbenchBlockEntity;
import dev.galacticraft.mod.machine.storage.VariableSizedContainer;
import dev.galacticraft.mod.recipe.RocketRecipe;
import dev.galacticraft.mod.world.inventory.RocketResultSlot;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class RocketWorkbenchMenu extends AbstractContainerMenu implements VariableSizedContainer.Listener, ContainerListener {
    public static final int SPACING = 2;

    public static final int SCREEN_CENTER_BASE_X = 88;
    public static final int SCREEN_CENTER_BASE_Y = 158;

    public final RocketWorkbenchBlockEntity workbench;
    public RecipeHolder<RocketRecipe> recipe;
    protected int recipeSize;

    public final Inventory playerInventory;

    private boolean coneComplete;
    private boolean bodyComplete;
    private boolean boostersComplete;
    private boolean finsComplete;
    private boolean engineComplete;

    // Tracking each part of the rocket for the preview
    private Slot coneSlot;
    private List<Slot> bodySlots;
    private List<Slot> boosterSlots;
    private List<Slot> finSlots;
    private Slot engineSlot;

    public RocketWorkbenchMenu(int syncId, RocketWorkbenchBlockEntity workbench, Inventory playerInventory) {
        super(GCMenuTypes.ROCKET_WORKBENCH, syncId);
        this.playerInventory = playerInventory;

        this.workbench = workbench;

        this.workbench.ingredients.addListener(this);
        this.workbench.chests.addListener(this);

        this.recipe = (RecipeHolder<RocketRecipe>) playerInventory.player.level().getRecipeManager().byKey(Constant.id("rocket/rocket")).get();
        this.recipeSize = this.recipe.value().getIngredients().size();
        this.addSlots();
        this.workbench.resizeInventory(this.recipeSize);
    }

    protected void addSlots() {
        RocketRecipe recipe = this.recipe.value();
        int bodyHeight = this.recipe.value().bodyHeight();
        int y = this.coneY();
        int centerX = SCREEN_CENTER_BASE_X - 36;

        // Cone
        this.coneSlot = this.addSlot(new FilteredSlot(this.workbench.ingredients, 0, centerX - 8, y, recipe.cone()).withBackground(Constant.SlotSprite.ROCKET_CONE));

        // Body
        this.bodySlots = new ArrayList<>();
        int nextSlot = 1;
        for (int i = 0; i < bodyHeight; i++) {
            this.bodySlots.add(this.addSlot(new FilteredSlot(this.workbench.ingredients, nextSlot, centerX - 17, 18 + 18*i + y, recipe.body()).withBackground(Constant.SlotSprite.ROCKET_PLATING)));
            this.bodySlots.add(this.addSlot(new FilteredSlot(this.workbench.ingredients, nextSlot + 1, centerX + 1, 18 + 18*i + y, recipe.body()).withBackground(Constant.SlotSprite.ROCKET_PLATING)));
            nextSlot += 2;
        }

        // Boosters
        this.boosterSlots = new ArrayList<>();
        if (!recipe.boosters().isEmpty()) {
            this.boosterSlots.add(this.addSlot(new FilteredSlot(this.workbench.ingredients, nextSlot, centerX - 35, 18 * bodyHeight - 18 + y, recipe.boosters()).withBackground(Constant.SlotSprite.ROCKET_BOOSTER)));
            this.boosterSlots.add(this.addSlot(new FilteredSlot(this.workbench.ingredients, nextSlot + 1, centerX + 19, 18 * bodyHeight - 18 + y, recipe.boosters()).withBackground(Constant.SlotSprite.ROCKET_BOOSTER)));
            nextSlot += 2;
        }

        // Left fins
        this.finSlots = new ArrayList<>();
        this.finSlots.add(this.addSlot(new FilteredSlot(this.workbench.ingredients, nextSlot, centerX - 35, 18*bodyHeight + y, recipe.fins()).withBackground(Constant.SlotSprite.ROCKET_FIN)));
        this.finSlots.add(this.addSlot(new FilteredSlot(this.workbench.ingredients, nextSlot + 1, centerX - 35, 18*bodyHeight + 18 + y, recipe.fins()).withBackground(Constant.SlotSprite.ROCKET_FIN)));
        nextSlot += 2;

        // Right fins
        this.finSlots.add(this.addSlot(new FilteredSlot(this.workbench.ingredients, nextSlot, centerX + 19, 18*bodyHeight + y, recipe.fins()).withBackground(Constant.SlotSprite.ROCKET_FIN)));
        this.finSlots.add(this.addSlot(new FilteredSlot(this.workbench.ingredients, nextSlot + 1, centerX + 19, 18*bodyHeight + 18 + y, recipe.fins()).withBackground(Constant.SlotSprite.ROCKET_FIN)));
        nextSlot += 2;

        // Engine
        this.engineSlot = this.addSlot(new FilteredSlot(this.workbench.ingredients, nextSlot, centerX - 8, 18*bodyHeight + 18 + y, recipe.engine()).withBackground(Constant.SlotSprite.ROCKET_ENGINE));

        // Chest
        this.addSlot(new FilteredSlot(this.workbench.chests, 0, 44, 140, stack -> this.workbench.chests.canPlaceItem(0, stack)).withBackground(Constant.SlotSprite.CHEST));

        // Output
        this.addSlot(new RocketResultSlot(this, this.workbench.output, 0, 125, 135));

        // Player inventory
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(this.playerInventory, column + row * 9 + 9, column * 18 + 8, row * 18 + 167));
            }
        }

        // Player hotbar
        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(this.playerInventory, column, column * 18 + 8, 225));
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        this.workbench.ingredients.removeListener(this);
        this.workbench.chests.removeListener(this);
    }

    public RocketWorkbenchMenu(int syncId, Inventory playerInventory, OpeningData data) {
        this(syncId, (RocketWorkbenchBlockEntity) playerInventory.player.level().getBlockEntity(data.pos), playerInventory);
    }

    private int rocketHeight() {
        return 18 + this.recipe.value().bodyHeight() * 18 + 18;
    }

    private int coneY() {
        return 81 - rocketHeight()/2;
    }

    protected boolean isIngredient(ItemStack stack) {
        return this.recipe.value().getIngredients().stream().distinct().anyMatch(ingredient -> ingredient.test(stack));
    }

    protected boolean isWorkbenchInventory(int slotIndex) {
        return slotIndex < this.slots.size() - 9 * 4;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        ItemStack out = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        ItemStack stack = slot.getItem();
        if (!stack.isEmpty()) {
            out = stack.copy();
            int slots = this.slots.size();
            if (isWorkbenchInventory(index)) {
                if (!this.moveItemStackTo(stack, slots - 9 * 4, slots, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (this.isIngredient(stack)) {
                    if (!this.moveItemStackTo(stack, 0, this.recipeSize, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stack, 0, slots - 9 * 4, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            slot.onTake(player, stack);
        }

        return out;
    }

    public RocketData previewRocket() {
        RocketData data = this.recipe.value().result().getOrDefault(GCDataComponents.ROCKET_DATA, RocketPrefabs.TIER_1);
        return new RocketData(
                this.coneComplete ? data.cone() : Optional.empty(),
                this.bodyComplete ? data.body() : Optional.empty(),
                this.finsComplete ? data.fin() : Optional.empty(),
                this.boostersComplete ? data.booster() : Optional.empty(),
                this.engineComplete ? data.engine() : Optional.empty(),
                this.workbench.chests.isEmpty() ? Optional.empty() : Optional.of(EitherHolder.fromEither(Either.right(GCRocketParts.STORAGE_UPGRADE))),
                data.color()
        );
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this.workbench, player);
    }

    @Override
    public void onSizeChanged() {
        this.onItemChanged();
    }

    // From ingredient slots
    @Override
    public void onItemChanged() {
        if (this.recipe.value().matches(this.workbench.ingredients.asInput(), this.workbench.getLevel())) {
            ItemStack output = this.recipe.value().result().copy();
            RocketData rocketData = output.getOrDefault(GCDataComponents.ROCKET_DATA, RocketPrefabs.TIER_1);
            RocketData upgradedRocketData = new RocketData(
                    rocketData.cone(),
                    rocketData.body(),
                    rocketData.fin(),
                    rocketData.booster(),
                    rocketData.engine(),
                    this.workbench.chests.isEmpty() ? Optional.empty() : Optional.of(EitherHolder.fromEither(Either.right(GCRocketParts.STORAGE_UPGRADE))),
                    rocketData.color()
            );
            output.set(GCDataComponents.ROCKET_DATA, upgradedRocketData);
            this.workbench.output.setItem(0, output);
        } else {
            this.workbench.output.clearContent();
        }

        this.coneComplete = this.coneSlot.hasItem();
        this.bodyComplete = this.bodySlots.stream().allMatch(Slot::hasItem);
        this.boostersComplete = this.boosterSlots.stream().allMatch(Slot::hasItem);
        this.finsComplete = this.finSlots.stream().allMatch(Slot::hasItem);
        this.engineComplete = this.engineSlot.hasItem();
    }

    // From chest slots
    @Override
    public void containerChanged(Container sender) {
        this.onItemChanged();
    }

    private static class FilteredSlot extends Slot {
        private final Predicate<ItemStack> filter;
        private Pair<ResourceLocation, ResourceLocation> background;

        public FilteredSlot(Container container, int slot, int x, int y, Predicate<ItemStack> filter) {
            super(container, slot, x, y);
            this.filter = filter;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.isEmpty() || this.filter.test(stack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public @Nullable com.mojang.datafixers.util.Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return this.background;
        }

        public FilteredSlot withBackground(ResourceLocation background) {
            this.background = new Pair<>(InventoryMenu.BLOCK_ATLAS, background);
            return this;
        }
    }

    public record OpeningData(BlockPos pos) {
        public static final StreamCodec<ByteBuf, OpeningData> CODEC = BlockPos.STREAM_CODEC.map(OpeningData::new, OpeningData::pos);
    }
}
