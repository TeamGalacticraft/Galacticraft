/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShapedCompressingRecipe implements CompressingRecipe {
    private final ShapedRecipePattern pattern;
    private final ItemStack output;
    private final String group;
    private final int time;

    public ShapedCompressingRecipe(String group, ShapedRecipePattern pattern, ItemStack output, int time) {
        this.group = group;
        this.pattern = pattern;
        this.output = output;
        this.time = time;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GCRecipes.SHAPED_COMPRESSING_SERIALIZER;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.pattern.ingredients();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.pattern.width() && height >= this.pattern.height();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.output;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level world) {
        return this.pattern.matches(inv);
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        return this.getResultItem(registryAccess).copy();
    }

    public int getWidth() {
        return this.pattern.width();
    }

    public int getHeight() {
        return this.pattern.height();
    }

    private static NonNullList<Ingredient> getIngredients(String[] pattern, Map<String, Ingredient> key, int width, int height) {
        NonNullList<Ingredient> defaultedList = NonNullList.withSize(width * height, Ingredient.EMPTY);
        Set<String> set = new HashSet<>(key.keySet());
        set.remove(" ");

        for (int i = 0; i < pattern.length; ++i) {
            for (int j = 0; j < pattern[i].length(); ++j) {
                String string = pattern[i].substring(j, j + 1);
                Ingredient ingredient = key.get(string);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
                }

                set.remove(string);
                defaultedList.set(j + width * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return defaultedList;
        }
    }

    @VisibleForTesting
    static String[] combinePattern(String... lines) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int m = 0; m < lines.length; ++m) {
            String string = lines[m];
            i = Math.min(i, findNextIngredient(string));
            int n = findNextIngredientReverse(string);
            j = Math.max(j, n);
            if (n < 0) {
                if (k == m) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (lines.length == l) {
            return new String[0];
        } else {
            String[] strings = new String[lines.length - l - k];

            for (int o = 0; o < strings.length; ++o) {
                strings[o] = lines[o + k].substring(i, j + 1);
            }

            return strings;
        }
    }

    private static int findNextIngredient(String pattern) {
        int i = 0;
        while (i < pattern.length() && pattern.charAt(i) == ' ') {
            ++i;
        }

        return i;
    }

    private static int findNextIngredientReverse(String pattern) {
        int i = pattern.length() - 1;
        while (i >= 0 && pattern.charAt(i) == ' ') {
            --i;
        }

        return i;
    }

    private static String[] getPattern(JsonArray json) {
        String[] strings = new String[json.size()];
        if (strings.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        } else if (strings.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for (int i = 0; i < strings.length; ++i) {
                String string = GsonHelper.convertToString(json.get(i), "pattern[" + i + "]");
                if (string.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                }

                if (i > 0 && strings[0].length() != string.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                strings[i] = string;
            }

            return strings;
        }
    }

    @Override
    public int getTime() {
        return this.time;
    }

    public static class Serializer implements RecipeSerializer<ShapedCompressingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final Codec<ShapedCompressingRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(recipe -> recipe.group),
                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                ExtraCodecs.strictOptionalField(Codec.INT, "time", 200).forGetter(recipe -> recipe.time)
        ).apply(instance, ShapedCompressingRecipe::new));

        @Override
        public Codec<ShapedCompressingRecipe> codec() {
            return CODEC;
        }

        @Override
        public ShapedCompressingRecipe fromNetwork(FriendlyByteBuf buf) {
            ShapedRecipePattern pattern = ShapedRecipePattern.fromNetwork(buf);
            String string = buf.readUtf(Constant.Misc.MAX_STRING_READ);
            int time = buf.readInt();

            ItemStack output = buf.readItem();
            return new ShapedCompressingRecipe(string, pattern, output, time);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ShapedCompressingRecipe recipe) {
            recipe.pattern.toNetwork(buf);
            buf.writeUtf(recipe.group);
            buf.writeInt(recipe.time);

            buf.writeItem(recipe.output);
        }
    }
}
