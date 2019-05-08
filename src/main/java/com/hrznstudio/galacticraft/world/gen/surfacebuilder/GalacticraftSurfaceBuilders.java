package com.hrznstudio.galacticraft.world.gen.surfacebuilder;

import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftSurfaceBuilders {

    public static final TernarySurfaceConfig MOON_CONFIG = new TernarySurfaceConfig(GalacticraftBlocks.MOON_TURF.getDefaultState(), GalacticraftBlocks.MOON_DIRT.getDefaultState(), Blocks.AIR.getDefaultState());
    public static final TernarySurfaceConfig MARS_CONFIG = new TernarySurfaceConfig(GalacticraftBlocks.MARS_SURFACE_ROCK.getDefaultState(), GalacticraftBlocks.MARS_SURFACE_ROCK.getDefaultState(), Blocks.AIR.getDefaultState());

    public static void init() {
    }
}
