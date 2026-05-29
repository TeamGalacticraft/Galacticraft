package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.RandomState;

/**
 * Fast cave style resolver that samples biome noise directly.
 *
 * <p>This does not use {@code BiomeManager#getBiome}. That method can stall during
 * world generation because it may depend on generated chunk biome data. This resolver
 * uses {@link BiomeSource#getNoiseBiome(int, int, int, net.minecraft.world.level.biome.Climate.Sampler)}
 * instead, which is pure noise sampling and does not load chunks.</p>
 */
public final class MoonCaveStyleResolver {
    private static final int SAMPLE_SPACING = 8;
    private static final int SAMPLE_PADDING = 24;
    private static final int TRANSITION_DISTANCE_SQR = 20 * 20;

    private final int originX;
    private final int originY;
    private final int originZ;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private final MoonCaveStyle[] styles;

    public MoonCaveStyleResolver(
            ChunkPos chunkPos,
            int minY,
            int maxY,
            BiomeSource biomeSource,
            RandomState randomState
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
        this.styles = new MoonCaveStyle[this.sizeX * this.sizeY * this.sizeZ];

        this.fill(biomeSource, randomState);
    }

    public MoonCaveStyle resolve(int x, int y, int z, MoonCaveStyle fallback, MoonCaveShapeType shapeType) {
        MoonCaveStyle center = this.sampleNearest(x, y, z, fallback);

        if (!MoonCaveRegistry.hasStyleForShapeType(shapeType, center)) {
            return fallback;
        }

        NearbyStyle nearby = this.findNearestDifferentStyle(x, y, z, center, shapeType);

        if (nearby == null || nearby.distanceSqr >= TRANSITION_DISTANCE_SQR) {
            return center;
        }

        int hash = hash(83492791L, x, y, z, nearby.style.ordinal());
        double noise = ((hash & 1023) / 1023.0D) - 0.5D;
        double blend = 1.0D - Math.sqrt(nearby.distanceSqr / (double) TRANSITION_DISTANCE_SQR);

        return noise < blend - 0.5D ? nearby.style : center;
    }

    private void fill(BiomeSource biomeSource, RandomState randomState) {
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

                    this.styles[this.index(sx, sy, sz)] = MoonCaveStyle.fromBiome(biome);
                }
            }
        }
    }

    private MoonCaveStyle sampleNearest(int x, int y, int z, MoonCaveStyle fallback) {
        int sx = this.clampSampleX(Math.floorDiv(x - this.originX + SAMPLE_SPACING / 2, SAMPLE_SPACING));
        int sy = this.clampSampleY(Math.floorDiv(y - this.originY + SAMPLE_SPACING / 2, SAMPLE_SPACING));
        int sz = this.clampSampleZ(Math.floorDiv(z - this.originZ + SAMPLE_SPACING / 2, SAMPLE_SPACING));

        MoonCaveStyle style = this.styles[this.index(sx, sy, sz)];
        return style == null ? fallback : style;
    }

    private NearbyStyle findNearestDifferentStyle(int x, int y, int z, MoonCaveStyle center, MoonCaveShapeType shapeType) {
        int baseX = this.clampSampleX(Math.floorDiv(x - this.originX + SAMPLE_SPACING / 2, SAMPLE_SPACING));
        int baseY = this.clampSampleY(Math.floorDiv(y - this.originY + SAMPLE_SPACING / 2, SAMPLE_SPACING));
        int baseZ = this.clampSampleZ(Math.floorDiv(z - this.originZ + SAMPLE_SPACING / 2, SAMPLE_SPACING));

        NearbyStyle nearest = null;

        for (int dx = -2; dx <= 2; dx++) {
            int sx = baseX + dx;

            if (sx < 0 || sx >= this.sizeX) {
                continue;
            }

            int sampleX = this.originX + sx * SAMPLE_SPACING;

            for (int dy = -2; dy <= 2; dy++) {
                int sy = baseY + dy;

                if (sy < 0 || sy >= this.sizeY) {
                    continue;
                }

                int sampleY = this.originY + sy * SAMPLE_SPACING;

                for (int dz = -2; dz <= 2; dz++) {
                    int sz = baseZ + dz;

                    if (sz < 0 || sz >= this.sizeZ) {
                        continue;
                    }

                    MoonCaveStyle style = this.styles[this.index(sx, sy, sz)];

                    if (style == null || style == center) {
                        continue;
                    }

                    if (!MoonCaveRegistry.hasStyleForShapeType(shapeType, style)) {
                        continue;
                    }

                    int sampleZ = this.originZ + sz * SAMPLE_SPACING;
                    int distanceX = x - sampleX;
                    int distanceY = y - sampleY;
                    int distanceZ = z - sampleZ;
                    int distanceSqr = distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;

                    if (nearest == null || distanceSqr < nearest.distanceSqr) {
                        nearest = new NearbyStyle(style, distanceSqr);
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

    private record NearbyStyle(MoonCaveStyle style, int distanceSqr) {
    }
}