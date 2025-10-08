package dev.galacticraft.mod.world.gen.dungeon;

import net.minecraft.core.BlockPos;

import java.util.*;

final class FreeGraph {
    final List<Node> nodes = new ArrayList<>();
    final List<Edge>[] adj;
    private final VoxelMask3D free;
    private final int stride;
    private final int edgeCount;

    private FreeGraph(VoxelMask3D free, int stride, List<Node> ns, List<Edge>[] adj) {
        this.free = Objects.requireNonNull(free, "free");
        if (stride <= 0) throw new IllegalArgumentException("stride must be > 0");
        this.stride = stride;
        this.nodes.addAll(ns);
        this.adj = adj;
        int e = 0;
        for (List<Edge> a : adj) e += a.size();
        this.edgeCount = e;
    }

    static FreeGraph build(VoxelMask3D free, int stride) {
        Objects.requireNonNull(free, "free");
        if (stride <= 0) throw new IllegalArgumentException("stride must be > 0");

        final int nx = free.nx, ny = free.ny, nz = free.nz;

        int[][][] id = new int[nx][ny][nz];
        for (int x = 0; x < nx; x++) for (int y = 0; y < ny; y++) Arrays.fill(id[x][y], -1);

        ArrayList<Node> ns = new ArrayList<>();
        int next = 0;

        // sample on stride
        for (int y = 0; y < ny; y += stride)
            for (int x = 0; x < nx; x += stride)
                for (int z = 0; z < nz; z += stride) {
                    if (free.get(x, y, z)) {
                        id[x][y][z] = next;
                        ns.add(new Node(x, y, z, next++));
                    }
                }

        @SuppressWarnings("unchecked")
        List<Edge>[] adj = new ArrayList[next];
        for (int i = 0; i < next; i++) adj[i] = new ArrayList<>();

        // 26-neighborhood (axis + planar + full diagonals)
        int s = stride;
        int[] ds = {-s, 0, +s};
        for (Node n : ns) {
            for (int dx : ds)
                for (int dy : ds)
                    for (int dz : ds) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        int x1 = n.x + dx, y1 = n.y + dy, z1 = n.z + dz;
                        if (!free.in(x1, y1, z1) || !free.get(x1, y1, z1)) continue;
                        int j = id[x1][y1][z1];
                        if (j < 0) continue;

                        // reject corner-cutting: require full voxel line to be free
                        if (!lineFree(free, n.x, n.y, n.z, x1, y1, z1)) continue;

                        float w = (float) Math.sqrt((double) dx * dx + (double) dy * dy + (double) dz * dz);
                        adj[n.id].add(new Edge(n.id, j, w));
                    }
        }
        return new FreeGraph(free, stride, ns, adj);
    }

    private static float heuristic(Node a, Node b) {
        float dx = a.x - b.x, dy = a.y - b.y, dz = a.z - b.z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * clamp to [0,max) then snap down to stride multiple
     */
    private static int clampToStride(int v, int s, int max) {
        int t = Math.max(0, Math.min(v, max - 1));
        return (t / s) * s;
    }

    /**
     * True iff every voxel along the inclusive line is free.
     */
    private static boolean lineFree(VoxelMask3D m, int x0, int y0, int z0, int x1, int y1, int z1) {
        for (int[] p : voxelLine(x0, y0, z0, x1, y1, z1, true)) {
            int x = p[0], y = p[1], z = p[2];
            if (!m.in(x, y, z) || !m.get(x, y, z)) return false;
        }
        return true;
    }

    /**
     * 3D integer DDA (Bresenham-like) between two voxels.
     * If includeStart=true, the first element is (x0,y0,z0); otherwise it starts at the next step.
     */
    private static List<int[]> voxelLine(int x0, int y0, int z0, int x1, int y1, int z1, boolean includeStart) {
        int dx = Math.abs(x1 - x0), dy = Math.abs(y1 - y0), dz = Math.abs(z1 - z0);
        int xs = x1 >= x0 ? 1 : -1;
        int ys = y1 >= y0 ? 1 : -1;
        int zs = z1 >= z0 ? 1 : -1;

        ArrayList<int[]> pts = new ArrayList<>(1 + Math.max(dx, Math.max(dy, dz)));
        int x = x0, y = y0, z = z0;

        int p1, p2;
        if (dx >= dy && dx >= dz) {
            p1 = 2 * dy - dx;
            p2 = 2 * dz - dx;
            if (includeStart) pts.add(new int[]{x, y, z});
            for (int i = 0; i < dx; i++) {
                x += xs;
                if (p1 >= 0) {
                    y += ys;
                    p1 -= 2 * dx;
                }
                if (p2 >= 0) {
                    z += zs;
                    p2 -= 2 * dx;
                }
                p1 += 2 * dy;
                p2 += 2 * dz;
                pts.add(new int[]{x, y, z});
            }
        } else if (dy >= dx && dy >= dz) {
            p1 = 2 * dx - dy;
            p2 = 2 * dz - dy;
            if (includeStart) pts.add(new int[]{x, y, z});
            for (int i = 0; i < dy; i++) {
                y += ys;
                if (p1 >= 0) {
                    x += xs;
                    p1 -= 2 * dy;
                }
                if (p2 >= 0) {
                    z += zs;
                    p2 -= 2 * dy;
                }
                p1 += 2 * dx;
                p2 += 2 * dz;
                pts.add(new int[]{x, y, z});
            }
        } else {
            p1 = 2 * dy - dz;
            p2 = 2 * dx - dz;
            if (includeStart) pts.add(new int[]{x, y, z});
            for (int i = 0; i < dz; i++) {
                z += zs;
                if (p1 >= 0) {
                    y += ys;
                    p1 -= 2 * dz;
                }
                if (p2 >= 0) {
                    x += xs;
                    p2 -= 2 * dz;
                }
                p1 += 2 * dy;
                p2 += 2 * dx;
                pts.add(new int[]{x, y, z});
            }
        }
        return pts;
    }

    // === public helpers (optional for your logs) ===
    VoxelMask3D freeMask() {
        return this.free;
    }

    int getStride() {
        return this.stride;
    }

    int nodeCount() {
        return this.nodes.size();
    }

    int edgeCount() {
        return this.edgeCount;
    }

    // ===== internals =====

    /**
     * Convenience
     */
    List<BlockPos> route(BlockPos startWorld, BlockPos goalWorld) {
        return route(startWorld, goalWorld, null, 0, null);
    }

    /**
     * A* on coarse graph; returns a dense polyline (unit steps) in world coords, anchored to the exact endpoints.
     */
    List<BlockPos> route(BlockPos startWorld, BlockPos goalWorld, VoxelMask3D ignoredFree, int ignoredStride, Random ignoredRnd) {
        if (nodes.isEmpty()) return List.of();

        // world -> local
        int sx = clampToStride(startWorld.getX() - free.ox, stride, free.nx);
        int sy = clampToStride(startWorld.getY() - free.oy, stride, free.ny);
        int sz = clampToStride(startWorld.getZ() - free.oz, stride, free.nz);
        int gx = clampToStride(goalWorld.getX() - free.ox, stride, free.nx);
        int gy = clampToStride(goalWorld.getY() - free.oy, stride, free.ny);
        int gz = clampToStride(goalWorld.getZ() - free.oz, stride, free.nz);

        int sIdx = findClosestNode(sx, sy, sz);
        int gIdx = findClosestNode(gx, gy, gz);
        if (sIdx < 0 || gIdx < 0) return List.of();

        if (sIdx == gIdx) {
            // trivial: still return anchored endpoints
            return List.of(startWorld, goalWorld);
        }

        // A*
        float[] gScore = new float[nodes.size()];
        float[] fScore = new float[nodes.size()];
        int[] prev = new int[nodes.size()];
        Arrays.fill(gScore, Float.POSITIVE_INFINITY);
        Arrays.fill(fScore, Float.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);

        final Node goal = nodes.get(gIdx);

        record QN(int id, float f) {
        }
        PriorityQueue<QN> open = new PriorityQueue<>(Comparator.comparingDouble(q -> q.f));
        boolean[] closed = new boolean[nodes.size()];

        gScore[sIdx] = 0f;
        fScore[sIdx] = heuristic(nodes.get(sIdx), goal);
        open.add(new QN(sIdx, fScore[sIdx]));

        while (!open.isEmpty()) {
            QN q = open.poll();
            int u = q.id;
            if (closed[u]) continue;
            if (u == gIdx) break;
            closed[u] = true;

            for (Edge e : adj[u]) {
                int v = e.b;
                if (closed[v]) continue;
                float alt = gScore[u] + e.w;
                if (alt < gScore[v]) {
                    prev[v] = u;
                    gScore[v] = alt;
                    fScore[v] = alt + heuristic(nodes.get(v), goal);
                    open.add(new QN(v, fScore[v]));
                }
            }
        }
        if (prev[gIdx] == -1) return List.of();

        // reconstruct in local coords (coarse)
        ArrayList<int[]> coarse = new ArrayList<>();
        for (int t = gIdx; t != -1; t = prev[t]) {
            Node n = nodes.get(t);
            coarse.add(new int[]{n.x, n.y, n.z});
        }
        Collections.reverse(coarse);

        // densify to unit steps (local), then map to world and anchor endpoints
        ArrayList<BlockPos> dense = new ArrayList<>();
        dense.add(startWorld); // ensure we start exactly at the portal

        int[] cur = coarse.get(0);
        for (int i = 1; i < coarse.size(); i++) {
            int[] nxt = coarse.get(i);
            // step from cur -> nxt at unit voxels, *excluding* cur to avoid dup
            for (int[] p : voxelLine(cur[0], cur[1], cur[2], nxt[0], nxt[1], nxt[2], /*includeStart*/false)) {
                dense.add(new BlockPos(p[0] + free.ox, p[1] + free.oy, p[2] + free.oz));
            }
            cur = nxt;
        }

        // ensure we end exactly at the portal
        if (dense.isEmpty() || !dense.get(dense.size() - 1).equals(goalWorld)) {
            dense.add(goalWorld);
        }
        // de-dup consecutive equal points just in case
        if (dense.size() >= 2) {
            ArrayList<BlockPos> tmp = new ArrayList<>(dense.size());
            BlockPos last = null;
            for (BlockPos p : dense) {
                if (!p.equals(last)) tmp.add(p);
                last = p;
            }
            dense = tmp;
        }
        return dense;
    }

    private int findClosestNode(int x, int y, int z) {
        double best = Double.POSITIVE_INFINITY;
        int bi = -1;
        for (Node n : nodes) {
            double dx = n.x - x, dy = n.y - y, dz = n.z - z;
            double d = dx * dx + dy * dy + dz * dz;
            if (d < best) {
                best = d;
                bi = n.id;
            }
        }
        return bi;
    }

    record Node(int x, int y, int z, int id) {
    }

    record Edge(int a, int b, float w) {
    }
}