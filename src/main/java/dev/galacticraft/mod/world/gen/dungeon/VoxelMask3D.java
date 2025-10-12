package dev.galacticraft.mod.world.gen.dungeon;

import java.util.BitSet;

final class VoxelMask3D {
    final int nx, ny, nz;     // dimensions
    final int ox, oy, oz;     // world-space origin (min corner) in blocks
    private final BitSet bits; // true = occupied (rooms OR carved corridor, depending on mask usage)

    VoxelMask3D(int nx, int ny, int nz, int ox, int oy, int oz) {
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        this.ox = ox;
        this.oy = oy;
        this.oz = oz;
        this.bits = new BitSet(nx * ny * nz);
    }

    int idx(int x, int y, int z) {
        return (y * nx + x) * nz + z;
    }

    boolean in(int x, int y, int z) {
        return (x >= 0 && x < nx) && (y >= 0 && y < ny) && (z >= 0 && z < nz);
    }

    void set(int x, int y, int z, boolean v) {
        if (in(x, y, z)) bits.set(idx(x, y, z), v);
    }

    boolean get(int x, int y, int z) {
        return in(x, y, z) && bits.get(idx(x, y, z));
    }

    void fillAABB(int x0, int y0, int z0, int x1, int y1, int z1, boolean v) {
        int xa = Math.max(0, Math.min(x0, x1)), xb = Math.min(nx - 1, Math.max(x0, x1));
        int ya = Math.max(0, Math.min(y0, y1)), yb = Math.min(ny - 1, Math.max(y0, y1));
        int za = Math.max(0, Math.min(z0, z1)), zb = Math.min(nz - 1, Math.max(z0, z1));
        for (int y = ya; y <= yb; y++)
            for (int x = xa; x <= xb; x++) {
                int base = (y * nx + x) * nz;
                for (int z = za; z <= zb; z++) bits.set(base + z, v);
            }
    }

    /**
     * World (block) -> local grid index
     */
    int gx(int wx) {
        return wx - ox;
    }

    int gy(int wy) {
        return wy - oy;
    }

    int gz(int wz) {
        return wz - oz;
    }

    /**
     * Clone
     */
    VoxelMask3D copy() {
        VoxelMask3D m = new VoxelMask3D(nx, ny, nz, ox, oy, oz);
        m.bits.or(this.bits);
        return m;
    }

    public double ox() {
        return this.ox;
    }

    public double oy() {
        return this.oy;
    }

    public double oz() {
        return this.oz;
    }

    public double nx() {
        return this.nx;
    }

    public double ny() {
        return this.ny;
    }

    public double nz() {
        return this.nz;
    }
}