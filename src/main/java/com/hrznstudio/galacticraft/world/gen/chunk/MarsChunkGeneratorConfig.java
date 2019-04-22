package com.hrznstudio.galacticraft.world.gen.chunk;

import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MarsChunkGeneratorConfig extends OverworldChunkGeneratorConfig {

    MarsChunkGeneratorConfig() {
    }

    public int getBiomeSize() {
        return 0;
    }

    public int getRiverSize() {
        return 2;
    }

    public int getForcedBiome() {
        return -1;
    }

    public int getMinY() {
        return 0;
    }

}
