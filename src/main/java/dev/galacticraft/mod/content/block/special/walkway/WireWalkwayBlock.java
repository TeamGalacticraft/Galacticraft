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
import dev.galacticraft.mod.api.block.WireBlock;
import dev.galacticraft.mod.api.block.entity.Connected;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WireWalkwayBlock extends WireBlock implements FluidLoggable, WalkwayBlock {
    public WireWalkwayBlock(Properties settings) {
        super(0.125f, settings);
        BlockState defaultState = this.getStateDefinition().any();
        defaultState = FluidLoggable.applyDefaultState(defaultState);
        defaultState = WalkwayBlock.applyDefaultState(defaultState);
        this.registerDefaultState(defaultState);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        if (level.getBlockEntity(blockPos) instanceof Connected connected) {
            return WalkwayBlock.getShape(connected, blockState);
        }
        return WalkwayBlock.getShape(blockState);
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
    public @NotNull BlockState updateShape(BlockState blockState, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        FluidLoggable.tryScheduleFluidTick(level, blockState, blockPos);
        return super.updateShape(blockState, direction, neighborState, level, blockPos, neighborPos);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState blockState) {
        return FluidLoggable.createFluidState(blockState);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        FluidLoggable.addStateDefinitions(stateBuilder);
        WalkwayBlock.addStateDefinitions(stateBuilder);
    }

    @Override
    @Nullable
    public WireBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return WireBlockEntity.createT1(GCBlockEntityTypes.WIRE_T1, blockPos, blockState);
    }
}