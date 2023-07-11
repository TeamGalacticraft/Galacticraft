/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.impl.rocket.travelpredicate.type;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.Tiered;
import dev.galacticraft.impl.rocket.travelpredicate.config.AccessWeightTravelPredicateConfig;

public final class AccessWeightTravelPredicateType extends TravelPredicateType<AccessWeightTravelPredicateConfig> {
    public static final AccessWeightTravelPredicateType INSTANCE = new AccessWeightTravelPredicateType(AccessWeightTravelPredicateConfig.CODEC);

    private AccessWeightTravelPredicateType(Codec<AccessWeightTravelPredicateConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public Result canTravel(CelestialBody<?, ?> from, CelestialBody<?, ?> to, RocketCone<?, ?> cone, RocketBody<?, ?> body, RocketFin<?, ?> fin, RocketBooster<?, ?> booster, RocketBottom<?, ?> bottom, RocketUpgrade<?, ?>[] upgrades, AccessWeightTravelPredicateConfig config) {
        if (to.type() instanceof Tiered tiered) {
            int weight = tiered.accessWeight(to.config());
            if (weight <= config.weight()) {
                return Result.ALLOW;
            } else {
                return Result.BLOCK;
            }
        }
        return config.defaultType();
    }
}
