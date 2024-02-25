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

package dev.galacticraft.mod.world.gen;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.gen.surfacebuilder.MoonSurfaceRules;
import dev.galacticraft.mod.world.gen.surfacebuilder.VenusSurfaceRules;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * This class describes how terrain should be shaped based on the given density fuctions
 */
public class GCNoiseGeneratorSettings {
    public static final ResourceKey<NoiseGeneratorSettings> MOON = key("moon");
    public static final ResourceKey<NoiseGeneratorSettings> VENUS = key("venus");

    public static void bootstrapRegistries(BootstapContext<NoiseGeneratorSettings> context) {
        HolderGetter<DensityFunction> densityLookup = context.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<NormalNoise.NoiseParameters> noiseLookup = context.lookup(Registries.NOISE);

//        context.register(MOON, new NoiseGeneratorSettings(
//                NoiseSettings.create(-32, 256, 1, 2),
//                GCBlocks.MOON_ROCK.defaultBlockState(),
//                Blocks.AIR.defaultBlockState(),
//                GCNoiseGeneratorSettings.moon(densityLookup, noiseLookup),
//                MoonSurfaceRules.MOON,
//                new OverworldBiomeBuilder().spawnTarget(),
//                -32,
//                false,
//                false,
//                true,
//                false
//        ));

        context.register(VENUS, new NoiseGeneratorSettings(
                NoiseSettings.create(-32, 256, 1, 2),
                GCBlocks.HARD_VENUS_ROCK.defaultBlockState(),
                Blocks.AIR.defaultBlockState(),
                GCNoiseGeneratorSettings.venus(densityLookup, noiseLookup),
                VenusSurfaceRules.VENUS,
                new OverworldBiomeBuilder().spawnTarget(),
                -32,
                false,
                false,
                true,
                false
        ));
    }

    public static NoiseRouter moon(HolderGetter<DensityFunction> densityLookup, HolderGetter<NormalNoise.NoiseParameters> noiseLookup) {
        DensityFunction shiftX = GCDensityFunctions.getFunction(densityLookup, NoiseRouterData.SHIFT_X);
        DensityFunction shiftZ = GCDensityFunctions.getFunction(densityLookup, NoiseRouterData.SHIFT_Z);
        DensityFunction y = GCDensityFunctions.getFunction(densityLookup, NoiseRouterData.Y);
        return new NoiseRouter(
                DensityFunctions.constant(1), // barrierNoise
                DensityFunctions.zero(), // fluidLevelFloodednessNoise
                DensityFunctions.zero(), // fluidLevelSpreadNoise
                DensityFunctions.zero(), // lavaNoise
                DensityFunctions.shiftedNoise2d(
                        shiftX, shiftZ, 0.25, noiseLookup.getOrThrow(Noises.TEMPERATURE)
                ), // temperature
                DensityFunctions.shiftedNoise2d(
                        shiftX, shiftZ, 0, noiseLookup.getOrThrow(Noises.VEGETATION)
                ), // vegetation
                GCDensityFunctions.getFunction(densityLookup, NoiseRouterData.CONTINENTS), // continents
                GCDensityFunctions.getFunction(densityLookup, GCDensityFunctions.Moon.EROSION), // erosion
                GCDensityFunctions.getFunction(densityLookup, NoiseRouterData.DEPTH), // depth
                GCDensityFunctions.getFunction(densityLookup, NoiseRouterData.RIDGES), // ridges
                DensityFunctions.add(
                        DensityFunctions.constant(0.1171875),
                        DensityFunctions.mul(
                                DensityFunctions.yClampedGradient(
                                        -30, -40, 0, 1
                                ),
                                DensityFunctions.add(
                                        DensityFunctions.constant(-0.1171875),
                                        DensityFunctions.add(
                                                DensityFunctions.constant(-0.078125),
                                                DensityFunctions.mul(
                                                        DensityFunctions.yClampedGradient(
                                                                240, 256, 1, 0
                                                        ),
                                                        DensityFunctions.add(
                                                                DensityFunctions.constant(0.078125),
                                                                DensityFunctions.add(
                                                                        DensityFunctions.constant(-0.703125),
                                                                        DensityFunctions.mul(
                                                                                DensityFunctions.constant(4),
                                                                                DensityFunctions.mul(
                                                                                        GCDensityFunctions.getFunction(densityLookup, NoiseRouterData.DEPTH),
                                                                                        DensityFunctions.cache2d(GCDensityFunctions.getFunction(densityLookup, NoiseRouterData.FACTOR))
                                                                                ).quarterNegative()
                                                                        )
                                                                ).clamp(-30, 64)
                                                        )
                                                )
                                        )
                                )
                        )
                ), // initialDensityWithoutJaggedness
                DensityFunctions.blendDensity(GCDensityFunctions.getFunction(densityLookup, GCDensityFunctions.Moon.FINAL_DENSITY)), // finalDensity
                DensityFunctions.interpolated(
                        DensityFunctions.rangeChoice(
                                y, -25, 51,
                                DensityFunctions.noise(noiseLookup.getOrThrow(Noises.ORE_VEININESS), 1.5, 1.5),
                                DensityFunctions.zero()
                        )
                ), // veinToggle
                DensityFunctions.add(
                        DensityFunctions.constant(-0.07999999821186066),
                        DensityFunctions.max(
                                DensityFunctions.interpolated(
                                        DensityFunctions.rangeChoice(
                                                y, -25, 51,
                                                DensityFunctions.noise(noiseLookup.getOrThrow(Noises.ORE_VEIN_A), 4, 4),
                                                DensityFunctions.zero()
                                        )
                                ).abs(),
                                DensityFunctions.interpolated(
                                       DensityFunctions.rangeChoice(
                                               y, -25, 51,
                                               DensityFunctions.noise(noiseLookup.getOrThrow(Noises.ORE_VEIN_B), 4, 4),
                                               DensityFunctions.zero()
                                           )
                                ).abs()
                        )
                ), // veinRidged
                DensityFunctions.noise(noiseLookup.getOrThrow(Noises.ORE_GAP)) // veinGap
        );
    }

    public static NoiseRouter venus(HolderGetter<DensityFunction> densityLookup, HolderGetter<NormalNoise.NoiseParameters> noiseLookup) {
        return new NoiseRouter(
                DensityFunctions.zero(), // barrierNoise
                DensityFunctions.zero(), // fluidLevelFloodednessNoise
                DensityFunctions.zero(), // fluidLevelSpreadNoise
                DensityFunctions.zero(), // lavaNoise
                DensityFunctions.zero(), // temperature
                DensityFunctions.zero(), // vegetation
                DensityFunctions.zero(), // continents
                DensityFunctions.zero(), // erosion
                DensityFunctions.zero(), // depth
                DensityFunctions.zero(), // ridges
                DensityFunctions.noise(noiseLookup.getOrThrow(Noises.SPAGHETTI_3D_1)), // initialDensityWithoutJaggedness
                DensityFunctions.blendDensity(GCDensityFunctions.getFunction(densityLookup, GCDensityFunctions.Venus.FINAL_DENSITY)), // finalDensity
                DensityFunctions.zero(), // veinToggle
                DensityFunctions.zero(), // veinRidged
                DensityFunctions.zero()  // veinGap
        );
    }

    @Contract(value = "_ -> new", pure = true)
    private static @NotNull ResourceKey<NoiseGeneratorSettings> key(String id) {
        return Constant.key(Registries.NOISE_SETTINGS, id);
    }
}
