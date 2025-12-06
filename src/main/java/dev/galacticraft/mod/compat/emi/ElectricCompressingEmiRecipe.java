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

package dev.galacticraft.mod.compat.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.ShapedCompressingRecipe;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

import static dev.galacticraft.mod.Constant.ElectricCompressor.*;
import static dev.galacticraft.mod.util.Translations.RecipeCategory.EMI_TIME;

public class ElectricCompressingEmiRecipe extends BasicEmiRecipe {
    private static final int PROGRESS_BAR_X = PROGRESS_X - RECIPE_VIEWER_X;
    private static final int PROGRESS_BAR_Y = PROGRESS_Y - RECIPE_VIEWER_Y;

    public final boolean shapeless;
    public final int width;
    public final int height;
    public final int time;
    private static final EmiIngredient EMPTY = EmiIngredient.of(Ingredient.EMPTY);

    public ElectricCompressingEmiRecipe(RecipeHolder<CompressingRecipe> holder) {
        super(GalacticraftEmiPlugin.ELECTRIC_COMPRESSING, holder.id().withPrefix("/electric_"), RECIPE_VIEWER_WIDTH, RECIPE_VIEWER_HEIGHT);
        CompressingRecipe recipe = holder.value();
        for (Ingredient ingredient : recipe.getIngredients()) {
            this.inputs.add(EmiIngredient.of(ingredient));
        }
        this.outputs.add(EmiStack.of(recipe.getResultItem(null)));
        if (recipe instanceof ShapedCompressingRecipe shapedRecipe) {
            this.shapeless = false;
            this.width = shapedRecipe.getWidth();
            this.height = shapedRecipe.getHeight();
        } else {
            this.shapeless = true;
            this.width = 3;
            this.height = 3;
        }
        this.time = (int) (recipe.getTime() / 1.5F);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // Add the background texture
        widgets.addTexture(SCREEN_TEXTURE, PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_WIDTH, PROGRESS_HEIGHT, PROGRESS_BACKGROUND_U, PROGRESS_BACKGROUND_V);

        if (shapeless) {
            widgets.addTexture(EmiTexture.SHAPELESS, OUTPUT_X_1 - RECIPE_VIEWER_X - 22, 0);
        }

        widgets.addAnimatedTexture(SCREEN_TEXTURE, PROGRESS_BAR_X - 1, PROGRESS_BAR_Y, PROGRESS_WIDTH + 2, PROGRESS_HEIGHT, PROGRESS_U - 1, PROGRESS_V, this.time * 50, true, false, false).tooltip((mx, my) -> {
            return List.of(ClientTooltipComponent.create(Component.translatable(EMI_TIME, this.time / 20.0F).getVisualOrderText()));
        });

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int i = x + this.width * y;
                EmiIngredient ingredient = (x < this.width && y < this.height && i < this.inputs.size()) ? this.inputs.get(i) : EMPTY;
                widgets.addSlot(ingredient, GRID_X - RECIPE_VIEWER_X - 1 + (x * 18), GRID_Y - RECIPE_VIEWER_Y - 1 + (y * 18));
            }
        }

        // Adds an output slot on the right
        // Note that output slots need to call `recipeContext` to inform EMI about their recipe context
        // This includes being able to resolve recipe trees, favorite stacks with recipe context, and more
        widgets.addSlot(this.outputs.get(0), OUTPUT_X_1 - RECIPE_VIEWER_X - 5, OUTPUT_Y_1 - RECIPE_VIEWER_Y - 5).large(true).recipeContext(this);
        widgets.addSlot(EMPTY, OUTPUT_X_2 - RECIPE_VIEWER_X - 5, OUTPUT_Y_2 - RECIPE_VIEWER_Y - 5).large(true).recipeContext(this);
    }
}