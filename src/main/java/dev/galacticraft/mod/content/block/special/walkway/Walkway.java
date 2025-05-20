/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.content.block.special.walkway;

import dev.galacticraft.mod.api.block.FluidLoggable;
import dev.galacticraft.mod.api.block.PipeShapedBlock;
import dev.galacticraft.mod.api.block.entity.Connected;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.WalkwayBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Walkway extends PipeShapedBlock<WalkwayBlockEntity> implements WalkwayBlock, FluidLoggable {
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
        return WalkwayBlock.getShape(state);
    }

    @Override
    protected @NotNull VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        state = FluidLoggable.applyFluidState(context.getLevel(), state, context.getClickedPos());
        state = WalkwayBlock.applyStateForPlacement(state, context);
        return state;
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState state) {
        return FluidLoggable.createFluidState(state);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        FluidLoggable.tryScheduleFluidTick(levelAccessor, state, pos);
        return super.updateShape(state, direction, neighborState, levelAccessor, pos, neighborPos);
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
