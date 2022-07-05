/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.world.gen.chunk;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.world.gen.surfacebuilder.MoonSurfaceRules;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseSettings;

import java.util.Collections;

public class GalacticraftChunkGeneratorSettings {
    public static final ResourceKey<NoiseGeneratorSettings> MOON = ResourceKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, Constant.id("moon"));

    public static void register() { // TODO: PORT
        NoiseSettings shapeConfig = NoiseSettings.create(
                -64,
                384,
                4,
                8);
        BuiltinRegistries.register(BuiltinRegistries.NOISE_GENERATOR_SETTINGS, MOON, new NoiseGeneratorSettings(
//                new StructuresConfig(
//                        Optional.empty(),
//                        ImmutableMap.of(
//                                GalacticraftStructureFeature.MOON_PILLAGER_OUTPOST, new StructureConfig(32, 16, 100737579),
//                                GalacticraftStructureFeature.MOON_RUINS, new StructureConfig(24, 8, 181808579),
//                                StructureFeature.VILLAGE, new StructureConfig(24, 8, 197408419)
//                        )
//                ),
                shapeConfig,
                GalacticraftBlock.MOON_ROCK.defaultBlockState(),
                Blocks.AIR.defaultBlockState(),
                NoiseRouterData.caves(BuiltinRegistries.DENSITY_FUNCTION), // TODO: PORT
                MoonSurfaceRules.createDefaultRule(),
                Collections.emptyList(),
                -65, // seaLevel - mapped incorrectly on yarn
                false,
                false,
                true,
                false
        ));
    }
}
