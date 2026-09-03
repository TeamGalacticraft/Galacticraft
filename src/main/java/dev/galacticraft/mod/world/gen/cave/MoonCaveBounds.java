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

package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

/**
 * Axis-aligned bounds for quickly clipping planned caves to chunks.
 */
public class MoonCaveBounds {
    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int minZ = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxY = Integer.MIN_VALUE;
    private int maxZ = Integer.MIN_VALUE;

    public void include(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = Math.min(this.minX, minX);
        this.minY = Math.min(this.minY, minY);
        this.minZ = Math.min(this.minZ, minZ);
        this.maxX = Math.max(this.maxX, maxX);
        this.maxY = Math.max(this.maxY, maxY);
        this.maxZ = Math.max(this.maxZ, maxZ);
    }

    public void includeRoom(BlockPos center, double rx, double ry, double rz, int padding) {
        this.include(
                (int) Math.floor(center.getX() - rx - padding),
                (int) Math.floor(center.getY() - ry - padding),
                (int) Math.floor(center.getZ() - rz - padding),
                (int) Math.ceil(center.getX() + rx + padding),
                (int) Math.ceil(center.getY() + ry + padding),
                (int) Math.ceil(center.getZ() + rz + padding)
        );
    }

    public void includeTunnel(BlockPos start, BlockPos end, double radius, double curve, int padding) {
        int p = (int) Math.ceil(radius + curve + padding);

        this.include(
                Math.min(start.getX(), end.getX()) - p,
                Math.min(start.getY(), end.getY()) - p,
                Math.min(start.getZ(), end.getZ()) - p,
                Math.max(start.getX(), end.getX()) + p,
                Math.max(start.getY(), end.getY()) + p,
                Math.max(start.getZ(), end.getZ()) + p
        );
    }

    public boolean intersectsChunk(ChunkPos chunk) {
        return this.maxX >= chunk.getMinBlockX()
                && this.minX <= chunk.getMaxBlockX()
                && this.maxZ >= chunk.getMinBlockZ()
                && this.minZ <= chunk.getMaxBlockZ();
    }

    public boolean intersects(MoonCaveBounds other) {
        return this.maxX >= other.minX
                && this.minX <= other.maxX
                && this.maxY >= other.minY
                && this.minY <= other.maxY
                && this.maxZ >= other.minZ
                && this.minZ <= other.maxZ;
    }

    public int minX() {
        return this.minX;
    }

    public int minY() {
        return this.minY;
    }

    public int minZ() {
        return this.minZ;
    }

    public int maxX() {
        return this.maxX;
    }

    public int maxY() {
        return this.maxY;
    }

    public int maxZ() {
        return this.maxZ;
    }

    @Override
    public String toString() {
        return "[" + this.minX + "," + this.minY + "," + this.minZ + " -> " + this.maxX + "," + this.maxY + "," + this.maxZ + "]";
    }
}