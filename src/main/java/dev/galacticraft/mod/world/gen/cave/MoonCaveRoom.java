package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;

public record MoonCaveRoom(
        BlockPos center,
        double radiusX,
        double radiusY,
        double radiusZ,
        int seed
) {
    public boolean contains(double x, double y, double z) {
        return this.distance(x, y, z) <= this.warpThreshold(x, y, z, 1.0D);
    }

    public boolean innerShell(double x, double y, double z) {
        double distance = this.distance(x, y, z);
        return distance > this.warpThreshold(x, y, z, 1.0D) && distance <= this.warpThreshold(x, y, z, 1.18D);
    }

    public boolean outerShell(double x, double y, double z) {
        double distance = this.distance(x, y, z);
        return distance > this.warpThreshold(x, y, z, 1.18D) && distance <= this.warpThreshold(x, y, z, 1.42D);
    }

    private double distance(double x, double y, double z) {
        double dx = (x + 0.5D - this.center.getX()) / this.radiusX;
        double dy = (y + 0.5D - this.center.getY()) / this.radiusY;
        double dz = (z + 0.5D - this.center.getZ()) / this.radiusZ;

        return dx * dx + dy * dy + dz * dz;
    }

    private double warpThreshold(double x, double y, double z, double base) {
        double warp = Math.sin((x + this.seed) * 0.071D)
                + Math.sin((z - this.seed) * 0.063D)
                + Math.sin((y + this.seed) * 0.113D);

        return base + warp * 0.035D;
    }
}