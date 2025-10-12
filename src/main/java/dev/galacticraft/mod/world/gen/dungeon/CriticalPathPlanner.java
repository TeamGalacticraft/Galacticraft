package dev.galacticraft.mod.world.gen.dungeon;

import com.mojang.logging.LogUtils;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

import java.util.*;

/**
 * Extremely robust, log-heavy fallback planner.
 * - buildWeightMatrix(): logs every pairwise weight and the room ids.
 * - solveNearest(): greedy Hamiltonian *path* Entrance -> ... -> Queen.
 *   Never throws; always returns a Result with ok=true when n>=2.
 */
final class CriticalPathPlanner {
    private static final Logger LOGGER = LogUtils.getLogger();

    private CriticalPathPlanner() {}

    static record Result(boolean ok, int[] order, double length, String reason) {}

    static double[][] buildWeightMatrix(java.util.List<RoomPlacer.Placed> nodes) {
        final int n = nodes.size();
        double[][] W = new double[n][n];
        LOGGER.info("[TSP] buildWeightMatrix: nodes={} (0=Entrance, {}=Queen)", n - 1, n - 1);
        for (int i = 0; i < n; i++) {
            var ai = nodes.get(i);
            AABB a = ai.aabb();
            double ax = (a.minX + a.maxX) * 0.5, ay = (a.minY + a.maxY) * 0.5, az = (a.minZ + a.maxZ) * 0.5;

            for (int j = 0; j < n; j++) {
                if (i == j) { W[i][j] = Double.POSITIVE_INFINITY; continue; }
                var bj = nodes.get(j);
                AABB b = bj.aabb();
                double bx = (b.minX + b.maxX) * 0.5, by = (b.minY + b.maxY) * 0.5, bz = (b.minZ + b.maxZ) * 0.5;

                double dx = ax - bx, dy = ay - by, dz = az - bz;
                double d = Math.sqrt(dx*dx + dy*dy + dz*dz);

                // Light bias that prefers EXIT(A)->ENTR(B) if available among portals (optional)
                // For now we only log polarity; routing will re-pick proper ports later.
                W[i][j] = d;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("[TSP] w({}->{}) ~ dist={}", i, j, String.format("%.2f", d));
                }
            }
        }
        return W;
    }

    /**
     * Greedy Hamiltonian path Entrance->...->Queen; does not revisit nodes.
     * - Entrance is index 0; Queen is index n-1; we must end at n-1.
     * - Always returns ok=true if n>=2; ok=false only when n<2.
     */
    static Result solveNearest(java.util.List<RoomPlacer.Placed> nodes) {
        final int n = nodes.size();
        if (n < 2) {
            String r = "[TSP] ERROR: need at least 2 nodes (Entrance + Queen); got " + n;
            LOGGER.error(r);
            return new Result(false, new int[0], 0.0, r);
        }

        double[][] W = buildWeightMatrix(nodes);

        // Path must start at 0 (Entrance) and end at n-1 (Queen).
        boolean[] used = new boolean[n];
        ArrayList<Integer> order = new ArrayList<>(n);
        int cur = 0; used[cur] = true; order.add(cur);

        // Visit all intermediates (1..n-2) greedily by nearest neighbor
        while (order.size() < n - 1) {
            int best = -1;
            double bestW = Double.POSITIVE_INFINITY;
            for (int j = 1; j < n - 1; j++) {
                if (used[j]) continue;
                double w = W[cur][j];
                if (w < bestW) { bestW = w; best = j; }
            }
            if (best < 0) {
                String r = "[TSP] Greedy stalled (no unused middle nodes); will jump to Queen.";
                LOGGER.warn(r);
                break;
            }
            used[best] = true;
            order.add(best);
            cur = best;
        }

        // Finally jump to Queen (n-1)
        order.add(n - 1);

        // Compute path length
        double total = 0.0;
        for (int i = 1; i < order.size(); i++) {
            int a = order.get(i - 1), b = order.get(i);
            total += W[a][b];
        }

        int[] ord = order.stream().mapToInt(Integer::intValue).toArray();
        LOGGER.info("[TSP] Greedy path ok: n={} totalLen≈{} order={}", n, String.format("%.1f", total), java.util.Arrays.toString(ord));
        return new Result(true, ord, total, "greedy");
    }
}