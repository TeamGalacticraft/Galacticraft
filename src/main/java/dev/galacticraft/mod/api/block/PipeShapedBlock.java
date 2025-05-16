package dev.galacticraft.mod.api.block;

import dev.galacticraft.mod.api.block.entity.Connected;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PipeShapedBlock<BE extends BlockEntity & Connected> extends Block implements EntityBlock {
    public final VoxelShape[] shapes;

    protected PipeShapedBlock(float radius, BlockBehaviour.Properties properties) {
        super(properties);
        this.shapes = makeShapes(radius);
    }

    @Override
    abstract public @Nullable BE newBlockEntity(BlockPos pos, BlockState state);

    abstract public boolean canConnectTo(Level level, BlockPos thisPos, Direction direction, BlockPos neighborPos, BlockState thisState);

    abstract protected void onConnectionChanged(Level level, BlockPos thisPos, Direction direction, BlockPos neighborPos);

    protected boolean updateConnection(BlockState currentState, BlockPos pos, Direction side, BlockPos neighborPos, Level level) {
        if (level.getBlockEntity(pos) instanceof Connected pipe) {
            boolean canConnect = this.canConnectTo(level, pos, side, neighborPos, currentState);

            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof PipeShapedBlock<?> neighbor) {
                canConnect &= neighbor.canConnectTo(level, neighborPos, side.getOpposite(), pos, neighborState);
            }

            boolean currentlyConnected = pipe.getConnections()[side.get3DDataValue()];
            pipe.getConnections()[side.get3DDataValue()] = canConnect;
            return canConnect != currentlyConnected;
        } else {
            return false;
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean notify) {
        super.neighborChanged(state, level, pos, block, neighborPos, notify);

        Direction direction = Direction.fromDelta(neighborPos.getX() - pos.getX(), neighborPos.getY() - pos.getY(), neighborPos.getZ() - pos.getZ());
        if (direction == null)
            return;

        if (this.updateConnection(state, pos, direction, neighborPos, level)) {
            level.updateNeighborsAtExceptFromFacing(pos, state.getBlock(), direction);
            this.onConnectionChanged(level, pos, direction, neighborPos);
        }
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        if (levelAccessor instanceof Level level) {
            this.updateConnection(state, pos, direction, neighborPos, level);
        }

        return state;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (world.getBlockEntity(pos) instanceof Connected connected) {
            return this.shapes[generateAABBIndex(connected)];
        }
        return this.shapes[0];
    }

    public static VoxelShape[] makeShapes(float radius) {
        Direction[] directions = Direction.values();

        float f = 0.5F - radius;
        float g = 0.5F + radius;
        VoxelShape voxelShape = Block.box(
                f * 16.0F, f * 16.0F, f * 16.0F, g * 16.0F, g * 16.0F, g * 16.0F
        );
        VoxelShape[] voxelShapes = new VoxelShape[directions.length];

        for (int i = 0; i < directions.length; i++) {
            Direction direction = directions[i];
            voxelShapes[i] = Shapes.box(
                    0.5 + Math.min(-radius, (double)direction.getStepX() * 0.5),
                    0.5 + Math.min(-radius, (double)direction.getStepY() * 0.5),
                    0.5 + Math.min(-radius, (double)direction.getStepZ() * 0.5),
                    0.5 + Math.max(radius, (double)direction.getStepX() * 0.5),
                    0.5 + Math.max(radius, (double)direction.getStepY() * 0.5),
                    0.5 + Math.max(radius, (double)direction.getStepZ() * 0.5)
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

    public static int generateAABBIndex(Connected connected) {
        int i = 0;

        Direction[] directions = Direction.values();
        for (int j = 0; j < directions.length; j++) {
            if (connected.getConnections()[directions[j].ordinal()]) {
                i |= 1 << j;
            }
        }

        return i;
    }

    public static <O, S extends StateHolder<O,S>> S applyDefaultState(S state) {
        return state;
    }

    public static <O, S extends StateHolder<O,S>> void addStateDefinitions(StateDefinition.Builder<O, S> builder) {
    }
}
