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

package dev.galacticraft.api.registry;

import com.mojang.serialization.Lifecycle;
import dev.galacticraft.api.rocket.part.type.*;
import dev.galacticraft.api.rocket.recipe.type.RocketPartRecipeType;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.impl.rocket.part.type.*;
import dev.galacticraft.impl.rocket.recipe.type.CenteredPatternedRocketPartRecipeType;
import dev.galacticraft.impl.rocket.recipe.type.PatternedRocketPartRecipeType;
import dev.galacticraft.impl.rocket.travelpredicate.type.*;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.rocket.part.type.StorageUpgradeType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;

public final class BuiltInRocketRegistries {
    public static final WritableRegistry<TravelPredicateType<?>> TRAVEL_PREDICATE_TYPE = FabricRegistryBuilder.from(
            new MappedRegistry<>(RocketRegistries.TRAVEL_PREDICATE_TYPE,
                    Lifecycle.experimental(),
                    true
            )).buildAndRegister();

    public static final WritableRegistry<RocketConeType<?>> ROCKET_CONE_TYPE = FabricRegistryBuilder.createSimple(
            RocketRegistries.ROCKET_CONE_TYPE
    ).buildAndRegister();

    public static final WritableRegistry<RocketBodyType<?>> ROCKET_BODY_TYPE = FabricRegistryBuilder.createSimple(
            RocketRegistries.ROCKET_BODY_TYPE
    ).buildAndRegister();

    public static final WritableRegistry<RocketFinType<?>> ROCKET_FIN_TYPE = FabricRegistryBuilder.createSimple(
            RocketRegistries.ROCKET_FIN_TYPE
    ).buildAndRegister();

    public static final WritableRegistry<RocketBoosterType<?>> ROCKET_BOOSTER_TYPE = FabricRegistryBuilder.createSimple(
            RocketRegistries.ROCKET_BOOSTER_TYPE
    ).buildAndRegister();

    public static final WritableRegistry<RocketEngineType<?>> ROCKET_ENGINE_TYPE = FabricRegistryBuilder.createSimple(
            RocketRegistries.ROCKET_ENGINE_TYPE
    ).buildAndRegister();

    public static final WritableRegistry<RocketUpgradeType<?>> ROCKET_UPGRADE_TYPE = FabricRegistryBuilder.createSimple(
            RocketRegistries.ROCKET_UPGRADE_TYPE
    ).buildAndRegister();


    public static final WritableRegistry<RocketPartRecipeType<?>> ROCKET_PART_RECIPE_TYPE = FabricRegistryBuilder.createSimple(
            RocketRegistries.ROCKET_PART_RECIPE_TYPE
    ).buildAndRegister();

    public static void initialize() {
    }

    static {
        Registry.register(TRAVEL_PREDICATE_TYPE, Constant.id("default"), DefaultTravelPredicateType.INSTANCE);
        Registry.register(TRAVEL_PREDICATE_TYPE, Constant.id("access_weight"), AccessWeightTravelPredicateType.INSTANCE);
        Registry.register(TRAVEL_PREDICATE_TYPE, Constant.id("constant"), ConstantTravelPredicateType.INSTANCE);
        Registry.register(TRAVEL_PREDICATE_TYPE, Constant.id("and"), AndTravelPredicateType.INSTANCE);
        Registry.register(TRAVEL_PREDICATE_TYPE, Constant.id("or"), OrTravelPredicateType.INSTANCE);

        Registry.register(ROCKET_CONE_TYPE, Constant.id("basic"), BasicRocketConeType.INSTANCE);
        Registry.register(ROCKET_BODY_TYPE, Constant.id("basic"), BasicRocketBodyType.INSTANCE);
        Registry.register(ROCKET_FIN_TYPE, Constant.id("basic"), BasicRocketFinType.INSTANCE);
        Registry.register(ROCKET_BOOSTER_TYPE, Constant.id("basic"), BasicRocketBoosterType.INSTANCE);
        Registry.register(ROCKET_ENGINE_TYPE, Constant.id("basic"), BasicRocketEngineType.INSTANCE);
        Registry.register(ROCKET_UPGRADE_TYPE, Constant.id("storage"), StorageUpgradeType.INSTANCE);

        Registry.register(ROCKET_PART_RECIPE_TYPE, Constant.id("wrap_patterned"), PatternedRocketPartRecipeType.INSTANCE);
        Registry.register(ROCKET_PART_RECIPE_TYPE, Constant.id("centered_patterned"), CenteredPatternedRocketPartRecipeType.INSTANCE);
    }
}