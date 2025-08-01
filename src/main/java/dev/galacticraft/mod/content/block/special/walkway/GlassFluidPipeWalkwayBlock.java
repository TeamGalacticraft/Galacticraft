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
import dev.galacticraft.mod.api.block.entity.Connected;
import dev.galacticraft.mod.api.block.entity.PipeColor;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.networked.GlassFluidPipeBlockEntity;
import dev.galacticraft.mod.content.block.special.fluidpipe.PipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlassFluidPipeWalkwayBlock extends FluidPipeBlock implements AbstractWalkwayBlock, FluidLoggable {
    public GlassFluidPipeWalkwayBlock(Properties settings, PipeColor color) {
        super(settings, color);

        BlockState state = this.getStateDefinition().any();
        state = FluidLoggable.applyDefaultState(state);
        state = AbstractWalkwayBlock.applyDefaultState(state);
        this.registerDefaultState(state);
    }

    @Override
    protected @NotNull MapCodec<? extends FluidPipeBlock> codec() {
        return this.simpleCodec(GlassFluidPipeWalkwayBlock::new);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        if (level.getBlockEntity(blockPos) instanceof Connected connected) {
            return AbstractWalkwayBlock.getShape(connected, blockState);
        }
        return AbstractWalkwayBlock.getShape(blockState);
    }

    @Override
    protected @NotNull VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        state = FluidLoggable.applyFluidState(context.getLevel(), state, context.getClickedPos());
        state = AbstractWalkwayBlock.applyStateForPlacement(state, context);
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
        FluidLoggable.addStateDefinitions(stateBuilder);
        AbstractWalkwayBlock.addStateDefinitions(stateBuilder);
    }

    @Override
    protected BlockState rotate(BlockState blockState, Rotation rotation) {
        return (BlockState)blockState.setValue(BlockStateProperties.FACING, rotation.rotate(blockState.getValue(BlockStateProperties.FACING)));
    }

    @Override
    protected BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(BlockStateProperties.FACING)));
    }

    @Override
    @Nullable
    public PipeBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GlassFluidPipeBlockEntity(blockPos, blockState);
    }

    @Override
    protected Block getMatchingBlock(PipeColor color) {
        return GCBlocks.FLUID_PIPE_WALKWAY;
    }
}