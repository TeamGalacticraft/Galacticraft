package dev.galacticraft.mod.world.gen.dungeon;

final class Morph3D {
    /**
     * Binary dilation by a Chebyshev radius r (cube).
     */
    static VoxelMask3D dilate(VoxelMask3D src, int r) {
        if (r <= 0) return src.copy();
        VoxelMask3D tmp = src.copy();
        VoxelMask3D out = new VoxelMask3D(src.nx, src.ny, src.nz, src.ox, src.oy, src.oz);

        // X pass
        for (int y = 0; y < src.ny; y++)
            for (int z = 0; z < src.nz; z++) {
                int acc = 0;
                for (int x = 0; x < src.nx; x++) {
                    if (src.get(x, y, z)) acc = r;
                    else acc = Math.max(0, acc - 1);
                    if (acc > 0) tmp.set(x, y, z, true);
                }
                acc = 0;
                for (int x = src.nx - 1; x >= 0; x--) {
                    if (src.get(x, y, z)) acc = r;
                    else acc = Math.max(0, acc - 1);
                    if (acc > 0) tmp.set(x, y, z, true);
                }
            }
        // Y pass -> into out
        for (int x = 0; x < src.nx; x++)
            for (int z = 0; z < src.nz; z++) {
                int acc = 0;
                for (int y = 0; y < src.ny; y++) {
                    if (tmp.get(x, y, z)) acc = r;
                    else acc = Math.max(0, acc - 1);
                    if (acc > 0) out.set(x, y, z, true);
                }
                acc = 0;
                for (int y = src.ny - 1; y >= 0; y--) {
                    if (tmp.get(x, y, z)) acc = r;
                    else acc = Math.max(0, acc - 1);
                    if (acc > 0) out.set(x, y, z, true);
                }
            }

        // Z pass (in-place out)
        VoxelMask3D out2 = new VoxelMask3D(src.nx, src.ny, src.nz, src.ox, src.oy, src.oz);
        for (int x = 0; x < src.nx; x++)
            for (int y = 0; y < src.ny; y++) {
                int acc = 0;
                for (int z = 0; z < src.nz; z++) {
                    if (out.get(x, y, z)) acc = r;
                    else acc = Math.max(0, acc - 1);
                    if (acc > 0) out2.set(x, y, z, true);
                }
                acc = 0;
                for (int z = src.nz - 1; z >= 0; z--) {
                    if (out.get(x, y, z)) acc = r;
                    else acc = Math.max(0, acc - 1);
                    if (acc > 0) out2.set(x, y, z, true);
                }
            }
        return out2;
    }

    /**
     * Compute M_free = world \ dilate(M_room, R). Caller constructs “world” implicitly by dims.
     */
    static VoxelMask3D freeMaskFromRooms(VoxelMask3D mRoom, int R) {
        VoxelMask3D dil = dilate(mRoom, R);
        VoxelMask3D free = new VoxelMask3D(mRoom.nx, mRoom.ny, mRoom.nz, mRoom.ox, mRoom.oy, mRoom.oz);
        // free = !dil
        for (int y = 0; y < free.ny; y++)
            for (int x = 0; x < free.nx; x++)
                for (int z = 0; z < free.nz; z++) {
                    if (!dil.get(x, y, z)) free.set(x, y, z, true);
                }
        return free;
    }
}