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
import dev.galacticraft.mod.tag.GCItemTags;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.BannerBlock;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FlagRecipeManagerPlugin implements ISimpleRecipeManagerPlugin<RecipeHolder<CraftingRecipe>> {
    @Override
    public boolean isHandledInput(ITypedIngredient<?> input) {
        if (input.getIngredient() instanceof ItemStack stack) {
            return stack.is(GCItems.STEEL_POLE) || stack.is(ItemTags.BANNERS);
        }
        return false;
    }

    @Override
    public boolean isHandledOutput(ITypedIngredient<?> output) {
        return output.getIngredient() instanceof ItemStack stack && stack.is(GCItemTags.FLAGS);
    }

    @Override
    public @NotNull List<RecipeHolder<CraftingRecipe>> getRecipesForInput(ITypedIngredient<?> input) {
        if (input.getIngredient() instanceof ItemStack stack) {
            if (stack.getItem() instanceof BannerItem) {
                return List.of(createRecipe(stack, FlagItem.fromBanner(stack)));
            } else if (stack.is(GCItems.STEEL_POLE)) {
                return defaultRecipes();
            }
        }
        return List.of();
    }

    @Override
    public @NotNull List<RecipeHolder<CraftingRecipe>> getRecipesForOutput(ITypedIngredient<?> output) {
        if (output.getIngredient() instanceof ItemStack stack) {
            if (stack.is(GCItemTags.FLAGS)) {
                return List.of(createRecipe(FlagItem.toBanner(stack), stack));
            }
        }
        return List.of();
    }

    @Override
    public @NotNull List<RecipeHolder<CraftingRecipe>> getAllRecipes() {
        return defaultRecipes();
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
        ResourceLocation id = flag.getItemHolder().unwrapKey().map(ResourceKey::location).orElse(Constant.id(Constant.Recipe.FLAG));
        return new RecipeHolder<>(id, recipe);
    }

    public static List<RecipeHolder<CraftingRecipe>> defaultRecipes() {
        return Arrays.stream(DyeColor.values())
                .map(color -> createRecipe(new ItemStack(BannerBlock.byColor(color)), new ItemStack(GCItems.FLAGS.get(color))))
                .toList();
    }
}
