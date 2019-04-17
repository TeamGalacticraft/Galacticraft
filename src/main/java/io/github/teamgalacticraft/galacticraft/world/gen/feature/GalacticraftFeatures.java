package io.github.teamgalacticraft.galacticraft.world.gen.feature;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftFeatures {

    public static final Feature<CraterFeatureConfig> CRATER_FEATURE = Registry.register(Registry.FEATURE, "crater", new CraterFeature(dynamic_1 -> CraterFeatureConfig.deserialize()));

    public static void init() {

    }
}
