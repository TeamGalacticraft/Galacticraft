package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;

public record MoonCaveRoom(
        BlockPos center,
        double radiusX,
        double radiusY,
        double radiusZ,
        int seed,
        MoonCaveBounds bounds
) implements MoonCaveElement {
    public MoonCaveRoom(BlockPos center, double radiusX, double radiusY, double radiusZ, int seed) {
        this(center, radiusX, radiusY, radiusZ, seed, createBounds(center, radiusX, radiusY, radiusZ));
    }

    @Override
    public CaveZone zone(int x, int y, int z) {
        double distance = this.distance(x, y, z);
        double air = this.threshold(x, y, z, 1.0D);
        double inner = this.threshold(x, y, z, 1.16D);
        double outer = this.threshold(x, y, z, 1.40D);

        if (distance <= air) return CaveZone.AIR;
        if (distance <= inner) return CaveZone.INNER_SHELL;
        if (distance <= outer) return CaveZone.OUTER_SHELL;
        return CaveZone.NONE;
    }

    private double distance(int x, int y, int z) {
        double dx = (x + 0.5D - this.center.getX()) / this.radiusX;
        double dy = (y + 0.5D - this.center.getY()) / this.radiusY;
        double dz = (z + 0.5D - this.center.getZ()) / this.radiusZ;
        return dx * dx + dy * dy + dz * dz;
    }

    private double threshold(int x, int y, int z, double base) {
        double warp = Math.sin((x + this.seed) * 0.065D)
                + Math.sin((z - this.seed) * 0.071D)
                + Math.sin((y + this.seed) * 0.121D);

        return base + warp * 0.03D;
    }

    private static MoonCaveBounds createBounds(BlockPos center, double radiusX, double radiusY, double radiusZ) {
        MoonCaveBounds bounds = new MoonCaveBounds();
        bounds.includeRoom(center, radiusX, radiusY, radiusZ, 5);
        return bounds;
    }
}