package com.hrznstudio.galacticraft.api.pipe;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PipeConnectable {
    @NotNull PipeConnectionType canPipeConnect(WorldAccess world, Direction opposite, BlockPos connectionSourcePos, BlockPos connectionTargetPos);
}
