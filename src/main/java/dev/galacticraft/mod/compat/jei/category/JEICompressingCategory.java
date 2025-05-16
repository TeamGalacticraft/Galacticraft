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
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.ShapedCompressingRecipe;
import dev.galacticraft.mod.util.Translations;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
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

public class JEICompressingCategory implements IRecipeCategory<CompressingRecipe> {
    private final IDrawable icon;
    private final ICraftingGridHelper craftingGridHelper;

    public JEICompressingCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemStack(new ItemStack(GCBlocks.COMPRESSOR));
        this.craftingGridHelper = helper.createCraftingGridHelper();
    }

    @Override
    public RecipeType<CompressingRecipe> getRecipeType() {
        return GCJEIRecipeTypes.COMPRESSING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(Translations.RecipeCategory.COMPRESSOR);
    }

    @Override
    public int getWidth() {
        return COMPRESSOR_WIDTH;
    }

    @Override
    public int getHeight() {
        return COMPRESSOR_HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CompressingRecipe recipe, IFocusGroup focuses) {
        int width = 0;
        int height = 0;
        if (recipe instanceof ShapedCompressingRecipe shapedRecipe) {
            width = shapedRecipe.getWidth();
            height = shapedRecipe.getHeight();
        }
        this.craftingGridHelper.createAndSetIngredients(builder, recipe.getIngredients(), width, height);

        builder.addSlot(RecipeIngredientRole.OUTPUT, COMPRESSED_X, COMPRESSED_Y)
                .addItemStack(recipe.getResultItem(null)); //fixme
    }

    @Override
    public void draw(CompressingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.blit(RECIPE_VIEWER_DISPLAY_TEXTURE, 0, 0, COMPRESSOR_U, COMPRESSOR_V, COMPRESSOR_WIDTH, COMPRESSOR_HEIGHT);
    }
}
