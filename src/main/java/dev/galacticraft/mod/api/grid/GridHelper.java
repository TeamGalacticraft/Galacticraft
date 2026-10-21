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

package dev.galacticraft.mod.api.grid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Position helpers for grids, where every edge runs along one axis.
 * Ported from ThermalDynamics' {@code GridHelper}.
 */
public final class GridHelper {
    private GridHelper() {
    }

    /**
     * {@return the duct at the given position, or {@code null} if there is none or its chunk is not loaded}
     * Never loads a chunk.
     */
    public static @Nullable Duct<?, ?> getDuct(Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return null;
        }
        return level.getBlockEntity(pos) instanceof Duct<?, ?> duct ? duct : null;
    }

    /**
     * {@return the positions strictly between {@code a} and {@code b}, which must be on the same axis}
     */
    public static Iterable<BlockPos> positionsBetween(BlockPos a, BlockPos b) {
        assert isOnSameAxis(a, b);
        if (a.equals(b)) {
            return java.util.List.of();
        }
        Direction dir = getSide(a, b);
        return () -> new Iterator<>() {
            private BlockPos next = a.relative(dir);

            @Override
            public boolean hasNext() {
                return !this.next.equals(b);
            }

            @Override
            public BlockPos next() {
                if (!this.hasNext()) throw new NoSuchElementException();
                BlockPos current = this.next;
                this.next = current.relative(dir);
                return current;
            }
        };
    }

    /**
     * {@return the number of positions strictly between {@code a} and {@code b}}
     */
    public static int numBetween(BlockPos a, BlockPos b) {
        return a.distManhattan(b) - 1;
    }

    /**
     * {@return whether {@code middle} is strictly between {@code a} and {@code b} on their axis}
     */
    public static boolean isOnEdge(BlockPos middle, BlockPos a, BlockPos b) {
        assert isOnSameAxis(a, b);
        if (a.getX() != b.getX()) {
            return middle.getY() == a.getY() && middle.getZ() == a.getZ() && betweenExclusive(middle.getX(), a.getX(), b.getX());
        }
        if (a.getY() != b.getY()) {
            return middle.getZ() == a.getZ() && middle.getX() == a.getX() && betweenExclusive(middle.getY(), a.getY(), b.getY());
        }
        if (a.getZ() != b.getZ()) {
            return middle.getX() == a.getX() && middle.getY() == a.getY() && betweenExclusive(middle.getZ(), a.getZ(), b.getZ());
        }
        throw new IllegalArgumentException("A == B");
    }

    private static boolean betweenExclusive(int v, int a, int b) {
        return a < b ? a < v && v < b : b < v && v < a;
    }

    /**
     * {@return whether the two positions share at least two coordinates}
     */
    public static boolean isOnSameAxis(BlockPos a, BlockPos b) {
        boolean x = a.getX() == b.getX();
        boolean y = a.getY() == b.getY();
        boolean z = a.getZ() == b.getZ();
        return (x && y) || (x && z) || (y && z);
    }

    /**
     * {@return the position next to {@code from}, one step towards {@code towards}}
     */
    public static BlockPos stepTowards(BlockPos from, BlockPos towards) {
        return from.relative(getSide(from, towards));
    }

    /**
     * {@return the direction from {@code from} to {@code to}, which must be on the same axis}
     */
    public static Direction getSide(BlockPos from, BlockPos to) {
        int dx = Integer.signum(to.getX() - from.getX());
        int dy = Integer.signum(to.getY() - from.getY());
        int dz = Integer.signum(to.getZ() - from.getZ());
        Direction dir = Direction.fromDelta(dx, dy, dz);
        if (dir == null) {
            throw new IllegalArgumentException("Positions " + from + " and " + to + " are not on one axis");
        }
        return dir;
    }
}
