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

package dev.galacticraft.api.rocket.travelpredicate;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import net.minecraft.resources.ResourceLocation;

public record ConfiguredTravelPredicate<C extends TravelPredicateConfig>(C config, TravelPredicateType<C> type) {
    public static final Codec<ConfiguredTravelPredicate<?>> CODEC = AddonRegistry.TRAVEL_PREDICATE.byNameCodec().dispatch(ConfiguredTravelPredicate::type, TravelPredicateType::codec);

    public TravelPredicateType.AccessType canTravelTo(CelestialBody<?, ?> type, Object2BooleanFunction<ResourceLocation> parts) {
        return this.type.canTravelTo(type, parts, this.config);
    }
}
