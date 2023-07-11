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

package dev.galacticraft.api.registry;

import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.rocket.part.type.*;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import dev.galacticraft.api.rocket.recipe.type.RocketPartRecipeType;
import dev.galacticraft.api.rocket.travelpredicate.ConfiguredTravelPredicate;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class RocketRegistries {
    public static final ResourceKey<Registry<TravelPredicateType<?>>> TRAVEL_PREDICATE_TYPE = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "travel_predicate_type"));
    public static final ResourceKey<Registry<ConfiguredTravelPredicate<?, ?>>> TRAVEL_PREDICATE = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "travel_predicate"));

    public static final ResourceKey<Registry<RocketConeType<?>>> ROCKET_CONE_TYPE = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "rocket_cone_type"));
    public static final ResourceKey<Registry<RocketBodyType<?>>> ROCKET_BODY_TYPE = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "rocket_body_type"));
    public static final ResourceKey<Registry<RocketFinType<?>>> ROCKET_FIN_TYPE = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "rocket_fin_type"));
    public static final ResourceKey<Registry<RocketBoosterType<?>>> ROCKET_BOOSTER_TYPE = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "rocket_booster_type"));
    public static final ResourceKey<Registry<RocketBottomType<?>>> ROCKET_BOTTOM_TYPE = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "rocket_bottom_type"));
    public static final ResourceKey<Registry<RocketUpgradeType<?>>> ROCKET_UPGRADE_TYPE = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "rocket_upgrade_type"));
    public static final ResourceKey<Registry<RocketPartRecipeType<?>>> ROCKET_PART_RECIPE_TYPE = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "rocket_part_recipe_type"));

    public static final ResourceKey<Registry<RocketCone<?, ?>>> ROCKET_CONE = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "rocket_cone"));
    public static final ResourceKey<Registry<RocketBody<?, ?>>> ROCKET_BODY = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "rocket_body"));
    public static final ResourceKey<Registry<RocketFin<?, ?>>> ROCKET_FIN = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "rocket_fin"));
    public static final ResourceKey<Registry<RocketBooster<?, ?>>> ROCKET_BOOSTER = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "rocket_booster"));
    public static final ResourceKey<Registry<RocketBottom<?, ?>>> ROCKET_BOTTOM = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "rocket_bottom"));
    public static final ResourceKey<Registry<RocketUpgrade<?, ?>>> ROCKET_UPGRADE = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "rocket_upgrade"));
    public static final ResourceKey<Registry<RocketPartRecipe<?, ?>>> ROCKET_PART_RECIPE = ResourceKey.createRegistryKey(new ResourceLocation(Constant.MOD_ID, "rocket_part_recipe"));
}
