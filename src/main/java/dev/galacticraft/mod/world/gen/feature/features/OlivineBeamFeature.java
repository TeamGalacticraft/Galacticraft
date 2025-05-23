package dev.galacticraft.mod.world.gen.feature.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class OlivineBeamFeature extends Feature<NoneFeatureConfiguration> {
    private static final BlockState OLIVINE_BLOCK = Blocks.BLUE_WOOL.defaultBlockState(); // placeholder
    private static final int MAX_BEAM_LENGTH = 30;

    public OlivineBeamFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos origin = context.origin();
        LevelAccessor level = context.level();
        RandomSource random = context.random();

        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        int length = 8 + random.nextInt(MAX_BEAM_LENGTH - 8);
        int thickness = 1 + random.nextInt(2);

        BlockPos.MutableBlockPos pos = origin.mutable();

        for (int i = 0; i < length; i++) {
            for (int dx = -thickness; dx <= thickness; dx++) {
                for (int dy = -thickness; dy <= thickness; dy++) {
                    BlockPos target = pos.offset(
                            direction.getStepX() * i + dx,
                            dy,
                            direction.getStepZ() * i + dx
                    );
                    if (level.isEmptyBlock(target) || level.getBlockState(target).isAir()) {
                        level.setBlock(target, OLIVINE_BLOCK, 2);
                    }
                }
            }
        }

        return true;
    }
}