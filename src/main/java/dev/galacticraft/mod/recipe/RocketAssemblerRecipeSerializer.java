/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.galacticraft.mod.Galacticraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketAssemblerRecipeSerializer<T extends RocketAssemblerRecipe> implements RecipeSerializer<T> {
    private final Factory<T> factory;

    public RocketAssemblerRecipeSerializer(Factory<T> factory) {
        this.factory = factory;
    }

    private static DefaultedList<ItemStack> getItemStacks(JsonArray json) {
        DefaultedList<ItemStack> ingredients = DefaultedList.of();

        for (int i = 0; i < json.size(); ++i) {
            JsonObject object = json.get(i).getAsJsonObject();
            ItemStack stack = new ItemStack(Registry.ITEM.getOrEmpty(new Identifier(object.get("item").getAsString())).orElseGet(() -> {
                Galacticraft.LOGGER.fatal("Missing item for recipe!!");
                Galacticraft.LOGGER.fatal(object.get("item").getAsString());
                return Items.AIR;
            }));
            int count = (object.get("count") != null ? object.get("count").getAsInt() : 1);
            do {
                stack.setCount(Math.min(64, count));
                count -= 64;
                if (!stack.isEmpty()) {
                    ingredients.add(stack);
                    stack = stack.copy();
                }
            } while (count > 0);
        }

        return ingredients;
    }


    @Override
    public T read(Identifier id, PacketByteBuf buf) {
        int ingredientCount = buf.readVarInt();
        DefaultedList<ItemStack> ingredients = DefaultedList.ofSize(ingredientCount, ItemStack.EMPTY);

        for (int i = 0; i < ingredients.size(); ++i) {
            ingredients.set(i, buf.readItemStack());
        }

        Identifier result = buf.readIdentifier();
        return factory.create(id, result, ingredients);
    }

    @Override
    public void write(PacketByteBuf buf, T recipe) {
        buf.writeVarInt(recipe.getInput().size());

        for (ItemStack ingredient : recipe.getInput()) {
            buf.writeItemStack(ingredient);
        }

        buf.writeIdentifier(recipe.getPartOutput());
    }

    @Override
    public T read(Identifier id, JsonObject json) {
        DefaultedList<ItemStack> ingredients = getItemStacks(JsonHelper.getArray(json, "ingredients"));
        if (ingredients.isEmpty()) {
            throw new JsonParseException("No ingredients for rocket assembler recipe");
        } else {
            Identifier result = new Identifier(JsonHelper.getString(json, "result"));
            return factory.create(id, result, ingredients);
        }
    }

    interface Factory<T extends RocketAssemblerRecipe> {
        T create(Identifier id, Identifier output, DefaultedList<ItemStack> ingredients);
    }
}