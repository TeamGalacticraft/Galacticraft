package io.github.teamgalacticraft.galacticraft.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * A part of Raphydaphy's MultiBlock API
 *
 * @author Raphydaphy
 */
public interface MultiBlock {
    List<BlockPos> getOtherParts(BlockState state, BlockPos pos);
}