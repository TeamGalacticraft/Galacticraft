package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.HashMap;
import java.util.Map;

public final class PlanetCaveResolverCache {
    private final ChunkPos chunkPos;
    private final int minY;
    private final int maxY;
    private final BiomeSource biomeSource;
    private final RandomState randomState;
    private final Map<ResourceLocation, PlanetCaveResolver> resolvers = new HashMap<>();

    public PlanetCaveResolverCache(
            ChunkPos chunkPos,
            int minY,
            int maxY,
            BiomeSource biomeSource,
            RandomState randomState
    ) {
        this.chunkPos = chunkPos;
        this.minY = minY;
        this.maxY = maxY;
        this.biomeSource = biomeSource;
        this.randomState = randomState;
    }

    public PlanetCave resolve(int x, int y, int z, PlanetCave fallback) {
        PlanetCaveResolver resolver = this.resolvers.computeIfAbsent(
                fallback.id(),
                ignored -> new PlanetCaveResolver(
                        this.chunkPos,
                        this.minY,
                        this.maxY,
                        this.biomeSource,
                        this.randomState,
                        fallback
                )
        );

        return resolver.resolve(x, y, z, fallback);
    }
}