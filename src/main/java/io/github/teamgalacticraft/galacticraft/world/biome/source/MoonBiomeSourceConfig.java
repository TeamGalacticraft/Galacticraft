package io.github.teamgalacticraft.galacticraft.world.biome.source;

import io.github.teamgalacticraft.galacticraft.world.gen.chunk.MoonChunkGeneratorConfig;
import net.minecraft.world.biome.source.BiomeSourceConfig;
import net.minecraft.world.level.LevelProperties;

public class MoonBiomeSourceConfig implements BiomeSourceConfig {

    private LevelProperties levelProperties;
    private MoonChunkGeneratorConfig generatorSettings;

    public LevelProperties getLevelProperties() {
        return this.levelProperties;
    }

    public MoonBiomeSourceConfig setLevelProperties(LevelProperties levelProperties_1) {
        this.levelProperties = levelProperties_1;
        return this;
    }

    public MoonChunkGeneratorConfig getGeneratorSettings() {
        return this.generatorSettings;
    }

    public MoonBiomeSourceConfig setGeneratorSettings(MoonChunkGeneratorConfig overworldChunkGeneratorConfig_1) {
        this.generatorSettings = overworldChunkGeneratorConfig_1;
        return this;
    }

}
