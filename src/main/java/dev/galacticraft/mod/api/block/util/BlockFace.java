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

package dev.galacticraft.mod.api.block.util;

import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public enum BlockFace {
    FRONT,
    RIGHT,
    BACK,
    LEFT,
    TOP,
    BOTTOM;

    @NotNull
    public static BlockFace toFace(Direction facing, Direction target) {
        assert facing == Direction.NORTH || facing == Direction.SOUTH || facing == Direction.EAST || facing == Direction.WEST;

        if (target == Direction.DOWN) {
            return BOTTOM;
        } else if (target == Direction.UP) {
            return TOP;
        }

        switch (facing) {
            case NORTH:
                switch (target) {
                    case NORTH:
                        return FRONT;
                    case EAST:
                        return RIGHT;
                    case SOUTH:
                        return BACK;
                    case WEST:
                        return LEFT;
                }
                break;
            case EAST:
                switch (target) {
                    case EAST:
                        return FRONT;
                    case NORTH:
                        return LEFT;
                    case WEST:
                        return BACK;
                    case SOUTH:
                        return RIGHT;
                }
                break;
            case SOUTH:
                switch (target) {
                    case SOUTH:
                        return FRONT;
                    case WEST:
                        return RIGHT;
                    case NORTH:
                        return BACK;
                    case EAST:
                        return LEFT;
                }
                break;
            case WEST:
                switch (target) {
                    case WEST:
                        return FRONT;
                    case SOUTH:
                        return LEFT;
                    case EAST:
                        return BACK;
                    case NORTH:
                        return RIGHT;
                }
                break;
        }

        throw new RuntimeException();
    }

    @NotNull
    public Direction toDirection(Direction facing) {
        assert facing == Direction.NORTH || facing == Direction.SOUTH || facing == Direction.EAST || facing == Direction.WEST;

        if (this == BOTTOM) {
            return Direction.DOWN;
        } else if (this == TOP) {
            return Direction.UP;
        }

        switch (facing) {
            case NORTH:
                switch (this) {
                    case FRONT:
                        return Direction.NORTH;
                    case RIGHT:
                        return Direction.EAST;
                    case BACK:
                        return Direction.SOUTH;
                    case LEFT:
                        return Direction.WEST;
                }
                break;
            case EAST:
                switch (this) {
                    case RIGHT:
                        return Direction.SOUTH;
                    case FRONT:
                        return Direction.EAST;
                    case LEFT:
                        return Direction.NORTH;
                    case BACK:
                        return Direction.WEST;
                }
                break;
            case SOUTH:
                switch (this) {
                    case BACK:
                        return Direction.NORTH;
                    case LEFT:
                        return Direction.EAST;
                    case FRONT:
                        return Direction.SOUTH;
                    case RIGHT:
                        return Direction.WEST;
                }
                break;
            case WEST:
                switch (this) {
                    case LEFT:
                        return Direction.SOUTH;
                    case BACK:
                        return Direction.EAST;
                    case RIGHT:
                        return Direction.NORTH;
                    case FRONT:
                        return Direction.WEST;
                }
                break;
        }

        throw new RuntimeException();
    }

    public BlockFace getOpposite() {
        switch (this) {
            case BOTTOM:
                return TOP;
            case TOP:
                return BOTTOM;
            case BACK:
                return FRONT;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            case FRONT:
                return BACK;
        }
        throw new RuntimeException();
    }
}
