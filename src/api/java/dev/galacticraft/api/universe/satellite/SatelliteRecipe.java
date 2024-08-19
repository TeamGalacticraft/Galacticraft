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

package dev.galacticraft.api.universe.satellite;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SatelliteRecipe implements Predicate<Container> {
    public static final Codec<SatelliteRecipe> CODEC = RecordCodecBuilder.create(i -> i.group(
            new Codec<Int2ObjectMap<Ingredient>>() {
                @Override
                public <T> DataResult<T> encode(Int2ObjectMap<Ingredient> input, DynamicOps<T> ops, T prefix) {
                    RecordBuilder<T> mapBuilder = ops.mapBuilder();
                    input.forEach((amount, ingredient) -> mapBuilder.add(ops.createInt(amount).toString(), Ingredient.CODEC.encodeStart(ops, ingredient).getOrThrow()));
                    return mapBuilder.build(prefix);
                }

                @Override
                public <T> DataResult<Pair<Int2ObjectMap<Ingredient>, T>> decode(DynamicOps<T> ops, T input) {
                    MapLike<T> mapLike = ops.getMap(input).getOrThrow();
                    Int2ObjectMap<Ingredient> list = new Int2ObjectArrayMap<>();
                    mapLike.entries().forEachOrdered(ttPair -> list.put(Integer.decode(ops.getStringValue(ttPair.getFirst()).getOrThrow()).intValue(), Ingredient.CODEC.decode(ops, ttPair.getSecond()).getOrThrow().getFirst()));
                    return DataResult.success(new Pair<>(list, input));
                }
            }.fieldOf("ingredients").forGetter(SatelliteRecipe::ingredients)
    ).apply(i, SatelliteRecipe::create));

    private final Int2ObjectMap<Ingredient> ingredients;

    public SatelliteRecipe(Int2ObjectMap<Ingredient> list) {
        this.ingredients = list;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull SatelliteRecipe create(Int2ObjectMap<Ingredient> list) {
        return new SatelliteRecipe(list);
    }

    public Int2ObjectMap<Ingredient> ingredients() {
        return ingredients;
    }

    @Override
    public boolean test(@NotNull Container inventory) {
        IntList slotModifiers = new IntArrayList(inventory.getContainerSize());

        for (Int2ObjectMap.Entry<Ingredient> ingredient : this.ingredients.int2ObjectEntrySet()) {
            int amount = ingredient.getIntKey();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (ingredient.getValue().test(stack)) {
                    amount -= (stack.getCount() - slotModifiers.getInt(i));
                    slotModifiers.set(i, slotModifiers.getInt(i) + (stack.getCount() - slotModifiers.getInt(i)) - Math.min(amount, 0));
                    if (amount <= 0) break;
                }
            }
            if (amount > 0) return false;
        }
        return true;
    }
}
