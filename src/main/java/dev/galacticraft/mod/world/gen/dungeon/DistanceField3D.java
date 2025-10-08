package dev.galacticraft.mod.world.gen.dungeon;

import java.util.ArrayDeque;

final class DistanceField3D {
    // Returns Manhattan distance to nearest false cell (i.e., distance-to-walls when called with free mask)
    static int[][][] manhattanDist(VoxelMask3D free) {
        int nx = free.nx, ny = free.ny, nz = free.nz;
        int[][][] D = new int[nx][ny][nz];
        ArrayDeque<int[]> q = new ArrayDeque<>();

        final int INF = 1 << 28;
        for (int x = 0; x < nx; x++)
            for (int y = 0; y < ny; y++)
                for (int z = 0; z < nz; z++) {
                    if (free.get(x, y, z)) {
                        D[x][y][z] = INF;
                    } else {
                        D[x][y][z] = 0;
                        q.add(new int[]{x, y, z});
                    }
                }

        int[][] N6 = {{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};
        while (!q.isEmpty()) {
            int[] p = q.poll();
            int px = p[0], py = p[1], pz = p[2], dv = D[px][py][pz];
            for (int[] d : N6) {
                int nx1 = px + d[0], ny1 = py + d[1], nz1 = pz + d[2];
                if (!free.in(nx1, ny1, nz1)) continue;
                int cand = dv + 1;
                if (cand < D[nx1][ny1][nz1]) {
                    D[nx1][ny1][nz1] = cand;
                    q.add(new int[]{nx1, ny1, nz1});
                }
            }
        }
        return D;
    }
}