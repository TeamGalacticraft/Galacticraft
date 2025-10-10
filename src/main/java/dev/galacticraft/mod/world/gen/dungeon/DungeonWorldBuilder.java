package dev.galacticraft.mod.world.gen.dungeon;

import com.mojang.logging.LogUtils;
import dev.galacticraft.mod.ServerHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import org.slf4j.Logger;

import java.util.*;

/**
 * World builder using "rooms first, corridors later".
 * Adds: degree-balanced planning of BASIC vs. TREASURE rooms, and dead-end filtering
 * so that total exits == total entrances. Also includes safer pass-through fallback
 * when picking OUT!=IN within pass-through rooms during critical path construction.
 */
public final class DungeonWorldBuilder {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final DungeonConfig cfg;
    private final RandomSource rnd;

    // cache for scans (fast)
    private final Map<ResourceLocation, ScannedTemplate> scanCache = new HashMap<>();

    public DungeonWorldBuilder(DungeonConfig cfg, RandomSource rnd) {
        this.cfg = cfg;
        this.rnd = rnd;
    }

    private static int corridorApertureFor(FlowRouter.RoutedPair rp, ProcConfig pc) {
        // Base corridor idea from config (independent of port size)
        int base = 2 * pc.rCorr + 1;

        // Real port apertures from the templates (min 3, must be odd already)
        int aAp = Math.max(3, rp.A().port().aperture());
        int bAp = Math.max(3, rp.B().port().aperture());

        // Return the MAX to ensure our bounding boxes never clip the carve
        return Math.max(base, Math.max(aAp, bAp));
    }

    /**
     * Fork spawning is disabled: corridors never create path forks.
     */
    private static List<FlowRouter.RoutedPair> spawnForks(
            List<FlowRouter.PNode> P,
            List<FlowRouter.EdgeCand> CE,
            List<FlowRouter.RoutedPair> already,
            List<RoomPlacer.Placed> placed,
            ProcConfig pc,
            Random rnd,
            int maxForksTotal,
            RoomPlacer.Placed endRoom
    ) {
        return java.util.Collections.emptyList();
    }

    private static List<FlowRouter.RoutedPair> growBranch(
            Map<FlowRouter.PNode, List<FlowRouter.PNode>> nbrs,
            Set<RoomPlacer.Placed> wired,
            FlowRouter.PNode seed,
            Random rnd,
            ProcConfig pc
    ) {
        ArrayList<FlowRouter.RoutedPair> out = new ArrayList<>();
        FlowRouter.PNode prev = null;
        FlowRouter.PNode cur = seed;

        int maxLen = 2 + rnd.nextInt(4); // 2..5 hops

        for (int i = 0; i < maxLen; i++) {
            List<FlowRouter.PNode> cand = nbrs.getOrDefault(cur, Collections.emptyList());
            FlowRouter.PNode pick = null;
            double best = Double.NEGATIVE_INFINITY;

            for (FlowRouter.PNode nx : cand) {
                if (nx.room() == cur.room()) continue;
                if (wired.contains(nx.room())) continue; // grow into new rooms

                double score = -cur.worldPos().distSqr(nx.worldPos()) * 0.0005 + (rnd.nextDouble() - 0.5) * 0.05;

                if (prev != null) {
                    int vx1 = cur.worldPos().getX() - prev.worldPos().getX();
                    int vy1 = cur.worldPos().getY() - prev.worldPos().getY();
                    int vz1 = cur.worldPos().getZ() - prev.worldPos().getZ();
                    int vx2 = nx.worldPos().getX() - cur.worldPos().getX();
                    int vy2 = nx.worldPos().getY() - cur.worldPos().getY();
                    int vz2 = nx.worldPos().getZ() - cur.worldPos().getZ();
                    double dot = vx1 * vx2 + vy1 * vy2 + vz1 * vz2;
                    double n1 = Math.sqrt((double) vx1 * vx1 + vy1 * vy1 + vz1 * vz1);
                    double n2 = Math.sqrt((double) vx2 * vx2 + vy2 * vy2 + vz2 * vz2);
                    if (n1 > 0 && n2 > 0) {
                        double cos = Math.abs(dot / (n1 * n2)); // 1=straight, 0=orthogonal
                        score += (1.0 - cos) * (0.75 * pc.forkTurnHardness);
                    }
                } else {
                    score += 0.15 * pc.forkTurnHardness;
                }

                boolean likelyTip = (i >= maxLen - 2);
                if (likelyTip && nx.room().def().type() == RoomTemplateDef.RoomType.BRANCH_END) {
                    score += 0.6;
                }

                if (score > best) {
                    best = score;
                    pick = nx;
                }
            }
            if (pick == null) break;

            out.add(new FlowRouter.RoutedPair(cur, pick));
            wired.add(pick.room());
            prev = cur;
            cur = pick;
        }
        return out;
    }

    private static List<FlowRouter.PNode> greedyNearestChain(
            List<FlowRouter.PNode> P,
            List<FlowRouter.EdgeCand> CE,
            Map<FlowRouter.PNode, List<FlowRouter.PNode>> nbrs,
            FlowRouter.PNode start,
            FlowRouter.PNode goal,
            int maxHops,
            Set<RoomPlacer.Placed> used,
            Random rnd
    ) {
        ArrayList<FlowRouter.PNode> chain = new ArrayList<>();
        FlowRouter.PNode cur = start;

        for (int i = 0; i < maxHops; i++) {
            List<FlowRouter.PNode> cand = nbrs.getOrDefault(cur, Collections.emptyList());
            if (cand.isEmpty()) break;

            FlowRouter.PNode best = null;
            double bestScore = Double.POSITIVE_INFINITY;

            for (FlowRouter.PNode nx : cand) {
                if (nx.room() == cur.room()) continue;
                if (used.contains(nx.room())) continue;
                double dist = nx.worldPos().distSqr(goal.worldPos());
                dist *= (0.98 + 0.04 * rnd.nextDouble());
                if (dist < bestScore) {
                    bestScore = dist;
                    best = nx;
                }
            }
            if (best == null) break;

            chain.add(best);
            cur = best;
            if (cur.room() == goal.room()) break;
        }
        return chain;
    }

    private static float clampShell(float min, float max, BlockPos surface, net.minecraft.world.level.LevelHeightAccessor h) {
        int worldSpan = surface.getY() - h.getMinBuildHeight();
        float verticalRoom = Math.max(24f, worldSpan - 12f);
        float s = Math.min(max, Math.max(min, verticalRoom * 0.60f));
        return s;
    }

