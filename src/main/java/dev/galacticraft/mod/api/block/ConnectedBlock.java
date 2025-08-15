package dev.galacticraft.mod.api.block;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import dev.galacticraft.mod.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConnectedBlock extends Block {
    public ConnectedBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(BlockStateProperties.NORTH, false)
                .setValue(BlockStateProperties.SOUTH, false)
                .setValue(BlockStateProperties.EAST, false)
                .setValue(BlockStateProperties.WEST, false)
                .setValue(BlockStateProperties.UP, false)
                .setValue(BlockStateProperties.DOWN, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.NORTH).add(BlockStateProperties.SOUTH)
                .add(BlockStateProperties.EAST).add(BlockStateProperties.WEST)
                .add(BlockStateProperties.UP).add(BlockStateProperties.DOWN);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        assert state != null;
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            state = state.setValue(ConnectingBlockUtil.getBooleanProperty(direction), this.connectsTo(ctx.getLevel(), ctx.getClickedPos(), state, direction, ctx.getClickedPos().relative(direction)));
        }
        return state;
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState blockState = this.updateConnection(level, pos, state, direction, neighborPos);
        if (state != blockState && level instanceof ServerLevel l) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                be.setBlockState(blockState);
                this.onConnectionUpdate(l, pos, blockState, direction, neighborPos, neighborState);
            }
        }
        return blockState;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborChanged(state, level, pos, sourceBlock, sourcePos, notify);
        if (!level.isClientSide) {
            Direction dir = DirectionUtil.fromNormal(sourcePos.getX() - pos.getX(), sourcePos.getY() - pos.getY(), sourcePos.getZ() - pos.getZ());
            BlockState sourceState = level.getBlockState(sourcePos);
            BlockState blockState = this.updateShape(state, dir, sourceState, level, pos, sourcePos);
            if (blockState != state) {
                level.setBlock(pos, blockState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                this.onConnectionUpdate((ServerLevel) level, pos, blockState, dir, sourcePos, sourceState);
            }
        }
    }

    protected abstract boolean canConnectTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, BlockPos neighborPos, BlockState neighborState);
    protected void onConnectionUpdate(ServerLevel level, BlockPos pos, BlockState state, Direction direction, BlockPos neighborPos, BlockState neighborState) {
    }

    protected BlockState updateConnection(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, BlockPos neighborPos) {
        return state.setValue(ConnectingBlockUtil.getBooleanProperty(direction), this.connectsTo(level, pos, state, direction, neighborPos));
    }

    protected boolean connectsTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, BlockPos neighborPos) {
        BlockState neighborState = level.getBlockState(neighborPos);
        boolean canConnect = this.canConnectTo(level, pos, state, direction, neighborPos, neighborState);

        if (neighborState.getBlock() instanceof ConnectedBlock neighbor) {
            canConnect &= neighbor.canConnectTo(level, neighborPos, neighborState, direction.getOpposite(), pos, state);
        }
        return canConnect;
    }
}
