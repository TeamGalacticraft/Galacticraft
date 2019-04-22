package com.hrznstudio.galacticraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.decorator.DecoratorConfig;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CraterDecoratorConfig implements DecoratorConfig {
    public final int chance;

    public CraterDecoratorConfig(int int_1) {
        this.chance = int_1;
    }

    public static CraterDecoratorConfig deserialize(Dynamic<?> dynamic_1) {
        int int_1 = dynamic_1.get("chance").asInt(0);
        return new CraterDecoratorConfig(int_1);
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps_1) {
        return new Dynamic(dynamicOps_1, dynamicOps_1.createMap(ImmutableMap.of(dynamicOps_1.createString("chance"), dynamicOps_1.createInt(this.chance))));
    }
}
