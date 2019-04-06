package io.github.teamgalacticraft.galacticraft.world.gen.surfacebuilder;

import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GCSurfaceBuilder {

    public static final TernarySurfaceConfig MOON_CONFIG = new TernarySurfaceConfig(GalacticraftBlocks.MOON_TURF_BLOCK.getDefaultState(), GalacticraftBlocks.MOON_DIRT_BLOCK.getDefaultState(), Blocks.AIR.getDefaultState());
}
