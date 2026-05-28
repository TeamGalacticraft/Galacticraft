package dev.galacticraft.mod.world.gen.carver;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.tag.GCBlockTags;
import dev.galacticraft.mod.world.gen.cave.MoonCavePlan;
import dev.galacticraft.mod.world.gen.cave.MoonCavePlanner;
import dev.galacticraft.mod.world.gen.cave.MoonCaveStyle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

import java.util.List;
import java.util.function.Function;

public class PlannedMoonCaveCarver extends WorldCarver<CaveCarverConfiguration> {
    public PlannedMoonCaveCarver(Codec<CaveCarverConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean carve(
            CarvingContext context,
            CaveCarverConfiguration config,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> posToBiome,
            RandomSource random,
            Aquifer aquifer,
            ChunkPos chunkPos,
            CarvingMask mask
    ) {
        List<MoonCavePlan> plans = MoonCavePlanner.INSTANCE.plansForChunk(context, chunkPos, posToBiome);

        if (plans.isEmpty()) {
            return false;
        }

        boolean changed = false;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int minY = Math.max(context.getMinGenY() + 1, minY(plans));
        int maxY = Math.min(context.getMinGenY() + context.getGenDepth() - 8, maxY(plans));

        for (int localX = 0; localX < 16; localX++) {
            int x = chunkPos.getBlockX(localX);

            for (int localZ = 0; localZ < 16; localZ++) {
                int z = chunkPos.getBlockZ(localZ);

                for (int y = minY; y <= maxY; y++) {
                    pos.set(x, y, z);

                    BlockState current = chunk.getBlockState(pos);
                    MoonCavePlan airPlan = airPlan(plans, x, y, z);

                    if (airPlan != null && canReplaceOrAlreadyCave(current)) {
                        mask.set(localX, y, localZ);
                        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
                        changed = true;
                        continue;
                    }

                    if (!canReplace(current)) {
                        continue;
                    }

                    MoonCavePlan inner = innerPlan(plans, x, y, z);
                    if (inner != null) {
                        chunk.setBlockState(pos, pickInner(inner, x, y, z), false);
                        changed = true;
                        continue;
                    }

                    MoonCavePlan outer = outerPlan(plans, x, y, z);
                    if (outer != null) {
                        chunk.setBlockState(pos, pickOuter(outer, x, y, z), false);
                        changed = true;
                    }
                }
            }
        }

        if (changed) {
            decorateChunk(context, chunk, chunkPos, plans, minY, maxY);
        }

        return changed;
    }

    @Override
    public boolean isStartChunk(CaveCarverConfiguration config, RandomSource random) {
        return true;
    }

    private static boolean canReplace(BlockState state) {
        return !state.isAir() && state.is(GCBlockTags.MOON_CARVER_REPLACEABLES);
    }

    private static boolean canReplaceOrAlreadyCave(BlockState state) {
        return state.isAir()
                || state.is(GCBlockTags.MOON_CARVER_REPLACEABLES)
                || state.is(Blocks.LIGHT_BLUE_WOOL)
                || state.is(Blocks.BLUE_WOOL)
                || state.is(Blocks.WHITE_WOOL)
                || state.is(Blocks.CYAN_WOOL)
                || state.is(Blocks.YELLOW_WOOL)
                || state.is(Blocks.ORANGE_WOOL)
                || state.is(Blocks.BROWN_WOOL);
    }

    private static MoonCavePlan airPlan(List<MoonCavePlan> plans, int x, int y, int z) {
        for (MoonCavePlan plan : plans) {
            if (plan.containsAir(x, y, z)) {
                return plan;
            }
        }
        return null;
    }

    private static MoonCavePlan innerPlan(List<MoonCavePlan> plans, int x, int y, int z) {
        for (MoonCavePlan plan : plans) {
            if (plan.innerShell(x, y, z)) {
                return plan;
            }
        }
        return null;
    }

    private static MoonCavePlan outerPlan(List<MoonCavePlan> plans, int x, int y, int z) {
        for (MoonCavePlan plan : plans) {
            if (plan.outerShell(x, y, z)) {
                return plan;
            }
        }
        return null;
    }

    private static BlockState pickInner(MoonCavePlan plan, int x, int y, int z) {
        MoonCaveStyle style = pickStyle(plan, x, y, z);
        int hash = hash(x, y, z, 3319);

        if (Math.floorMod(hash, 37) == 0) {
            return style.accent();
        }

        return style.innerWall();
    }

    private static BlockState pickOuter(MoonCavePlan plan, int x, int y, int z) {
        return pickStyle(plan, x, y, z).outerWall();
    }

    private static MoonCaveStyle pickStyle(MoonCavePlan plan, int x, int y, int z) {
        if (plan.styles().size() == 1) {
            return plan.primaryStyle();
        }

        MoonCaveStyle[] styles = plan.styles().toArray(MoonCaveStyle[]::new);
        return styles[Math.floorMod(hash(x, y, z, 9811), styles.length)];
    }

    private static void decorateChunk(CarvingContext context, ChunkAccess chunk, ChunkPos chunkPos, List<MoonCavePlan> plans, int minY, int maxY) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int localX = 2; localX <= 13; localX++) {
            int x = chunkPos.getBlockX(localX);

            for (int localZ = 2; localZ <= 13; localZ++) {
                int z = chunkPos.getBlockZ(localZ);

                for (int y = minY + 2; y <= maxY - 2; y++) {
                    pos.set(x, y, z);

                    if (!chunk.getBlockState(pos).isAir()) {
                        continue;
                    }

                    MoonCavePlan plan = airPlan(plans, x, y, z);
                    if (plan == null) {
                        continue;
                    }

                    BlockPos above = pos.above();
                    BlockPos below = pos.below();

                    if (!insideChunk(chunkPos, above) || !insideChunk(chunkPos, below)) {
                        continue;
                    }

                    if (!chunk.getBlockState(above).isAir()) {
                        decorateCeiling(context, chunk, chunkPos, plan, pos.immutable());
                    }

                    if (!chunk.getBlockState(below).isAir()) {
                        decorateFloor(context, chunk, chunkPos, plan, pos.immutable());
                    }
                }
            }
        }
    }

    private static void decorateCeiling(CarvingContext context, ChunkAccess chunk, ChunkPos chunkPos, MoonCavePlan plan, BlockPos start) {
        if (plan.styles().contains(MoonCaveStyle.GLACIAL) && Math.floorMod(hash(start, 1201), 34) == 0) {
            placeSpike(context, chunk, chunkPos, plan, start, -1, 2 + Math.floorMod(hash(start, 1301), 5), MoonCaveStyle.GLACIAL);
        }

        if (plan.styles().contains(MoonCaveStyle.OLIVINE) && Math.floorMod(hash(start, 1401), 48) == 0) {
            placeSpike(context, chunk, chunkPos, plan, start, -1, 1 + Math.floorMod(hash(start, 1501), 3), MoonCaveStyle.OLIVINE);
        }
    }

    private static void decorateFloor(CarvingContext context, ChunkAccess chunk, ChunkPos chunkPos, MoonCavePlan plan, BlockPos start) {
        if (plan.styles().contains(MoonCaveStyle.GLACIAL) && Math.floorMod(hash(start, 2201), 48) == 0) {
            placeSpike(context, chunk, chunkPos, plan, start, 1, 2 + Math.floorMod(hash(start, 2301), 4), MoonCaveStyle.GLACIAL);
        }

        if (plan.styles().contains(MoonCaveStyle.CHEESE) && Math.floorMod(hash(start, 2401), 30) == 0) {
            setIfAir(chunk, chunkPos, start, MoonCaveStyle.CHEESE.accent());
        }

        if (plan.styles().contains(MoonCaveStyle.OLIVINE) && Math.floorMod(hash(start, 2501), 42) == 0) {
            setIfAir(chunk, chunkPos, start, MoonCaveStyle.OLIVINE.accent());
        }
    }

    private static void placeSpike(CarvingContext context, ChunkAccess chunk, ChunkPos chunkPos, MoonCavePlan plan, BlockPos start, int yDir, int height, MoonCaveStyle style) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int minY = context.getMinGenY() + 1;
        int maxY = context.getMinGenY() + context.getGenDepth() - 8;

        for (int i = 0; i < height; i++) {
            int y = start.getY() + i * yDir;

            if (y < minY || y > maxY) {
                break;
            }

            int radius = i == 0 && height >= 5 ? 1 : 0;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radius * radius) {
                        continue;
                    }

                    pos.set(start.getX() + dx, y, start.getZ() + dz);

                    if (!insideChunk(chunkPos, pos)) {
                        continue;
                    }

                    if (!chunk.getBlockState(pos).isAir()) {
                        continue;
                    }

                    if (!plan.containsAir(pos.getX(), pos.getY(), pos.getZ())) {
                        continue;
                    }

                    chunk.setBlockState(pos, style.spike(), false);
                }
            }
        }
    }

    private static void setIfAir(ChunkAccess chunk, ChunkPos chunkPos, BlockPos pos, BlockState state) {
        if (insideChunk(chunkPos, pos) && chunk.getBlockState(pos).isAir()) {
            chunk.setBlockState(pos, state, false);
        }
    }

    private static boolean insideChunk(ChunkPos chunkPos, BlockPos pos) {
        return pos.getX() >= chunkPos.getMinBlockX()
                && pos.getX() <= chunkPos.getMaxBlockX()
                && pos.getZ() >= chunkPos.getMinBlockZ()
                && pos.getZ() <= chunkPos.getMaxBlockZ();
    }

    private static int minY(List<MoonCavePlan> plans) {
        int min = Integer.MAX_VALUE;
        for (MoonCavePlan plan : plans) {
            min = Math.min(min, plan.bounds().minY());
        }
        return min;
    }

    private static int maxY(List<MoonCavePlan> plans) {
        int max = Integer.MIN_VALUE;
        for (MoonCavePlan plan : plans) {
            max = Math.max(max, plan.bounds().maxY());
        }
        return max;
    }

    private static int hash(BlockPos pos, int salt) {
        return hash(pos.getX(), pos.getY(), pos.getZ(), salt);
    }

    private static int hash(int x, int y, int z, int salt) {
        int h = x * 73428767;
        h ^= y * 91227153;
        h ^= z * 42317861;
        h ^= salt * 1376312589;
        h ^= h >>> 13;
        h *= 1274126177;
        h ^= h >>> 16;
        return h;
    }
}