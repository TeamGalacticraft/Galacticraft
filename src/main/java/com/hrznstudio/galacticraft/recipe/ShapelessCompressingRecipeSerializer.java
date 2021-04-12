/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hrznstudio.galacticraft.recipe.ShapelessCompressingRecipeSerializer.RecipeFactory;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ShapelessCompressingRecipeSerializer<T extends ShapelessCompressingRecipe> implements RecipeSerializer<T> {
    private final RecipeFactory<T> factory;

    public ShapelessCompressingRecipeSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    private static NonNullList<Ingredient> getIngredients(JsonArray jsonArray_1) {
        NonNullList<Ingredient> defaultedList_1 = NonNullList.create();

        for (int int_1 = 0; int_1 < jsonArray_1.size(); ++int_1) {
            Ingredient ingredient_1 = Ingredient.fromJson(jsonArray_1.get(int_1));
            if (!ingredient_1.isEmpty()) {
                defaultedList_1.add(ingredient_1);
            }
        }

        return defaultedList_1;
    }

    @Override
    public void write(FriendlyByteBuf packet, ShapelessCompressingRecipe recipe) {
//            packet.writeString(recipe.group);
        packet.writeVarInt(recipe.getInput().size());

        for (Ingredient ingredient : recipe.getInput()) {
            ingredient.toNetwork(packet);
        }

        packet.writeItem(recipe.getResultItem());
    }

    @Override
    public T fromNetwork(ResourceLocation id, FriendlyByteBuf packet) {
//            String group = packet.readString(32767);
        int ingredientCount = packet.readVarInt();
        NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);

        for (int index = 0; index < ingredients.size(); ++index) {
            ingredients.set(index, Ingredient.fromNetwork(packet));
        }

        ItemStack result = packet.readItem();
        return factory.create(id, /*group, */result, ingredients);
    }

    @Override
    public T fromJson(ResourceLocation id, JsonObject json) {
//            String group = JsonHelper.getString(json, "group", "");
        NonNullList<Ingredient> ingredients = getIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
        if (ingredients.isEmpty()) {
            throw new JsonParseException("No ingredients for compressing recipe");
        } else if (ingredients.size() > 9) {
            throw new JsonParseException("Too many ingredients for compressing recipe");
        } else {
            ItemStack result = ShapelessCompressingRecipe.getStack(GsonHelper.getAsJsonObject(json, "result"));
            return factory.create(id, /*group, */result, ingredients);
        }
    }

    interface RecipeFactory<T extends ShapelessCompressingRecipe> {
        T create(ResourceLocation id, ItemStack output, NonNullList<Ingredient> ingredients);
    }
}