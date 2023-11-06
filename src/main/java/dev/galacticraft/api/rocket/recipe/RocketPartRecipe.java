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

package dev.galacticraft.api.rocket.recipe;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.registry.BuiltInRocketRegistries;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartTypes;
import dev.galacticraft.api.rocket.recipe.config.RocketPartRecipeConfig;
import dev.galacticraft.api.rocket.recipe.type.RocketPartRecipeType;
import dev.galacticraft.impl.rocket.recipe.RocketPartRecipeImpl;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RocketPartRecipe<C extends RocketPartRecipeConfig, T extends RocketPartRecipeType<C>> extends Recipe<Container> {
    Codec<RocketPartRecipe<?, ?>> DIRECT_CODEC = BuiltInRocketRegistries.ROCKET_PART_RECIPE_TYPE.byNameCodec().dispatch(RocketPartRecipe::type, RocketPartRecipeType::codec);
    Codec<Holder<RocketPartRecipe<?, ?>>> CODEC = RegistryFileCodec.create(RocketRegistries.ROCKET_PART_RECIPE, DIRECT_CODEC);
    Codec<HolderSet<RocketPartRecipe<?, ?>>> LIST_CODEC = RegistryCodecs.homogeneousList(RocketRegistries.ROCKET_PART_RECIPE, DIRECT_CODEC);

    @Contract("_, _ -> new")
    static <C extends RocketPartRecipeConfig, T extends RocketPartRecipeType<C>> @NotNull RocketPartRecipe<C, RocketPartRecipeType<C>> create(C config, T type) {
        return new RocketPartRecipeImpl<>(type, config);
    }

    RocketPartTypes partType();

    @NotNull T type();

    @NotNull C config();

    // PIXELS
    int width();

    int height();

    @NotNull List<RocketPartRecipeSlot> slots();

    @Override
    NonNullList<Ingredient> getIngredients();

    Holder.Reference<? extends RocketPart<?, ?>> output();

    @Override
    default boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    default ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    default ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    default ResourceLocation getId() {
        throw new AssertionError();
    }

    @Override
    default RecipeSerializer<?> getSerializer() {
        throw new AssertionError();
    }

    @Override
    default RecipeType<?> getType() {
        throw new AssertionError();
    }

    static void bootstrapRegistries(BootstapContext<RocketPartRecipe<?, ?>> context) {

    }
}
