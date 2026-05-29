package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public record MoonCaveContext(
        MoonCaveCellPos cell,
        BlockPos anchor,
        PlanetCave cave,
        RandomSource random,
        int minY,
        int maxY
) {
    public int clampY(int y) {
        return Math.max(this.minY, Math.min(this.maxY, y));
    }
}