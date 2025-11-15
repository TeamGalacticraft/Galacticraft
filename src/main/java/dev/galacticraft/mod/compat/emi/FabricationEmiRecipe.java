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
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import static dev.galacticraft.mod.Constant.CircuitFabricator.*;

public class FabricationEmiRecipe extends BasicEmiRecipe {
    public final int processingTime;

    public FabricationEmiRecipe(RecipeHolder<FabricationRecipe> holder) {
        super(GalacticraftEmiPlugin.FABRICATION, holder.id(), RECIPE_VIEWER_WIDTH, RECIPE_VIEWER_HEIGHT);
        FabricationRecipe recipe = holder.value();
        for (Ingredient ingredient : recipe.getIngredients()) {
            this.inputs.add(EmiIngredient.of(ingredient));
        }
        this.outputs.add(EmiStack.of(recipe.getResultItem(null)));
        this.processingTime = recipe.getProcessingTime();
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // Add the background texture
        widgets.addTexture(SCREEN_TEXTURE, PROGRESS_X - RECIPE_VIEWER_X, PROGRESS_Y - RECIPE_VIEWER_Y, PROGRESS_WIDTH, PROGRESS_HEIGHT, PROGRESS_BACKGROUND_U, PROGRESS_BACKGROUND_V);
        widgets.add(new FabricationProgressEmiWidget(PROGRESS_X - RECIPE_VIEWER_X, PROGRESS_Y - RECIPE_VIEWER_Y, this.processingTime));

        widgets.addSlot(this.inputs.get(0), DIAMOND_X - RECIPE_VIEWER_X - 1, DIAMOND_Y - RECIPE_VIEWER_Y - 1);
        widgets.addSlot(this.inputs.get(1), SILICON_X_1 - RECIPE_VIEWER_X - 1, SILICON_Y_1 - RECIPE_VIEWER_Y - 1);
        widgets.addSlot(this.inputs.get(2), SILICON_X_2 - RECIPE_VIEWER_X - 1, SILICON_Y_2 - RECIPE_VIEWER_Y - 1);
        widgets.addSlot(this.inputs.get(3), REDSTONE_X - RECIPE_VIEWER_X - 1, REDSTONE_Y - RECIPE_VIEWER_Y - 1);
        widgets.addSlot(this.inputs.get(4), INGREDIENT_X - RECIPE_VIEWER_X - 1, INGREDIENT_Y - RECIPE_VIEWER_Y - 1);

        // Adds an output slot on the right
        // Note that output slots need to call `recipeContext` to inform EMI about their recipe context
        // This includes being able to resolve recipe trees, favorite stacks with recipe context, and more
        widgets.addSlot(this.outputs.get(0), OUTPUT_X - RECIPE_VIEWER_X - 1, OUTPUT_Y - RECIPE_VIEWER_Y - 1).recipeContext(this);
    }
}