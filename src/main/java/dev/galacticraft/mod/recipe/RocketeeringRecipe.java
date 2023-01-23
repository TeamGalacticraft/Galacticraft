/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.google.gson.*; // *?

import dev.galacticraft.mod.Constant;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class RocketeeringRecipe implements Recipe<Container> {
    public final NonNullList<Ingredient> inputs;
    private final int width;
    private final ItemStack output;
    private final ResourceLocation id; // this is the name of recipes i guess?
    private final String group; // do we need you?

    public RocketeeringRecipe(ResourceLocation id, String group, int width, NonNullList<Ingredient> inputs, ItemStack output) {
        this.id = id;
        this.group = group;
        this.width = width;
        this.inputs = inputs;
        this.output = output;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.inputs;
    }

    @Override
    public boolean matches(Container inventory, Level world) { //?
        for (int i = 0; i < this.width; ++i) { // Complex logic no
            Ingredient ingredient = this.inputs.get(i); // 0 index? Watch out of bounds with empty chest spaces?
            if (!ingredient.test(inventory.getItem(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(Container var1) {
        return this.getResultItem().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width == this.width; // TODO: Check the logic here this should properly split things up?
    }

    @Override
    public ItemStack getResultItem() {
        // TODO Auto-generated method stub
        return this.output;
    }

    @Override
    public ResourceLocation getId() {
        // TODO Auto-generated method stub
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        // TODO Auto-generated method stub
        return GalacticraftRecipe.ROCKETEERING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        // TODO Auto-generated method stub
        return GalacticraftRecipe.ROCKETEERING_TYPE;
    }

    // Serialization Helpers

    public static ItemStack getItemStack(JsonObject json) { // TODO: Extract this to a shared code place since this is used alot with the sister classes
        String string = GsonHelper.getAsString(json, "item");
        Item item = Registry.ITEM.getOptional(new ResourceLocation(string)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + string + "'"));
        if (json.has("data")) {
           throw new JsonParseException("Disallowed data tag found");
        } else {
           int i = GsonHelper.getAsInt(json, "count", 1);
           return new ItemStack(item, i);
        }
     }

    private static Map<String, Ingredient> getComponents(JsonObject json) {
        Map<String, Ingredient> map = Maps.newHashMap();

        for (Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
        }
        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    private static String getPattern(JsonArray json) {
        String pattern = new String();
        if (json.size() > 1) {
            throw new JsonSyntaxException("Invalid pattern: rockets should be a flat pattern");
        } else if (json.size() == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            String string = GsonHelper.convertToString(json.get(0), "pattern[" + 0 + "]"); //? what we doing here???????

            pattern = string;

            return pattern;
        }
    }

    // @Override // TODO: Implement to shuffle chests and empties when width > 4 
    // public NonNullList<Ingredient> getIngredients() {
    //     return this.inputs;
    // }

    public enum Serializer implements RecipeSerializer<RocketeeringRecipe> {
        INSTANCE;

        @Override
        public RocketeeringRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            Map<String, Ingredient> map = RocketeeringRecipe.getComponents(GsonHelper.getAsJsonObject(json, "key"));
            String pattern = RocketeeringRecipe.getPattern(GsonHelper.getAsJsonArray(json, "pattern"));
            int width = pattern.length();

            // ????? here
            NonNullList<Ingredient> inputs = NonNullList.withSize(width, Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); ++i) {
                inputs.set(i, map.get(pattern.substring(i, i + 1)));
            }

            ItemStack output = RocketeeringRecipe.getItemStack(GsonHelper.getAsJsonObject(json, "result"));

            return new RocketeeringRecipe(id, group, width, inputs, output);
        }

        @Override
        public RocketeeringRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int width = buf.readVarInt();
            String string = buf.readUtf(Constant.Misc.MAX_STRING_READ);

            NonNullList<Ingredient> ingredients = NonNullList.withSize(width, Ingredient.EMPTY);
            for(int k = 0; k < ingredients.size(); ++k) {
                ingredients.set(k, Ingredient.fromNetwork(buf));
            }
    
            ItemStack output = buf.readItem();

            return new RocketeeringRecipe(id, string, width, ingredients, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, RocketeeringRecipe recipe) {
            buf.writeVarInt(recipe.width);
            buf.writeUtf(recipe.group);

            for (Ingredient ingredient : recipe.inputs) {
                ingredient.toNetwork(buf);
            }

            buf.writeItem(recipe.output);
        }

    } 
}