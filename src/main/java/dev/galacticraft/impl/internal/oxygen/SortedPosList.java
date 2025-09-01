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

package dev.galacticraft.impl.internal.oxygen;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import net.minecraft.core.BlockPos;

import java.util.Arrays;

public class SortedPosList {
    private final BlockPos center;
    double[] distances; // alt: don't cache and just calculate from pos each time?
    private BlockPos[] positions;
    int size = 0;

    public SortedPosList(BlockPos center) {
        this.center = center;
        this.distances = new double[0];
        this.positions = new BlockPos[0];
    }

    // returns the index of the given position OR the index of where the position would be inserted (-ve)
    private int findIndex(BlockPos pos, double distance) {
        int i = binarySearch(distance);
        // negative: not found || equal: found immediately!
        if (i < 0 || this.positions[i].equals(pos)) return i;
        // many positions can have the same distance from the center, so check for the pos

        // search left
        for (int j = i - 1; j >= 0; j--) {
            if (this.distances[j] == distance) {
                if (this.positions[j].equals(pos)) {
                    return j;
                }
            } else {
                // now in a different distance group - no need to search further
                break;
            }
        }

        // search right
        for (int j = i + 1; j < this.size; j++) {
            if (this.distances[j] == distance) {
                if (this.positions[j].equals(pos)) {
                    return j;
                }
            } else {
                // now in a different distance group - no need to search further
                break;
            }
        }
        return -(i + 1);
    }

    protected int binarySearch(double distance) {
        return Arrays.binarySearch(this.distances, 0, this.size, distance);
    }

    public boolean add(BlockPos pos, double distance) {
        int i = this.findIndex(pos, distance);
        if (i >= 0) {
            return false;
        } else {
            int j = getInsertionPosition(i);
            this.addInternal(pos, distance, j);
            return true;
        }

    }

    public boolean add(BlockPos pos) {
        return this.add(pos, this.calculateDistanceSq(pos));
    }

    public BlockPos get(int index) {
        return this.positions[index];
    }

    public double getDistance(int index) {
        return this.distances[index];
    }

    public boolean remove(BlockPos pos) {
        return this.remove(pos, this.calculateDistanceSq(pos));
    }
    public boolean remove(BlockPos pos, double distance) {
        int i = this.findIndex(pos, distance);
        if (i >= 0) {
            this.remove(i);
            return true;
        } else {
            return false;
        }
    }

    public boolean contains(BlockPos pos) {
        return this.contains(pos, this.calculateDistanceSq(pos));
    }

    public boolean contains(BlockPos pos, double distance) {
        return this.findIndex(pos, distance) >= 0;
    }

    public int size() {
        return this.size;
    }

    public double calculateDistanceSq(BlockPos object) {
        return this.center.distToLowCornerSqr(object.getX(), object.getY() - 0.5, object.getZ());
    }

    private static int getInsertionPosition(int binarySearchResult) {
        return -binarySearchResult - 1;
    }

    private void grow(int minCapacity) {
        if (minCapacity > this.positions.length) {
            if (this.positions != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
                // grow at least ~ 1.5x each time.
                minCapacity = Math.max(Math.max(this.positions.length + (this.positions.length >> 1), minCapacity), 16);
            }

            double[] distances = new double[minCapacity];
            System.arraycopy(this.distances, 0, distances, 0, this.size);
            this.distances = distances;

            BlockPos[] positions = new BlockPos[minCapacity];
            System.arraycopy(this.positions, 0, positions, 0, this.size);
            this.positions = positions;
        }
    }

    private void addInternal(BlockPos object, double distance, int index) {
        this.grow(this.size + 1);
        if (index != this.size) {
            System.arraycopy(this.distances, index, this.distances, index + 1, this.size - index);
            System.arraycopy(this.positions, index, this.positions, index + 1, this.size - index);
        }

        this.distances[index] = distance;
        this.positions[index] = object;
        this.size++;
    }

    public void remove(int index) {
        this.size--;
        if (index != this.size) {
            System.arraycopy(this.distances, index + 1, this.distances, index, this.size - index);
            System.arraycopy(this.positions, index + 1, this.positions, index, this.size - index);
        }

        this.distances[this.size] = 0.0;
        this.positions[this.size] = null;
    }

    public void clear() {
        this.size = 0;
        Arrays.fill(this.distances, 0.0);
        Arrays.fill(this.positions, null);
    }
}