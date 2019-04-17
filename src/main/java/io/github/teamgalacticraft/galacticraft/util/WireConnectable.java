package io.github.teamgalacticraft.galacticraft.blocks.machines;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public interface WireConnectable {
    default boolean canWireConnect(IWorld world, Direction opposite, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        return true;
    }
}