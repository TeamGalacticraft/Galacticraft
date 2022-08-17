/*
 * Copyright (c) 2019-2022 Team Galacticraft
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
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import me.shedaniel.rei.api.common.transfer.RecipeFinder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketAssemblerRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final ResourceLocation output;
    private final Object2IntMap<Ingredient> input;

    public RocketAssemblerRecipe(ResourceLocation id, ResourceLocation output, Object2IntMap<Ingredient> input) {
        this.id = id;
        this.output = output;
        this.input = input;
    }

    @Override
    public ResourceLocation getId() {
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
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    public ResourceLocation getPartOutput() {
        return output;
    }

    @Override
    public boolean matches(Container inv, Level world) {
        RecipeFinder finder = new RecipeFinder();
        int found = 0;

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                ++found;
                finder.addItem(stack);
            }
        }
        NonNullList<Ingredient> ingredientList = NonNullList.withSize(this.input.keySet().size(), Ingredient.of());
        ingredientList.addAll(this.input.keySet());

        return found == this.input.size() && finder.findRecipe(ingredientList, new IntArrayList(this.input.values()));
    }

    @Override
    public ItemStack assemble(Container inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
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
        public RocketAssemblerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            Object2IntMap<Ingredient> ingredients = new Object2IntArrayMap<>(size);

            for (int i = 0; i < ingredients.size(); ++i) {
                ingredients.put(Ingredient.fromNetwork(buf), buf.readInt());
            }

            ResourceLocation result = buf.readResourceLocation();
            return new RocketAssemblerRecipe(id, result, ingredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, RocketAssemblerRecipe recipe) {
            buf.writeVarInt(recipe.getInput().size());

            for (Object2IntMap.Entry<Ingredient> ingredientEntry : recipe.getInput().object2IntEntrySet()) {
                ingredientEntry.getKey().toNetwork(buf);
                buf.writeInt(ingredientEntry.getIntValue());
            }

            buf.writeResourceLocation(recipe.getPartOutput());
        }

        @Override
        public RocketAssemblerRecipe fromJson(ResourceLocation id, JsonObject json) {
            Object2IntMap<Ingredient> ingredients = getItemStacks(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for rocket assembler recipe");
            } else {
                ResourceLocation result = new ResourceLocation(GsonHelper.getAsString(json, "result"));
                return new RocketAssemblerRecipe(id, result, ingredients);
            }
        }
    }
}