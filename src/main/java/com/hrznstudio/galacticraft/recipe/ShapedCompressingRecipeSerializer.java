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
 *
 */

package com.hrznstudio.galacticraft.recipe;

import com.google.gson.JsonObject;
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
    public T read(Identifier identifier_1, JsonObject jsonObject_1) {
        String string_1 = JsonHelper.getString(jsonObject_1, "group", "");
        Map<String, Ingredient> map_1 = ShapedCompressingRecipe.getComponents(JsonHelper.getObject(jsonObject_1, "key"));
        String[] strings_1 = ShapedCompressingRecipe.combinePattern(ShapedCompressingRecipe.getPattern(JsonHelper.getArray(jsonObject_1, "pattern")));
        int int_1 = strings_1[0].length();
        int int_2 = strings_1.length;
        DefaultedList<Ingredient> defaultedList_1 = ShapedCompressingRecipe.getIngredients(strings_1, map_1, int_1, int_2);
        ItemStack itemStack_1 = ShapedRecipe.getItemStack(JsonHelper.getObject(jsonObject_1, "result"));
        return factory.create(identifier_1, string_1, int_1, int_2, defaultedList_1, itemStack_1);
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

        ItemStack itemStack_1 = packet.readItemStack();
        return factory.create(identifier_1, group, int_1, int_2, defaultedList_1, itemStack_1);
    }

    @Override
    public void write(PacketByteBuf packet, T recipe) {
        packet.writeVarInt(recipe.getWidth());
        packet.writeVarInt(recipe.getHeight());
        packet.writeString(recipe.group);

        for (Ingredient ingredient_1 : recipe.getIngredients()) {
            ingredient_1.write(packet);
        }

        packet.writeItemStack(recipe.getOutput());
    }

    interface RecipeFactory<T extends ShapedCompressingRecipe> {
        T create(Identifier id, String group, int int_1, int int_2, DefaultedList<Ingredient> input, ItemStack output);
    }
}