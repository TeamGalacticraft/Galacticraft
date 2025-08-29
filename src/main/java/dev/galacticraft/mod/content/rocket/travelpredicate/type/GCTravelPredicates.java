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

package dev.galacticraft.mod.content.rocket.travelpredicate.type;

import dev.galacticraft.api.registry.BuiltInRocketRegistries;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.mod.Constant.TravelPredicate;
import dev.galacticraft.mod.content.GCRegistry;
import dev.galacticraft.mod.content.rocket.travelpredicate.config.AccessWeightTravelPredicateConfig;
import dev.galacticraft.mod.content.rocket.travelpredicate.config.AndTravelPredicateConfig;
import dev.galacticraft.mod.content.rocket.travelpredicate.config.OrTravelPredicateConfig;

public class GCTravelPredicates {
    public static final GCRegistry<TravelPredicateType<?>> TRAVEL_PREDICATES = new GCRegistry<>(BuiltInRocketRegistries.TRAVEL_PREDICATE_TYPE);

    public static final TravelPredicateType<?> ACCESS_WEIGHT = TRAVEL_PREDICATES.register(TravelPredicate.ACCESS_WEIGHT, new AccessWeightTravelPredicateType(AccessWeightTravelPredicateConfig.CODEC));
    public static final TravelPredicateType<?> CONSTANT = TRAVEL_PREDICATES.register(TravelPredicate.CONSTANT, new ConstantTravelPredicateType());
    public static final TravelPredicateType<?> AND = TRAVEL_PREDICATES.register(TravelPredicate.AND, new AndTravelPredicateType(AndTravelPredicateConfig.CODEC));
    public static final TravelPredicateType<?> OR = TRAVEL_PREDICATES.register(TravelPredicate.OR, new OrTravelPredicateType(OrTravelPredicateConfig.CODEC));

    public static void register() {}
}
