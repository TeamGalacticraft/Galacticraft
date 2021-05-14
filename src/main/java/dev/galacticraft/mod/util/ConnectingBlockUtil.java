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

package dev.galacticraft.mod.util;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class ConnectingBlockUtil {
    private ConnectingBlockUtil() {}

    public static BooleanProperty getBooleanProperty(Direction dir) {
        switch (dir) {
            case SOUTH:
                return Properties.SOUTH;
            case EAST:
                return Properties.EAST;
            case WEST:
                return Properties.WEST;
            case NORTH:
                return Properties.NORTH;
            case UP:
                return Properties.UP;
            case DOWN:
                return Properties.DOWN;
            default:
                throw new IllegalArgumentException("cannot be null");
        }
    }

    public static VoxelShape getVoxelShape(BlockState blockState, VoxelShape north, VoxelShape south, VoxelShape east, VoxelShape west, VoxelShape up, VoxelShape down, VoxelShape none) {
        VoxelShape shape = none;

        if (blockState.get(Properties.NORTH)) {
            shape = VoxelShapes.combineAndSimplify(shape, north, BooleanBiFunction.OR);
        }
        if (blockState.get(Properties.SOUTH)) {
            shape = VoxelShapes.combineAndSimplify(shape, south, BooleanBiFunction.OR);
        }
        if (blockState.get(Properties.EAST)) {
            shape = VoxelShapes.combineAndSimplify(shape, east, BooleanBiFunction.OR);
        }
        if (blockState.get(Properties.WEST)) {
            shape = VoxelShapes.combineAndSimplify(shape, west, BooleanBiFunction.OR);
        }
        if (blockState.get(Properties.UP)) {
            shape = VoxelShapes.combineAndSimplify(shape, up, BooleanBiFunction.OR);
        }
        if (blockState.get(Properties.DOWN)) {
            shape = VoxelShapes.combineAndSimplify(shape, down, BooleanBiFunction.OR);
        }
        return shape;
    }
}
