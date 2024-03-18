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
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;

public class GCDensityFunctions {
    public static final ResourceKey<DensityFunction> NOODLES = createKey("caves/noodles");
    public static final class Moon {
        public static final ResourceKey<DensityFunction> EROSION = createKey("moon/erosion");
        public static final ResourceKey<DensityFunction> FINAL_DENSITY = createKey("moon/final_density");
    }
    public static final class Venus {
        // Final Density handles overall terrain shape
        public static final ResourceKey<DensityFunction> FINAL_DENSITY = createKey("venus/final_density");
    }

    private static ResourceKey<DensityFunction> createKey(String id) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, Constant.id(id));
    }
    public static void bootstrapRegistries(BootstapContext<DensityFunction> context) {
        var vanillaRegistry = context.lookup(Registries.DENSITY_FUNCTION);
        var noiseRegistry = context.lookup(Registries.NOISE);
        DensityFunction shiftX = getFunction(vanillaRegistry, NoiseRouterData.SHIFT_X);
        DensityFunction shiftZ = getFunction(vanillaRegistry, NoiseRouterData.SHIFT_Z);
        DensityFunction y = getFunction(vanillaRegistry, NoiseRouterData.Y);
//        DensityFunction noodles = registerAndWrap(context, NOODLES, DensityFunctions.rangeChoice(
//                DensityFunctions.interpolated(
//                        DensityFunctions.rangeChoice(
//                                y, -25, 45,
//                                DensityFunctions.noise(noiseRegistry.getOrThrow(Noises.NOODLE), 1, 1),
//                                DensityFunctions.constant(-1)
//                        )
//                ),
//                -1000000, 0, DensityFunctions.constant(64),
//                DensityFunctions.add(
//                        DensityFunctions.interpolated(
//                                DensityFunctions.rangeChoice(
//                                        y, -25, 45,
//                                        DensityFunctions.add(
//                                                DensityFunctions.constant(-0.07500000000000001),
//                                                DensityFunctions.mul(
//                                                        DensityFunctions.constant(-0.025),
//                                                        DensityFunctions.noise(noiseRegistry.getOrThrow(Noises.NOODLE_THICKNESS), 1, 1)
//                                                )
//                                        ),
//                                        DensityFunctions.constant(0)
//                                )
//                        ),
//                        DensityFunctions.mul(
//                                DensityFunctions.constant(1.5),
//                                DensityFunctions.max(
//                                        DensityFunctions.interpolated(
//                                                DensityFunctions.rangeChoice(
//                                                        y, -25, 45,
//                                                        DensityFunctions.noise(noiseRegistry.getOrThrow(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665),
//                                                        DensityFunctions.zero()
//                                                )
//                                        ).abs(),
//                                        DensityFunctions.interpolated(
//                                                DensityFunctions.rangeChoice(
//                                                        y, -25, 45,
//                                                        DensityFunctions.noise(noiseRegistry.getOrThrow(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665),
//                                                        DensityFunctions.zero()
//                                                    )
//                                        ).abs()
//                                )
//                        )
//                )
//        ));
//        DensityFunction erosion = registerAndWrap(context, Moon.EROSION, DensityFunctions.flatCache(
//              DensityFunctions.shiftedNoise2d(
//                      shiftX, shiftZ, 1, noiseRegistry.getOrThrow(GCNoiseData.EROSION)
//              )
//        ));
//        context.register(Moon.FINAL_DENSITY, DensityFunctions.min(
//                DensityFunctions.add(
//                        DensityFunctions.interpolated(
//                                DensityFunctions.blendDensity(
//                                        DensityFunctions.rangeChoice(
//                                                erosion, 0.05, 2.000000000000001,
//                                                DensityFunctions.yClampedGradient(-64, 190, 1, -1),
//                                                DensityFunctions.yClampedGradient(-64, 200, 1, -1)
//                                        )
//                                )
//                        ),
//                        DensityFunctions.add(
//                                DensityFunctions.noise(noiseRegistry.getOrThrow(GCNoiseData.BASALT_MARE), 0.00005, 0.0007),
//                                DensityFunctions.noise(noiseRegistry.getOrThrow(GCNoiseData.BASALT_MARE_HEIGHT), 0, 0)
//                        )
//                ),
//                noodles
//            )
//        );

        context.register(Venus.FINAL_DENSITY, DensityFunctions.add(
                DensityFunctions.yClampedGradient(0, 90, 1, -1),
                BlendedNoise.createUnseeded(0.25, 0.375, 80.0, 160.0, 8.0)
        ));
    }

    private static DensityFunction registerAndWrap(BootstapContext<DensityFunction> context, ResourceKey<DensityFunction> key, DensityFunction densityFunction) {
        return new DensityFunctions.HolderHolder(context.register(key, densityFunction));
    }

    public static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
        return new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(key));
    }
}
