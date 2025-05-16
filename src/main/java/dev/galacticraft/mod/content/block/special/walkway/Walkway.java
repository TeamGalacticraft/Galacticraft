package dev.galacticraft.mod.content.block.special.walkway;

import dev.galacticraft.mod.api.block.FluidLoggable;
import dev.galacticraft.mod.api.block.PipeShapedBlock;
import dev.galacticraft.mod.content.block.entity.WalkwayBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
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
    public boolean canConnectTo(Level level, BlockPos thisPos, Direction direction, BlockPos neighborPos, BlockState thisState) {
        return true;
    }

    @Override
    protected void onConnectionChanged(Level level, BlockPos thisPos, Direction direction, BlockPos neighborPos) {

    }

    @Override
    public @Nullable WalkwayBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WalkwayBlockEntity(pos, state);
    }
}
