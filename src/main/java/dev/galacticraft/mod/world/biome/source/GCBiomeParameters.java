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

package dev.galacticraft.mod.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.biome.GCBiomeKey;
import java.util.function.Consumer;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.Parameter;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GCBiomeParameters {
    private static final Parameter DEFAULT = Parameter.span(-1.0F, 1.0F);

    private static final Parameter RIVER_CONTINENTALNESS = Parameter.span(-0.11F, 0.55F);
    private static final Parameter SHORE_CONTINENTALNESS = Parameter.span(-0.19F, -0.11F);
    private static final Parameter NEAR_INLAND_CONTINENTALNESS = Parameter.span(-0.11F, 0.03F);
    private static final Parameter MID_INLAND_CONTINENTALNESS = Parameter.span(0.03F, 0.3F);
    private static final Parameter FAR_INLAND_CONTINENTALNESS = Parameter.span(0.3F, 1.0F);

    private static final Parameter DRY = Parameter.span(-1.0F, -0.35F);
    private static final Parameter SOMEWHAT_DRY = Parameter.span(-0.35F, -0.1F);
    private static final Parameter DEFAULT_HUMIDITY = Parameter.span(-0.1F, 0.1F);
    private static final Parameter SOMEWHAT_HUMID = Parameter.span(0.1F, 0.3F);
    private static final Parameter HUMID = Parameter.span(0.3F, 1.0F);

    private static final Parameter COLD = Parameter.span(-1.0F, -0.45F);
    private static final Parameter COOL = Parameter.span(-0.45F, -0.15F);
    private static final Parameter DEFAULT_TEMPERATURE = Parameter.span(-0.15F, 0.2F);
    private static final Parameter WARM = Parameter.span(0.2F, 0.55F);
    private static final Parameter HOT = Parameter.span(0.55F, 1.0F);


    private static final Parameter MAX_EROSION = Parameter.span(-1.0F, -0.78F); //todo better names
    private static final Parameter ERODED = Parameter.span(-0.78F, -0.375F);
    private static final Parameter MOSTLY_ERODED = Parameter.span(-0.375F, -0.2225F);
    private static final Parameter MED_EROSION = Parameter.span(-0.2225F, 0.05F);
    private static final Parameter SOMEWHAT_ERODED = Parameter.span(0.05F, 0.45F);
    private static final Parameter NOT_VERY_ERODED = Parameter.span(0.45F, 0.55F);
    private static final Parameter MIN_EROSION = Parameter.span(0.55F, 1.0F);

    private static final Parameter CAVE_DEPTH = Parameter.span(0.2F, 0.9F);

    private static final Parameter WEIRDNESS_L_MOUNTAINS = Parameter.span(-0.7666667F, -0.56666666F);
    private static final Parameter WEIRDNESS_L_PLAINS = Parameter.span(-0.56666666F, -0.4F);
    private static final Parameter WEIRDNESS_L_MIXED = Parameter.span(-0.4F, -0.26666668F);
    private static final Parameter WEIRDNESS_L_ADJ_RIVER = Parameter.span(-0.26666668F, -0.05F);
    private static final Parameter WEIRDNESS_RIVER = Parameter.span(-0.05F, 0.05F);
    private static final Parameter WEIRDNESS_H_ADJ_RIVER = Parameter.span(0.05F, 0.26666668F);
    private static final Parameter WEIRDNESS_H_MIXED = Parameter.span(0.26666668F, 0.4F);
    private static final Parameter WEIRDNESS_H_PLAINS = Parameter.span(0.4F, 0.56666666F);
    private static final Parameter WEIRDNESS_H_MOUNTAINS = Parameter.span(0.56666666F, 0.7666667F);

    public static final MultiNoiseBiomeSource.Preset MOON = new MultiNoiseBiomeSource.Preset(Constant.id("moon"), (biomeRegistry) -> {
        ImmutableList.Builder<Pair<Climate.ParameterPoint, Holder<Biome>>> builder = ImmutableList.builder();
        // HIGHLANDS
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                Parameter.span(SHORE_CONTINENTALNESS, MID_INLAND_CONTINENTALNESS), //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_H_MIXED, // weirdness
                0.0F,
                biomeRegistry.getHolder(GCBiomeKey.Moon.HIGHLANDS).orElseThrow());
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                Parameter.span(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS), //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_H_PLAINS, // weirdness
                0.0F,
                biomeRegistry.getHolder(GCBiomeKey.Moon.HIGHLANDS_FLAT).orElseThrow());
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                Parameter.span(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS), //continentalness
                SOMEWHAT_ERODED, //erosion
                WEIRDNESS_H_MOUNTAINS, // weirdness
                0.0F,
                biomeRegistry.getHolder(GCBiomeKey.Moon.HIGHLANDS_HILLS).orElseThrow());
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                Parameter.span(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS), //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_H_ADJ_RIVER, // weirdness
                0.0F,
                biomeRegistry.getHolder(GCBiomeKey.Moon.HIGHLANDS_VALLEY).orElseThrow());
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                RIVER_CONTINENTALNESS, //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_RIVER, // weirdness
                0.0F,
                biomeRegistry.getHolder(GCBiomeKey.Moon.HIGHLANDS_EDGE).orElseThrow());

        // MARE
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                Parameter.span(SHORE_CONTINENTALNESS, MID_INLAND_CONTINENTALNESS), //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_L_MIXED, // weirdness
                0.0F,
                biomeRegistry.getHolder(GCBiomeKey.Moon.MARE).orElseThrow());
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                Parameter.span(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS), //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_L_PLAINS, // weirdness
                0.0F,
                biomeRegistry.getHolder(GCBiomeKey.Moon.MARE_FLAT).orElseThrow());
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                Parameter.span(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS), //continentalness
                SOMEWHAT_ERODED, //erosion
                WEIRDNESS_L_MOUNTAINS, // weirdness
                0.0F,
                biomeRegistry.getHolder(GCBiomeKey.Moon.MARE_HILLS).orElseThrow());
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                Parameter.span(NEAR_INLAND_CONTINENTALNESS, FAR_INLAND_CONTINENTALNESS), //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_L_ADJ_RIVER, // weirdness
                0.0F,
                biomeRegistry.getHolder(GCBiomeKey.Moon.MARE_VALLEY).orElseThrow());
        writeBiomeParameters(builder::add,
                COLD, //temperature
                DRY, //humidity
                RIVER_CONTINENTALNESS, //continentalness
                MIN_EROSION, //erosion
                WEIRDNESS_RIVER, // weirdness
                0.0F,
                biomeRegistry.getHolder(GCBiomeKey.Moon.MARE_EDGE).orElseThrow());
        return new Climate.ParameterList<>(builder.build());
    });

    private static void writeBiomeParameters(
            Consumer<Pair<Climate.ParameterPoint, Holder<Biome>>> parameters,
            Parameter temperature,
            Parameter humidity,
            Parameter continentalness,
            Parameter erosion,
            Parameter weirdness,
            float offset,
            Holder<Biome> biome
    ) {
        parameters.accept(
                Pair.of(
                        Climate.parameters(temperature, humidity, continentalness, erosion, Parameter.point(0.0F), weirdness, offset),
                        biome
                )
        );
        parameters.accept(
                Pair.of(
                        Climate.parameters(temperature, humidity, continentalness, erosion, Parameter.point(1.0F), weirdness, offset),
                        biome
                )
        );
    }

    public static void register() {}
}

