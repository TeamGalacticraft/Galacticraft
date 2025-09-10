/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.world.gen.feature.features;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.dimension.MoonConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class OliFlyEggPlacedFeature extends Feature<NoneFeatureConfiguration> {
    public OliFlyEggPlacedFeature(Codec<NoneFeatureConfiguration> codec) {
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
            int dy = MoonConstants.OlivineCaves.MIN_FEATURE_SPAWN + random.nextInt(MoonConstants.OlivineCaves.MAX_FEATURE_SPAWN - MoonConstants.OlivineCaves.MIN_FEATURE_SPAWN);

            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(dx, dy, dz);

            // Skip if already above surface Y limit
            if (dy > MoonConstants.OlivineCaves.MAX_FEATURE_SPAWN) continue;

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

            if (!floorState.isAir() && level.isEmptyBlock(eggPos) && floorPos.getY() < MoonConstants.OlivineCaves.MAX_FEATURE_SPAWN) {
                level.setBlock(eggPos, GCBlocks.OLI_FLY_EGG.defaultBlockState(), 2);
                placedAny = true;
            }
        }

        return placedAny;
    }
}