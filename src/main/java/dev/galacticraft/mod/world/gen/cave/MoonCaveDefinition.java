package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.resources.ResourceLocation;

public record MoonCaveDefinition(
        ResourceLocation id,
        MoonCaveStyle style,
        MoonCaveShape shape,
        int weight,
        float chance
) {
}