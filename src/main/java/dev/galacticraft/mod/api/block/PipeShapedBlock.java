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
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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

            BlockState neighborState = level.getBlockState(neighborPos);
            if (this.canConnectTo(level, ctx.getClickedPos(), direction, neighborPos, state, neighborState)) {
                if (neighborState.getBlock() instanceof PipeShapedBlock neighborPipe) {
                    if (neighborPipe.canConnectTo(level, neighborPos, direction.getOpposite(), ctx.getClickedPos(), neighborState, state)) {
                        state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), true);
                    }
                } else {
                    state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), true);
                }
            }
        }

        return state;
    }

    public static VoxelShape[] makeShapes(float radius) {
        Direction[] directions = Direction.values();

        float f = 0.5F - radius;
        float g = 0.5F + radius;
        VoxelShape voxelShape = Block.box(
                (double)(f * 16.0F), (double)(f * 16.0F), (double)(f * 16.0F), (double)(g * 16.0F), (double)(g * 16.0F), (double)(g * 16.0F)
        );
        VoxelShape[] voxelShapes = new VoxelShape[directions.length];

        for (int i = 0; i < directions.length; i++) {
            Direction direction = directions[i];
            voxelShapes[i] = Shapes.box(
                    0.5 + Math.min((double)(-radius), (double)direction.getStepX() * 0.5),
                    0.5 + Math.min((double)(-radius), (double)direction.getStepY() * 0.5),
                    0.5 + Math.min((double)(-radius), (double)direction.getStepZ() * 0.5),
                    0.5 + Math.max((double)radius, (double)direction.getStepX() * 0.5),
                    0.5 + Math.max((double)radius, (double)direction.getStepY() * 0.5),
                    0.5 + Math.max((double)radius, (double)direction.getStepZ() * 0.5)
            );
        }

        VoxelShape[] voxelShapes2 = new VoxelShape[64];

        for (int j = 0; j < 64; j++) {
            VoxelShape voxelShape2 = voxelShape;

            for (int k = 0; k < directions.length; k++) {
                if ((j & 1 << k) != 0) {
                    voxelShape2 = Shapes.or(voxelShape2, voxelShapes[k]);
                }
            }

            voxelShapes2[j] = voxelShape2;
        }

        return voxelShapes2;
    }

    public static int generateAABBIndex(BlockState state) {
        int i = 0;

        Direction[] directions = Direction.values();
        for (int j = 0; j < directions.length; j++) {
            if (state.getValue(PROPERTY_BY_DIRECTION.get(directions[j]))) {
                i |= 1 << j;
            }
        }

        return i;
    }

    public static <O, S extends StateHolder<O,S>> S applyDefaultState(S state) {
        return state
                .setValue(PipeBlock.NORTH, Boolean.FALSE)
                .setValue(PipeBlock.EAST, Boolean.FALSE)
                .setValue(PipeBlock.SOUTH, Boolean.FALSE)
                .setValue(PipeBlock.WEST, Boolean.FALSE)
                .setValue(PipeBlock.UP, Boolean.FALSE)
                .setValue(PipeBlock.DOWN, Boolean.FALSE);
    }

    public static <O, S extends StateHolder<O,S>> void addStateDefinitions(StateDefinition.Builder<O, S> builder) {
        builder.add(PipeBlock.NORTH, PipeBlock.EAST, PipeBlock.SOUTH, PipeBlock.WEST, PipeBlock.UP, PipeBlock.DOWN);
    }
}
