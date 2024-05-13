/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import com.google.common.collect.Lists;
import dev.galacticraft.mod.api.block.FluidLoggable;
import dev.galacticraft.mod.content.block.entity.WalkwayBlockEntity;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import dev.galacticraft.mod.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class WalkwayBlock extends Block implements FluidLoggable, EntityBlock {
    private static final int OFFSET = 2;
    private static final VoxelShape NORTH = box(8 - OFFSET, 8 - OFFSET, 0, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape EAST = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 16, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape SOUTH = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 16);
    private static final VoxelShape WEST = box(0, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape UP = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 16, 8 + OFFSET);
    private static final VoxelShape DOWN = box(8 - OFFSET, 0, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape[] SHAPES = new VoxelShape[64];

    public WalkwayBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FLUID, INVALID)
                .setValue(FlowingFluid.LEVEL, 8)
                .setValue(FlowingFluid.FALLING, false));
    }

    private static int getFacingMask(Direction direction) {
        return 1 << direction.get3DDataValue();
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        if (level.getBlockEntity(blockPos) instanceof WalkwayBlockEntity walkway) {
            var index = getFacingMask(walkway.getDirection());
            var shapes = Lists.newArrayList(ConnectingBlockUtil.WALKWAY_TOP);

            if (walkway.getConnections()[2]) {
                shapes.add(NORTH);
            }
            if (walkway.getConnections()[3]) {
                shapes.add(SOUTH);
            }
            if (walkway.getConnections()[5]) {
                shapes.add(EAST);
            }
            if (walkway.getConnections()[4]) {
                shapes.add(WEST);
            }
            if (walkway.getConnections()[1]) {
                shapes.add(UP);
            }
            if (walkway.getConnections()[0]) {
                shapes.add(DOWN);
            }
            if (SHAPES[index] != null) {
                return Shapes.or(SHAPES[index], shapes.toArray(VoxelShape[]::new));
            }
            return Shapes.or(SHAPES[index] = ConnectingBlockUtil.createWalkwayShape(walkway.getDirection()), shapes.toArray(VoxelShape[]::new));
        }
        return ConnectingBlockUtil.WALKWAY_TOP;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(FLUID, BuiltInRegistries.FLUID.getKey(fluidState.getType()))
                .setValue(FlowingFluid.LEVEL, Math.max(fluidState.getAmount(), 1))
                .setValue(FlowingFluid.FALLING, fluidState.hasProperty(FlowingFluid.FALLING) ? fluidState.getValue(FlowingFluid.FALLING) : false);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
        if (level.getBlockEntity(blockPos) instanceof WalkwayBlockEntity walkway) {
            walkway.setDirection(Direction.orderedByNearest(livingEntity)[0].getOpposite());
            level.updateNeighborsAt(blockPos, blockState.getBlock());
        }
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        if (!this.isEmpty(blockState)) {
            level.scheduleTick(blockPos, BuiltInRegistries.FLUID.get(blockState.getValue(FLUID)), BuiltInRegistries.FLUID.get(blockState.getValue(FLUID)).getTickDelay(level));
        }
        return blockState;
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborChanged(blockState, level, blockPos, block, fromPos, notify);
        var distance = fromPos.subtract(blockPos);

        if (Math.abs(distance.getX() + distance.getY() + distance.getZ()) == 1 && level.getBlockEntity(blockPos) instanceof WalkwayBlockEntity walkway) {
//            walkway.updateConnection(DirectionUtil.fromNormal(distance));
            var direction = DirectionUtil.fromNormal(distance);

            if (level.getBlockEntity(fromPos) instanceof WalkwayBlockEntity walkway2 && walkway2.getDirection() != null) {
                if (!fromPos.relative(walkway2.getDirection()).equals(blockPos)) {
                    if (!blockPos.relative(walkway.getDirection()).equals(fromPos)) {
                        if (walkway.getConnections()[direction.ordinal()] != (walkway.getConnections()[direction.ordinal()] = true)) {
                            level.neighborChanged(blockPos.relative(direction), blockState.getBlock(), blockPos);
                            level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
                        }
                        return;
                    }
                }
            }
            walkway.getConnections()[Objects.requireNonNull(direction).ordinal()] = false;
            level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
        }
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (this.isEmpty(blockState)) {
            return EMPTY_STATE;
        }

        var state1 = BuiltInRegistries.FLUID.get(blockState.getValue(FLUID)).defaultFluidState();

        if (state1.getValues().containsKey(FlowingFluid.LEVEL)) {
            state1 = state1.setValue(FlowingFluid.LEVEL, blockState.getValue(FlowingFluid.LEVEL));
        }
        if (state1.getValues().containsKey(FlowingFluid.FALLING)) {
            state1 = state1.setValue(FlowingFluid.FALLING, blockState.getValue(FlowingFluid.FALLING));
        }
        return state1;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FLUID, FlowingFluid.LEVEL, FlowingFluid.FALLING);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new WalkwayBlockEntity(blockPos, blockState);
    }
}