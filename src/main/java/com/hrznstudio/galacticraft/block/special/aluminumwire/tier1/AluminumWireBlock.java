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

package com.hrznstudio.galacticraft.block.special.aluminumwire.tier1;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.block.WireBlock;
import com.hrznstudio.galacticraft.util.EnergyUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import java.util.ArrayList;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class AluminumWireBlock extends WireBlock {

    // If we start at 8,8,8 and subtract/add to/from 8, we do operations starting from the centre.
    private static final int OFFSET = 2;
    private static final VoxelShape NORTH = box(8 - OFFSET, 8 - OFFSET, 0, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape EAST = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 16, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape SOUTH = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 16);
    private static final VoxelShape WEST = box(0, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape UP = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 16, 8 + OFFSET);
    private static final VoxelShape DOWN = box(8 - OFFSET, 0, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape NONE = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);    // 6x6x6 box in the center.
    private static final BooleanProperty ATTACHED_NORTH = BooleanProperty.create("attached_north");
    private static final BooleanProperty ATTACHED_EAST = BooleanProperty.create("attached_east");
    private static final BooleanProperty ATTACHED_SOUTH = BooleanProperty.create("attached_south");
    private static final BooleanProperty ATTACHED_WEST = BooleanProperty.create("attached_west");
    private static final BooleanProperty ATTACHED_UP = BooleanProperty.create("attached_up");
    private static final BooleanProperty ATTACHED_DOWN = BooleanProperty.create("attached_down");

    public AluminumWireBlock(Properties settings) {
        super(settings);
        registerDefaultState(this.getStateDefinition().any()
                .setValue(ATTACHED_NORTH, false).setValue(ATTACHED_EAST, false)
                .setValue(ATTACHED_SOUTH, false).setValue(ATTACHED_WEST, false)
                .setValue(ATTACHED_UP, false).setValue(ATTACHED_DOWN, false));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockView, BlockPos blockPos, CollisionContext context) {
        ArrayList<VoxelShape> shapes = new ArrayList<>();

        if (blockState.getValue(ATTACHED_NORTH)) {
            shapes.add(NORTH);
        }
        if (blockState.getValue(ATTACHED_SOUTH)) {
            shapes.add(SOUTH);
        }
        if (blockState.getValue(ATTACHED_EAST)) {
            shapes.add(EAST);
        }
        if (blockState.getValue(ATTACHED_WEST)) {
            shapes.add(WEST);
        }
        if (blockState.getValue(ATTACHED_UP)) {
            shapes.add(UP);
        }
        if (blockState.getValue(ATTACHED_DOWN)) {
            shapes.add(DOWN);
        }
        if (shapes.isEmpty()) {
            return NONE;
        } else {
            return Shapes.or(NONE, shapes.toArray(new VoxelShape[0]));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        for (Direction direction : Constants.Misc.DIRECTIONS) {
            BlockState block = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
            state = state.setValue(getPropForDirection(direction), !block.isAir() && (block.getBlock() instanceof WireBlock
                    || EnergyUtils.canAccessEnergy(context.getLevel(), context.getClickedPos().relative(direction), direction)));

        }

        return state;
    }

    private BooleanProperty getPropForDirection(Direction dir) {
        switch (dir) {
            case SOUTH:
                return ATTACHED_SOUTH;
            case EAST:
                return ATTACHED_EAST;
            case WEST:
                return ATTACHED_WEST;
            case NORTH:
                return ATTACHED_NORTH;
            case UP:
                return ATTACHED_UP;
            case DOWN:
                return ATTACHED_DOWN;
            default:
                throw new IllegalArgumentException("cannot be null");
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor world, BlockPos thisWire, BlockPos otherConnectable) {
        return state.setValue(getPropForDirection(dir), !(neighbor).isAir() && (neighbor.getBlock() instanceof WireBlock
                || EnergyUtils.canAccessEnergy(world.getBlockEntity(thisWire).getLevel(), otherConnectable, dir.getOpposite())
        ));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ATTACHED_NORTH, ATTACHED_EAST, ATTACHED_SOUTH, ATTACHED_WEST, ATTACHED_UP, ATTACHED_DOWN);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getShadeBrightness(BlockState state, BlockGetter view, BlockPos pos) {
        return 1.0F;
    }
}