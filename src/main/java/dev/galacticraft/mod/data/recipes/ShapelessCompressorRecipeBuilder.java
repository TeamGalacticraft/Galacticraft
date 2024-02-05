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

package dev.galacticraft.mod.data.recipes;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.galacticraft.mod.recipe.GCRecipes;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ShapelessCompressorRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final int count;
    private final List<Ingredient> ingredients = Lists.<Ingredient>newArrayList();

    public ShapelessCompressorRecipeBuilder(ItemLike itemLike, int i) {
        this.result = itemLike.asItem();
        this.count = i;
    }

    public static ShapelessCompressorRecipeBuilder shapeless(ItemLike itemLike) {
        return new ShapelessCompressorRecipeBuilder(itemLike, 1);
    }

    public static ShapelessCompressorRecipeBuilder shapeless(ItemLike itemLike, int i) {
        return new ShapelessCompressorRecipeBuilder(itemLike, i);
    }

    public ShapelessCompressorRecipeBuilder requires(TagKey<Item> tagKey) {
        return this.requires(Ingredient.of(tagKey));
    }

    public ShapelessCompressorRecipeBuilder requires(ItemLike itemLike) {
        return this.requires(itemLike, 1);
    }

    public ShapelessCompressorRecipeBuilder requires(ItemLike itemLike, int i) {
        for (var j = 0; j < i; ++j) {
            this.requires(Ingredient.of(itemLike));
        }

        return this;
    }

    public ShapelessCompressorRecipeBuilder requires(Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    public ShapelessCompressorRecipeBuilder requires(Ingredient ingredient, int i) {
        for (var j = 0; j < i; ++j) {
            this.ingredients.add(ingredient);
        }

        return this;
    }

    @Override
    public ShapelessCompressorRecipeBuilder unlockedBy(String string, CriterionTriggerInstance criterionTriggerInstance) {
        return this;
    }

    @Override
    public ShapelessCompressorRecipeBuilder group(@Nullable String string) {
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation recipeId) {
        consumer.accept(new Result(recipeId.withPrefix("compressing/"), this.ingredients, this.result, this.count));
    }

    public static class Result implements FinishedRecipe {

        private final ResourceLocation recipeId;
        private final List<Ingredient> ingredients;
        private final Item result;
        private final int count;

        public Result(ResourceLocation recipeId, List<Ingredient> ingredients, Item result, int count) {
            this.ingredients = ingredients;
            this.recipeId = recipeId;
            this.result = result;
            this.count = count;
        }

        @Override
        public void serializeRecipeData(JsonObject jsonRecipe) {
            var jsonIngredients = new JsonArray();

            for(var ingredient : this.ingredients) {
                jsonIngredients.add(ingredient.toJson());
            }

            jsonRecipe.add("ingredients", jsonIngredients);

            var itemJson = new JsonObject();
            itemJson.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
            if (this.count > 1) {
                itemJson.addProperty("count", this.count);
            }

            jsonRecipe.add("result", itemJson);
        }

        @Override
        public ResourceLocation getId() {
            return this.recipeId;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return GCRecipes.SHAPELESS_COMPRESSING_SERIALIZER;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
