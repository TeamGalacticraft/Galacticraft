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
import com.hrznstudio.galacticraft.api.block.WireBlock;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class WireWalkway extends WireBlock implements FluidLoggableBlock {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private static final VoxelShape[] shape = new VoxelShape[4096];
    private final Object2IntMap<BlockState> SHAPE_INDEX_CACHE = new Object2IntOpenHashMap<>();

    public WireWalkway(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(FACING, Direction.UP)
                .setValue(FLUID, Constants.Misc.EMPTY)
                .setValue(FlowingFluid.LEVEL, 8));
    }

    private static int getDirectionMask(Direction dir) {
        return 1 << dir.get3DDataValue();
    }

    private static int getFacingMask(Direction dir) {
        return 1 << (dir.get3DDataValue() + 6);
    }

    private static VoxelShape createShape(Direction facing, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down) {
        VoxelShape base = Block.box(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);
        switch (facing) {
            case UP:
                base = Shapes.or(
                        base,
                        Block.box(6.0D, 10.0D, 6.0D, 10.0D, 14.0D, 10.0D),
                        Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D));
                break;
            case DOWN:
                base = Shapes.or(
                        base,
                        Block.box(6.0D, 2.0D, 6.0D, 10.0D, 6.0D, 10.0D),
                        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D));
                break;
            case NORTH:
                base = Shapes.or(
                        base,
                        Block.box(6.0D, 6.0D, 2.0D, 10.0D, 10.0D, 6.0D),
                        Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D));
                break;
            case SOUTH:
                base = Shapes.or(
                        base,
                        Block.box(6.0D, 6.0D, 10.0D, 10.0D, 10.0D, 14.0D),
                        Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D));
                break;
            case EAST:
                base = Shapes.or(
                        base,
                        Block.box(10.0D, 6.0D, 6.0D, 14.0D, 10.0D, 10.0D),
                        Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D));
                break;
            case WEST:
                base = Shapes.or(
                        base,
                        Block.box(2.0D, 6.0D, 6.0D, 6.0D, 10.0D, 10.0D),
                        Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D));
                break;
        }

        int offset = 2;
        VoxelShape northC = Block.box(8 - offset, 8 - offset, 0, 8 + offset, 8 + offset, 8 + offset);
        VoxelShape eastC = Block.box(8 - offset, 8 - offset, 8 - offset, 16, 8 + offset, 8 + offset);
        VoxelShape southC = Block.box(8 - offset, 8 - offset, 8 - offset, 8 + offset, 8 + offset, 16);
        VoxelShape westC = Block.box(0, 8 - offset, 8 - offset, 8 + offset, 8 + offset, 8 + offset);
        VoxelShape upC = Block.box(8 - offset, 8 - offset, 8 - offset, 8 + offset, 16, 8 + offset);
        VoxelShape downC = Block.box(8 - offset, 0, 8 - offset, 8 + offset, 8 + offset, 8 + offset);

        if (north)
            base = Shapes.or(base, northC);
        if (south)
            base = Shapes.or(base, southC);
        if (east)
            base = Shapes.or(base, eastC);
        if (west)
            base = Shapes.or(base, westC);
        if (up)
            base = Shapes.or(base, upC);
        if (down)
            base = Shapes.or(base, downC);

        return base;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getShape(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getShape(state);
    }

    private VoxelShape getShape(BlockState state) {
        int index = getShapeIndex(state);
        if (shape[index] != null) {
            return shape[index];
        }
        return shape[index] = createShape(state.getValue(FACING), state.getValue(NORTH), state.getValue(SOUTH), state.getValue(EAST), state.getValue(WEST), state.getValue(UP), state.getValue(DOWN));
    }

    private int getShapeIndex(BlockState state) {
        return this.SHAPE_INDEX_CACHE.computeIntIfAbsent(state, (blockState) -> {
            int i = 0;
            if (blockState.getValue(NORTH))
                i |= getDirectionMask(Direction.NORTH);
            if (blockState.getValue(EAST))
                i |= getDirectionMask(Direction.EAST);
            if (blockState.getValue(SOUTH))
                i |= getDirectionMask(Direction.SOUTH);
            if (blockState.getValue(WEST))
                i |= getDirectionMask(Direction.WEST);
            if (blockState.getValue(UP))
                i |= getDirectionMask(Direction.UP);
            if (blockState.getValue(DOWN))
                i |= getDirectionMask(Direction.DOWN);
            if (blockState.getValue(FACING).equals(Direction.NORTH))
                i |= getFacingMask(Direction.NORTH);
            if (blockState.getValue(FACING).equals(Direction.SOUTH))
                i |= getFacingMask(Direction.SOUTH);
            if (blockState.getValue(FACING).equals(Direction.EAST))
                i |= getFacingMask(Direction.EAST);
            if (blockState.getValue(FACING).equals(Direction.WEST))
                i |= getFacingMask(Direction.WEST);
            if (blockState.getValue(FACING).equals(Direction.UP))
                i |= getFacingMask(Direction.UP);
            if (blockState.getValue(FACING).equals(Direction.DOWN))
                i |= getFacingMask(Direction.DOWN);
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
        throw new IllegalArgumentException();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180:
                return state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST)).setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90:
                return state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
            case CLOCKWISE_90:
                return state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH)).setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
            default:
                return state;
        }
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT:
                return state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
            case FRONT_BACK:
                return state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
            default:
                return super.mirror(state, mirror);
        }
    }

    public boolean canConnect(BlockState state, BlockState neighborState, BlockPos pos, BlockPos neighborPos, LevelAccessor world, Direction facing) {
        try {
            if (pos.relative(state.getValue(FACING)).equals(neighborPos))
                return false;
            if (neighborPos.relative(neighborState.getValue(FACING)).equals(pos))
                return false;
        } catch (IllegalArgumentException ignored) {}
        // TODO: The WireBlockEntity will still connect on the top face of this block (there's no wire there)
        return neighborState.getBlock() instanceof WireBlock || EnergyUtils.canAccessEnergy(world.getBlockEntity(pos).getLevel(), pos.relative(facing), facing.getOpposite());
    }

    @Override
    public BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        for (Direction direction : Direction.values()) {
            state = state.setValue(getPropForDir(direction), this.canConnect(state,
                    context.getLevel().getBlockState(context.getClickedPos().relative(direction)),
                    context.getClickedPos(),
                    context.getClickedPos().relative(direction),
                    context.getLevel(),
                    direction));
        }
        return state
                .setValue(FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(FLUID, Registry.FLUID.getKey(fluidState.getType()))
                .setValue(FlowingFluid.LEVEL, Math.max(fluidState.getAmount(), 1));
    }

    public BlockState updateShape(BlockState state, Direction facing, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
           if (!state.getValue(FLUID).equals(Constants.Misc.EMPTY)) {
            world.getLiquidTicks().scheduleTick(pos, Registry.FLUID.get(state.getValue(FLUID)), Registry.FLUID.get(state.getValue(FLUID)).getTickDelay(world));
        }
        return state.setValue(getPropForDir(facing), this.canConnect(state, neighborState, pos, neighborPos, world, facing));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        FluidState state1 = Registry.FLUID.get(state.getValue(FLUID)).defaultFluidState();
        if (state1.getValues().containsKey(FlowingFluid.LEVEL)) {
            state1 = state1.setValue(FlowingFluid.LEVEL, state.getValue(FlowingFluid.LEVEL));
        }
        return state1;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(NORTH, EAST, WEST, SOUTH, UP, DOWN, FACING, FLUID, FlowingFluid.LEVEL);
    }
}