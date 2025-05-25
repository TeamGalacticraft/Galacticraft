/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSpline;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;

import static javax.swing.Spring.constant;
import static net.minecraft.world.level.levelgen.DensityFunctions.yClampedGradient;

public class GCDensityFunctions {
    public static final ResourceKey<DensityFunction> NOODLES = createKey("caves/noodles");
    public static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("moon/sloped_cheese");
    public static final ResourceKey<DensityFunction> BASE_3D_NOISE_OVERWORLD = ResourceKey.create(Registries.DENSITY_FUNCTION, ResourceLocation.fromNamespaceAndPath("minecraft", "overworld/base_3d_noise"));

    public static final class Moon {
        public static final ResourceKey<DensityFunction> EROSION = createKey("moon/erosion");
        public static final ResourceKey<DensityFunction> FINAL_DENSITY = createKey("moon/final_density");
        public static final ResourceKey<DensityFunction> EROSION_BIOME = createKey("moon/erosion_biome");
    }

    public static final class Venus {
        // Final Density handles overall terrain shape
        public static final ResourceKey<DensityFunction> FINAL_DENSITY = createKey("venus/final_density");
    }

    public static final class Asteroid {
        // Final Density handles overall terrain shape
        public static final ResourceKey<DensityFunction> FINAL_DENSITY = createKey("asteroid/final_density");
    }

    private static ResourceKey<DensityFunction> createKey(String id) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, Constant.id(id));
    }

    public static void bootstrapRegistries(BootstrapContext<DensityFunction> context) {
        var vanillaRegistry = context.lookup(Registries.DENSITY_FUNCTION);
        var noiseRegistry = context.lookup(Registries.NOISE);
        DensityFunction shiftX = getFunction(vanillaRegistry, NoiseRouterData.SHIFT_X);
        DensityFunction shiftZ = getFunction(vanillaRegistry, NoiseRouterData.SHIFT_Z);

        // --- NOODLES ---

        DensityFunction y = getFunction(vanillaRegistry, NoiseRouterData.Y);

        DensityFunction ridgeA = DensityFunctions.interpolated(DensityFunctions.rangeChoice(
                y, -25, 45,
                DensityFunctions.noise(noiseRegistry.getOrThrow(Constant.key(Registries.NOISE, "noodle_ridge_a")), 2.6666666666666665),
                DensityFunctions.constant(0)
        )).abs();

        DensityFunction ridgeB = DensityFunctions.interpolated(DensityFunctions.rangeChoice(
                y, -25, 45,
                DensityFunctions.noise(noiseRegistry.getOrThrow(Constant.key(Registries.NOISE, "noodle_ridge_b")), 2.6666666666666665),
                DensityFunctions.constant(0)
        )).abs();

        DensityFunction noodleDetails = DensityFunctions.add(
                DensityFunctions.interpolated(DensityFunctions.rangeChoice(
                        y, -25, 45,
                        DensityFunctions.add(
                                DensityFunctions.constant(-0.07500000000000001),
                                DensityFunctions.mul(
                                        DensityFunctions.constant(-0.025),
                                        DensityFunctions.noise(noiseRegistry.getOrThrow(Constant.key(Registries.NOISE, "noodle_thickness")))
                                )
                        ),
                        DensityFunctions.constant(0)
                )),
                DensityFunctions.mul(
                        DensityFunctions.constant(1.5),
                        DensityFunctions.max(ridgeA, ridgeB)
                )
        );

        DensityFunction noodles = registerAndWrap(context, NOODLES,
                DensityFunctions.rangeChoice(
                        DensityFunctions.interpolated(DensityFunctions.rangeChoice(
                                y, -25, 45,
                                DensityFunctions.noise(noiseRegistry.getOrThrow(Constant.key(Registries.NOISE, "noodle"))),
                                DensityFunctions.constant(-1)
                        )),
                        -1000000, 0,
                        DensityFunctions.constant(64),
                        noodleDetails
                )
        );

        // --- EROSION ---

        DensityFunction erosion = registerAndWrap(context, Moon.EROSION, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseRegistry.getOrThrow(GCNoiseData.EROSION))));

        DensityFunction erosionLowRes = registerAndWrap(context, Moon.EROSION_BIOME, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseRegistry.getOrThrow(GCNoiseData.EROSION_BIOME))));

        // --- SLOPED CHEESE ---

        DensityFunction slopedCheese = registerAndWrap(context, SLOPED_CHEESE,
                DensityFunctions.add(
                        DensityFunctions.mul(
                                DensityFunctions.constant(4),
                                DensityFunctions.mul(
                                        DensityFunctions.add(
                                                getFunction(vanillaRegistry, NoiseRouterData.DEPTH),
                                                DensityFunctions.mul(
                                                        getFunction(vanillaRegistry, NoiseRouterData.JAGGEDNESS),
                                                        DensityFunctions.noise(noiseRegistry.getOrThrow(Noises.JAGGED), 1000, 0).halfNegative()
                                                )
                                        ),
                                        getFunction(vanillaRegistry, NoiseRouterData.FACTOR)
                                ).quarterNegative()
                        ),
                        getFunction(vanillaRegistry, BASE_3D_NOISE_OVERWORLD)
                                .squeeze()
                                .clamp(0.09999999999999998, 0.05)
                )
        );

        // --- FINAL DENSITY ---

        DensityFunction continentalness = getFunction(vanillaRegistry, NoiseRouterData.CONTINENTS);

        DensityFunction comet = DensityFunctions.yClampedGradient(80, 80, 1.0, -1.0);       // y = 80
        DensityFunction mare = DensityFunctions.yClampedGradient(90, 90, 1.0, -1.0);       // y = 90
        DensityFunction lowlands = DensityFunctions.yClampedGradient(100, 100, 1.0, -1.0);       // y = 100
        DensityFunction highlands = DensityFunctions.yClampedGradient(110, 110, 1.0, -1.0);      // y = 110

        // Use range choice over continentalness to select flat height based on biome
        DensityFunction finalDensity = DensityFunctions.rangeChoice(
                continentalness,
                -1.0, -0.8, comet, // Comet Tundra
                DensityFunctions.rangeChoice(
                        continentalness,
                        -0.8, -0.1, mare, // Mare
                        DensityFunctions.rangeChoice(
                                continentalness,
                                -0.1, 0.4, lowlands, // Lowlands
                                highlands // Highlands
                        )
                )
        );

        context.register(Moon.FINAL_DENSITY, finalDensity);

        context.register(Venus.FINAL_DENSITY, DensityFunctions.add(
                yClampedGradient(0, 90, 1, -1),
                BlendedNoise.createUnseeded(0.25, 0.375, 80.0, 160.0, 8.0)
        ));

        context.register(Asteroid.FINAL_DENSITY, DensityFunctions.add(
                yClampedGradient(0, 90, 1, -1),
                BlendedNoise.createUnseeded(0.25, 0.375, 80.0, 160.0, 8.0)
        ));
    }

    private static DensityFunction registerAndWrap(BootstrapContext<DensityFunction> context, ResourceKey<DensityFunction> key, DensityFunction densityFunction) {
        return new DensityFunctions.HolderHolder(context.register(key, densityFunction));
    }

    public static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
        return new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(key));
    }
}
