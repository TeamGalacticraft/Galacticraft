package dev.galacticraft.mod.world.gen.dungeon;

import com.mojang.logging.LogUtils;
import dev.galacticraft.mod.ServerHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

import java.util.*;

/**
 * Orchestrates dungeon build. Now with verbose logging and error paths.
 */
public final class DungeonWorldBuilder {
    private static final Logger LOGGER = LogUtils.getLogger();

    // --- state ---
    private final DungeonConfig cfg;
    private final ProcConfig pc;
    private final RandomSource rnd;

    private VoxelMask3D mRoom;     // occupied by rooms
    private VoxelMask3D mCarve;    // occupied by corridors

    private int worldMinY = -64;
    private int worldMaxY = 320;

    // track consumed portals so they cannot be reused (prevents branching)
    private final java.util.Set<PortalKey> usedPorts = new java.util.HashSet<>();

    private static record PortalKey(RoomPlacer.Placed room, BlockPos localPos, boolean isExit) {
        static PortalKey of(FlowRouter.PNode pn) {
            return new PortalKey(pn.room(), pn.port().localPos(), pn.port().isExit());
        }
    }

    private final ArrayList<RoomPlacer.Placed> rooms = new ArrayList<>();
    private final RoomPlacer roomPlacer;

    private final Map<RoomPlacer.Placed, Integer> roomDegree = new HashMap<>();
    private final Map<RoomPlacer.Placed, RoomTemplateDef> overrideDef = new HashMap<>();

    // --- cache for routing graph ---
    private FreeGraph cachedGraph = null;
    private boolean graphDirty = true;   // invalidate when rooms/corridors change

    // --- local routing ROI sizing (tweakable) ---
    private static final int ROI_MIN_MARGIN = 12;    // blocks
    private static final int ROI_SCALE      = 6;     // margin ≈ effectiveRadius * ROI_SCALE

    /** Build a small local FreeGraph around aOut..bOut instead of the whole mask. */
    private FreeGraph buildLocalGraph(BlockPos aOut, BlockPos bOut) {
        // --- ROI sizing (tweakable) ---
        final int ROI_MIN_MARGIN = 12;           // blocks
        final int ROI_SCALE      = 6;            // margin ≈ effectiveRadius * ROI_SCALE

        int r = Math.max(ROI_MIN_MARGIN, pc.effectiveRadius() * ROI_SCALE);

        int minX = Math.min(aOut.getX(), bOut.getX()) - r;
        int minY = Math.min(aOut.getY(), bOut.getY()) - r;
        int minZ = Math.min(aOut.getZ(), bOut.getZ()) - r;
        int maxX = Math.max(aOut.getX(), bOut.getX()) + r;
        int maxY = Math.max(aOut.getY(), bOut.getY()) + r;
        int maxZ = Math.max(aOut.getZ(), bOut.getZ()) + r;

        // Clamp to mask world box (assumes VoxelMask3D exposes origin & dims)
        minX = (int) Math.max(minX, mRoom.ox());
        minY = (int) Math.max(minY, mRoom.oy());
        minZ = (int) Math.max(minZ, mRoom.oz());
        maxX = (int) Math.min(maxX, mRoom.ox() + mRoom.nx() - 1);
        maxY = (int) Math.min(maxY, mRoom.oy() + mRoom.ny() - 1);
        maxZ = (int) Math.min(maxZ, mRoom.oz() + mRoom.nz() - 1);

        // Build FREE mask once from rooms, then copy only ROI into a compact mask.
        VoxelMask3D freeFull = Morph3D.freeMaskFromRooms(mRoom, pc.effectiveRadius());

        int nx = maxX - minX + 1, ny = maxY - minY + 1, nz = maxZ - minZ + 1;
        VoxelMask3D freeROI = new VoxelMask3D(nx, ny, nz, minX, minY, minZ);

        // Copy ROI bits (world coords → each mask’s local coords)
        for (int z = 0; z < nz; z++) {
            int wz = minZ + z;
            for (int y = 0; y < ny; y++) {
                int wy = minY + y;
                for (int x = 0; x < nx; x++) {
                    int wx = minX + x;
                    int fx = freeFull.gx(wx), fy = freeFull.gy(wy), fz = freeFull.gz(wz);
                    if (freeFull.in(fx, fy, fz) && freeFull.get(fx, fy, fz)) {
                        int gx = freeROI.gx(wx), gy = freeROI.gy(wy), gz = freeROI.gz(wz);
                        freeROI.set(gx, gy, gz, true);
                    }
                }
            }
        }

        int stride = Math.max(2, pc.backboneStride); // keep ROI sparse
        FreeGraph G = FreeGraph.build(freeROI, stride);
        LOGGER.info("[DWBuilder] Local routing graph: ROI=({}, {}, {})→({}, {}, {})  nodes={} edges={} stride={}",
                minX, minY, minZ, maxX, maxY, maxZ, G.nodeCount(), G.edgeCount(), G.getStride());
        return G;
    }

    public DungeonWorldBuilder(DungeonConfig cfg, RandomSource rnd) {
        this.cfg = Objects.requireNonNull(cfg, "cfg");
        this.rnd = Objects.requireNonNull(rnd, "rnd");
        this.pc  = ProcConfig.fromDungeonConfig(cfg, rnd);
        this.roomPlacer = new RoomPlacer(this.pc, new java.util.Random(rnd.nextLong()));
    }

