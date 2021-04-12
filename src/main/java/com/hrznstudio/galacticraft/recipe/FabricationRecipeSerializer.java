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

import com.google.gson.JsonObject;
import com.hrznstudio.galacticraft.recipe.FabricationRecipeSerializer.RecipeFactory;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class FabricationRecipeSerializer<T extends FabricationRecipe> implements RecipeSerializer<T> {
    private final RecipeFactory<T> recipeFactory;

    public FabricationRecipeSerializer(RecipeFactory<T> factory) {
        this.recipeFactory = factory;
    }

    @Override
    public void write(FriendlyByteBuf packetByteBuf, T recipe) {
        packetByteBuf.writeUtf(recipe.group);
        recipe.getInput().toNetwork(packetByteBuf);
        packetByteBuf.writeItem(recipe.getResultItem());
    }

    @Override
    public T fromNetwork(ResourceLocation id, FriendlyByteBuf packet) {
        String group = packet.readUtf(32767);
        Ingredient ingredient = Ingredient.fromNetwork(packet);
        ItemStack stack = packet.readItem();
        return this.recipeFactory.create(id, group, ingredient, stack);
    }

    @Override
    public T fromJson(ResourceLocation id, JsonObject json) {
        String group = GsonHelper.getAsString(json, "group", "");
        Ingredient inputIngredient;
        if (GsonHelper.isArrayNode(json, "ingredient")) {
            inputIngredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "ingredient"));
        } else {
            inputIngredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
        }

        String result = GsonHelper.getAsString(json, "result");
        int count = GsonHelper.getAsInt(json, "count");
        ItemStack outputItem = new ItemStack(Registry.ITEM.get(new ResourceLocation(result)), count);
        return this.recipeFactory.create(id, group, inputIngredient, outputItem);
    }

    interface RecipeFactory<T extends FabricationRecipe> {
        T create(ResourceLocation id, String var2, Ingredient input, ItemStack output);
    }
}