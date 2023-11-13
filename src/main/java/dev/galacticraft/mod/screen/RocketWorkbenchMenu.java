/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import dev.galacticraft.api.accessor.ResearchAccessor;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipeSlot;
import dev.galacticraft.mod.content.block.entity.RocketWorkbenchBlockEntity;
import dev.galacticraft.mod.content.block.entity.RocketWorkbenchBlockEntity.RecipeSelection;
import dev.galacticraft.mod.machine.storage.VariableSizedContainer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RocketWorkbenchMenu extends AbstractContainerMenu {
    public static final int SPACING = 2;

    public static final int SCREEN_CENTER_BASE_X = 88;
    public static final int SCREEN_CENTER_BASE_Y = 144;

    public final RecipeSelection cone;
    public final RecipeSelection body;
    public final RecipeSelection fins;
    public final RecipeSelection booster;
    public final RecipeSelection bottom;
    public final RecipeSelection upgrade;

    public final RecipeCollection coneRecipes;
    public final RecipeCollection bodyRecipes;
    public final RecipeCollection finsRecipes;
    public final RecipeCollection boosterRecipes;
    public final RecipeCollection bottomRecipes;
    public final RecipeCollection upgradeRecipes;

    public int additionalHeight = 0;

    public final Inventory playerInventory;
    public final Registry<RocketPartRecipe<?, ?>> recipeRegistry;

    public RocketWorkbenchMenu(int syncId, RocketWorkbenchBlockEntity workbench, Inventory playerInventory) {
        super(GCMenuTypes.ROCKET_WORKBENCH, syncId);
        this.playerInventory = playerInventory;

        List<RocketPartRecipe<?, ?>> cones = new ArrayList<>();
        List<RocketPartRecipe<?, ?>> bodies = new ArrayList<>();
        List<RocketPartRecipe<?, ?>> fins = new ArrayList<>();
        List<RocketPartRecipe<?, ?>> boosters = new ArrayList<>();
        List<RocketPartRecipe<?, ?>> bottoms = new ArrayList<>();
        List<RocketPartRecipe<?, ?>> upgrades = new ArrayList<>();

        RegistryAccess registryAccess = playerInventory.player.level().registryAccess();
        this.recipeRegistry = registryAccess.registryOrThrow(RocketRegistries.ROCKET_PART_RECIPE);

        for (Holder<RocketPartRecipe<?, ?>> recipe : recipeRegistry.asHolderIdMap()) {
            if (recipe.unwrapKey().map(e -> ((ResearchAccessor) playerInventory.player).galacticraft$isUnlocked(e.location())).orElse(false)) {
                switch (recipe.value().partType()) {
                    case CONE -> cones.add(recipe.value());
                    case BODY -> bodies.add(recipe.value());
                    case FIN -> fins.add(recipe.value());
                    case BOOSTER -> boosters.add(recipe.value());
                    case BOTTOM -> bottoms.add(recipe.value());
                    case UPGRADE -> upgrades.add(recipe.value());
                }
            }
        }
        
        this.cone = workbench.cone;
        this.body = workbench.body;
        this.fins = workbench.fins;
        this.booster = workbench.booster;
        this.bottom = workbench.bottom;
        this.upgrade = workbench.upgrade;

        this.cone.inventory.setListener(this::onSizeChange);
        this.body.inventory.setListener(this::onSizeChange);
        this.fins.inventory.setListener(this::onSizeChange);
        this.booster.inventory.setListener(this::onSizeChange);
        this.bottom.inventory.setListener(this::onSizeChange);
        this.upgrade.inventory.setListener(this::onSizeChange);

        this.coneRecipes = new RecipeCollection(cones);
        this.bodyRecipes = new RecipeCollection(bodies);
        this.finsRecipes = new RecipeCollection(fins);
        this.boosterRecipes = new RecipeCollection(boosters);
        this.bottomRecipes = new RecipeCollection(bottoms);
        this.upgradeRecipes = new RecipeCollection(upgrades);

        StackedContents contents = new StackedContents();
        playerInventory.fillStackedContents(contents);
        this.coneRecipes.calculateCraftable(contents);
        this.bodyRecipes.calculateCraftable(contents);
        this.finsRecipes.calculateCraftable(contents);
        this.boosterRecipes.calculateCraftable(contents);
        this.bottomRecipes.calculateCraftable(contents);
        this.upgradeRecipes.calculateCraftable(contents);

        this.onSizeChange();
    }

    public RocketWorkbenchMenu(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(syncId, (RocketWorkbenchBlockEntity) playerInventory.player.level().getBlockEntity(buf.readBlockPos()), playerInventory);
    }

    public static int calculateAdditionalHeight(RocketPartRecipe<?, ?> cone, RocketPartRecipe<?, ?> body, RocketPartRecipe<?, ?> fins, RocketPartRecipe<?, ?> booster, RocketPartRecipe<?, ?> bottom, RocketPartRecipe<?, ?> upgrade) {
        int rocketHeight = Math.max(126, Math.max(Math.max(
                        (bottom != null ? bottom.height() + SPACING : 0) + (body != null ? body.height() + SPACING : 0) + (cone != null ? cone.height() + SPACING : 0),
                        (booster != null ? booster.height() + SPACING : 0) + (fins != null ? fins.height() + SPACING : 0)),
                35 + (upgrade != null ? SPACING : 0) + ((int)Math.ceil(1 / 2.0)) * 19));

        return rocketHeight - 126;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.playerInventory.stillValid(player);
    }

    public void onSizeChange() {
        this.slots.clear();

        this.additionalHeight = calculateAdditionalHeight(this.cone.selection, this.body.selection, this.fins.selection, this.booster.selection, this.bottom.selection, this.upgrade.selection);

        int midsectionWidth = Math.max((this.bottom.selection != null ? this.bottom.selection.width() : 0), Math.max((this.body.selection != null ? this.body.selection.width() : 0), (this.cone.selection != null ? this.cone.selection.width() : 0)));
        int leftEdge = SCREEN_CENTER_BASE_X - midsectionWidth / 2;
        int rightSide = SCREEN_CENTER_BASE_X + midsectionWidth / 2;

        if (this.bottom.selection != null) {
            int leftSide = SCREEN_CENTER_BASE_X - this.bottom.selection.width() / 2;
            int bottomEdge = SCREEN_CENTER_BASE_Y + this.additionalHeight;
            centered(this.bottom.selection, this.bottom.inventory, leftSide, bottomEdge);
        }

        if (this.body.selection != null) {
            int leftSide = SCREEN_CENTER_BASE_X - this.body.selection.width() / 2;
            int bottomEdge = (SCREEN_CENTER_BASE_Y + this.additionalHeight) - (this.bottom.selection != null ? this.bottom.selection.height() + SPACING : 0);
            centered(this.body.selection, this.body.inventory, leftSide, bottomEdge);
        }

        if (this.cone.selection != null) {
            int leftSide = SCREEN_CENTER_BASE_X - this.cone.selection.width() / 2;
            int bottomEdge = (SCREEN_CENTER_BASE_Y + this.additionalHeight) - (this.bottom.selection != null ? this.bottom.selection.height() + SPACING : 0) - (this.body.selection != null ? this.body.selection.height() + SPACING : 0);
            centered(this.cone.selection, this.cone.inventory, leftSide, bottomEdge);
        }

        if (this.booster.selection != null) {
            int bottomEdge = SCREEN_CENTER_BASE_Y + this.additionalHeight;
            mirrored(this.booster.selection, this.booster.inventory, leftEdge, rightSide, bottomEdge);
        }

        if (this.fins.selection != null) {
            int bottomEdge = SCREEN_CENTER_BASE_Y + this.additionalHeight - (this.booster.selection != null ? this.booster.selection.height() + SPACING : 0);
            mirrored(this.fins.selection, this.fins.inventory, leftEdge, rightSide, bottomEdge);
        }

        if (this.upgrade.selection != null) {
            final int baseX = 11;
            int y = 62 + this.additionalHeight;
            int x = baseX;
            for (int i = 0; i < this.upgrade.inventory.getContainerSize(); i++) {
                this.addSlot(new Slot(this.upgrade.inventory, 0, x, y - 2));
                if (x == baseX) {
                    x += 21;
                } else {
                    x = baseX;
                    y -= 21;
                }
            }
        }

        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(this.playerInventory, column + row * 9 + 9, column * 18 + 8, row * 18 + 167 + this.additionalHeight));
            }
        }

        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(this.playerInventory, column, column * 18 + 8, 225 + this.additionalHeight));
        }
    }

    private void mirrored(RocketPartRecipe<?, ?> recipe, VariableSizedContainer container, int leftEdge, int rightSide, int bottomEdge) {
        List<RocketPartRecipeSlot> slots = recipe.slots();
        for (int i = 0; i < slots.size(); i++) {
            RocketPartRecipeSlot slot = slots.get(i);
            int x = slot.x();
            if (x < 0) {
                this.addSlot(new Slot(container, i, leftEdge - x - 18 - SPACING, bottomEdge - recipe.height() + slot.y()));
            } else {
                this.addSlot(new Slot(container, i, rightSide + x + SPACING, bottomEdge - recipe.height() + slot.y()));
            }
        }
    }

    private void centered(RocketPartRecipe<?, ?> recipe, VariableSizedContainer container, int leftSide, int bottomEdge) {
        List<RocketPartRecipeSlot> slots = recipe.slots();
        for (int i = 0; i < slots.size(); i++) {
            RocketPartRecipeSlot slot = slots.get(i);
            this.addSlot(new Slot(container, i, slot.x() + leftSide, bottomEdge - recipe.height() + slot.y()));
        }
    }

    public static class RecipeCollection {
        private final List<RocketPartRecipe<?, ?>> recipes;
        private final List<RocketPartRecipe<?, ?>> uncraftable = new ArrayList<>();
        private final List<RocketPartRecipe<?, ?>> craftable = new ArrayList<>();

        public RecipeCollection(List<RocketPartRecipe<?, ?>> recipes) {
            this.recipes = recipes;
        }

        public List<RocketPartRecipe<?, ?>> getCraftable() {
            return craftable;
        }

        public List<RocketPartRecipe<?, ?>> getUncraftable() {
            return uncraftable;
        }

        public void calculateCraftable(StackedContents contents) {
            this.craftable.clear();
            this.uncraftable.clear();
            for (RocketPartRecipe<?, ?> recipe : this.recipes) {
                if (contents.canCraft(recipe, null)) {
                    this.craftable.add(recipe);
                } else {
                    this.uncraftable.add(recipe);
                }
            }
        }
    }
}
