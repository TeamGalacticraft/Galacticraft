package com.hrznstudio.galacticraft.world.biome.moon.highlands;

import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.biome.moon.MoonBiome;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public abstract class MoonHighlandsBiome extends MoonBiome {
    public static final TernarySurfaceConfig MOON_HIGHLANDS_BIOME_CONFIG = new TernarySurfaceConfig(GalacticraftBlocks.MOON_TURF.getDefaultState(), GalacticraftBlocks.MOON_DIRT.getDefaultState(), GalacticraftBlocks.MOON_TURF.getDefaultState());

    public MoonHighlandsBiome(Settings settings) {
        super(settings);
    }

    @Override
    protected final String getCategoryName() {
        return "highlands";
    }

}
