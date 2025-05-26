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
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import static javax.swing.Spring.constant;
import static net.minecraft.world.level.levelgen.DensityFunctions.*;

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

        DensityFunction y = getFunction(vanillaRegistry, NoiseRouterData.Y);

        // --- NOODLES ---

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

        context.register(Moon.EROSION, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseRegistry.getOrThrow(GCNoiseData.EROSION))));

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
        int cometHeight = 60;
        int mareHeight = 60;
        int lowLandsHeight = 80;
        int highLandsHeight = 140;

        int variationComet = 3;
        int variationMare = 3;
        int variationLowLands = 20;
        int variationHighLands = 3;

        float cometContinentalnessMinimum = -1.2f;
        float mareContinentalnessMinimum = -0.455f;
        float lowLandsContinentalnessMinimum = -0.11f;
        float highLandsContinentalnessMinimum = 0.3f;

        float lowToHighLandsRange = 0.05f;
        float mareToLowLandsRange = 0.1f;

        DensityFunction continentalness = getFunction(vanillaRegistry, NoiseRouterData.CONTINENTS);

        Holder.Reference<NormalNoise.NoiseParameters> erosionHolder = noiseRegistry.getOrThrow(GCNoiseData.EROSION);

        DensityFunction cometNoise = make2DNoise(5, shiftX, shiftZ, erosionHolder);
        DensityFunction mareNoise = make2DNoise(5, shiftX, shiftZ, erosionHolder);
        DensityFunction lowLandsNoise = make2DNoise(2, shiftX, shiftZ, erosionHolder);
        DensityFunction highLandsNoise = make2DNoise(5, shiftX, shiftZ, erosionHolder);

        DensityFunction cometNoisedHeightUnclamped = clampedNoisedHeight(cometHeight, variationComet, cometNoise, y);
        DensityFunction mareNoisedHeightUnclamped = clampedNoisedHeight(mareHeight, variationMare, mareNoise, y);
        DensityFunction lowLandsNoisedHeightUnclamped = clampedNoisedHeight(lowLandsHeight, variationLowLands, lowLandsNoise, y);
        DensityFunction highLandsNoisedHeightUnclamped = clampedNoisedHeight(highLandsHeight, variationHighLands, highLandsNoise, y);

        DensityFunction normalizedLowToHighLands = normalizeInRange(continentalness, highLandsContinentalnessMinimum - lowToHighLandsRange, highLandsContinentalnessMinimum + lowToHighLandsRange);
        DensityFunction normalizedMareToLowLands = normalizeInRange(continentalness, lowLandsContinentalnessMinimum - mareToLowLandsRange, lowLandsContinentalnessMinimum + mareToLowLandsRange);

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> lowToHighLandsSpline = CubicSpline
                .builder(new DensityFunctions.Spline.Coordinate(Holder.direct(normalizedLowToHighLands)))
                .addPoint(-1f, 0f, 0.0f)
                .addPoint(-0.2f, 0.1f, 0.0f)
                .addPoint(0f, 0.5f, 0.0f)
                .addPoint(0.2f, 0.9f, 0.0f)
                .addPoint(1f, 1f, 0.0f)
                .build();

        CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> mareToLowLandsSpline = CubicSpline
                .builder(new DensityFunctions.Spline.Coordinate(Holder.direct(normalizedMareToLowLands)))
                .addPoint(-1f, 0f, 0.0f)
                .addPoint(-0.7f, 0.3f, 0.0f)
                .addPoint(0f, 0.5f, 0.0f)
                .addPoint(0.7f, 0.7f, 0.0f)
                .addPoint(1f, 1f, 0.0f)
                .build();

        DensityFunction lowToHighLandsSplineModifier = DensityFunctions.spline(lowToHighLandsSpline);
        DensityFunction mareToLowLandsSplineModifier = DensityFunctions.spline(mareToLowLandsSpline);

        // Blend target heights before converting to density
        DensityFunction blendedLowToHighLandsNoisedHeightClamped = heightToDensity(interpolated(DensityFunctions.lerp(
                lowToHighLandsSplineModifier,
                lowLandsNoisedHeightUnclamped,
                highLandsNoisedHeightUnclamped
        )), y);

        DensityFunction blendedMareToLowLandsNoisedHeightClamped = heightToDensity(interpolated(DensityFunctions.lerp(
                mareToLowLandsSplineModifier,
                mareNoisedHeightUnclamped,
                lowLandsNoisedHeightUnclamped
        )), y);

        // Use range choice to get noise + base height
        DensityFunction finalDensity = rangeChoice(
                continentalness,
                cometContinentalnessMinimum, mareContinentalnessMinimum,
                heightToDensity(cometNoisedHeightUnclamped, y), // comet tundra base height

                rangeChoice(
                        continentalness,
                        mareContinentalnessMinimum, lowLandsContinentalnessMinimum - mareToLowLandsRange,
                        heightToDensity(mareNoisedHeightUnclamped, y), // basaltic mare base height

                        rangeChoice(
                                continentalness,
                                lowLandsContinentalnessMinimum - mareToLowLandsRange, lowLandsContinentalnessMinimum + mareToLowLandsRange,
                                blendedMareToLowLandsNoisedHeightClamped, // basaltic mare blended to lunar low lands

                                rangeChoice(
                                        continentalness,
                                        lowLandsContinentalnessMinimum + mareToLowLandsRange, highLandsContinentalnessMinimum - lowToHighLandsRange,
                                        heightToDensity(lowLandsNoisedHeightUnclamped, y), // lunar low lands base height

                                        rangeChoice(
                                                continentalness,
                                                highLandsContinentalnessMinimum - lowToHighLandsRange, highLandsContinentalnessMinimum + lowToHighLandsRange,
                                                blendedLowToHighLandsNoisedHeightClamped, // lunar low lands blended to lunar high lands

                                                heightToDensity(highLandsNoisedHeightUnclamped, y) // lunar high lands base height
                                        )
                                )
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

    public static DensityFunction make2DNoise(double scaleXZ, DensityFunction shiftX, DensityFunction shiftZ, Holder<NormalNoise.NoiseParameters> erosion) {
        return DensityFunctions.shiftedNoise2d(shiftX, shiftZ, scaleXZ, erosion);
    }

    private static DensityFunction clampedNoisedHeight(
            int height, int variation, DensityFunction noise, DensityFunction y
    ) {
        // Scale normalized noise [−1, 1] to variation range [−variation, variation]
        DensityFunction scaledNoise = DensityFunctions.mul(
                noise,
                DensityFunctions.constant(variation)
        );

        // Add to base height
        DensityFunction targetHeight = DensityFunctions.add(
                DensityFunctions.constant(height),
                scaledNoise
        );

        return targetHeight;
    }

    private static DensityFunction heightToDensity(DensityFunction height, DensityFunction y) {
        return DensityFunctions.add(
                height,
                DensityFunctions.mul(DensityFunctions.constant(-1), y)
        ).clamp(-1.0, 1.0);
    }

    public static DensityFunction normalizeInRange(DensityFunction value, double min, double max) {
        double range = max - min;

        // (value - min) / range → [0, 1] for values inside range
        DensityFunction normalized = DensityFunctions.mul(
                DensityFunctions.add(value, DensityFunctions.constant(-min)),
                DensityFunctions.constant(1.0 / range)
        );

        // Map [0, 1] → [-1, 1]
        DensityFunction scaled = DensityFunctions.add(
                DensityFunctions.mul(normalized, DensityFunctions.constant(2.0)),
                DensityFunctions.constant(-1.0)
        );

        return scaled.clamp(-1, 1);
    }
}
