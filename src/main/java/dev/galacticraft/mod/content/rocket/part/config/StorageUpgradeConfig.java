package dev.galacticraft.mod.content.rocket.part.config;

import dev.galacticraft.api.rocket.part.config.RocketUpgradeConfig;
import net.minecraft.world.item.crafting.Ingredient;

public record StorageUpgradeConfig(Ingredient recipe) implements RocketUpgradeConfig {
}
