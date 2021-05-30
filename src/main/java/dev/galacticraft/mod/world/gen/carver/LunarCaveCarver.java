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
import dev.galacticraft.mod.block.GalacticraftBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CaveCarver;
import net.minecraft.world.gen.carver.CaveCarverConfig;
import net.minecraft.world.gen.chunk.AquiferSampler;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class LunarCaveCarver extends CaveCarver {
    public LunarCaveCarver() {
        super(CaveCarverConfig.CAVE_CODEC);
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
        return 17; //slightly longer caves
    }

    @Override
    protected float getTunnelSystemWidth(Random random) {
        float f = (random.nextFloat() * 2.1F) + random.nextFloat();
        if (random.nextInt(10) == 0) {
            f *= random.nextFloat() * random.nextFloat() * 3.0F + 1.1F; //slightly wider caves
        }
        return f;
    }

    @Nullable
    private BlockState getState(CarverContext context, CaveCarverConfig config, BlockPos pos, AquiferSampler sampler) {
        if (pos.getY() <= config.lavaLevel.getY(context)) {
            return /*LAVA.getBlockState()*/null;
        } else/* if (!config.aquifers)*/ {
            return AIR;
        }/* else {
            BlockState blockState = sampler.apply(STONE_SOURCE, pos.getX(), pos.getY(), pos.getZ(), 0.0D);
            if (blockState == Blocks.STONE.getDefaultState()) {
                return null;
            } else {
                return blockState;
            }
        }*/
    }
}
