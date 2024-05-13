/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import dev.galacticraft.mod.api.block.entity.Connected;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConnectingBlockUtil {
    public static final VoxelShape WALKWAY_TOP = Block.box(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);
    private ConnectingBlockUtil() {}

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

    public static VoxelShape getVoxelShape(BlockState blockState, VoxelShape north, VoxelShape south, VoxelShape east, VoxelShape west, VoxelShape up, VoxelShape down, VoxelShape none) {
        VoxelShape shape = none;

        if (blockState.getValue(BlockStateProperties.NORTH)) {
            shape = Shapes.join(shape, north, BooleanOp.OR);
        }
        if (blockState.getValue(BlockStateProperties.SOUTH)) {
            shape = Shapes.join(shape, south, BooleanOp.OR);
        }
        if (blockState.getValue(BlockStateProperties.EAST)) {
            shape = Shapes.join(shape, east, BooleanOp.OR);
        }
        if (blockState.getValue(BlockStateProperties.WEST)) {
            shape = Shapes.join(shape, west, BooleanOp.OR);
        }
        if (blockState.getValue(BlockStateProperties.UP)) {
            shape = Shapes.join(shape, up, BooleanOp.OR);
        }
        if (blockState.getValue(BlockStateProperties.DOWN)) {
            shape = Shapes.join(shape, down, BooleanOp.OR);
        }
        return shape;
    }

    public static VoxelShape getVoxelShape(Connected connected, VoxelShape north, VoxelShape south, VoxelShape east, VoxelShape west, VoxelShape up, VoxelShape down, VoxelShape none) {
        VoxelShape shape = none;

        final boolean[] connections = connected.getConnections();
        if (connections[2]) {
            shape = Shapes.join(shape, north, BooleanOp.OR);
        }
        if (connections[3]) {
            shape = Shapes.join(shape, south, BooleanOp.OR);
        }
        if (connections[5]) {
            shape = Shapes.join(shape, east, BooleanOp.OR);
        }
        if (connections[4]) {
            shape = Shapes.join(shape, west, BooleanOp.OR);
        }
        if (connections[1]) {
            shape = Shapes.join(shape, up, BooleanOp.OR);
        }
        if (connections[0]) {
            shape = Shapes.join(shape, down, BooleanOp.OR);
        }
        return shape;
    }

    public static VoxelShape createWalkwayShape(Direction facing) {
        return switch (facing) {
            case UP -> Shapes.or(
                    WALKWAY_TOP,
                    Block.box(6.0D, 10.0D, 6.0D, 10.0D, 14.0D, 10.0D),
                    Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D));
            case DOWN -> Shapes.or(
                    WALKWAY_TOP,
                    Block.box(6.0D, 2.0D, 6.0D, 10.0D, 6.0D, 10.0D),
                    Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D));
            case NORTH -> Shapes.or(
                    WALKWAY_TOP,
                    Block.box(6.0D, 6.0D, 2.0D, 10.0D, 10.0D, 6.0D),
                    Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D));
            case SOUTH -> Shapes.or(
                    WALKWAY_TOP,
                    Block.box(6.0D, 6.0D, 10.0D, 10.0D, 10.0D, 14.0D),
                    Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D));
            case EAST -> Shapes.or(
                    WALKWAY_TOP,
                    Block.box(10.0D, 6.0D, 6.0D, 14.0D, 10.0D, 10.0D),
                    Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D));
            case WEST -> Shapes.or(
                    WALKWAY_TOP,
                    Block.box(2.0D, 6.0D, 6.0D, 6.0D, 10.0D, 10.0D),
                    Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D));
        };
    }

    public static BlockState rotateConnections(BlockState state, Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 -> state.setValue(BlockStateProperties.NORTH, state.getValue(BlockStateProperties.SOUTH)).setValue(BlockStateProperties.EAST, state.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.SOUTH, state.getValue(BlockStateProperties.NORTH)).setValue(BlockStateProperties.WEST, state.getValue(BlockStateProperties.EAST));
            case COUNTERCLOCKWISE_90 -> state.setValue(BlockStateProperties.NORTH, state.getValue(BlockStateProperties.EAST)).setValue(BlockStateProperties.EAST, state.getValue(BlockStateProperties.SOUTH)).setValue(BlockStateProperties.SOUTH, state.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.WEST, state.getValue(BlockStateProperties.NORTH));
            case CLOCKWISE_90 -> state.setValue(BlockStateProperties.NORTH, state.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.EAST, state.getValue(BlockStateProperties.NORTH)).setValue(BlockStateProperties.SOUTH, state.getValue(BlockStateProperties.EAST)).setValue(BlockStateProperties.WEST, state.getValue(BlockStateProperties.SOUTH));
            default -> state;
        };
    }

    public static BlockState mirror(BlockState state, Mirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT -> state.setValue(BlockStateProperties.NORTH, state.getValue(BlockStateProperties.SOUTH)).setValue(BlockStateProperties.SOUTH, state.getValue(BlockStateProperties.NORTH));
            case FRONT_BACK -> state.setValue(BlockStateProperties.EAST, state.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.WEST, state.getValue(BlockStateProperties.EAST));
            case NONE -> state;
        };
    }
}
