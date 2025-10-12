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
import org.jetbrains.annotations.Nullable;
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

    private static float clampShell(float min, float max, BlockPos surface, net.minecraft.world.level.LevelHeightAccessor h) {
        int worldSpan = surface.getY() - h.getMinBuildHeight();
        float verticalRoom = Math.max(24f, worldSpan - 12f);
        float s = Math.min(max, Math.max(min, verticalRoom * 0.60f));
        return s;
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
            int goalStraightIn,
            VoxelMask3D avoidMask,  // NEW
            int avoidStride         // pass pc.backboneStride
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
            // pass the avoid mask so corridors won't overlap
            mid = G.route(a4, b4, avoidMask, avoidStride, null);
        } catch (Throwable t) {
            mid = Collections.emptyList();
        }
        if (mid.isEmpty() || mid.size() < 2) {
            LOGGER.info("(DockedRoute) mid-route empty  aFace={} bFace={} a2={} a4={} b4={}",
                    aFace, bFace, a2, a4, b4);
            return Collections.emptyList();
        }

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

    private static FlowRouter.RoutedPair pickPortalPairForRooms(
            RoomPlacer.Placed from, RoomPlacer.Placed to,
            List<FlowRouter.PNode> P, List<FlowRouter.EdgeCand> CE) {

        FlowRouter.EdgeCand best = null;
        float bestW = Float.POSITIVE_INFINITY;
        for (FlowRouter.EdgeCand ec : CE) {
            FlowRouter.PNode A = P.get(ec.a()), B = P.get(ec.b());
            if (A.room() != from || B.room() != to) continue;
            if (!isExitNode(A) || !isEntranceNode(B)) continue;
            if (ec.w() < bestW) { bestW = ec.w(); best = ec; }
        }
        if (best == null) {
            // TEMP DEBUG
            // Count how many candidate edges exist for (from,to) ignoring direction,
            // and how many would pass if B had an entrance PNode.
            long any = CE.stream().filter(ec -> P.get(ec.a()).room()==from && P.get(ec.b()).room()==to).count();
            boolean toHasEntranceNode = P.stream().anyMatch(pn -> pn.room()==to && pn.port().isEntrance());
            LOGGER.info("(pickPair) no directed match from={} to={}, edgesAny={} toHasEntranceNode={}",
                    from.def().id(), to.def().id(), any, toHasEntranceNode);
            return null;
        }
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

    private static void reserveCorridor(VoxelMask3D mask, List<BlockPos> path, int aperture) {
        int r = Math.max(0, (aperture - 1) / 2);
        for (BlockPos p : path) {
            for (int dx=-r; dx<=r; dx++) for (int dy=-r; dy<=r; dy++) for (int dz=-r; dz<=r; dz++) {
                int gx = mask.gx(p.getX()+dx), gy = mask.gy(p.getY()+dy), gz = mask.gz(p.getZ()+dz);
                if (mask.in(gx,gy,gz)) mask.set(gx,gy,gz, true);
            }
        }
    }

    /**
     * Build exactly up to 3 room-disjoint critical paths, one per queen:
     *   Entrance -> BASIC x K_i -> Queen_i -> End
     *
     * Where sum(K_i) == round(criticalBasicFraction * BASIC_COUNT)
     * and K_i are split as evenly as possible across the chosen queens.
     *
     * - Enforces EXACT K_i BASICs before the queen (no Entrance->Queen direct).
     * - Uses directed room graph (EXIT->ENTRANCE).
     * - Keeps paths room-disjoint (except Entrance/End).
     * - Prefers distinct End entrances for different queens.
     */
    private static List<FlowRouter.RoutedPair> buildCriticalRoomPaths(
            List<RoomPlacer.Placed> placed,
            List<FlowRouter.PNode> P,
            List<FlowRouter.EdgeCand> CE,
            RoomPlacer.Placed entranceRoom,
            RoomPlacer.Placed endRoom,
            List<RoomPlacer.Placed> queenRooms,
            ProcConfig pc,
            Random rnd
    ) {
        ArrayList<FlowRouter.RoutedPair> result = new ArrayList<>();
        if (entranceRoom == null || endRoom == null || queenRooms == null || queenRooms.isEmpty()) {
            return result;
        }

        // Choose up to 3 queens; shuffle for variety
        ArrayList<RoomPlacer.Placed> queens = new ArrayList<>(queenRooms);
        if (queens.size() > 3) queens = new ArrayList<>(queens.subList(0, 3));
        Collections.shuffle(queens, rnd);
        int paths = queens.size();

        // ----- Compute the EXACT BASIC budget from the actual BASIC count -----
        int basicCount = 0;
        for (RoomPlacer.Placed r : placed) if (r.def().type() == RoomTemplateDef.RoomType.BASIC) basicCount++;
        int totalCriticalBasics = Math.max(0, Math.round(basicCount * Math.max(0f, Math.min(1f, pc.criticalBasicFraction))));
        // Split evenly across paths: base + distribute remainder to first few
        int[] K = new int[paths];
        int base = (paths == 0) ? 0 : totalCriticalBasics / paths;
        int rem  = (paths == 0) ? 0 : totalCriticalBasics % paths;
        for (int i = 0; i < paths; i++) K[i] = base + (i < rem ? 1 : 0);

        // ----- Build directed room graph from CE (EXIT -> ENTRANCE only), keep best edge per (room,room) -----
        record EdgeKey(RoomPlacer.Placed a, RoomPlacer.Placed b) {}
        Map<EdgeKey, FlowRouter.EdgeCand> bestEdge = new HashMap<>();
        Map<RoomPlacer.Placed, List<RoomHop>> G = new HashMap<>();

        for (FlowRouter.EdgeCand ec : CE) {
            FlowRouter.PNode A = P.get(ec.a()), B = P.get(ec.b());
            if (!isExitNode(A) || !isEntranceNode(B)) continue;
            RoomPlacer.Placed ra = A.room(), rb = B.room();
            if (ra == rb) continue;
            EdgeKey key = new EdgeKey(ra, rb);
            FlowRouter.EdgeCand cur = bestEdge.get(key);
            if (cur == null || ec.w() < cur.w()) bestEdge.put(key, ec);
        }
        for (var e : bestEdge.entrySet()) {
            RoomPlacer.Placed a = e.getKey().a();
            RoomPlacer.Placed b = e.getKey().b();
            float w = e.getValue().w();
            G.computeIfAbsent(a, k -> new ArrayList<>()).add(new RoomHop(b, w));
        }

        // Disjointness: don't reuse rooms across different paths (except Entrance/End)
        HashSet<RoomPlacer.Placed> usedRooms = new HashSet<>();
        usedRooms.add(entranceRoom);
        usedRooms.add(endRoom);

        // Prefer different End entrances for each queen
        HashSet<FlowRouter.PNode> usedEndEntrances = new HashSet<>();

        // Build each queen path with EXACT K[i] BASICs before the queen
        for (int i = 0; i < paths; i++) {
            RoomPlacer.Placed queen = queens.get(i);
            int needBasics = K[i];

            // Force: (Entrance) -> BASIC x needBasics -> (Queen)
            List<RoomPlacer.Placed> eq = exactBasicsPath(G, entranceRoom, queen, needBasics, usedRooms);
            if (eq.isEmpty()) {
                // If impossible, try a tiny relaxation: borrow from remainder around this queen only.
                // (Try lowering by 1, then 2) — keeps total close, but avoids total failure on rare graphs.
                boolean found = false;
                for (int relax = 1; relax <= Math.min(2, needBasics); relax++) {
                    eq = exactBasicsPath(G, entranceRoom, queen, needBasics - relax, usedRooms);
                    if (!eq.isEmpty()) { found = true; break; }
                }
                if (!found) continue; // skip this queen if we truly cannot route
            }

            // Queen -> End: prefer direct; if absent, allow BASIC intermediates (not counted toward K[i])
            List<RoomPlacer.Placed> qe;
            FlowRouter.EdgeCand directQE = bestEdge.get(new EdgeKey(queen, endRoom));
            if (directQE != null) {
                qe = Arrays.asList(queen, endRoom);
            } else {
                qe = shortestPathRooms(
                        G,
                        queen,
                        endRoom,
                        node -> node == queen || node == endRoom || node.def().type() == RoomTemplateDef.RoomType.BASIC,
                        usedRooms
                );
                if (qe.isEmpty()) continue; // must reach End
            }

            // Convert room path lists to portal-level pairs
            ArrayList<FlowRouter.RoutedPair> pairs = new ArrayList<>();
            // Entrance -> Queen (through K basics)
            for (int k = 0; k + 1 < eq.size(); k++) {
                RoomPlacer.Placed a = eq.get(k), b = eq.get(k + 1);
                FlowRouter.EdgeCand ec = bestEdge.get(new EdgeKey(a, b));
                if (ec == null) { pairs.clear(); break; }
                pairs.add(new FlowRouter.RoutedPair(P.get(ec.a()), P.get(ec.b())));
            }
            if (pairs.isEmpty()) continue;

            // Queen -> End (prefer unused End entrance)
            ArrayList<FlowRouter.RoutedPair> qePairs = new ArrayList<>();
            for (int k = 0; k + 1 < qe.size(); k++) {
                RoomPlacer.Placed a = qe.get(k), b = qe.get(k + 1);
                FlowRouter.RoutedPair rp = null;
                if (b == endRoom) {
                    float bestW = Float.POSITIVE_INFINITY;
                    FlowRouter.EdgeCand pick = null;
                    for (FlowRouter.EdgeCand cand : CE) {
                        FlowRouter.PNode A = P.get(cand.a()), B = P.get(cand.b());
                        if (A.room() != a || B.room() != b) continue;
                        if (!isExitNode(A) || !isEntranceNode(B)) continue;
                        if (usedEndEntrances.contains(B)) continue;
                        if (cand.w() < bestW) { bestW = cand.w(); pick = cand; }
                    }
                    if (pick == null) pick = bestEdge.get(new EdgeKey(a, b));
                    if (pick != null) {
                        rp = new FlowRouter.RoutedPair(P.get(pick.a()), P.get(pick.b()));
                        usedEndEntrances.add(rp.B());
                    }
                } else {
                    FlowRouter.EdgeCand ec = bestEdge.get(new EdgeKey(a, b));
                    if (ec != null) rp = new FlowRouter.RoutedPair(P.get(ec.a()), P.get(ec.b()));
                }
                if (rp == null) { qePairs.clear(); break; }
                qePairs.add(rp);
            }
            if (qePairs.isEmpty()) continue;

            // Accept and lock rooms (keep paths disjoint)
            result.addAll(pairs);
            result.addAll(qePairs);
            for (RoomPlacer.Placed r : eq) if (r != entranceRoom && r != endRoom) usedRooms.add(r);
            for (RoomPlacer.Placed r : qe) if (r != entranceRoom && r != endRoom) usedRooms.add(r);
        }

        return dedupePairs(result);
    }

    private static record RoomHop(RoomPlacer.Placed to, float w) {}

    /**
     * Find a path with EXACTLY 'kBasics' BASIC rooms between src(Entrance) and dst(Queen):
     *   src -> BASIC x kBasics -> dst
     *
     * - No direct src->dst allowed.
     * - Respects 'forbidden' to keep paths room-disjoint.
     * - Uses the directed room graph 'G' (EXIT->ENTRANCE).
     * Returns [src, b1, b2, ..., bK, dst] or empty if impossible.
     */
    private static List<RoomPlacer.Placed> exactBasicsPath(
            Map<RoomPlacer.Placed, List<RoomHop>> G,
            RoomPlacer.Placed src,
            RoomPlacer.Placed dst,
            int kBasics,
            Set<RoomPlacer.Placed> forbidden
    ) {
        if (kBasics < 0) return Collections.emptyList();
        // State is (room, usedBasics). We do a small BFS/DFS hybrid.
        record State(RoomPlacer.Placed r, int k) {}

        // For reconstruction
        Map<State, State> prev = new HashMap<>();
        Deque<State> dq = new ArrayDeque<>();
        dq.add(new State(src, 0));

        // Disallow using forbidden rooms as intermediates
        java.util.function.Predicate<RoomPlacer.Placed> allowBasic =
                r -> r.def().type() == RoomTemplateDef.RoomType.BASIC && !forbidden.contains(r);

        HashSet<State> seen = new HashSet<>();
        seen.add(new State(src, 0));

        while (!dq.isEmpty()) {
            State s = dq.pollFirst();
            RoomPlacer.Placed u = s.r;
            int used = s.k;

            for (RoomHop hop : G.getOrDefault(u, Collections.emptyList())) {
                RoomPlacer.Placed v = hop.to();

                // Never allow Entrance -> Queen direct
                if (u == src && v == dst) continue;

                if (v == dst) {
                    // We can finish only if we've used EXACTLY kBasics
                    if (used == kBasics) {
                        // reconstruct [src ... dst]
                        ArrayList<RoomPlacer.Placed> path = new ArrayList<>();
                        State t = s;
                        // t.r is the node before dst
                        while (t != null) {
                            path.add(t.r);
                            t = prev.get(t);
                        }
                        Collections.reverse(path);
                        path.add(dst);
                        return path;
                    }
                    // else can’t step into dst yet
                    continue;
                }

                // Intermediate must be BASIC and available
                if (!allowBasic.test(v)) continue;

                int nxtUsed = used + 1;
                if (nxtUsed > kBasics) continue;

                State ns = new State(v, nxtUsed);
                if (seen.add(ns)) {
                    prev.put(ns, s);
                    dq.addLast(ns);
                }
            }
        }
        return Collections.emptyList();
    }


    /**
     * Dijkstra over rooms with:
     *  - Directed edges from G (EXIT->ENTRANCE)
     *  - 'allowed' filter for intermediates
     *  - 'forbidden' set for global disjointness (Entrance/End may appear even if in 'forbidden')
     * Returns a list of rooms [src ... dst], or empty if no route.
     */
    private static List<RoomPlacer.Placed> shortestPathRooms(
            Map<RoomPlacer.Placed, List<RoomHop>> G,
            RoomPlacer.Placed src,
            RoomPlacer.Placed dst,
            java.util.function.Predicate<RoomPlacer.Placed> allowed,
            Set<RoomPlacer.Placed> forbidden
    ) {
        if (src == null || dst == null) return Collections.emptyList();
        if (src == dst) return List.of(src);

        // Dijkstra
        Map<RoomPlacer.Placed, Float> dist = new HashMap<>();
        Map<RoomPlacer.Placed, RoomPlacer.Placed> prev = new HashMap<>();
        PriorityQueue<RoomPlacer.Placed> pq = new PriorityQueue<>(Comparator.comparing(dist::get));

        for (RoomPlacer.Placed r : G.keySet()) dist.put(r, Float.POSITIVE_INFINITY);
        dist.put(src, 0f);
        pq.add(src);

        while (!pq.isEmpty()) {
            RoomPlacer.Placed u = pq.poll();
            if (u == dst) break;

            List<RoomHop> outs = G.getOrDefault(u, Collections.emptyList());
            for (RoomHop h : outs) {
                RoomPlacer.Placed v = h.to();

                // Filter intermediates:
                // - Always allow src and dst
                // - Otherwise require 'allowed', and not in 'forbidden'
                if (v != dst && v != src) {
                    if (!allowed.test(v)) continue;
                    if (forbidden.contains(v)) continue;
                }

                float alt = dist.getOrDefault(u, Float.POSITIVE_INFINITY) + h.w();
                if (alt < dist.getOrDefault(v, Float.POSITIVE_INFINITY)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }

        if (!prev.containsKey(dst) && src != dst) return Collections.emptyList();

        ArrayList<RoomPlacer.Placed> path = new ArrayList<>();
        RoomPlacer.Placed t = dst;
        path.add(t);
        while (t != null && t != src) {
            t = prev.get(t);
            if (t == null) return Collections.emptyList();
            path.add(t);
        }
        Collections.reverse(path);
        return path;
    }


    /** Tiny helper: best directed EXIT(from) -> ENTRANCE(to) pair, with a light fallback. Returns null if none. */
    private static FlowRouter.RoutedPair bestRoomToRoom(
            RoomPlacer.Placed from,
            RoomPlacer.Placed to,
            List<FlowRouter.PNode> P,
            List<FlowRouter.EdgeCand> CE
    ) {
        FlowRouter.RoutedPair rp = pickPortalPairForRooms(from, to, P, CE);
        if (rp != null) return rp;
        // Light fallback: allow reusing the same 'A' if unavoidable (avoidA=null)
        return pickEdgeFromRoomToRoomAvoidingA(from, to, null, P, CE);
    }

    private static boolean isBasicRoom(RoomPlacer.Placed r, RoomPlacer.Placed ent, RoomPlacer.Placed end) {
        if (r==ent || r==end) return false;
        return r.def().type()==RoomTemplateDef.RoomType.BASIC;
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

            VoxelMask3D mRoom = new VoxelMask3D(nx, ny, nz, minX, minY, minZ);

            // --- plan exact room count, degree-balanced mix ---
            int fixedRooms = 5; // entrance + end + 3 queens
            int roomsExact = Math.max(fixedRooms, rnd.nextIntBetweenInclusive(pc.minRooms, pc.maxRooms));

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

            if (placed.size() < roomsExact) {
                LOGGER.info("[Proc] Not enough rooms this pass ({} < {}). Relaxing and retrying...", placed.size(), roomsExact);
                continue; // try next relaxed pass
            }

            // --- free-space backbone for routing ---
            int R = pc.effectiveRadius();
            VoxelMask3D mFree = Morph3D.freeMaskFromRooms(mRoom, R);
            FreeGraph G = FreeGraph.build(mFree, pc.backboneStride);

            VoxelMask3D reserved = new VoxelMask3D(mFree.nx, mFree.ny, mFree.nz, mFree.ox, mFree.oy, mFree.oz);

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

                List<FlowRouter.EdgeCand> CE = FlowRouter.candidateEdges(P, G, pc);

                int stepOut = Math.max(1, pc.effectiveRadius() + 1);
                int sClipped=0, gClipped=0, sFree=0, gFree=0;
                for (var ec : CE) {
                    var A = P.get(ec.a());
                    var B = P.get(ec.b());
                    BlockPos aStart = A.worldPos().relative(A.facing(), stepOut);
                    BlockPos bStart = B.worldPos().relative(B.facing(), stepOut);

                    int ax = G.mask().gx(aStart.getX()), ay = G.mask().gy(aStart.getY()), az = G.mask().gz(aStart.getZ());
                    int bx = G.mask().gx(bStart.getX()), by = G.mask().gy(bStart.getY()), bz = G.mask().gz(bStart.getZ());

                    boolean asIn = G.mask().in(ax,ay,az), bsIn = G.mask().in(bx,by,bz);
                    if (!asIn) sClipped++; else if (G.mask().get(ax,ay,az)) sFree++; else sClipped++;
                    if (!bsIn) gClipped++; else if (G.mask().get(bx,by,bz)) gFree++; else gClipped++;
                }
                LOGGER.info("(RoutePrep) endpoints: aFree={} bFree={} aClipped={} bClipped={} stepOut={}",
                        sFree, gFree, sClipped, gClipped, stepOut);

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

                LOGGER.info("(RoutePlan) criticalPairs={} allPairs(after dedupe)={}", criticalPairs.size(), pairs.size());
                int vertical=0, horizontal=0;
                for (var rp : pairs) {
                    boolean hv = rp.A().facing().getAxis() == Direction.Axis.Y || rp.B().facing().getAxis() == Direction.Axis.Y;
                    if (hv) vertical++; else horizontal++;
                }
                LOGGER.info("(RoutePlan) pair orientation: horizontal={} vertical={}", horizontal, vertical);

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

                List<FlowRouter.RoutedPair> forest = FlowRouter.connectAllRooms(P, CE_filtered, pairs, /*blocked*/ null);
                if (!forest.isEmpty()) {
                    pairs.addAll(forest);
                    pairs = dedupePairs(pairs);
                    LOGGER.info("(RoutePlan) forest-connected {} leftover links", forest.size());
                }

                // --- route + emit corridors ---
                int corridors = 0, routeEmpty=0, bboxOut=0;
                for (FlowRouter.RoutedPair rp : pairs) {
                    int aAp = Math.max(3, rp.A().port().aperture());
                    int bAp = Math.max(3, rp.B().port().aperture());

                    // Log the pair we’re attempting
                    LOGGER.info("(RouteTry) A={} {} -> B={} {}  aAp={} bAp={}",
                            rp.A().worldPos(), rp.A().facing(), rp.B().worldPos(), rp.B().facing(), aAp, bAp);

                    List<BlockPos> path = dockedRoute(
                            G, rp,
                            /*preKickOut*/2, /*preStraight*/2, /*goalKickOut*/4, /*goalStraightIn*/3,
                            reserved, pc.backboneStride // NEW
                    );
                    if (path == null || path.isEmpty()) { /* log+continue */ }

                    // compute aperture as you already do
                    int apBox = Math.max(corridorApertureFor(rp, pc),
                            Math.max(aAp, bAp));

                    // Reserve volume BEFORE emitting the piece so future routes avoid it
                    reserveCorridor(reserved, path, apBox);

                    // Emit corridor piece as you do now...
                    BoundingBox cbox = corridorBox(path, apBox);

                    // Emit + log
                    long keyA = rp.A().worldPos().asLong();
                    long keyB = rp.B().worldPos().asLong();
                    long key = (keyA * 1469598103934665603L) ^ keyB;
                    boolean isCritical = criticalKey.contains(key);

                    LOGGER.info("(RouteOK) len={} apBox={} bbox=[({}, {}, {}) -> ({}, {}, {})] critical={}",
                            path.size(), apBox, cbox.minX(), cbox.minY(), cbox.minZ(), cbox.maxX(), cbox.maxY(), cbox.maxZ(), isCritical);

                    out.addPiece(new CorridorPiece(
                            path, apBox, cbox,
                            rp.A().worldPos(), rp.B().worldPos(),
                            rp.A().facing(), rp.B().facing(),
                            aAp, bAp,
                            isCritical
                    ));
                    corridors++;
                }
                LOGGER.info("(Emit) pairsTried={} critical={} emitted={} routeEmpty={}", pairs.size(), criticalPairs.size(), corridors, routeEmpty);

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

    public static boolean isEntranceNode(FlowRouter.PNode pn) {
        RoomPlacer.Placed room = pn.room();
        for (Port port : room.scan().entrances()) if (pnodeMatchesPort(pn, room, port)) return true;
        return false;
    }

    public static boolean isExitNode(FlowRouter.PNode pn) {
        RoomPlacer.Placed room = pn.room();
        for (Port port : room.scan().exits()) if (pnodeMatchesPort(pn, room, port)) return true;
        return false;
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

        // ---------- Degree caps per type (unchanged) ----------
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

        // ---------- Current degrees from 'already' ----------
        final class DegIO { int in=0, out=0; }
        final Map<RoomPlacer.Placed, DegIO> deg = new HashMap<>();
        for (RoomPlacer.Placed r : placed) deg.put(r, new DegIO());
        for (FlowRouter.RoutedPair rp : already) {
            deg.get(rp.A().room()).out++;
            deg.get(rp.B().room()).in++;
        }

        // ---------- Backbone set (rooms touched by critical/seed pairs) ----------
        final Set<RoomPlacer.Placed> backbone = new HashSet<>();
        for (FlowRouter.RoutedPair rp : already) {
            backbone.add(rp.A().room());
            backbone.add(rp.B().room());
        }

        // ---------- Port usage: each PNode can be used at most once ----------
        final Set<FlowRouter.PNode> usedPorts = new HashSet<>();
        for (FlowRouter.RoutedPair rp : already) {
            usedPorts.add(rp.A());
            usedPorts.add(rp.B());
        }

        // Convenience: quickly get cheapest valid EdgeCand for a given (fromRoom -> toRoom)
        // while checking caps AND port usage.
        record EdgeKey(RoomPlacer.Placed a, RoomPlacer.Placed b) {}
        List<FlowRouter.RoutedPair> added = new ArrayList<>();

        java.util.function.Function<EdgeKey, FlowRouter.EdgeCand> pickBestValid = key -> {
            FlowRouter.EdgeCand best = null;
            float bestW = Float.POSITIVE_INFINITY;
            for (FlowRouter.EdgeCand ec : CE) {
                FlowRouter.PNode A = P.get(ec.a()), B = P.get(ec.b());
                if (A.room() != key.a() || B.room() != key.b()) continue;
                if (!isExitNode(A) || !isEntranceNode(B)) continue;

                // Degree caps
                if (deg.get(A.room()).out >= capOut.getOrDefault(A.room(), 0)) continue;
                if (deg.get(B.room()).in  >= capIn.getOrDefault(B.room(), 0)) continue;

                // Port availability
                if (usedPorts.contains(A)) continue;
                if (usedPorts.contains(B)) continue;

                // Queen -> Queen forbidden
                if (A.room().def().type() == RoomTemplateDef.RoomType.QUEEN &&
                        B.room().def().type() == RoomTemplateDef.RoomType.QUEEN) continue;

                // If from is a QUEEN and End is reachable, prefer End (Phase 2 loop already biases cheapest edges; here we just allow)
                if (ec.w() < bestW) { bestW = ec.w(); best = ec; }
            }
            return best;
        };

        // Helper: commit an EdgeCand
        java.util.function.Function<FlowRouter.EdgeCand, FlowRouter.RoutedPair> commit = ec -> {
            FlowRouter.PNode A = P.get(ec.a()), B = P.get(ec.b());
            usedPorts.add(A);
            usedPorts.add(B);
            deg.get(A.room()).out++;
            deg.get(B.room()).in++;
            FlowRouter.RoutedPair rp = new FlowRouter.RoutedPair(A, B);
            added.add(rp);
            return rp;
        };

        // ---------- Union-Find over rooms to avoid cycles & prefer connecting components ----------
        class DSU {
            final Map<RoomPlacer.Placed, RoomPlacer.Placed> p = new HashMap<>();
            DSU(Collection<RoomPlacer.Placed> nodes) { for (var r : nodes) p.put(r, r); }
            RoomPlacer.Placed find(RoomPlacer.Placed x) {
                RoomPlacer.Placed px = p.get(x);
                if (px != x) p.put(x, px = find(px));
                return px;
            }
            boolean unite(RoomPlacer.Placed a, RoomPlacer.Placed b) {
                RoomPlacer.Placed ra = find(a), rb = find(b);
                if (ra == rb) return false;
                p.put(ra, rb);
                return true;
            }
            boolean connected(RoomPlacer.Placed a, RoomPlacer.Placed b) {
                return find(a) == find(b);
            }
        }
        DSU dsu = new DSU(placed);
        for (FlowRouter.RoutedPair rp : already) dsu.unite(rp.A().room(), rp.B().room());

        // -------------------------------------------------
        // PHASE 1: Fork ONLY from backbone leaves/rooms
        // -------------------------------------------------
        // Build a priority list of candidate edges that attach non-backbone rooms to the backbone,
        // respecting caps and ports, cheapest first.
        PriorityQueue<FlowRouter.EdgeCand> pq1 = new PriorityQueue<>(Comparator.comparingDouble(FlowRouter.EdgeCand::w));

        // Consider edges where FROM is in backbone, TO is not (and TO can receive)
        for (FlowRouter.EdgeCand ec : CE) {
            FlowRouter.PNode A = P.get(ec.a()), B = P.get(ec.b());
            if (!isExitNode(A) || !isEntranceNode(B)) continue;
            RoomPlacer.Placed ra = A.room(), rb = B.room();
            if (!backbone.contains(ra) || backbone.contains(rb)) continue;

            // degree & port constraints
            if (deg.get(ra).out >= capOut.getOrDefault(ra, 0)) continue;
            if (deg.get(rb).in  >= capIn.getOrDefault(rb, 0)) continue;
            if (usedPorts.contains(A) || usedPorts.contains(B)) continue;

            // Queen -> Queen forbidden
            if (ra.def().type() == RoomTemplateDef.RoomType.QUEEN &&
                    rb.def().type() == RoomTemplateDef.RoomType.QUEEN) continue;

            pq1.add(ec);
        }

        while (!pq1.isEmpty()) {
            FlowRouter.EdgeCand ec = pq1.poll();
            FlowRouter.PNode A = P.get(ec.a()), B = P.get(ec.b());
            RoomPlacer.Placed ra = A.room(), rb = B.room();

            // Re-check in case caps/ports changed
            if (deg.get(ra).out >= capOut.getOrDefault(ra, 0)) continue;
            if (deg.get(rb).in  >= capIn.getOrDefault(rb, 0)) continue;
            if (usedPorts.contains(A) || usedPorts.contains(B)) continue;

            // Don’t form intra-backbone cycles in phase 1: we only add if it helps attach a non-backbone
            if (backbone.contains(rb)) continue;

            // Commit
            commit.apply(ec);
            backbone.add(rb);
            dsu.unite(ra, rb);

            // New edges may become available from this newly added room (as a new fork source)
            for (FlowRouter.EdgeCand nxt : CE) {
                FlowRouter.PNode nA = P.get(nxt.a()), nB = P.get(nxt.b());
                if (!isExitNode(nA) || !isEntranceNode(nB)) continue;
                if (nA.room() != rb) continue;               // fork from the just-attached rb
                if (backbone.contains(nB.room())) continue;  // only attach new outsiders in phase 1
                if (deg.get(rb).out >= capOut.getOrDefault(rb, 0)) break;

                if (deg.get(nB.room()).in >= capIn.getOrDefault(nB.room(), 0)) continue;
                if (usedPorts.contains(nA) || usedPorts.contains(nB)) continue;

                // Queen -> Queen forbidden
                if (rb.def().type() == RoomTemplateDef.RoomType.QUEEN &&
                        nB.room().def().type() == RoomTemplateDef.RoomType.QUEEN) continue;

                pq1.add(nxt);
            }
        }

        // -------------------------------------------------
        // PHASE 2: Global completion (minimum-forest, port-aware)
        // -------------------------------------------------
        // Greedy over ALL candidate edges, cheapest first; only add edges that:
        //  - do not violate degree caps
        //  - do not reuse ports
        //  - preferably connect different DSU components (avoid cycles)
        //  - otherwise, if caps allow and you still want extra intra-component corridors, you can skip.
        PriorityQueue<FlowRouter.EdgeCand> pq2 = new PriorityQueue<>(Comparator.comparingDouble(FlowRouter.EdgeCand::w));
        pq2.addAll(CE);

        while (!pq2.isEmpty()) {
            FlowRouter.EdgeCand ec = pq2.poll();
            FlowRouter.PNode A = P.get(ec.a()), B = P.get(ec.b());
            if (!isExitNode(A) || !isEntranceNode(B)) continue;

            RoomPlacer.Placed ra = A.room(), rb = B.room();

            // Degree caps
            if (deg.get(ra).out >= capOut.getOrDefault(ra, 0)) continue;
            if (deg.get(rb).in  >= capIn.getOrDefault(rb, 0)) continue;

            // Port usage
            if (usedPorts.contains(A) || usedPorts.contains(B)) continue;

            // Queen -> Queen forbidden
            if (ra.def().type() == RoomTemplateDef.RoomType.QUEEN &&
                    rb.def().type() == RoomTemplateDef.RoomType.QUEEN) continue;

            // Prefer edges that connect different components
            if (!dsu.connected(ra, rb)) {
                commit.apply(ec);
                dsu.unite(ra, rb);
            } else {
                // If you want a STRICT forest, skip this else branch entirely.
                // If you want to allow some extra loops when caps allow, you can add them.
                // Here we skip to keep a simple, loop-free network.
                continue;
            }
        }

        return added;
    }
}