package dev.galacticraft.mod.world.gen.feature.custom;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.world.dimension.MoonConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GlacialIceSpikeFeature extends Feature<NoneFeatureConfiguration> {
    private static final BlockState SMALL_SPIKE = Blocks.WHITE_WOOL.defaultBlockState();
    private static final BlockState LARGE_SPIKE = Blocks.LIGHT_BLUE_WOOL.defaultBlockState();
    private static final BlockState CORE_SPIKE = Blocks.CYAN_WOOL.defaultBlockState();

    private final int attempts;

    public GlacialIceSpikeFeature(Codec<NoneFeatureConfiguration> codec, int attempts) {
        super(codec);
        this.attempts = attempts;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        boolean placedAny = false;

        for (int attempt = 0; attempt < attempts; attempt++) {
            int x = origin.getX() + random.nextInt(16);
            int z = origin.getZ() + random.nextInt(16);
            int y = MoonConstants.GlacialCaverns.MIN_FEATURE_SPAWN
                    + random.nextInt(MoonConstants.GlacialCaverns.MAX_FEATURE_SPAWN - MoonConstants.GlacialCaverns.MIN_FEATURE_SPAWN + 1);

            BlockPos air = findCaveAir(level, new BlockPos(x, y, z), random);
            if (air == null) {
                continue;
            }

            boolean ceilingSpike = random.nextBoolean();

            if (random.nextInt(18) == 0) {
                placedAny |= placeLargeSpike(level, air, ceilingSpike, random);
            } else {
                placedAny |= placeSmallSpike(level, air, ceilingSpike, random);
            }
        }

        return placedAny;
    }

    private static BlockPos findCaveAir(WorldGenLevel level, BlockPos start, RandomSource random) {
        for (int i = 0; i < 24; i++) {
            BlockPos pos = start.offset(random.nextInt(9) - 4, random.nextInt(15) - 7, random.nextInt(9) - 4);

            if (!level.isEmptyBlock(pos)) {
                continue;
            }

            if (level.getBlockState(pos.above()).isSolidRender(level, pos.above())
                    || level.getBlockState(pos.below()).isSolidRender(level, pos.below())) {
                return pos;
            }
        }

        return null;
    }

    private static boolean placeSmallSpike(WorldGenLevel level, BlockPos air, boolean ceilingSpike, RandomSource random) {
        Direction growth = ceilingSpike ? Direction.DOWN : Direction.UP;
        BlockPos anchor = ceilingSpike ? air.above() : air.below();

        if (!level.getBlockState(anchor).isSolidRender(level, anchor)) {
            return false;
        }

        int height = 1 + random.nextInt(4);
        boolean placedAny = false;

        for (int i = 0; i < height; i++) {
            BlockPos pos = air.relative(growth, i);

            if (!level.isEmptyBlock(pos)) {
                break;
            }

            level.setBlock(pos, SMALL_SPIKE, 2);
            placedAny = true;
        }

        return placedAny;
    }

    private static boolean placeLargeSpike(WorldGenLevel level, BlockPos air, boolean ceilingSpike, RandomSource random) {
        Direction growth = ceilingSpike ? Direction.DOWN : Direction.UP;
        BlockPos anchor = ceilingSpike ? air.above() : air.below();

        if (!level.getBlockState(anchor).isSolidRender(level, anchor)) {
            return false;
        }

        int height = 6 + random.nextInt(14);
        int baseRadius = 1 + random.nextInt(3);
        boolean placedAny = false;

        for (int y = 0; y < height; y++) {
            int radius = Math.max(0, baseRadius - (y * baseRadius / height));
            BlockPos layerCenter = air.relative(growth, y);

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radius * radius) {
                        continue;
                    }

                    BlockPos pos = layerCenter.offset(dx, 0, dz);

                    if (!level.isEmptyBlock(pos)) {
                        continue;
                    }

                    BlockState state = dx == 0 && dz == 0 && random.nextFloat() < 0.25F ? CORE_SPIKE : LARGE_SPIKE;
                    level.setBlock(pos, state, 2);
                    placedAny = true;
                }
            }
        }

        return placedAny;
    }
}