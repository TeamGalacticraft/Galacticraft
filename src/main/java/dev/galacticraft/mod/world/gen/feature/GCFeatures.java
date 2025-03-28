package dev.galacticraft.mod.world.gen.feature;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;

import static dev.galacticraft.mod.world.gen.feature.GCConfiguredFeature.METEOR_FEATURE;

public class GCFeatures {
    public static void register() {
        register("meteor", METEOR_FEATURE);
    }

    private static void register(String id, Feature feature) {
        Registry.register(BuiltInRegistries.FEATURE, Constant.id(id), feature);
    }
}
