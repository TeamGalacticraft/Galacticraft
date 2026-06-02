package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.RandomState;

/**
 * Chunk-local cave resolver.
 *
 * <p>This samples biome noise directly using {@link BiomeSource#getNoiseBiome}
 * and never uses {@code BiomeManager#getBiome}, avoiding chunk-generation stalls.</p>
 */
public final class PlanetCaveResolver {
    private static final int SAMPLE_SPACING = 8;
    private static final int SAMPLE_PADDING = 24;
    private static final int TRANSITION_DISTANCE_SQR = 20 * 20;

    private final int originX;
    private final int originY;
    private final int originZ;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private final PlanetCave[] caves;

    public PlanetCaveResolver(
            ChunkPos chunkPos,
            int minY,
            int maxY,
            BiomeSource biomeSource,
            RandomState randomState,
            PlanetCave fallbackCave
    ) {
        this.originX = chunkPos.getMinBlockX() - SAMPLE_PADDING;
        this.originY = minY - SAMPLE_PADDING;
        this.originZ = chunkPos.getMinBlockZ() - SAMPLE_PADDING;

        int endX = chunkPos.getMaxBlockX() + SAMPLE_PADDING;
        int endY = maxY + SAMPLE_PADDING;
        int endZ = chunkPos.getMaxBlockZ() + SAMPLE_PADDING;

        this.sizeX = ((endX - this.originX) / SAMPLE_SPACING) + 1;
        this.sizeY = ((endY - this.originY) / SAMPLE_SPACING) + 1;
        this.sizeZ = ((endZ - this.originZ) / SAMPLE_SPACING) + 1;
        this.caves = new PlanetCave[this.sizeX * this.sizeY * this.sizeZ];

        this.fill(biomeSource, randomState, fallbackCave);
    }

    public PlanetCave resolve(int x, int y, int z, PlanetCave fallback) {
        if (CaveDebugConfig.DEBUG_DISABLE_TRANSITIONS) {
            return fallback;
        }

        PlanetCave center = this.sampleNearest(x, y, z, fallback);

        if (center == fallback) {
            return fallback;
        }

        if (!fallback.canTransitionTo(center)) {
            return fallback;
        }

        CaveTransitionStrength fallbackStrength = fallback.transitionConfig().strength();
        CaveTransitionStrength centerStrength = center.transitionConfig().strength();

        int radius = Math.max(
                fallbackStrength.transitionRadius(),
                centerStrength.transitionRadius()
        );

        NearbyCave nearby = this.findNearestDifferentCave(x, y, z, fallback, center, radius);

        if (nearby == null) {
            return center;
        }

        double distance = Math.sqrt(nearby.distanceSqr);
        double blend = 1.0D - Math.min(1.0D, distance / radius);

        double centerPower = centerStrength.dominance();
        double nearbyPower = nearby.cave.transitionConfig().strength().dominance();

        double threshold = nearbyPower / (centerPower + nearbyPower);
        double noise = ((hash(83492791L, x, y, z, nearby.cave.id().hashCode()) & 1023) / 1023.0D);

        double transitionPressure = blend * threshold;

        return noise < transitionPressure ? nearby.cave : center;
    }

    private void fill(BiomeSource biomeSource, RandomState randomState, PlanetCave fallbackCave) {
        for (int sx = 0; sx < this.sizeX; sx++) {
            int x = this.originX + sx * SAMPLE_SPACING;

            for (int sy = 0; sy < this.sizeY; sy++) {
                int y = this.originY + sy * SAMPLE_SPACING;

                for (int sz = 0; sz < this.sizeZ; sz++) {
                    int z = this.originZ + sz * SAMPLE_SPACING;

                    Holder<Biome> biome = biomeSource.getNoiseBiome(
                            QuartPos.fromBlock(x),
                            QuartPos.fromBlock(y),
                            QuartPos.fromBlock(z),
                            randomState.sampler()
                    );

                    PlanetCave transition = MoonCaveRegistry.findTransitionCave(biome, fallbackCave.shapeType());
                    this.caves[this.index(sx, sy, sz)] = transition;
                }
            }
        }
    }

