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

import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.machinelib.api.filter.ResourceFilter;
import dev.galacticraft.mod.content.block.entity.RocketWorkbenchBlockEntity;
import dev.galacticraft.mod.content.block.entity.RocketWorkbenchBlockEntity.RecipeSelection;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.machine.storage.VariableSizedContainer;
import dev.galacticraft.mod.mixin.AbstractContainerMenuAccessor;
import dev.galacticraft.mod.world.inventory.RocketResultSlot;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RocketWorkbenchMenu extends AbstractContainerMenu implements VariableSizedContainer.Listener {
    public static final int SPACING = 2;

    public static final int SCREEN_CENTER_BASE_X = 88;
    public static final int SCREEN_CENTER_BASE_Y = 158;

    public static final int BASE_HEIGHT = 140;

    public final RecipeSelection<RocketCone<?, ?>> cone;
    public final RecipeSelection<RocketBody<?, ?>> body;
    public final RecipeSelection<RocketFin<?, ?>> fins;
    public final RecipeSelection<RocketBooster<?, ?>> booster;
    public final RecipeSelection<RocketEngine<?, ?>> engine;
    public final RecipeSelection<RocketUpgrade<?, ?>> upgrade;

    public final RecipeCollection coneRecipes;
    public final RecipeCollection bodyRecipes;
    public final RecipeCollection finsRecipes;
    public final RecipeCollection boosterRecipes;
    public final RecipeCollection engineRecipes;
    public final RecipeCollection upgradeRecipes;

    public final RocketWorkbenchBlockEntity workbench;

    public int additionalHeight = 0;

    public final Inventory playerInventory;

    public RocketWorkbenchMenu(int syncId, RocketWorkbenchBlockEntity workbench, Inventory playerInventory) {
        super(GCMenuTypes.ROCKET_WORKBENCH, syncId);
        this.playerInventory = playerInventory;

        this.cone = workbench.cone;
        this.body = workbench.body;
        this.fins = workbench.fins;
        this.booster = workbench.booster;
        this.engine = workbench.engine;
        this.upgrade = workbench.upgrade;
        this.workbench = workbench;

        this.cone.inventory.addListener(this);
        this.body.inventory.addListener(this);
        this.fins.inventory.addListener(this);
        this.booster.inventory.addListener(this);
        this.engine.inventory.addListener(this);
        this.upgrade.inventory.addListener(this);

        RegistryAccess registryAccess = playerInventory.player.level().registryAccess();
        this.coneRecipes = new RecipeCollection(registryAccess.registryOrThrow(RocketRegistries.ROCKET_CONE));
        this.bodyRecipes = new RecipeCollection(registryAccess.registryOrThrow(RocketRegistries.ROCKET_BODY));
        this.finsRecipes = new RecipeCollection(registryAccess.registryOrThrow(RocketRegistries.ROCKET_FIN));
        this.boosterRecipes = new RecipeCollection(registryAccess.registryOrThrow(RocketRegistries.ROCKET_BOOSTER));
        this.engineRecipes = new RecipeCollection(registryAccess.registryOrThrow(RocketRegistries.ROCKET_ENGINE));
        this.upgradeRecipes = new RecipeCollection(registryAccess.registryOrThrow(RocketRegistries.ROCKET_UPGRADE));

        StackedContents contents = new StackedContents();
        playerInventory.fillStackedContents(contents);
        this.coneRecipes.calculateCraftable(contents);
        this.bodyRecipes.calculateCraftable(contents);
        this.finsRecipes.calculateCraftable(contents);
        this.boosterRecipes.calculateCraftable(contents);
        this.engineRecipes.calculateCraftable(contents);
        this.upgradeRecipes.calculateCraftable(contents);

        this.onSizeChanged();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.cone.inventory.removeListener(this);
        this.body.inventory.removeListener(this);
        this.fins.inventory.removeListener(this);
        this.booster.inventory.removeListener(this);
        this.engine.inventory.removeListener(this);
        this.upgrade.inventory.removeListener(this);
    }

    public RocketWorkbenchMenu(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(syncId, (RocketWorkbenchBlockEntity) playerInventory.player.level().getBlockEntity(buf.readBlockPos()), playerInventory);

        if (buf.readBoolean()) this.cone.setSelection(buf.readResourceLocation());
        if (buf.readBoolean()) this.body.setSelection(buf.readResourceLocation());
        if (buf.readBoolean()) this.fins.setSelection(buf.readResourceLocation());
        if (buf.readBoolean()) this.booster.setSelection(buf.readResourceLocation());
        if (buf.readBoolean()) this.engine.setSelection(buf.readResourceLocation());
        if (buf.readBoolean()) this.upgrade.setSelection(buf.readResourceLocation());
    }

    public static int calculateAdditionalHeight(RocketPartRecipe<?, ?> cone, RocketPartRecipe<?, ?> body, RocketPartRecipe<?, ?> fins, RocketPartRecipe<?, ?> booster, RocketPartRecipe<?, ?> engine, RocketPartRecipe<?, ?> upgrade) {
        int rocketHeight = Math.max(BASE_HEIGHT, Math.max(Math.max(
                        (engine != null ? engine.height() + SPACING : 0) + (body != null ? body.height() + SPACING : 0) + (cone != null ? cone.height() + SPACING : 0),
                        (booster != null ? booster.height() + SPACING : 0) + (fins != null ? fins.height() + SPACING : 0)),
                35 + (upgrade != null ? SPACING : 0) + ((int)Math.ceil(1 / 2.0)) * 19));

        return rocketHeight - BASE_HEIGHT;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        ItemStack out = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        ItemStack stack = slot.getItem();
        if (!stack.isEmpty()) {
            out = stack.copy();
            int slots = this.slots.size();
            if (index < slots - 9 * 4) {
                if (!this.moveItemStackTo(stack, slots - 9 * 4, slots, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, slots - 9 * 4, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return out;
    }

    public RocketWorkbenchBlockEntity.RecipeSelection<?> getSelection(RocketPartTypes type) {
        return switch (type) {
            case CONE -> this.cone;
            case BODY -> this.body;
            case FIN -> this.fins;
            case BOOSTER -> this.booster;
            case ENGINE -> this.engine;
            case UPGRADE -> this.upgrade;
        };
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this.workbench, player);
    }

    @Override
    public void onSizeChanged() {
        this.slots.clear();
        ((AbstractContainerMenuAccessor)this).getLastSlots().clear();
        ((AbstractContainerMenuAccessor)this).getRemoteSlots().clear();

        RocketPartRecipe<?, ?> engine = this.engine.getRecipe();
        RocketPartRecipe<?, ?> body = this.body.getRecipe();
        RocketPartRecipe<?, ?> cone = this.cone.getRecipe();
        RocketPartRecipe<?, ?> fins = this.fins.getRecipe();
        RocketPartRecipe<?, ?> booster = this.booster.getRecipe();
        RocketPartRecipe<?, ?> upgrade = this.upgrade.getRecipe();
        this.additionalHeight = calculateAdditionalHeight(cone, body, fins, booster, engine, upgrade);

        final int[] ext = {0, 0};
        final int[] ext1 = {0, 0};
        final int[] ext2 = {0, 0};

        if (engine != null) {
            engine.place((i, x, y, filter) -> {
                        ext[0] = Math.min(ext[0], x - SCREEN_CENTER_BASE_X);
                        ext[1] = Math.max(ext[1], x - SCREEN_CENTER_BASE_X + 18);
                        this.addSlot(new RocketCraftingSlot(this.engine.inventory, i, x, y, filter));
                    },
                    SCREEN_CENTER_BASE_X,
                    SCREEN_CENTER_BASE_X,
                    SCREEN_CENTER_BASE_Y + this.additionalHeight);
        }

        if (body != null) {
            body.place((i, x, y, filter) -> {
                        ext1[0] = Math.min(ext1[0], x - SCREEN_CENTER_BASE_X);
                        ext1[1] = Math.max(ext1[1], x - SCREEN_CENTER_BASE_X + 18);
                        this.addSlot(new RocketCraftingSlot(this.body.inventory, i, x, y, filter));
                    },
                    SCREEN_CENTER_BASE_X,
                    SCREEN_CENTER_BASE_X,
                    SCREEN_CENTER_BASE_Y + this.additionalHeight
                            - (engine != null ? engine.height() + SPACING : 0));
        }

        if (cone != null) {
            cone.place((i, x, y, filter) -> {
                        ext2[0] = Math.min(ext2[0], x - SCREEN_CENTER_BASE_X);
                        ext2[1] = Math.max(ext2[1], x - SCREEN_CENTER_BASE_X + 18);
                        this.addSlot(new RocketCraftingSlot(this.cone.inventory, i, x, y, filter));
                    },
                    SCREEN_CENTER_BASE_X,
                    SCREEN_CENTER_BASE_X,
                    SCREEN_CENTER_BASE_Y + this.additionalHeight
                            - (engine != null ? engine.height() + SPACING : 0)
                            - (body != null ? body.height() + SPACING : 0)
            );
        }

        if (fins != null) {
            if (fins.height() > (engine != null ? engine.height() : 0)) {
                ext[0] = Math.min(ext[0], ext1[0]);
                ext[1] = Math.max(ext[1], ext1[1]);
            }
            if (fins.height() > (engine != null ? engine.height() : 0) + (body != null ? body.height() : 0)) {
                ext[0] = Math.min(ext[0], ext2[0]);
                ext[1] = Math.max(ext[1], ext2[1]);
            }
            fins.place((i, x, y, filter) -> this.addSlot(new RocketCraftingSlot(this.fins.inventory, i, x, y, filter)),
                    SCREEN_CENTER_BASE_X + ext[0] - SPACING,
                    SCREEN_CENTER_BASE_X + ext[1] + SPACING,
                    SCREEN_CENTER_BASE_Y + this.additionalHeight
            );
        }

        if (booster != null) {
            if (booster.height() + (fins != null ? fins.height() : 0) > (engine != null ? engine.height() : 0)) {
                ext[0] = Math.min(ext[0], ext1[0]);
                ext[1] = Math.max(ext[1], ext1[1]);
            }
            if (booster.height() + (fins != null ? fins.height() : 0) > (engine != null ? engine.height() : 0) + (body != null ? body.height() : 0)) {
                ext[0] = Math.min(ext[0], ext2[0]);
                ext[1] = Math.max(ext[1], ext2[1]);
            }

            booster.place((i, x, y, filter) -> this.addSlot(new RocketCraftingSlot(this.booster.inventory, i, x, y, filter)),
                    SCREEN_CENTER_BASE_X + ext[0] - SPACING,
                    SCREEN_CENTER_BASE_X + ext[1] + SPACING,
                    SCREEN_CENTER_BASE_Y + this.additionalHeight
                            - (fins != null ? fins.height() + SPACING : 0)
            );

        }

        if (upgrade != null) {
            upgrade.place((i, x, y, filter) -> this.addSlot(new RocketCraftingSlot(this.upgrade.inventory, i, x, y, filter)),
                    18,
                    18,
                    SCREEN_CENTER_BASE_Y + this.additionalHeight
                            - (engine != null ? engine.height() + SPACING : 0)
                            - (body != null ? body.height() + SPACING : 0)
            );
        }

        this.addSlot(new RocketResultSlot(this, this.workbench.output, 0, 203, 136 + this.additionalHeight));

        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(this.playerInventory, column + row * 9 + 9, column * 18 + 8, row * 18 + 167 + this.additionalHeight));
            }
        }

        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(this.playerInventory, column, column * 18 + 8, 225 + this.additionalHeight));
        }

        this.onItemChanged();
    }

    @Override
    public void onItemChanged() {
        RocketData rocketData = RocketData.create(-1, this.cone.getSelectionKey(), this.body.getSelectionKey(), this.fins.getSelectionKey(), this.booster.getSelectionKey(), this.engine.getSelectionKey(), this.upgrade.getSelectionKey());
        boolean craftable = rocketData.isValid();
        RocketPartRecipe<?, ?> recipe = this.cone.getRecipe();
        craftable = craftable && (recipe != null && recipe.matches(this.cone.inventory, this.workbench.getLevel()));
        recipe = this.body.getRecipe();
        craftable = craftable && (recipe != null && recipe.matches(this.body.inventory, this.workbench.getLevel()));
        recipe = this.fins.getRecipe();
        craftable = craftable && (recipe != null && recipe.matches(this.fins.inventory, this.workbench.getLevel()));
        recipe = this.booster.getRecipe();
        craftable = craftable && (recipe == null || recipe.matches(this.booster.inventory, this.workbench.getLevel()));
        recipe = this.engine.getRecipe();
        craftable = craftable && (recipe != null && recipe.matches(this.engine.inventory, this.workbench.getLevel()));
        recipe = this.upgrade.getRecipe();
        craftable = craftable && (recipe == null ||recipe.matches(this.upgrade.inventory, this.workbench.getLevel()));
        if (craftable) {
            ItemStack stack = new ItemStack(GCItems.ROCKET, 1);
            CompoundTag tag = new CompoundTag();
            rocketData.toNbt(tag);
            stack.setTag(tag);
            this.workbench.output.setItem(0, stack);
        } else {
            this.workbench.output.setItem(0, ItemStack.EMPTY);
        }
    }

    public static class RecipeCollection {
        private final Registry<? extends RocketPart<?, ?>> recipes;
        private final List<Holder.Reference<? extends RocketPart<?, ?>>> uncraftable = new ArrayList<>();
        private final List<Holder.Reference<? extends RocketPart<?, ?>>> craftable = new ArrayList<>();

        public RecipeCollection(Registry<? extends RocketPart<?, ?>> recipes) {
            this.recipes = recipes;
        }

        public List<Holder.Reference<? extends RocketPart<?, ?>>> getCraftable() {
            return craftable;
        }

        public List<Holder.Reference<? extends RocketPart<?, ?>>> getUncraftable() {
            return uncraftable;
        }

        public void calculateCraftable(StackedContents contents) {
            this.craftable.clear();
            this.uncraftable.clear();

            this.recipes.holders().forEach(holder -> {
                RocketPartRecipe<?, ?> recipe = holder.value().getRecipe();
                if (recipe != null) {
                    if (contents.canCraft(recipe, null)) {
                        this.craftable.add(holder);
                    } else {
                        this.uncraftable.add(holder);
                    }
                }
            });
        }
    }

    private static class RocketCraftingSlot extends Slot {
        private final ResourceFilter<Item> filter;

        public RocketCraftingSlot(Container container, int slot, int x, int y, ResourceFilter<Item> filter) {
            super(container, slot, x, y);
            this.filter = filter;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.isEmpty() || this.filter.test(stack.getItem(), stack.getTag());
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
