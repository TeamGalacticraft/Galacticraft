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

package dev.galacticraft.mod.compat.jei.category;

import dev.galacticraft.mod.client.gui.screen.ingame.RocketWorkbenchScreen;
import dev.galacticraft.mod.compat.jei.GCJEIRecipeTypes;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.entity.vehicle.RocketEntity;
import dev.galacticraft.mod.recipe.RocketRecipe;
import dev.galacticraft.mod.util.Translations;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import static dev.galacticraft.mod.Constant.RocketWorkbench.*;

public class JEIRocketCategory implements IRecipeCategory<RocketRecipe> {
    private static final int CHEST_BORDER_X = CHEST_X - 2 - RECIPE_VIEWER_X;
    private static final int CHEST_BORDER_Y = CHEST_Y - 2 - RECIPE_VIEWER_Y;
    private static final int OUTPUT_BORDER_X = OUTPUT_X - OUTPUT_X_OFFSET - RECIPE_VIEWER_X;
    private static final int OUTPUT_BORDER_Y = OUTPUT_Y - OUTPUT_Y_OFFSET - RECIPE_VIEWER_Y;
    private static final int PREVIEW_BACKGROUND_X = PREVIEW_X - RECIPE_VIEWER_X;
    private static final int PREVIEW_BACKGROUND_Y = PREVIEW_Y - RECIPE_VIEWER_Y;
    private static final int PREVIEW_ROCKET_X = ROCKET_X - RECIPE_VIEWER_X;
    private static final int PREVIEW_ROCKET_Y = ROCKET_Y - RECIPE_VIEWER_Y;

    public static RocketEntity ROCKET_ENTITY = new RocketEntity(GCEntityTypes.ROCKET, Minecraft.getInstance().level);

    private final IDrawable icon, chestSprite;

    public JEIRocketCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemStack(new ItemStack(GCBlocks.ROCKET_WORKBENCH));
        this.chestSprite = helper.createDrawable(SCREEN_TEXTURE, CHEST_U + 2, CHEST_V + 2, 16, 16);
        ROCKET_ENTITY.setYRot(90.0F);
    }

    @Override
    public RecipeType<RocketRecipe> getRecipeType() {
        return GCJEIRecipeTypes.ROCKET;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(Translations.RecipeCategory.ROCKET_WORKBENCH);
    }

    @Override
    public int getWidth() {
        return RECIPE_VIEWER_WIDTH;
    }

    @Override
    public int getHeight() {
        return RECIPE_VIEWER_HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RocketRecipe recipe, IFocusGroup focuses) {
        for (RocketRecipe.RocketSlotData data : RocketRecipe.slotData(recipe.bodyHeight(), !recipe.boosters().isEmpty())) {
            Ingredient ingredient = switch(data.partType()) {
                case CONE -> recipe.cone();
                case BODY -> recipe.body();
                case BOOSTER -> recipe.boosters();
                case FIN -> recipe.fins();
                case ENGINE -> recipe.engine();
                default -> Ingredient.EMPTY;
            };

            IRecipeSlotBuilder slotBuilder = builder
                    .addInputSlot(data.x() - RECIPE_VIEWER_X, data.y() - RECIPE_VIEWER_Y)
                    .addIngredients(ingredient)
                    .setStandardSlotBackground();

            if (data.mirror()) {
                slotBuilder.setCustomRenderer(VanillaTypes.ITEM_STACK, MirroredIngredientRenderer.INSTANCE);
            }
        }

        // Storage
        IRecipeSlotBuilder storageSlot = builder.addInputSlot(CHEST_X - RECIPE_VIEWER_X, CHEST_Y - RECIPE_VIEWER_Y)
                    .addIngredients(recipe.storage());
        if (recipe.storage() == Ingredient.EMPTY) {
            storageSlot.setBackground(this.chestSprite, 0, 0);
        } else {
            storageSlot.setStandardSlotBackground();
        }

        // Output
        builder.addOutputSlot(OUTPUT_X - RECIPE_VIEWER_X, OUTPUT_Y - RECIPE_VIEWER_Y)
                .addItemStack(recipe.getResultItem(null)); //fixme
    }

    @Override
    public void draw(RocketRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.blit(SCREEN_TEXTURE, CHEST_BORDER_X, CHEST_BORDER_Y, CHEST_U, CHEST_V, CHEST_WIDTH, CHEST_HEIGHT);
        graphics.blit(SCREEN_TEXTURE, OUTPUT_BORDER_X, OUTPUT_BORDER_Y, OUTPUT_U, OUTPUT_V, OUTPUT_WIDTH, OUTPUT_HEIGHT);
        graphics.blit(SCREEN_TEXTURE, PREVIEW_BACKGROUND_X, PREVIEW_BACKGROUND_Y, PREVIEW_U, PREVIEW_V, PREVIEW_WIDTH, PREVIEW_HEIGHT);

        ROCKET_ENTITY.setYRot(ROCKET_ENTITY.getYRot() + 0.2F);
        RocketWorkbenchScreen.renderEntityInInventory(graphics, PREVIEW_ROCKET_X, PREVIEW_ROCKET_Y, 15, SmithingScreen.ARMOR_STAND_ANGLE, null, ROCKET_ENTITY);
    }
}
