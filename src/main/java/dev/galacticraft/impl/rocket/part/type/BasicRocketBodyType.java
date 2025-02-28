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

package dev.galacticraft.impl.rocket.part.type;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.api.rocket.part.type.RocketBodyType;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.api.rocket.travelpredicate.ConfiguredTravelPredicate;
import dev.galacticraft.impl.rocket.part.config.BasicRocketBodyConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BasicRocketBodyType extends RocketBodyType<BasicRocketBodyConfig> {
    public static final BasicRocketBodyType INSTANCE = new BasicRocketBodyType(BasicRocketBodyConfig.CODEC);

    private BasicRocketBodyType(@NotNull Codec<BasicRocketBodyConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public int getMaxPassengers(@NotNull BasicRocketBodyConfig config) {
        return config.maxPassengers();
    }

    @Override
    public void tick(@NotNull Rocket rocket, @NotNull BasicRocketBodyConfig config) {
    }

    @Override
    public @NotNull ConfiguredTravelPredicate<?, ?> travelPredicate(@NotNull BasicRocketBodyConfig config) {
        return config.predicate();
    }

    @Override
    public @Nullable RocketPartRecipe<?, ?> getRecipe(BasicRocketBodyConfig config) {
        return config.recipe();
    }
}
