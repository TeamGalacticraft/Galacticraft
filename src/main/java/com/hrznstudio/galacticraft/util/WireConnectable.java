package com.hrznstudio.galacticraft.util;

import com.hrznstudio.galacticraft.blocks.special.aluminumwire.WireConnectionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public interface WireConnectable {

    default WireConnectionType canWireConnect(IWorld world, Direction opposite, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        return WireConnectionType.NONE;
    }

}