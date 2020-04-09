/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.recipes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Set;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ShapedCompressingRecipe implements Recipe<Inventory> {
    private final int width;
    private final int height;
    private final DefaultedList<Ingredient> ingredients;
    private final ItemStack output;
    private final Identifier id;
    private final String group;

    public ShapedCompressingRecipe(Identifier id, String group, int width, int height, DefaultedList<Ingredient> ingredients, ItemStack output) {
        this.id = id;
        this.group = group;
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
        this.output = output;
    }

    static DefaultedList<Ingredient> getIngredients(String[] strings_1, Map<String, Ingredient> map_1, int int_1, int int_2) {
        DefaultedList<Ingredient> defaultedList_1 = DefaultedList.ofSize(int_1 * int_2, Ingredient.EMPTY);
        Set<String> set_1 = Sets.newHashSet(map_1.keySet());
        set_1.remove(" ");

        for (int int_3 = 0; int_3 < strings_1.length; ++int_3) {
            for (int int_4 = 0; int_4 < strings_1[int_3].length(); ++int_4) {
                String string_1 = strings_1[int_3].substring(int_4, int_4 + 1);
                Ingredient ingredient_1 = map_1.get(string_1);
                if (ingredient_1 == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + string_1 + "' but it's not defined in the key");
                }

                set_1.remove(string_1);
                defaultedList_1.set(int_4 + int_1 * int_3, ingredient_1);
            }
        }

        if (!set_1.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set_1);
        } else {
            return defaultedList_1;
        }
    }

    @VisibleForTesting
    static String[] combinePattern(String... strings_1) {
        int int_1 = Integer.MAX_VALUE;
        int int_2 = 0;
        int int_3 = 0;
        int int_4 = 0;

        for (int int_5 = 0; int_5 < strings_1.length; ++int_5) {
            String string_1 = strings_1[int_5];
            int_1 = Math.min(int_1, findNextIngredient(string_1));
            int int_6 = findNextIngredientReverse(string_1);
            int_2 = Math.max(int_2, int_6);
            if (int_6 < 0) {
                if (int_3 == int_5) {
                    ++int_3;
                }

                ++int_4;
            } else {
                int_4 = 0;
            }
        }

        if (strings_1.length == int_4) {
            return new String[0];
        } else {
            String[] strings_2 = new String[strings_1.length - int_4 - int_3];

            for (int int_7 = 0; int_7 < strings_2.length; ++int_7) {
                strings_2[int_7] = strings_1[int_7 + int_3].substring(int_1, int_2 + 1);
            }

            return strings_2;
        }
    }

    private static int findNextIngredient(String string_1) {
        int int_1;
        for (int_1 = 0; int_1 < string_1.length() && string_1.charAt(int_1) == ' '; ++int_1) {
        }

        return int_1;
    }

    private static int findNextIngredientReverse(String string_1) {
        int int_1;
        for (int_1 = string_1.length() - 1; int_1 >= 0 && string_1.charAt(int_1) == ' '; --int_1) {
        }

        return int_1;
    }

    static String[] getPattern(JsonArray jsonArray_1) {
        String[] strings_1 = new String[jsonArray_1.size()];
        if (strings_1.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        } else if (strings_1.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for (int int_1 = 0; int_1 < strings_1.length; ++int_1) {
                String string_1 = JsonHelper.asString(jsonArray_1.get(int_1), "pattern[" + int_1 + "]");
                if (string_1.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                }

                if (int_1 > 0 && strings_1[0].length() != string_1.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                strings_1[int_1] = string_1;
            }

            return strings_1;
        }
    }

    static Map<String, Ingredient> getComponents(JsonObject jsonObject_1) {
        Map<String, Ingredient> map_1 = Maps.newHashMap();

        for (Map.Entry<String, JsonElement> entry : jsonObject_1.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map_1.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
        }

        map_1.put(" ", Ingredient.EMPTY);
        return map_1;
    }

    public DefaultedList<Ingredient> getIngredients() {
        return ingredients;
    }

    public Identifier getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return GalacticraftRecipes.SHAPED_COMPRESSING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return GalacticraftRecipes.SHAPED_COMPRESSING_TYPE;
    }

    @Environment(EnvType.CLIENT)
    public String getGroup() {
        return this.group;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public DefaultedList<Ingredient> getPreviewInputs() {
        return this.ingredients;
    }

    @Environment(EnvType.CLIENT)
    public boolean fits(int int_1, int int_2) {
        return int_1 >= this.width && int_2 >= this.height;
    }

    @Override
    public boolean matches(Inventory inv, World world_1) {
        int invWidth = 3;
        int invHeight = 3;

        for (int x = 0; x <= invWidth - this.width; ++x) {
            for (int y = 0; y <= invHeight - this.height; ++y) {
                if (this.matchesSmall(inv, x, y, true)) {
                    return true;
                }

                if (this.matchesSmall(inv, x, y, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matchesSmall(Inventory inv, int int_1, int int_2, boolean boolean_1) {
        int invWidth = 3;
        int invHeight = 3;

        for (int int_3 = 0; int_3 < invWidth; ++int_3) {
            for (int int_4 = 0; int_4 < invHeight; ++int_4) {
                int int_5 = int_3 - int_1;
                int int_6 = int_4 - int_2;
                Ingredient ingredient_1 = Ingredient.EMPTY;
                if (int_5 >= 0 && int_6 >= 0 && int_5 < this.width && int_6 < this.height) {
                    if (boolean_1) {
                        ingredient_1 = this.ingredients.get(this.width - int_5 - 1 + int_6 * this.width);
                    } else {
                        ingredient_1 = this.ingredients.get(int_5 + int_6 * this.width);
                    }
                }

                if (!ingredient_1.method_8093(inv.getInvStack(int_3 + int_4 * invWidth))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(Inventory inv) {
        return this.getOutput().copy();
    }

    int getWidth() {
        return this.width;
    }

    int getHeight() {
        return this.height;
    }
}