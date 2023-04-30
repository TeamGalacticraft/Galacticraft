package dev.galacticraft.mod.content.item;

import dev.galacticraft.api.item.Schematic;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.recipe.RocketPartRecipe;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RocketPartSchematic extends Item implements Schematic {
    private final ResourceKey<RocketPartRecipe<?, ?>> recipe;

    public RocketPartSchematic(Properties properties, ResourceLocation recipe) {
        super(properties);
        this.recipe = ResourceKey.create(RocketRegistries.ROCKET_PART_RECIPE, recipe);
    }

    @Override
    public @Nullable RocketPartRecipe<?, ?> getRecipe(Registry<RocketPartRecipe<?, ?>> registry, @NotNull ItemStack stack) {
        return registry.get(this.recipe);
    }
}
