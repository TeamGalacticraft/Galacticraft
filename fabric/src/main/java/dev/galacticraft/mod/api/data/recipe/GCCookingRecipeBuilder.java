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

package dev.galacticraft.mod.api.data.recipe;

import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public class GCCookingRecipeBuilder extends GCRecipeBuilder<GCCookingRecipeBuilder> {
    private final CookingBookCategory bookCategory;
    private final Ingredient ingredient;
    private final float experience;
    private final int cookingTime;
    private final AbstractCookingRecipe.Factory<?> factory;

    protected GCCookingRecipeBuilder(RecipeCategory category, CookingBookCategory cookingBookCategory, ItemLike result, Ingredient ingredient, float f, int n, AbstractCookingRecipe.Factory<?> factory) {
        super("", category, result, 1);
        this.bookCategory = cookingBookCategory;
        this.ingredient = ingredient;
        this.experience = f;
        this.cookingTime = n;
        this.factory = factory;
    }

    public static <T extends AbstractCookingRecipe> GCCookingRecipeBuilder generic(Ingredient ingredient, RecipeCategory category, ItemLike result, float f, int n, RecipeSerializer<T> recipeSerializer, AbstractCookingRecipe.Factory<T> factory) {
        return new GCCookingRecipeBuilder(category, GCCookingRecipeBuilder.determineRecipeCategory(recipeSerializer, result), result, ingredient, f, n, factory);
    }

    public static GCCookingRecipeBuilder campfireCooking(Ingredient ingredient, RecipeCategory category, ItemLike result, float f, int n) {
        return new GCCookingRecipeBuilder(category, CookingBookCategory.FOOD, result, ingredient, f, n, CampfireCookingRecipe::new);
    }

    public static GCCookingRecipeBuilder blasting(Ingredient ingredient, RecipeCategory category, ItemLike result, float f, int n) {
        return new GCCookingRecipeBuilder(category, GCCookingRecipeBuilder.determineBlastingRecipeCategory(result), result, ingredient, f, n, BlastingRecipe::new);
    }

    public static GCCookingRecipeBuilder smelting(Ingredient ingredient, RecipeCategory category, ItemLike result, float f, int n) {
        return new GCCookingRecipeBuilder(category, GCCookingRecipeBuilder.determineSmeltingRecipeCategory(result), result, ingredient, f, n, SmeltingRecipe::new);
    }

    public static GCCookingRecipeBuilder smoking(Ingredient ingredient, RecipeCategory category, ItemLike result, float f, int n) {
        return new GCCookingRecipeBuilder(category, CookingBookCategory.FOOD, result, ingredient, f, n, SmokingRecipe::new);
    }

    @Override
    public Recipe<?> createRecipe(ResourceLocation id) {
        this.ensureValid(id);
        String groupName = Objects.requireNonNullElse(this.group, "");
        ItemStack itemStack = new ItemStack(this.result, this.count);
        return this.factory.create(groupName, this.bookCategory, this.ingredient, itemStack, this.experience, this.cookingTime);
    }

    private static CookingBookCategory determineSmeltingRecipeCategory(ItemLike itemLike) {
        if (itemLike.asItem().components().has(DataComponents.FOOD)) {
            return CookingBookCategory.FOOD;
        }
        if (itemLike.asItem() instanceof BlockItem) {
            return CookingBookCategory.BLOCKS;
        }
        return CookingBookCategory.MISC;
    }

    private static CookingBookCategory determineBlastingRecipeCategory(ItemLike itemLike) {
        if (itemLike.asItem() instanceof BlockItem) {
            return CookingBookCategory.BLOCKS;
        }
        return CookingBookCategory.MISC;
    }

    private static CookingBookCategory determineRecipeCategory(RecipeSerializer<? extends AbstractCookingRecipe> recipeSerializer, ItemLike itemLike) {
        if (recipeSerializer == RecipeSerializer.SMELTING_RECIPE) {
            return GCCookingRecipeBuilder.determineSmeltingRecipeCategory(itemLike);
        }
        if (recipeSerializer == RecipeSerializer.BLASTING_RECIPE) {
            return GCCookingRecipeBuilder.determineBlastingRecipeCategory(itemLike);
        }
        if (recipeSerializer == RecipeSerializer.SMOKING_RECIPE || recipeSerializer == RecipeSerializer.CAMPFIRE_COOKING_RECIPE) {
            return CookingBookCategory.FOOD;
        }
        throw new IllegalStateException("Unknown cooking recipe type");
    }

    private void ensureValid(ResourceLocation resourceLocation) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + String.valueOf(resourceLocation));
        }
    }
}
