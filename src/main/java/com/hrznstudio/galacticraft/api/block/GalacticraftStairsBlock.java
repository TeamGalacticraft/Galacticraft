package com.hrznstudio.galacticraft.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

/**
 * Because Mojang is stupid and set the constructor of StairsBlock to protected
 *
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftStairsBlock extends StairsBlock {
    public GalacticraftStairsBlock(BlockState state, Settings settings) {
        super(state, settings);
    }
}
