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

package dev.galacticraft.mod.world.gen.feature;

import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.block.GalacticraftBlocks;
import dev.galacticraft.mod.world.gen.stateprovider.MoonFloraBlockStateProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.stateprovider.PillarBlockStateProvider;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftFeatures {
    public static final RandomPatchFeatureConfig MOON_FLOWER_CONFIG = new RandomPatchFeatureConfig.Builder(new MoonFloraBlockStateProvider(), new SimpleBlockPlacer()).tries(64).build();
    public static final BlockPileFeatureConfig CHEESE_LOG_PILE_CONFIG = new BlockPileFeatureConfig(new PillarBlockStateProvider(GalacticraftBlocks.MOON_CHEESE_LOG));
    public static final BlockStateProviderType<MoonFloraBlockStateProvider> MOON_FLOWER_PROVIDER = Registry.register(Registry.BLOCK_STATE_PROVIDER_TYPE, new Identifier(Constants.MOD_ID, "moon_flower_provider"), new BlockStateProviderType<>(MoonFloraBlockStateProvider.CODEC));
    public static final ConfiguredFeature<?, ?> OIL_LAKE = Feature.LAKE.configure(new SingleStateFeatureConfig(GalacticraftBlocks.CRUDE_OIL.getDefaultState())).decorate(Decorator.WATER_LAKE.configure(new ChanceDecoratorConfig(70)));

    public static void register() {
    }
}
