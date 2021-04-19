/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.block.special.walkway;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.FluidLoggableBlock;
import dev.galacticraft.mod.api.block.WireBlock;
import dev.galacticraft.mod.util.EnergyUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class WireWalkway extends WireBlock implements FluidLoggableBlock {
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty DOWN = Properties.DOWN;
    public static final DirectionProperty FACING = Properties.FACING;
    private static final VoxelShape[] shape = new VoxelShape[64];

    public WireWalkway(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false)
                .with(FACING, Direction.UP)
                .with(FLUID, Constant.Misc.EMPTY)
                .with(FlowableFluid.LEVEL, 8));
    }

    private static int getFacingMask(Direction dir) {
        return 1 << (dir.getId());
    }

    private static VoxelShape createShape(Direction facing) {
        VoxelShape base = Block.createCuboidShape(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);
        switch (facing) {
            case UP:
                base = VoxelShapes.union(
                        base,
                        Block.createCuboidShape(6.0D, 10.0D, 6.0D, 10.0D, 14.0D, 10.0D),
                        Block.createCuboidShape(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D));
                break;
            case DOWN:
                base = VoxelShapes.union(
                        base,
                        Block.createCuboidShape(6.0D, 2.0D, 6.0D, 10.0D, 6.0D, 10.0D),
                        Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D));
                break;
            case NORTH:
                base = VoxelShapes.union(
                        base,
                        Block.createCuboidShape(6.0D, 6.0D, 2.0D, 10.0D, 10.0D, 6.0D),
                        Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D));
                break;
            case SOUTH:
                base = VoxelShapes.union(
                        base,
                        Block.createCuboidShape(6.0D, 6.0D, 10.0D, 10.0D, 10.0D, 14.0D),
                        Block.createCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D));
                break;
            case EAST:
                base = VoxelShapes.union(
                        base,
                        Block.createCuboidShape(10.0D, 6.0D, 6.0D, 14.0D, 10.0D, 10.0D),
                        Block.createCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D));
                break;
            case WEST:
                base = VoxelShapes.union(
                        base,
                        Block.createCuboidShape(2.0D, 6.0D, 6.0D, 6.0D, 10.0D, 10.0D),
                        Block.createCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D));
                break;
        }

        return base;
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
        int index = getFacingMask(state.get(FACING));
        if (shape[index] != null) {
            return shape[index];
        }
        return shape[index] = createShape(state.get(FACING));
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

    public boolean canConnect(BlockState state, BlockState neighborState, BlockPos pos, BlockPos neighborPos, WorldAccess world, Direction facing) {
        /* This code tests if the connecting block is on the top of the walkway (it used to be unable to connect there)
        try {
            if (pos.offset(state.get(FACING)).equals(neighborPos))
                return false;
            if (neighborPos.offset(neighborState.get(FACING)).equals(pos))
                return false;
        } catch (IllegalArgumentException ignored) {}
        */
        return neighborState.getBlock() instanceof WireBlock || EnergyUtil.canAccessEnergy((World) world, pos.offset(facing), facing.getOpposite());
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockState state = this.getDefaultState();
        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            state = state.with(getPropForDir(direction), this.canConnect(state,
                    context.getWorld().getBlockState(context.getBlockPos().offset(direction)),
                    context.getBlockPos(),
                    context.getBlockPos().offset(direction),
                    context.getWorld(),
                    direction));
        }
        return state
                .with(FACING, context.getPlayerLookDirection().getOpposite())
                .with(FLUID, Registry.FLUID.getId(fluidState.getFluid()))
                .with(FlowableFluid.LEVEL, Math.max(fluidState.getLevel(), 1));
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
           if (!state.get(FLUID).equals(Constant.Misc.EMPTY)) {
            world.getFluidTickScheduler().schedule(pos, Registry.FLUID.get(state.get(FLUID)), Registry.FLUID.get(state.get(FLUID)).getTickRate(world));
        }
        return state.with(getPropForDir(facing), this.canConnect(state, neighborState, pos, neighborPos, world, facing));
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
        stateBuilder.add(NORTH, EAST, WEST, SOUTH, UP, DOWN, FACING, FLUID, FlowableFluid.LEVEL);
    }
}