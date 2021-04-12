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

package com.hrznstudio.galacticraft.world.gen.feature;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.gen.stateprovider.MoonFloraBlockStateProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.blockplacers.SimpleBlockPlacer;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.stateproviders.RotatedBlockProvider;
import net.minecraft.world.level.levelgen.placement.ChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftFeatures {
    public static final RandomPatchConfiguration MOON_FLOWER_CONFIG = new RandomPatchConfiguration.GrassConfigurationBuilder(new MoonFloraBlockStateProvider(), new SimpleBlockPlacer()).tries(64).build();
    public static final BlockPileConfiguration CHEESE_LOG_PILE_CONFIG = new BlockPileConfiguration(new RotatedBlockProvider(GalacticraftBlocks.MOON_CHEESE_LOG));
    public static final BlockStateProviderType<MoonFloraBlockStateProvider> MOON_FLOWER_PROVIDER = Registry.register(Registry.BLOCKSTATE_PROVIDER_TYPES, new ResourceLocation(Constants.MOD_ID, "moon_flower_provider"), new BlockStateProviderType<>(MoonFloraBlockStateProvider.CODEC));
    public static final ConfiguredFeature<?, ?> OIL_LAKE = Feature.LAKE.configured(new BlockStateConfiguration(GalacticraftBlocks.CRUDE_OIL.defaultBlockState())).decorated(FeatureDecorator.WATER_LAKE.configured(new ChanceDecoratorConfiguration(70)));

    public static void register() {
    }
}
