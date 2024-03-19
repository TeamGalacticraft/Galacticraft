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

package dev.galacticraft.impl.rocket.recipe.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipeSlot;
import dev.galacticraft.api.rocket.recipe.config.RocketPartRecipeConfig;
import dev.galacticraft.impl.codec.AlternateDecoderCodec;
import dev.galacticraft.impl.codec.JsonDecoder;
import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record PatternedRocketPartRecipeConfig(int height, @NotNull List<RocketPartRecipeSlot> right, @NotNull List<RocketPartRecipeSlot> left, @NotNull
                                              NonNullList<Ingredient> ingredients) implements RocketPartRecipeConfig {
    private static final Codec<PatternedRocketPartRecipeConfig> INTERNAL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("height").forGetter(PatternedRocketPartRecipeConfig::height),
            RocketPartRecipeSlot.CODEC.listOf().fieldOf("right").forGetter(PatternedRocketPartRecipeConfig::right),
            RocketPartRecipeSlot.CODEC.listOf().fieldOf("left").forGetter(PatternedRocketPartRecipeConfig::left)
    ).apply(instance, (height, right, left) -> {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(right.size() + left.size());
        for (RocketPartRecipeSlot slot : right) {
            ingredients.add(slot.ingredient());
        }
        for (RocketPartRecipeSlot slot : left) {
            ingredients.add(slot.ingredient());
        }
        return new PatternedRocketPartRecipeConfig(height, right, left, ingredients);
    }));

    public static final Decoder<PatternedRocketPartRecipeConfig> PRETTY_DECODER = (JsonDecoder<PatternedRocketPartRecipeConfig>) (ops, elem) -> {
        JsonObject json = elem.getAsJsonObject();

        Char2ObjectMap<Ingredient> ingredients = new Char2ObjectArrayMap<>();
        Char2IntMap spacing = new Char2IntArrayMap();
        spacing.put(' ', 18);
        spacing.put('.', 9);

        json.getAsJsonObject("key").asMap().forEach((s, element) -> {
            char key = s.charAt(0);
            if (spacing.containsKey(key) || ingredients.containsKey(key)) {
                throw new RuntimeException("duplicate key '" + key + "'!");
            }
            if (element.isJsonPrimitive()) {
                spacing.put(key, element.getAsInt());
            } else {
                Ingredient.CODEC.decode(JsonOps.INSTANCE, element).get().ifLeft(pair -> ingredients.put(key, pair.getFirst()));
            }
        });

        return PatternedRocketPartRecipeConfig.parse(spacing, ingredients,
                json.getAsJsonArray("left").asList().stream().map(JsonElement::getAsString).toList(),
                json.getAsJsonArray("right").asList().stream().map(JsonElement::getAsString).toList());
    };

    @NotNull
    public static PatternedRocketPartRecipeConfig parse(Char2IntMap spacing, Char2ObjectMap<Ingredient> ingredients, List<String> leftPattern, List<String> rightPattern) {
        ImmutableList.Builder<RocketPartRecipeSlot> left = ImmutableList.builder();
        int[] widths = new int[leftPattern.size()];
        for (int i = 0; i < leftPattern.size(); i++) {
            String s = leftPattern.get(i);
            int x = 0;
            for (char c : s.toCharArray()) {
                if (spacing.containsKey(c)) {
                    x += spacing.get(c);
                } else {
                    assert ingredients.containsKey(c);
                    x += 18;
                }
            }
            widths[i] = x;
        }

        int y = 0;
        for (int i = 0; i < leftPattern.size(); i++) {
            int x = 0;
            for (char c : leftPattern.get(i).toCharArray()) {
                if (spacing.containsKey(c)) {
                    x += spacing.get(c);
                } else {
                    left.add(RocketPartRecipeSlot.create(-widths[i] + x, y, ingredients.get(c)));
                    x += 18;
                }
            }
            y += 18;
        }

        int height = y;

        ImmutableList.Builder<RocketPartRecipeSlot> right = ImmutableList.builder();
        y = 0;
        for (String s : rightPattern) {
            int x = 0;
            char[] chars = s.toCharArray();
            for (char c : chars) {
                if (spacing.containsKey(c)) {
                    x += spacing.get(c);
                } else {
                    right.add(RocketPartRecipeSlot.create(x, y, ingredients.get(c)));
                    x += 18;
                }
            }
            y += 18;
        }

        height = Math.max(height, y);

        ImmutableList<RocketPartRecipeSlot> leftB = left.build();
        ImmutableList<RocketPartRecipeSlot> rightB = right.build();
        NonNullList<Ingredient> ingredientsList = NonNullList.createWithCapacity(rightB.size() + leftB.size());
        for (RocketPartRecipeSlot slot : rightB) {
            ingredientsList.add(slot.ingredient());
        }
        for (RocketPartRecipeSlot slot : leftB) {
            ingredientsList.add(slot.ingredient());
        }

        return new PatternedRocketPartRecipeConfig(height, rightB, leftB, ingredientsList);
    }

    public static final Codec<PatternedRocketPartRecipeConfig> CODEC = new AlternateDecoderCodec<>(PRETTY_DECODER, INTERNAL_CODEC);
}
