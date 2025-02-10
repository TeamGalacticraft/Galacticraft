/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.api.rocket.recipe.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.api.rocket.recipe.config.RocketPartRecipeConfig;
import dev.galacticraft.machinelib.api.filter.ResourceFilter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class RocketPartRecipeType<C extends RocketPartRecipeConfig> {
    private final @NotNull MapCodec<RocketPartRecipe<C, RocketPartRecipeType<C>>> codec;

    protected RocketPartRecipeType(@NotNull Codec<C> configCodec) {
        this.codec = configCodec.fieldOf("config").xmap(this::configure, RocketPartRecipe::config);
    }

    public @NotNull MapCodec<RocketPartRecipe<C, RocketPartRecipeType<C>>> codec() {
        return this.codec;
    }

    public @NotNull RocketPartRecipe<C, RocketPartRecipeType<C>> configure(@NotNull C config) {
        return RocketPartRecipe.create(config, this);
    }

    public abstract int slots(C config);

    public abstract int height(C config);

    public abstract void place(@NotNull SlotConsumer consumer, int leftEdge, int rightEdge, int bottomEdge, C config);

    public abstract @NotNull NonNullList<Ingredient> ingredients(C config);

    public abstract boolean matches(RecipeInput input, Level level, C config);

    @FunctionalInterface
    public interface SlotConsumer {
        void createSlot(int index, int x, int y, ResourceFilter<Item> filter);
    }
}
