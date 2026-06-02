package dev.galacticraft.mod.world.gen.cave;

import dev.galacticraft.mod.tag.GCBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.ArrayList;
import java.util.List;

public final class MoonCaveChunkGenerator {
    private MoonCaveChunkGenerator() {
    }

    public static boolean generate(
            ChunkAccess chunk,
            RandomState randomState,
            int minY,
            int maxY,
            BiomeSource biomeSource
    ) {
        ChunkPos chunkPos = chunk.getPos();
        List<MoonCavePlan> plans = MoonCavePlanner.INSTANCE.plansForChunk(randomState, chunkPos, biomeSource);

        if (plans.isEmpty()) {
            return false;
        }

        CaveCarvingMask mask = new CaveCarvingMask(minY, maxY);

        for (MoonCavePlan plan : plans) {
            if (!plan.bounds().intersectsChunk(chunkPos)) {
                continue;
            }

            buildMask(chunkPos, plan, minY, maxY, mask);
        }

        return applyMask(chunk, chunkPos, randomState, minY, maxY, biomeSource, plans, mask);
    }

    private static void buildMask(
            ChunkPos chunkPos,
            MoonCavePlan plan,
            int minY,
            int maxY,
            CaveCarvingMask mask
    ) {
        for (MoonCaveElement element : plan.elements()) {
            if (!element.bounds().intersectsChunk(chunkPos)) {
                continue;
            }

            element.stamp(chunkPos, minY, maxY, mask, plan);
        }
    }

    private static boolean applyMask(
            ChunkAccess chunk,
            ChunkPos chunkPos,
            RandomState randomState,
            int minY,
            int maxY,
            BiomeSource biomeSource,
            List<MoonCavePlan> plans,
            CaveCarvingMask mask
    ) {
        boolean changed = false;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        List<CaveSample> samples = new ArrayList<>();

        PlanetCaveResolverCache resolverCache = new PlanetCaveResolverCache(
                chunkPos,
                minY,
                maxY,
                biomeSource,
                randomState
        );

        for (int localX = 0; localX < 16; localX++) {
            int x = chunkPos.getMinBlockX() + localX;

            for (int localZ = 0; localZ < 16; localZ++) {
                int z = chunkPos.getMinBlockZ() + localZ;

                for (int y = minY; y <= maxY; y++) {
                    CaveZone zone = mask.get(localX, y, localZ);

                    if (zone == CaveZone.NONE) {
                        continue;
                    }

                    MoonCavePlan owner = mask.owner(localX, y, localZ);

                    if (owner == null) {
                        continue;
                    }

                    PlanetCave cave = resolverCache.resolve(x, y, z, owner.cave());
                    pos.set(x, y, z);
                    BlockState current = chunk.getBlockState(pos);

                    if (zone == CaveZone.AIR) {
                        if (canBecomeAir(current)) {
                            chunk.setBlockState(pos, cave.air(x, y, z), false);
                            changed = true;

                            collectSampleIfNeeded(chunk, chunkPos, pos, cave, samples);
                        }

                        continue;
                    }

                    if (!canReplace(current)) {
                        continue;
                    }

                    if (zone == CaveZone.INNER_SHELL) {
                        chunk.setBlockState(pos, cave.innerWall(x, y, z), false);
                        changed = true;
                    } else if (zone == CaveZone.OUTER_SHELL) {
                        chunk.setBlockState(pos, cave.outerWall(x, y, z), false);
                        changed = true;
                    }
                }
            }
        }

        decorate(chunk, chunkPos, randomState, minY, maxY, samples);
        return changed;
    }

    private static void collectSampleIfNeeded(
            ChunkAccess chunk,
            ChunkPos chunkPos,
            BlockPos pos,
            PlanetCave cave,
            List<CaveSample> samples
    ) {
        BlockPos above = pos.above();
        BlockPos below = pos.below();

        if (insideChunk(chunkPos, above) && !chunk.getBlockState(above).isAir()) {
            samples.add(new CaveSample(pos.immutable(), CaveSampleType.CEILING, cave));
        }

        if (insideChunk(chunkPos, below) && !chunk.getBlockState(below).isAir()) {
            samples.add(new CaveSample(pos.immutable(), CaveSampleType.FLOOR, cave));
        }
    }

    private static void decorate(
            ChunkAccess chunk,
            ChunkPos chunkPos,
            RandomState randomState,
            int minY,
            int maxY,
            List<CaveSample> samples
    ) {
        if (CaveDebugConfig.DEBUG_DISABLE_DECORATIONS) {
            return;
        }

        for (CaveSample sample : samples) {
            int hash = hash(chunkPos.toLong(), sample.pos.getX(), sample.pos.getY(), sample.pos.getZ(), sample.type.ordinal());

            CaveFeatureContext context = new CaveFeatureContext(
                    chunk,
                    chunkPos,
                    sample.cave,
                    randomState,
                    minY,
                    maxY
            );

            sample.cave.decorate(context, sample.pos, sample.type, hash);
        }
    }

    private static boolean canReplace(BlockState state) {
        return !state.isAir() && state.is(GCBlockTags.MOON_CARVER_REPLACEABLES);
    }

    private static boolean canBecomeAir(BlockState state) {
        return state.isAir()
                || state.is(GCBlockTags.MOON_CARVER_REPLACEABLES)
                || MoonCaveRegistry.isKnownCaveBlock(state::is);
    }

    private static boolean insideChunk(ChunkPos chunkPos, BlockPos pos) {
        return pos.getX() >= chunkPos.getMinBlockX()
                && pos.getX() <= chunkPos.getMaxBlockX()
                && pos.getZ() >= chunkPos.getMinBlockZ()
                && pos.getZ() <= chunkPos.getMaxBlockZ();
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

    private record CaveSample(BlockPos pos, CaveSampleType type, PlanetCave cave) {
    }
}