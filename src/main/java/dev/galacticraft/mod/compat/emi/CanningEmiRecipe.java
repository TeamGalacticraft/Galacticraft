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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import dev.galacticraft.mod.content.item.GCItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import static dev.galacticraft.mod.Constant.FoodCanner.*;

public class CanningEmiRecipe extends BasicEmiRecipe {
    private static final EmiIngredient EMPTY = EmiIngredient.of(Ingredient.EMPTY);

    public static ResourceLocation canningSyntheticId(ItemStack cannedFood) {
        return Constant.id("/canning/" + CannedFoodItem.getFirst(cannedFood).getItem().toString().replace(":", "_"));
    }

    public CanningEmiRecipe(ItemStack cannedFood) {
        super(GalacticraftEmiPlugin.CANNING, canningSyntheticId(cannedFood), RECIPE_VIEWER_WIDTH, RECIPE_VIEWER_HEIGHT);
        this.inputs.add(EmiStack.of(GCItems.EMPTY_CAN.getDefaultInstance()));
        for (ItemStack itemStack : CannedFoodItem.getContents(cannedFood)) {
            this.inputs.add(EmiStack.of(itemStack));
        }
        this.outputs.add(EmiStack.of(cannedFood));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // Add the background texture
        widgets.addTexture(SCREEN_TEXTURE, PROGRESS_X - RECIPE_VIEWER_X, PROGRESS_Y - RECIPE_VIEWER_Y, PROGRESS_WIDTH, PROGRESS_HEIGHT, PROGRESS_BACKGROUND_U, PROGRESS_BACKGROUND_V);

        int n = this.inputs.size() - 1;
        CanningProgressEmiWidget progressWidget = new CanningProgressEmiWidget(PROGRESS_X - RECIPE_VIEWER_X, PROGRESS_Y - RECIPE_VIEWER_Y, n);
        widgets.add(progressWidget);

        for (int i = 0; i < 16; i++) {
            widgets.addSlot(i < n ? this.inputs.get(i + 1) : EMPTY, GRID_X + 18 * (i % 4) - RECIPE_VIEWER_X - 1, GRID_Y + 18 * (i / 4) - RECIPE_VIEWER_Y - 1);
        }

        widgets.addSlot(this.inputs.get(0), INPUT_X - RECIPE_VIEWER_X - 1, INPUT_Y - RECIPE_VIEWER_Y - 1);
        widgets.add(progressWidget.getSlotWidget(
                this.outputs.get(0),
                EmiStack.of(GCItems.CANNED_FOOD.getDefaultInstance()),
                CURRENT_X - RECIPE_VIEWER_X - 1,
                CURRENT_Y - RECIPE_VIEWER_Y - 1
        ));
        widgets.addSlot(this.outputs.get(0), OUTPUT_X - RECIPE_VIEWER_X - 1, OUTPUT_Y - RECIPE_VIEWER_Y - 1).recipeContext(this);
    }
}