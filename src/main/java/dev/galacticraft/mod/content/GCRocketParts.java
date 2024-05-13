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

package dev.galacticraft.mod.content;

import dev.galacticraft.api.data.RocketPartRecipeBuilder;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.impl.rocket.part.config.*;
import dev.galacticraft.impl.rocket.part.type.*;
import dev.galacticraft.impl.rocket.travelpredicate.config.AccessWeightTravelPredicateConfig;
import dev.galacticraft.impl.rocket.travelpredicate.type.AccessWeightTravelPredicateType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.content.rocket.part.config.StorageUpgradeConfig;
import dev.galacticraft.mod.content.rocket.part.type.StorageUpgradeType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class GCRocketParts {
    public static final ResourceKey<RocketCone<?, ?>> TIER_1_CONE = cone("tier_1");
    public static final ResourceKey<RocketBody<?, ?>> TIER_1_BODY = body("tier_1");
    public static final ResourceKey<RocketFin<?, ?>> TIER_1_FIN = fin("tier_1");
    public static final ResourceKey<RocketBooster<?, ?>> TIER_1_BOOSTER = booster("tier_1");
    public static final ResourceKey<RocketEngine<?, ?>> TIER_1_ENGINE = engine("tier_1");

    public static final ResourceKey<RocketCone<?, ?>> ADVANCED_CONE = cone("advanced_cone"); //todo implement these again
    public static final ResourceKey<RocketCone<?, ?>> SLOPED_CONE = cone("sloped_cone");

    public static final ResourceKey<RocketBooster<?, ?>> TIER_2_BOOSTER = booster("tier_2");
    public static final ResourceKey<RocketUpgrade<?, ?>> STORAGE_UPGRADE = upgrade("storage");

    public static void bootstrapCone(BootstapContext<RocketCone<?, ?>> context) {
        context.register(TIER_1_CONE,
                BasicRocketConeType.INSTANCE.configure(
                        new BasicRocketConeConfig(
                                AccessWeightTravelPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(1, TravelPredicateType.Result.PASS)),
                                RocketPartRecipeBuilder.create()
                                        .define('T', Ingredient.of(Items.REDSTONE_TORCH))
                                        .define('D', Ingredient.of(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                                        .center("T")
                                        .center("D")
                                        .center("DD")
                                        .build()
                        )
                )
        );
        context.register(SLOPED_CONE,
                BasicRocketConeType.INSTANCE.configure(
                        new BasicRocketConeConfig(
                                AccessWeightTravelPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(1, TravelPredicateType.Result.PASS)),
                                RocketPartRecipeBuilder.create()
                                        .define('T', Ingredient.of(Items.REDSTONE_TORCH))
                                        .define('D', Ingredient.of(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                                        .center("T")
                                        .center("DD")
                                        .center("DD")
                                        .build()
                        )
                )
        );
        context.register(ADVANCED_CONE,
                BasicRocketConeType.INSTANCE.configure(
                        new BasicRocketConeConfig(
                                AccessWeightTravelPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(1, TravelPredicateType.Result.PASS)),
                                RocketPartRecipeBuilder.create()
                                        .define('T', Ingredient.of(Items.REDSTONE_TORCH))
                                        .define('D', Ingredient.of(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                                        .center("T")
                                        .center("DD")
                                        .center("DD")
                                        .build()
                        )
                )
        );
    }

    public static void bootstrapBody(BootstapContext<RocketBody<?, ?>> context) {
        context.register(TIER_1_BODY,
                BasicRocketBodyType.INSTANCE.configure(new BasicRocketBodyConfig(
                        AccessWeightTravelPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(1, TravelPredicateType.Result.PASS)),
                        1,
                        RocketPartRecipeBuilder.create()
                                .define('D', Ingredient.of(GCItems.TIER_1_HEAVY_DUTY_PLATE))
                                .left("D")
                                .left("D")
                                .left("D")
                                .left("D")
                                .right("D")
                                .right("D")
                                .right("D")
                                .right("D")
                                .build()
                ))
        );
    }

    public static void bootstrapFin(BootstapContext<RocketFin<?, ?>> context) {
        context.register(TIER_1_FIN,
                BasicRocketFinType.INSTANCE.configure(
                        new BasicRocketFinConfig(
                                AccessWeightTravelPredicateType.INSTANCE.configure(
                                        new AccessWeightTravelPredicateConfig(1, TravelPredicateType.Result.PASS)
                                ),
                                false,
                                RocketPartRecipeBuilder.create()
                                        .define('F', Ingredient.of(GCItems.ROCKET_FIN))
                                        .left("F")
                                        .left("F")
                                        .right("F")
                                        .right("F")
                                        .build()
                        )
                )
        );
    }

    public static void bootstrapBooster(BootstapContext<RocketBooster<?, ?>> context) {
        context.register(TIER_1_BOOSTER,
                BasicRocketBoosterType.INSTANCE.configure(
                        new BasicRocketBoosterConfig(
                                AccessWeightTravelPredicateType.INSTANCE.configure(
                                        new AccessWeightTravelPredicateConfig(1, TravelPredicateType.Result.PASS)
                                ),
                                0,
                                0,
                                0,
                                null //fixme no t1 booster (t2 only!)
                        )
                )
        );
    }

    public static void bootstrapEngine(BootstapContext<RocketEngine<?, ?>> context) {
        context.register(TIER_1_ENGINE,
                BasicRocketEngineType.INSTANCE.configure(
                        new BasicRocketEngineConfig(
                                AccessWeightTravelPredicateType.INSTANCE.configure(
                                        new AccessWeightTravelPredicateConfig(1, TravelPredicateType.Result.PASS)
                                ),
                                FluidConstants.BUCKET * 16,
                                RocketPartRecipeBuilder.create()
                                        .define('E', Ingredient.of(GCItems.ROCKET_ENGINE))
                                        .center("E")
                                        .build()
                        )
                )
        );
    }

    public static void bootstrapUpgrade(BootstapContext<RocketUpgrade<?, ?>> context) {
        context.register(STORAGE_UPGRADE, RocketUpgrade.create(new StorageUpgradeConfig(1,
                RocketPartRecipeBuilder.create()
                        .define('C', Ingredient.of(Items.CHEST))
                        .center("C")
                        .build()), StorageUpgradeType.INSTANCE));
    }

    @Contract(pure = true)
    private static @NotNull ResourceKey<RocketCone<?, ?>> cone(@NotNull String id) {
        return Constant.key(RocketRegistries.ROCKET_CONE, id);
    }

    @Contract(pure = true)
    private static @NotNull ResourceKey<RocketBody<?, ?>> body(@NotNull String id) {
        return Constant.key(RocketRegistries.ROCKET_BODY, id);
    }

    @Contract(pure = true)
    private static @NotNull ResourceKey<RocketFin<?, ?>> fin(@NotNull String id) {
        return Constant.key(RocketRegistries.ROCKET_FIN, id);
    }

    @Contract(pure = true)
    private static @NotNull ResourceKey<RocketBooster<?, ?>> booster(@NotNull String id) {
        return Constant.key(RocketRegistries.ROCKET_BOOSTER, id);
    }

    @Contract(pure = true)
    private static @NotNull ResourceKey<RocketEngine<?, ?>> engine(@NotNull String id) {
        return Constant.key(RocketRegistries.ROCKET_ENGINE, id);
    }

    @Contract(pure = true)
    private static @NotNull ResourceKey<RocketUpgrade<?, ?>> upgrade(@NotNull String id) {
        return Constant.key(RocketRegistries.ROCKET_UPGRADE, id);
    }
}
