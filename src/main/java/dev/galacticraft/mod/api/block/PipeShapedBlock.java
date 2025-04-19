package dev.galacticraft.mod.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PipeShapedBlock extends PipeBlock {
    protected PipeShapedBlock(float radius, Properties properties) {
        super(radius, properties);
    }

    abstract protected boolean canConnectTo(Level level, BlockPos thisPos, Direction direction, BlockPos neighborPos, BlockState thisState, BlockState neighborState);

    abstract protected void onConnectionChanged(Level level, BlockPos thisPos, Direction direction, BlockPos neighborPos, BlockState newState, BlockState neighborState);

    protected BlockState updateConnection(BlockState currentState, BlockPos pos, Direction side, BlockPos neighborPos, BlockState neighborState, Level level) {
        BooleanProperty directionProperty = PipeBlock.PROPERTY_BY_DIRECTION.get(side);
        return currentState.setValue(directionProperty, this.canConnectTo(level, pos, side, neighborPos, currentState, neighborState));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean notify) {
        super.neighborChanged(state, level, pos, block, neighborPos, notify);

        Direction direction = Direction.fromDelta(neighborPos.getX() - pos.getX(), neighborPos.getY() - pos.getY(), neighborPos.getZ() - pos.getZ());
        if (direction == null)
            return;

        BlockState neighborState = level.getBlockState(neighborPos);

        BlockState newState = this.updateConnection(state, pos, direction, neighborPos, neighborState, level);
        if (newState != state) {
            level.setBlockAndUpdate(pos, newState);
            this.onConnectionChanged(level, pos, direction, neighborPos, newState, neighborState);
        }
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        if (levelAccessor instanceof Level level) {
            return this.updateConnection(state, pos, direction, neighborPos, neighborState, level);
        }

        return state;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = this.defaultBlockState();

        for (Direction direction : Direction.values()) {
            Level level = ctx.getLevel();
            BlockPos neighborPos = ctx.getClickedPos().relative(direction);

            if (this.canConnectTo(level, ctx.getClickedPos(), direction, neighborPos, state, level.getBlockState(neighborPos))) {
                state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), true);
            }
        }

        return state;
    }

    protected static <O, S extends StateHolder<O,S>> S applyDefaultState(S state) {
        return state
                .setValue(PipeBlock.NORTH, Boolean.FALSE)
                .setValue(PipeBlock.EAST, Boolean.FALSE)
                .setValue(PipeBlock.SOUTH, Boolean.FALSE)
                .setValue(PipeBlock.WEST, Boolean.FALSE)
                .setValue(PipeBlock.UP, Boolean.FALSE)
                .setValue(PipeBlock.DOWN, Boolean.FALSE);
    }

    protected static <O, S extends StateHolder<O,S>> void addStateDefinitions(StateDefinition.Builder<O, S> builder) {
        builder.add(PipeBlock.NORTH, PipeBlock.EAST, PipeBlock.SOUTH, PipeBlock.WEST, PipeBlock.UP, PipeBlock.DOWN);
    }
}
