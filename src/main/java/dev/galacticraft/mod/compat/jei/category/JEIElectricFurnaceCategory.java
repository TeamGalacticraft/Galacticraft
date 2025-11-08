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
import dev.galacticraft.mod.util.Translations.RecipeCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
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
import net.minecraft.world.item.crafting.SmeltingRecipe;

import static dev.galacticraft.mod.Constant.ElectricFurnace.*;

public class JEIElectricFurnaceCategory implements IRecipeCategory<SmeltingRecipe> {
    private static final int PROGRESS_BAR_X = PROGRESS_X - JEI_X;
    private static final int PROGRESS_BAR_Y = PROGRESS_Y - JEI_Y;

    private final IDrawable icon;
    private final IDrawableStatic arrow;
    private final IGuiHelper helper;

    public JEIElectricFurnaceCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemStack(new ItemStack(GCBlocks.ELECTRIC_FURNACE));
        this.arrow = helper.createDrawable(SCREEN_TEXTURE, PROGRESS_U, PROGRESS_V, PROGRESS_WIDTH, PROGRESS_HEIGHT);
        this.helper = helper;
    }

    @Override
    public RecipeType<SmeltingRecipe> getRecipeType() {
        return GCJEIRecipeTypes.ELECTRIC_SMELTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(RecipeCategory.ELECTRIC_FURNACE);
    }

    @Override
    public int getWidth() {
        return JEI_WIDTH;
    }

    @Override
    public int getHeight() {
        return JEI_HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SmeltingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(INPUT_X - JEI_X, INPUT_Y - JEI_Y)
                .setStandardSlotBackground()
                .addIngredients(recipe.getIngredients().get(0));
        builder.addOutputSlot(OUTPUT_X - JEI_X, OUTPUT_Y - JEI_Y)
                .setOutputSlotBackground()
                .addItemStack(recipe.getResultItem(null)); //fixme
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, SmeltingRecipe recipe, IFocusGroup focuses) {
        int cookingTime = recipe.getCookingTime();
        if (cookingTime > 0) {
            Component timeString = Component.translatable(RecipeCategory.JEI_TIME, cookingTime / 20);
            builder.addText(timeString, PROGRESS_WIDTH, 10)
                    .setPosition(PROGRESS_BAR_X, 0, PROGRESS_WIDTH, this.getHeight(), HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM)
                    .setTextAlignment(HorizontalAlignment.CENTER)
                    .setTextAlignment(VerticalAlignment.BOTTOM)
                    .setColor(0xFF808080);

            builder.addDrawable(this.helper.createAnimatedDrawable(this.arrow, cookingTime, IDrawableAnimated.StartDirection.LEFT, false))
                    .setPosition(PROGRESS_BAR_X, PROGRESS_BAR_Y);
        }

        // float experience = recipe.getExperience();
        // if (experience > 0.0F) {
        //     Component xpString = Component.translatable(RecipeCategory.JEI_XP, experience);
        //     builder.addText(xpString, PROGRESS_WIDTH + 8, 10)
        //             .setPosition(PROGRESS_BAR_X, 0, PROGRESS_WIDTH, this.getHeight(), HorizontalAlignment.CENTER, VerticalAlignment.TOP)
        //             .setTextAlignment(HorizontalAlignment.CENTER)
        //             .setTextAlignment(VerticalAlignment.TOP)
        //             .setColor(0xFF808080);
        // }
    }

    @Override
    public void draw(SmeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.blit(SCREEN_TEXTURE, PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_BACKGROUND_U, PROGRESS_BACKGROUND_V, PROGRESS_WIDTH, PROGRESS_HEIGHT);
    }
}