    private static Direction dirFrom(List<BlockPos> path, int i) {
        if (path.isEmpty()) return Direction.NORTH;
        int j = Math.max(0, Math.min(path.size() - 1, i));
        BlockPos a = path.get(j), b = path.get(Math.min(j + 1, path.size() - 1));
        int dx = b.getX() - a.getX(), dy = b.getY() - a.getY(), dz = b.getZ() - a.getZ();
        int ax = Math.abs(dx), ay = Math.abs(dy), az = Math.abs(dz);
        if (ax >= ay && ax >= az) return dx > 0 ? Direction.EAST : Direction.WEST;
        if (az >= ax && az >= ay) return dz > 0 ? Direction.SOUTH : Direction.NORTH;
        return dy > 0 ? Direction.UP : Direction.DOWN;
    }

    private static BoundingBox corridorBox(List<BlockPos> path, int aperture) {
        int r = Math.max(0, (aperture - 1) / 2);
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (BlockPos p : path) {
            minX = Math.min(minX, p.getX() - r);
            minY = Math.min(minY, p.getY() - r);
            minZ = Math.min(minZ, p.getZ() - r);
            maxX = Math.max(maxX, p.getX() + r);
            maxY = Math.max(maxY, p.getY() + r);
            maxZ = Math.max(maxZ, p.getZ() + r);
        }
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static List<BlockPos> dockedRoute(
            FreeGraph G,
            FlowRouter.RoutedPair pair,
            int preKickOut,
            int preStraight,
            int goalKickOut,
            int goalStraightIn
    ) {
        Direction aFace = pair.A().facing();
        Direction bFace = pair.B().facing();

        BlockPos aPort = pair.A().worldPos();
        BlockPos bPort = pair.B().worldPos();

        // step out of each portal along its facing
        BlockPos a2 = aPort.relative(aFace, Math.max(1, preKickOut));
        BlockPos a4 = a2.relative(aFace, Math.max(0, preStraight));
        BlockPos b4 = bPort.relative(bFace, Math.max(1, goalKickOut));

        List<BlockPos> mid;
        try {
            mid = G.route(a4, b4);
        } catch (Throwable t) {
            mid = Collections.emptyList();
        }
        if (mid == null || mid.isEmpty() || mid.size() < 2) return Collections.emptyList();

        ArrayList<BlockPos> path = new ArrayList<>(mid.size() + preStraight + goalStraightIn);

        // lead-out run from A
        if (preStraight > 0) {
            BlockPos cur = a2;
            for (int i = 0; i < preStraight; i++) {
                path.add(cur);
                cur = cur.relative(aFace, 1);
            }
        } else {
            path.add(a2);
        }

        // backbone segment
        path.addAll(mid);

        // straight-in run toward B
        if (goalStraightIn > 0) {
            BlockPos cur = b4;
            Direction towardB = bFace.getOpposite();
            for (int i = 0; i < goalStraightIn; i++) {
                cur = cur.relative(towardB, 1);
                path.add(cur);
            }
        }

        // de-dup consecutive equal nodes
        if (path.size() >= 2) {
            ArrayList<BlockPos> dedup = new ArrayList<>(path.size());
            BlockPos last = null;
            for (BlockPos p : path) {
                if (!p.equals(last)) dedup.add(p);
                last = p;
            }
            return dedup;
        }
        return path;
    }

    private static Map<RoomPlacer.Placed, Set<RoomPlacer.Placed>> buildRoomAdj(List<FlowRouter.PNode> P, List<FlowRouter.EdgeCand> CE) {
        Map<RoomPlacer.Placed, Set<RoomPlacer.Placed>> adj = new HashMap<>();
        for (FlowRouter.EdgeCand ec : CE) {
            FlowRouter.PNode a = P.get(ec.a()), b = P.get(ec.b());
            if (a.room() == b.room()) continue;
            adj.computeIfAbsent(a.room(), k -> new HashSet<>()).add(b.room());
            adj.computeIfAbsent(b.room(), k -> new HashSet<>()).add(a.room());
        }
        return adj;
    }

    private static FlowRouter.RoutedPair pickPortalPairForRooms(
            RoomPlacer.Placed from, RoomPlacer.Placed to,
            List<FlowRouter.PNode> P, List<FlowRouter.EdgeCand> CE) {

        FlowRouter.EdgeCand best = null;
        float bestW = Float.POSITIVE_INFINITY;
        for (FlowRouter.EdgeCand ec : CE) {
            FlowRouter.PNode A = P.get(ec.a()), B = P.get(ec.b());
            if (A.room() != from || B.room() != to) continue;
            if (!isExitNode(A) || !isEntranceNode(B)) continue;      // <-- enforce direction
            if (ec.w() < bestW) { bestW = ec.w(); best = ec; }
        }
        if (best == null) return null;
        return new FlowRouter.RoutedPair(P.get(best.a()), P.get(best.b()));
    }

    private static FlowRouter.RoutedPair pickEdgeFromRoomToRoomAvoidingA(
            RoomPlacer.Placed fromRoom, RoomPlacer.Placed toRoom, FlowRouter.PNode avoidA,
            List<FlowRouter.PNode> P, List<FlowRouter.EdgeCand> CE) {

        FlowRouter.EdgeCand best = null;
        float bestW = Float.POSITIVE_INFINITY;
        for (FlowRouter.EdgeCand ec : CE) {
            FlowRouter.PNode A = P.get(ec.a()), B = P.get(ec.b());
            if (A.room() != fromRoom || B.room() != toRoom) continue;
            if (avoidA != null && A == avoidA) continue;
            if (!isExitNode(A) || !isEntranceNode(B)) continue;      // <-- enforce direction
            if (ec.w() < bestW) { bestW = ec.w(); best = ec; }
        }
        if (best == null) return null;
        return new FlowRouter.RoutedPair(P.get(best.a()), P.get(best.b()));
    }

    private static Map<RoomPlacer.Placed, Integer> bfsDist(
            Map<RoomPlacer.Placed, Set<RoomPlacer.Placed>> adj,
            RoomPlacer.Placed src,
            Set<RoomPlacer.Placed> blocked,
            RoomPlacer.Placed allow) {
        ArrayDeque<RoomPlacer.Placed> q = new ArrayDeque<>();
        Map<RoomPlacer.Placed, Integer> dist = new HashMap<>();
        q.add(src);
        dist.put(src, 0);
        while (!q.isEmpty()) {
            var u = q.removeFirst();
            int du = dist.get(u);
            for (var v : adj.getOrDefault(u, Collections.emptySet())) {
                if (v != allow && blocked.contains(v)) continue;
                if (dist.containsKey(v)) continue;
                dist.put(v, du + 1);
                q.addLast(v);
            }
        }
        return dist;
    }

    private static Map<RoomPlacer.Placed, RoomPlacer.Placed> bfsParents(
            Map<RoomPlacer.Placed, Set<RoomPlacer.Placed>> adj,
            RoomPlacer.Placed src,
            Set<RoomPlacer.Placed> blocked,
            RoomPlacer.Placed goal,
            RoomPlacer.Placed allow) {
        ArrayDeque<RoomPlacer.Placed> q = new ArrayDeque<>();
        Map<RoomPlacer.Placed, RoomPlacer.Placed> par = new HashMap<>();
        q.add(src);
        par.put(src, null);
        while (!q.isEmpty()) {
            var u = q.removeFirst();
            if (u == goal) break;
            for (var v : adj.getOrDefault(u, Collections.emptySet())) {
                if (v != allow && blocked.contains(v)) continue;
                if (par.containsKey(v)) continue;
                par.put(v, u);
                q.addLast(v);
            }
        }
        return par;
    }

    private static List<RoomPlacer.Placed> rebuildPath(
            Map<RoomPlacer.Placed, RoomPlacer.Placed> par,
            RoomPlacer.Placed dst) {
        ArrayList<RoomPlacer.Placed> path = new ArrayList<>();
        if (!par.containsKey(dst)) return path;
        for (RoomPlacer.Placed v = dst; v != null; v = par.get(v)) path.add(v);
        Collections.reverse(path);
        return path;
    }

    private static List<RoomPlacer.Placed> joinPaths(List<RoomPlacer.Placed> a, List<RoomPlacer.Placed> b) {
        if (!a.isEmpty() && !b.isEmpty() && a.get(a.size() - 1) == b.get(0)) {
            ArrayList<RoomPlacer.Placed> out = new ArrayList<>(a.size() + b.size() - 1);
            out.addAll(a);
            out.addAll(b.subList(1, b.size()));
            return out;
        }
        ArrayList<RoomPlacer.Placed> out = new ArrayList<>(a.size() + b.size());
        out.addAll(a);
        out.addAll(b);
        return out;
    }

    private static RoomPlacer.Placed pickWaypoint(
            Map<RoomPlacer.Placed, Integer> dE,
            Map<RoomPlacer.Placed, Integer> dQ,
            Set<RoomPlacer.Placed> used,
            int targetLen,
            Random rnd) {
        RoomPlacer.Placed best = null;
        int bestLen = -1;
        int overBest = Integer.MAX_VALUE;
        RoomPlacer.Placed overPick = null;

        for (var v : dE.keySet()) {
            if (!dQ.containsKey(v)) continue;
            if (used.contains(v)) continue;
            int L = dE.get(v) + dQ.get(v);
            if (L <= targetLen) {
                if (L > bestLen || (L == bestLen && rnd.nextBoolean())) {
                    bestLen = L;
                    best = v;
                }
            } else {
                int over = L - targetLen;
                if (over < overBest || (over == overBest && rnd.nextBoolean())) {
                    overBest = over;
                    overPick = v;
                }
            }
        }
        return (best != null) ? best : overPick;
    }

    /**
     * Build 3 disjoint critical room-paths with a fixed entrance portal per path set.
     * Adds pass-through safety: if OUT!=IN cannot be enforced for a hop, falls back
     * to any portal pair between rooms to avoid collapsing the whole chain.
     */
    private static List<FlowRouter.RoutedPair> buildCriticalRoomPaths(
            List<RoomPlacer.Placed> placed,
            List<FlowRouter.PNode> P,
            List<FlowRouter.EdgeCand> CE,
            RoomPlacer.Placed entranceRoom,
            RoomPlacer.Placed endRoom,
            List<RoomPlacer.Placed> queenRooms, // expect up to 3
            ProcConfig pc,
            Random rnd
    ) {
        ArrayList<FlowRouter.RoutedPair> criticalPairs = new ArrayList<>();

        if (entranceRoom == null || endRoom == null || queenRooms == null || queenRooms.isEmpty()) {
            return criticalPairs;
        }

        // ---- Compute budgets: (pc.criticalBasicFraction) of all rooms, split evenly across paths (clamped to available BASICs) ----
        java.util.function.Predicate<RoomPlacer.Placed> isBasic =
                r -> r.def().type() == RoomTemplateDef.RoomType.BASIC;
        int total = placed.size();
        int totalBasicAvailable = 0;
        for (RoomPlacer.Placed r : placed) if (isBasic.test(r)) totalBasicAvailable++;

        float f = Math.max(0f, Math.min(1f, pc.criticalBasicFraction));
        int totalBasicBudget = Math.max(0, Math.round(total * f));
        totalBasicBudget = Math.min(totalBasicBudget, totalBasicAvailable);

        int paths = Math.min(3, queenRooms.size());
        if (paths == 0 || totalBasicBudget == 0) return criticalPairs;

        int base = totalBasicBudget / paths, rem = totalBasicBudget % paths;
        int[] budget = new int[paths];
        for (int i = 0; i < paths; i++) budget[i] = base + (i < rem ? 1 : 0);

        // ---- Directed room-level adjacency from candidate edges ----
        Map<RoomPlacer.Placed, List<RoomPlacer.Placed>> dirAdj = new HashMap<>();
        for (FlowRouter.EdgeCand ec : CE) {
            FlowRouter.PNode a = P.get(ec.a());
            FlowRouter.PNode b = P.get(ec.b());
            if (a.room() == b.room()) continue;
            dirAdj.computeIfAbsent(a.room(), k -> new ArrayList<>()).add(b.room());
        }

        // ---- Index portals; collect entrance portals ----
        Map<FlowRouter.PNode, Integer> idxOf = new HashMap<>();
        for (int i = 0; i < P.size(); i++) idxOf.put(P.get(i), i);

        ArrayList<FlowRouter.PNode> entrancePorts = new ArrayList<>();
        for (FlowRouter.PNode pn : P) if (pn.room() == entranceRoom) entrancePorts.add(pn);
        if (entrancePorts.isEmpty()) return criticalPairs;

        Map<FlowRouter.PNode, Integer> fanoutBasic = new HashMap<>();
        for (FlowRouter.PNode ep : entrancePorts) {
            int ei = idxOf.get(ep);
            HashSet<RoomPlacer.Placed> uniq = new HashSet<>();
            for (FlowRouter.EdgeCand ec : CE) {
                if (ec.a() != ei) continue;
                FlowRouter.PNode tb = P.get(ec.b());
                if (tb.room() != entranceRoom && tb.room() != endRoom && isBasic.test(tb.room())) {
                    uniq.add(tb.room());
                }
            }
            fanoutBasic.put(ep, uniq.size());
        }
        entrancePorts.sort((a, b) -> Integer.compare(fanoutBasic.getOrDefault(b, 0), fanoutBasic.getOrDefault(a, 0)));

        HashSet<RoomPlacer.Placed> used = new HashSet<>();
        used.add(entranceRoom);
        used.add(endRoom);

        java.util.function.BiConsumer<RoomPlacer.Placed, RoomPlacer.Placed> emitPair = (from, to) -> {
            FlowRouter.RoutedPair rp = pickPortalPairForRooms(from, to, P, CE);
            if (rp != null) criticalPairs.add(rp);
        };

        for (FlowRouter.PNode entrancePort : entrancePorts) {
            ArrayList<FlowRouter.RoutedPair> savepoint = new ArrayList<>(criticalPairs);
            HashSet<RoomPlacer.Placed> usedThisPort = new HashSet<>(used);

            boolean allOk = true;

            int ei = idxOf.get(entrancePort);
            ArrayList<RoomPlacer.Placed> firstHopBasicsAll = new ArrayList<>();
            {
                HashSet<RoomPlacer.Placed> uniq = new HashSet<>();
                for (FlowRouter.EdgeCand ec : CE) {
                    if (ec.a() != ei) continue;
                    RoomPlacer.Placed tgtRoom = P.get(ec.b()).room();
                    if (tgtRoom != entranceRoom && tgtRoom != endRoom && isBasic.test(tgtRoom)) {
                        uniq.add(tgtRoom);
                    }
                }
                firstHopBasicsAll.addAll(uniq);
            }

            for (int pth = 0; pth < Math.min(3, queenRooms.size()); pth++) {
                RoomPlacer.Placed queen = queenRooms.get(pth);

                ArrayList<RoomPlacer.Placed> firstHopBasics = new ArrayList<>();
                for (RoomPlacer.Placed r : firstHopBasicsAll) if (!usedThisPort.contains(r)) firstHopBasics.add(r);
                Collections.shuffle(firstHopBasics, rnd);

                List<RoomPlacer.Placed> chosenChain = Collections.emptyList();
                RoomPlacer.Placed chosenFirst = null;
                FlowRouter.PNode chosenFirstPortalB = null;

                attemptFirst:
                for (RoomPlacer.Placed first : firstHopBasics) {
                    FlowRouter.PNode bestB = null;
                    float bestW = Float.POSITIVE_INFINITY;
                    for (FlowRouter.EdgeCand ec : CE) {
                        if (ec.a() != ei) continue;
                        FlowRouter.PNode bpn = P.get(ec.b());
                        if (bpn.room() == first && ec.w() < bestW) {
                            bestW = ec.w();
                            bestB = bpn;
                        }
                    }
                    if (bestB == null) continue;

                    int basicStepsAfterFirst = Math.max(0, budget[pth] - 1);

                    List<RoomPlacer.Placed> chain = findExactChain(dirAdj, first, queen, usedThisPort, basicStepsAfterFirst, rnd);
                    if (!chain.isEmpty()) {
                        chosenChain = chain;
                        chosenFirst = first;
                        chosenFirstPortalB = bestB;
                        break attemptFirst;
                    }
                }

                if (chosenChain.isEmpty() || chosenFirst == null || chosenFirstPortalB == null) {
                    allOk = false;
                    break;
                }

                FlowRouter.RoutedPair firstIn = new FlowRouter.RoutedPair(entrancePort, chosenFirstPortalB);
                criticalPairs.add(firstIn);
                FlowRouter.PNode lastInOnCurrent = chosenFirstPortalB;

                for (int k = 0; k + 1 < chosenChain.size(); k++) {
                    RoomPlacer.Placed fromRoom = chosenChain.get(k);
                    RoomPlacer.Placed toRoom   = chosenChain.get(k + 1);

                    FlowRouter.RoutedPair outPair =
                            pickEdgeFromRoomToRoomAvoidingA(fromRoom, toRoom, lastInOnCurrent, P, CE);
                    // NEW: pass-through fallback (allow OUT==IN if geometry forces it)
                    if (outPair == null) outPair = pickPortalPairForRooms(fromRoom, toRoom, P, CE);

                    if (outPair == null) { allOk = false; break; }
                    criticalPairs.add(outPair);
                    lastInOnCurrent = outPair.B();
                }

                if (allOk) {
                    FlowRouter.RoutedPair q2e =
                            pickEdgeFromRoomToRoomAvoidingA(queen, endRoom, lastInOnCurrent, P, CE);
                    // NEW: fallback
                    if (q2e == null) q2e = pickPortalPairForRooms(queen, endRoom, P, CE);

                    if (q2e == null) { allOk = false; }
                    else criticalPairs.add(q2e);
                }

                for (RoomPlacer.Placed r : chosenChain) {
                    if (r != queen) usedThisPort.add(r);
                }
                usedThisPort.add(queen);

                try {
                    String s = chosenChain.stream().map(r -> r.def().id().toString())
                            .reduce((a, b) -> a + " -> " + b).orElse("<empty>");
                    LOGGER.info("[Critical] Path #{} (budget BASICs={}): EntrancePort -> {}  -> End",
                            (pth + 1), budget[pth], s);
                } catch (Throwable ignored) {}
            }

            if (allOk) {
                used.addAll(usedThisPort);
                return criticalPairs;
            } else {
                criticalPairs.clear();
                criticalPairs.addAll(savepoint);
            }
        }

        return criticalPairs;
    }

    /**
     * DFS to find an exact-length BASIC-only chain that ends at the given queen:
     *   startBasic -> (basicSteps more BASIC rooms) -> queen
     * Returns [startBasic, ..., queen] or empty if not possible under 'usedGlobal' disjointness.
     */
    private static List<RoomPlacer.Placed> findExactChain(
            Map<RoomPlacer.Placed, List<RoomPlacer.Placed>> dirAdj,
            RoomPlacer.Placed startBasic,
            RoomPlacer.Placed queen,
            Set<RoomPlacer.Placed> usedGlobal,
            int basicSteps,
            Random rnd
    ) {
        java.util.function.Predicate<RoomPlacer.Placed> isBasic =
                r -> r.def().type() == RoomTemplateDef.RoomType.BASIC;

        ArrayList<RoomPlacer.Placed> basicsPath = new ArrayList<>();
        basicsPath.add(startBasic);

        class DFS {
            boolean go(RoomPlacer.Placed cur, int usedBasics) {
                if (usedBasics == basicSteps) {
                    // final hop must be cur -> queen
                    List<RoomPlacer.Placed> outs = dirAdj.getOrDefault(cur, Collections.emptyList());
                    return outs.contains(queen);
                }
                List<RoomPlacer.Placed> outs = new ArrayList<>(dirAdj.getOrDefault(cur, Collections.emptyList()));
                Collections.shuffle(outs, rnd);
                for (RoomPlacer.Placed nx : outs) {
                    if (!isBasic.test(nx)) continue;
                    if (usedGlobal.contains(nx)) continue;
                    if (basicsPath.contains(nx)) continue; // local cycle guard
                    basicsPath.add(nx);
                    if (go(nx, usedBasics + 1)) return true;
                    basicsPath.remove(basicsPath.size() - 1);
                }
                return false;
            }
        }

        if (!isBasic.test(startBasic) || usedGlobal.contains(startBasic)) return Collections.emptyList();
        boolean ok = new DFS().go(startBasic, 0);
        if (!ok) return Collections.emptyList();

        ArrayList<RoomPlacer.Placed> chain = new ArrayList<>(2 + basicSteps);
        chain.addAll(basicsPath);
        chain.add(queen);
        return chain;
    }

    private static List<FlowRouter.RoutedPair> dedupePairs(List<FlowRouter.RoutedPair> in) {
        LinkedHashMap<Long, FlowRouter.RoutedPair> m = new LinkedHashMap<>();
        for (FlowRouter.RoutedPair rp : in) {
            long keyA = rp.A().worldPos().asLong();
            long keyB = rp.B().worldPos().asLong();
            long key = (keyA * 1469598103934665603L) ^ keyB;
            m.putIfAbsent(key, rp);
        }
        return new ArrayList<>(m.values());
    }

    // ---------- NEW: Degree planning helpers ----------

    private record Deg(int in, int out) {}
    private Deg degreeOf(RoomTemplateDef def) {
        ScannedTemplate s = scan(def.id());
        return new Deg(s.entrances().size(), s.exits().size());
    }

    private static class DegreePlan {
        final List<RoomTemplateDef> basicsExact;
        final List<RoomTemplateDef> treasuresExact;
        DegreePlan(List<RoomTemplateDef> b, List<RoomTemplateDef> t) {
            this.basicsExact = b; this.treasuresExact = t;
        }
    }

    /**
     * Plan a degree-balanced multiset:
     * - fixed rooms: Entrance (+3 out), End (+3 in), Queens (0 net)
     * - treasure rooms: (+1 in, 0 out) each
     * Condition: sum(basic(out - in)) == treasureCount
     */
    private DegreePlan planDegreeMix(Random rnd, int targetRooms,
                                     List<RoomTemplateDef> BASIC,
                                     List<RoomTemplateDef> BRANCH_END,
                                     ProcConfig pc) {
        int queens = 3;
        int fixed = 1 /*entrance*/ + 1 /*end*/ + queens;
        int remaining = Math.max(0, targetRooms - fixed);

        float tf = Math.max(0f, Math.min(1f, pc.treasureFraction));
        int treasureCount = Math.max(0, Math.round(remaining * tf));
        treasureCount = Math.min(treasureCount, remaining);
        int basicCount = remaining - treasureCount;

        Map<Integer, List<RoomTemplateDef>> byDelta = new HashMap<>();
        for (RoomTemplateDef def : BASIC) {
            Deg d = degreeOf(def);
            byDelta.computeIfAbsent(d.out() - d.in(), k -> new ArrayList<>()).add(def);
        }

        ArrayList<RoomTemplateDef> pickBasics = new ArrayList<>(basicCount);
        int needDelta = treasureCount;

        // Prefer +1 delta basics first
        while (needDelta > 0 && pickBasics.size() < basicCount) {
            List<RoomTemplateDef> list = byDelta.getOrDefault(1, Collections.emptyList());
            if (list.isEmpty()) break;
            pickBasics.add(list.get(rnd.nextInt(list.size())));
            needDelta -= 1;
        }
        // Then larger positives
        for (int d = 2; needDelta > 0 && d <= 4; d++) {
            List<RoomTemplateDef> list = byDelta.get(d);
            if (list == null || list.isEmpty()) continue;
            while (needDelta > 0 && pickBasics.size() < basicCount) {
                pickBasics.add(list.get(rnd.nextInt(list.size())));
                needDelta -= d;
            }
        }
        // Overshoot correction with negatives
        for (int d = -1; needDelta < 0 && d >= -4; d--) {
            List<RoomTemplateDef> list = byDelta.get(d);
            if (list == null || list.isEmpty()) continue;
            while (needDelta < 0 && pickBasics.size() < basicCount) {
                pickBasics.add(list.get(rnd.nextInt(list.size())));
                needDelta -= d;
            }
        }
        // Fill remainder with delta==0 (or anything) to reach basicCount
        List<RoomTemplateDef> zeros = byDelta.getOrDefault(0, Collections.emptyList());
        while (pickBasics.size() < basicCount) {
            RoomTemplateDef def = zeros.isEmpty()
                    ? BASIC.get(rnd.nextInt(BASIC.size()))
                    : zeros.get(rnd.nextInt(zeros.size()));
            pickBasics.add(def);
        }

        ArrayList<RoomTemplateDef> pickTreasures = new ArrayList<>(treasureCount);
        for (int i = 0; i < treasureCount; i++) {
            pickTreasures.add(BRANCH_END.get(rnd.nextInt(BRANCH_END.size())));
        }
        Collections.shuffle(pickBasics, rnd);
        Collections.shuffle(pickTreasures, rnd);
        LOGGER.info("[Proc/Plan] basicsExact={} treasuresExact={} target={}, needDelta(after)={}",
                pickBasics.size(), pickTreasures.size(), targetRooms, needDelta);
        return new DegreePlan(pickBasics, pickTreasures);
    }

    // ---------- scan cache helper ----------
    private ScannedTemplate scan(ResourceLocation id) {
        return scanCache.computeIfAbsent(id, rid -> TemplatePortScanner.scan(ServerHolder.get().getStructureManager(), rid));
    }

    // ---------- MAIN BUILD ----------
    public boolean build(Structure.GenerationContext ctx,
                         StructurePiecesBuilder out,
                         BlockPos surface,
                         BlockPos centerYHint) {

        int budget = rnd.nextIntBetweenInclusive(cfg.minPoints(), cfg.maxPoints());
        LOGGER.info("[Proc] Budget={} points  surface={}  centerHint={}", budget, surface, centerYHint);

        // --- entrance/end template scans ---
        RoomTemplateDef entDef = RoomDefs.ENTRANCE;
        RoomTemplateDef endDef = RoomDefs.pickEnd(new java.util.Random(rnd.nextLong()));
        ScannedTemplate entScan = scan(entDef.id());
        ScannedTemplate endScan = scan(endDef.id());
        if (entScan == null || endScan == null) return fail("scan() returned null for entrance/end");

        final int worldMinY = ctx.heightAccessor().getMinBuildHeight();
        final int surfY = surface.getY();
        final int drop = 28 + rnd.nextInt(18);

        // desired centers (vertical line, end below entrance)
        BlockPos entranceCenterWorld = surface.below(30);
        BlockPos endCenterWorld = new BlockPos(
                entranceCenterWorld.getX(),
                Math.max(worldMinY + 12, Math.min(centerYHint.getY(), entranceCenterWorld.getY() - drop)),
                entranceCenterWorld.getZ()
        );
        LOGGER.info("[Proc] Desired centers: entranceCenterWorld={}  endCenterWorld={}  (drop={})",
                entranceCenterWorld, endCenterWorld, entranceCenterWorld.getY() - endCenterWorld.getY());

        // base proc config
        ProcConfig base = ProcConfig.fromDungeonConfig(this.cfg, rnd);
        float baseShell = clampShell(base.shellMin, base.shellMax, surface, ctx.heightAccessor());
        LOGGER.info("[Proc] Shell: base={}  (min={} max={})", String.format("%.1f", baseShell), base.shellMin, base.shellMax);

        int passes = base.maxRelaxPasses + 1;
        for (int pass = 0; pass < passes; pass++) {
            // derive per-pass pc (relaxation only reduces pads/room-clearance a little)
            ProcConfig pc = new ProcConfig();
            pc.minRooms = base.minRooms;
            pc.targetRooms = base.targetRooms;
            pc.maxRooms = base.maxRooms;

            pc.rCorr = Math.max(base.minRCorr, base.rCorr);
            pc.cPad = Math.max(base.minCPad, base.cPad - pass);
            pc.cRoom = Math.max(base.minCRoom, base.cRoom - pass);

            pc.minRCorr = base.minRCorr;
            pc.minCPad = base.minCPad;
            pc.minCRoom = base.minCRoom;

            pc.anchorAttempts = base.anchorAttempts;
            pc.orientTries = base.orientTries;
            pc.perRoomTries = base.perRoomTries;

            pc.shellMin = base.shellMin;
            pc.shellMax = base.shellMax;
            pc.maxRelaxPasses = base.maxRelaxPasses;
            pc.relaxShellScale = base.relaxShellScale;

            pc.backboneStride = base.backboneStride;
            pc.maxRouteLen = base.maxRouteLen;

            pc.requiredMainPaths = base.requiredMainPaths;
            pc.straightPenalty = base.straightPenalty;
            pc.jitter = base.jitter;
            pc.wallBias = base.wallBias;
            pc.skipEndpoint = base.skipEndpoint;
            pc.gridPad = base.gridPad;

            float shell = (float)(baseShell * Math.pow(pc.relaxShellScale, pass));
            LOGGER.info("[Proc] === PASS {}/{}  rCorr={}  cPad={}  cRoom={}  shell={} ===",
                    pass + 1, passes, pc.rCorr, pc.cPad, pc.cRoom, String.format("%.1f", shell));

            // grid bounds around entrance/end
            int pad = Math.max(pc.gridPad, Math.round(shell) + 8);
            int minX = Math.min(entranceCenterWorld.getX(), endCenterWorld.getX()) - pad;
            int minY = Math.min(entranceCenterWorld.getY(), endCenterWorld.getY()) - pad;
            int minZ = Math.min(entranceCenterWorld.getZ(), endCenterWorld.getZ()) - pad;
            int maxX = Math.max(entranceCenterWorld.getX(), endCenterWorld.getX()) + pad;
            int maxY = Math.max(entranceCenterWorld.getY(), endCenterWorld.getY()) + pad;
            int maxZ = Math.max(entranceCenterWorld.getZ(), endCenterWorld.getZ()) + pad;

            int nx = Math.max(32, maxX - minX + 1);
            int ny = Math.max(32, maxY - minY + 1);
            int nz = Math.max(32, maxZ - minZ + 1);

            LOGGER.info("[Proc/Grid] world bounds min=({}, {}, {})  max=({}, {}, {})  size=({}, {}, {})  pad={}",
                    minX, minY, minZ, maxX, maxY, maxZ, nx, ny, nz, pad);

            VoxelMask3D mRoom = new VoxelMask3D(nx, ny, nz, minX, minY, minZ);

            // --- plan exact room count, degree-balanced mix ---
            int fixedRooms = 5; // entrance + end + 3 queens
            int roomsExact = Math.max(fixedRooms, rnd.nextIntBetweenInclusive(pc.minRooms, pc.maxRooms));
            LOGGER.info("[Proc/Plan] roomsExact={} (min={} max={})", roomsExact, pc.minRooms, pc.maxRooms);

            DegreePlan plan = planDegreeMix(new java.util.Random(rnd.nextLong()), roomsExact, RoomDefs.BASIC, RoomDefs.BRANCH_END, pc);
            ArrayList<RoomTemplateDef> exactPool = new ArrayList<>(plan.basicsExact);
            exactPool.addAll(plan.treasuresExact);

            // --- place rooms (entrance, end, 3 queens, then exact pool) ---
            RoomPlacer placer = new RoomPlacer(pc, new java.util.Random(rnd.nextLong()));
            List<RoomPlacer.Placed> placed = placer.placeAllExact(
                    exactPool, entScan, endScan,
                    surface, endCenterWorld,
                    worldMinY, surfY - 2,
                    mRoom
            );

            LOGGER.info("[Proc/Place] pass={}  placed={}  (min={} target={} max={})",
                    pass + 1, placed.size(), pc.minRooms, pc.targetRooms, pc.maxRooms);
            LOGGER.info("[Proc/Place] pass={} placed={} (need roomsExact={})", pass + 1, placed.size(), roomsExact);

            if (placed.size() < roomsExact) {
                LOGGER.info("[Proc] Not enough rooms this pass ({} < {}). Relaxing and retrying...", placed.size(), roomsExact);
                continue; // try next relaxed pass
            }

            // --- free-space backbone for routing ---
            int R = pc.effectiveRadius();
            VoxelMask3D mFree = Morph3D.freeMaskFromRooms(mRoom, R);
            LOGGER.info("[Proc/Free] effectiveRadius={} (free-mask built)", R);
            int[][][] Dfree = DistanceField3D.manhattanDist(mFree);
            FreeGraph G = FreeGraph.build(mFree, pc.backboneStride);
            LOGGER.info("[Proc/Free] backbone stride={} (graph built)", pc.backboneStride);

            try {
                // --- portals/candidates ---
                List<FlowRouter.PNode> P = FlowRouter.collectPortals(placed);

                RoomPlacer.Placed entPlaced = placed.stream()
                        .filter(p -> p.def().type() == RoomTemplateDef.RoomType.ENTRANCE)
                        .findFirst().orElse(null);
                RoomPlacer.Placed endPlaced = placed.stream()
                        .filter(p -> p.def().type() == RoomTemplateDef.RoomType.END)
                        .findFirst().orElse(null);
                if (entPlaced == null || endPlaced == null) return fail("missing entrance/end rooms");

                List<FlowRouter.PNode> entrancePortList = new ArrayList<>();
                List<FlowRouter.PNode> endPortList = new ArrayList<>();
                for (FlowRouter.PNode pn : P) {
                    if (pn.room() == entPlaced) entrancePortList.add(pn);
                    if (pn.room() == endPlaced) endPortList.add(pn);
                }
                LOGGER.info("(FlowRouter) [Portal] entrancePorts={} endPorts={}", entrancePortList.size(), endPortList.size());

                List<FlowRouter.EdgeCand> CE = FlowRouter.candidateEdges(P, G, pc);
                int exitEntrance = 0, wrongDir = 0;
                for (var ec : CE) {
                    var A = P.get(ec.a());
                    var B = P.get(ec.b());
                    boolean ok = isExitNode(A) && isEntranceNode(B);
                    if (ok) exitEntrance++; else wrongDir++;
                }
                LOGGER.info("(FlowRouter) exit->entrance ok={} rejectedByDir={}", exitEntrance, wrongDir);

                long outFromEntrance = CE.stream().filter(ec -> P.get(ec.a()).room() == entPlaced).count();
                long intoEnd = CE.stream().filter(ec -> P.get(ec.b()).room() == endPlaced).count();
                LOGGER.info("(FlowRouter) [Cand] edges from entrance={}  into end={}", outFromEntrance, intoEnd);
                LOGGER.info("(DungeonWorldBuilder) [Proc/Portal] portals={}  candidateEdges={}", P.size(), CE.size());

                // --- emit rooms first ---
                for (RoomPlacer.Placed p : placed) {
                    Vec3i rs = Transforms.rotatedSize(p.scan().size(), p.rot());
                    BoundingBox bb = BoundingBox.fromCorners(
                            p.origin(),
                            p.origin().offset(rs.getX() - 1, rs.getY() - 1, rs.getZ() - 1)
                    );
                    // small expansion to allow corridor entry overlap
                    bb = new BoundingBox(
                            bb.minX() - 1, bb.minY() - 1, bb.minZ() - 1,
                            bb.maxX() + 1, bb.maxY() + 1, bb.maxZ() + 1
                    );

                    out.addPiece(new TemplatePiece(p.def(), p.origin(), p.rot(), bb));
                    LOGGER.info("[Proc/Layout] Room {} at {} size(rot)={} rot={}", p.def().id(), p.origin(), rs, p.rot());
                }

                // --- critical chains: Entrance -> BASICs -> Queen -> End ---
                Random jrand = new Random(rnd.nextLong());
                java.util.Set<ResourceLocation> queenIds =
                        RoomDefs.QUEEN.stream().map(RoomTemplateDef::id).collect(java.util.stream.Collectors.toSet());
                List<RoomPlacer.Placed> queenRooms = new ArrayList<>();
                for (RoomPlacer.Placed p : placed) if (queenIds.contains(p.def().id())) queenRooms.add(p);
                if (queenRooms.size() > 3) queenRooms = queenRooms.subList(0, 3);

                List<FlowRouter.RoutedPair> criticalPairs =
                        buildCriticalRoomPaths(placed, P, CE, entPlaced, endPlaced, queenRooms, pc, jrand);

                List<FlowRouter.RoutedPair> pairs = new ArrayList<>(criticalPairs);
                pairs = dedupePairs(pairs);

                // record-critical for tagging pieces
                HashSet<Long> criticalKey = new HashSet<>();
                for (FlowRouter.RoutedPair rp : criticalPairs) {
                    long keyA = rp.A().worldPos().asLong();
                    long keyB = rp.B().worldPos().asLong();
                    long key = (keyA * 1469598103934665603L) ^ keyB;
                    criticalKey.add(key);
                }

                // --- dead-end planning on non-critical basics ---
                HashSet<RoomPlacer.Placed> criticalRooms = new HashSet<>();
                for (FlowRouter.RoutedPair rp : criticalPairs) {
                    criticalRooms.add(rp.A().room());
                    criticalRooms.add(rp.B().room());
                }

                HashSet<RoomPlacer.Placed> deadEnds = new HashSet<>();
                ArrayList<RoomPlacer.Placed> basicsNonCritical = new ArrayList<>();
                for (RoomPlacer.Placed p : placed) {
                    if (p.def().type() == RoomTemplateDef.RoomType.BASIC && !criticalRooms.contains(p)) basicsNonCritical.add(p);
                }

                int minDead = Math.min(basicsNonCritical.size(), (int)Math.ceil(pc.deadEndMinFraction * basicsNonCritical.size()));
                basicsNonCritical.sort((a,b) -> Integer.compare(
                        (b.scan().exits().size() - b.scan().entrances().size()),
                        (a.scan().exits().size() - a.scan().entrances().size())));
                for (int i = 0; i < minDead; i++) deadEnds.add(basicsNonCritical.get(i));

                int treasureCount = 0;
                for (RoomPlacer.Placed p : placed) if (p.def().type() == RoomTemplateDef.RoomType.BRANCH_END) treasureCount++;
                int S = 0;
                for (RoomPlacer.Placed p : basicsNonCritical) {
                    int outDeg = p.scan().exits().size();
                    int inDeg  = p.scan().entrances().size();
                    S += (outDeg - inDeg);
                }
                int over = S - treasureCount;
                if (over > 0) {
                    for (RoomPlacer.Placed r : basicsNonCritical) {
                        if (over <= 0) break;
                        if (deadEnds.contains(r)) continue;
                        int reduceBy = Math.max(1, r.scan().exits().size());
                        deadEnds.add(r);
                        over -= reduceBy;
                    }
                }
                LOGGER.info("[Balance] basics(non-critical)={} minDead={} surplusBasics={} deadEndsPicked={}",
                        basicsNonCritical.size(), minDead, S, deadEnds.size());

                // filter CE -> no edges OUT of dead-end rooms
                ArrayList<FlowRouter.EdgeCand> CE_filtered = new ArrayList<>();
                for (FlowRouter.EdgeCand ec : CE) {
                    FlowRouter.PNode a = P.get(ec.a());
                    if (!deadEnds.contains(a.room())) CE_filtered.add(ec);
                }

                // --- grow simple paths (no branching; entrance may start 3) ---
                List<FlowRouter.RoutedPair> more = connectAsSimplePaths(placed, P, CE_filtered, pairs, deadEnds, entPlaced, queenRooms, endPlaced);
                pairs.addAll(more);
                pairs = dedupePairs(pairs);

                // --- route + emit corridors ---
                int corridors = 0;
                for (FlowRouter.RoutedPair rp : pairs) {
                    // gradient endpoints (ports can now be different sizes)
                    int aAp = Math.max(3, rp.A().port().aperture());
                    int bAp = Math.max(3, rp.B().port().aperture());

                    // compute a route
                    List<BlockPos> path = dockedRoute(G, rp,
                            /*preKickOut*/2, /*preStraight*/2,
                            /*goalKickOut*/4, /*goalStraightIn*/3);
                    if (path == null || path.isEmpty()) continue;

                    // bbox must enclose the largest aperture used anywhere along the path
                    int apBox = Math.max(corridorApertureFor(rp, pc), Math.max(aAp, bAp));
                    BoundingBox cbox = corridorBox(path, apBox);

                    long keyA = rp.A().worldPos().asLong();
                    long keyB = rp.B().worldPos().asLong();
                    long key = (keyA * 1469598103934665603L) ^ keyB;
                    boolean isCritical = criticalKey.contains(key);

                    // NEW: pass both end apertures (we still store "aperture" as max for bbox/compat)
                    out.addPiece(new CorridorPiece(
                            path, apBox, cbox,
                            rp.A().worldPos(), rp.B().worldPos(),
                            rp.A().facing(), rp.B().facing(),
                            aAp, bAp,
                            isCritical
                    ));
                    corridors++;
                }
                LOGGER.info("[Proc/Route] pairs={}  critical={}  corridorsEmitted={}",
                        pairs.size(), criticalPairs.size(), corridors);

                return true;

            } catch (Throwable t) {
                return fail("routing exception: " + t);
            }
        }
        return fail("all passes failed (not enough rooms)");
    }

    private static BlockPos localToWorldMinCorner(BlockPos local, RoomPlacer.Placed room) {
        BlockPos origin = room.origin();
        Vec3i size = room.scan().size(); // ✅ pass UNROTATED size
        return Transforms.worldOfLocalMin(local, origin, size, room.rot());
    }

    private static boolean pnodeMatchesPort(FlowRouter.PNode pn, RoomPlacer.Placed room, Port port) {
        BlockPos wp = localToWorldMinCorner(port.localPos(), room);
        Direction faceR = Transforms.rotateFacingYaw(port.facing(), room.rot());
        return wp.equals(pn.worldPos()) && faceR == pn.facing();
    }

    private static boolean isEntranceNode(FlowRouter.PNode pn) {
        RoomPlacer.Placed room = pn.room();
        for (Port port : room.scan().entrances()) if (pnodeMatchesPort(pn, room, port)) return true;
        return false;
    }

    private static boolean isExitNode(FlowRouter.PNode pn) {
        RoomPlacer.Placed room = pn.room();
        for (Port port : room.scan().exits()) if (pnodeMatchesPort(pn, room, port)) return true;
        return false;
    }

    private static boolean isEntrance(FlowRouter.PNode pn) {
        return pn.port().isEntrance(); // or pn.port().isEntrance()
    }
    private static boolean isExit(FlowRouter.PNode pn) {
        return pn.port().isExit(); // or pn.port().isExit()
    }

    private boolean fail(String msg) {
        LOGGER.info("[Proc] FAIL: {}", msg);
        return false;
    }


    private static final class DegIO { int in=0, out=0; }

    public static List<FlowRouter.RoutedPair> connectAsSimplePaths(
            List<RoomPlacer.Placed> placed,
            List<FlowRouter.PNode> P,
            List<FlowRouter.EdgeCand> CE,
            List<FlowRouter.RoutedPair> already,
            Set<RoomPlacer.Placed> deadEnds,
            RoomPlacer.Placed entrance,
            List<RoomPlacer.Placed> queens,
            RoomPlacer.Placed end) {

        // Degree caps per type
        final Map<RoomPlacer.Placed, Integer> capIn  = new HashMap<>();
        final Map<RoomPlacer.Placed, Integer> capOut = new HashMap<>();
        for (RoomPlacer.Placed r : placed) {
            switch (r.def().type()) {
                case ENTRANCE -> { capIn.put(r, 0);  capOut.put(r, 3); }
                case END      -> { capIn.put(r, 3);  capOut.put(r, 0); } // 3 queens -> end
                case QUEEN    -> { capIn.put(r, 1);  capOut.put(r, 1); }
                case BRANCH_END -> { capIn.put(r, 1); capOut.put(r, 0); } // treasures terminate
                default       -> { capIn.put(r, 1);  capOut.put(r, deadEnds.contains(r) ? 0 : 1); }
            }
        }

        // Current degrees
        final Map<RoomPlacer.Placed, DegIO> deg = new HashMap<>();
        for (RoomPlacer.Placed r : placed) deg.put(r, new DegIO());
        for (FlowRouter.RoutedPair rp : already) {
            deg.get(rp.A().room()).out++;
            deg.get(rp.B().room()).in++;
        }

        // cheapest Exit->Entrance edge per (fromRoom,toRoom)
        record EdgeKey(RoomPlacer.Placed a, RoomPlacer.Placed b) {}
        final Map<EdgeKey, FlowRouter.EdgeCand> best = new HashMap<>();
        for (FlowRouter.EdgeCand ec : CE) {
            FlowRouter.PNode A = P.get(ec.a()), B = P.get(ec.b());
            if (!isExitNode(A) || !isEntranceNode(B)) continue; // enforce direction
            EdgeKey key = new EdgeKey(A.room(), B.room());
            FlowRouter.EdgeCand cur = best.get(key);
            if (cur == null || ec.w() < cur.w()) best.put(key, ec);
        }

        // Rooms touched by any corridor so far
        final Set<RoomPlacer.Placed> seen = new HashSet<>();
        for (FlowRouter.RoutedPair rp : already) {
            seen.add(rp.A().room());
            seen.add(rp.B().room());
        }

        ArrayList<FlowRouter.RoutedPair> added = new ArrayList<>();

        boolean progress;
        do {
            progress = false;

            // leaves we can extend (no branching; entrance may have multiple outs up to its cap)
            ArrayList<RoomPlacer.Placed> leaves = new ArrayList<>();
            for (RoomPlacer.Placed r : placed) {
                int out = deg.get(r).out;
                int co = capOut.getOrDefault(r, 0);
                boolean canExtend = out < co && r.def().type() != RoomTemplateDef.RoomType.END && r.def().type() != RoomTemplateDef.RoomType.BRANCH_END;
                if (!canExtend) continue;
                // only entrance may extend when already has outgoing edges; others must be pristine on out-degree
                if (r == entrance || out == 0) leaves.add(r);
            }

            for (RoomPlacer.Placed from : leaves) {
                FlowRouter.EdgeCand pick = null;
                RoomPlacer.Placed toPick = null;

                // prefer rooms that (a) can receive; (b) are unseen; (c) have lowest weight
                for (RoomPlacer.Placed to : placed) {
                    if (to == from) continue;
                    if (deg.get(to).in >= capIn.getOrDefault(to, 0)) continue;
                    if (deg.get(to).out >= capOut.getOrDefault(to, 0)) continue;

                    FlowRouter.EdgeCand ec = best.get(new EdgeKey(from, to));
                    if (ec == null) continue;

                    if (pick == null) {
                        pick = ec; toPick = to;
                    } else {
                        boolean preferUnseen = (!seen.contains(to) && seen.contains(toPick));
                        boolean cheaper = ec.w() < pick.w();
                        if (preferUnseen || cheaper) {
                            pick = ec; toPick = to;
                        }
                    }
                }

                if (pick != null) {
                    FlowRouter.PNode A = P.get(pick.a()), B = P.get(pick.b());
                    FlowRouter.RoutedPair rp = new FlowRouter.RoutedPair(A, B);
                    added.add(rp);
                    deg.get(A.room()).out++;
                    deg.get(B.room()).in++;
                    seen.add(A.room());
                    seen.add(B.room());
                    progress = true;
                }
            }
        } while (progress);

        return added;
    }
}