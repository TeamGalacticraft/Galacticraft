package com.hrznstudio.galacticraft.world.biome.moon.mare;

import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.biome.moon.MoonBiome;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.BlockStateWithChance;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.MultiBlockSurfaceConfig;

public abstract class MoonMareBiome extends MoonBiome {
    public static final MultiBlockSurfaceConfig MOON_MARE_BIOME_CONFIG = new MultiBlockSurfaceConfig(
            new BlockStateWithChance[]{
                    new BlockStateWithChance(GalacticraftBlocks.MOON_BASALT.getDefaultState(), -1),
                    new BlockStateWithChance(GalacticraftBlocks.MOON_TURF.getDefaultState(), 100)}, //DISABLED for now. Need to find a good ratio
            new BlockStateWithChance[]{
                    new BlockStateWithChance(GalacticraftBlocks.MOON_BASALT.getDefaultState(), -1),
                    new BlockStateWithChance(GalacticraftBlocks.MOON_DIRT.getDefaultState(), 100)},
            new BlockStateWithChance[]{
                    new BlockStateWithChance(GalacticraftBlocks.MOON_BASALT.getDefaultState(), -1),
                    new BlockStateWithChance(GalacticraftBlocks.MOON_ROCK.getDefaultState(), 100)}
    );

    public MoonMareBiome(Settings settings) {
        super(settings);
    }

    @Override
    protected final String getCategoryName() {
        return "mare";
    }

}
