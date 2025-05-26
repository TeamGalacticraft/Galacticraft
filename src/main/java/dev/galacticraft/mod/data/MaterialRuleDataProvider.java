package dev.galacticraft.mod.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.gen.ShiftedScaledNoiseThresholdConditionSource;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.function.Function;

public class MaterialRuleDataProvider {
    public static final Codec<SurfaceRules.ConditionSource> CODEC = BuiltInRegistries.MATERIAL_CONDITION
            .byNameCodec()
            .dispatch(conditionSource -> conditionSource.codec().codec(), Function.identity());

    public static final ResourceKey<MapCodec<? extends SurfaceRules.ConditionSource>> SHIFTED_SCALED_NOISE_CONDITION =
            ResourceKey.create(Registries.MATERIAL_CONDITION, Constant.id("shifted_scaled_noise_threshold"));

    public static MapCodec<? extends SurfaceRules.ConditionSource> bootstrap(Registry<MapCodec<? extends SurfaceRules.ConditionSource>> registry) {
        return Registry.register(registry, "shifted_scaled_noise_threshold", ShiftedScaledNoiseThresholdConditionSource.CODEC.codec());
    }

    public static void register() {
        Registry.register(
                BuiltInRegistries.MATERIAL_CONDITION,
                MaterialRuleDataProvider.SHIFTED_SCALED_NOISE_CONDITION.location(),
                ShiftedScaledNoiseThresholdConditionSource.CODEC.codec()
        );
    }
}
