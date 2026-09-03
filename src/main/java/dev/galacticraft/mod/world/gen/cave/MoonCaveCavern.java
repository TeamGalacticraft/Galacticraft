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

public final class MoonCaveCavern implements MoonCaveElement {
    private static final double INNER_EXTRA = 0.18D;
    private static final double OUTER_EXTRA = 0.42D;

    private final BlockPos center;
    private final double radiusX;
    private final double radiusY;
    private final double radiusZ;
    private final int seed;
    private final Pillar[] pillars;
    private final MoonCaveBounds bounds;

    public MoonCaveCavern(BlockPos center, double radiusX, double radiusY, double radiusZ, int pillarCount, int seed) {
        this.center = center;
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.radiusZ = radiusZ;
        this.seed = seed;
        this.pillars = this.createPillars(Math.max(1, pillarCount));
        this.bounds = createBounds(center, radiusX, radiusY, radiusZ);
    }

    @Override
    public MoonCaveBounds bounds() {
        return this.bounds;
    }

    @Override
    public void stamp(ChunkPos chunkPos, int minY, int maxY, CaveCarvingMask mask, MoonCavePlan owner) {
        int minX = Math.max(chunkPos.getMinBlockX(), this.bounds.minX());
        int maxX = Math.min(chunkPos.getMaxBlockX(), this.bounds.maxX());
        int minZ = Math.max(chunkPos.getMinBlockZ(), this.bounds.minZ());
        int maxZ = Math.min(chunkPos.getMaxBlockZ(), this.bounds.maxZ());
        int lowY = Math.max(minY, this.bounds.minY());
        int highY = Math.min(maxY, this.bounds.maxY());

        double invX = 1.0D / this.radiusX;
        double invY = 1.0D / this.radiusY;
        double invZ = 1.0D / this.radiusZ;

        for (int x = minX; x <= maxX; x++) {
            int localX = x - chunkPos.getMinBlockX();

            for (int z = minZ; z <= maxZ; z++) {
                int localZ = z - chunkPos.getMinBlockZ();

                double columnRough = hashNoise(x >> 3, 0, z >> 3, this.seed + 91) * 0.18D;

                for (int y = lowY; y <= highY; y++) {
                    double dx = (x + 0.5D - this.center.getX()) * invX;
                    double dy = (y + 0.5D - this.center.getY()) * invY;
                    double dz = (z + 0.5D - this.center.getZ()) * invZ;

                    double cavernDensity = 1.0D - (dx * dx + dy * dy * 0.82D + dz * dz);
                    cavernDensity += hashNoise(x >> 2, y >> 2, z >> 2, this.seed + 17) * 0.20D;
                    cavernDensity += columnRough;

                    if (cavernDensity < -OUTER_EXTRA) {
                        continue;
                    }

                    byte pillarZone = this.pillarZone(x, y, z);

                    if (pillarZone != CaveCarvingMask.NONE) {
                        mask.setRaw(localX, y, localZ, pillarZone, owner);
                        continue;
                    }

                    if (cavernDensity >= 0.0D) {
                        mask.setRaw(localX, y, localZ, CaveCarvingMask.AIR, owner);
                    } else if (cavernDensity >= -INNER_EXTRA) {
                        mask.setRaw(localX, y, localZ, CaveCarvingMask.INNER, owner);
                    } else {
                        mask.setRaw(localX, y, localZ, CaveCarvingMask.OUTER, owner);
                    }
                }
            }
        }
    }

    private byte pillarZone(int x, int y, int z) {
        for (Pillar pillar : this.pillars) {
            double dx = x + 0.5D - pillar.x;
            double dz = z + 0.5D - pillar.z;
            double distanceSqr = dx * dx + dz * dz;

            double rough = hashNoise(x >> 1, y >> 2, z >> 1, this.seed + pillar.index * 31) * 0.55D;
            double radius = pillar.radius + rough;

            if (distanceSqr <= radius * radius && y >= pillar.minY && y <= pillar.maxY) {
                return CaveCarvingMask.NONE;
            }

            double shellRadius = radius + 1.45D;

            if (distanceSqr <= shellRadius * shellRadius && y >= pillar.minY && y <= pillar.maxY) {
                return CaveCarvingMask.INNER;
            }
        }

        return CaveCarvingMask.NONE;
    }

    private Pillar[] createPillars(int count) {
        Pillar[] result = new Pillar[count];

        for (int i = 0; i < count; i++) {
            double angle = random01(i, 0) * Math.PI * 2.0D;
            double distance = (0.15D + random01(i, 1) * 0.45D);

            double x = this.center.getX() + Math.cos(angle) * this.radiusX * distance;
            double z = this.center.getZ() + Math.sin(angle) * this.radiusZ * distance;
            double radius = 3.0D + random01(i, 2) * 5.0D;

            int minY = (int) Math.floor(this.center.getY() - this.radiusY - 2.0D);
            int maxY = (int) Math.ceil(this.center.getY() + this.radiusY + 2.0D);

            result[i] = new Pillar(i, x, z, radius, minY, maxY);
        }

        return result;
    }

    private double random01(int index, int salt) {
        return (hash(this.seed, index, salt, 0) & 0xFFFF) / 65535.0D;
    }

    private static double hashNoise(int x, int y, int z, int seed) {
        return ((hash(seed, x, y, z) & 1023) / 511.5D) - 1.0D;
    }

    private static int hash(int seed, int x, int y, int z) {
        long h = seed;
        h ^= (long) x * 73428767L;
        h ^= (long) y * 91227153L;
        h ^= (long) z * 42317861L;
        h ^= h >>> 33;
        h *= 0xff51afd7ed558ccdL;
        h ^= h >>> 33;
        return (int) h;
    }

    private static MoonCaveBounds createBounds(BlockPos center, double radiusX, double radiusY, double radiusZ) {
        MoonCaveBounds bounds = new MoonCaveBounds();
        bounds.includeRoom(center, radiusX + 8.0D, radiusY + 8.0D, radiusZ + 8.0D, 6);
        return bounds;
    }

    private record Pillar(int index, double x, double z, double radius, int minY, int maxY) {
    }
}