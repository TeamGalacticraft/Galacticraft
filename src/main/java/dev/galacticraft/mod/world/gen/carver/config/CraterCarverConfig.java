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

package dev.galacticraft.mod.world.gen.carver.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CarverDebugConfig;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public class CraterCarverConfig extends CarverConfig {
    public static final Codec<CraterCarverConfig> CRATER_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("probability").forGetter(i -> i.probability),
            HeightProvider.CODEC.fieldOf("y").forGetter(i -> i.y),
            FloatProvider.VALUE_CODEC.fieldOf("y_scale").forGetter(i -> i.yScale),
            CarverDebugConfig.CODEC.fieldOf("debug_settings").forGetter(i -> i.debugConfig),
            Codec.INT.fieldOf("max_radius").forGetter(i -> i.maxRadius),
            Codec.INT.fieldOf("min_radius").forGetter(i -> i.minRadius),
            Codec.INT.fieldOf("ideal_range_offset").forGetter(i -> i.idealRangeOffset)
    ).apply(instance, CraterCarverConfig::new));

    public final int maxRadius;
    public final int minRadius;
    public final int idealRangeOffset;

    public CraterCarverConfig(float probability, HeightProvider y, FloatProvider yScale, CarverDebugConfig debugConfig, int maxRadius, int minRadius, int idealRangeOffset) {
        super(probability, y, yScale, YOffset.fixed(0), false, debugConfig);
        this.maxRadius = maxRadius;
        this.minRadius = minRadius;
        this.idealRangeOffset = idealRangeOffset;
    }
}
