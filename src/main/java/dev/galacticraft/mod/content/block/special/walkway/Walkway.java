package dev.galacticraft.mod.content.block.special.walkway;

import dev.galacticraft.mod.api.block.FluidLoggable;
import dev.galacticraft.mod.api.block.PipeShapedBlock;
import dev.galacticraft.mod.api.block.entity.Connected;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.WalkwayBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Walkway extends PipeShapedBlock<WalkwayBlockEntity> implements WalkwayBlock {
    public Walkway(Properties properties) {
        super(0.125f, properties);

        BlockState defaultState = this.getStateDefinition().any();
        defaultState = FluidLoggable.applyDefaultState(defaultState);
        defaultState = WalkwayBlock.applyDefaultState(defaultState);
        this.registerDefaultState(defaultState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> compositeStateBuilder) {
        FluidLoggable.addStateDefinitions(compositeStateBuilder);
        WalkwayBlock.addStateDefinitions(compositeStateBuilder);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (world.getBlockEntity(pos) instanceof Connected connected) {
            return WalkwayBlock.getShape(connected, state);
        }
        return Shapes.empty();
    }

    @Override
    public boolean canConnectTo(Level level, BlockPos thisPos, Direction direction, BlockPos neighborPos, BlockState thisState) {
        BlockState neighborState = level.getBlockState(neighborPos);
        return neighborState.is(GCBlocks.WALKWAY) || neighborState.isFaceSturdy(level, neighborPos, direction.getOpposite(), SupportType.CENTER);
    }

    @Override
    protected void onConnectionChanged(Level level, BlockPos thisPos, Direction direction, BlockPos neighborPos) {

    }

    @Override
    public @Nullable WalkwayBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WalkwayBlockEntity(pos, state);
    }
}
