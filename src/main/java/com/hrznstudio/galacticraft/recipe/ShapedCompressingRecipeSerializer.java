/*
 * Copyright (c) 2020 HRZN LTD
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
import com.hrznstudio.galacticraft.recipe.ShapedCompressingRecipeSerializer.RecipeFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

import java.util.Map;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ShapedCompressingRecipeSerializer<T extends ShapedCompressingRecipe> implements RecipeSerializer<T> {
    private final RecipeFactory<T> factory;

    public ShapedCompressingRecipeSerializer(RecipeFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public T read(Identifier id, JsonObject json) {
        String string_1 = JsonHelper.getString(json, "group", "");
        Map<String, Ingredient> ingredients = ShapedCompressingRecipe.getComponents(JsonHelper.getObject(json, "key"));
        String[] pattern = ShapedCompressingRecipe.combinePattern(ShapedCompressingRecipe.getPattern(JsonHelper.getArray(json, "pattern")));
        int int_1 = pattern[0].length();
        int int_2 = pattern.length;
        DefaultedList<Ingredient> list = ShapedCompressingRecipe.getIngredients(pattern, ingredients, int_1, int_2);
        ItemStack stack = ShapedRecipe.getItemStack(JsonHelper.getObject(json, "result"));
        return factory.create(id, string_1, int_1, int_2, list, stack);
    }

    @Override
    public T read(Identifier identifier_1, PacketByteBuf packet) {
        int int_1 = packet.readVarInt();
        int int_2 = packet.readVarInt();
        String group = packet.readString(32767);
        DefaultedList<Ingredient> defaultedList_1 = DefaultedList.ofSize(int_1 * int_2, Ingredient.EMPTY);

        for (int int_3 = 0; int_3 < defaultedList_1.size(); ++int_3) {
            defaultedList_1.set(int_3, Ingredient.fromPacket(packet));
        }

        ItemStack stack = packet.readItemStack();
        return factory.create(identifier_1, group, int_1, int_2, defaultedList_1, stack);
    }

    @Override
    public void write(PacketByteBuf packet, T recipe) {
        packet.writeVarInt(recipe.getWidth());
        packet.writeVarInt(recipe.getHeight());
        packet.writeString(recipe.group);

        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.write(packet);
        }

        packet.writeItemStack(recipe.getOutput());
    }

    interface RecipeFactory<T extends ShapedCompressingRecipe> {
        T create(Identifier id, String group, int int_1, int int_2, DefaultedList<Ingredient> input, ItemStack output);
    }
}