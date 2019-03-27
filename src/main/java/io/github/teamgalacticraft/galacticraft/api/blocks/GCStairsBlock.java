package io.github.teamgalacticraft.galacticraft.api.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

/**
 * Because Mojang is stupid and set the constructor of GCStairsBlock to protected
 *
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GCStairsBlock extends StairsBlock {
    public GCStairsBlock(BlockState state, Settings settings) {
        super(state, settings);
    }
}
