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

package dev.galacticraft.mod.util;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ConnectingBlockUtils {
    private ConnectingBlockUtils() {
    }

    public static final BooleanProperty ATTACHED_NORTH = BooleanProperty.of("attached_north");
    public static final BooleanProperty ATTACHED_EAST = BooleanProperty.of("attached_east");
    public static final BooleanProperty ATTACHED_SOUTH = BooleanProperty.of("attached_south");
    public static final BooleanProperty ATTACHED_WEST = BooleanProperty.of("attached_west");
    public static final BooleanProperty ATTACHED_UP = BooleanProperty.of("attached_up");
    public static final BooleanProperty ATTACHED_DOWN = BooleanProperty.of("attached_down");

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

        if (blockState.get(ATTACHED_NORTH)) {
            shape = VoxelShapes.combineAndSimplify(shape, north, BooleanBiFunction.OR);
        }
        if (blockState.get(ATTACHED_SOUTH)) {
            shape = VoxelShapes.combineAndSimplify(shape, south, BooleanBiFunction.OR);
        }
        if (blockState.get(ATTACHED_EAST)) {
            shape = VoxelShapes.combineAndSimplify(shape, east, BooleanBiFunction.OR);
        }
        if (blockState.get(ATTACHED_WEST)) {
            shape = VoxelShapes.combineAndSimplify(shape, west, BooleanBiFunction.OR);
        }
        if (blockState.get(ATTACHED_UP)) {
            shape = VoxelShapes.combineAndSimplify(shape, up, BooleanBiFunction.OR);
        }
        if (blockState.get(ATTACHED_DOWN)) {
            shape = VoxelShapes.combineAndSimplify(shape, down, BooleanBiFunction.OR);
        }
        return shape;
    }
}
