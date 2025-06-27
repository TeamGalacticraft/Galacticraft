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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.mixin.SurfaceRulesAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public record ShiftedScaledNoiseThresholdConditionSource(
        ResourceKey<NormalNoise.NoiseParameters> noise,
        double xzScale,
        double minThreshold,
        double maxThreshold
) implements SurfaceRules.ConditionSource {
    public static final KeyDispatchDataCodec<ShiftedScaledNoiseThresholdConditionSource> CODEC =
            KeyDispatchDataCodec.of(RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(s -> s.noise),
                    Codec.DOUBLE.fieldOf("xz_scale").forGetter(s -> s.xzScale),
                    Codec.DOUBLE.fieldOf("min_threshold").forGetter(s -> s.minThreshold),
                    Codec.DOUBLE.fieldOf("max_threshold").forGetter(s -> s.maxThreshold)
            ).apply(instance, ShiftedScaledNoiseThresholdConditionSource::new)));

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
        return CODEC;
    }

    @Override
    public SurfaceRules.Condition apply(SurfaceRules.Context context) {
        RandomState randomState = ((SurfaceRulesAccessor) (Object) context).getRandomState();
        NormalNoise cachedNoise = randomState.getOrCreateNoise(noise);
        return () -> {
            double x = ((SurfaceRulesAccessor) (Object) context).getBlockX() * xzScale;
            double z = ((SurfaceRulesAccessor) (Object) context).getBlockZ() * xzScale;
            double value = cachedNoise.getValue(x, 0.0, z);
            return value >= minThreshold && value <= maxThreshold;
        };
    }
}