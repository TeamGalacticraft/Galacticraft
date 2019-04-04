package io.github.teamgalacticraft.galacticraft.world.biome.source;

import io.github.teamgalacticraft.galacticraft.world.gen.chunk.MoonChunkGeneratorConfig;
import net.minecraft.world.biome.source.BiomeSourceConfig;
import net.minecraft.world.level.LevelProperties;

public class MoonLayeredBiomeSourceConfig implements BiomeSourceConfig {
    private LevelProperties levelProperties;
    private MoonChunkGeneratorConfig generatorSettings;

    public MoonLayeredBiomeSourceConfig() {
    }

    public MoonLayeredBiomeSourceConfig setLevelProperties(LevelProperties levelProperties_1) {
        this.levelProperties = levelProperties_1;
        return this;
    }

    public MoonLayeredBiomeSourceConfig setGeneratorSettings(MoonChunkGeneratorConfig overworldChunkGeneratorConfig_1) {
        this.generatorSettings = overworldChunkGeneratorConfig_1;
        return this;
    }

    public LevelProperties getLevelProperties() {
        return this.levelProperties;
    }

    public MoonChunkGeneratorConfig getGeneratorSettings() {
        return this.generatorSettings;
    }
}
