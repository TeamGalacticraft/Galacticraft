package io.github.teamgalacticraft.galacticraft.world.gen.decorator;

import io.github.teamgalacticraft.galacticraft.world.gen.feature.CraterFeatureConfig;
import io.github.teamgalacticraft.galacticraft.world.gen.feature.GalacticraftFeatures;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftDecorators {
    public static final Decorator<CraterDecoratorConfig> CRATER = Registry.register(Registry.DECORATOR, "water_lake", new CraterDecorator(CraterDecoratorConfig::deserialize));
    public static final ConfiguredFeature<?> CRATER_CONF = Biome.configureFeature(GalacticraftFeatures.CRATER_FEATURE, new CraterFeatureConfig(), GalacticraftDecorators.CRATER, new CraterDecoratorConfig(2));

    public static void init() {
    }
}
