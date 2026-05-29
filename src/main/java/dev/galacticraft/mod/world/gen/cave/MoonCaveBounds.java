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
}