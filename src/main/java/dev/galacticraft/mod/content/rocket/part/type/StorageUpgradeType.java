package dev.galacticraft.mod.content.rocket.part.type;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.api.rocket.part.type.RocketUpgradeType;
import dev.galacticraft.api.rocket.travelpredicate.ConfiguredTravelPredicate;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.impl.rocket.travelpredicate.config.ConstantTravelPredicateConfig;
import dev.galacticraft.impl.rocket.travelpredicate.type.ConstantTravelPredicateType;
import dev.galacticraft.mod.content.rocket.part.config.StorageUpgradeConfig;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class StorageUpgradeType extends RocketUpgradeType<StorageUpgradeConfig> {
    protected StorageUpgradeType(@NotNull Codec<StorageUpgradeConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public @NotNull Ingredient upgradeRecipe(@NotNull StorageUpgradeConfig config) {
        return config.recipe();
    }

    @Override
    public void tick(@NotNull Rocket rocket, @NotNull StorageUpgradeConfig config) {

    }

    @Override
    public @NotNull ConfiguredTravelPredicate<?, ?> travelPredicate(@NotNull StorageUpgradeConfig config) {
        return ConstantTravelPredicateType.INSTANCE.configure(new ConstantTravelPredicateConfig(TravelPredicateType.Result.PASS));
    }
}
