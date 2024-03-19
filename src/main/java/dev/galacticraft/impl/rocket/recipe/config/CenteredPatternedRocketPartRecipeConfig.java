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
import java.util.Map;

public record CenteredPatternedRocketPartRecipeConfig(int height, @NotNull List<RocketPartRecipeSlot> slots, @NotNull
                                              NonNullList<Ingredient> ingredients) implements RocketPartRecipeConfig {
    private static final Codec<CenteredPatternedRocketPartRecipeConfig> INTERNAL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("height").forGetter(CenteredPatternedRocketPartRecipeConfig::height),
            RocketPartRecipeSlot.CODEC.listOf().fieldOf("slots").forGetter(CenteredPatternedRocketPartRecipeConfig::slots)
    ).apply(instance, (height, slots) -> {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(slots.size());
        for (RocketPartRecipeSlot slot : slots) {
            ingredients.add(slot.ingredient());
        }
        return new CenteredPatternedRocketPartRecipeConfig(height, slots, ingredients);
    }));

    public static final Decoder<CenteredPatternedRocketPartRecipeConfig> PRETTY_DECODER = (JsonDecoder<CenteredPatternedRocketPartRecipeConfig>) (ops, elem) -> {
        try {
            JsonObject json = elem.getAsJsonObject();
            Char2ObjectMap<Ingredient> ingredients = new Char2ObjectArrayMap<>();
            Char2IntMap spacing = new Char2IntArrayMap();
            spacing.put(' ', 18);
            spacing.put('.', 9);


            for (Map.Entry<String, JsonElement> entry : json.get("key").getAsJsonObject().entrySet()) {
                String s = entry.getKey();
                JsonElement element = entry.getValue();
                char key = s.charAt(0);
                if (spacing.containsKey(key) || ingredients.containsKey(key)) {
                    throw new RuntimeException("duplicate key '" + key + "'!");
                }
                if (element.isJsonPrimitive()) {
                    spacing.put(key, element.getAsInt());
                } else {
                    Ingredient.CODEC.decode(JsonOps.INSTANCE, element).get().ifLeft(pair -> ingredients.put(key, pair.getFirst()));
                }
            }

            return CenteredPatternedRocketPartRecipeConfig.parse(spacing, ingredients,
                    json.getAsJsonArray("pattern").asList().stream().map(JsonElement::getAsString).toList());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            return null; //fixme
        }
    };

    @NotNull
    public static CenteredPatternedRocketPartRecipeConfig parse(Char2IntMap spacing, Char2ObjectMap<Ingredient> ingredients, List<String> pattern) {
        ImmutableList.Builder<RocketPartRecipeSlot> builder = ImmutableList.builder();
        int[] offset = new int[pattern.size()];
        for (int i = 0; i < offset.length; i++) {
            int x = 0;
            for (char c : pattern.get(i).toCharArray()) {
                if (spacing.containsKey(c)) {
                    x += spacing.get(c);
                } else {
                    assert ingredients.containsKey(c);
                    x += 18;
                }
            }
            offset[i] = x / 2;
        }

        int y = 0;
        for (int i = 0; i < pattern.size(); i++) {
            int x = 0;
            for (char c : pattern.get(i).toCharArray()) {
                if (spacing.containsKey(c)) {
                    x += spacing.get(c);
                } else {
                    builder.add(RocketPartRecipeSlot.create(-offset[i] + x, y, ingredients.get(c)));
                    x += 18;
                }
            }
            y += 18;
        }

        ImmutableList<RocketPartRecipeSlot> slots = builder.build();
        NonNullList<Ingredient> ingredientsList = NonNullList.createWithCapacity(slots.size());
        for (RocketPartRecipeSlot slot : slots) {
            ingredientsList.add(slot.ingredient());
        }

        return new CenteredPatternedRocketPartRecipeConfig(y, slots, ingredientsList);
    }

    public static final Codec<CenteredPatternedRocketPartRecipeConfig> CODEC = new AlternateDecoderCodec<>(PRETTY_DECODER, INTERNAL_CODEC);
}
