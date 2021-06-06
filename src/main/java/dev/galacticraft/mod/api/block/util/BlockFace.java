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

import dev.galacticraft.mod.Constant;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public enum BlockFace {
    FRONT(new TranslatableText("ui.galacticraft.machine.configuration.front"), true),
    RIGHT(new TranslatableText("ui.galacticraft.machine.configuration.right"), true),
    BACK(new TranslatableText("ui.galacticraft.machine.configuration.back"), true),
    LEFT(new TranslatableText("ui.galacticraft.machine.configuration.left"), true),
    TOP(new TranslatableText("ui.galacticraft.machine.configuration.top"), false),
    BOTTOM(new TranslatableText("ui.galacticraft.machine.configuration.bottom"), false);

    private final MutableText name;
    private final boolean horizontal;

    BlockFace(MutableText name, boolean horizontal) {
        this.name = name.setStyle(Constant.Text.GOLD_STYLE);
        this.horizontal = horizontal;
    }

    public Text getName() {
        return name;
    }

    @NotNull
    public static BlockFace toFace(Direction facing, Direction target) {
        assert facing.getAxis() != Direction.Axis.Y;

        if (target == Direction.DOWN) {
            return BOTTOM;
        } else if (target == Direction.UP) {
            return TOP;
        }

        return switch (facing) {
            case NORTH -> switch (target) {
                case NORTH -> FRONT;
                case EAST -> RIGHT;
                case SOUTH -> BACK;
                case WEST -> LEFT;
                default -> throw new IllegalStateException("Unexpected value: " + target);
            };
            case EAST -> switch (target) {
                case EAST -> FRONT;
                case NORTH -> LEFT;
                case WEST -> BACK;
                case SOUTH -> RIGHT;
                default -> throw new IllegalStateException("Unexpected value: " + target);
            };
            case SOUTH -> switch (target) {
                case SOUTH -> FRONT;
                case WEST -> RIGHT;
                case NORTH -> BACK;
                case EAST -> LEFT;
                default -> throw new IllegalStateException("Unexpected value: " + target);
            };
            case WEST -> switch (target) {
                case WEST -> FRONT;
                case SOUTH -> LEFT;
                case EAST -> BACK;
                case NORTH -> RIGHT;
                default -> throw new IllegalStateException("Unexpected value: " + target);
            };
            default -> throw new IllegalStateException("Unexpected value: " + target);
        };
    }

    @NotNull
    public Direction toDirection(Direction facing) {
        assert facing == Direction.NORTH || facing == Direction.SOUTH || facing == Direction.EAST || facing == Direction.WEST;

        if (this == BOTTOM) {
            return Direction.DOWN;
        } else if (this == TOP) {
            return Direction.UP;
        }

        return switch (facing) {
            case NORTH -> switch (this) {
                case FRONT -> Direction.NORTH;
                case RIGHT -> Direction.EAST;
                case BACK -> Direction.SOUTH;
                case LEFT -> Direction.WEST;
                default -> throw new IllegalStateException("Unexpected value: " + this);
            };
            case EAST -> switch (this) {
                case RIGHT -> Direction.SOUTH;
                case FRONT -> Direction.EAST;
                case LEFT -> Direction.NORTH;
                case BACK -> Direction.WEST;
                default -> throw new IllegalStateException("Unexpected value: " + this);
            };
            case SOUTH -> switch (this) {
                case BACK -> Direction.NORTH;
                case LEFT -> Direction.EAST;
                case FRONT -> Direction.SOUTH;
                case RIGHT -> Direction.WEST;
                default -> throw new IllegalStateException("Unexpected value: " + this);
            };
            case WEST -> switch (this) {
                case LEFT -> Direction.SOUTH;
                case BACK -> Direction.EAST;
                case RIGHT -> Direction.NORTH;
                case FRONT -> Direction.WEST;
                default -> throw new IllegalStateException("Unexpected value: " + this);
            };
            default -> throw new IllegalStateException("Unexpected value: " + facing);
        };
    }

    public BlockFace getOpposite() {
        return switch (this) {
            case BOTTOM -> TOP;
            case TOP -> BOTTOM;
            case BACK -> FRONT;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            case FRONT -> BACK;
        };
    }

    public boolean horizontal() {
        return this.horizontal;
    }

    public boolean vertical() {
        return !this.horizontal;
    }
}
