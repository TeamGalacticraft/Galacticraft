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

package dev.galacticraft.api.satellite;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.impl.satellite.SatelliteRecipeImpl;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface SatelliteRecipe extends Predicate<Container> {

    Codec<SatelliteRecipe> CODEC = RecordCodecBuilder.create(i -> i.group(
            new Codec<Int2ObjectMap<Ingredient>>() {
                @Override
                public <T> DataResult<T> encode(Int2ObjectMap<Ingredient> input, DynamicOps<T> ops, T prefix) {
                    RecordBuilder<T> mapBuilder = ops.mapBuilder();
                    input.forEach((amount, ingredient) -> mapBuilder.add(ops.createInt(amount).toString(), Ingredient.CODEC.encodeStart(ops, ingredient).get().orThrow()));
                    return mapBuilder.build(prefix);
                }

                @Override
                public <T> DataResult<Pair<Int2ObjectMap<Ingredient>, T>> decode(DynamicOps<T> ops, T input) {
                    MapLike<T> mapLike = ops.getMap(input).get().orThrow();
                    Int2ObjectMap<Ingredient> list = new Int2ObjectArrayMap<>();
                    mapLike.entries().forEachOrdered(ttPair -> list.put(Integer.decode(ops.getStringValue(ttPair.getFirst()).get().orThrow()).intValue(), Ingredient.CODEC.decode(ops, ttPair.getSecond()).get().orThrow().getFirst()));
                    return DataResult.success(new Pair<>(list, input));
                }
            }.fieldOf("ingredients").forGetter(SatelliteRecipe::ingredients)
    ).apply(i, SatelliteRecipe::create));

    @Contract(value = "_ -> new", pure = true)
    static @NotNull SatelliteRecipe create(Int2ObjectMap<Ingredient> list) {
        return new SatelliteRecipeImpl(list);
    }

    Int2ObjectMap<Ingredient> ingredients();

    @Override
    boolean test(@NotNull Container inventory);
}
