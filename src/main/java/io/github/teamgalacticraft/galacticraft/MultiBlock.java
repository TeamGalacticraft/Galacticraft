package io.github.teamgalacticraft.galacticraft;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface MultiBlock {
    List<BlockPos> getOtherParts(BlockState state, BlockPos pos);
}