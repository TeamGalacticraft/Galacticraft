package com.hrznstudio.galacticraft.world.gen.feature.moonvillage;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.FeatureConfig;

public class MoonVillageFeatureConfig implements FeatureConfig {
    public final Identifier startPool;
    public final int size;

    public MoonVillageFeatureConfig(String startPool, int size) {
        this.startPool = new Identifier(startPool);
        this.size = size;
    }

    public static <T> MoonVillageFeatureConfig deserialize(Dynamic<T> dynamic) {
        String string = dynamic.get("start_pool").asString("");
        int i = dynamic.get("size").asInt(6);
        return new MoonVillageFeatureConfig(string, i);
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("start_pool"), ops.createString(this.startPool.toString()), ops.createString("size"), ops.createInt(this.size))));
    }
}
