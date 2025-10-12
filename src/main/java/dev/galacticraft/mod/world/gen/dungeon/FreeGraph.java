package dev.galacticraft.mod.world.gen.dungeon;

import com.mojang.logging.LogUtils;
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
     * A* on coarse graph; returns a dense polyline (unit steps) in world coords, anchored to the exact endpoints.
     * When 'ignoredFree' is provided, those voxels are treated as NON-free for hard checks; additionally we add a
     * distance-based repulsion penalty so paths prefer to stay away from occupied geometry.
     */
    java.util.List<BlockPos> route(BlockPos startWorld, BlockPos goalWorld, VoxelMask3D ignoredFree, int ignoredStride, java.util.Random ignoredRnd) {
        if (nodes.isEmpty()) return java.util.List.of();

        // world -> local grid coords (snapped to stride)
        int sx = clampToStride(startWorld.getX() - free.ox, stride, free.nx);
        int sy = clampToStride(startWorld.getY() - free.oy, stride, free.ny);
        int sz = clampToStride(startWorld.getZ() - free.oz, stride, free.nz);
        int gx = clampToStride(goalWorld.getX() - free.ox, stride, free.nx);
        int gy = clampToStride(goalWorld.getY() - free.oy, stride, free.ny);
        int gz = clampToStride(goalWorld.getZ() - free.oz, stride, free.nz);

        // Prefer start/goal nodes that are "usable" under the avoid mask; fall back to closest if none.
        int sIdx = findClosestUsableNode(sx, sy, sz, ignoredFree);
        if (sIdx < 0) sIdx = findClosestNode(sx, sy, sz);

        int gIdx = findClosestUsableNode(gx, gy, gz, ignoredFree);
        if (gIdx < 0) gIdx = findClosestNode(gx, gy, gz);

        if (sIdx < 0 || gIdx < 0) return java.util.List.of();
        if (sIdx == gIdx) {
            // trivial: still return anchored endpoints
            return java.util.List.of(startWorld, goalWorld);
        }

        // --- A* over prebuilt adjacency, but filter each edge against the avoid mask + add repulsion penalty ---
        float[] gScore = new float[nodes.size()];
        float[] fScore = new float[nodes.size()];
        int[] prev = new int[nodes.size()];
        java.util.Arrays.fill(gScore, Float.POSITIVE_INFINITY);
        java.util.Arrays.fill(fScore, Float.POSITIVE_INFINITY);
        java.util.Arrays.fill(prev, -1);

        final Node goal = nodes.get(gIdx);

        record QN(int id, float f) {}
        java.util.PriorityQueue<QN> open = new java.util.PriorityQueue<>(java.util.Comparator.comparingDouble(q -> q.f));
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

            Node nu = nodes.get(u);

            for (Edge e : adj[u]) {
                int v = e.b;
                if (closed[v]) continue;

                Node nv = nodes.get(v);

                // Reject if the voxel line intersects blocked voxels (hard check)
                if (!lineFree(free, ignoredFree, nu.x, nu.y, nu.z, nv.x, nv.y, nv.z)) continue;

                float baseCost = e.w;

                // --- Soft repulsion from blocked geometry ---
                // We treat ignoredFree as the "repulsion sources". Push away within a small radius.
                final int REPULSE_R = Math.max(3, this.stride * 6); // search radius in voxels
                final float REPULSE_K = 6.5f;                       // strength

                int mx = (nu.x + nv.x) >> 1, my = (nu.y + nv.y) >> 1, mz = (nu.z + nv.z) >> 1;
                int d = (ignoredFree == null) ? (REPULSE_R + 1) : distToBlocked(free, ignoredFree, mx, my, mz, REPULSE_R);
                float repulse = (d > REPULSE_R) ? 0f : (REPULSE_K * (1f / Math.max(1f, d)));

                float alt = gScore[u] + baseCost + repulse;
                if (alt < gScore[v]) {
                    prev[v] = u;
                    gScore[v] = alt;
                    fScore[v] = alt + heuristic(nv, goal);
                    open.add(new QN(v, fScore[v]));
                }
            }
        }
        if (prev[gIdx] == -1) return java.util.List.of();

        // reconstruct in local coords (coarse path of sampled nodes)
        java.util.ArrayList<int[]> coarse = new java.util.ArrayList<>();
        for (int t = gIdx; t != -1; t = prev[t]) {
            Node n = nodes.get(t);
            coarse.add(new int[]{n.x, n.y, n.z});
        }
        java.util.Collections.reverse(coarse);

        // densify to unit steps (local), then map to world and anchor endpoints
        java.util.ArrayList<BlockPos> dense = new java.util.ArrayList<>();
        dense.add(startWorld); // start exactly at the portal

        int[] cur = coarse.get(0);
        for (int i = 1; i < coarse.size(); i++) {
            int[] nxt = coarse.get(i);
            for (int[] p : voxelLine(cur[0], cur[1], cur[2], nxt[0], nxt[1], nxt[2], /*includeStart*/false)) {
                dense.add(new BlockPos(p[0] + free.ox, p[1] + free.oy, p[2] + free.oz));
            }
            cur = nxt;
        }

        if (dense.isEmpty() || !dense.get(dense.size() - 1).equals(goalWorld)) {
            dense.add(goalWorld);
        }

        // de-dup consecutive equal points
        if (dense.size() >= 2) {
            java.util.ArrayList<BlockPos> tmp = new java.util.ArrayList<>(dense.size());
            BlockPos last = null;
            for (BlockPos p : dense) {
                if (!p.equals(last)) tmp.add(p);
                last = p;
            }
            dense = tmp;
        }
        return dense;
    }

    // place inside FreeGraph (static method)
    private static int distToBlocked(VoxelMask3D base, VoxelMask3D blocked, int x, int y, int z, int maxR) {
        // 0 => the sample itself is blocked under combinedFree()
        if (!combinedFree(base, blocked, x, y, z)) return 0;
        for (int r = 1; r <= maxR; r++) {
            int xa = x - r, xb = x + r, ya = y - r, yb = y + r, za = z - r, zb = z + r;
            for (int yy = ya; yy <= yb; yy++) {
                for (int xx = xa; xx <= xb; xx++) {
                    if (!combinedFree(base, blocked, xx, yy, za)) return r;
                    if (!combinedFree(base, blocked, xx, yy, zb)) return r;
                }
            }
            for (int zz = za; zz <= zb; zz++) {
                for (int xx = xa; xx <= xb; xx++) {
                    if (!combinedFree(base, blocked, xx, ya, zz)) return r;
                    if (!combinedFree(base, blocked, xx, yb, zz)) return r;
                }
            }
            for (int zz = za; zz <= zb; zz++) {
                for (int yy = ya; yy <= yb; yy++) {
                    if (!combinedFree(base, blocked, xa, yy, zz)) return r;
                    if (!combinedFree(base, blocked, xb, yy, zz)) return r;
                }
            }
        }
        return maxR + 1;
    }

    // === NEW: soft-repulsion A* variant with verbose logging ===
    /**
     * A* on the coarse graph with a soft "repulsion" cost against a blocked mask.
     * - blocked: voxels that represent carved corridors / rooms we want to avoid
     * - repelRadius: how far (in voxels) we look around a node to estimate proximity penalty
     * - repelCoeff: multiplier for that proximity penalty added to the edge cost
     * Returns empty list on failure (never null).
     */
    List<BlockPos> routeWithRepulsion(BlockPos startWorld, BlockPos goalWorld,
                                      VoxelMask3D blocked,
                                      int repelRadius, float repelCoeff,
                                      Random ignoredRnd) {
        if (nodes.isEmpty()) {
            LogUtils.getLogger().warn("(FreeGraph) routeWithRepulsion: no nodes in graph");
            return List.of();
        }

        // Map endpoints to grid (like route())
        int sx = clampToStride(startWorld.getX() - free.ox, stride, free.nx);
        int sy = clampToStride(startWorld.getY() - free.oy, stride, free.ny);
        int sz = clampToStride(startWorld.getZ() - free.oz, stride, free.nz);
        int gx = clampToStride(goalWorld.getX() - free.ox, stride, free.nx);
        int gy = clampToStride(goalWorld.getY() - free.oy, stride, free.ny);
        int gz = clampToStride(goalWorld.getZ() - free.oz, stride, free.nz);

        int sIdx = findClosestUsableNode(sx, sy, sz, /*blocked hard-test=*/null);
        int gIdx = findClosestUsableNode(gx, gy, gz, /*blocked hard-test=*/null);
        if (sIdx < 0 || gIdx < 0) {
            LogUtils.getLogger().warn("(FreeGraph) routeWithRepulsion: no start/goal nodes (sIdx={}, gIdx={})", sIdx, gIdx);
            return List.of();
        }
        if (sIdx == gIdx) return List.of(startWorld, goalWorld);

        float[] gScore = new float[nodes.size()];
        float[] fScore = new float[nodes.size()];
        int[] prev = new int[nodes.size()];
        Arrays.fill(gScore, Float.POSITIVE_INFINITY);
        Arrays.fill(fScore, Float.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);
        boolean[] closed = new boolean[nodes.size()];

        final Node goal = nodes.get(gIdx);

        record QN(int id, float f) {}
        PriorityQueue<QN> open = new PriorityQueue<>(Comparator.comparingDouble(q -> q.f));

        gScore[sIdx] = 0f;
        fScore[sIdx] = heuristic(nodes.get(sIdx), goal);
        open.add(new QN(sIdx, fScore[sIdx]));

        // small helper: proximity penalty (0 if far; larger when close)
        java.util.function.IntUnaryOperator clampR = v -> Math.max(0, Math.min(v, 127));
        int R = clampR.applyAsInt(repelRadius);
        float K = Math.max(0f, repelCoeff);

        int expanded = 0, skippedBlockedEdge = 0;

        while (!open.isEmpty()) {
            QN q = open.poll();
            int u = q.id;
            if (closed[u]) continue;
            if (u == gIdx) break;
            closed[u] = true;
            expanded++;

            Node nu = nodes.get(u);

            for (Edge e : adj[u]) {
                int v = e.b;
                if (closed[v]) continue;

                Node nv = nodes.get(v);

                // still disallow edges that actually *intersect* blocked voxels (hard constraint)
                if (blocked != null && !lineFree(free, blocked, nu.x, nu.y, nu.z, nv.x, nv.y, nv.z)) {
                    skippedBlockedEdge++;
                    continue;
                }

                // proximity penalty: look in a small cube around nv in the blocked mask, compute 1/(1+d)
                float repel = 0f;
                if (blocked != null && R > 0 && K > 0f) {
                    int wx0 = free.ox + nv.x, wy0 = free.oy + nv.y, wz0 = free.oz + nv.z;
                    int bestD = Integer.MAX_VALUE;
                    // check a sparse shell first; if nothing, cost is 0
                    outer:
                    for (int dy = -R; dy <= R; dy++) {
                        for (int dx = -R; dx <= R; dx++) {
                            for (int dz = -R; dz <= R; dz++) {
                                int bx = blocked.gx(wx0 + dx), by = blocked.gy(wy0 + dy), bz = blocked.gz(wz0 + dz);
                                if (!blocked.in(bx, by, bz) || !blocked.get(bx, by, bz)) continue;
                                int md = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                                if (md < bestD) {
                                    bestD = md;
                                    if (bestD <= 1) break outer; // right next to it
                                }
                            }
                        }
                    }
                    if (bestD != Integer.MAX_VALUE) {
                        // closer => larger penalty; at md=1 => penalty=1.0, at md=R => ~1/R
                        repel = (1.0f / Math.max(1f, bestD));
                    }
                }

                float step = e.w + (K * repel);
                float alt = gScore[u] + step;
                if (alt < gScore[v]) {
                    prev[v] = u;
                    gScore[v] = alt;
                    fScore[v] = alt + heuristic(nv, goal);
                    open.add(new QN(v, fScore[v]));
                }
            }
        }

        if (prev[gIdx] == -1) {
            LogUtils.getLogger().warn("(FreeGraph) routeWithRepulsion: NO PATH  expanded={}  skippedEdges(blocked)={}", expanded, skippedBlockedEdge);
            return List.of();
        }

        // reconstruct, densify, anchor (same as route())
        ArrayList<int[]> coarse = new ArrayList<>();
        for (int t = gIdx; t != -1; t = prev[t]) {
            Node n = nodes.get(t);
            coarse.add(new int[]{n.x, n.y, n.z});
        }
        Collections.reverse(coarse);

        ArrayList<BlockPos> dense = new ArrayList<>();
        dense.add(startWorld);
        int[] cur = coarse.get(0);
        for (int i = 1; i < coarse.size(); i++) {
            int[] nxt = coarse.get(i);
            for (int[] p : voxelLine(cur[0], cur[1], cur[2], nxt[0], nxt[1], nxt[2], /*includeStart*/false)) {
                dense.add(new BlockPos(p[0] + free.ox, p[1] + free.oy, p[2] + free.oz));
            }
            cur = nxt;
        }
        if (dense.isEmpty() || !dense.get(dense.size() - 1).equals(goalWorld)) dense.add(goalWorld);

        // de-dup
        if (dense.size() >= 2) {
            ArrayList<BlockPos> tmp = new ArrayList<>(dense.size());
            BlockPos last = null;
            for (BlockPos p : dense) { if (!p.equals(last)) tmp.add(p); last = p; }
            dense = tmp;
        }
        return dense;
    }

    /**
     * Closest sampled node to (x,y,z) that is free under the avoid-mask.
     * Returns -1 if none qualifies.
     */
    private int findClosestUsableNode(int x, int y, int z, VoxelMask3D blocked) {
        double best = Double.POSITIVE_INFINITY;
        int bi = -1;
        for (Node n : nodes) {
            if (!combinedFree(free, blocked, n.x, n.y, n.z)) continue;
            double dx = n.x - x, dy = n.y - y, dz = n.z - z;
            double d = dx * dx + dy * dy + dz * dz;
            if (d < best) { best = d; bi = n.id; }
        }
        return bi;
    }

    private static boolean combinedFree(VoxelMask3D base, VoxelMask3D blocked, int x, int y, int z) {
        // base coords (x,y,z) are in the ROI-local space of `base`
        if (!base.in(x, y, z)) return false;
        if (!base.get(x, y, z)) return false;

        if (blocked != null) {
            // convert base-local -> world -> blocked-local
            int wx = base.ox + x;
            int wy = base.oy + y;
            int wz = base.oz + z;

            int bx = blocked.gx(wx);
            int by = blocked.gy(wy);
            int bz = blocked.gz(wz);

            if (blocked.in(bx, by, bz) && blocked.get(bx, by, bz)) return false;
        }
        return true;
    }

    private static boolean lineFree(VoxelMask3D base, VoxelMask3D blocked,
                                    int x0, int y0, int z0, int x1, int y1, int z1) {
        for (int[] p : voxelLine(x0, y0, z0, x1, y1, z1, true)) {
            if (!combinedFree(base, blocked, p[0], p[1], p[2])) return false;
        }
        return true;
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

    public VoxelMask3D mask() {
        return this.free;
    }

    record Node(int x, int y, int z, int id) {
    }

    record Edge(int a, int b, float w) {
    }
}