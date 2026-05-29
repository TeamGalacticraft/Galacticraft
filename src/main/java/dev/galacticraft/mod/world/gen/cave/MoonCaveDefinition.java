package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.resources.ResourceLocation;

public record MoonCaveDefinition(
        ResourceLocation id,
        MoonCaveStyle style,
        MoonCaveShapeType shapeType,
        MoonCaveShape shape,
        int weight,
        float spawnChance,
        int minAnchorY,
        int maxAnchorY,
        int minY,
        int maxY
) {
}