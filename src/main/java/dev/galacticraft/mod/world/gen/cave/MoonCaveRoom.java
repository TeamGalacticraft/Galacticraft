package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

/**
 * Organic multi-lobed cave chamber.
 */
public final class MoonCaveRoom implements MoonCaveElement {
    private static final int LOBE_COUNT = 5;

    private final BlockPos center;
    private final double radiusX;
    private final double radiusY;
    private final double radiusZ;
    private final int seed;
    private final MoonCaveBounds bounds;
    private final Lobe[] lobes;

    public MoonCaveRoom(BlockPos center, double radiusX, double radiusY, double radiusZ, int seed) {
        this.center = center;
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.radiusZ = radiusZ;
        this.seed = seed;
        this.lobes = this.createLobes();
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

        for (int x = minX; x <= maxX; x++) {
            int localX = x - chunkPos.getMinBlockX();

            for (int z = minZ; z <= maxZ; z++) {
                int localZ = z - chunkPos.getMinBlockZ();

                for (int y = lowY; y <= highY; y++) {
                    CaveZone zone = this.zone(x, y, z);

                    if (zone != CaveZone.NONE) {
                        mask.set(localX, y, localZ, zone, owner);
                    }
                }
            }
        }
    }

    private CaveZone zone(int x, int y, int z) {
        double warpX = CaveNoise.fbm(this.seed + 1001L, x, y, z, 0.032D, 2, 0.55D) * this.radiusX * 0.34D;
        double warpY = CaveNoise.fbm(this.seed + 2002L, x, y, z, 0.032D, 2, 0.55D) * this.radiusY * 0.25D;
        double warpZ = CaveNoise.fbm(this.seed + 3003L, x, y, z, 0.032D, 2, 0.55D) * this.radiusZ * 0.34D;

        double px = x + 0.5D + warpX;
        double py = y + 0.5D + warpY;
        double pz = z + 0.5D + warpZ;

        double best = -999.0D;

        for (Lobe lobe : this.lobes) {
            double dx = (px - lobe.x) / lobe.rx;
            double dy = (py - lobe.y) / lobe.ry;
            double dz = (pz - lobe.z) / lobe.rz;

            double density = 1.0D - (dx * dx + dy * dy + dz * dz);

            if (density > best) {
                best = density;
            }
        }

        double large = CaveNoise.warpedFbm(this.seed + 4444L, x, y, z, 0.045D, 8.0D, 2);
        double detail = CaveNoise.fbm(this.seed + 5555L, x, y, z, 0.16D, 2, 0.45D);
        double density = best + large * 0.34D + detail * 0.13D;

        if (density >= 0.0D) {
            return CaveZone.AIR;
        }

        if (density >= -0.20D) {
            return CaveZone.INNER_SHELL;
        }

        if (density >= -0.46D) {
            return CaveZone.OUTER_SHELL;
        }

        return CaveZone.NONE;
    }

    private Lobe[] createLobes() {
        Lobe[] result = new Lobe[LOBE_COUNT];

        result[0] = new Lobe(
                this.center.getX(),
                this.center.getY(),
                this.center.getZ(),
                this.radiusX,
                this.radiusY,
                this.radiusZ
        );

        for (int i = 1; i < LOBE_COUNT; i++) {
            double ox = randomSigned(i, 0) * this.radiusX * 0.55D;
            double oy = randomSigned(i, 1) * this.radiusY * 0.35D;
            double oz = randomSigned(i, 2) * this.radiusZ * 0.55D;

            double scale = 0.55D + random01(i, 3) * 0.45D;

            result[i] = new Lobe(
                    this.center.getX() + ox,
                    this.center.getY() + oy,
                    this.center.getZ() + oz,
                    this.radiusX * scale,
                    this.radiusY * (0.65D + random01(i, 4) * 0.35D),
                    this.radiusZ * scale
            );
        }

        return result;
    }

    private double randomSigned(int index, int salt) {
        return random01(index, salt) * 2.0D - 1.0D;
    }

    private double random01(int index, int salt) {
        long h = this.seed;
        h ^= (long) index * 73428767L;
        h ^= (long) salt * 91227153L;
        h ^= h >>> 33;
        h *= 0xff51afd7ed558ccdL;
        h ^= h >>> 33;

        return (h & 0xFFFFL) / 65535.0D;
    }

    private static MoonCaveBounds createBounds(BlockPos center, double radiusX, double radiusY, double radiusZ) {
        MoonCaveBounds bounds = new MoonCaveBounds();
        bounds.includeRoom(center, radiusX * 1.85D, radiusY * 1.55D, radiusZ * 1.85D, 8);
        return bounds;
    }

    private record Lobe(double x, double y, double z, double rx, double ry, double rz) {
    }
}