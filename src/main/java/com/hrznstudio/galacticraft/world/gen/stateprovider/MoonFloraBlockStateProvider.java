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

package com.hrznstudio.galacticraft.world.gen.stateprovider;

import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonFloraBlockStateProvider extends BlockStateProvider {
    public static final Codec<MoonFloraBlockStateProvider> CODEC = Codec.unit(() -> MoonFloraBlockStateProvider.INSTANCE);
    public static final MoonFloraBlockStateProvider INSTANCE = new MoonFloraBlockStateProvider();

    public static final BlockState[] mix1 = new BlockState[]{GalacticraftBlocks.MOON_BERRY_BUSH.defaultBlockState()};
    public static final BlockState[] mix2 = new BlockState[]{GalacticraftBlocks.MOON_BERRY_BUSH.defaultBlockState()};

    @Override
    protected BlockStateProviderType<MoonFloraBlockStateProvider> type() {
        return GalacticraftFeatures.MOON_FLOWER_PROVIDER;
    }

    public BlockState getState(Random random, BlockPos pos) {
        double d = Biome.BIOME_INFO_NOISE.getValue((double) pos.getX() / 200.0D, (double) pos.getZ() / 200.0D, false);
        if (d < -0.8D) {
            return Util.getRandom(mix1, random);
        } else {
            return random.nextInt(3) > 0 ? Util.getRandom(mix2, random) : GalacticraftBlocks.MOON_BERRY_BUSH.defaultBlockState();
        }
    }

}
