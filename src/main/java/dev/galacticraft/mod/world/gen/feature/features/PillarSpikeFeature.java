package dev.galacticraft.mod.world.gen.feature.features;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.world.dimension.MoonConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.Vec3;

public class PillarSpikeFeature extends Feature<NoneFeatureConfiguration> {
    private final int attempts;

    public PillarSpikeFeature(Codec<NoneFeatureConfiguration> codec, int attempts) {
        super(codec);
        this.attempts = attempts;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        boolean placed = false;

        for (int i = 0; i < attempts; i++) {
            BlockPos start = findAirInCave(level, origin.offset(random.nextInt(16), 0, random.nextInt(16)), random);
            BlockPos end = findAirInCave(level, origin.offset(random.nextInt(16), 0, random.nextInt(16)), random);

            if (start == null || end == null || start.equals(end)) continue;

            // 🔍 Check minimum distance (at least 5 blocks apart)
            if (start.distSqr(end) < 25) continue;

            if (!isAirLine(level, start, end)) continue;

            placed |= queueOrPlace(level, start, Blocks.GOLD_BLOCK.defaultBlockState());
            placed |= queueOrPlace(level, end, Blocks.GOLD_BLOCK.defaultBlockState());

            placed |= drawLine(level, start, end, Blocks.GLASS.defaultBlockState());
        }

        return placed;
    }

    private BlockPos findAirInCave(WorldGenLevel level, BlockPos center, RandomSource random) {
        for (int tries = 0; tries < 30; tries++) {
            int y = MoonConstants.OLIVINE_CAVE_MIN_HEIGHT + random.nextInt(MoonConstants.OLIVINE_CAVE_MAX_HEIGHT - MoonConstants.OLIVINE_CAVE_MIN_HEIGHT);
            BlockPos pos = new BlockPos(center.getX(), y, center.getZ());
            if (level.getBlockState(pos).isAir()) {
                for (Direction direction : Direction.values()) {
                    if (!level.getBlockState(pos.relative(direction)).getBlock().defaultBlockState().isAir()) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    private boolean drawLine(WorldGenLevel level, BlockPos start, BlockPos end, BlockState state) {
        boolean placed = false;

        Vec3 from = Vec3.atCenterOf(start);
        Vec3 to = Vec3.atCenterOf(end);
        Vec3 dir = to.subtract(from);
        int steps = (int) (dir.length() * 2.5);
        Vec3 step = dir.normalize().scale(1.0 / 2.5); // 0.4 blocks per step

        Vec3 pos = from;
        for (int i = 0; i <= steps; i++) {
            BlockPos blockPos = BlockPos.containing(pos);
            placed |= queueOrPlace(level, blockPos, state);
            pos = pos.add(step);
        }

        return placed;
    }

    private boolean isAirLine(WorldGenLevel level, BlockPos start, BlockPos end) {
        Vec3 from = Vec3.atCenterOf(start);
        Vec3 to = Vec3.atCenterOf(end);
        Vec3 dir = to.subtract(from);
        int steps = (int) (dir.length() * 3); // finer steps for accuracy
        Vec3 step = dir.normalize().scale(1.0 / 3.0); // 0.33 blocks per step

        Vec3 pos = from;
        for (int i = 0; i <= steps; i++) {
            BlockPos blockPos = BlockPos.containing(pos);
            if (!blockPos.equals(start) && !blockPos.equals(end)) {
                if (!level.getBlockState(blockPos).isAir()) return false;
            }
            pos = pos.add(step);
        }

        return true;
    }

    private boolean queueOrPlace(WorldGenLevel level, BlockPos pos, BlockState state) {
        ChunkPos chunkPos = new ChunkPos(pos);
        try {
            if (level.getBlockState(pos).isAir() || level.getBlockState(pos).is(Blocks.GLASS)) {
                level.setBlock(pos, state, 2);
                return true;
            }
        } catch (IllegalStateException e) {
            DeferredBlockPlacement.queue(pos.immutable(), state);
        }
        return false;
    }
}