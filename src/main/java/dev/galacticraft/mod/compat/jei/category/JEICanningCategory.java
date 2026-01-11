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

import dev.galacticraft.mod.compat.jei.CanningRecipe;
import dev.galacticraft.mod.compat.jei.GCJEIRecipeTypes;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.util.Translations.RecipeCategory;
import mezz.jei.api.constants.VanillaTypes;
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

import java.util.List;

import static dev.galacticraft.mod.Constant.FoodCanner.*;

public class JEICanningCategory implements IRecipeCategory<CanningRecipe> {
    private static final int PROGRESS_BAR_X = PROGRESS_X - RECIPE_VIEWER_X;
    private static final int PROGRESS_BAR_Y = PROGRESS_Y - RECIPE_VIEWER_Y;

    private final IDrawable icon;
    private final JEICanningProgressBar progressBar;

    public JEICanningCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemStack(new ItemStack(GCBlocks.FOOD_CANNER));
        this.progressBar = new JEICanningProgressBar();
    }

    @Override
    public RecipeType<CanningRecipe> getRecipeType() {
        return GCJEIRecipeTypes.CANNING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(RecipeCategory.CANNING);
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
    public void setRecipe(IRecipeLayoutBuilder builder, CanningRecipe recipe, IFocusGroup focuses) {
        List<ItemStack> ingredients = recipe.getContents();
        int n = ingredients.size();
        this.progressBar.setSlotsUsed(n);

        builder.addInputSlot(INPUT_X - RECIPE_VIEWER_X, INPUT_Y - RECIPE_VIEWER_Y)
                .setStandardSlotBackground()
                .addItemStack(GCItems.EMPTY_CAN.getDefaultInstance());

        for (int i = 0; i < 16; i++) {
            builder.addInputSlot(GRID_X - RECIPE_VIEWER_X + 18 * (i % 4), GRID_Y - RECIPE_VIEWER_Y + 18 * (i / 4))
                    .setStandardSlotBackground()
                    .addItemStack(i < n ? ingredients.get(i) : ItemStack.EMPTY);
        }

        var ingredientRenderer = this.progressBar.getIngredientRenderer(GCItems.CANNED_FOOD.getDefaultInstance());

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, CURRENT_X - RECIPE_VIEWER_X, CURRENT_Y - RECIPE_VIEWER_Y)
                .setStandardSlotBackground()
                .addItemStack(recipe.cannedFood())
                .setCustomRenderer(VanillaTypes.ITEM_STACK, ingredientRenderer)
                .addRichTooltipCallback(ingredientRenderer);
        builder.addOutputSlot(OUTPUT_X - RECIPE_VIEWER_X, OUTPUT_Y - RECIPE_VIEWER_Y)
                .setStandardSlotBackground()
                .addItemStack(recipe.cannedFood());
    }

    @Override
    public void draw(CanningRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.blit(SCREEN_TEXTURE, PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_BACKGROUND_U, PROGRESS_BACKGROUND_V, PROGRESS_WIDTH, PROGRESS_HEIGHT);
        this.progressBar.draw(graphics, PROGRESS_BAR_X, PROGRESS_BAR_Y);
    }
}
