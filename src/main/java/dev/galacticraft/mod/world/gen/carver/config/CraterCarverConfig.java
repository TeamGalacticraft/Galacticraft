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

package dev.galacticraft.mod.world.gen.carver.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.tag.GCTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class CraterCarverConfig extends CarverConfiguration {
    public static final Codec<CraterCarverConfig> CRATER_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("probability").forGetter(i -> i.probability),
            HeightProvider.CODEC.fieldOf("y").forGetter(i -> i.y),
            FloatProvider.CODEC.fieldOf("y_scale").forGetter(i -> i.yScale),
            CarverDebugSettings.CODEC.fieldOf("debug_settings").forGetter(i -> i.debugSettings),
            Codec.INT.fieldOf("max_radius").forGetter(i -> i.maxRadius),
            Codec.INT.fieldOf("min_radius").forGetter(i -> i.minRadius),
            Codec.INT.fieldOf("ideal_range_offset").forGetter(i -> i.idealRangeOffset)
    ).apply(instance, CraterCarverConfig::new));

    public final int maxRadius;
    public final int minRadius;
    public final int idealRangeOffset;

    public CraterCarverConfig(float probability, HeightProvider y, FloatProvider yScale, CarverDebugSettings carverDebugConfig, int maxRadius, int minRadius, int idealRangeOffset) {
        super(probability, y, yScale, VerticalAnchor.absolute(-64), carverDebugConfig, BuiltInRegistries.BLOCK.getOrCreateTag(GCTags.MOON_CRATER_CARVER_REPLACEABLES)); // TODO: Crater replaceables
        this.maxRadius = maxRadius;
        this.minRadius = minRadius;
        this.idealRangeOffset = idealRangeOffset;
    }
}
