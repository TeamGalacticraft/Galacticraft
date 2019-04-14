package io.github.teamgalacticraft.galacticraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FeatureConfig;

public class CraterFeatureConfig implements FeatureConfig {

    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps_1) {
        return new Dynamic<>(dynamicOps_1, dynamicOps_1.createMap(ImmutableMap.of(dynamicOps_1.createString("state"), BlockState.serialize(dynamicOps_1, Blocks.AIR.getDefaultState()).getValue())));
    }

    public static <T> CraterFeatureConfig deserialize(Dynamic<T> dynamic_1) {
        return new CraterFeatureConfig();
    }
}
