/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.biome.GalacticraftBiome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.MultiNoiseUtil.ParameterRange;

import java.util.Optional;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftBiomeNoisePreset {
    private static final ParameterRange ZERO = ParameterRange.of(0.0F);

    private static final ParameterRange RIVER_CONTINENTALNESS = ParameterRange.of(-0.11F, 0.55F);
    private static final ParameterRange SHORE_CONTINENTALNESS = ParameterRange.of(-0.19F, -0.11F);
    private static final ParameterRange NEAR_INLAND_CONTINENTALNESS = ParameterRange.of(-0.11F, 0.03F);
    private static final ParameterRange MID_INLAND_CONTINENTALNESS = ParameterRange.of(0.03F, 0.3F);
    private static final ParameterRange FAR_INLAND_CONTINENTALNESS = ParameterRange.of(0.3F, 1.0F);

    private static final ParameterRange MAX_EROSION = ParameterRange.of(-1.0F, -0.78F); //todo better names
    private static final ParameterRange ERODED = ParameterRange.of(-0.78F, -0.375F);
    private static final ParameterRange MOSTLY_ERODED = ParameterRange.of(-0.375F, -0.2225F);
    private static final ParameterRange MED_EROSION = ParameterRange.of(-0.2225F, 0.05F);
    private static final ParameterRange SOMEWHAT_ERODED = ParameterRange.of(0.05F, 0.45F);
    private static final ParameterRange NOT_VERY_ERODED = ParameterRange.of(0.45F, 0.55F);
    private static final ParameterRange MIN_EROSION = ParameterRange.of(0.55F, 1.0F);

    private static final ParameterRange DEFAULT_DEPTH = MultiNoiseUtil.ParameterRange.of(0.2F, 0.9F);

    private static final ParameterRange WEIRDNESS_L_MOUNTAINS = MultiNoiseUtil.ParameterRange.of(-0.7666667F, -0.56666666F);
    private static final ParameterRange WEIRDNESS_L_PLAINS = MultiNoiseUtil.ParameterRange.of(-0.56666666F, -0.4F);
    private static final ParameterRange WEIRDNESS_L_MIXED = MultiNoiseUtil.ParameterRange.of(-0.4F, -0.26666668F);
    private static final ParameterRange WEIRDNESS_L_ADJ_RIVER = MultiNoiseUtil.ParameterRange.of(-0.26666668F, -0.05F);
    private static final ParameterRange WEIRDNESS_RIVER = MultiNoiseUtil.ParameterRange.of(-0.05F, 0.05F);
    private static final ParameterRange WEIRDNESS_H_ADJ_RIVER = MultiNoiseUtil.ParameterRange.of(0.05F, 0.26666668F);
    private static final ParameterRange WEIRDNESS_H_MIXED = MultiNoiseUtil.ParameterRange.of(0.26666668F, 0.4F);
    private static final ParameterRange WEIRDNESS_H_PLAINS = MultiNoiseUtil.ParameterRange.of(0.4F, 0.56666666F);
    private static final ParameterRange WEIRDNESS_H_MOUNTAINS = MultiNoiseUtil.ParameterRange.of(0.56666666F, 0.7666667F);

    public static final MultiNoiseBiomeSource.Preset MOON = new MultiNoiseBiomeSource.Preset(Constant.id("moon"), (preset, biomeRegistry) -> new MultiNoiseBiomeSource(new MultiNoiseUtil.Entries<>(
            ImmutableList.of(
                    // HIGHLANDS

                    Pair.of(MultiNoiseUtil.createNoiseHypercube(
                            ZERO, //temperature
                            ZERO, //humidity
                            ParameterRange.combine(SHORE_CONTINENTALNESS, MID_INLAND_CONTINENTALNESS), //continentalness
                            MIN_EROSION, //erosion
                            DEFAULT_DEPTH, // depth
                            WEIRDNESS_H_MIXED, // weirdness
                            0L
                    ), () -> biomeRegistry.getOrThrow(GalacticraftBiome.Moon.HIGHLANDS)),
                    Pair.of(MultiNoiseUtil.createNoiseHypercube(
                            ZERO,
                            ZERO,
                            ParameterRange.combine(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS),
                            MIN_EROSION,
                            DEFAULT_DEPTH,
                            WEIRDNESS_H_PLAINS,
                            0L
                    ), () -> biomeRegistry.getOrThrow(GalacticraftBiome.Moon.HIGHLANDS_FLAT)),
                    Pair.of(MultiNoiseUtil.createNoiseHypercube(
                            ZERO,
                            ZERO,
                            ParameterRange.combine(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS),
                            MIN_EROSION,
                            DEFAULT_DEPTH,
                            WEIRDNESS_H_MOUNTAINS,
                            0L
                    ), () -> biomeRegistry.getOrThrow(GalacticraftBiome.Moon.HIGHLANDS_HILLS)),
                    Pair.of(MultiNoiseUtil.createNoiseHypercube(
                            ZERO,
                            ZERO,
                            ParameterRange.combine(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS),
                            MIN_EROSION,
                            DEFAULT_DEPTH,
                            WEIRDNESS_H_ADJ_RIVER,
                            0L
                    ), () -> biomeRegistry.getOrThrow(GalacticraftBiome.Moon.HIGHLANDS_VALLEY)),
                    Pair.of(MultiNoiseUtil.createNoiseHypercube(
                            ZERO,
                            ZERO,
                            RIVER_CONTINENTALNESS,
                            MIN_EROSION,
                            DEFAULT_DEPTH,
                            WEIRDNESS_RIVER,
                            0L
                    ), () -> biomeRegistry.getOrThrow(GalacticraftBiome.Moon.HIGHLANDS_EDGE)),

                    // MARE

                    Pair.of(MultiNoiseUtil.createNoiseHypercube(
                            ZERO,
                            ZERO,
                            ParameterRange.combine(SHORE_CONTINENTALNESS, MID_INLAND_CONTINENTALNESS),
                            MIN_EROSION,
                            DEFAULT_DEPTH,
                            WEIRDNESS_L_MIXED,
                            0L
                    ), () -> biomeRegistry.getOrThrow(GalacticraftBiome.Moon.MARE)),
                    Pair.of(MultiNoiseUtil.createNoiseHypercube(
                            ZERO,
                            ZERO,
                            ParameterRange.combine(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS),
                            MIN_EROSION,
                            DEFAULT_DEPTH,
                            WEIRDNESS_L_PLAINS,
                            0L
                    ), () -> biomeRegistry.getOrThrow(GalacticraftBiome.Moon.MARE_FLAT)),
                    Pair.of(MultiNoiseUtil.createNoiseHypercube(
                            ZERO,
                            ZERO,
                            ParameterRange.combine(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS),
                            MIN_EROSION,
                            DEFAULT_DEPTH,
                            WEIRDNESS_L_MOUNTAINS,
                            0L
                    ), () -> biomeRegistry.getOrThrow(GalacticraftBiome.Moon.MARE_HILLS)),
                    Pair.of(MultiNoiseUtil.createNoiseHypercube(
                            ZERO,
                            ZERO,
                            ParameterRange.combine(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS),
                            MIN_EROSION,
                            DEFAULT_DEPTH,
                            WEIRDNESS_L_ADJ_RIVER,
                            0L
                    ), () -> biomeRegistry.getOrThrow(GalacticraftBiome.Moon.MARE_VALLEY)),
                    Pair.of(MultiNoiseUtil.createNoiseHypercube(
                            ZERO,
                            ZERO,
                            RIVER_CONTINENTALNESS,
                            MIN_EROSION,
                            DEFAULT_DEPTH,
                            WEIRDNESS_RIVER,
                            0L
                    ), () -> biomeRegistry.getOrThrow(GalacticraftBiome.Moon.MARE_EDGE))
            )), Optional.of(Pair.of(biomeRegistry, preset))));
}

