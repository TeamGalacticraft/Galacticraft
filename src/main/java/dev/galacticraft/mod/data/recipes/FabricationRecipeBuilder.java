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

import com.google.gson.JsonObject;
import dev.galacticraft.mod.recipe.GCRecipes;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class FabricationRecipeBuilder extends GCRecipeBuilder {

    private Ingredient ingredient;
    private int time;

    public FabricationRecipeBuilder(ItemLike result, int count) {
        super(GCRecipes.FABRICATION_SERIALIZER, "fabrication", result, count);
    }

    public static FabricationRecipeBuilder create(ItemLike itemLike) {
        return new FabricationRecipeBuilder(itemLike, 1);
    }

    public static FabricationRecipeBuilder create(ItemLike itemLike, int i) {
        return new FabricationRecipeBuilder(itemLike, i);
    }

    public FabricationRecipeBuilder requires(TagKey<Item> tagKey) {
        this.ingredient = Ingredient.of(tagKey);
        return this;
    }

    public FabricationRecipeBuilder requires(ItemLike itemLike) {
        this.ingredient = Ingredient.of(itemLike);
        return this;
    }

    public FabricationRecipeBuilder time(int time) {
        this.time = time;
        return this;
    }

    @Override
    public void serializeRecipeData(JsonObject recipeJson) {
        recipeJson.add("ingredient", this.ingredient.toJson());
        if (this.time > 1) {
            recipeJson.addProperty("time", this.time);
        }
        this.createResult(recipeJson);
    }
}