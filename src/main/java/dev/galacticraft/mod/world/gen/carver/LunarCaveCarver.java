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
import net.minecraft.world.gen.carver.*;
import net.minecraft.world.gen.chunk.AquiferSampler;
import net.minecraft.world.tick.OrderedTick;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    protected boolean carveAtPoint(CarverContext context, CaveCarverConfig config, @NotNull Chunk chunk, Function<BlockPos, Biome> posToBiome, CarvingMask carvingMask, BlockPos.Mutable mutable, BlockPos.Mutable mutable2, AquiferSampler aquiferSampler, MutableBoolean mutableBoolean) {
        BlockState blockState = chunk.getBlockState(mutable);
        if (blockState.isOf(Blocks.GRASS_BLOCK) || blockState.isOf(Blocks.MYCELIUM)) {
            mutableBoolean.setTrue();
        }

        if (!this.canAlwaysCarveBlock(blockState) && !isDebug(config)) {
            return false;
        } else {
            BlockState blockState2 = this.getState(context, config, mutable, aquiferSampler);
            if (blockState2 == null) {
                return false;
            } else {
                chunk.setBlockState(mutable, blockState2, false);
                if (aquiferSampler.needsFluidTick() && !blockState2.getFluidState().isEmpty()) {
                    chunk.getFluidTickScheduler().scheduleTick(OrderedTick.create(blockState2.getFluidState().getFluid(), mutable));
                }

                if (mutableBoolean.isTrue()) {
                    mutable2.set(mutable, Direction.DOWN);
                    if (chunk.getBlockState(mutable2).isOf(Blocks.DIRT)) {
                        context.method_39114(posToBiome, chunk, mutable2, !blockState2.getFluidState().isEmpty()).ifPresent(blockStatex -> chunk.setBlockState(mutable2, blockStatex, false));
                    }
                }

                return true;
            }
        }
    }

    @Nullable
    private BlockState getState(CarverContext context, CaveCarverConfig config, BlockPos pos, AquiferSampler sampler) {
        if (pos.getY() <= config.lavaLevel.getY(context)) {
            return CAVE_AIR; //LAVA.getBlockState();
        } else {
            BlockState blockState = sampler.apply(pos.getX(), pos.getY(), pos.getZ(), 0.0, 0.0);
            if (blockState == null) {
                return isDebug(config) ? config.debugConfig.getBarrierState() : null;
            } else {
                return isDebug(config) ? getDebugState(config, blockState) : blockState;
            }
        }
    }

    private static boolean isDebug(CarverConfig config) {
        return config.debugConfig.isDebugMode();
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
