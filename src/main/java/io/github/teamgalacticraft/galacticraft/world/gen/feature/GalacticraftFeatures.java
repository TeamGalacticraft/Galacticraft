package io.github.teamgalacticraft.galacticraft.world.gen.feature;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;

public class GalacticraftFeatures {

    public static final Feature<CraterFeatureConfig> CRATER_FEATURE = Registry.register(Registry.FEATURE, "crater", new CraterFeature(CraterFeatureConfig::deserialize));

    public static void init() {

    }
}
