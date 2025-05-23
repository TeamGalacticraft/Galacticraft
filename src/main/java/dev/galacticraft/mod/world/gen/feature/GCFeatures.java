package dev.galacticraft.mod.world.gen.feature;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.gen.feature.features.OlivineBeamFeature;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GCFeatures {
    public static final Feature<NoneFeatureConfiguration> OLIVINE_BEAM =
            Registry.register(BuiltInRegistries.FEATURE, Constant.id("olivine_beam"),
                    new OlivineBeamFeature(NoneFeatureConfiguration.CODEC));
}
