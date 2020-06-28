/*
 * Copyright (c) 2020 HRZN LTD
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

package com.hrznstudio.galacticraft.block.special.vacuumglass;

import com.hrznstudio.galacticraft.api.block.FluidLoggableBlock;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
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
public class VacuumGlass extends Block implements FluidLoggableBlock {
    public static final BooleanProperty NORTH = ConnectingBlock.NORTH;
    public static final BooleanProperty EAST = ConnectingBlock.EAST;
    public static final BooleanProperty SOUTH = ConnectingBlock.SOUTH;
    public static final BooleanProperty WEST = ConnectingBlock.WEST;
    public static final BooleanProperty UP = ConnectingBlock.UP;
    public static final BooleanProperty DOWN = ConnectingBlock.DOWN;
    private static final VoxelShape[] shape = new VoxelShape[16];
    private final Object2IntMap<BlockState> SHAPE_INDEX_CACHE = new Object2IntOpenHashMap<>();

    public VacuumGlass(Settings settings) {
        super(settings);

        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false)
                .with(FLUID, new Identifier("empty"))
                .with(FlowableFluid.LEVEL, 8));
    }

    private static int getDirectionMask(Direction dir) {
        return 1 << dir.getHorizontal();
    }

    private static VoxelShape createShape(boolean north, boolean south, boolean east, boolean west, boolean up, boolean down) {
        VoxelShape core = VoxelShapes.union(
                Block.createCuboidShape(4.0D, 15.0D, 4.0D, 12.0D, 16.0D, 12.0D),
                Block.createCuboidShape(5.0D, 14.0D, 5.0D, 11.0D, 15.0D, 11.0D),
                Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 1.0D, 12.0D),
                Block.createCuboidShape(5.0D, 1.0D, 5.0D, 11.0D, 2.0D, 11.0D));
        VoxelShape n_m = VoxelShapes.union(
                Block.createCuboidShape(4.0D, 1.0D, 0.0D, 12.0D, 15.0D, 1.0D),
                Block.createCuboidShape(5.0D, 1.0D, 1.0D, 11.0D, 15.0D, 2.0D),
                Block.createCuboidShape(4.0D, 15.0D, 0.0D, 12.0D, 16.0D, 4.0D),
                Block.createCuboidShape(5.0D, 14.0D, 2.0D, 11.0D, 15.0D, 5.0D),
                Block.createCuboidShape(5.0D, 1.0D, 2.0D, 11.0D, 2.0D, 5.0D),
                Block.createCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 1.0D, 4.0D));
        VoxelShape n_g = VoxelShapes.union(
                Block.createCuboidShape(6.0D, 2.0D, 0.0D, 10.0D, 14.0D, 2.0D),
                Block.createCuboidShape(4.0D, 15.0D, 0.0D, 12.0D,16.0D,4.0D),
                Block.createCuboidShape(5.0D,14.0D,0.0D,11.0D,15.0D,5.0D),
                Block.createCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 1.0D, 4.0D),
                Block.createCuboidShape(5.0D, 1.0D, 0.0D, 11.0D, 2.0D, 5.0D));
        VoxelShape e_m = VoxelShapes.union(
                Block.createCuboidShape(15.0D, 1.0D, 4.0D, 16.0D, 15.0D, 12.0D)
                //TODO: Add rest of cubes
        );
        VoxelShape e_g;
        VoxelShape s_m;
        VoxelShape s_g;
        VoxelShape w_m;
        VoxelShape w_g;
        VoxelShape ns_glass = Block.createCuboidShape(6.0D, 2.0D, 2.0D, 10.0D, 14.0D, 14.0D);
        VoxelShape nw_glass;
        VoxelShape nes_glass;
        VoxelShape x_glass;
        /*
        if (north) {
            core = VoxelShapes.union(core, n_g);
        } else {
            core = VoxelShapes.union(core, n_m);
        }
        if (south) {

        }
        if (east) {

        }
        if (west) {

        }*/
        return core;
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
        return shape[index] = createShape(state.get(NORTH),
                state.get(SOUTH),
                state.get(EAST),
                state.get(WEST),
                state.get(UP),
                state.get(DOWN));
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
            case UP:
                return UP;
            case DOWN:
                return DOWN;
        }
        throw new IllegalArgumentException("Wrong direction for VacuumGlass!");
    }

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
        return state.getBlock() instanceof VacuumGlass;
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return true;
    }

    private BooleanProperty propFromDirection(Direction direction) {
        switch (direction) {
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case EAST:
                return EAST;
            case WEST:
                return WEST;
            case UP:
                return UP;
            case DOWN:
                return DOWN;
            default:
                return null;
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockState state = this.getDefaultState();
        for (Direction direction : Direction.values()) {
            Block block = context.getWorld().getBlockState(context.getBlockPos().offset(direction)).getBlock();
            if (block instanceof VacuumGlass) {
                state = state.with(propFromDirection(direction), true);
            }
        }
        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        return state.with(FLUID, Registry.FLUID.getId(fluidState.getFluid()))
                .with(FlowableFluid.LEVEL, Math.max(fluidState.getLevel(), 1));
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.get(FLUID).equals(new Identifier("empty"))) {
            world.getFluidTickScheduler().schedule(pos, Registry.FLUID.get(state.get(FLUID)), Registry.FLUID.get(state.get(FLUID)).getTickRate(world));
        }
        return state.with(getPropForDir(facing), this.canConnect(neighborState));
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
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
        stateBuilder.add(NORTH, EAST, WEST, SOUTH, UP, DOWN, FLUID, FlowableFluid.LEVEL);
    }
}