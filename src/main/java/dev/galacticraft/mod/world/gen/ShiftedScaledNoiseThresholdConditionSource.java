package dev.galacticraft.mod.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.mixin.SurfaceRulesAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
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