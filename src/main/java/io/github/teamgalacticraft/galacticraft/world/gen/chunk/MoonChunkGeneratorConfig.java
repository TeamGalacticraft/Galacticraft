package io.github.teamgalacticraft.galacticraft.world.gen.chunk;

import io.github.teamgalacticraft.galacticraft.world.gen.decorator.CraterDecoratorConfig;
import io.github.teamgalacticraft.galacticraft.world.gen.decorator.GalacticraftDecorators;
import io.github.teamgalacticraft.galacticraft.world.gen.feature.CraterFeatureConfig;
import io.github.teamgalacticraft.galacticraft.world.gen.feature.GalacticraftFeatures;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class MoonChunkGeneratorConfig extends OverworldChunkGeneratorConfig {

    public static final ConfiguredFeature<?> CRATER = Biome.configureFeature(GalacticraftFeatures.CRATER_FEATURE, new CraterFeatureConfig(), GalacticraftDecorators.CRATER, new CraterDecoratorConfig(4));

    MoonChunkGeneratorConfig() {
    }

    public int getBiomeSize() {
        return 0;
    }

    public int getRiverSize() {
        return 0;
    }

    public int getForcedBiome() {
        return -1;
    }

    public int getMinY() {
        return 0;
    }

}
