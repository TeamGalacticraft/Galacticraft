package dev.galacticraft.mod.world.gen.cave;

import dev.galacticraft.mod.tag.GCBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;

/**
 * Applies planned Moon caves directly to one already-filled terrain chunk.
 */
public final class MoonCaveChunkGenerator {
    private MoonCaveChunkGenerator() {
    }

    public static boolean generate(
            ChunkAccess chunk,
            RandomState randomState,
            int minY,
            int maxY,
            Function<BlockPos, Holder<Biome>> biomeLookup
    ) {
        ChunkPos chunkPos = chunk.getPos();
        List<MoonCavePlan> plans = MoonCavePlanner.INSTANCE.plansForChunk(randomState, chunkPos, biomeLookup);

        if (plans.isEmpty()) {
            return false;
        }

        List<CaveSample> samples = new ArrayList<>();
        boolean changed = carve(chunk, chunkPos, plans, minY, maxY, samples);
        decorate(chunk, chunkPos, samples, chunkPos.toLong());

        return changed;
    }

    private static boolean carve(
            ChunkAccess chunk,
            ChunkPos chunkPos,
            List<MoonCavePlan> plans,
            int minY,
            int maxY,
            List<CaveSample> samples
    ) {
        boolean changed = false;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (MoonCavePlan plan : plans) {
            int minX = Math.max(chunkPos.getMinBlockX(), plan.bounds().minX());
            int maxX = Math.min(chunkPos.getMaxBlockX(), plan.bounds().maxX());
            int minZ = Math.max(chunkPos.getMinBlockZ(), plan.bounds().minZ());
            int maxZ = Math.min(chunkPos.getMaxBlockZ(), plan.bounds().maxZ());
            int lowY = Math.max(minY, plan.bounds().minY());
            int highY = Math.min(maxY, plan.bounds().maxY());

            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    for (int y = lowY; y <= highY; y++) {
                        pos.set(x, y, z);
                        BlockState current = chunk.getBlockState(pos);

                        CaveZone zone = plan.zone(x, y, z);
                        if (zone == CaveZone.NONE) {
                            continue;
                        }

                        CaveClassification classification = new CaveClassification(zone, EnumSet.copyOf(plan.styles()));

                        if (classification.zone == CaveZone.AIR) {
                            if (canBecomeAir(current)) {
                                chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
                                changed = true;
                            }
                            collectSamples(chunk, chunkPos, pos, classification.styles, samples);
                        } else if (classification.zone == CaveZone.INNER_SHELL && canReplace(current)) {
                            chunk.setBlockState(pos, pickInnerWall(classification.styles, x, y, z), false);
                            changed = true;
                        } else if (classification.zone == CaveZone.OUTER_SHELL && canReplace(current)) {
                            chunk.setBlockState(pos, pickOuterWall(classification.styles, x, y, z), false);
                            changed = true;
                        }
                    }
                }
            }
        }

        return changed;
    }

    private static CaveClassification classify(List<MoonCavePlan> plans, int x, int y, int z) {
        CaveZone zone = CaveZone.NONE;
        EnumSet<MoonCaveStyle> styles = EnumSet.noneOf(MoonCaveStyle.class);

        for (MoonCavePlan plan : plans) {
            CaveZone planZone = plan.zone(x, y, z);

            if (planZone != CaveZone.NONE) {
                styles.addAll(plan.styles());
            }

            if (planZone.ordinal() > zone.ordinal()) {
                zone = planZone;
            }
        }

        return new CaveClassification(zone, styles);
    }

    private static void collectSamples(
            ChunkAccess chunk,
            ChunkPos chunkPos,
            BlockPos pos,
            EnumSet<MoonCaveStyle> styles,
            List<CaveSample> samples
    ) {
        BlockPos above = pos.above();
        BlockPos below = pos.below();

        if (insideChunk(chunkPos, above) && !chunk.getBlockState(above).isAir()) {
            samples.add(new CaveSample(pos.immutable(), CaveSampleType.CEILING, EnumSet.copyOf(styles)));
        }

        if (insideChunk(chunkPos, below) && !chunk.getBlockState(below).isAir()) {
            samples.add(new CaveSample(pos.immutable(), CaveSampleType.FLOOR, EnumSet.copyOf(styles)));
        }
    }

    private static void decorate(ChunkAccess chunk, ChunkPos chunkPos, List<CaveSample> samples, long seed) {
        for (CaveSample sample : samples) {
            int hash = hash(seed, sample.pos.getX(), sample.pos.getY(), sample.pos.getZ(), sample.type.ordinal());

            if (sample.styles.contains(MoonCaveStyle.GLACIAL)) {
                decorateGlacial(chunk, chunkPos, sample, hash);
            }

            if (sample.styles.contains(MoonCaveStyle.OLIVINE)) {
                decorateOlivine(chunk, chunkPos, sample, hash);
            }

            if (sample.styles.contains(MoonCaveStyle.CHEESE)) {
                decorateCheese(chunk, chunkPos, sample, hash);
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

    private static BlockState pickInnerWall(EnumSet<MoonCaveStyle> styles, int x, int y, int z) {
        MoonCaveStyle style = pickStyle(styles, x, y, z, 9911);
        int hash = hash(0, x, y, z, 3319);

        if (Math.floorMod(hash, 29) == 0) {
            return style.accent();
        }

        return style.innerWall();
    }

    private static BlockState pickOuterWall(EnumSet<MoonCaveStyle> styles, int x, int y, int z) {
        return pickStyle(styles, x, y, z, 6619).outerWall();
    }

    private static MoonCaveStyle pickStyle(EnumSet<MoonCaveStyle> styles, int x, int y, int z, int salt) {
        if (styles.isEmpty()) {
            return MoonCaveStyle.CHEESE;
        }

        MoonCaveStyle[] array = styles.toArray(MoonCaveStyle[]::new);
        return array[Math.floorMod(hash(0, x, y, z, salt), array.length)];
    }

    private static boolean canReplace(BlockState state) {
        return !state.isAir() && state.is(GCBlockTags.MOON_CARVER_REPLACEABLES);
    }

    private static boolean canBecomeAir(BlockState state) {
        return state.isAir() || state.is(GCBlockTags.MOON_CARVER_REPLACEABLES);
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

    private record CaveClassification(CaveZone zone, EnumSet<MoonCaveStyle> styles) {
    }

    private record CaveSample(BlockPos pos, CaveSampleType type, EnumSet<MoonCaveStyle> styles) {
    }

    private enum CaveSampleType {
        FLOOR,
        CEILING
    }
}