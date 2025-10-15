package dev.galacticraft.mod.world.gen.dungeon.util;

import dev.galacticraft.mod.world.gen.dungeon.DungeonBuilder;
import dev.galacticraft.mod.world.gen.dungeon.records.PortDef;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;

import java.util.*;

/**
 * Shortest Hamiltonian Path (start->end) with EXIT->ENTRANCE port constraint.
 * Exact solver using Held–Karp DP (O(n^2 * 2^n)), n<=20 practical; n=10 trivial.
 */
public final class RoomHamiltonianPath {
    /**
     * Solve shortest Hamiltonian path from startIndex to endIndex (inclusive), visiting every room exactly once.
     *
     * @param rooms      list of rooms (size n ≥ 2)
     * @param startIndex index of start room
     * @param endIndex   index of end room
     * @return optimal path result, or empty if no feasible port chain exists
     */
    public static Optional<PathResult> solve(List<DungeonBuilder.Room> rooms, int startIndex, int endIndex) {
        final int n = rooms.size();
        if (n < 2) return Optional.empty();
        if (startIndex == endIndex) return Optional.empty();

        // Precompute pairwise best distances and remember which exit/entrance ports achieve them.
        final double[][] dist = new double[n][n];
        final int[][] bestExitIdx = new int[n][n];
        final int[][] bestEntrIdx = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], Double.POSITIVE_INFINITY);
            Arrays.fill(bestExitIdx[i], -1);
            Arrays.fill(bestEntrIdx[i], -1);
        }

        for (int i = 0; i < n; i++) {
            final DungeonBuilder.Room ri = rooms.get(i);
            final List<PortDef> iExits = exits(ri);
            if (iExits.isEmpty()) continue;

            // Precompute world positions for exits of room i
            final double[][] exitPos = new double[iExits.size()][3];
            for (int e = 0; e < iExits.size(); e++) {
                exitPos[e] = portCenterWorld(ri, iExits.get(e));
            }

            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                final DungeonBuilder.Room rj = rooms.get(j);
                final List<PortDef> jEntrs = entrances(rj);
                if (jEntrs.isEmpty()) continue;

                // Precompute world positions for entrances of room j
                final double[][] entrPos = new double[jEntrs.size()][3];
                for (int a = 0; a < jEntrs.size(); a++) {
                    entrPos[a] = portCenterWorld(rj, jEntrs.get(a));
                }

                // Find best exit->entrance pair distance
                double best = Double.POSITIVE_INFINITY;
                int be = -1, ba = -1;
                for (int e = 0; e < exitPos.length; e++) {
                    for (int a = 0; a < entrPos.length; a++) {
                        double d = euclid(exitPos[e], entrPos[a]);
                        if (d < best) {
                            best = d;
                            be = e;
                            ba = a;
                        }
                    }
                }
                dist[i][j] = best;
                bestExitIdx[i][j] = be;
                bestEntrIdx[i][j] = ba;
            }
        }

        // Held–Karp for Hamiltonian PATH pinned at start->end.
        // DP[mask][i] = min cost to reach i with visited=mask, starting at startIndex.
        final int FULL = (1 << n) - 1;
        final double[][] DP = new double[1 << n][n];
        final int[][] parent = new int[1 << n][n]; // predecessor node for reconstruction

        for (int m = 0; m < DP.length; m++) {
            Arrays.fill(DP[m], Double.POSITIVE_INFINITY);
            Arrays.fill(parent[m], -1);
        }

        // Initialize at start only
        final int startMask = 1 << startIndex;
        DP[startMask][startIndex] = 0.0;

        // Iterate subsets that include start
        for (int mask = 0; mask <= FULL; mask++) {
            if ((mask & startMask) == 0) continue;

            for (int i = 0; i < n; i++) {
                if (Double.isInfinite(DP[mask][i])) continue;
                if ((mask & (1 << i)) == 0) continue;

                // Do not leave end room unless mask already FULL (end is sink)
                boolean atEnd = (i == endIndex);
                if (atEnd && mask != FULL) continue;

                for (int j = 0; j < n; j++) {
                    if ((mask & (1 << j)) != 0) continue; // already visited
                    if (j == startIndex) continue;        // never return to start

                    double w = dist[i][j];
                    if (Double.isInfinite(w)) continue;    // no feasible exit->entrance pairing

                    int nm = mask | (1 << j);
                    double cand = DP[mask][i] + w;
                    if (cand < DP[nm][j]) {
                        DP[nm][j] = cand;
                        parent[nm][j] = i;
                    }
                }
            }
        }

        // Answer is DP[FULL][endIndex]
        double bestCost = DP[FULL][endIndex];
        if (Double.isInfinite(bestCost)) {
            return Optional.empty();
        }

        // Reconstruct order
        List<Integer> order = new ArrayList<>(n);
        int cur = endIndex;
        int mask = FULL;
        while (cur != -1) {
            order.add(cur);
            int p = parent[mask][cur];
            if (p == -1) break;
            mask ^= (1 << cur);
            cur = p;
        }
        Collections.reverse(order);

        // Sanity: starts/ends correct
        if (order.size() != n || order.get(0) != startIndex || order.get(n - 1) != endIndex) {
            return Optional.empty(); // should not happen, but guard
        }

        // Reconstruct chosen port pairs for each hop using bestExitIdx/bestEntrIdx
        List<int[]> portPairs = new ArrayList<>(n - 1);
        double recomputed = 0.0;
        for (int k = 0; k + 1 < order.size(); k++) {
            int i = order.get(k);
            int j = order.get(k + 1);
            int ex = bestExitIdx[i][j];
            int en = bestEntrIdx[i][j];
            portPairs.add(new int[]{ex, en});
            recomputed += dist[i][j];
        }

        return Optional.of(new PathResult(order, portPairs, recomputed));
    }

    // ---- Public API ----

    private static List<PortDef> entrances(DungeonBuilder.Room r) {
        PortDef[] arr = r.def().entrances();
        if (arr == null) return List.of();
        List<PortDef> out = new ArrayList<>(arr.length);
        for (PortDef p : arr) if (p != null && p.entrance()) out.add(p);
        return out;
    }

    // ---- Helpers ----

    private static List<PortDef> exits(DungeonBuilder.Room r) {
        PortDef[] arr = r.def().exits();
        if (arr == null) return List.of();
        List<PortDef> out = new ArrayList<>(arr.length);
        for (PortDef p : arr) if (p != null && p.exit()) out.add(p);
        return out;
    }

    /**
     * Center point of the AABB face corresponding to the port's (rotated) facing.
     */
    private static double[] portCenterWorld(DungeonBuilder.Room room, PortDef port) {
        Direction face = rotate(port.facing(), room.rotation());
        AABB box = room.aabb();

        double cx = 0.5 * (box.minX + box.maxX);
        double cy = 0.5 * (box.minY + box.maxY);
        double cz = 0.5 * (box.minZ + box.maxZ);

        return switch (face) {
            case NORTH -> new double[]{cx, cy, box.minZ}; // -Z
            case SOUTH -> new double[]{cx, cy, box.maxZ}; // +Z
            case WEST -> new double[]{box.minX, cy, cz}; // -X
            case EAST -> new double[]{box.maxX, cy, cz}; // +X
            case DOWN -> new double[]{cx, box.minY, cz}; // -Y
            case UP -> new double[]{cx, box.maxY, cz}; // +Y
        };
    }

    private static double euclid(double[] a, double[] b) {
        double dx = a[0] - b[0], dy = a[1] - b[1], dz = a[2] - b[2];
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Rotate a Direction by a Block Rotation (Minecraft semantics).
     */
    private static Direction rotate(Direction d, Rotation r) {
        // Rotation only affects horizontal in Minecraft; UP/DOWN unchanged.
        if (d.getAxis().isVertical()) return d;
        return switch (r) {
            case NONE -> d;
            case CLOCKWISE_90 -> d.getClockWise();
            case CLOCKWISE_180 -> d.getClockWise().getClockWise();
            case COUNTERCLOCKWISE_90 -> d.getCounterClockWise();
        };
    }

    // ---- Result container ----
    public static final class PathResult {
        /**
         * Indices into the input rooms list, in visiting order (includes start and end).
         */
        public final List<Integer> order;
        /**
         * For each hop k: portPairs[k] = {fromExitIndexInRoom, toEntranceIndexInRoom}; size = order.size()-1
         */
        public final List<int[]> portPairs;
        /**
         * Sum of hop distances using chosen ports.
         */
        public final double totalDistance;

        PathResult(List<Integer> order, List<int[]> portPairs, double totalDistance) {
            this.order = order;
            this.portPairs = portPairs;
            this.totalDistance = totalDistance;
        }
    }
}