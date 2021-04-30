package dev.galacticraft.mod.rocket.travel;

import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.api.rocket.part.travel.AccessType;
import dev.galacticraft.api.rocket.part.travel.AccessWeightPredicateType;
import dev.galacticraft.api.rocket.part.travel.ConfiguredTravelPredicate;
import dev.galacticraft.api.rocket.part.travel.config.AccessWeightTravelPredicateConfig;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GalacticraftConfiguredTravelPredicates {
    public static final ConfiguredTravelPredicate<AccessWeightTravelPredicateConfig> TIER_ONE = AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(1, AccessType.PASS));
    public static final ConfiguredTravelPredicate<AccessWeightTravelPredicateConfig> TIER_TWO = AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(2, AccessType.PASS));
    public static final ConfiguredTravelPredicate<AccessWeightTravelPredicateConfig> TIER_THREE = AccessWeightPredicateType.INSTANCE.configure(new AccessWeightTravelPredicateConfig(3, AccessType.PASS));

    public static void register() {
        Registry.register(AddonRegistry.CONFIGURED_TRAVEL_PREDICATE, new Identifier(Constant.MOD_ID, "tier_one"), TIER_ONE);
        Registry.register(AddonRegistry.CONFIGURED_TRAVEL_PREDICATE, new Identifier(Constant.MOD_ID, "tier_two"), TIER_TWO);
        Registry.register(AddonRegistry.CONFIGURED_TRAVEL_PREDICATE, new Identifier(Constant.MOD_ID, "tier_three"), TIER_THREE);
    }
}
