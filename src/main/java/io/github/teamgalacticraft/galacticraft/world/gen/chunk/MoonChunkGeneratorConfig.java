package io.github.teamgalacticraft.galacticraft.world.gen.chunk;

import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class MoonChunkGeneratorConfig extends OverworldChunkGeneratorConfig {

    public MoonChunkGeneratorConfig() {
    }

    public int getBiomeSize() {
        return 4;
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
