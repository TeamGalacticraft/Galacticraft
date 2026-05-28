/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.world.gen.feature.custom;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.content.GCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class OlivineBeamFeature extends Feature<NoneFeatureConfiguration> {
    private static final BlockState OLIVINE_BLOCK = GCBlocks.OLIVINE_BLOCK.defaultBlockState();
    private static final int MIN_BEAM_LENGTH = 8;
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
        int length = MIN_BEAM_LENGTH + random.nextInt(MAX_BEAM_LENGTH - MIN_BEAM_LENGTH + 1);
        int radius = 1 + random.nextInt(2);

        boolean placedAny = false;

        for (int i = 0; i < length; i++) {
            BlockPos center = origin.relative(direction, i);

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dz = -radius; dz <= radius; dz++) {
                        if ((dx * dx) + (dy * dy) + (dz * dz) > radius * radius) {
                            continue;
                        }

                        BlockPos target = center.offset(dx, dy, dz);

                        if (level.isEmptyBlock(target)) {
                            level.setBlock(target, OLIVINE_BLOCK, 2);
                            placedAny = true;
                        }
                    }
                }
            }
        }

        return placedAny;
    }
}