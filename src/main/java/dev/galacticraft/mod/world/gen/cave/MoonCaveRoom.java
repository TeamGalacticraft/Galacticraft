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

public final class MoonCaveRoom implements MoonCaveElement {
    private static final int LOBE_COUNT = 7;

    private final BlockPos center;
    private final int seed;
    private final MoonCaveBounds bounds;
    private final Lobe[] lobes;

    public MoonCaveRoom(BlockPos center, double radiusX, double radiusY, double radiusZ, int seed) {
        this.center = center;
        this.seed = seed;
        this.lobes = this.createLobes(radiusX, radiusY, radiusZ);
        this.bounds = createBounds(this.lobes);
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

        if (minX > maxX || minZ > maxZ || lowY > highY) {
            return;
        }

        for (int x = minX; x <= maxX; x++) {
            int localX = x - chunkPos.getMinBlockX();

            for (int z = minZ; z <= maxZ; z++) {
                int localZ = z - chunkPos.getMinBlockZ();

                for (int y = lowY; y <= highY; y++) {
                    byte zone = this.zoneRaw(x, y, z);

                    if (zone != CaveCarvingMask.NONE) {
                        mask.setRaw(localX, y, localZ, zone, owner);
                    }
                }
            }
        }
    }

    private byte zoneRaw(int x, int y, int z) {
        double px = x + 0.5D;
        double py = y + 0.5D;
        double pz = z + 0.5D;

        double best = -999.0D;

        for (Lobe lobe : this.lobes) {
            double dx = (px - lobe.x) * lobe.invRx;
            double dy = (py - lobe.y) * lobe.invRy;
            double dz = (pz - lobe.z) * lobe.invRz;

            double density = 1.0D - (dx * dx + dy * dy + dz * dz);

            if (density > best) {
                best = density;
            }
        }

        double rough = hashNoise(x >> 1, y >> 1, z >> 1, this.seed) * 0.18D
                + hashNoise(x >> 2, y >> 2, z >> 2, this.seed + 41) * 0.26D
                + fractureNoise(x, y, z) * 0.16D;

        double density = best + rough;

        if (density >= 0.0D) {
            return CaveCarvingMask.AIR;
        }

        if (density >= -0.16D) {
            return CaveCarvingMask.INNER;
        }

        if (density >= -0.38D) {
            return CaveCarvingMask.OUTER;
        }

        return CaveCarvingMask.NONE;
    }

    private Lobe[] createLobes(double radiusX, double radiusY, double radiusZ) {
        Lobe[] result = new Lobe[LOBE_COUNT];

        result[0] = new Lobe(
                this.center.getX(),
                this.center.getY(),
                this.center.getZ(),
                radiusX,
                radiusY,
                radiusZ
        );

        for (int i = 1; i < LOBE_COUNT; i++) {
            double angle = random01(i, 0) * Math.PI * 2.0D;
            double distance = 0.25D + random01(i, 1) * 0.55D;

            double ox = Math.cos(angle) * radiusX * distance;
            double oz = Math.sin(angle) * radiusZ * distance;
            double oy = randomSigned(i, 2) * radiusY * 0.45D;

            double scaleX = 0.48D + random01(i, 3) * 0.45D;
            double scaleY = 0.45D + random01(i, 4) * 0.35D;
            double scaleZ = 0.48D + random01(i, 5) * 0.45D;

            result[i] = new Lobe(
                    this.center.getX() + ox,
                    this.center.getY() + oy,
                    this.center.getZ() + oz,
                    radiusX * scaleX,
                    radiusY * scaleY,
                    radiusZ * scaleZ
            );
        }

        return result;
    }

    private double randomSigned(int index, int salt) {
        return random01(index, salt) * 2.0D - 1.0D;
    }

    private double random01(int index, int salt) {
        int h = hash(this.seed, index, salt, 0);
        return (h & 0xFFFF) / 65535.0D;
    }

    private static MoonCaveBounds createBounds(Lobe[] lobes) {
        MoonCaveBounds bounds = new MoonCaveBounds();

        for (Lobe lobe : lobes) {
            bounds.include(
                    (int) Math.floor(lobe.x - lobe.rx - 4),
                    (int) Math.floor(lobe.y - lobe.ry - 4),
                    (int) Math.floor(lobe.z - lobe.rz - 4),
                    (int) Math.ceil(lobe.x + lobe.rx + 4),
                    (int) Math.ceil(lobe.y + lobe.ry + 4),
                    (int) Math.ceil(lobe.z + lobe.rz + 4)
            );
        }

        return bounds;
    }

    private static double hashNoise(int x, int y, int z, int seed) {
        return ((hash(seed, x, y, z) & 1023) / 511.5D) - 1.0D;
    }

    private static double fractureNoise(int x, int y, int z) {
        int a = Math.floorMod(x * 3 + z * 5 + y, 19);
        int b = Math.floorMod(x * -4 + z * 2 - y * 3, 23);

        double fractureA = 1.0D - Math.min(1.0D, Math.abs(a - 9) / 9.0D);
        double fractureB = 1.0D - Math.min(1.0D, Math.abs(b - 11) / 11.0D);

        return Math.max(fractureA, fractureB) * 2.0D - 1.0D;
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

    private static final class Lobe {
        private final double x;
        private final double y;
        private final double z;
        private final double rx;
        private final double ry;
        private final double rz;
        private final double invRx;
        private final double invRy;
        private final double invRz;

        private Lobe(double x, double y, double z, double rx, double ry, double rz) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.rx = rx;
            this.ry = ry;
            this.rz = rz;
            this.invRx = 1.0D / rx;
            this.invRy = 1.0D / ry;
            this.invRz = 1.0D / rz;
        }
    }
}