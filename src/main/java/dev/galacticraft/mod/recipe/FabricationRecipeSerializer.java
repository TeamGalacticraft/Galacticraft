/*
 * Copyright (c) 2020 Team Galacticraft
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

package dev.galacticraft.mod.recipe;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class FabricationRecipeSerializer<T extends FabricationRecipe> implements RecipeSerializer<T> {
    private final RecipeFactory<T> recipeFactory;

    public FabricationRecipeSerializer(RecipeFactory<T> factory) {
        this.recipeFactory = factory;
    }

    @Override
    public void write(PacketByteBuf packetByteBuf, T recipe) {
        packetByteBuf.writeString(recipe.group);
        recipe.getInput().write(packetByteBuf);
        packetByteBuf.writeItemStack(recipe.getOutput());
    }

    @Override
    public T read(Identifier id, PacketByteBuf packet) {
        String group = packet.readString(32767);
        Ingredient ingredient = Ingredient.fromPacket(packet);
        ItemStack stack = packet.readItemStack();
        return this.recipeFactory.create(id, group, ingredient, stack);
    }

    @Override
    public T read(Identifier id, JsonObject json) {
        String group = JsonHelper.getString(json, "group", "");
        Ingredient inputIngredient;
        if (JsonHelper.hasArray(json, "ingredient")) {
            inputIngredient = Ingredient.fromJson(JsonHelper.getArray(json, "ingredient"));
        } else {
            inputIngredient = Ingredient.fromJson(JsonHelper.getObject(json, "ingredient"));
        }

        String result = JsonHelper.getString(json, "result");
        int count = JsonHelper.getInt(json, "count");
        ItemStack outputItem = new ItemStack(Registry.ITEM.get(new Identifier(result)), count);
        return this.recipeFactory.create(id, group, inputIngredient, outputItem);
    }

    interface RecipeFactory<T extends FabricationRecipe> {
        T create(Identifier id, String var2, Ingredient input, ItemStack output);
    }
}