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

package dev.galacticraft.impl.rocket.travelpredicate.type;

import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.rocket.travelpredicate.config.ConstantTravelPredicateConfig;
import net.minecraft.core.Holder;

public final class ConstantTravelPredicateType extends TravelPredicateType<ConstantTravelPredicateConfig> {
    public static final ConstantTravelPredicateType INSTANCE = new ConstantTravelPredicateType();

    private ConstantTravelPredicateType() {
        super(ConstantTravelPredicateConfig.CODEC);
    }

    @Override
    public Result canTravel(CelestialBody<?, ?> from, CelestialBody<?, ?> to, Holder<RocketCone<?, ?>> cone, Holder<RocketBody<?, ?>> body, Holder<RocketFin<?, ?>> fin, Holder<RocketBooster<?, ?>> booster, Holder<RocketEngine<?, ?>> engine, Holder<RocketUpgrade<?, ?>> upgrade, ConstantTravelPredicateConfig config) {
        return config.type();
    }
}
