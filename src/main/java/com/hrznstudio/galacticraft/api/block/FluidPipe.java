package com.hrznstudio.galacticraft.api.block;

import com.hrznstudio.galacticraft.api.pipe.PipeConnectable;
import com.hrznstudio.galacticraft.api.pipe.PipeConnectionType;
import com.hrznstudio.galacticraft.block.special.fluidpipe.FluidPipeBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidPipe extends Block implements BlockEntityProvider, PipeConnectable {
    public FluidPipe(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new FluidPipeBlockEntity();
    }

    @Override
    public @NotNull PipeConnectionType canPipeConnect(WorldAccess world, Direction opposite, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        return PipeConnectionType.PIPE;
    }
}
