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
import dev.galacticraft.api.rocket.recipe.config.RocketPartRecipeConfig;
import dev.galacticraft.api.rocket.recipe.type.RocketPartRecipeType;
import dev.galacticraft.impl.rocket.recipe.RocketPartRecipeImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RocketPartRecipe<C extends RocketPartRecipeConfig, T extends RocketPartRecipeType<C>> {
    Codec<RocketPartRecipe<?, ?>> DIRECT_CODEC = BuiltInRocketRegistries.ROCKET_PART_RECIPE_TYPE.byNameCodec().dispatch(RocketPartRecipe::type, RocketPartRecipeType::codec);
    Codec<Holder<RocketPartRecipe<?, ?>>> CODEC = RegistryFileCodec.create(RocketRegistries.ROCKET_PART_RECIPE, DIRECT_CODEC);
    Codec<HolderSet<RocketPartRecipe<?, ?>>> LIST_CODEC = RegistryCodecs.homogeneousList(RocketRegistries.ROCKET_PART_RECIPE, DIRECT_CODEC);

    @Contract("_, _ -> new")
    static <C extends RocketPartRecipeConfig, T extends RocketPartRecipeType<C>> @NotNull RocketPartRecipe<C, RocketPartRecipeType<C>> create(C config, T type) {
        return new RocketPartRecipeImpl<>(type, config);
    }

    @NotNull T type();

    @NotNull C config();

    // PIXELS
    int width();

    int height();

    @NotNull List<RocketPartRecipeSlot> slots();

    ResourceKey<? extends RocketPart<?, ?>> output();

    static void bootstrapRegistries(BootstapContext<RocketPartRecipe<?, ?>> context) {

    }
}
