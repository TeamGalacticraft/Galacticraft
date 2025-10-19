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

package dev.galacticraft.mod.world.gen.dungeon.util;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import java.util.Objects;

/**
 * Sparse voxel bitmask for worldgen.
 * Backed by a LongOpenHashSet of BlockPos.asLong().
 * <p>
 * Features:
 * - add/contains/size/clear
 * - union/combine
 * - dilation (Chebyshev distance) to create a "safety buffer" (e.g., margin=1)
 * - iteration via forEachLong()
 */
public final class Bitmask {
    private final LongOpenHashSet set;

    public Bitmask() {
        this.set = new LongOpenHashSet();
    }

    private Bitmask(LongOpenHashSet backing) {
        this.set = backing;
    }

    /**
     * Returns a NEW Bitmask that is the union of a and b.
     */
    public static Bitmask combine(Bitmask a, Bitmask b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        LongOpenHashSet s = new LongOpenHashSet(a.set);
        s.addAll(b.set);
        return new Bitmask(s);
    }

    public void add(BlockPos p) {
        set.add(p.asLong());
    }

    public void add(long packedPos) {
        set.add(packedPos);
    }

    public void add(AABB aabb) {
        int minX = (int) Math.floor(aabb.minX);
        int minY = (int) Math.floor(aabb.minY);
        int minZ = (int) Math.floor(aabb.minZ);
        int maxX = (int) Math.ceil(aabb.maxX) - 1;
        int maxY = (int) Math.ceil(aabb.maxY) - 1;
        int maxZ = (int) Math.ceil(aabb.maxZ) - 1;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    this.set.add(BlockPos.asLong(x, y, z));
                }
            }
        }
    }

    public void add(Bitmask mask) {
        if (mask == null) return;
        this.set.addAll(mask.set);
    }

    public void addAll(Bitmask other) {
        this.set.addAll(other.set);
    }

    public boolean contains(BlockPos p) {
        return set.contains(p.asLong());
    }

    public boolean contains(long packedPos) {
        return set.contains(packedPos);
    }

    public boolean contains(AABB aabb) {
        int minX = (int) Math.floor(aabb.minX);
        int minY = (int) Math.floor(aabb.minY);
        int minZ = (int) Math.floor(aabb.minZ);
        int maxX = (int) Math.ceil(aabb.maxX) - 1;
        int maxY = (int) Math.ceil(aabb.maxY) - 1;
        int maxZ = (int) Math.ceil(aabb.maxZ) - 1;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (this.set.contains(BlockPos.asLong(x, y, z))) return true;
                }
            }
        }
        return false;
    }

    public int size() {
        return set.size();
    }

    public void clear() {
        set.clear();
    }

    /**
     * Create a NEW Bitmask dilated by Chebyshev distance "margin".
     * margin=1 pads every filled voxel by its 26 neighbors.
     */
    public Bitmask dilated(int margin) {
        if (margin <= 0) return copy();
        LongOpenHashSet out = new LongOpenHashSet(Math.max(16, set.size() * 27));
        LongIterator it = set.iterator();
        while (it.hasNext()) {
            long packed = it.nextLong();
            int x = BlockPos.getX(packed);
            int y = BlockPos.getY(packed);
            int z = BlockPos.getZ(packed);
            for (int dx = -margin; dx <= margin; dx++) {
                for (int dy = -margin; dy <= margin; dy++) {
                    for (int dz = -margin; dz <= margin; dz++) {
                        out.add(BlockPos.asLong(x + dx, y + dy, z + dz));
                    }
                }
            }
        }
        return new Bitmask(out);
    }

    public Bitmask copy() {
        return new Bitmask(new LongOpenHashSet(this.set));
    }

    /**
     * Iterate packed long positions (BlockPos.asLong()).
     */
    public void forEachLong(java.util.function.LongConsumer consumer) {
        LongIterator it = set.iterator();
        while (it.hasNext()) consumer.accept(it.nextLong());
    }
}