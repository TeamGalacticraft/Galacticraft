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

        List<CaveSample> samples = new ArrayList<>();
        boolean changed = false;

        for (MoonCavePlan plan : plans) {
            if (!plan.bounds().intersectsChunk(chunkPos)) {
                continue;
            }

            PlanetCaveResolver resolver = new PlanetCaveResolver(
                    chunkPos,
                    minY,
                    maxY,
                    biomeSource,
                    randomState,
                    plan.cave()
            );

            for (MoonCaveElement element : plan.elements()) {
                if (element.bounds().intersectsChunk(chunkPos)) {
                    changed |= carveAir(chunk, chunkPos, plan, element, minY, maxY, resolver, samples);
                }
            }

            for (MoonCaveElement element : plan.elements()) {
                if (element.bounds().intersectsChunk(chunkPos)) {
                    changed |= carveShell(chunk, chunkPos, plan, element, minY, maxY, resolver);
                }
            }
        }

        decorate(chunk, chunkPos, samples);
        return changed;
    }

    private static boolean carveAir(
            ChunkAccess chunk,
            ChunkPos chunkPos,
            MoonCavePlan plan,
            MoonCaveElement element,
            int minY,
            int maxY,
            PlanetCaveResolver resolver,
            List<CaveSample> samples
    ) {
        boolean changed = false;
        MoonCaveBounds bounds = element.bounds();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int minX = Math.max(chunkPos.getMinBlockX(), bounds.minX());
        int maxX = Math.min(chunkPos.getMaxBlockX(), bounds.maxX());
        int minZ = Math.max(chunkPos.getMinBlockZ(), bounds.minZ());
        int maxZ = Math.min(chunkPos.getMaxBlockZ(), bounds.maxZ());
        int lowY = Math.max(minY, bounds.minY());
        int highY = Math.min(maxY, bounds.maxY());

        if (minX > maxX || minZ > maxZ || lowY > highY) {
            return false;
        }

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = lowY; y <= highY; y++) {
                    if (element.zone(x, y, z) != CaveZone.AIR) {
                        continue;
                    }

                    pos.set(x, y, z);
                    BlockState current = chunk.getBlockState(pos);

                    if (!canBecomeAir(current)) {
                        continue;
                    }

                    PlanetCave cave = resolver.resolve(x, y, z, plan.cave());
                    chunk.setBlockState(pos, cave.air(x, y, z), false);
                    collectSamples(chunk, chunkPos, pos, cave, samples);
                    changed = true;
                }
            }
        }

        return changed;
    }

    private static boolean carveShell(
            ChunkAccess chunk,
            ChunkPos chunkPos,
            MoonCavePlan plan,
            MoonCaveElement element,
            int minY,
            int maxY,
            PlanetCaveResolver resolver
    ) {
        boolean changed = false;
        MoonCaveBounds bounds = element.bounds();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int minX = Math.max(chunkPos.getMinBlockX(), bounds.minX());
        int maxX = Math.min(chunkPos.getMaxBlockX(), bounds.maxX());
        int minZ = Math.max(chunkPos.getMinBlockZ(), bounds.minZ());
        int maxZ = Math.min(chunkPos.getMaxBlockZ(), bounds.maxZ());
        int lowY = Math.max(minY, bounds.minY());
        int highY = Math.min(maxY, bounds.maxY());

        if (minX > maxX || minZ > maxZ || lowY > highY) {
            return false;
        }

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = lowY; y <= highY; y++) {
                    CaveZone zone = element.zone(x, y, z);

                    if (zone == CaveZone.NONE || zone == CaveZone.AIR) {
                        continue;
                    }

                    pos.set(x, y, z);
                    BlockState current = chunk.getBlockState(pos);

                    if (!canReplace(current)) {
                        continue;
                    }

                    PlanetCave cave = resolver.resolve(x, y, z, plan.cave());

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

        return changed;
    }

    private static void collectSamples(
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

    private static void decorate(ChunkAccess chunk, ChunkPos chunkPos, List<CaveSample> samples) {
        for (CaveSample sample : samples) {
            int hash = hash(chunkPos.toLong(), sample.pos.getX(), sample.pos.getY(), sample.pos.getZ(), sample.type.ordinal());
            sample.cave.decorate(chunk, chunkPos, sample.pos, sample.type, hash);
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