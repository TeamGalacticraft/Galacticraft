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
import dev.galacticraft.mod.world.biome.GalacticraftBiomeKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.MultiNoiseUtil.ParameterRange;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftBiomeParameters {
    private static final ParameterRange DEFAULT = ParameterRange.of(-1.0F, 1.0F);

    private static final ParameterRange RIVER_CONTINENTALNESS = ParameterRange.of(-0.11F, 0.55F);
    private static final ParameterRange SHORE_CONTINENTALNESS = ParameterRange.of(-0.19F, -0.11F);
    private static final ParameterRange NEAR_INLAND_CONTINENTALNESS = ParameterRange.of(-0.11F, 0.03F);
    private static final ParameterRange MID_INLAND_CONTINENTALNESS = ParameterRange.of(0.03F, 0.3F);
    private static final ParameterRange FAR_INLAND_CONTINENTALNESS = ParameterRange.of(0.3F, 1.0F);

    private static final ParameterRange DRY = ParameterRange.of(-1.0F, -0.35F);
    private static final ParameterRange SOMEWHAT_DRY = ParameterRange.of(-0.35F, -0.1F);
    private static final ParameterRange DEFAULT_HUMIDITY = ParameterRange.of(-0.1F, 0.1F);
    private static final ParameterRange SOMEWHAT_HUMID = ParameterRange.of(0.1F, 0.3F);
    private static final ParameterRange HUMID = ParameterRange.of(0.3F, 1.0F);

    private static final ParameterRange COLD = ParameterRange.of(-1.0F, -0.45F);
    private static final ParameterRange COOL = ParameterRange.of(-0.45F, -0.15F);
    private static final ParameterRange DEFAULT_TEMPERATURE = ParameterRange.of(-0.15F, 0.2F);
    private static final ParameterRange WARM = ParameterRange.of(0.2F, 0.55F);
    private static final ParameterRange HOT = ParameterRange.of(0.55F, 1.0F);


    private static final ParameterRange MAX_EROSION = ParameterRange.of(-1.0F, -0.78F); //todo better names
    private static final ParameterRange ERODED = ParameterRange.of(-0.78F, -0.375F);
    private static final ParameterRange MOSTLY_ERODED = ParameterRange.of(-0.375F, -0.2225F);
    private static final ParameterRange MED_EROSION = ParameterRange.of(-0.2225F, 0.05F);
    private static final ParameterRange SOMEWHAT_ERODED = ParameterRange.of(0.05F, 0.45F);
    private static final ParameterRange NOT_VERY_ERODED = ParameterRange.of(0.45F, 0.55F);
    private static final ParameterRange MIN_EROSION = ParameterRange.of(0.55F, 1.0F);

    private static final ParameterRange CAVE_DEPTH = ParameterRange.of(0.2F, 0.9F);

    private static final ParameterRange WEIRDNESS_L_MOUNTAINS = ParameterRange.of(-0.7666667F, -0.56666666F);
    private static final ParameterRange WEIRDNESS_L_PLAINS = ParameterRange.of(-0.56666666F, -0.4F);
    private static final ParameterRange WEIRDNESS_L_MIXED = ParameterRange.of(-0.4F, -0.26666668F);
    private static final ParameterRange WEIRDNESS_L_ADJ_RIVER = ParameterRange.of(-0.26666668F, -0.05F);
    private static final ParameterRange WEIRDNESS_RIVER = ParameterRange.of(-0.05F, 0.05F);
    private static final ParameterRange WEIRDNESS_H_ADJ_RIVER = ParameterRange.of(0.05F, 0.26666668F);
    private static final ParameterRange WEIRDNESS_H_MIXED = ParameterRange.of(0.26666668F, 0.4F);
    private static final ParameterRange WEIRDNESS_H_PLAINS = ParameterRange.of(0.4F, 0.56666666F);
    private static final ParameterRange WEIRDNESS_H_MOUNTAINS = ParameterRange.of(0.56666666F, 0.7666667F);

    public static final MultiNoiseBiomeSource.Preset MOON = new MultiNoiseBiomeSource.Preset(Constant.id("moon"), (biomeRegistry) -> {
        ImmutableList.Builder<Pair<MultiNoiseUtil.NoiseHypercube, Supplier<Biome>>> builder = ImmutableList.builder();
        // HIGHLANDS
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                ParameterRange.combine(SHORE_CONTINENTALNESS, MID_INLAND_CONTINENTALNESS), //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_H_MIXED, // weirdness
                0.0F,
                () -> biomeRegistry.getOrThrow(GalacticraftBiomeKey.Moon.HIGHLANDS));
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                ParameterRange.combine(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS), //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_H_PLAINS, // weirdness
                0.0F,
                () -> biomeRegistry.getOrThrow(GalacticraftBiomeKey.Moon.HIGHLANDS_FLAT));
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                ParameterRange.combine(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS), //continentalness
                SOMEWHAT_ERODED, //erosion
                WEIRDNESS_H_MOUNTAINS, // weirdness
                0.0F,
                () -> biomeRegistry.getOrThrow(GalacticraftBiomeKey.Moon.HIGHLANDS_HILLS));
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                ParameterRange.combine(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS), //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_H_ADJ_RIVER, // weirdness
                0.0F,
                () -> biomeRegistry.getOrThrow(GalacticraftBiomeKey.Moon.HIGHLANDS_VALLEY));
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                RIVER_CONTINENTALNESS, //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_RIVER, // weirdness
                0.0F,
                () -> biomeRegistry.getOrThrow(GalacticraftBiomeKey.Moon.HIGHLANDS_EDGE));

        // MARE
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                ParameterRange.combine(SHORE_CONTINENTALNESS, MID_INLAND_CONTINENTALNESS), //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_L_MIXED, // weirdness
                0.0F,
                () -> biomeRegistry.getOrThrow(GalacticraftBiomeKey.Moon.MARE));
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                ParameterRange.combine(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS), //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_L_PLAINS, // weirdness
                0.0F,
                () -> biomeRegistry.getOrThrow(GalacticraftBiomeKey.Moon.MARE_FLAT));
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                ParameterRange.combine(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS), //continentalness
                SOMEWHAT_ERODED, //erosion
                WEIRDNESS_L_MOUNTAINS, // weirdness
                0.0F,
                () -> biomeRegistry.getOrThrow(GalacticraftBiomeKey.Moon.MARE_HILLS));
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                ParameterRange.combine(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS), //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_L_ADJ_RIVER, // weirdness
                0.0F,
                () -> biomeRegistry.getOrThrow(GalacticraftBiomeKey.Moon.MARE_VALLEY));
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                RIVER_CONTINENTALNESS, //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_RIVER, // weirdness
                0.0F,
                () -> biomeRegistry.getOrThrow(GalacticraftBiomeKey.Moon.MARE_EDGE));
        return new MultiNoiseUtil.Entries<>(builder.build());
    });

    private static void writeBiomeParameters(
            Consumer<Pair<MultiNoiseUtil.NoiseHypercube, Supplier<Biome>>> parameters,
            ParameterRange temperature,
            ParameterRange humidity,
            ParameterRange continentalness,
            ParameterRange erosion,
            ParameterRange weirdness,
            float offset,
            Supplier<Biome> biome
    ) {
        parameters.accept(
                Pair.of(
                        MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, ParameterRange.of(0.0F), weirdness, offset),
                        biome
                )
        );
        parameters.accept(
                Pair.of(
                        MultiNoiseUtil.createNoiseHypercube(temperature, humidity, continentalness, erosion, ParameterRange.of(1.0F), weirdness, offset),
                        biome
                )
        );
    }
}

