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

package dev.galacticraft.mod.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import dev.galacticraft.mod.block.GalacticraftBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CaveCarver;
import net.minecraft.world.gen.carver.CaveCarverConfig;
import net.minecraft.world.gen.chunk.AquiferSampler;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class LunarCaveCarver extends CaveCarver {
    public LunarCaveCarver(Codec<CaveCarverConfig> codec) {
        super(codec);
        this.alwaysCarvableBlocks = ImmutableSet.<Block>builder().addAll(this.alwaysCarvableBlocks)
                .add(GalacticraftBlock.MOON_ROCKS[0])
                .add(GalacticraftBlock.MOON_SURFACE_ROCK)
                .add(GalacticraftBlock.MOON_TURF)
                .add(GalacticraftBlock.MOON_BASALTS[0])
                .add(GalacticraftBlock.MOON_DIRT)
                .build();
    }

    @Override
    protected int getMaxCaveCount() {
        return 13;
    }

    @Override
    protected float getTunnelSystemWidth(Random random) {
        float f = (random.nextFloat() * 2.1F) + random.nextFloat();
        if (random.nextInt(10) == 0) {
            f *= random.nextFloat() * random.nextFloat() * 3.0F + 1.1F; //slightly wider caves
        }
        return f;
    }

    @Override
    protected boolean carveAtPoint(CarverContext context, CaveCarverConfig config, Chunk chunk, Function<BlockPos, Biome> posToBiome, BitSet carvingMask, Random random, BlockPos.Mutable pos, BlockPos.Mutable downPos, AquiferSampler sampler, MutableBoolean foundSurface) {
        BlockState blockState = chunk.getBlockState(pos);
        BlockState blockState2 = chunk.getBlockState(downPos.set(pos, Direction.UP));
        if (blockState.isOf(GalacticraftBlock.MOON_TURF) || blockState.isOf(GalacticraftBlock.MOON_SURFACE_ROCK)) {
            foundSurface.setTrue();
        }

        if (!this.canCarveBlock(blockState, blockState2) && !config.debugConfig.isDebugMode()) {
            return false;
        } else {
            BlockState blockState3 = this.getState(context, config, pos, sampler);
            if (blockState3 == null) {
                return false;
            } else {
                chunk.setBlockState(pos, blockState3, false);
                if (foundSurface.isTrue()) {
                    downPos.set(pos, Direction.DOWN);
                    if (chunk.getBlockState(downPos).isOf(GalacticraftBlock.MOON_DIRT)) {
                        chunk.setBlockState(downPos, posToBiome.apply(pos).getGenerationSettings().getSurfaceConfig().getTopMaterial(), false);
                    }
                }

                return true;
            }
        }
    }
    
    @Nullable
    private BlockState getState(CarverContext context, CarverConfig config, BlockPos pos, AquiferSampler sampler) {
        if (pos.getY() <= config.lavaLevel.getY(context)) {
            return CAVE_AIR;
        } else if (!config.aquifers) {
            return config.debugConfig.isDebugMode() ? getDebugState(config, AIR) : AIR;
        } else {
            BlockState blockState = sampler.apply(STONE_SOURCE, pos.getX(), pos.getY(), pos.getZ(), 0.0D);
            if (blockState == GalacticraftBlock.MOON_ROCKS[0].getDefaultState()) {
                return config.debugConfig.isDebugMode() ? config.debugConfig.getBarrierState() : null;
            } else {
                return config.debugConfig.isDebugMode() ? getDebugState(config, blockState) : blockState;
            }
        }
    }

    private static BlockState getDebugState(CarverConfig config, BlockState state) {
        if (state.isOf(Blocks.AIR)) {
            return config.debugConfig.getAirState();
        } else if (state.isOf(Blocks.WATER)) {
            BlockState blockState = config.debugConfig.getWaterState();
            return blockState.contains(Properties.WATERLOGGED) ? blockState.with(Properties.WATERLOGGED, true) : blockState;
        } else {
            return state.isOf(Blocks.LAVA) ? config.debugConfig.getLavaState() : state;
        }
    }
}
