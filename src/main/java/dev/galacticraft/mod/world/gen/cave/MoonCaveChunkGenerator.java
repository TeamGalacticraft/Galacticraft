package dev.galacticraft.mod.world.gen.cave;

import dev.galacticraft.mod.tag.GCBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Chunk-local Moon cave carver and decorator.
 *
 * <p>Planning is deterministic and biome-free. Biome styles are resolved only here
 * from a cached chunk-local style grid, which avoids chunk dependency chains while
 * still allowing smooth biome transitions at block level.</p>
 */
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
        List<MoonCavePlan> plans = MoonCavePlanner.INSTANCE.plansForChunk(randomState, chunkPos);

        if (plans.isEmpty()) {
            return false;
        }

        MoonCaveStyleResolver styleResolver = new MoonCaveStyleResolver(
                chunkPos,
                minY,
                maxY,
                biomeSource,
                randomState
        );

        List<CaveSample> samples = new ArrayList<>();
        boolean changed = false;

        /*
         * Pass 1:
         * Carve all air first across every plan/element.
         *
         * This prevents tunnels and rooms from being separated by shell membranes
         * created by earlier elements.
         */
        for (MoonCavePlan plan : plans) {
            if (!plan.bounds().intersectsChunk(chunkPos)) {
                continue;
            }

            for (MoonCaveElement element : plan.elements()) {
                if (!element.bounds().intersectsChunk(chunkPos)) {
                    continue;
                }

                changed |= carveAir(chunk, chunkPos, plan, element, minY, maxY, styleResolver, samples);
            }
        }

        /*
         * Pass 2:
         * Paint shell blocks only after all cave interiors are open.
         */
        for (MoonCavePlan plan : plans) {
            if (!plan.bounds().intersectsChunk(chunkPos)) {
                continue;
            }

            for (MoonCaveElement element : plan.elements()) {
                if (!element.bounds().intersectsChunk(chunkPos)) {
                    continue;
                }

                changed |= carveShell(chunk, chunkPos, plan, element, minY, maxY, styleResolver);
            }
        }

        decorate(chunk, chunkPos, samples, chunkPos.toLong());
        return changed;
    }

    private static boolean carveAir(
            ChunkAccess chunk,
            ChunkPos chunkPos,
            MoonCavePlan plan,
            MoonCaveElement element,
            int minY,
            int maxY,
            MoonCaveStyleResolver styleResolver,
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

                    MoonCaveStyle style = styleResolver.resolve(x, y, z, plan.primaryStyle());

                    chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
                    collectSamples(chunk, chunkPos, pos, style, samples);
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
            MoonCaveStyleResolver styleResolver
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

                    /*
                     * Never paint shell into already-carved cave air.
                     * This is what keeps joined rooms/tunnels open.
                     */
                    if (!canReplace(current)) {
                        continue;
                    }

                    MoonCaveStyle style = styleResolver.resolve(x, y, z, plan.primaryStyle());

                    if (zone == CaveZone.INNER_SHELL) {
                        chunk.setBlockState(pos, pickInnerWall(style, x, y, z), false);
                        changed = true;
                    } else if (zone == CaveZone.OUTER_SHELL) {
                        chunk.setBlockState(pos, style.outerWall(), false);
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
            MoonCaveStyle style,
            List<CaveSample> samples
    ) {
        BlockPos above = pos.above();
        BlockPos below = pos.below();

        if (insideChunk(chunkPos, above) && !chunk.getBlockState(above).isAir()) {
            samples.add(new CaveSample(pos.immutable(), CaveSampleType.CEILING, style));
        }

        if (insideChunk(chunkPos, below) && !chunk.getBlockState(below).isAir()) {
            samples.add(new CaveSample(pos.immutable(), CaveSampleType.FLOOR, style));
        }
    }

    private static void decorate(ChunkAccess chunk, ChunkPos chunkPos, List<CaveSample> samples, long seed) {
        for (CaveSample sample : samples) {
            int hash = hash(seed, sample.pos.getX(), sample.pos.getY(), sample.pos.getZ(), sample.type.ordinal());

            switch (sample.style) {
                case GLACIAL -> decorateGlacial(chunk, chunkPos, sample, hash);
                case OLIVINE -> decorateOlivine(chunk, chunkPos, sample, hash);
                case CHEESE -> decorateCheese(chunk, chunkPos, sample, hash);
            }
        }
    }

    private static void decorateGlacial(ChunkAccess chunk, ChunkPos chunkPos, CaveSample sample, int hash) {
        if (sample.type == CaveSampleType.CEILING && Math.floorMod(hash, 32) == 0) {
            placeSpike(chunk, chunkPos, sample.pos, -1, 2 + Math.floorMod(hash >> 3, 5), MoonCaveStyle.GLACIAL.spike());
        } else if (sample.type == CaveSampleType.FLOOR && Math.floorMod(hash, 52) == 0) {
            placeSpike(chunk, chunkPos, sample.pos, 1, 2 + Math.floorMod(hash >> 4, 4), MoonCaveStyle.GLACIAL.spike());
        }
    }

    private static void decorateOlivine(ChunkAccess chunk, ChunkPos chunkPos, CaveSample sample, int hash) {
        if (sample.type == CaveSampleType.FLOOR && Math.floorMod(hash, 45) == 0) {
            setIfAir(chunk, chunkPos, sample.pos, MoonCaveStyle.OLIVINE.accent());
        }
    }

    private static void decorateCheese(ChunkAccess chunk, ChunkPos chunkPos, CaveSample sample, int hash) {
        if (sample.type == CaveSampleType.FLOOR && Math.floorMod(hash, 30) == 0) {
            setIfAir(chunk, chunkPos, sample.pos, MoonCaveStyle.CHEESE.accent());
        }
    }

    private static void placeSpike(ChunkAccess chunk, ChunkPos chunkPos, BlockPos start, int yDir, int height, BlockState state) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int i = 0; i < height; i++) {
            int radius = i == 0 && height >= 5 ? 1 : 0;
            int y = start.getY() + i * yDir;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radius * radius) {
                        continue;
                    }

                    pos.set(start.getX() + dx, y, start.getZ() + dz);
                    setIfAir(chunk, chunkPos, pos, state);
                }
            }
        }
    }

    private static void setIfAir(ChunkAccess chunk, ChunkPos chunkPos, BlockPos pos, BlockState state) {
        if (insideChunk(chunkPos, pos) && chunk.getBlockState(pos).isAir()) {
            chunk.setBlockState(pos, state, false);
        }
    }

    private static BlockState pickInnerWall(MoonCaveStyle style, int x, int y, int z) {
        int hash = hash(0L, x, y, z, 3319);

        if (Math.floorMod(hash, 29) == 0) {
            return style.accent();
        }

        return style.innerWall();
    }

    private static boolean canReplace(BlockState state) {
        return !state.isAir() && state.is(GCBlockTags.MOON_CARVER_REPLACEABLES);
    }

    private static boolean canBecomeAir(BlockState state) {
        return state.isAir()
                || state.is(GCBlockTags.MOON_CARVER_REPLACEABLES)
                || isCaveShellBlock(state);
    }

    private static boolean isCaveShellBlock(BlockState state) {
        for (MoonCaveStyle style : MoonCaveStyle.values()) {
            if (state.is(style.innerWall().getBlock())
                    || state.is(style.outerWall().getBlock())
                    || state.is(style.accent().getBlock())) {
                return true;
            }
        }

        return false;
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

    private record CaveSample(BlockPos pos, CaveSampleType type, MoonCaveStyle style) {
    }

    private enum CaveSampleType {
        FLOOR,
        CEILING
    }
}