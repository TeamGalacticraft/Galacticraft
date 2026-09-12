/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.client.model.vacuum_glass;

import dev.galacticraft.mod.client.model.VacuumGlassBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HelperMethods {
    /**
     * Builds a box expanded toward the supplied directions.
     *
     * @param directions directions to expand toward
     * @return expanded box definition
     */
    public static EmitHelper.Box getExpandedBox(Direction... directions) {
        float minX = VacuumGlassBakedModel.FRAME_INSET;
        float minY = VacuumGlassBakedModel.FRAME_INSET;
        float minZ = VacuumGlassBakedModel.FRAME_INSET;
        float maxX = VacuumGlassBakedModel.INVERTED_FRAME_INSET;
        float maxY = VacuumGlassBakedModel.INVERTED_FRAME_INSET;
        float maxZ = VacuumGlassBakedModel.INVERTED_FRAME_INSET;

        for (Direction direction : directions) {
            switch (direction) {
                case NORTH -> minZ = 0.0F;
                case SOUTH -> maxZ = 1.0F;
                case WEST -> minX = 0.0F;
                case EAST -> maxX = 1.0F;
                case DOWN -> minY = 0.0F;
                case UP -> maxY = 1.0F;
            }
        }

        return new EmitHelper.Box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Lightweight integer vector helper used for direction cross products.
     */
    public record Vec3iDir(int x, int y, int z) {
        static Vec3iDir from(Direction dir) {
            return new Vec3iDir(dir.getStepX(), dir.getStepY(), dir.getStepZ());
        }

        Vec3iDir cross(Vec3iDir other) {
            return new Vec3iDir(
                    this.y * other.z - this.z * other.y,
                    this.z * other.x - this.x * other.z,
                    this.x * other.y - this.y * other.x
            );
        }

        Direction toDirection() {
            for (Direction dir : Direction.values()) {
                if (dir.getStepX() == x && dir.getStepY() == y && dir.getStepZ() == z) {
                    return dir;
                }
            }
            throw new IllegalStateException("No cardinal direction for vector: " + this);
        }
    }

    /** Returns the axis not present in the supplied axis pair. */
    public static Direction.Axis remainingAxis(Direction.Axis a, Direction.Axis b) {
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (axis != a && axis != b) {
                return axis;
            }
        }
        throw new IllegalStateException("No remaining axis for " + a + " and " + b);
    }

    /** Returns the positive direction for an axis. */
    public static Direction positiveDirection(Direction.Axis axis) {
        return Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
    }

    /** Returns the negative direction for an axis. */
    public static Direction negativeDirection(Direction.Axis axis) {
        return Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
    }

    public static float outerCoord(Direction dir) {
        return switch (dir) {
            case WEST, DOWN, NORTH -> 0.0F;
            case EAST, UP, SOUTH -> 1.0F;
        };
    }

    public static float deepInset(Direction dir) {
        // WEST/NORTH/DOWN -> I
        // EAST/SOUTH/UP   -> F
        return outerCoord(dir) - dirStep(dir) * VacuumGlassBakedModel.INVERTED_FRAME_INSET;
    }

    public static float shallowInset(Direction dir) {
        // WEST/NORTH/DOWN -> F
        // EAST/SOUTH/UP   -> I
        return outerCoord(dir) - dirStep(dir) * VacuumGlassBakedModel.FRAME_INSET;
    }

    /** Returns the signed step for a direction as {@code -1} or {@code 1}. */
    private static int dirStep(Direction dir) {
        return switch (dir) {
            case WEST, DOWN, NORTH -> -1;
            case EAST, UP, SOUTH -> 1;
        };
    }

    /** Sets the value for the supplied axis on an XYZ coordinate array. */
    private static void setAxis(float[] xyz, Direction axisDir, float value) {
        switch (axisDir.getAxis()) {
            case X -> xyz[0] = value;
            case Y -> xyz[1] = value;
            case Z -> xyz[2] = value;
        }
    }

    public static void setAxis(float[] xyz, Direction.Axis axis, float value) {
        switch (axis) {
            case X -> xyz[0] = value;
            case Y -> xyz[1] = value;
            case Z -> xyz[2] = value;
        }
    }

    /** Returns the arithmetic center of a collection of XYZ float points. */
    public static float[] averagePoints(float[]... points) {
        float x = 0.0F;
        float y = 0.0F;
        float z = 0.0F;

        for (float[] p : points) {
            x += p[0];
            y += p[1];
            z += p[2];
        }

        float inv = 1.0F / points.length;
        return new float[] { x * inv, y * inv, z * inv };
    }

    public static @Nullable VacuumGlassBakedModel.DirectionPair findOppositePair(Direction[] dirs) {
        for (int i = 0; i < dirs.length; i++) {
            for (int j = i + 1; j < dirs.length; j++) {
                if (dirs[i].getOpposite() == dirs[j]) {
                    return new VacuumGlassBakedModel.DirectionPair(dirs[i], dirs[j]);
                }
            }
        }
        return null;
    }

    /**
     * Returns the direction from the array that is not equal to either of the
     * supplied directions.
     *
     * @param dirs source directions
     * @param a first direction to exclude
     * @param b second direction to exclude
     * @return the remaining direction
     */
    public static Direction findRemainingDirection(Direction[] dirs, Direction a, Direction b) {
        for (Direction dir : dirs) {
            if (dir != a && dir != b) return dir;
        }
        throw new IllegalStateException("No remaining direction");
    }

    /**
     * Returns the directions from the set that are not the supplied excluded pair.
     */
    public static Direction[] findRemainingDirections(Direction[] dirs, Direction excludedA, Direction excludedB) {
        Direction[] result = new Direction[2];
        int index = 0;

        for (Direction dir : dirs) {
            if (dir != excludedA && dir != excludedB) {
                if (index >= 2) {
                    throw new IllegalStateException("More than two remaining directions found.");
                }
                result[index++] = dir;
            }
        }

        if (index != 2) {
            throw new IllegalStateException("Expected two remaining directions, found " + index);
        }

        return result;
    }

    /** Returns the direction step as a {@link Vec3}. */
    public static Vec3 dirVec(Direction dir) {
        return new Vec3(dir.getStepX(), dir.getStepY(), dir.getStepZ());
    }
}
