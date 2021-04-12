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

package com.hrznstudio.galacticraft.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConnectingBlockUtils {
    private ConnectingBlockUtils() {
    }

    public static final BooleanProperty ATTACHED_NORTH = BooleanProperty.create("attached_north");
    public static final BooleanProperty ATTACHED_EAST = BooleanProperty.create("attached_east");
    public static final BooleanProperty ATTACHED_SOUTH = BooleanProperty.create("attached_south");
    public static final BooleanProperty ATTACHED_WEST = BooleanProperty.create("attached_west");
    public static final BooleanProperty ATTACHED_UP = BooleanProperty.create("attached_up");
    public static final BooleanProperty ATTACHED_DOWN = BooleanProperty.create("attached_down");

    public static BooleanProperty getBooleanProperty(Direction dir) {
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

    public static VoxelShape getVoxelShape(BlockState blockState, VoxelShape north, VoxelShape south, VoxelShape east, VoxelShape west, VoxelShape up, VoxelShape down, VoxelShape none) {
        VoxelShape shape = none;

        if (blockState.getValue(ATTACHED_NORTH)) {
            shape = Shapes.join(shape, north, BooleanOp.OR);
        }
        if (blockState.getValue(ATTACHED_SOUTH)) {
            shape = Shapes.join(shape, south, BooleanOp.OR);
        }
        if (blockState.getValue(ATTACHED_EAST)) {
            shape = Shapes.join(shape, east, BooleanOp.OR);
        }
        if (blockState.getValue(ATTACHED_WEST)) {
            shape = Shapes.join(shape, west, BooleanOp.OR);
        }
        if (blockState.getValue(ATTACHED_UP)) {
            shape = Shapes.join(shape, up, BooleanOp.OR);
        }
        if (blockState.getValue(ATTACHED_DOWN)) {
            shape = Shapes.join(shape, down, BooleanOp.OR);
        }
        return shape;
    }
}
