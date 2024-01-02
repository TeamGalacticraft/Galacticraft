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

package dev.galacticraft.impl.rocket.part.type;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.api.rocket.part.type.RocketBoosterType;
import dev.galacticraft.api.rocket.travelpredicate.ConfiguredTravelPredicate;
import dev.galacticraft.impl.rocket.part.config.DefaultRocketBoosterConfig;
import dev.galacticraft.impl.rocket.travelpredicate.type.DefaultTravelPredicateType;
import org.jetbrains.annotations.NotNull;

public final class InvalidRocketBoosterType extends RocketBoosterType<DefaultRocketBoosterConfig> {
    public static final InvalidRocketBoosterType INSTANCE = new InvalidRocketBoosterType(DefaultRocketBoosterConfig.CODEC);

    private InvalidRocketBoosterType(@NotNull Codec<DefaultRocketBoosterConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public double getMaximumVelocity(@NotNull DefaultRocketBoosterConfig config) {
        return 0;
    }

    @Override
    public double getAccelerationPerTick(@NotNull DefaultRocketBoosterConfig config) {
        return 0;
    }

    @Override
    public long getFuelUsagePerTick(@NotNull DefaultRocketBoosterConfig config) {
        return 0;
    }

    @Override
    public void tick(@NotNull Rocket rocket, @NotNull DefaultRocketBoosterConfig config) {

    }

    @Override
    public @NotNull ConfiguredTravelPredicate<?, ?> travelPredicate(@NotNull DefaultRocketBoosterConfig config) {
        return DefaultTravelPredicateType.CONFIGURED;
    }
}
