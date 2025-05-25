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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class BasaltBeamFeature extends Feature<NoneFeatureConfiguration> {
    private static final BlockState BASALT_BLOCK = Blocks.BLACK_WOOL.defaultBlockState(); // placeholder for basalt
    private static final BlockState ORE_BLOCK = Blocks.YELLOW_WOOL.defaultBlockState();   // placeholder for rich olivine basalt

    private static final int MAX_BEAM_LENGTH = 20;
    private static final int MIN_BEAM_LENGTH = 6;
    private static final int MAX_RADIUS = 2;

    public BasaltBeamFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos origin = context.origin();
        LevelAccessor level = context.level();
        RandomSource random = context.random();

        Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        int length = MIN_BEAM_LENGTH + random.nextInt(MAX_BEAM_LENGTH - MIN_BEAM_LENGTH + 1);
        int radius = 1 + random.nextInt(MAX_RADIUS);

        BlockPos.MutableBlockPos pos = origin.mutable();

        for (int i = 0; i < length; i++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    BlockPos target = pos.offset(
                            direction.getStepX() * i + dx,
                            dy,
                            direction.getStepZ() * i + dx
                    );
                    if (level.isEmptyBlock(target) || level.getBlockState(target).isAir()) {
                        BlockState toPlace = random.nextInt(10) == 0 ? ORE_BLOCK : BASALT_BLOCK;
                        level.setBlock(target, toPlace, 2);
                    }
                }
            }
        }

        return true;
    }
}