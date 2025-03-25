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

package dev.galacticraft.api.rocket.part.type;

import com.mojang.serialization.MapCodec;
import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.config.RocketPartConfig;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.api.rocket.travelpredicate.ConfiguredTravelPredicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base rocket part interface.
 * To create a custom rocket part, extend {@link RocketEngineType}, {@link RocketBoosterType}, {@link RocketEngineType}, {@link RocketConeType}, {@link RocketFinType}, or {@link RocketUpgradeType}
 */
public sealed interface RocketPartType<C extends RocketPartConfig> permits RocketBodyType, RocketBoosterType, RocketEngineType, RocketConeType, RocketFinType, RocketUpgradeType {

    @NotNull RocketPart<C, ? extends RocketPartType<C>> configure(@NotNull C config);

    /**
     * Called every tick when this part is applied to a placed rocket.
     * The rocket may not have launched yet.
     *
     * @param rocket the rocket that this part is a part of.
     */
    void tick(@NotNull Rocket rocket, @NotNull C config);

    @NotNull MapCodec<? extends RocketPart<C, ? extends RocketPartType<C>>> codec();

    @NotNull ConfiguredTravelPredicate<?, ?> travelPredicate(@NotNull C config);

    @Nullable RocketPartRecipe<?, ?> getRecipe(C config);
}
