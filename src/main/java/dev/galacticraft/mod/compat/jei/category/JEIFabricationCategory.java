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
import dev.galacticraft.mod.util.Translations.RecipeCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import static dev.galacticraft.mod.Constant.CircuitFabricator.*;

public class JEIFabricationCategory implements IRecipeCategory<FabricationRecipe> {
    private static final int PROGRESS_BAR_X = PROGRESS_X - RECIPE_VIEWER_X;
    private static final int PROGRESS_BAR_Y = PROGRESS_Y - RECIPE_VIEWER_Y;

    private final IDrawable icon;
    private final JEIFabricationProgressBar progressBar;

    public JEIFabricationCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemStack(new ItemStack(GCBlocks.CIRCUIT_FABRICATOR));
        this.progressBar = new JEIFabricationProgressBar();
    }

    @Override
    public RecipeType<FabricationRecipe> getRecipeType() {
        return GCJEIRecipeTypes.FABRICATION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(RecipeCategory.CIRCUIT_FABRICATOR);
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
    public void setRecipe(IRecipeLayoutBuilder builder, FabricationRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(DIAMOND_X - RECIPE_VIEWER_X, DIAMOND_Y - RECIPE_VIEWER_Y)
                .setStandardSlotBackground()
                .addIngredients(recipe.getIngredients().get(0));
        builder.addInputSlot(SILICON_X_1 - RECIPE_VIEWER_X, SILICON_Y_1 - RECIPE_VIEWER_Y)
                .setStandardSlotBackground()
                .addIngredients(recipe.getIngredients().get(1));
        builder.addInputSlot(SILICON_X_2 - RECIPE_VIEWER_X, SILICON_Y_2 - RECIPE_VIEWER_Y)
                .setStandardSlotBackground()
                .addIngredients(recipe.getIngredients().get(2));
        builder.addInputSlot(REDSTONE_X - RECIPE_VIEWER_X, REDSTONE_Y - RECIPE_VIEWER_Y)
                .setStandardSlotBackground()
                .addIngredients(recipe.getIngredients().get(3));
        builder.addInputSlot(INGREDIENT_X - RECIPE_VIEWER_X, INGREDIENT_Y - RECIPE_VIEWER_Y)
                .setStandardSlotBackground()
                .addIngredients(recipe.getIngredients().get(4));

        builder.addOutputSlot(OUTPUT_X - RECIPE_VIEWER_X, OUTPUT_Y - RECIPE_VIEWER_Y)
                .setStandardSlotBackground()
                .addItemStack(recipe.getResultItem(null)); //fixme
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, FabricationRecipe recipe, IFocusGroup focuses) {
        int processingTime = recipe.getProcessingTime();
        if (processingTime > 0) {
            this.progressBar.setProcessingTime(processingTime);
            Component timeString = Component.translatable(RecipeCategory.JEI_TIME, processingTime / 20);
            builder.addText(timeString, 57, 10)
                    .setPosition(0, 0, this.getWidth(), this.getHeight(), HorizontalAlignment.CENTER, VerticalAlignment.TOP)
                    .setTextAlignment(HorizontalAlignment.CENTER)
                    .setTextAlignment(VerticalAlignment.TOP)
                    .setColor(0xFF808080);
        }
    }

    @Override
    public void draw(FabricationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.blit(SCREEN_TEXTURE, PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_BACKGROUND_U, PROGRESS_BACKGROUND_V, PROGRESS_WIDTH, PROGRESS_HEIGHT);
        this.progressBar.draw(graphics, PROGRESS_BAR_X, PROGRESS_BAR_Y);
    }
}