    private PlanetCave sampleNearest(int x, int y, int z, PlanetCave fallback) {
        int sx = this.clampSampleX(Math.floorDiv(x - this.originX + SAMPLE_SPACING / 2, SAMPLE_SPACING));
        int sy = this.clampSampleY(Math.floorDiv(y - this.originY + SAMPLE_SPACING / 2, SAMPLE_SPACING));
        int sz = this.clampSampleZ(Math.floorDiv(z - this.originZ + SAMPLE_SPACING / 2, SAMPLE_SPACING));

        PlanetCave cave = this.caves[this.index(sx, sy, sz)];
        return cave == null ? fallback : cave;
    }

    private NearbyCave findNearestDifferentCave(
            int x,
            int y,
            int z,
            PlanetCave fallback,
            PlanetCave center,
            int radius
    ) {
        int baseX = this.clampSampleX(Math.floorDiv(x - this.originX + SAMPLE_SPACING / 2, SAMPLE_SPACING));
        int baseY = this.clampSampleY(Math.floorDiv(y - this.originY + SAMPLE_SPACING / 2, SAMPLE_SPACING));
        int baseZ = this.clampSampleZ(Math.floorDiv(z - this.originZ + SAMPLE_SPACING / 2, SAMPLE_SPACING));

        int sampleRadius = Math.max(1, radius / SAMPLE_SPACING + 1);
        NearbyCave nearest = null;

        for (int dx = -sampleRadius; dx <= sampleRadius; dx++) {
            int sx = baseX + dx;

            if (sx < 0 || sx >= this.sizeX) {
                continue;
            }

            int sampleX = this.originX + sx * SAMPLE_SPACING;

            for (int dy = -sampleRadius; dy <= sampleRadius; dy++) {
                int sy = baseY + dy;

                if (sy < 0 || sy >= this.sizeY) {
                    continue;
                }

                int sampleY = this.originY + sy * SAMPLE_SPACING;

                for (int dz = -sampleRadius; dz <= sampleRadius; dz++) {
                    int sz = baseZ + dz;

                    if (sz < 0 || sz >= this.sizeZ) {
                        continue;
                    }

                    PlanetCave cave = this.caves[this.index(sx, sy, sz)];

                    if (cave == null || cave == center || !fallback.canTransitionTo(cave)) {
                        continue;
                    }

                    int sampleZ = this.originZ + sz * SAMPLE_SPACING;
                    int distanceX = x - sampleX;
                    int distanceY = y - sampleY;
                    int distanceZ = z - sampleZ;
                    int distanceSqr = distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;

                    if (distanceSqr > radius * radius) {
                        continue;
                    }

                    if (nearest == null || distanceSqr < nearest.distanceSqr) {
                        nearest = new NearbyCave(cave, distanceSqr);
                    }
                }
            }
        }

        return nearest;
    }

    private int index(int sx, int sy, int sz) {
        return (sy * this.sizeZ + sz) * this.sizeX + sx;
    }

    private int clampSampleX(int sample) {
        return Math.max(0, Math.min(this.sizeX - 1, sample));
    }

    private int clampSampleY(int sample) {
        return Math.max(0, Math.min(this.sizeY - 1, sample));
    }

    private int clampSampleZ(int sample) {
        return Math.max(0, Math.min(this.sizeZ - 1, sample));
    }

    private static int hash(long seed, int x, int y, int z, int salt) {
        long h = seed;
        h ^= (long) x * 73428767L;
        h ^= (long) y * 91227153L;
        h ^= (long) z * 42317861L;
        h ^= (long) salt * 1376312589L;
        h ^= h >>> 33;
        h *= 0xff51afd7ed558ccdL;
        h ^= h >>> 33;
        return (int) h;
    }

    private record NearbyCave(PlanetCave cave, int distanceSqr) {
    }
}