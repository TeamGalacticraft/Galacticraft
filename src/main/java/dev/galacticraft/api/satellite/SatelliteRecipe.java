/*
 * Copyright (c) 2019-2026 Team Galacticraft
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
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public interface SatelliteRecipe extends Predicate<Container> {

    Codec<Pair<Ingredient, Integer>> INGREDIENT_CODEC = RecordCodecBuilder.create(i -> i.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(Pair::getFirst),
            Codec.INT.fieldOf("count").forGetter(Pair::getSecond)
    ).apply(i, Pair::new));

    Codec<SatelliteRecipe> CODEC = RecordCodecBuilder.create(i -> i.group(
            INGREDIENT_CODEC.listOf().fieldOf("ingredients").forGetter(SatelliteRecipe::ingredients)
    ).apply(i, SatelliteRecipe::create));

    @Contract(value = "_ -> new", pure = true)
    static @NotNull SatelliteRecipe create(List<Pair<Ingredient, Integer>> list) {
        return new SatelliteRecipeImpl(list);
    }

    List<Pair<Ingredient, Integer>> ingredients();

    @Override
    boolean test(@NotNull Container inventory);
}
