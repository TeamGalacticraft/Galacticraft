/*
 * Copyright (c) 2020 HRZN LTD
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

package com.hrznstudio.galacticraft.world.gen.stateprovider;

import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonFloraBlockStateProvider extends BlockStateProvider {
    public static final Codec<MoonFloraBlockStateProvider> CODEC = Codec.unit(() -> MoonFloraBlockStateProvider.INSTANCE);
    public static final MoonFloraBlockStateProvider INSTANCE = new MoonFloraBlockStateProvider();

    public static final BlockState[] mix1 = new BlockState[]{GalacticraftBlocks.MOON_BERRY_BUSH.getDefaultState()};
    public static final BlockState[] mix2 = new BlockState[]{GalacticraftBlocks.MOON_BERRY_BUSH.getDefaultState()};

    @Override
    protected BlockStateProviderType<MoonFloraBlockStateProvider> getType() {
        return GalacticraftFeatures.MOON_FLOWER_PROVIDER;
    }

    public BlockState getBlockState(Random random, BlockPos pos) {
        double d = Biome.FOLIAGE_NOISE.sample((double) pos.getX() / 200.0D, (double) pos.getZ() / 200.0D, false);
        if (d < -0.8D) {
            return Util.getRandom(mix1, random);
        } else {
            return random.nextInt(3) > 0 ? Util.getRandom(mix2, random) : GalacticraftBlocks.MOON_BERRY_BUSH.getDefaultState();
        }
    }

}
