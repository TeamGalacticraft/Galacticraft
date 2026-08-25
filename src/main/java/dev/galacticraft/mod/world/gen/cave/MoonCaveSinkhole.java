package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public final class MoonCaveSinkhole implements MoonCaveElement {
    private static final double INNER_EXTRA = 1.15D;
    private static final double OUTER_EXTRA = 2.35D;

    private final BlockPos top;
    private final BlockPos bottom;
    private final double radius;
    private final int seed;
    private final MoonCaveBounds bounds;

    public MoonCaveSinkhole(BlockPos top, BlockPos bottom, double radius, int seed) {
        this.top = top;
        this.bottom = bottom;
        this.radius = radius;
        this.seed = seed;
        this.bounds = createBounds(top, bottom, radius);
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

        for (int x = minX; x <= maxX; x++) {
            int localX = x - chunkPos.getMinBlockX();

            for (int z = minZ; z <= maxZ; z++) {
                int localZ = z - chunkPos.getMinBlockZ();

                for (int y = lowY; y <= highY; y++) {
                    double t = (this.top.getY() - y) / (double) Math.max(1, this.top.getY() - this.bottom.getY());
                    t = Math.max(0.0D, Math.min(1.0D, t));

                    double centerX = lerp(this.top.getX(), this.bottom.getX(), t);
                    double centerZ = lerp(this.top.getZ(), this.bottom.getZ(), t);

                    double rough = hashNoise(x >> 1, y >> 2, z >> 1, this.seed) * 1.15D;
                    double localRadius = Math.max(2.0D, this.radius + rough);

                    double dx = x + 0.5D - centerX;
                    double dz = z + 0.5D - centerZ;
                    double distanceSqr = dx * dx + dz * dz;

                    if (distanceSqr <= localRadius * localRadius) {
                        mask.setRaw(localX, y, localZ, CaveCarvingMask.AIR, owner);
                    } else if (distanceSqr <= (localRadius + INNER_EXTRA) * (localRadius + INNER_EXTRA)) {
                        mask.setRaw(localX, y, localZ, CaveCarvingMask.INNER, owner);
                    } else if (distanceSqr <= (localRadius + OUTER_EXTRA) * (localRadius + OUTER_EXTRA)) {
                        mask.setRaw(localX, y, localZ, CaveCarvingMask.OUTER, owner);
                    }
                }
            }
        }
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
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

    private static MoonCaveBounds createBounds(BlockPos top, BlockPos bottom, double radius) {
        MoonCaveBounds bounds = new MoonCaveBounds();
        int p = (int) Math.ceil(radius + OUTER_EXTRA + 4.0D);

        bounds.include(
                Math.min(top.getX(), bottom.getX()) - p,
                Math.min(top.getY(), bottom.getY()) - p,
                Math.min(top.getZ(), bottom.getZ()) - p,
                Math.max(top.getX(), bottom.getX()) + p,
                Math.max(top.getY(), bottom.getY()) + p,
                Math.max(top.getZ(), bottom.getZ()) + p
        );

        return bounds;
    }
}