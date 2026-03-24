/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.item.FlagItem;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.FlagRecipe;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FlagRecipeManagerPlugin implements ISimpleRecipeManagerPlugin<RecipeHolder<CraftingRecipe>> {
    @Override
    public boolean isHandledInput(ITypedIngredient<?> input) {
        if (input.getIngredient() instanceof ItemStack stack) {
            return stack.is(GCItems.STEEL_POLE) || !FlagRecipe.invalidBanner(stack);
        }
        return false;
    }

    @Override
    public boolean isHandledOutput(ITypedIngredient<?> output) {
        if (output.getIngredient() instanceof ItemStack stack) {
            return stack.getItem() instanceof FlagItem;
        }
        return false;
    }

    @Override
    public @NotNull List<RecipeHolder<CraftingRecipe>> getRecipesForInput(ITypedIngredient<?> input) {
        if (input.getIngredient() instanceof ItemStack stack) {
            if (stack.getItem() instanceof BannerItem) {
                return List.of(createRecipe(stack, FlagItem.fromBanner(stack)));
            } else if (stack.is(GCItems.STEEL_POLE)) {
                return List.of(defaultRecipe());
            }
        }
        return List.of();
    }

    @Override
    public @NotNull List<RecipeHolder<CraftingRecipe>> getRecipesForOutput(ITypedIngredient<?> output) {
        if (output.getIngredient() instanceof ItemStack stack) {
            if (stack.getItem() instanceof FlagItem) {
                return List.of(createRecipe(FlagItem.toBanner(stack), stack));
            }
        }
        return List.of();
    }

    @Override
    public @NotNull List<RecipeHolder<CraftingRecipe>> getAllRecipes() {
        return List.of(defaultRecipe());
    }

    public static RecipeHolder<CraftingRecipe> createRecipe(ItemStack banner, ItemStack flag) {
        if (banner.getCount() > 1) {
            banner = banner.copyWithCount(1);
        }

        Map<Character, Ingredient> key = Map.of(
                '|', Ingredient.of(GCItems.STEEL_POLE),
                'B', Ingredient.of(banner)
        );
        List<String> pattern = List.of(
                "|B",
                "| ",
                "| "
        );
        ShapedRecipe recipe = new ShapedRecipe(Constant.Recipe.FLAG, CraftingBookCategory.BUILDING, ShapedRecipePattern.of(key, pattern), flag);
        return new RecipeHolder<>(Constant.id(Constant.Recipe.FLAG), recipe);
    }

    public static RecipeHolder<CraftingRecipe> defaultRecipe() {
        return createRecipe(new ItemStack(Items.WHITE_BANNER), new ItemStack(GCItems.FLAGS.get(DyeColor.WHITE)));
    }
}
