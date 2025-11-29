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

package dev.galacticraft.mod.api.data.recipe;

import dev.galacticraft.mod.recipe.ShapelessCompressingRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GCShapelessRecipeBuilder extends GCCraftingRecipeBuilder {
    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private int time;

    protected GCShapelessRecipeBuilder(RecipeCategory category, ItemLike result, int count) {
        super(category, result, count);
    }

    public static GCShapelessRecipeBuilder crafting(RecipeCategory category, ItemLike result) {
        return new GCShapelessRecipeBuilder(category, result, 1);
    }

    public static GCShapelessRecipeBuilder crafting(RecipeCategory category, ItemLike result, int count) {
        return new GCShapelessRecipeBuilder(category, result, count);
    }

    public static GCShapelessRecipeBuilder compressing(ItemLike result) {
        return new GCShapelessRecipeBuilder(null, result, 1);
    }

    public static GCShapelessRecipeBuilder compressing(ItemLike result, int count) {
        return new GCShapelessRecipeBuilder(null, result, count);
    }

    public GCShapelessRecipeBuilder requires(TagKey<Item> tagKey) {
        return this.requires(Ingredient.of(tagKey));
    }

    public GCShapelessRecipeBuilder requires(ItemLike itemLike) {
        return this.requires(itemLike, 1);
    }

    public GCShapelessRecipeBuilder requires(ItemLike itemLike, int count) {
        for (int i = 0; i < count; ++i) {
            this.requires(Ingredient.of(itemLike));
        }

        return this;
    }

    public GCShapelessRecipeBuilder requires(Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    public GCShapelessRecipeBuilder requires(Ingredient ingredient, int count) {
        for (int i = 0; i < count; ++i) {
            this.ingredients.add(ingredient);
        }

        return this;
    }

    public GCShapelessRecipeBuilder time(int time) {
        this.time = time;
        return this;
    }

    @Override
    public GCShapelessRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public GCShapelessRecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public GCShapelessRecipeBuilder emiDefault(boolean emiDefault) {
        this.emiDefault = emiDefault;
        return this;
    }

    @Override
    public Recipe<?> createRecipe(ResourceLocation id) {
        String groupName = Objects.requireNonNullElse(this.group, "");
        ItemStack itemStack = new ItemStack(this.result, this.count);
        if (this.category != null) {
            return new ShapelessRecipe(groupName, RecipeBuilder.determineBookCategory(this.category), itemStack, this.ingredients);
        } else {
            return new ShapelessCompressingRecipe(groupName, itemStack, this.ingredients, this.time > 1 ? this.time : 200);
        }
    }
}
