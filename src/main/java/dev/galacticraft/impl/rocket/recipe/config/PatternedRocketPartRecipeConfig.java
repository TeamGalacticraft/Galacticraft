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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipeSlot;
import dev.galacticraft.api.rocket.recipe.config.RocketPartRecipeConfig;
import dev.galacticraft.impl.codec.AlternateDecoderCodec;
import dev.galacticraft.impl.codec.JsonDecoder;
import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record PatternedRocketPartRecipeConfig(int width, int height, @NotNull List<RocketPartRecipeSlot> slots, Holder.Reference<? extends RocketPart<?, ?>> output) implements RocketPartRecipeConfig {
    private static final Codec<ResourceKey<? extends RocketPart<?, ?>>> ROCKET_PART_RESOURCE_KEY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("registry").forGetter(ResourceKey::registry),
            ResourceLocation.CODEC.fieldOf("location").forGetter(ResourceKey::location)
    ).apply(instance, (registry, value) -> ResourceKey.create(ResourceKey.createRegistryKey(registry), value)));

    private static final Codec<Holder.Reference<? extends RocketPart<?, ?>>> ARB_PART_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Holder.Reference<? extends RocketPart<?, ?>>, T>> decode(DynamicOps<T> ops, T input) {
            return DataResult.success(Pair.of(ROCKET_PART_RESOURCE_KEY_CODEC.decode(ops, input).map(key -> ((RegistryOps<T>) ops).getter(ResourceKey.<RocketPart<?, ?>>createRegistryKey(key.getFirst().registry())).get().get((ResourceKey<RocketPart<?, ?>>) key.getFirst()).get()).get().orThrow(), input));
        }

        @Override
        public <T> DataResult<T> encode(Holder.Reference<? extends RocketPart<?, ?>> input, DynamicOps<T> ops, T prefix) {
            return ROCKET_PART_RESOURCE_KEY_CODEC.encode(input.key(), ops, prefix);
        }
    };

    private static final Codec<PatternedRocketPartRecipeConfig> INTERNAL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("width").forGetter(PatternedRocketPartRecipeConfig::width),
            Codec.INT.fieldOf("height").forGetter(PatternedRocketPartRecipeConfig::height),
            RocketPartRecipeSlot.CODEC.listOf().fieldOf("slots").forGetter(PatternedRocketPartRecipeConfig::slots),
            ARB_PART_CODEC.fieldOf("output").forGetter(PatternedRocketPartRecipeConfig::output)
    ).apply(instance, PatternedRocketPartRecipeConfig::new));

    public static final Decoder<PatternedRocketPartRecipeConfig> PRETTY_DECODER = (JsonDecoder<PatternedRocketPartRecipeConfig>) (ops, elem) -> {
        JsonObject json = elem.getAsJsonObject();
        JsonObject result = json.getAsJsonObject("result");
        ResourceKey<RocketPart<?, ?>> key1 = ResourceKey.create(
                ResourceKey.createRegistryKey(new ResourceLocation(result.get("type").getAsString())),
                new ResourceLocation(result.get("id").getAsString())
        );

        Holder.Reference<? extends RocketPart<?, ?>> output = ((RegistryOps<?>)ops).getter(ResourceKey.<RocketPart<?, ?>>createRegistryKey(key1.registry())).orElseThrow().get(key1).get();
        Char2ObjectMap<Ingredient> ingredients = new Char2ObjectArrayMap<>();
        Char2IntMap spacing = new Char2IntArrayMap();
        spacing.put(' ', 16);
        json.getAsJsonObject("key").asMap().forEach((s, element) -> {
            char key = s.charAt(0);
            if (spacing.containsKey(key) || ingredients.containsKey(key)) {
                throw new RuntimeException("duplicate key '" + key + "'!");
            }
            if (element.isJsonPrimitive()) {
                spacing.put(key, element.getAsInt());
            } else {

                ingredients.put(key, Ingredient.fromJson(element));
            }
        });
        JsonArray array = json.getAsJsonArray("pattern");
        assert array.size() > 0;

        boolean mirrored = (!json.has("mirrored") && (key1.isFor(RocketRegistries.ROCKET_BOOSTER) || key1.isFor(RocketRegistries.ROCKET_FIN))) || (json.has("mirrored") && json.get("mirrored").getAsBoolean());

        ImmutableList.Builder<RocketPartRecipeSlot> slots = ImmutableList.builder();
        int width = 0;
        int x = mirrored ? 1 : 0;
        int y = 0;
        for (JsonElement element : array) {
            for (char c : element.getAsString().toCharArray()) {
                if (spacing.containsKey(c)) {
                    x += spacing.get(c);
                } else {
                    slots.add(RocketPartRecipeSlot.create(x, y, ingredients.get(c)));
                    if (mirrored) slots.add(RocketPartRecipeSlot.create(-x, y, ingredients.get(c)));
                    x += 18;
                }
            }
            width = Math.max(width, x);
            y += 18;
            x = mirrored ? 1 : 0;
        }

        return new PatternedRocketPartRecipeConfig(width, y, slots.build(), output);
    };

    public static final Codec<PatternedRocketPartRecipeConfig> CODEC = new AlternateDecoderCodec<>(PRETTY_DECODER, INTERNAL_CODEC);
}
