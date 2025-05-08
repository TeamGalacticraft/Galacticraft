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
import dev.galacticraft.mod.content.block.entity.WalkwayBlockEntity;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import dev.galacticraft.mod.util.DirectionUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
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
import java.util.Map;
import java.util.Objects;

public class WalkwayBlock extends Block implements FluidLoggable, EntityBlock {
    // Maps connection state (from PipeShapedBlock.generateAABBIndex()) and walkway facing direction to the right VoxelShape
    public static final Map<Pair<Integer, Direction>, VoxelShape> SHAPES = Util.make(new HashMap<>(), map -> {
        float pipeRadius = 0.125f;
        VoxelShape[] pipeShapes = PipeShapedBlock.makeShapes(pipeRadius);

        for (int pipeAabb = 0; pipeAabb < Math.pow(2, 6); pipeAabb++) {
            for (Direction platformDirection : Direction.values()) {
                map.put(Pair.of(pipeAabb, platformDirection), Shapes.or(
                        pipeShapes[pipeAabb],
                        ConnectingBlockUtil.WALKWAY_SHAPES.get(platformDirection)
                ));
            }
        }
    });

    public WalkwayBlock(Properties settings) {
        super(settings);
        BlockState defaultState = this.getStateDefinition().any();
        defaultState = FluidLoggable.applyDefaultState(defaultState);
        defaultState = PipeShapedBlock.applyDefaultState(defaultState);
        defaultState = defaultState.setValue(BlockStateProperties.FACING, Direction.UP);
        this.registerDefaultState(defaultState);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        return WalkwayBlock.SHAPES.get(Pair.of(PipeShapedBlock.generateAABBIndex(blockState), blockState.getValue(BlockStateProperties.FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        FluidLoggable.applyFluidState(context.getLevel(), state, context.getClickedPos());
        if (context.getPlayer() != null) {
            state.setValue(BlockStateProperties.FACING, Direction.orderedByNearest(context.getPlayer())[0].getOpposite());
        }
        return state;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState blockState, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        if (!FluidLoggable.hasFluid(blockState)) {
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

            BlockState neighborState = level.getBlockState(fromPos);
            if (level.getBlockEntity(fromPos) instanceof WalkwayBlockEntity walkway2 && neighborState.hasProperty(BlockStateProperties.FACING)) {
                Direction walkway2Facing = neighborState.getValue(BlockStateProperties.FACING);
                if (!fromPos.relative(walkway2Facing).equals(blockPos)) {
                    if (!blockPos.relative(walkway2Facing).equals(fromPos)) {
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
    public @NotNull FluidState getFluidState(BlockState blockState) {
        return FluidLoggable.createFluidState(blockState);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        FluidLoggable.addStateDefinitions(builder);
        PipeShapedBlock.addStateDefinitions(builder);
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new WalkwayBlockEntity(blockPos, blockState);
    }
}