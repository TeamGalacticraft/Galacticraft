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

package dev.galacticraft.mod.compat.jei.category;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.compat.jei.GCJEIRecipeTypes;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.util.Translations;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class JEIFabricationCategory implements IRecipeCategory<FabricationRecipe> {

    private final IDrawable icon, background;

    public JEIFabricationCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemStack(new ItemStack(GCBlocks.CIRCUIT_FABRICATOR));
        this.background = helper.createDrawable(Constant.ScreenTexture.RECIPE_VEIWER_DISPLAY_TEXTURE, 0, 0, 162, 82);
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
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FabricationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .addItemStack(Items.DIAMOND.getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.INPUT, 55, 47)
                .addItemStack(GCItems.RAW_SILICON.getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.INPUT, 55, 65)
                .addItemStack(GCItems.RAW_SILICON.getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.CATALYST, 109, 47)
                .addItemStack(Items.REDSTONE.getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.CATALYST, 127, 1)
                .addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 145, 65)
                .addItemStack(recipe.getResultItem(null)); //fixme
    }
}
