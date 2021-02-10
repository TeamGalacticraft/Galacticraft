/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.block.special.walkway;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.FluidLoggableBlock;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class Walkway extends Block implements FluidLoggableBlock {
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    private static final VoxelShape[] shape = new VoxelShape[16];
    private final Object2IntMap<BlockState> SHAPE_INDEX_CACHE = new Object2IntOpenHashMap<>();

    public Walkway(Settings settings) {
        super(settings);

        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(FLUID, Constants.Misc.EMPTY)
                .with(FlowableFluid.LEVEL, 8));
    }

    private static int getDirectionMask(Direction dir) {
        return 1 << dir.getHorizontal();
    }

    private static VoxelShape createShape(boolean north, boolean south, boolean east, boolean west) {
        VoxelShape top1 = Block.createCuboidShape(0.0D, 13.0D, 0.0D, 2.0D, 16.0D, 16.0D);
        VoxelShape top2 = Block.createCuboidShape(0.0D, 13.0D, 16.0D, 16.0D, 16.0D, 14.0D);
        VoxelShape top3 = Block.createCuboidShape(16.0D, 13.0D, 16.0D, 14.0D, 16.0D, 0.0D);
        VoxelShape top4 = Block.createCuboidShape(16.0D, 13.0D, 0.0D, 0.0D, 16.0D, 2.0D);
        VoxelShape mTop1 = Block.createCuboidShape(6.0D, 15.0D, 2.0D, 16.0D - 6.0D, 14.0D, 16.0D - 2.0D);
        VoxelShape mTop2 = Block.createCuboidShape(2.0D, 15.0D, 6.0D, 16.0D - 2.0D, 14.0D, 16.0D - 6.0D);
        VoxelShape center = Block.createCuboidShape(6.0D, 15.0D, 6.0D, 10.0D, 6.0D, 10.0);
        VoxelShape base = VoxelShapes.union(top1, top2, top3, top4, mTop1, mTop2, center);

        if (north) {
            base = VoxelShapes.union(base, Block.createCuboidShape(7.0D, 9.0D, 7.0D, 9.0D, 7.0D, 0.0D));
        }

        if (south) {
            base = VoxelShapes.union(base, Block.createCuboidShape(7.0D, 9.0D, 9.0D, 9.0D, 7.0D, 16.0D));
        }

        if (east) {
            base = VoxelShapes.union(base, Block.createCuboidShape(9.0D, 9.0D, 7.0D, 16.0D, 7.0D, 9.0D));
        }

        if (west) {
            base = VoxelShapes.union(base, Block.createCuboidShape(7.0D, 9.0D, 7.0D, 0.0D, 7.0D, 9.0D));
        }

        return VoxelShapes.union(base);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state);
    }

    private VoxelShape getShape(BlockState state) {
        int index = getShapeIndex(state);
        if (shape[index] != null) {
            return shape[index];
        }
        return shape[index] = createShape(state.get(NORTH), state.get(SOUTH), state.get(EAST), state.get(WEST));
    }

    protected int getShapeIndex(BlockState state) {
        return this.SHAPE_INDEX_CACHE.computeIntIfAbsent(state, (blockState) -> {
            int i = 0;
            if (blockState.get(NORTH)) {
                i |= getDirectionMask(Direction.NORTH);
            }

            if (blockState.get(EAST)) {
                i |= getDirectionMask(Direction.EAST);
            }

            if (blockState.get(SOUTH)) {
                i |= getDirectionMask(Direction.SOUTH);
            }

            if (blockState.get(WEST)) {
                i |= getDirectionMask(Direction.WEST);
            }

            return i;
        });
    }

    private BooleanProperty getPropForDir(Direction direction) {
        switch (direction) {
            case NORTH:
                return NORTH;
            case WEST:
                return WEST;
            case EAST:
                return EAST;
            case SOUTH:
                return SOUTH;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180:
                return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
            case COUNTERCLOCKWISE_90:
                return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
            case CLOCKWISE_90:
                return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
            default:
                return state;
        }
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT:
                return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
            case FRONT_BACK:
                return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
            default:
                return super.mirror(state, mirror);
        }
    }

    public boolean canConnect(BlockState state) {
        return state.getBlock() instanceof Walkway;
    }

//
//    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
//        return false;
//    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        return this.getDefaultState()
                .with(FLUID, Registry.FLUID.getId(fluidState.getFluid()))
                .with(FlowableFluid.LEVEL, Math.max(fluidState.getLevel(), 1));
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.get(FLUID).equals(Constants.Misc.EMPTY)) {
            world.getFluidTickScheduler().schedule(pos, Registry.FLUID.get(state.get(FLUID)), Registry.FLUID.get(state.get(FLUID)).getTickRate(world));
        }
        return facing.getAxis().getType() == Direction.Type.HORIZONTAL ? state.with(getPropForDir(facing), this.canConnect(neighborState)) : super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        FluidState state1 = Registry.FLUID.get(state.get(FLUID)).getDefaultState();
        if (state1.getEntries().containsKey(FlowableFluid.LEVEL)) {
            state1 = state1.with(FlowableFluid.LEVEL, state.get(FlowableFluid.LEVEL));
        }
        return state1;
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(NORTH, EAST, WEST, SOUTH, FLUID, FlowableFluid.LEVEL);
    }
}