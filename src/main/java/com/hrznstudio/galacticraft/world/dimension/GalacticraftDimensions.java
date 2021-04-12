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

package com.hrznstudio.galacticraft.world.dimension;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.gen.chunk.MoonChunkGenerator;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSamplingSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.NoiseSlideSettings;
import net.minecraft.world.level.levelgen.StructureSettings;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftDimensions {
    public static final ResourceKey<Level> MOON = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(Constants.MOD_ID, "moon"));

    public static void register() {
        BuiltinRegistries.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, new ResourceLocation(Constants.MOD_ID, "moon"), new NoiseGeneratorSettings(
                new StructureSettings(false),
                new NoiseSettings(
                        256, new NoiseSamplingSettings(0.9999999814507745D, 0.9999999814507745D, 80.0D, 160.0D),
                        new NoiseSlideSettings(-10, 3, 0), new NoiseSlideSettings(-30, 0, 0),
                        1, 2, 1.0D, -0.46875D, true,
                        true, false, false),
                GalacticraftBlocks.MOON_ROCKS[0].defaultBlockState(), Blocks.AIR.defaultBlockState(), -10, 0, 63, false)
        );

        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(Constants.MOD_ID, "moon"), MoonChunkGenerator.CODEC);
//        FabricDimensions.registerDefaultPlacer(MOON, GalacticraftDimensions::placeEntity);
    }
}
