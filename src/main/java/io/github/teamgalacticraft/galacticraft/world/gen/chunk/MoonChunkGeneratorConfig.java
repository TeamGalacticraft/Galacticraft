package io.github.teamgalacticraft.galacticraft.world.gen.chunk;

import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class MoonChunkGeneratorConfig extends OverworldChunkGeneratorConfig {

    MoonChunkGeneratorConfig() {
        this.setDefaultBlock(GalacticraftBlocks.MOON_ROCK_BLOCK.getDefaultState());
        this.setDefaultFluid(Blocks.AIR.getDefaultState());
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
