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

package dev.galacticraft.mod.content.block.special.fluidpipe;

import dev.galacticraft.mod.api.block.FluidLoggable;
import dev.galacticraft.mod.api.block.FluidPipeBlock;
import dev.galacticraft.mod.api.block.entity.PipeColor;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.entity.networked.GlassFluidPipeBlockEntity;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlassFluidPipeBlock extends FluidPipeBlock implements FluidLoggable {
    public static final VoxelShape NORTH = box(6, 6, 0, 10, 10, 10);
    public static final VoxelShape EAST = box(6, 6, 6, 16, 10, 10);
    public static final VoxelShape SOUTH = box(6, 6, 6, 10, 10, 16);
    public static final VoxelShape WEST = box(0, 6, 6, 10, 10, 10);
    public static final VoxelShape UP = box(6, 6, 6, 10, 16, 10);
    public static final VoxelShape DOWN = box(6, 0, 6, 10, 10, 10);
    public static final VoxelShape NONE = box(6, 6, 6, 10, 10, 10);

    public GlassFluidPipeBlock(Properties settings, PipeColor color) {
        super(settings, color);

        BlockState state = this.getStateDefinition().any();
        state = FluidPipeBlock.applyDefaultState(state);
        state = FluidLoggable.applyDefaultState(state);
        this.registerDefaultState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> compositeStateBuilder) {
        FluidPipeBlock.addStateDefinitions(compositeStateBuilder);
        FluidLoggable.addStateDefinitions(compositeStateBuilder);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborChanged(blockState, level, blockPos, block, fromPos, notify);

        if (level.getBlockEntity(blockPos) instanceof PipeBlockEntity glassPipe) {
            var direction = DirectionUtil.fromNormal(fromPos.getX() - blockPos.getX(), fromPos.getY() - blockPos.getY(), fromPos.getZ() - blockPos.getZ());

            if (direction != null) {
                if (!level.isClientSide
                        && glassPipe.getConnections()[direction.ordinal()]
                        != (glassPipe.getConnections()[direction.ordinal()]
                        = glassPipe.canConnect(direction) && FluidUtil.canAccessFluid(level, fromPos, direction))
                ) {
                    level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
                    glassPipe.setChanged();
                }
            }
        }
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        state = FluidLoggable.applyFluidState(ctx.getLevel(), state, ctx.getClickedPos());
        return state;
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        FluidLoggable.tryScheduleFluidTick(levelAccessor, state, pos);
        return super.updateShape(state, direction, neighborState, levelAccessor, pos, neighborPos);
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState state) {
        return FluidLoggable.createFluidState(state);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return 1.0F;
    }

    @Override
    @Nullable
    public PipeBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GlassFluidPipeBlockEntity(blockPos, blockState);
    }

    @Override
    protected Block getMatchingBlock(PipeColor color) {
        return GCBlocks.GLASS_FLUID_PIPES.get(color);
    }
}