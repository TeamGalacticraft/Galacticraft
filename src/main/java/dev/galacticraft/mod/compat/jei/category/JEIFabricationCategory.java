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

package dev.galacticraft.mod.compat.jei.category;

import dev.galacticraft.mod.compat.jei.GCJEIRecipeTypes;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.util.Translations;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import static dev.galacticraft.mod.Constant.RecipeViewer.*;

public class JEIFabricationCategory implements IRecipeCategory<FabricationRecipe> {
    private final IDrawable icon;

    public JEIFabricationCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemStack(new ItemStack(GCBlocks.CIRCUIT_FABRICATOR));
    }

    @Override
    public RecipeType<FabricationRecipe> getRecipeType() {
        return GCJEIRecipeTypes.FABRICATION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(Translations.RecipeCategory.CIRCUIT_FABRICATOR);
    }

    @Override
    public int getWidth() {
        return CIRCUIT_FABRICATOR_WIDTH;
    }

    @Override
    public int getHeight() {
        return CIRCUIT_FABRICATOR_HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FabricationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, DIAMOND_X, DIAMOND_Y)
                .addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, SILICON_X_1, SILICON_Y_1)
                .addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, SILICON_X_2, SILICON_Y_2)
                .addIngredients(recipe.getIngredients().get(2));
        builder.addSlot(RecipeIngredientRole.CATALYST, REDSTONE_X, REDSTONE_Y)
                .addIngredients(recipe.getIngredients().get(3));
        builder.addSlot(RecipeIngredientRole.CATALYST, INGREDIENT_X, INGREDIENT_Y)
                .addIngredients(recipe.getIngredients().get(4));

        builder.addSlot(RecipeIngredientRole.OUTPUT, WAFER_X, WAFER_Y)
                .addItemStack(recipe.getResultItem(null)); //fixme
    }

    @Override
    public void draw(FabricationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.blit(RECIPE_VIEWER_DISPLAY_TEXTURE, 0, 0, CIRCUIT_FABRICATOR_U, CIRCUIT_FABRICATOR_V, CIRCUIT_FABRICATOR_WIDTH, CIRCUIT_FABRICATOR_HEIGHT);
    }
}
