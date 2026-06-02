package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;

public record CaveFeatureContext(
        ChunkAccess chunk,
        ChunkPos chunkPos,
        PlanetCave cave,
        RandomState randomState,
        int minY,
        int maxY
) {
}