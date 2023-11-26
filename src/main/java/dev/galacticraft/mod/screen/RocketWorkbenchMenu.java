/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import dev.galacticraft.api.item.Schematic;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.part.RocketBody;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipeSlot;
import dev.galacticraft.mod.content.block.entity.RocketWorkbenchBlockEntity;
import dev.galacticraft.mod.machine.storage.VariableSizedContainer;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RocketWorkbenchMenu extends AbstractContainerMenu {
    public static final int SPACING = 4;

    public static final int SCREEN_CENTER_BASE_X = 118;
    public static final int SCREEN_CENTER_BASE_Y = 116;

    public final Container schematics;
    public final VariableSizedContainer cone;
    public final VariableSizedContainer body;
    public final VariableSizedContainer fins;
    public final VariableSizedContainer booster;
    public final VariableSizedContainer bottom;
    public final VariableSizedContainer upgrades;

    public @Nullable RocketPartRecipe<?, ?> coneRecipe;
    public @Nullable RocketPartRecipe<?, ?> bodyRecipe;
    public @Nullable RocketPartRecipe<?, ?> finsRecipe;
    public @Nullable RocketPartRecipe<?, ?> boosterRecipe;
    public @Nullable RocketPartRecipe<?, ?> bottomRecipe;

    public int upgradeCapacity = 0;
    public int additionalDistance = 0;

    public final Inventory playerInventory;
    private final Registry<RocketPartRecipe<?, ?>> recipeRegistry;

    public RocketWorkbenchMenu(int syncId, RocketWorkbenchBlockEntity workbench, Inventory playerInventory) {
        super(GCMenuTypes.ROCKET_WORKBENCH, syncId);
        this.playerInventory = playerInventory;
        this.schematics = workbench.inventory;
        this.cone = workbench.cone;
        this.body = workbench.body;
        this.fins = workbench.fins;
        this.booster = workbench.booster;
        this.bottom = workbench.bottom;
        this.upgrades = workbench.upgrades;
        
        this.cone.setListener(this::onSizeChange);
        this.body.setListener(this::onSizeChange);
        this.fins.setListener(this::onSizeChange);
        this.booster.setListener(this::onSizeChange);
        this.bottom.setListener(this::onSizeChange);
        this.upgrades.setListener(this::onSizeChange);
        
        this.recipeRegistry = playerInventory.player.level().registryAccess().registryOrThrow(RocketRegistries.ROCKET_PART_RECIPE);
        
        this.onSizeChange();
    }

    public RocketWorkbenchMenu(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(syncId, (RocketWorkbenchBlockEntity) playerInventory.player.level().getBlockEntity(buf.readBlockPos()), playerInventory);
    }

    public static int calculateAdditionalHeight(RocketPartRecipe<?, ?> cone, RocketPartRecipe<?, ?> body, RocketPartRecipe<?, ?> fins, RocketPartRecipe<?, ?> booster, RocketPartRecipe<?, ?> bottom, int upgradeCapacity) {
        int rocketHeight = Math.max(106, Math.max(Math.max(
                        (bottom != null ? bottom.height() + SPACING : 0) + (body != null ? body.height() + SPACING : 0) + (cone != null ? cone.height() + SPACING : 0),
                        (booster != null ? booster.height() + SPACING : 0) + (fins != null ? fins.height() + SPACING : 0)),
                35 + (upgradeCapacity != 0 ? SPACING : 0) + ((int)Math.ceil(upgradeCapacity / 2.0)) * 19));

        return rocketHeight - 106;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.playerInventory.stillValid(player);
    }

    public void onSizeChange() {
        this.slots.clear();

        this.coneRecipe = this.getRecipe(RocketRegistries.ROCKET_CONE);
        this.bodyRecipe = this.getRecipe(RocketRegistries.ROCKET_BODY);
        this.finsRecipe = this.getRecipe(RocketRegistries.ROCKET_FIN);
        this.boosterRecipe = this.getRecipe(RocketRegistries.ROCKET_BOOSTER);
        this.bottomRecipe = this.getRecipe(RocketRegistries.ROCKET_BOTTOM);

        this.upgradeCapacity = bodyRecipe != null ? bodyRecipe.output().cast(RocketRegistries.ROCKET_BODY)
                .flatMap(key -> this.playerInventory.player.level().registryAccess().registryOrThrow(RocketRegistries.ROCKET_BODY).getOptional(key))
                .map(RocketBody::getUpgradeCapacity).orElse(0) : 0;


        this.additionalDistance = calculateAdditionalHeight(coneRecipe, bodyRecipe, finsRecipe, boosterRecipe, bottomRecipe, upgradeCapacity);

        int midsectionWidth = Math.max((bottomRecipe != null ? bottomRecipe.width() : 0), Math.max((bodyRecipe != null ? bodyRecipe.width() : 0), (coneRecipe != null ? coneRecipe.width() : 0)));
        int leftEdge = SCREEN_CENTER_BASE_X - midsectionWidth / 2;
        int rightSide = SCREEN_CENTER_BASE_X + midsectionWidth / 2;

        this.addSlot(new Slot(this.schematics, 0, 198, 13 + this.additionalDistance));
        this.addSlot(new Slot(this.schematics, 1, 198, 34 + this.additionalDistance));
        this.addSlot(new Slot(this.schematics, 2, 198, 55 + this.additionalDistance));
        this.addSlot(new Slot(this.schematics, 3, 198, 76 + this.additionalDistance));
        this.addSlot(new Slot(this.schematics, 4, 198, 97 + this.additionalDistance));

        if (this.bottomRecipe != null) {
            int leftSide = SCREEN_CENTER_BASE_X - bottomRecipe.width() / 2;
            int bottomEdge = SCREEN_CENTER_BASE_Y + this.additionalDistance;
            centered(bottomRecipe, this.bottom, leftSide, bottomEdge);
        }

        if (this.bodyRecipe != null) {
            int leftSide = SCREEN_CENTER_BASE_X - bodyRecipe.width() / 2;
            int bottomEdge = (SCREEN_CENTER_BASE_Y + this.additionalDistance) - (bottomRecipe != null ? bottomRecipe.height() + SPACING : 0);
            centered(bodyRecipe, this.body, leftSide, bottomEdge);
        }

        if (this.coneRecipe != null) {
            int leftSide = SCREEN_CENTER_BASE_X - coneRecipe.width() / 2;
            int bottomEdge = (SCREEN_CENTER_BASE_Y + this.additionalDistance) - (bottomRecipe != null ? bottomRecipe.height() + SPACING : 0) - (bodyRecipe != null ? bodyRecipe.height() + SPACING : 0);
            centered(coneRecipe, this.cone, leftSide, bottomEdge);
        }

        if (this.boosterRecipe != null) {
            int bottomEdge = SCREEN_CENTER_BASE_Y + this.additionalDistance;
            mirrored(boosterRecipe, this.booster, leftEdge, rightSide, bottomEdge);
        }

        if (this.finsRecipe != null) {
            int bottomEdge = SCREEN_CENTER_BASE_Y + this.additionalDistance - (boosterRecipe != null ? boosterRecipe.height() + SPACING : 0);
            mirrored(finsRecipe, this.fins, leftEdge, rightSide, bottomEdge);
        }

        final int baseX = 11;
        if (upgradeCapacity > 0) {
            int y = 62 + this.additionalDistance;
            int x = baseX;
            for (int i = 0; i < upgradeCapacity; i++) {
                this.addSlot(new Slot(this.upgrades, 0, x, y - 2));
                if (x == baseX) {
                    x += 21;
                } else {
                    x = baseX;
                    y -= 21;
                }
            }
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(this.playerInventory, j + i * 9 + 9, j * 18 + 28, i * 18 + 126 + this.additionalDistance));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(this.playerInventory, i, i * 18 + 28, 184 + this.additionalDistance));
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

    public <T> @Nullable RocketPartRecipe<?, ?> getRecipe(ResourceKey<Registry<T>> type) {
        if (type.location().equals(RocketRegistries.ROCKET_CONE.location())) {
            return this.getRecipe(0, type);
        } else if (type.location().equals(RocketRegistries.ROCKET_BODY.location())) {
            return this.getRecipe(1, type);
        } else if (type.location().equals(RocketRegistries.ROCKET_FIN.location())) {
            return this.getRecipe(2, type);
        } else if (type.location().equals(RocketRegistries.ROCKET_BOOSTER.location())) {
            return this.getRecipe(3, type);
        } else if (type.location().equals(RocketRegistries.ROCKET_BOTTOM.location())) {
            return this.getRecipe(4, type);
        }
        return null;
    }

    private <T> @Nullable RocketPartRecipe<?, ?> getRecipe(int slot, ResourceKey<Registry<T>> type) {
        ItemStack stack = this.schematics.getItem(slot);
        if (stack.getItem() instanceof Schematic schematic) {
            RocketPartRecipe<?, ?> recipe = schematic.getRecipe(this.recipeRegistry, stack);
            if (recipe != null) {
                if (recipe.output().isFor(type)) {
                    return recipe;
                }
            }
        }
        return null;
    }
}
