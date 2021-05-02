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
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Map;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketAssemblerRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final Identifier output;
    private final Object2IntMap<Ingredient> input;

    public RocketAssemblerRecipe(Identifier id, Identifier output, Object2IntMap<Ingredient> input) {
        this.id = id;
        this.output = output;
        this.input = input;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GalacticraftRecipe.ROCKET_ASSEMBLER_SERIALIZER;
    }

    @Override
    public RecipeType<RocketAssemblerRecipe> getType() {
        return GalacticraftRecipe.ROCKET_ASSEMBLER_TYPE;
    }

    @Override
    public ItemStack getOutput() {
        return ItemStack.EMPTY;
    }

    public Identifier getPartOutput() {
        return output;
    }

    @Override
    public boolean matches(Inventory inv, World world_1) {
        RecipeFinder finder = new RecipeFinder();
        int found = 0;

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack itemStack_1 = inv.getStack(i);
            if (!itemStack_1.isEmpty()) {
                ++found;
                finder.addItem(itemStack_1);
            }
        }

        return found == this.input.size() && finder.findRecipe(this, null);
    }

    @Override
    public ItemStack craft(Inventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return false;
    }

    public Object2IntMap<Ingredient> getInput() {
        return this.input;
    }

    public enum Serializer implements RecipeSerializer<RocketAssemblerRecipe> {
        INSTANCE;

        private static Object2IntMap<Ingredient> getItemStacks(JsonArray json) {
            Object2IntMap<Ingredient> ingredients = new Object2IntArrayMap<>(json.size());
            for (int i = 0; i < json.size(); ++i) {
                JsonObject object = json.get(i).getAsJsonObject();
                ingredients.put(Ingredient.fromJson(object), object.get("count") == null ? 1 : object.get("count").getAsInt());
            }
            return ingredients;
        }

        @Override
        public RocketAssemblerRecipe read(Identifier id, PacketByteBuf buf) {
            int size = buf.readVarInt();
            Object2IntMap<Ingredient> ingredients = new Object2IntArrayMap<>(size);

            for (int i = 0; i < ingredients.size(); ++i) {
                ingredients.put(Ingredient.fromPacket(buf), buf.readInt());
            }

            Identifier result = buf.readIdentifier();
            return new RocketAssemblerRecipe(id, result, ingredients);
        }

        @Override
        public void write(PacketByteBuf buf, RocketAssemblerRecipe recipe) {
            buf.writeVarInt(recipe.getInput().size());

            for (Object2IntMap.Entry<Ingredient> ingredientEntry : recipe.getInput().object2IntEntrySet()) {
                ingredientEntry.getKey().write(buf);
                buf.writeInt(ingredientEntry.getIntValue());
            }

            buf.writeIdentifier(recipe.getPartOutput());
        }

        @Override
        public RocketAssemblerRecipe read(Identifier id, JsonObject json) {
            Object2IntMap<Ingredient> ingredients = getItemStacks(JsonHelper.getArray(json, "ingredients"));
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for rocket assembler recipe");
            } else {
                Identifier result = new Identifier(JsonHelper.getString(json, "result"));
                return new RocketAssemblerRecipe(id, result, ingredients);
            }
        }
    }
}