    private static int clamp(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); }

    private static AABB unionAABBOf(List<RoomPlacer.Placed> rs) {
        if (rs.isEmpty()) return new AABB(0,0,0,0,0,0);
        AABB a = rs.get(0).aabb();
        for (int i=1;i<rs.size();i++) a = a.minmax(rs.get(i).aabb());
        return a;
    }

    static AABB expandXZ(AABB box, double s) {
        double cx = (box.minX + box.maxX) * 0.5;
        double cz = (box.minZ + box.maxZ) * 0.5;
        double rx = (box.maxX - box.minX) * 0.5 * (1.0 + s);
        double rz = (box.maxZ - box.minZ) * 0.5 * (1.0 + s);
        return new AABB(cx - rx, box.minY, cz - rz, cx + rx, box.maxY, cz + rz);
    }

    private static void writeRoomToMask(VoxelMask3D mask, RoomPlacer.Placed p) {
        Vec3i sz = Transforms.rotatedSize(p.scan().size(), p.rot());
        int x0 = mask.gx(p.origin().getX());
        int y0 = mask.gy(p.origin().getY());
        int z0 = mask.gz(p.origin().getZ());
        mask.fillAABB(x0, y0, z0, x0 + sz.getX() - 1, y0 + sz.getY() - 1, z0 + sz.getZ() - 1, true);
    }
    private void roomPlacerWriteMask(VoxelMask3D m, RoomPlacer.Placed p) { writeRoomToMask(m, p); }

    private static BoundingBox toBB(AABB a) {
        return new BoundingBox((int)Math.floor(a.minX), (int)Math.floor(a.minY), (int)Math.floor(a.minZ),
                (int)Math.ceil(a.maxX),  (int)Math.ceil(a.maxY),  (int)Math.ceil(a.maxZ));
    }

    static boolean isEntranceNode(FlowRouter.PNode n) { return n.port().isEntrance(); }
    static boolean isExitNode(FlowRouter.PNode n)     { return n.port().isExit(); }

    // --------- NEW: mask init + logging ---------

    /** Build a conservative world-space box for masks, centered around surface/centerHint with shell+pad. */
    private AABB computeMaskWorldBox(BlockPos surface, BlockPos centerHint) {
        // cover both the surface anchor and the underground cluster (end+queens)
        int pad   = Math.max(8, pc.gridPad);
        int shell = Math.max(16, Math.round(pc.shellMax));
        int minX = Math.min(surface.getX(), centerHint.getX()) - shell - pad;
        int minZ = Math.min(surface.getZ(), centerHint.getZ()) - shell - pad;
        int maxX = Math.max(surface.getX(), centerHint.getX()) + shell + pad;
        int maxZ = Math.max(surface.getZ(), centerHint.getZ()) + shell + pad;

        // vertical: use worldMinY..worldMaxY but clamp near surface +/- shell
        int minY = Math.max(worldMinY, Math.min(surface.getY() - (shell + pad), centerHint.getY() - pad));
        int maxY = Math.min(worldMaxY, Math.max(surface.getY() + pad, centerHint.getY() + (shell + pad)));

        AABB box = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        LOGGER.info("[DWBuilder] Mask box: min=({}, {}, {}) max=({}, {}, {}) size=({} x {} x {})",
                (int)box.minX, (int)box.minY, (int)box.minZ,
                (int)box.maxX, (int)box.maxY, (int)box.maxZ,
                (int)(box.maxX - box.minX + 1), (int)(box.maxY - box.minY + 1), (int)(box.maxZ - box.minZ + 1));
        return box;
    }

    private void initMasks(BlockPos surface, BlockPos centerHint) {
        AABB box = computeMaskWorldBox(surface, centerHint);
        int nx = (int)(box.maxX - box.minX + 1);
        int ny = (int)(box.maxY - box.minY + 1);
        int nz = (int)(box.maxZ - box.minZ + 1);
        int ox = (int)box.minX;
        int oy = (int)box.minY;
        int oz = (int)box.minZ;

        this.mRoom  = new VoxelMask3D(nx, ny, nz, ox, oy, oz);
        this.mCarve = new VoxelMask3D(nx, ny, nz, ox, oy, oz);
        // routing graph will be (re)built lazily
        this.cachedGraph = null;
        this.graphDirty = true;

        LOGGER.info("[DWBuilder] Masks initialized: dims=({},{},{}) origin=({}, {}, {})",
                nx, ny, nz, ox, oy, oz);
    }

    // --------- scatter basics (same as before, but with logs) ---------
    private ArrayList<RoomPlacer.Placed> scatterBasicsInside(AABB area, int wantCount,
                                                             List<RoomTemplateDef> basicDefs,
                                                             List<RoomPlacer.Placed> already,
                                                             VoxelMask3D mRoom,
                                                             java.util.Random jr) {
        var out = new ArrayList<RoomPlacer.Placed>();
        if (wantCount <= 0) return out;

        LOGGER.info("[DWBuilder] Scatter basics: want={} area=({}, {}, {})→({}, {}, {})",
                wantCount, area.minX, area.minY, area.minZ, area.maxX, area.maxY, area.maxZ);

        int attempts = Math.max(400, wantCount * 20);
        int placed = 0, rejectedClash = 0, rejectedPortal = 0, rejectedOOB = 0;
        for (int tries = 0; tries < attempts && out.size() < wantCount; tries++) {
            var def = basicDefs.get(jr.nextInt(basicDefs.size()));
            var scan = TemplatePortScanner.scan(ServerHolder.get().getStructureManager(), def.id());
            if (scan == null) {
                LOGGER.warn("[DWBuilder] Scan for {} returned null; skipping", def.id());
                continue;
            }

            Rotation rot = Rotation.values()[jr.nextInt(4)];
            Vec3i rs = Transforms.rotatedSize(scan.size(), rot);

            int rx = (int)Math.round(area.minX + 1 + jr.nextDouble() * Math.max(1, area.getXsize() - rs.getX() - 2));
            int rz = (int)Math.round(area.minZ + 1 + jr.nextDouble() * Math.max(1, area.getZsize() - rs.getZ() - 2));
            // place roughly near end Y (rooms[1] will be END after entrance)
            int endY = rooms.size() > 1 ? rooms.get(1).origin().getY() : (int)Math.round((area.minY + area.maxY) * 0.5);
            int ry = (int)Math.round(Math.max(area.minY + 1, Math.min(area.maxY - rs.getY() - 1, endY)));

            BlockPos origin = new BlockPos(rx, ry, rz);
            AABB box = new AABB(origin.getX(), origin.getY(), origin.getZ(),
                    origin.getX()+rs.getX(), origin.getY()+rs.getY(), origin.getZ()+rs.getZ());
            var placedCand = new RoomPlacer.Placed(scan, def, rot, origin, box);

            // bounds check against mask (avoid AIOOB later)
            if (!maskBoundsOk(mRoom, placedCand)) { rejectedOOB++; continue; }

            // clearance check vs already & out
            AABB infl = box.inflate(this.pc.cRoom);
            boolean clash = false;
            for (var p : already) if (infl.intersects(p.aabb().inflate(pc.cRoom))) { clash = true; break; }
            if (!clash) for (var p : out) if (infl.intersects(p.aabb().inflate(pc.cRoom))) { clash = true; break; }
            if (clash) { rejectedClash++; continue; }

            // portals viability (cheap)
            if (!QueenPlanner.portalsViableLikeRoomPlacer(placedCand, mRoom, this.pc)) { rejectedPortal++; continue; }

            out.add(placedCand);
            placed++;
        }

        LOGGER.info("[DWBuilder] Scatter basics result: placed={} rejected[clash={}/portal={}/oob={}] attempts={}",
                placed, rejectedClash, rejectedPortal, rejectedOOB, attempts);
        return out;
    }

    private static int countBasics(List<RoomPlacer.Placed> rs) {
        int c=0; for (var p : rs) if (p.def().type() == RoomTemplateDef.RoomType.BASIC) c++; return c;
    }

    private void convertLeafBasicsToTreasure(List<RoomPlacer.Placed> rooms, int treasureCount, java.util.Random jr) {
        LOGGER.info("[DWBuilder] convertLeafBasicsToTreasure: want={}", treasureCount);
        if (treasureCount <= 0) return;

        ArrayList<RoomPlacer.Placed> candidates = new ArrayList<>();
        for (var p : rooms) {
            if (p.def().type() != RoomTemplateDef.RoomType.BASIC) continue;
            int deg = roomDegree.getOrDefault(p, 0);
            if (deg <= 1) { // leaf (no outgoing branches besides its single attachment)
                candidates.add(p);
            }
        }
        if (candidates.isEmpty()) {
            LOGGER.info("[DWBuilder] No BASIC leaf rooms to upgrade to treasure.");
            return;
        }
        Collections.shuffle(candidates, jr);

        int done = 0;
        for (var p : candidates) {
            if (done >= treasureCount) break;
            RoomTemplateDef tre = RoomDefs.BRANCH_END.get(jr.nextInt(RoomDefs.BRANCH_END.size()));
            overrideDef.put(p, tre);
            done++;
            LOGGER.info("[DWBuilder] Treasure swap: {} -> {} at {}", p.def().id(), tre.id(), p.origin());
        }
        LOGGER.info("[DWBuilder] Treasure swaps applied: {}/{}", done, treasureCount);
    }

    // ---------------- MAIN ENTRY ----------------
    public boolean build(Structure.GenerationContext ctx,
                         StructurePiecesBuilder pieces,
                         BlockPos surface,
                         BlockPos centerHint) {
        long seedLog = rnd.nextLong(); // don’t advance rnd; just log something stable-ish
        LOGGER.info("[DWBuilder] build() start surface={} centerHint={} rndSeed?={} cfg[minPoints={},maxPoints={},sphereRadiusMin={},sphereRadiusMax={}] pc[shellMin={},shellMax={},gridPad={},rCorr={},cPad={},cRoom={}]",
                surface, centerHint, seedLog,
                cfg.minPoints(), cfg.maxPoints(), cfg.sphereRadiusMin(), cfg.sphereRadiusMax(),
                pc.shellMin, pc.shellMax, pc.gridPad, pc.rCorr, pc.cPad, pc.cRoom);

        try {
            // init masks before any room writes
            initMasks(surface, centerHint);

            RandomSource rnd = this.rnd;
            java.util.Random jr = new java.util.Random(rnd.nextLong());

            int totalRooms = clamp(this.cfg.maxPoints(), this.cfg.minPoints(), this.cfg.maxPoints());
            int usable = Math.max(0, totalRooms - 5);
            int targetBasicsPlusTreasure = usable;
            int treasureCount = Math.round(targetBasicsPlusTreasure * this.pc.treasureFraction);
            int basicsCount   = targetBasicsPlusTreasure - treasureCount;
            int criticalBasicsBudget = Math.round(targetBasicsPlusTreasure * this.pc.criticalBasicFraction);

            LOGGER.info("[DWBuilder] counts: total={} usable={} basics={} treasure={} criticalBasicBudget={}",
                    totalRooms, usable, basicsCount, treasureCount, criticalBasicsBudget);

            // 1) Entrance
            var entranceScan = TemplatePortScanner.scan(ServerHolder.get().getStructureManager(), RoomDefs.ENTRANCE.id());
            if (entranceScan == null) {
                LOGGER.error("[DWBuilder] Entrance scan returned null for {}", RoomDefs.ENTRANCE.id());
                return false;
            }
            int yTop = surface.getY();
            BlockPos entOrigin = surface.offset(-entranceScan.size().getX()/2, -10 - entranceScan.size().getY(), -entranceScan.size().getZ()/2);
            var entPlaced = roomPlacer.placeFixed(entranceScan, RoomDefs.ENTRANCE, Rotation.NONE, entOrigin);
            LOGGER.info("[DWBuilder] Entrance placed: origin={} size={} rot={}", entPlaced.origin(), entranceScan.size(), entPlaced.rot());
            if (!maskBoundsOk(mRoom, entPlaced)) {
                LOGGER.error("[DWBuilder] Entrance AABB out of mask bounds; origin={} size={}", entPlaced.origin(), entranceScan.size());
                return false;
            }
            rooms.add(entPlaced);
            roomPlacerWriteMask(mRoom, entPlaced);

            // ---- 2) Choose End center: exactly 20 above BEDROCK (worldMinY), with small XZ jitter ----
            var endScan = TemplatePortScanner.scan(ServerHolder.get().getStructureManager(), RoomDefs.pickEnd(jr).id());
            int endHalfY = Math.max(1, endScan.size().getY() / 2);
            // Y target = worldMinY + 20 (above bedrock), center Y accounts for half the template height
            int endCY = Math.max(worldMinY + 20 + endHalfY, worldMinY + 8); // keep >= min + 8
            int dx = jr.nextInt(41) - 20; // [-20..20]
            int dz = jr.nextInt(41) - 20;
            BlockPos endCenterHint = new BlockPos(surface.getX() + dx, endCY, surface.getZ() + dz);
            LOGGER.info("[DWBuilder] End center (20 above bedrock): hint={} (dx={},dz={},halfY={})", endCenterHint, dx, dz, endHalfY);

            // place END near that hint (keeps retry/portal checks)
            var endPlaced = roomPlacer.tryPlaceEnd(endScan, endCenterHint, worldMinY, worldMaxY, rooms, mRoom);
            if (endPlaced == null) {
                LOGGER.error("[DWBuilder] Failed to place END near bedrock target {}", endCenterHint);
                return false;
            }
            rooms.add(endPlaced);
            roomPlacerWriteMask(mRoom, endPlaced);
            LOGGER.info("[DWBuilder] End placed: origin={} rot={} aabb={}", endPlaced.origin(), endPlaced.rot(), endPlaced.aabb());

            // 3) Queens
            var queens = new QueenPlanner(pc, jr).placeQueens(roomPlacer, rooms, mRoom, endPlaced, RoomDefs.QUEEN, worldMinY, worldMaxY);
            LOGGER.info("[DWBuilder] Queens placed: {}", queens.size());
            if (queens.size() < 3) {
                LOGGER.error("[DWBuilder] Not enough queens (need 3). Got {}.", queens.size());
                return false;
            }

            // 4) Candidate basics area
            AABB bbox = unionAABBOf(rooms);
            AABB expanded = expandXZ(bbox, 1.0);
            expanded.setMaxY(expanded.maxY - 20);
            LOGGER.info("[DWBuilder] Core bbox={}  expandedXZ(+100%)={}", bbox, expanded);

            List<RoomTemplateDef> basicDefs = new ArrayList<>(RoomDefs.BASIC);
            ArrayList<RoomPlacer.Placed> candidateBasics =
                    gatherBasicsWithRelax(expanded, basicsCount, basicDefs, rooms, mRoom, jr);
            LOGGER.info("[DWBuilder] Candidate basics (after relax): {}", candidateBasics.size());

            try {
                // steps 5–8 (split, solve, carve, connect queens to end)
                // ---- 5) Pick critical set and split ----
                int needCritical = Math.min(criticalBasicsBudget, candidateBasics.size());
                if (needCritical < criticalBasicsBudget) {
                    LOGGER.warn("[DWBuilder] Not enough candidate basics for full critical budget: need={} have={}. Reducing.",
                            criticalBasicsBudget, candidateBasics.size());
                }
                var split = CriticalSetPicker.pickAndSplit(candidateBasics, jr, /*totalBasics=*/needCritical);
                LOGGER.info("[DWBuilder] Critical split sizes: p1={} p2={} p3={}", split.path1.size(), split.path2.size(), split.path3.size());

                // ---- 6) Solve minimal path for each (Entrance -> mids -> Queen) ----
                var q1 = queens.get(0); var q2 = queens.get(1); var q3 = queens.get(2);
                var path1Nodes = concatNodes(entPlaced, split.path1, q1);
                var path2Nodes = concatNodes(entPlaced, split.path2, q2);
                var path3Nodes = concatNodes(entPlaced, split.path3, q3);

                CriticalPathPlanner.Result res1, res2, res3;

                try {
                    res1 = CriticalPathPlanner.solveNearest(path1Nodes);
                    res2 = CriticalPathPlanner.solveNearest(path2Nodes);
                    res3 = CriticalPathPlanner.solveNearest(path3Nodes);
                    LOGGER.info("[DWBuilder] TSP/Greedy results ok? p1={} p2={} p3={}", res1.ok(), res2.ok(), res3.ok());
                } catch (Throwable t) {
                    LOGGER.error("[DWBuilder] CRITICAL: exception during critical-path solve", t);
                    return false; // bail early with a clear log instead of silent failure
                }

                // If any failed (shouldn’t with greedy), log and continue
                if (!res1.ok() || !res2.ok() || !res3.ok()) {
                    LOGGER.error("[DWBuilder] One or more critical paths failed even with greedy (p1={} p2={} p3={})",
                            res1.ok(), res2.ok(), res3.ok());
                    // Don't hard-fail; we’ll carve direct Entrance→Queen for the failed ones.
                }

                // ---- Add chosen BASICs to the world state ----
                for (var p : split.path1) addRoom(p);
                for (var p : split.path2) addRoom(p);
                for (var p : split.path3) addRoom(p);

                // ---- 7) Carve each critical path leg-by-leg ----
                java.util.function.BiConsumer<java.util.List<RoomPlacer.Placed>, CriticalPathPlanner.Result> carvePath =
                        (nodeList, res) -> {
                            if (res.ok() && res.order() != null && res.order().length >= 2) {
                                int[] ord = res.order();
                                for (int i = 0; i < ord.length - 1; i++) {
                                    var A = nodeList.get(ord[i]);
                                    var B = nodeList.get(ord[i+1]);
                                    if (!connectRooms(pieces, A, B, /*critical=*/true, jr)) {
                                        LOGGER.warn("[DWBuilder] Critical leg failed: {} -> {}", A.def().id(), B.def().id());
                                    }
                                }
                            } else {
                                // Fallback: Entrance→Queen direct
                                var A = nodeList.get(0);
                                var B = nodeList.get(nodeList.size()-1);
                                if (!connectRooms(pieces, A, B, true, jr)) {
                                    LOGGER.warn("[DWBuilder] Fallback Entrance→Queen route failed: {} -> {}", A.def().id(), B.def().id());
                                }
                            }
                        };

                carvePath.accept(path1Nodes, res1);
                carvePath.accept(path2Nodes, res2);
                carvePath.accept(path3Nodes, res3);

                // 8) Link queens -> end
                for (var q : queens) {
                    if (!connectRooms(pieces, q, endPlaced, /*critical=*/true, jr)) {
                        LOGGER.warn("[DWBuilder] Queen→End route failed: {} -> {}", q.def().id(), endPlaced.def().id());
                    }
                }

                // 9) Grow branches until basicsCount
                // Remaining candidates we didn't use in paths
                HashSet<RoomPlacer.Placed> used = new HashSet<>();
                used.addAll(split.path1); used.addAll(split.path2); used.addAll(split.path3);
                ArrayList<RoomPlacer.Placed> leftovers = new ArrayList<>();
                for (var c : candidateBasics) if (!used.contains(c)) leftovers.add(c);

                int placedBasics = countBasics(rooms);
                LOGGER.info("[DWBuilder] Branch growth start: basicsPlaced={} targetBasics={} leftovers={}", placedBasics, basicsCount, leftovers.size());

                // Attach leftovers one-by-one to closest existing room with a viable route
                for (var cand : leftovers) {
                    if (placedBasics >= basicsCount) break;

                    // Try attach to the nearest existing room (by center distance)
                    RoomPlacer.Placed best = null;
                    double bestD2 = Double.POSITIVE_INFINITY;
                    var cbox = cand.aabb();
                    double cx = (cbox.minX + cbox.maxX)*0.5, cy = (cbox.minY + cbox.maxY)*0.5, cz = (cbox.minZ + cbox.maxZ)*0.5;

                    for (var exist : rooms) {
                        var ebox = exist.aabb();
                        double ex = (ebox.minX + ebox.maxX)*0.5, ey = (ebox.minY + ebox.maxY)*0.5, ez = (ebox.minZ + ebox.maxZ)*0.5;
                        double d2 = (ex-cx)*(ex-cx) + (ey-cy)*(ey-cy) + (ez-cz)*(ez-cz);
                        if (d2 < bestD2) { bestD2 = d2; best = exist; }
                    }
                    if (best == null) continue;

                    // Add the room to masks before routing so FreeGraph “sees” both ends
                    addRoom(cand);

                    if (connectRooms(pieces, best, cand, /*critical=*/false, jr)) {
                        placedBasics++;
                    } else {
                        LOGGER.warn("[DWBuilder] Could not attach BASIC {} to {}", cand.def().id(), best.def().id());
                        // If attach failed, you may optionally remove from rooms/masks here; we’ll keep it to avoid oscillation.
                    }
                }
                LOGGER.info("[DWBuilder] Branch growth end: basicsPlaced={}/{}", placedBasics, basicsCount);
            } catch (Throwable t) {
                LOGGER.error("[DWBuilder] FATAL in critical-path pipeline", t);
                return false;
            }

            // 10) Treasure leaf conversion
            convertLeafBasicsToTreasure(rooms, treasureCount, jr);

            // 11) Emit pieces
            int emitted = 0;
            for (var r : rooms) {
                RoomTemplateDef def = overrideDef.getOrDefault(r, r.def());
                pieces.addPiece(new TemplatePiece(def, r.origin(), r.rot(), toBB(r.aabb())));
                emitted++;
            }
            LOGGER.info("[DWBuilder] Emitted {} room pieces (with treasure overrides where applicable).", emitted);

            LOGGER.info("[DWBuilder] build() SUCCESS");
            return true;

        } catch (Throwable t) {
            LOGGER.error("[DWBuilder] build() FAILED with exception", t);
            return false;
        }
    }

    // Piecewise-linear vertical bias. Higher weight => more likely to accept.
    private float depthBiasAtY(int y, int yEntrance, int yEnd) {
        int yEndPlus20 = yEnd + 20;

        if (y >= yEntrance) return 1.0f; // at/above entrance = baseline

        if (y <= yEnd) {
            // interpolate Entrance(1.0) -> End(1.2)
            float t = (float)(yEntrance - y) / Math.max(1f, (yEntrance - yEnd));
            return 1.0f + 0.2f * Math.min(1f, Math.max(0f, t));
        } else {
            // between End and End+20: End(1.2) -> End+20(1.5)
            float t = (float)(y - yEnd) / 20f;
            return 1.2f + 0.3f * Math.min(1f, Math.max(0f, t));
        }
    }

    /** Add a placed room into masks + list (idempotent add-by-identity) */
    private void addRoom(RoomPlacer.Placed p0) {
        try {
            RoomPlacer.Placed p = p0;

            if (p0.def().type() == RoomTemplateDef.RoomType.BASIC) {
                var fixed = roomPlacer.placeFixed(p0.scan(), p0.def(), p0.rot(), p0.origin());
                if (fixed != null) p = fixed;
                else LOGGER.warn("[DWBuilder] addRoom canonicalize failed; using original for {}", p0.def().id());
            }

            if (!rooms.contains(p)) {
                rooms.add(p);
                roomPlacerWriteMask(mRoom, p);
                // mark graph dirty so next route rebuilds once
                this.graphDirty = true;
                LOGGER.info("[DWBuilder] addRoom: {} at {} rot={} aabb={}", p.def().id(), p.origin(), p.rot(), p.aabb());
            }
        } catch (Throwable t) {
            LOGGER.error("[DWBuilder] addRoom exception", t);
        }
    }

    /** Build (or reuse) the routing graph from current occupancy (rooms + carved). */
    private FreeGraph buildGraphForRouting() {
        if (!graphDirty && cachedGraph != null) {
            return cachedGraph;
        }
        // mRoom = rooms solid; free = dilation of empty space around them
        VoxelMask3D free = Morph3D.freeMaskFromRooms(mRoom, pc.effectiveRadius());
        // slightly coarser stride helps a lot; keep >=1
        int stride = Math.max(1, pc.backboneStride);
        cachedGraph = FreeGraph.build(free, stride);
        graphDirty = false;
        LOGGER.info("[DWBuilder] Routing graph: nodes={} edges={} stride={}",
                cachedGraph.nodeCount(), cachedGraph.edgeCount(), cachedGraph.getStride());
        return cachedGraph;
    }

    // REPLACE ENTIRE METHOD
    private boolean connectRooms(StructurePiecesBuilder pieces,
                                 RoomPlacer.Placed a, RoomPlacer.Placed b,
                                 boolean critical, java.util.Random r) {
        try {
            if (a == b || a.origin().equals(b.origin())) {
                LOGGER.warn("[DWBuilder] Skipping self-connection: {}", a.def().id());
                return false;
            }

            LOGGER.info("[DWBuilder] connectRooms: {} -> {}  critical={}", a.def().id(), b.def().id(), critical);

            // Collect only these two rooms' portals & strict EXIT(A)->ENTR(B) candidates
            List<RoomPlacer.Placed> pair = java.util.List.of(a, b);
            List<FlowRouter.PNode> P = FlowRouter.collectPortals(pair);
            List<FlowRouter.EdgeCand> CE = FlowRouter.candidateEdges(P, /*G=*/null, pc);

            if (CE.isEmpty()) {
                LOGGER.warn("[DWBuilder] No EXIT→ENTR candidates ({} -> {}): portals={}  NOTE: polarity filter is strict",
                        a.def().id(), b.def().id(), P.size());
                return false;
            }

            // Pick best EXIT(A)->ENTR(B)
            FlowRouter.PNode Aport = null, Bport = null;
            float bestW = Float.POSITIVE_INFINITY;

            for (FlowRouter.EdgeCand ec : CE) {
                FlowRouter.PNode A0 = P.get(ec.a()), B0 = P.get(ec.b());
                if (A0.room() != a || B0.room() != b) continue;              // restrict to this pair
                if (!isExitNode(A0) || !isEntranceNode(B0)) continue;         // enforce polarity
                if (ec.w() < bestW) { bestW = ec.w(); Aport = A0; Bport = B0; }
            }

            if (Aport == null || Bport == null) {
                LOGGER.warn("[DWBuilder] Could not pick EXIT→ENTR pair between {} and {}  (cands={}, portals={})",
                        a.def().id(), b.def().id(), CE.size(), P.size());
                // If critical: try *any* exit/entr pairing ignoring weights, just to give routing a chance
                if (critical) {
                    for (FlowRouter.PNode x : P) for (FlowRouter.PNode y : P) {
                        if (x.room() == a && y.room() == b && isExitNode(x) && isEntranceNode(y)) { Aport = x; Bport = y; break; }
                    }
                    if (Aport == null) return false;
                } else {
                    return false;
                }
            }

            final int stepOut = Math.max(1, pc.effectiveRadius() + 1);
            BlockPos aPort = Aport.worldPos();
            BlockPos bPort = Bport.worldPos();
            BlockPos aOut  = aPort.relative(Aport.facing(), stepOut);
            BlockPos bOut  = bPort.relative(Bport.facing(), stepOut);

            // === ROUTING STAGES (escalation) ===
            // Each stage logs success/failure clearly.
            enum Stage { LOCAL_REPEL, LOCAL_WEAK, LOCAL_NONE, GLOBAL_REPEL, GLOBAL_NONE, DIRECT_LINE }
            record Attempt(Stage s, int stride, int repelR, float repelK, boolean global) {}

            List<Attempt> plan = new ArrayList<>(6);
            // Local ROI graph sized by buildLocalGraph(); soft repulsion near carved corridors
            plan.add(new Attempt(Stage.LOCAL_REPEL, Math.max(2, pc.backboneStride), /*repelR*/ 3, /*repelK*/ 6.5f, false));
            plan.add(new Attempt(Stage.LOCAL_WEAK,  Math.max(2, pc.backboneStride), /*repelR*/ 2, /*repelK*/ 3.0f, false));
            plan.add(new Attempt(Stage.LOCAL_NONE,  Math.max(2, pc.backboneStride), /*repelR*/ 0, /*repelK*/ 0.0f, false));
            // Escalate to global graph if locals fail
            plan.add(new Attempt(Stage.GLOBAL_REPEL, 1, /*repelR*/ 3, /*repelK*/ 6.5f, true));
            plan.add(new Attempt(Stage.GLOBAL_NONE,  1, /*repelR*/ 0, /*repelK*/ 0.0f, true));
            // Last resort: direct line, even if it passes through other space
            plan.add(new Attempt(Stage.DIRECT_LINE,  1, 0, 0.0f, true));

            List<BlockPos> poly = null;
            Stage usedStage = null;

            for (Attempt at : plan) {
                if (at.s == Stage.DIRECT_LINE) {
                    // Straight densified line: guarantees connectivity for critical recovery / diagnostics
                    poly = rasterLine3D(aOut, bOut, /*includeStart*/ false);
                    usedStage = at.s;
                    LOGGER.warn("[DWBuilder] ROUTE(Stage={}) used as last resort: direct voxel line {}→{} (len={})",
                            at.s, aOut, bOut, poly.size());
                    break;
                }

                FreeGraph G;
                if (at.global) {
                    VoxelMask3D free = Morph3D.freeMaskFromRooms(mRoom, pc.effectiveRadius());
                    G = FreeGraph.build(free, at.stride);
                    LOGGER.info("[DWBuilder] Routing graph (GLOBAL): nodes={} edges={} stride={}", G.nodeCount(), G.edgeCount(), G.getStride());
                } else {
                    G = buildLocalGraph(aOut, bOut); // already logs ROI + nodes/edges
                }

                VoxelMask3D avoid = (at.repelR > 0) ? Morph3D.dilate(mCarve, 1) : null;

                if (at.repelR > 0 && avoid != null && at.repelK > 0f) {
                    LOGGER.info("[DWBuilder] Route try Stage={} repelR={} repelK={} ({} graph)",
                            at.s, at.repelR, String.format("%.2f", at.repelK), at.global ? "GLOBAL" : "LOCAL");
                    poly = G.routeWithRepulsion(aOut, bOut, avoid, at.repelR, at.repelK, r);
                } else {
                    LOGGER.info("[DWBuilder] Route try Stage={} (no-avoid) ({} graph)", at.s, at.global ? "GLOBAL" : "LOCAL");
                    poly = G.route(aOut, bOut, /*ignoredFree*/null, 0, r);
                }

                if (poly != null && !poly.isEmpty()) { usedStage = at.s; break; }
                LOGGER.warn("[DWBuilder] Route failed Stage={} ({} graph).", at.s, at.global ? "GLOBAL" : "LOCAL");
            }

            if (poly == null || poly.isEmpty()) {
                LOGGER.error("[DWBuilder] ABORT: All routing stages failed {} -> {}  (critical={})", a.def().id(), b.def().id(), critical);
                return false;
            }

            // Build full poly incl. step-outs
            ArrayList<BlockPos> full = new ArrayList<>(poly.size() + 2);
            full.add(aOut); full.addAll(poly); full.add(bOut);

            int aAp  = Math.max(3, Aport.port().aperture());
            int bAp  = Math.max(3, Bport.port().aperture());
            int ap   = Math.max(aAp, bAp);

            pieces.addPiece(new CorridorPiece(
                    full, ap, cboxFor(full, ap),
                    aPort, bPort,
                    Aport.facing(), Bport.facing(),
                    aAp, bAp, critical
            ));

            markCorridorInMask(full, ap);

            roomDegree.merge(a, 1, Integer::sum);
            roomDegree.merge(b, 1, Integer::sum);

            LOGGER.info("[DWBuilder] Corridor emitted: nodes={} ap={} aAp={} bAp={} stage={}",
                    poly.size(), ap, aAp, bAp, usedStage);
            return true;

        } catch (Throwable t) {
            LOGGER.error("[DWBuilder] connectRooms exception", t);
            return false;
        }
    }

    /** 3D integer DDA/Bresenham from A to B (inclusiveStart controls whether we include A). */
    private static List<BlockPos> rasterLine3D(BlockPos a, BlockPos b, boolean includeStart) {
        int x0 = a.getX(), y0 = a.getY(), z0 = a.getZ();
        int x1 = b.getX(), y1 = b.getY(), z1 = b.getZ();

        int dx = Math.abs(x1 - x0), dy = Math.abs(y1 - y0), dz = Math.abs(z1 - z0);
        int xs = x1 >= x0 ? 1 : -1;
        int ys = y1 >= y0 ? 1 : -1;
        int zs = z1 >= z0 ? 1 : -1;

        ArrayList<BlockPos> pts = new ArrayList<>(1 + Math.max(dx, Math.max(dy, dz)));
        int x = x0, y = y0, z = z0;

        int p1, p2;
        if (dx >= dy && dx >= dz) {
            p1 = 2 * dy - dx;
            p2 = 2 * dz - dx;
            if (includeStart) pts.add(new BlockPos(x, y, z));
            for (int i = 0; i < dx; i++) {
                x += xs;
                if (p1 >= 0) { y += ys; p1 -= 2 * dx; }
                if (p2 >= 0) { z += zs; p2 -= 2 * dx; }
                p1 += 2 * dy;
                p2 += 2 * dz;
                pts.add(new BlockPos(x, y, z));
            }
        } else if (dy >= dx && dy >= dz) {
            p1 = 2 * dx - dy;
            p2 = 2 * dz - dy;
            if (includeStart) pts.add(new BlockPos(x, y, z));
            for (int i = 0; i < dy; i++) {
                y += ys;
                if (p1 >= 0) { x += xs; p1 -= 2 * dy; }
                if (p2 >= 0) { z += zs; p2 -= 2 * dy; }
                p1 += 2 * dx;
                p2 += 2 * dz;
                pts.add(new BlockPos(x, y, z));
            }
        } else {
            p1 = 2 * dy - dz;
            p2 = 2 * dx - dz;
            if (includeStart) pts.add(new BlockPos(x, y, z));
            for (int i = 0; i < dz; i++) {
                z += zs;
                if (p1 >= 0) { y += ys; p1 -= 2 * dz; }
                if (p2 >= 0) { x += xs; p2 -= 2 * dz; }
                p1 += 2 * dy;
                p2 += 2 * dx;
                pts.add(new BlockPos(x, y, z));
            }
        }
        return pts;
    }

    private record ScatterStats(int placed, int clash, int portal, int oob, int tries) {}

    private static BoundingBox cboxFor(List<BlockPos> poly, int pad) {
        int minX=Integer.MAX_VALUE,minY=Integer.MAX_VALUE,minZ=Integer.MAX_VALUE;
        int maxX=Integer.MIN_VALUE,maxY=Integer.MIN_VALUE,maxZ=Integer.MIN_VALUE;
        for (BlockPos p : poly) {
            if (p.getX()<minX) minX=p.getX(); if (p.getY()<minY) minY=p.getY(); if (p.getZ()<minZ) minZ=p.getZ();
            if (p.getX()>maxX) maxX=p.getX(); if (p.getY()>maxY) maxY=p.getY(); if (p.getZ()>maxZ) maxZ=p.getZ();
        }
        return new BoundingBox(minX-pad, minY-pad, minZ-pad, maxX+pad, maxY+pad, maxZ+pad);
    }

    // DungeonWorldBuilder#markCorridorInMask
    private void markCorridorInMask(List<BlockPos> poly, int ap) {
        // +1 voxel padding around the actual tunnel
        int rad = Math.max(1, (ap + 1) / 2) + 1;   // <-- was just ((ap+1)/2)
        for (BlockPos p : poly) {
            for (int dx = -rad; dx <= rad; dx++)
                for (int dy = -rad; dy <= rad; dy++)
                    for (int dz = -rad; dz <= rad; dz++) {
                        int gx = mCarve.gx(p.getX() + dx), gy = mCarve.gy(p.getY() + dy), gz = mCarve.gz(p.getZ() + dz);
                        mCarve.set(gx, gy, gz, true);
                    }
        }
        // keep the "no graph invalidation" comment as-is
    }

    private ArrayList<RoomPlacer.Placed> gatherBasicsWithRelax(
            AABB baseArea,
            int wantCount,
            List<RoomTemplateDef> basicDefs,
            List<RoomPlacer.Placed> alreadyPlaced,
            VoxelMask3D mRoom,
            java.util.Random jr
    ) {
        ArrayList<RoomPlacer.Placed> acc = new ArrayList<>();
        int passes = Math.max(1, this.pc.maxRelaxPasses);
        double scale = Math.max(1.0, (double)this.pc.relaxShellScale);

        int cRoom = this.pc.cRoom;
        int pad0 = this.pc.cPad;
        int rCorr = this.pc.rCorr;

        AABB area = baseArea;

        LOGGER.info("[DWBuilder] Relax gather basics: want={} passes={} relaxScale={} start[cRoom={},cPad={},rCorr={}] floors[minCRoom={},minCPad={},minRCorr={}]",
                wantCount, passes, scale, cRoom, pad0, rCorr, pc.minCRoom, pc.minCPad, pc.minRCorr);

        for (int pass = 0; pass < passes && acc.size() < wantCount; pass++) {
            // Widen the box on XZ each pass.
            if (pass > 0) {
                area = expandXZ(area, (scale - 1.0)); // e.g., 1.12 → +12% each pass
            }

            // Relax clearances, but never below floors.
            int cRoomUse = Math.max(pc.minCRoom, (int)Math.floor(cRoom * Math.pow(0.8, pass)));
            int cPadUse  = Math.max(pc.minCPad,  (int)Math.floor(pad0 * Math.pow(0.85, pass)));
            int rCorrUse = Math.max(pc.minRCorr, (int)Math.floor(rCorr * Math.pow(0.9, pass)));
            int rEff     = Math.max(1, rCorrUse + cPadUse);

            ScatterStats stats = new ScatterStats(0,0,0,0,0);
            ArrayList<RoomPlacer.Placed> got = scatterBasicsInsideRelaxed(
                    area, wantCount - acc.size(), basicDefs, alreadyPlaced, acc, mRoom, jr, cRoomUse, rEff, stats
            );

            LOGGER.info("[DWBuilder] Pass#{} area=({}, {}, {})→({}, {}, {})  placed={}  rejections[clash={},portal={},oob={}] tries={}" ,
                    pass,
                    area.minX, area.minY, area.minZ, area.maxX, area.maxY, area.maxZ,
                    got.size(), stats.clash(), stats.portal(), stats.oob(), stats.tries()
            );

            acc.addAll(got);
        }

        if (acc.size() < wantCount) {
            LOGGER.warn("[DWBuilder] Relax gather basics: shortfall={} (got {} / want {}) — continuing with fewer basics",
                    wantCount - acc.size(), acc.size(), wantCount);
        }
        return acc;
    }

    /**
     * A more flexible scatter that:
     *  - accepts per-pass cRoom clearance and effective routing radius,
     *  - counts rejection reasons,
     *  - tries multiple vertical layers rather than anchoring everything to ~endY.
     */
    private ArrayList<RoomPlacer.Placed> scatterBasicsInsideRelaxed(
            AABB area, int wantCount,
            List<RoomTemplateDef> basicDefs,
            List<RoomPlacer.Placed> already,
            List<RoomPlacer.Placed> alsoAgainst,   // treat rooms gathered in this call as existing for clash checks
            VoxelMask3D mRoom,
            java.util.Random jr,
            int cRoomUse,
            int effectiveRadiusForPortal,
            ScatterStats statsOut
    ) {
        ArrayList<RoomPlacer.Placed> out = new ArrayList<>();
        if (wantCount <= 0) return out;

        // Helper: piecewise-linear depth bias with peak at (endY + 20)
        // Weights:
        //   y >= entranceY        -> 1.0
        //   entranceY .. endY+20  -> lerp 1.0 .. 1.5 (as y decreases)
        //   endY .. endY+20       -> lerp 1.2 .. 1.5 (as y increases)
        //   y <= endY             -> 1.2
        final java.util.function.IntUnaryOperator biasAtY = (int y) -> {
            int entranceY = (this.rooms.size() > 0 ? this.rooms.get(0).origin().getY() : (int)Math.round(area.maxY));
            int endY      = (this.rooms.size() > 1 ? this.rooms.get(1).origin().getY() : (int)Math.round(area.minY));
            int endYp20   = endY + 20;

            if (y >= entranceY) {
                return Float.floatToIntBits(1.0f);
            }
            if (y >= endYp20 && y < entranceY) {
                float t = (entranceY == endYp20) ? 1.0f : (float)(entranceY - y) / (float)(entranceY - endYp20);
                float w = 1.0f + 0.5f * Math.min(1.0f, Math.max(0.0f, t)); // 1.0 -> 1.5
                return Float.floatToIntBits(w);
            }
            if (y >= endY && y < endYp20) {
                float t = (float)(y - endY) / 20.0f; // 0..1 across [endY .. endY+20)
                float w = 1.2f + 0.3f * Math.min(1.0f, Math.max(0.0f, t)); // 1.2 -> 1.5
                return Float.floatToIntBits(w);
            }
            // y < endY
            return Float.floatToIntBits(1.2f);
        };

        int attempts = Math.max(800, wantCount * 40); // dense packing is hard
        int clash = 0, portalFail = 0, oob = 0;

        for (int t = 0; t < attempts && out.size() < wantCount; t++) {
            var def = basicDefs.get(jr.nextInt(basicDefs.size()));
            var scan = TemplatePortScanner.scan(ServerHolder.get().getStructureManager(), def.id());
            if (scan == null) continue;

            Rotation rot = Rotation.values()[jr.nextInt(4)];
            Vec3i rs = Transforms.rotatedSize(scan.size(), rot);

            // choose a fuller 3D sample:
            int rx = (int)Math.round(area.minX + 1 + jr.nextDouble() * Math.max(1, area.getXsize() - rs.getX() - 2));
            int rz = (int)Math.round(area.minZ + 1 + jr.nextDouble() * Math.max(1, area.getZsize() - rs.getZ() - 2));

            // Y: span the box with bias peaking at (endY + 20)
            int minY = (int)Math.floor(area.minY + 1);
            int maxY = (int)Math.floor(area.maxY - rs.getY() - 1);
            if (maxY < minY) { oob++; continue; }

            int ry = jr.nextInt(minY, maxY + 1);

            // Bias acceptance (normalize by max weight = 1.5)
            float w = Float.intBitsToFloat(biasAtY.applyAsInt(ry));
            float acceptP = w / 1.5f; // ∈ (≈0.67 .. 1.0]
            if (jr.nextFloat() > acceptP) {
                // reject this vertical sample; try another attempt
                continue;
            }

            BlockPos origin = new BlockPos(rx, ry, rz);
            AABB box = new AABB(origin.getX(), origin.getY(), origin.getZ(),
                    origin.getX()+rs.getX(), origin.getY()+rs.getY(), origin.getZ()+rs.getZ());
            var placed = new RoomPlacer.Placed(scan, def, rot, origin, box);

            // mask bounds test (so later routing grids are valid)
            if (!maskBoundsOk(mRoom, placed)) { oob++; continue; }

            // clearance vs already & out (using cRoomUse for this pass)
            AABB infl = box.inflate(cRoomUse);
            boolean clashHit = false;
            for (var p : already) if (infl.intersects(p.aabb().inflate(cRoomUse))) { clashHit = true; break; }
            if (!clashHit) for (var p : alsoAgainst) if (infl.intersects(p.aabb().inflate(cRoomUse))) { clashHit = true; break; }
            if (!clashHit) for (var p : out) if (infl.intersects(p.aabb().inflate(cRoomUse))) { clashHit = true; break; }
            if (clashHit) { clash++; continue; }

            // portal viability using per-pass effective radius (rCorr + cPad we computed)
            if (!portalsViableLikeRoomPlacer(placed, mRoom, effectiveRadiusForPortal)) {
                portalFail++; continue;
            }

            out.add(placed);
        }

        // write stats (immutable record; caller logs its own counters)
        try {
            java.lang.reflect.Field fPlaced = ScatterStats.class.getDeclaredField("placed");
        } catch (Throwable ignored) {}
        statsOut = new ScatterStats(out.size(), clash, portalFail, oob, Math.max(attempts, 0));

        LOGGER.info("[DWBuilder] ScatterBasics(pass) want={} got={} rejections[clash={},portal={},oob={}] attempts={}",
                wantCount, out.size(), clash, portalFail, oob, attempts);

        return out;
    }


    // Helper: bounds check like RoomPlacer.withinMaskBounds but exposed here for logging
    private static boolean maskBoundsOk(VoxelMask3D mask, RoomPlacer.Placed p) {
        Vec3i sz = Transforms.rotatedSize(p.scan().size(), p.rot());
        int x0 = mask.gx(p.origin().getX());
        int y0 = mask.gy(p.origin().getY());
        int z0 = mask.gz(p.origin().getZ());
        int x1 = x0 + sz.getX() - 1;
        int y1 = y0 + sz.getY() - 1;
        int z1 = z0 + sz.getZ() - 1;
        return mask.in(x0,y0,z0) && mask.in(x1,y1,z1);
    }

    // Portal viability that accepts a custom effective radius (so each relax pass can shrink padding)
    private static boolean portalsViableLikeRoomPlacer(RoomPlacer.Placed p, VoxelMask3D mRoom, int effectiveRadius) {
        VoxelMask3D mTmp = mRoom.copy();
        writeRoomToMask(mTmp, p);
        VoxelMask3D mFreePrime = Morph3D.freeMaskFromRooms(mTmp, effectiveRadius);
        int minGood = 1;
        int stepOut = effectiveRadius + 1;

        List<Port> ports = p.scan().exits().isEmpty() ? p.scan().entrances() : p.scan().exits();
        if (ports.isEmpty()) ports = p.scan().entrances();

        for (Port port : ports) {
            BlockPos wp = Transforms.worldOfLocalMin(port.localPos(), p.origin(), p.scan().size(), p.rot());
            net.minecraft.core.Direction face = Transforms.rotateFacingYaw(port.facing(), p.rot());
            BlockPos outward = wp.relative(face, Math.max(1, stepOut));
            int gx = mFreePrime.gx(outward.getX());
            int gy = mFreePrime.gy(outward.getY());
            int gz = mFreePrime.gz(outward.getZ());
            if (mFreePrime.in(gx, gy, gz) && mFreePrime.get(gx, gy, gz)) return true;
        }
        return false;
    }

    static List<RoomPlacer.Placed> concatNodes(RoomPlacer.Placed entrance, List<RoomPlacer.Placed> mid, RoomPlacer.Placed queen) {
        ArrayList<RoomPlacer.Placed> list = new ArrayList<>();
        list.add(entrance); list.addAll(mid); list.add(queen);
        return list;
    }
}