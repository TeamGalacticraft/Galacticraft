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

import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.api.block.FluidLoggable;
import dev.galacticraft.mod.api.block.FluidPipeBlock;
import dev.galacticraft.mod.api.block.entity.PipeColor;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.networked.FluidPipeWalkwayBlockEntity;
import dev.galacticraft.mod.content.block.special.fluidpipe.PipeBlockEntity;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class FluidPipeWalkway extends FluidPipeBlock implements FluidLoggable {
    public FluidPipeWalkway(Properties settings, PipeColor color) {
        super(settings, color);

        BlockState state = this.getStateDefinition().any();
        state = FluidLoggable.applyDefaultState(state);
        state = FluidPipeBlock.applyDefaultState(state);
        state.setValue(BlockStateProperties.FACING, Direction.UP);
        this.registerDefaultState(state);
    }

    @Override
    protected @NotNull MapCodec<? extends PipeBlock> codec() {
        return this.simpleCodec(FluidPipeWalkway::new);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return WalkwayBlock.SHAPES.get(Pair.of(this.getAABBIndex(blockState), blockState.getValue(BlockStateProperties.FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        state = FluidLoggable.applyFluidState(context.getLevel(), state, context.getClickedPos());
        if (context.getPlayer() != null) {
            state = state.setValue(BlockStateProperties.FACING, Direction.orderedByNearest(context.getPlayer())[0].getOpposite());
        }
        return state;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState blockState, Direction facing, BlockState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        FluidLoggable.tryScheduleFluidTick(level, blockState, blockPos);
        return super.updateShape(blockState, facing, neighborState, level, blockPos, neighborPos);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState blockState) {
        return FluidLoggable.createFluidState(blockState);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        FluidPipeBlock.addStateDefinitions(stateBuilder);
        FluidLoggable.addStateDefinitions(stateBuilder);
        stateBuilder.add(BlockStateProperties.FACING);
    }

    @Override
    @Nullable
    public PipeBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FluidPipeWalkwayBlockEntity(blockPos, blockState);
    }

    @Override
    protected Block getMatchingBlock(PipeColor color) {
        return GCBlocks.FLUID_PIPE_WALKWAY;
    }
}