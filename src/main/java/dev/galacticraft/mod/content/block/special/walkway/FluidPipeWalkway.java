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
import dev.galacticraft.mod.api.block.FluidPipeBlock;
import dev.galacticraft.mod.api.block.entity.PipeColor;
import dev.galacticraft.mod.api.block.entity.Walkway;
import dev.galacticraft.mod.api.pipe.FluidPipe;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.networked.FluidPipeWalkwayBlockEntity;
import dev.galacticraft.mod.content.block.special.fluidpipe.GlassFluidPipeBlock;
import dev.galacticraft.mod.content.block.special.fluidpipe.PipeBlockEntity;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import dev.galacticraft.mod.util.DirectionUtil;
import dev.galacticraft.mod.util.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FluidPipeWalkway extends FluidPipeBlock implements FluidLoggable {
//    public static final MapCodec<FluidPipeWalkway> CODEC = simpleCodec(FluidPipeWalkway::new);
    private static final VoxelShape[] SHAPES = new VoxelShape[64];

    public FluidPipeWalkway(Properties settings, PipeColor color) {
        super(settings, color);

        BlockState state = this.getStateDefinition().any();
        state = FluidLoggable.applyDefaultState(state);
        state = FluidPipeBlock.applyDefaultState(state);
        this.registerDefaultState(state);
    }

//    @Override
//    protected MapCodec<? extends Block> codec() {
//        return CODEC;
//    }

    private static int getFacingMask(Direction direction) {
        return 1 << direction.get3DDataValue();
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        if (level.getBlockEntity(blockPos) instanceof FluidPipeWalkwayBlockEntity walkway) {
            var index = getFacingMask(walkway.getDirection());
            if (SHAPES[index] != null) {
                return ConnectingBlockUtil.getVoxelShape(walkway, GlassFluidPipeBlock.NORTH, GlassFluidPipeBlock.SOUTH, GlassFluidPipeBlock.EAST, GlassFluidPipeBlock.WEST, GlassFluidPipeBlock.UP, GlassFluidPipeBlock.DOWN, SHAPES[index]);
            }
            return ConnectingBlockUtil.getVoxelShape(walkway, GlassFluidPipeBlock.NORTH, GlassFluidPipeBlock.SOUTH, GlassFluidPipeBlock.EAST, GlassFluidPipeBlock.WEST, GlassFluidPipeBlock.UP, GlassFluidPipeBlock.DOWN, SHAPES[index] = ConnectingBlockUtil.createWalkwayShape(walkway.getDirection()));
        }
        return ConnectingBlockUtil.WALKWAY_TOP;
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return FluidLoggable.applyFluidState(context.getLevel(), state, context.getClickedPos());
    }

    @Override
    public @NotNull BlockState updateShape(BlockState blockState, Direction facing, BlockState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        this.tryScheduleFluidTick(level, blockState, blockPos);
        return blockState;
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborChanged(blockState, level, blockPos, block, fromPos, notify);
        var distance = fromPos.subtract(blockPos);

        if (Math.abs(distance.getX() + distance.getY() + distance.getZ()) == 1 && level.getBlockEntity(blockPos) instanceof Walkway walkway) {
            var direction = DirectionUtil.fromNormal(distance);
            if (direction != walkway.getDirection()) {
                if (level.getBlockEntity(blockPos.relative(direction)) instanceof FluidPipe pipe) {
                    if (pipe.canConnect(direction.getOpposite())) {
                        if (walkway.getConnections()[direction.ordinal()] != (walkway.getConnections()[direction.ordinal()] = true)) {
                            level.neighborChanged(blockPos.relative(direction), blockState.getBlock(), blockPos);
                            level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
                        }
                        return;
                    }
                } else if (FluidUtil.canAccessFluid(level, blockPos.relative(direction), direction)) {
                    if (walkway.getConnections()[direction.ordinal()] != (walkway.getConnections()[direction.ordinal()] = true)) {
                        level.neighborChanged(blockPos.relative(direction), blockState.getBlock(), blockPos);
                        level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
                    }
                    return;
                }
            }
            walkway.getConnections()[Objects.requireNonNull(direction).ordinal()] = false;
            level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
        }
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState blockState) {
        return this.createFluidState(blockState);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        FluidPipeBlock.addStateDefinitions(stateBuilder);
        FluidLoggable.addStateDefinitions(stateBuilder);
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