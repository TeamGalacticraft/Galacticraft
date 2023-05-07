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

package dev.galacticraft.mod.content;

import dev.galacticraft.api.registry.BuiltInRocketRegistries;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.rocket.part.type.RocketConeType;
import dev.galacticraft.api.rocket.travelpredicate.ConfiguredTravelPredicate;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.impl.rocket.part.RocketConeImpl;
import dev.galacticraft.impl.rocket.part.config.*;
import dev.galacticraft.impl.rocket.part.type.*;
import dev.galacticraft.impl.rocket.travelpredicate.config.AccessWeightTravelPredicateConfig;
import dev.galacticraft.impl.rocket.travelpredicate.type.AccessWeightTravelPredicateType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.rocket.part.config.StorageUpgradeConfig;
import dev.galacticraft.mod.content.rocket.part.type.StorageUpgradeType;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.http.config.Registry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class GCRocketParts {
    public static final ResourceKey<RocketCone<?, ?>> TIER_1_CONE = cone("tier_1");
    public static final ResourceKey<RocketBody<?, ?>> TIER_1_BODY = body("tier_1");
    public static final ResourceKey<RocketFin<?, ?>> TIER_1_FIN = fin("tier_1");
    public static final ResourceKey<RocketBooster<?, ?>> TIER_1_BOOSTER = booster("tier_1");
    public static final ResourceKey<RocketBottom<?, ?>> TIER_1_BOTTOM = bottom("tier_1");

    public static final ResourceKey<RocketCone<?, ?>> ADVANCED_CONE = cone("advanced_cone"); //todo implement these again
    public static final ResourceKey<RocketCone<?, ?>> SLOPED_CONE = cone("sloped_cone");

    public static final ResourceKey<RocketBooster<?, ?>> TIER_2_BOOSTER = booster("tier_2");
    public static final ResourceKey<RocketUpgrade<?, ?>> STORAGE_UPGRADE = upgrade("storage");

    public static void bootstrapCone(BootstapContext<RocketCone<?, ?>> context) {
        context.register(TIER_1_CONE,
                RocketCone.create(
                        new BasicRocketConeConfig(
                                new ConfiguredTravelPredicate<>(
                                        new AccessWeightTravelPredicateConfig(1, TravelPredicateType.Result.PASS),
                                        AccessWeightTravelPredicateType.INSTANCE
                                )
                        ),
                        BasicRocketConeType.INSTANCE
                )
        );
    }

    public static void bootstrapBody(BootstapContext<RocketBody<?, ?>> context) {
        context.register(TIER_1_BODY,
                RocketBody.create(
                        new BasicRocketBodyConfig(
                                new ConfiguredTravelPredicate<>(
                                        new AccessWeightTravelPredicateConfig(1, TravelPredicateType.Result.PASS),
                                        AccessWeightTravelPredicateType.INSTANCE
                                )
                                , 1, 1
                        ),
                        BasicRocketBodyType.INSTANCE
                )
        );
    }

    public static void bootstrapFin(BootstapContext<RocketFin<?, ?>> context) {
        context.register(TIER_1_FIN,
                RocketFin.create(
                        new BasicRocketFinConfig(
                                new ConfiguredTravelPredicate<>(
                                        new AccessWeightTravelPredicateConfig(1, TravelPredicateType.Result.PASS),
                                        AccessWeightTravelPredicateType.INSTANCE
                                ),
                                false
                        ),
                        BasicRocketFinType.INSTANCE
                )
        );
    }

    public static void bootstrapBooster(BootstapContext<RocketBooster<?, ?>> context) {
        context.register(TIER_1_BOOSTER,
                RocketBooster.create(
                        new BasicRocketBoosterConfig(
                                new ConfiguredTravelPredicate<>(
                                        new AccessWeightTravelPredicateConfig(1, TravelPredicateType.Result.PASS),
                                        AccessWeightTravelPredicateType.INSTANCE
                                ),
                                0, 0, 0
                        ),
                        BasicRocketBoosterType.INSTANCE
                )
        );
    }

    public static void bootstrapBottom(BootstapContext<RocketBottom<?, ?>> context) {
        context.register(TIER_1_BOTTOM,
                RocketBottom.create(
                        new BasicRocketBottomConfig(
                                new ConfiguredTravelPredicate<>(
                                        new AccessWeightTravelPredicateConfig(1, TravelPredicateType.Result.PASS),
                                        AccessWeightTravelPredicateType.INSTANCE
                                ),
                                1296000
                        ),
                        BasicRocketBottomType.INSTANCE
                )
        );
    }

    public static void bootstrapUpgrade(BootstapContext<RocketUpgrade<?, ?>> context) {
//        context.register(STORAGE_UPGRADE,
//                RocketUpgrade.create(new StorageUpgradeConfig(Ingredient.EMPTY), null)
//        );
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
    private static @NotNull ResourceKey<RocketBottom<?, ?>> bottom(@NotNull String id) {
        return Constant.key(RocketRegistries.ROCKET_BOTTOM, id);
    }

    @Contract(pure = true)
    private static @NotNull ResourceKey<RocketUpgrade<?, ?>> upgrade(@NotNull String id) {
        return Constant.key(RocketRegistries.ROCKET_UPGRADE, id);
    }

//    public static final RocketPart DEFAULT_CONE = RocketPart.Builder.create()
//            .name(Component.translatable("rocket_part.galacticraft.default_cone"))
//            .travelPredicate(AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(1, TravelPredicateType.AccessType.PASS)))
//            .type(RocketPartType.CONE)
//            .research(Constant.id(Constant.MOD_ID))
//            .build();
//
//    public static final RocketPart DEFAULT_BODY = RocketPart.Builder.create()
//            .name(Component.translatable("rocket_part.galacticraft.default_body"))
//            .travelPredicate(AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(1, TravelPredicateType.AccessType.PASS)))
//            .type(RocketPartType.BODY)
//            .research(Constant.id(Constant.MOD_ID))
//            .build();
//
//    public static final RocketPart DEFAULT_FIN = RocketPart.Builder.create()
//            .name(Component.translatable("rocket_part.galacticraft.default_fin"))
//            .travelPredicate(AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(1, TravelPredicateType.AccessType.PASS)))
//            .type(RocketPartType.FIN)
//            .research(Constant.id(Constant.MOD_ID))
//            .build();
//
//    public static final RocketPart NO_BOOSTER = RocketPart.Builder.create()
//            .name(Component.translatable("rocket_part.galacticraft.default_booster"))
//            .travelPredicate(AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(1, TravelPredicateType.AccessType.PASS)))
//            .type(RocketPartType.BOOSTER)
//            .recipe(false)
//            .research(Constant.id(Constant.MOD_ID))
//            .build();
//
//    public static final RocketPart DEFAULT_BOTTOM = RocketPart.Builder.create()
//            .name(Component.translatable("rocket_part.galacticraft.default_bottom"))
//            .travelPredicate(AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(1, TravelPredicateType.AccessType.PASS)))
//            .type(RocketPartType.BOTTOM)
//            .research(Constant.id(Constant.MOD_ID))
//            .build();
//
//    public static final RocketPart ADVANCED_CONE = RocketPart.Builder.create()
//            .name(Component.translatable("rocket_part.galacticraft.advanced_cone"))
//            .travelPredicate(ConstantTravelPredicateType.INSTANCE.configure(new AccessTypeTravelPredicateConfig(TravelPredicateType.AccessType.PASS)))
//            .type(RocketPartType.CONE)
//            .research(Constant.id(Constant.MOD_ID))
//            .build();
//
//    public static final RocketPart SLOPED_CONE = RocketPart.Builder.create()
//            .name(Component.translatable("rocket_part.galacticraft.cone_sloped"))
//            .travelPredicate(ConstantTravelPredicateType.INSTANCE.configure(new AccessTypeTravelPredicateConfig(TravelPredicateType.AccessType.PASS)))
//            .type(RocketPartType.CONE)
//            .research(Constant.id(Constant.MOD_ID))
//            .build();
//
//    public static final RocketPart BOOSTER_TIER_1 = RocketPart.Builder.create()
//            .name(Component.translatable("rocket_part.galacticraft.booster_1"))
//            .travelPredicate(AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(2, TravelPredicateType.AccessType.PASS)))
//            .type(RocketPartType.BOOSTER)
//            .research(Constant.id(Constant.MOD_ID))
//            .build();
//
//    public static final RocketPart BOOSTER_TIER_2 = RocketPart.Builder.create()
//            .name(Component.translatable("rocket_part.galacticraft.booster_2"))
//            .travelPredicate(AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(3, TravelPredicateType.AccessType.PASS)))
//            .type(RocketPartType.BOOSTER)
//            .research(Constant.id(Constant.MOD_ID))
//            .build();
//
//    public static final RocketPart NO_UPGRADE = RocketPart.Builder.create()
//            .name(Component.translatable("rocket_part.galacticraft.default_upgrade"))
//            .travelPredicate(ConstantTravelPredicateType.INSTANCE.configure(new AccessTypeTravelPredicateConfig(TravelPredicateType.AccessType.PASS)))
//            .type(RocketPartType.UPGRADE)
//            .research(Constant.id(Constant.MOD_ID))
//            .build();
//
//    public static final RocketPart STORAGE_UPGRADE = RocketPart.Builder.create()
//            .name(Component.translatable("rocket_part.galacticraft.storage_upgrade"))
//            .travelPredicate(ConstantTravelPredicateType.INSTANCE.configure(new AccessTypeTravelPredicateConfig(TravelPredicateType.AccessType.PASS)))
//            .type(RocketPartType.UPGRADE)
//            .research(Constant.id(Constant.MOD_ID))
//            .build();
//
//    public static void register() {
//        Registry.register(AddonRegistry.ROCKET_PART, new ResourceLocation(Constant.MOD_ID, "default_cone"), DEFAULT_CONE);
//        Registry.register(AddonRegistry.ROCKET_PART, new ResourceLocation(Constant.MOD_ID, "default_body"), DEFAULT_BODY);
//        Registry.register(AddonRegistry.ROCKET_PART, new ResourceLocation(Constant.MOD_ID, "default_fin"), DEFAULT_FIN);
//        Registry.register(AddonRegistry.ROCKET_PART, new ResourceLocation(Constant.MOD_ID, "default_booster"), NO_BOOSTER);
//        Registry.register(AddonRegistry.ROCKET_PART, new ResourceLocation(Constant.MOD_ID, "default_bottom"), DEFAULT_BOTTOM);
//        Registry.register(AddonRegistry.ROCKET_PART, new ResourceLocation(Constant.MOD_ID, "advanced_cone"), ADVANCED_CONE);
//        Registry.register(AddonRegistry.ROCKET_PART, new ResourceLocation(Constant.MOD_ID, "sloped_cone"), SLOPED_CONE);
//        Registry.register(AddonRegistry.ROCKET_PART, new ResourceLocation(Constant.MOD_ID, "booster_1"), BOOSTER_TIER_1);
//        Registry.register(AddonRegistry.ROCKET_PART, new ResourceLocation(Constant.MOD_ID, "booster_2"), BOOSTER_TIER_2);
//        Registry.register(AddonRegistry.ROCKET_PART, new ResourceLocation(Constant.MOD_ID, "default_upgrade"), NO_UPGRADE);
//        Registry.register(AddonRegistry.ROCKET_PART, new ResourceLocation(Constant.MOD_ID, "storage_upgrade"), STORAGE_UPGRADE);
//    }
}
