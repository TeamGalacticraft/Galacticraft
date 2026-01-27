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

package dev.galacticraft.mod.compat.jei;

import dev.galacticraft.mod.content.item.CannedFoodItem;
import dev.galacticraft.mod.content.item.GCItems;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CanningRecipeManagerPlugin implements ISimpleRecipeManagerPlugin<CanningRecipe> {
    @Override
    public boolean isHandledInput(ITypedIngredient<?> input) {
        if (input.getIngredient() instanceof ItemStack itemStack) {
            return CannedFoodItem.isEmptyCan(itemStack) || CannedFoodItem.canAddToCan(itemStack.getItem());
        }
        return false;
    }

    @Override
    public boolean isHandledOutput(ITypedIngredient<?> output) {
        if (output.getIngredient() instanceof ItemStack itemStack) {
            return CannedFoodItem.isCannedFoodItem(itemStack);
        }
        return false;
    }

    @Override
    public List<CanningRecipe> getRecipesForInput(ITypedIngredient<?> input) {
        if (input.getIngredient() instanceof ItemStack itemStack) {
            if (CannedFoodItem.isEmptyCan(itemStack)) {
                return this.getAllRecipes();
            } else if (CannedFoodItem.canAddToCan(itemStack.getItem())) {
                // Create new canned food item with empty components
                ItemStack cannedFoodItem = GCItems.CANNED_FOOD.getDefaultInstance();
                // Add the default itemstack of the edible item into the canned foods components
                CannedFoodItem.add(cannedFoodItem, itemStack.copyWithCount(CannedFoodItem.MAX_FOOD));
                return List.of(new CanningRecipe(cannedFoodItem));
            }
        }
        return List.of();
    }

    @Override
    public List<CanningRecipe> getRecipesForOutput(ITypedIngredient<?> output) {
        if (output.getIngredient() instanceof ItemStack itemStack) {
            return List.of(new CanningRecipe(itemStack));
        }
        return List.of();
    }

    @Override
    public List<CanningRecipe> getAllRecipes() {
        return CannedFoodItem.getDefaultCannedFoods().stream().map(itemStack -> new CanningRecipe(itemStack)).toList();
    }
}
