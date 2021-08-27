/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

import java.util.Random;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MultiBlockSurfaceBuilder extends SurfaceBuilder<MultiBlockSurfaceConfig> {
    public MultiBlockSurfaceBuilder(Codec<MultiBlockSurfaceConfig> function) {
        super(function);
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int x, int z, int height, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, int h, long seed, MultiBlockSurfaceConfig surfaceConfig) {
        BlockState blockState = surfaceConfig.getTopMaterial();
        BlockState blockState2 = surfaceConfig.getUnderMaterial();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = -1;
        int j = (int) (noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
        int k = x & 15;
        int l = z & 15;

        for (int m = height; m >= 0; --m) {
            mutable.set(k, m, l);
            BlockState blockState3 = chunk.getBlockState(mutable);
            if (blockState3.isAir()) {
                i = -1;
            } else if (blockState3.getBlock() == defaultBlock.getBlock()) {
                if (i == -1) {
                    if (j <= 0) {
                        blockState = Blocks.AIR.getDefaultState();
                        blockState2 = surfaceConfig.getUnderMaterial();
                    } else if (m >= seaLevel - 4 && m <= seaLevel + 1) {
                        blockState = surfaceConfig.getTopMaterial();
                        blockState2 = surfaceConfig.getUnderMaterial();
                    }

                    if (m < seaLevel && (blockState == null || blockState.isAir())) {
                        if (biome.getTemperature(mutable.set(x, m, z)) < 0.15F) {
                            blockState = Blocks.ICE.getDefaultState();
                        } else {
                            blockState = Blocks.WATER.getDefaultState();
                        }

                        mutable.set(k, m, l);
                    }

                    i = j;
                    if (m >= seaLevel - 1) {
                        chunk.setBlockState(mutable, blockState, false);
                    } else if (m < seaLevel - 7 - j) {
                        blockState = Blocks.AIR.getDefaultState();
                        blockState2 = surfaceConfig.getTopMaterial();
                        chunk.setBlockState(mutable, surfaceConfig.getUnderwaterMaterial(), false);
                    } else {
                        chunk.setBlockState(mutable, blockState2, false);
                    }
                } else if (i > 0) {
                    --i;
                    chunk.setBlockState(mutable, blockState2, false);
                    if (i == 0 && blockState2.getBlock() == Blocks.SAND && j > 1) {
                        i = random.nextInt(4) + Math.max(0, m - 63);
                        blockState2 = blockState2.getBlock() == Blocks.RED_SAND ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
                    }
                }
            }
        }
    }
}
