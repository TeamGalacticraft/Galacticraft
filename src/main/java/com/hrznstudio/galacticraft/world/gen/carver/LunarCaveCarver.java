/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.mojang.serialization.Codec;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

public class LunarCaveCarver extends CaveWorldCarver {
    public LunarCaveCarver(Codec<ProbabilityFeatureConfiguration> codec, int i) {
        super(codec, i);
        this.replaceableBlocks = ImmutableSet.<Block>builder().addAll(this.replaceableBlocks)
                .add(GalacticraftBlocks.MOON_ROCKS[0])
                .add(GalacticraftBlocks.MOON_SURFACE_ROCK)
                .add(GalacticraftBlocks.MOON_TURF)
                .add(GalacticraftBlocks.MOON_BASALTS[0])
                .add(GalacticraftBlocks.MOON_DIRT)
                .build();
    }

    @Override
    protected int getCaveBound() {
        return 17; //slightly longer caves
    }

    @Override
    protected float getThickness(Random random) {
        float f = (random.nextFloat() * 2.1F) + random.nextFloat();
        if (random.nextInt(10) == 0) {
            f *= random.nextFloat() * random.nextFloat() * 3.0F + 1.1F; //slightly wider caves
        }
        return f;
    }

    @Override
    protected boolean carveBlock(ChunkAccess chunk, Function<BlockPos, Biome> posToBiome, BitSet carvingMask, Random random, BlockPos.MutableBlockPos mutable, BlockPos.MutableBlockPos mutable2, BlockPos.MutableBlockPos mutable3, int seaLevel, int mainChunkX, int mainChunkZ, int x, int z, int relativeX, int y, int relativeZ, MutableBoolean mutableBoolean) {
        int i = relativeX | relativeZ << 4 | y << 8;
        if (carvingMask.get(i)) {
            return false;
        } else {
            carvingMask.set(i);
            mutable.set(x, y, z);
            BlockState blockState = chunk.getBlockState(mutable);
            BlockState blockState2 = chunk.getBlockState(mutable2.setWithOffset(mutable, Direction.UP));
            if (blockState.is(GalacticraftBlocks.MOON_BASALTS[0]) || blockState.is(GalacticraftBlocks.MOON_ROCKS[0])) {
                mutableBoolean.setTrue();
            }

            if (!this.canReplaceBlock(blockState, blockState2)) {
                return false;
            } else {
                if (y < 11) {
                    chunk.setBlockState(mutable, LAVA.createLegacyBlock(), false); //todo what block (preferably fluid) should cover up the bedrock floor?
                } else {
                    chunk.setBlockState(mutable, CAVE_AIR, false);
                    if (mutableBoolean.isTrue()) {
                        mutable3.setWithOffset(mutable, Direction.DOWN);
                        if (chunk.getBlockState(mutable3).is(GalacticraftBlocks.MOON_DIRT)) {
                            chunk.setBlockState(mutable3, posToBiome.apply(mutable).getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial(), false);
                        }
                    }
                }

                return true;
            }
        }
    }
}
