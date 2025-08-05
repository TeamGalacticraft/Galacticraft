package dev.galacticraft.mod.world.gen.feature.features;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.world.dimension.MoonConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class OliGrubEggPlacedFeature extends Feature<NoneFeatureConfiguration> {
    public OliGrubEggPlacedFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        ChunkAccess chunk = level.getChunk(origin);

        boolean placedAny = false;

        for (int i = 0; i < 6; i++) {
            int dx = origin.getX() + random.nextInt(16);
            int dz = origin.getZ() + random.nextInt(16);
            int dy = MoonConstants.MIN_FEATURE_SPAWN + random.nextInt(MoonConstants.MAX_FEATURE_SPAWN - MoonConstants.MIN_FEATURE_SPAWN);

            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(dx, dy, dz);

            // Skip if already above surface Y limit
            if (dy > MoonConstants.MAX_FEATURE_SPAWN) continue;

            // Step down until we hit air
            while (pos.getY() > level.getMinBuildHeight() + 1 && !level.isEmptyBlock(pos)) {
                pos.move(0, -1, 0);
            }

            // Keep moving down until solid block is found (cave floor)
            while (pos.getY() > level.getMinBuildHeight() + 1 && level.isEmptyBlock(pos)) {
                pos.move(0, -1, 0);
            }

            BlockPos floorPos = pos.immutable();
            BlockPos eggPos = floorPos.above();

            BlockState floorState = level.getBlockState(floorPos);

            if (!floorState.isAir() && level.isEmptyBlock(eggPos) && floorPos.getY() < MoonConstants.MAX_FEATURE_SPAWN) {
                level.setBlock(eggPos, Blocks.SNIFFER_EGG.defaultBlockState(), 2);
                placedAny = true;
            }
        }

        return placedAny;
    }
}