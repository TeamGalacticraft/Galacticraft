/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.content.GCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.ticks.ScheduledTick;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class LunarCaveCarver extends CaveWorldCarver {
    public LunarCaveCarver(Codec<CaveCarverConfiguration> codec) {
        super(codec);
    }

    @Override
    protected int getCaveBound() {
        return 13;
    }

    @Override
    protected float getThickness(RandomSource random) {
        float f = (random.nextFloat() * 2.1F) + random.nextFloat();
        if (random.nextInt(10) == 0) {
            f *= random.nextFloat() * random.nextFloat() * 3.0F + 1.1F; //slightly wider caves
        }
        return f;
    }

    @Override
    protected boolean carveBlock(CarvingContext context, CaveCarverConfiguration config, @NotNull ChunkAccess chunk, Function<BlockPos, Holder<Biome>> posToBiome, CarvingMask carvingMask, BlockPos.MutableBlockPos mutable, BlockPos.MutableBlockPos mutable2, Aquifer aquiferSampler, MutableBoolean mutableBoolean) {
        BlockState blockState = chunk.getBlockState(mutable);
        if (blockState.is(GCBlocks.MOON_TURF) || blockState.is(Blocks.MYCELIUM)) {
            mutableBoolean.setTrue();
        }

        if (!this.canReplaceBlock(config, blockState) && !isDebugEnabled(config)) {
            return false;
        } else {
            BlockState blockState2 = this.getState(context, config, mutable, aquiferSampler);
            if (blockState2 == null) {
                return false;
            } else {
                chunk.setBlockState(mutable, blockState2, false);
                if (aquiferSampler.shouldScheduleFluidUpdate() && !blockState2.getFluidState().isEmpty()) {
                    chunk.getFluidTicks().schedule(ScheduledTick.probe(blockState2.getFluidState().getType(), mutable));
                }

                if (mutableBoolean.isTrue()) {
                    mutable2.setWithOffset(mutable, Direction.DOWN);
                    if (chunk.getBlockState(mutable2).is(GCBlocks.MOON_DIRT)) {
                        context.topMaterial(posToBiome, chunk, mutable2, !blockState2.getFluidState().isEmpty()).ifPresent(blockStatex -> chunk.setBlockState(mutable2, blockStatex, false));
                    }
                }

                return true;
            }
        }
    }

    @Nullable
    private BlockState getState(CarvingContext context, CaveCarverConfiguration config, BlockPos pos, Aquifer sampler) {
        if (pos.getY() <= config.lavaLevel.resolveY(context)) {
            return CAVE_AIR; //LAVA.getBlockState();
        } else {
            BlockState blockState = sampler.computeSubstance(new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ()), 0.0);
            if (blockState == null) {
                return isDebugEnabled(config) ? config.debugSettings.getBarrierState() : null;
            } else {
                return isDebugEnabled(config) ? getDebugState(config, blockState) : blockState;
            }
        }
    }

    private static boolean isDebugEnabled(CarverConfiguration config) {
        return config.debugSettings.isDebugMode();
    }

    private static BlockState getDebugState(CarverConfiguration config, BlockState state) {
        if (state.is(Blocks.AIR)) {
            return config.debugSettings.getAirState();
        } else if (state.is(Blocks.WATER)) {
            BlockState blockState = config.debugSettings.getWaterState();
            return blockState.hasProperty(BlockStateProperties.WATERLOGGED) ? blockState.setValue(BlockStateProperties.WATERLOGGED, true) : blockState;
        } else {
            return state.is(Blocks.LAVA) ? config.debugSettings.getLavaState() : state;
        }
    }
}
