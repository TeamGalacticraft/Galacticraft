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

package dev.galacticraft.api.rocket.recipe;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.registry.BuiltInRocketRegistries;
import dev.galacticraft.api.rocket.recipe.config.RocketPartRecipeConfig;
import dev.galacticraft.api.rocket.recipe.type.RocketPartRecipeType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record RocketPartRecipe<C extends RocketPartRecipeConfig, T extends RocketPartRecipeType<C>>(T type, C config) implements Recipe<RecipeInput> {
    public static final Codec<RocketPartRecipe<?, ?>> DIRECT_CODEC = BuiltInRocketRegistries.ROCKET_PART_RECIPE_TYPE.byNameCodec().dispatch(RocketPartRecipe::type, RocketPartRecipeType::codec);

    @Contract("_, _ -> new")
    public static <C extends RocketPartRecipeConfig, T extends RocketPartRecipeType<C>> @NotNull RocketPartRecipe<C, RocketPartRecipeType<C>> create(C config, T type) {
        return new RocketPartRecipe<>(type, config);
    }

    public int slots() {
        return this.type.slots(this.config);
    }

    public int height() {
        return this.type.height(this.config);
    }

    public void place(@NotNull RocketPartRecipeType.SlotConsumer consumer, int leftEdge, int rightEdge, int bottomEdge) {
        this.type.place(consumer, leftEdge, rightEdge, bottomEdge, this.config);
    }

    @Override
    @NotNull
    public NonNullList<Ingredient> getIngredients() {
        return this.type.ingredients(this.config);
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return this.type.matches(input, level, this.config);
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput input, HolderLookup.Provider lookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        throw new AssertionError();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        throw new AssertionError();
    }

    static void bootstrapRegistries(BootstrapContext<RocketPartRecipe<?, ?>> context) {

    }
}
