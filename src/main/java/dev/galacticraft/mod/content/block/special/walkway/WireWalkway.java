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
import dev.galacticraft.mod.api.block.WireBlock;
import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
import dev.galacticraft.mod.content.block.entity.networked.WireWalkwayBlockEntity;
import dev.galacticraft.mod.content.block.special.aluminumwire.tier1.AluminumWireBlock;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.Objects;

public class WireWalkway extends WireBlock implements FluidLoggable {
    private static final VoxelShape[] SHAPES = new VoxelShape[64];

    public WireWalkway(Properties settings) {
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
        if (level.getBlockEntity(blockPos) instanceof WireWalkwayBlockEntity walkway) {
            var index = getFacingMask(walkway.getDirection());
            var shapes = Lists.newArrayList(ConnectingBlockUtil.WALKWAY_TOP);

            if (walkway.getConnections()[2]) {
                shapes.add(AluminumWireBlock.NORTH);
            }
            if (walkway.getConnections()[3]) {
                shapes.add(AluminumWireBlock.SOUTH);
            }
            if (walkway.getConnections()[5]) {
                shapes.add(AluminumWireBlock.EAST);
            }
            if (walkway.getConnections()[4]) {
                shapes.add(AluminumWireBlock.WEST);
            }
            if (walkway.getConnections()[1]) {
                shapes.add(AluminumWireBlock.UP);
            }
            if (walkway.getConnections()[0]) {
                shapes.add(AluminumWireBlock.DOWN);
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
        if (level.getBlockEntity(blockPos) instanceof WireWalkwayBlockEntity walkway) {
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
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos neighborPos, boolean notify) {
        super.neighborChanged(blockState, level, blockPos, block, neighborPos, notify);
        var distance = neighborPos.subtract(blockPos);

        if (Math.abs(distance.getX() + distance.getY() + distance.getZ()) == 1 && level.getBlockEntity(blockPos) instanceof WireWalkwayBlockEntity walkway) {
            var direction = DirectionUtil.fromNormal(distance);
            if (direction != walkway.getDirection()) {
                if (level.getBlockEntity(blockPos.relative(direction)) instanceof Wire wire && wire instanceof WireWalkwayBlockEntity) {
                    if (wire.canConnect(direction.getOpposite())) {
                        if (walkway.getConnections()[direction.ordinal()] != (walkway.getConnections()[direction.ordinal()] = true)) {
                            level.neighborChanged(blockPos.relative(direction), blockState.getBlock(), blockPos);
                            level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
                        }
                        return;
                    }
                }
                else if (EnergyStorage.SIDED.find(level, blockPos.relative(direction), direction.getOpposite()) != null) {
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
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FLUID, FlowingFluid.LEVEL, FlowingFluid.FALLING);
    }

    @Override
    @Nullable
    public WireBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new WireWalkwayBlockEntity(blockPos, blockState);
    }
}