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
 * - Place rooms with corridor clearance baked into a room mask.
 * - Build free-space mask = World \ dilate(rooms, R).
 * - Build a portal graph and reserve K entrance->end pairings.
 * - Route corridors inside free space and emit everything as pieces.
 * <p>
 * Heavily instrumented with LOGGER.info for debugging.
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
        // Base corridor aperture (odd number, 2*r + 1)
        int base = 2 * pc.rCorr + 1;
        // If PNode later exposes real port apertures, clamp to those here.
        // Most templates use 3x3 ports; use 3 as safe minimum today.
        int portAp = 3;
        return Math.max(3, Math.min(base, portAp));
    }

    /**
     * After critical routes & minimal connectors, spawn probabilistic forks:
     * - Each node on a critical route has a 50% chance to sprout a branch.
     * - That branch point then has a 25% chance to sprout a second branch (fork-in-fork).
     * - Branch lengths are variable (1..4 segments).
     * - Each branch end: 80% normal basic, 20% treasure end (if available).
     * <p>
     * Returns extra RoutedPairs to route.
     */
    private static List<FlowRouter.RoutedPair> spawnForks(
            List<FlowRouter.PNode> P,
            List<FlowRouter.EdgeCand> CE,
            List<FlowRouter.RoutedPair> already,
            List<RoomPlacer.Placed> placed,
            ProcConfig pc,
            Random rnd,
            int maxForksTotal
    ) {
        ArrayList<FlowRouter.RoutedPair> extra = new ArrayList<>();

        // adjacency
        Map<FlowRouter.PNode, List<FlowRouter.PNode>> nbrs = new HashMap<>();
        for (FlowRouter.EdgeCand e : CE) {
            FlowRouter.PNode a = P.get(e.a()), b = P.get(e.b());
            nbrs.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
            nbrs.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
        }

        // rooms already wired
        HashSet<RoomPlacer.Placed> wired = new HashSet<>();
        for (FlowRouter.RoutedPair rp : already) {
            wired.add(rp.A().room());
            wired.add(rp.B().room());
        }

        // branch points = any portals on wired rooms
        ArrayList<FlowRouter.PNode> branchFrom = new ArrayList<>();
        for (FlowRouter.PNode pn : P) if (wired.contains(pn.room())) branchFrom.add(pn);

        int forksUsed = 0;
        for (FlowRouter.PNode base : branchFrom) {
            if (forksUsed >= maxForksTotal) break;
            if (rnd.nextFloat() > pc.forkChance) continue;

            // grow one fork
            List<FlowRouter.RoutedPair> fork = growBranch(nbrs, wired, base, rnd, pc);
            if (!fork.isEmpty()) {
                extra.addAll(fork);
                forksUsed++;
            }
        }
        return extra;
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

                // base score = prefer closer spacing (just small jitter to vary)
                double score = -cur.worldPos().distSqr(nx.worldPos()) * 0.0005 + (rnd.nextDouble() - 0.5) * 0.05;

                // turn hardness: prefer orthogonal first step vs. prev->cur
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
                        score += (1.0 - cos) * (0.75 * pc.forkTurnHardness); // push to right angles if hard
                    }
                } else {
                    // first step: compare seed facing if available, nudge to orthogonal
                    score += 0.15 * pc.forkTurnHardness;
                }

                // If this is likely the last step, prefer BRANCH_END rooms as the tip
                boolean likelyTip = (i >= maxLen - 2);
                if (likelyTip && nx.room().def().type() == RoomTemplateDef.RoomType.BRANCH_END) {
                    score += 0.6; // strong nudge to end forks in a treasure/branch_end room
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

    /**
     * Build exactly 3 unique critical paths:
     * entrance -> ... -> queen
     * and then link each queen to the end room with one hop:
     * queen -> end
     * <p>
     * - No room is reused across the three entrance->queen chains
     * - Queens chosen are the ones nearest to the end (and unique rooms)
     * - Greedy selection is purely "closer-to-queen" (no vertical bias)
     */
    private static CriticalPlan planCriticalRoutes(
            List<FlowRouter.PNode> P,
            List<FlowRouter.EdgeCand> CE,
            List<FlowRouter.PNode> entrancePorts,
            List<FlowRouter.PNode> endPorts,
            List<RoomPlacer.Placed> placed,
            ProcConfig pc,
            Random rnd
    ) {
        ArrayList<FlowRouter.RoutedPair> chainPairs = new ArrayList<>();
        ArrayList<FlowRouter.RoutedPair> qToEndPairs = new ArrayList<>();

        if (entrancePorts == null || entrancePorts.isEmpty() || endPorts == null || endPorts.isEmpty()) {
            return new CriticalPlan(chainPairs, qToEndPairs);
        }

        // one entrance to start from (the generator only places one entrance template)
        final FlowRouter.PNode start = entrancePorts.get(0);

        // end ports (use all of them, but we only need 3 queens near the end-room)
        final ArrayList<FlowRouter.PNode> ends = new ArrayList<>(endPorts);

        // adjacency (portal-graph neighbors)
        Map<FlowRouter.PNode, List<FlowRouter.PNode>> nbrs = new HashMap<>();
        for (FlowRouter.EdgeCand e : CE) {
            FlowRouter.PNode a = P.get(e.a()), b = P.get(e.b());
            nbrs.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
            nbrs.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
        }

        // gather queen ports
        java.util.Set<ResourceLocation> queenIds =
                RoomDefs.QUEEN.stream().map(RoomTemplateDef::id).collect(java.util.stream.Collectors.toSet());
        ArrayList<FlowRouter.PNode> queenPorts = new ArrayList<>();
        for (FlowRouter.PNode pn : P) if (queenIds.contains(pn.room().def().id())) queenPorts.add(pn);

        // pick/end reference (use the lowest end port as the end anchor)
        FlowRouter.PNode endAnchor = ends.stream()
                .min(Comparator.comparingInt(pn -> pn.worldPos().getY()))
                .orElse(ends.get(0));

        // ensure we have 3 queens (unique rooms), nearest to the end anchor
        HashSet<RoomPlacer.Placed> usedQueenRooms = new HashSet<>();
        queenPorts.sort(Comparator.comparingDouble(q -> q.worldPos().distSqr(endAnchor.worldPos())));
        ArrayList<FlowRouter.PNode> pickedQueens = new ArrayList<>(3);
        for (FlowRouter.PNode q : queenPorts) {
            if (usedQueenRooms.add(q.room())) pickedQueens.add(q);
            if (pickedQueens.size() == 3) break;
        }
        if (pickedQueens.size() < 3) {
            // fallback: choose any non-queen ports close to end as "pseudo-queens"
            ArrayList<FlowRouter.PNode> extras = new ArrayList<>(P);
            extras.sort(Comparator.comparingDouble(pn -> pn.worldPos().distSqr(endAnchor.worldPos())));
            for (FlowRouter.PNode pn : extras) {
                if (queenIds.contains(pn.room().def().id())) continue;
                if (usedQueenRooms.add(pn.room())) pickedQueens.add(pn);
                if (pickedQueens.size() == 3) break;
            }
        }
        if (pickedQueens.isEmpty()) return new CriticalPlan(chainPairs, qToEndPairs);

        // used rooms across ALL three critical paths (no overlap allowed)
        HashSet<RoomPlacer.Placed> usedRooms = new HashSet<>();
        usedRooms.add(start.room());

        for (FlowRouter.PNode queen : pickedQueens) {
            // greedily walk toward queen, maximizing closeness to queen (no up/down preference)
            List<FlowRouter.PNode> chain = greedyNearestChain(P, CE, nbrs, start, queen, /*max hops*/ pc.maxRouteLen, usedRooms, rnd);
            if (chain.isEmpty() || chain.get(chain.size() - 1).room() != queen.room()) {
                // if we couldn't reach this queen uniquely, skip it
                continue;
            }
            // stitch chain into pairs (critical)
            for (int i = 0; i + 1 < chain.size(); i++) {
                chainPairs.add(new FlowRouter.RoutedPair(chain.get(i), chain.get(i + 1)));
                usedRooms.add(chain.get(i).room());
                usedRooms.add(chain.get(i + 1).room());
            }

            // add one hop from this queen to the nearest end port (non-critical)
            FlowRouter.PNode bestEnd = ends.stream()
                    .min(Comparator.comparingDouble(ep -> ep.worldPos().distSqr(queen.worldPos())))
                    .orElse(endAnchor);
            qToEndPairs.add(new FlowRouter.RoutedPair(queen, bestEnd));
        }

        return new CriticalPlan(chainPairs, qToEndPairs);
    }

    /**
     * Greedy chain that minimizes distance-to-goal only (no vertical bias).
     * Stops early when stuck or when rooms would repeat.
     */
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

            // distance-based choice; avoid already-used rooms
            for (FlowRouter.PNode nx : cand) {
                if (nx.room() == cur.room()) continue;
                if (used.contains(nx.room())) continue; // uniqueness across all critical paths
                double dist = nx.worldPos().distSqr(goal.worldPos());
                // tiny jitter to break ties
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

    private static FlowRouter.PNode pickNearestUnusedPort(
            List<FlowRouter.PNode> pool,
            FlowRouter.PNode target,
            Set<RoomPlacer.Placed> used
    ) {
        FlowRouter.PNode best = null;
        double bd = Double.MAX_VALUE;
        for (FlowRouter.PNode p : pool) {
            if (used.contains(p.room())) continue;
            double d = p.worldPos().distSqr(target.worldPos());
            if (d < bd) {
                bd = d;
                best = p;
            }
        }
        return best;
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
        // BoundingBox is inclusive; these are the correct limits with no +1 overflow
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Build a corridor polyline that docks cleanly with room ports by adding straight stubs:
     * - Start: begin 2 blocks out from A, carve 'preStraight' more steps straight out (A* starts at 2+preStraight).
     * - Route: A* from (A 2+preStraight out) to (B goalKickOut out).
     * - End: carve 'goalStraightIn' steps straight in toward B (ending goalKickOut - goalStraightIn out).
     * <p>
     * All distances are along each port's facing direction (positive away from the room).
     */
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

        // A side: kick out and pre-stub
        BlockPos a2 = aPort.relative(aFace, Math.max(1, preKickOut));   // typically 2 out
        // we’ll make A* start at a4 = a2 + preStraight
        BlockPos a4 = a2.relative(aFace, Math.max(0, preStraight));     // typically 4 out

        // B side: A* targets 4 out of the port
        BlockPos b4 = bPort.relative(bFace, Math.max(1, goalKickOut));  // typically 4 out

        // Route between the two outer anchors
        List<BlockPos> mid = G.route(a4, b4);
        if (mid.isEmpty() || mid.size() < 2) {
            return Collections.emptyList();
        }

        // Compose full path:
        // pre-stub at A (just the extra straight steps; 'mid' already starts at a4)
        ArrayList<BlockPos> path = new ArrayList<>(mid.size() + preStraight + goalStraightIn);

        // Add the two straight steps out of A before the A* path (do not duplicate a4)
        // a2 -> ... -> a4
        if (preStraight > 0) {
            // Fill a2..a4-1
            BlockPos cur = a2;
            for (int i = 0; i < preStraight; i++) {
                path.add(cur);
                cur = cur.relative(aFace, 1);
            }
            // 'cur' is now a4; 'mid' begins with a4
        } else {
            path.add(a2);
        }

        // A* middle (anchored at a4..b4)
        // mid already includes a4 as first and b4 as last; just append all of it
        path.addAll(mid);

        // Post-stub at B: step inward (toward the room) goalStraightIn times: b4 -> ... -> b2
        if (goalStraightIn > 0) {
            BlockPos cur = b4;
            Direction towardB = bFace.getOpposite();
            for (int i = 0; i < goalStraightIn; i++) {
                cur = cur.relative(towardB, 1);
                path.add(cur);
            }
        }

        // De-dup any accidental duplicates
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

    public boolean build(Structure.GenerationContext ctx,
                         StructurePiecesBuilder out,
                         BlockPos surface,
                         BlockPos centerYHint) {

        // Budget (for parity with older generator; not strictly required here)
        int budget = rnd.nextIntBetweenInclusive(cfg.minPoints(), cfg.maxPoints());
        LOGGER.info("[Proc] Budget={} points  surface={}  centerHint={}", budget, surface, centerYHint);

        // Pull room defs/scans up-front (fail-fast)
        RoomTemplateDef entDef = RoomDefs.ENTRANCE;
        RoomTemplateDef endDef = RoomDefs.pickEnd(new java.util.Random(rnd.nextLong()));
        ScannedTemplate entScan = scan(entDef.id());
        ScannedTemplate endScan = scan(endDef.id());
        if (entScan == null || endScan == null) return fail("scan() returned null for entrance/end");

        // Compute desired vertical relationship (end below entrance)
        final int worldMinY = ctx.heightAccessor().getMinBuildHeight();
        final int surfY = surface.getY();
        final int drop = 28 + rnd.nextInt(18);

        BlockPos entranceCenterWorld = surface.below(30);
        BlockPos endCenterWorld = new BlockPos(
                entranceCenterWorld.getX(),
                Math.max(worldMinY + 12, Math.min(centerYHint.getY(), entranceCenterWorld.getY() - drop)),
                entranceCenterWorld.getZ()
        );
        LOGGER.info("[Proc] Desired centers: entranceCenterWorld={}  endCenterWorld={}  (drop={})",
                entranceCenterWorld, endCenterWorld, entranceCenterWorld.getY() - endCenterWorld.getY());

        // Base procedural config
        ProcConfig base = ProcConfig.fromDungeonConfig(this.cfg, rnd);

        // Compute an initial shell that roughly fits the world depth (clamped by config)
        float baseShell = clampShell(base.shellMin, base.shellMax, surface, ctx.heightAccessor());
        LOGGER.info("[Proc] Shell: base={}  (min={} max={})", String.format("%.1f", baseShell), base.shellMin, base.shellMax);

        // Adaptive relax-and-retry loop (slightly loosens spacing & enlarges shell)
        int passes = base.maxRelaxPasses + 1;
        for (int pass = 0; pass < passes; pass++) {
            // Copy the base config and nudge clearances/shell for this pass
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

            // Corridor routing preferences
            pc.requiredMainPaths = base.requiredMainPaths;
            pc.straightPenalty = base.straightPenalty;
            pc.jitter = base.jitter;
            pc.wallBias = base.wallBias;
            pc.skipEndpoint = base.skipEndpoint;
            pc.gridPad = base.gridPad;

            float shell = (float) (baseShell * Math.pow(pc.relaxShellScale, pass));

            LOGGER.info("[Proc] === PASS {}/{}  rCorr={}  cPad={}  cRoom={}  shell={} ===",
                    pass + 1, passes, pc.rCorr, pc.cPad, pc.cRoom, String.format("%.1f", shell));

            // Compute voxel bounds (world-space) for the mask grid
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

            // --- 1) Place rooms with baked-in clearance
            ArrayList<RoomTemplateDef> pool = new ArrayList<>();
            pool.addAll(RoomDefs.BASIC);
            pool.addAll(RoomDefs.BRANCH_END);
            pool.addAll(RoomDefs.QUEEN); // ensure queen rooms exist for critical-path caps

            RoomPlacer placer = new RoomPlacer(pc, new java.util.Random(rnd.nextLong()));
            List<RoomPlacer.Placed> placed = placer.placeAll(
                    pool, entScan, endScan,
                    surface, endCenterWorld,
                    worldMinY, surfY - 2,
                    mRoom
            );

            LOGGER.info("[Proc/Place] pass={}  placed={}  (min={} target={} max={})",
                    pass + 1, placed.size(), pc.minRooms, pc.targetRooms, pc.maxRooms);

            if (placed.size() < pc.minRooms) {
                LOGGER.info("[Proc] Not enough rooms this pass ({} < {}). Relaxing and retrying...", placed.size(), pc.minRooms);
                continue; // retry next pass with looser spacing
            }

            // --- 2) Build free-space & backbone graph
            int R = pc.effectiveRadius(); // dilation radius for routing
            VoxelMask3D mFree = Morph3D.freeMaskFromRooms(mRoom, R);
            LOGGER.info("[Proc/Free] effectiveRadius={} (free-mask built)", R);
            int[][][] Dfree = DistanceField3D.manhattanDist(mFree); // kept for debugging/metrics
            FreeGraph G = FreeGraph.build(mFree, pc.backboneStride);
            LOGGER.info("[Proc/Free] backbone stride={} (graph built)", pc.backboneStride);

            // --- 3) Build portal set and choose entrance->end pairings
            List<FlowRouter.PNode> P;
            List<FlowRouter.EdgeCand> CE;
            List<FlowRouter.RoutedPair> mainPairs;

            try {
                P = FlowRouter.collectPortals(placed);

                // identify entrance/end rooms first
                RoomPlacer.Placed entPlaced = placed.stream()
                        .filter(p -> p.def().type() == RoomTemplateDef.RoomType.ENTRANCE)
                        .findFirst().orElse(null);
                RoomPlacer.Placed endPlaced = placed.stream()
                        .filter(p -> p.def().type() == RoomTemplateDef.RoomType.END)
                        .findFirst().orElse(null);
                if (entPlaced == null || endPlaced == null) return fail("missing entrance/end rooms");

                // Build entrance/end *lists* from P (no more undefined vars)
                List<FlowRouter.PNode> entrancePortList = new ArrayList<>();
                List<FlowRouter.PNode> endPortList = new ArrayList<>();
                for (FlowRouter.PNode pn : P) {
                    if (pn.room() == entPlaced) entrancePortList.add(pn);
                    if (pn.room() == endPlaced) endPortList.add(pn);
                }
                LOGGER.info("(FlowRouter) [Portal] entrancePorts={} endPorts={}", entrancePortList.size(), endPortList.size());

                // build candidates (routes in free space)
                CE = FlowRouter.candidateEdges(P, G, pc);

                // debug: how many edges leave entrance & enter end
                long outFromEntrance = CE.stream().filter(ec -> P.get(ec.a()).room() == entPlaced).count();
                long intoEnd = CE.stream().filter(ec -> P.get(ec.b()).room() == endPlaced).count();
                LOGGER.info("(FlowRouter) [Cand] edges from entrance={}  into end={}", outFromEntrance, intoEnd);

                LOGGER.info("(DungeonWorldBuilder) [Proc/Portal] portals={}  candidateEdges={}", P.size(), CE.size());

                mainPairs = FlowRouter.pickKDisjoint(P, CE, entPlaced, endPlaced, pc.requiredMainPaths);
                if (mainPairs.isEmpty()) {
                    LOGGER.info("(DungeonWorldBuilder) [Proc/Portal] Could not reserve {} routes, falling back to 1",
                            pc.requiredMainPaths);
                    mainPairs = FlowRouter.pickKDisjoint(P, CE, entPlaced, endPlaced, 1);
                    if (mainPairs.isEmpty()) return fail("no entrance->end pairing");
                }

                // --- 4) Emit rooms (first)
                for (RoomPlacer.Placed p : placed) {
                    Vec3i rs = Transforms.rotatedSize(p.scan().size(), p.rot());
                    BoundingBox bb = BoundingBox.fromCorners(
                            p.origin(),
                            p.origin().offset(rs.getX() - 1, rs.getY() - 1, rs.getZ() - 1)
                    );
                    // Expand by 1 so neighbor chunks include the piece fully
                    bb = new BoundingBox(
                            bb.minX() - 1, bb.minY() - 1, bb.minZ() - 1,
                            bb.maxX() + 1, bb.maxY() + 1, bb.maxZ() + 1
                    );

                    out.addPiece(new TemplatePiece(p.def(), p.origin(), p.rot(), bb));
                    LOGGER.info("[Proc/Layout] Room {} at {} size(rot)={} rot={}", p.def().id(), p.origin(), rs, p.rot());
                }

                // --- 5) Route and emit corridors ---------------------------------------------
                Random jrand = new Random(rnd.nextLong());
                int emittedCorr = 0;

// Plan: entrance->...->queen (critical), then queen->end (non-critical)
                CriticalPlan cplan = planCriticalRoutes(P, CE, entrancePortList, endPortList, placed, pc, jrand);

// Route set order: critical chains first, then queen->end links
                List<FlowRouter.RoutedPair> pairs = new ArrayList<>(cplan.chainPairs);
                pairs.addAll(cplan.qToEndPairs);

// Grow minimal extra links so every room is reachable
                pairs.addAll(FlowRouter.connectAllRooms(P, CE, cplan.chainPairs));

// Spawn forks (configurable) with a global budget derived from the number of critical paths
                int maxForksTotal = Math.max(0, pc.forkMaxPerPath * Math.max(1, cplan.chainPairs.isEmpty() ? 1 : 3));
                pairs.addAll(spawnForks(P, CE, pairs, placed, pc, jrand, maxForksTotal));

// Mark *only* entrance->queen pairs as critical
                java.util.Set<FlowRouter.RoutedPair> criticalSet = new java.util.HashSet<>(cplan.chainPairs);

                for (FlowRouter.RoutedPair pair : pairs) {
                    int ap = corridorApertureFor(pair, pc);

                    List<BlockPos> path = dockedRoute(G, pair, 2, 2, 4, 2);
                    if (path.isEmpty() || path.size() < 2) {
                        LOGGER.info("[Proc/Route] Docked route failed {}:{} -> {}:{}",
                                pair.A().worldPos(), pair.A().facing(), pair.B().worldPos(), pair.B().facing());
                        continue;
                    }

                    Direction startDir = dirFrom(path, 0);
                    Direction endDir = dirFrom(path, Math.max(0, path.size() - 2));
                    BoundingBox box = corridorBox(path, ap);
                    box = new BoundingBox(box.minX() - 1, box.minY() - 1, box.minZ() - 1,
                            box.maxX() + 1, box.maxY() + 1, box.maxZ() + 1);

                    boolean isCritical = criticalSet.contains(pair);
                    LOGGER.info("[Proc/Layout] Corridor {} -> {}  len={} ap={}  critical={}",
                            pair.A().worldPos(), pair.B().worldPos(), path.size(), ap, isCritical);

                    out.addPiece(new CorridorPiece(
                            path, ap, box,
                            path.get(0), path.get(path.size() - 1),
                            startDir, endDir,
                            isCritical
                    ));
                    emittedCorr++;
                }
                LOGGER.info("[Proc] Done: rooms={} corridors={}", placed.size(), emittedCorr);
                return true; // success on this pass

            } catch (Throwable t) {
                LOGGER.error("(DungeonWorldBuilder) [Proc/Portal] Exception: {}", t, t);
                return fail("exception during portal/flow stage");
            }
        }

        // If we got here, all passes failed to hit minRooms
        return fail("too few rooms after placement (all relax passes exhausted)");
    }

    private ScannedTemplate scan(ResourceLocation id) {
        var cached = scanCache.get(id);
        if (cached != null) return cached;
        var server = ServerHolder.get();
        if (server == null) {
            LOGGER.info("[Proc] scan FAIL: server is null for {}", id);
            return null;
        }
        var tm = server.getStructureManager();
        var s = TemplatePortScanner.scan(tm, id);
        scanCache.put(id, s);
        LOGGER.info("[Proc] Scanned template id={}  size={}  entrances={}  exits={}",
                id, s.size(), s.entrances().size(), s.exits().size());
        return s;
    }

    private boolean fail(String msg) {
        LOGGER.info("[Proc] FAIL: {}", msg);
        return false;
    }

    /**
     * @param chainPairs  entrance -> ... -> queen (critical)
     * @param qToEndPairs queen -> end (non-critical)
     */
    private record CriticalPlan(List<FlowRouter.RoutedPair> chainPairs, List<FlowRouter.RoutedPair> qToEndPairs) {
    }
}