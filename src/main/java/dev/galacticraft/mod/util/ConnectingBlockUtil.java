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

package dev.galacticraft.mod.util;

import dev.galacticraft.mod.Constant;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

public class ConnectingBlockUtil {
    public static final VoxelShape WALKWAY_TOP = Block.box(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);

    public static final Map<Direction, VoxelShape> WALKWAY_SHAPES = Util.make(new EnumMap<>(Direction.class), map -> {
        map.put(Direction.UP, Shapes.or(
                WALKWAY_TOP,
                Block.box(6.0D, 10.0D, 6.0D, 10.0D, 14.0D, 10.0D),
                Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D))
        );
        map.put(Direction.DOWN, Shapes.or(
                WALKWAY_TOP,
                Block.box(6.0D, 2.0D, 6.0D, 10.0D, 6.0D, 10.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D))
        );
        map.put(Direction.NORTH, Shapes.or(
                WALKWAY_TOP,
                Block.box(6.0D, 6.0D, 2.0D, 10.0D, 10.0D, 6.0D),
                Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D))
        );
        map.put(Direction.SOUTH, Shapes.or(
                WALKWAY_TOP,
                Block.box(6.0D, 6.0D, 10.0D, 10.0D, 10.0D, 14.0D),
                Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D))
        );
        map.put(Direction.EAST, Shapes.or(
                WALKWAY_TOP,
                Block.box(10.0D, 6.0D, 6.0D, 14.0D, 10.0D, 10.0D),
                Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D))
        );
        map.put(Direction.WEST, Shapes.or(
                WALKWAY_TOP,
                Block.box(2.0D, 6.0D, 6.0D, 6.0D, 10.0D, 10.0D),
                Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D))
        );
    });

    private ConnectingBlockUtil() {
    }

    public static BooleanProperty getBooleanProperty(Direction dir) {
        return switch (dir) {
            case SOUTH -> BlockStateProperties.SOUTH;
            case EAST -> BlockStateProperties.EAST;
            case WEST -> BlockStateProperties.WEST;
            case NORTH -> BlockStateProperties.NORTH;
            case UP -> BlockStateProperties.UP;
            case DOWN -> BlockStateProperties.DOWN;
        };
    }

    public static BlockState rotateConnections(BlockState state, Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 ->
                    state.setValue(BlockStateProperties.NORTH, state.getValue(BlockStateProperties.SOUTH)).setValue(BlockStateProperties.EAST, state.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.SOUTH, state.getValue(BlockStateProperties.NORTH)).setValue(BlockStateProperties.WEST, state.getValue(BlockStateProperties.EAST));
            case COUNTERCLOCKWISE_90 ->
                    state.setValue(BlockStateProperties.NORTH, state.getValue(BlockStateProperties.EAST)).setValue(BlockStateProperties.EAST, state.getValue(BlockStateProperties.SOUTH)).setValue(BlockStateProperties.SOUTH, state.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.WEST, state.getValue(BlockStateProperties.NORTH));
            case CLOCKWISE_90 ->
                    state.setValue(BlockStateProperties.NORTH, state.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.EAST, state.getValue(BlockStateProperties.NORTH)).setValue(BlockStateProperties.SOUTH, state.getValue(BlockStateProperties.EAST)).setValue(BlockStateProperties.WEST, state.getValue(BlockStateProperties.SOUTH));
            default -> state;
        };
    }

    public static BlockState mirror(BlockState state, Mirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT ->
                    state.setValue(BlockStateProperties.NORTH, state.getValue(BlockStateProperties.SOUTH)).setValue(BlockStateProperties.SOUTH, state.getValue(BlockStateProperties.NORTH));
            case FRONT_BACK ->
                    state.setValue(BlockStateProperties.EAST, state.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.WEST, state.getValue(BlockStateProperties.EAST));
            case NONE -> state;
        };
    }

    public static VoxelShape[] generateShapeCache(VoxelShape baseShape, VoxelShape[] outerShapes) {
        VoxelShape[] shapes = new VoxelShape[64];

        for (int j = 0; j < 64; j++) {
            VoxelShape shape = baseShape;

            for (int k = 0; k < Constant.Misc.DIRECTIONS.length; k++) {
                if ((j & 1 << k) != 0) {
                    shape = Shapes.or(shape, outerShapes[k]);
                }
            }
            shapes[j] = shape;
        }
        return shapes;
    }

    public static int generateAABBIndex(BlockState state) {
        int i = 0;

        for (int j = 0; j < Constant.Misc.DIRECTIONS.length; j++) {
            if (state.getValue(getBooleanProperty(Constant.Misc.DIRECTIONS[j]))) {
                i |= 1 << j;
            }
        }

        return i;
    }
}
