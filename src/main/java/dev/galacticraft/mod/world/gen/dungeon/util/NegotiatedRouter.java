package dev.galacticraft.mod.world.gen.dungeon.util;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

import java.util.*;

/**
 * Multi-corridor negotiated router (rip-up & reroute).
 */
public final class NegotiatedRouter {

    private static final Logger LOGGER = LogUtils.getLogger();

    public record Net(BlockPos a, Direction aFacing,
                      BlockPos b, Direction bFacing,
                      int preflight) {}

    public record Result(List<List<BlockPos>> perNetPaths, Bitmask unionMask, int iterations, int unresolvedOveruse) {}

    // ---- public entry point --------------------------------------------------

    public static Result routeAll(
            List<Net> nets,
            Bitmask staticMask,
            int radius,
            int minY, int maxY
    ) {
        // Tunables
        final int MAX_ITERS      = 10;
        final double PRESENT_START = 3.0;
        final double PRESENT_GROW  = 1.35;
        final int HISTORY_ADD      = 2;
        final int TUBE_R1 = 28;
        final int TUBE_R2 = 40;
        final double W     = 2.8;
        final int MARGIN   = 4;
        final int LOOK_R   = radius + MARGIN;

        Objects.requireNonNull(nets, "nets");
        Objects.requireNonNull(staticMask, "staticMask");
        if (nets.isEmpty()) return new Result(Collections.emptyList(), new Bitmask(), 0, 0);

        List<List<BlockPos>> paths = new ArrayList<>(nets.size());
        for (int i = 0; i < nets.size(); i++) paths.add(Collections.emptyList());

        // Raw volumetric use and history
        Long2IntOpenHashMap presentUse    = new Long2IntOpenHashMap();
        Long2IntOpenHashMap history       = new Long2IntOpenHashMap();
        // Dilated max views for O(1) lookups in A*
        Long2IntOpenHashMap dilatedPresent = new Long2IntOpenHashMap();
        Long2IntOpenHashMap dilatedHistory = new Long2IntOpenHashMap();

        // FastUtil micro-opts
        presentUse.defaultReturnValue(0);
        history.defaultReturnValue(0);
        dilatedPresent.defaultReturnValue(0);
        dilatedHistory.defaultReturnValue(0);

        double presentK = PRESENT_START;
        Clearance clearance = new Clearance(staticMask, radius, minY, maxY);

        // ---------- initial plan ----------
        for (int i = 0; i < nets.size(); i++) {
            Net net = nets.get(i);
            List<BlockPos> seedsA = buildLaunchBundle(net.a, net.aFacing, net.preflight, clearance);
            List<BlockPos> seedsB = buildLaunchBundle(net.b, net.bFacing, net.preflight, clearance);

            List<BlockPos> path = aStarTube(clearance, seedsA, seedsB, net.a, net.b, TUBE_R1, W, 800);
            if (path.isEmpty()) path = aStarTube(clearance, seedsA, seedsB, net.a, net.b, TUBE_R2, W, 1200);
            paths.set(i, path);

            // Count volume once per net and incrementally update dilated present
            LongOpenHashSet vox = collectVoxelsForNet(net, path, radius, minY, maxY);
            applyVoxelSetIncrement(vox, presentUse, 1);
            injectIntoDilatedMax(vox, presentUse, dilatedPresent, LOOK_R);
        }

        int iter = 0;
        int overuse;
        do {
            overuse = totalOveruse(presentUse);
            if (overuse == 0) break;

            // History accumulates only where there was overuse; rebuild its dilated view once/iter
            addHistory(history, presentUse, HISTORY_ADD);
            dilateMaxInPlace(history, LOOK_R, dilatedHistory);

            List<Integer> order = hardestFirstOrder(nets);

            // Re-route all nets this iteration; rebuild present and its dilated copy on the fly
            presentUse.clear();
            dilatedPresent.clear();

            for (int idx : order) {
                Net net = nets.get(idx);
                List<BlockPos> seedsA = buildLaunchBundle(net.a, net.aFacing, net.preflight, clearance);
                List<BlockPos> seedsB = buildLaunchBundle(net.b, net.bFacing, net.preflight, clearance);

                List<BlockPos> path = aStarTubeWithCosts(
                        clearance, seedsA, seedsB, net.a, net.b,
                        TUBE_R2, W, 1200,
                        dilatedPresent, dilatedHistory, presentK
                );
                if (path.isEmpty()) path = aStarTube(clearance, seedsA, seedsB, net.a, net.b, TUBE_R2, W, 1600);

                paths.set(idx, path);

                // Incremental present + dilated-present updates used by subsequent nets
                LongOpenHashSet vox = collectVoxelsForNet(net, path, radius, minY, maxY);
                applyVoxelSetIncrement(vox, presentUse, 1);
                injectIntoDilatedMax(vox, presentUse, dilatedPresent, LOOK_R);
            }

            presentK *= PRESENT_GROW;
            iter++;
        } while (iter < MAX_ITERS);

        // Final union (includes preflights)
        Bitmask union = new Bitmask();
        for (int i = 0; i < nets.size(); i++) {
            Net net = nets.get(i);
            List<BlockPos> p = paths.get(i);
            rasterizePreflightAndPathToBitmask(net, p, radius, minY, maxY, union);
        }

        return new Result(paths, union, iter, overuse);
    }

    // ---- clearance & helpers -------------------------------------------------

    private static void rasterizePreflightAndPathToBitmask(
            Net net,
            List<BlockPos> path,
            int r, int minY, int maxY,
            Bitmask out
    ) {
        if (path == null || path.isEmpty()) {
            // no seeds connected; just rasterize straight A->B as a fallback
            LOGGER.error("PATH FAILED");
            //rasterizeLineToBitmask(net.a, net.b, r, minY, maxY, out);
            return;
        }
        // A -> first seed
        rasterizeLineToBitmask(net.a, path.get(0), r, minY, maxY, out);
        // seed path itself
        rasterizePathToBitmask(path, r, minY, maxY, out);
        // last seed -> B
        rasterizeLineToBitmask(path.get(path.size() - 1), net.b, r, minY, maxY, out);
    }

    private static void rasterizeLineToBitmask(BlockPos a, BlockPos b, int r, int minY, int maxY, Bitmask out) {
        int x = a.getX(), y = a.getY(), z = a.getZ();
        int dx = b.getX() - x, dy = b.getY() - y, dz = b.getZ() - z;

        int ax = Math.abs(dx), ay = Math.abs(dy), az = Math.abs(dz);
        int sx = Integer.signum(dx), sy = Integer.signum(dy), sz = Integer.signum(dz);

        int m = Math.max(ax, Math.max(ay, az));
        int ex = m >> 1, ey = m >> 1, ez = m >> 1;

        for (int i = 0; i <= m; i++) {
            // fill cube around (x,y,z)
            for (int oy = -r; oy <= r; oy++) {
                int yy = y + oy; if (yy < minY || yy > maxY) continue;
                for (int ox = -r; ox <= r; ox++) {
                    int xx = x + ox;
                    for (int oz = -r; oz <= r; oz++) {
                        int zz = z + oz;
                        out.add(BlockPos.asLong(xx, yy, zz));
                    }
                }
            }
            ex -= ax; if (ex < 0) { ex += m; x += sx; }
            ey -= ay; if (ey < 0) { ey += m; y += sy; }
            ez -= az; if (ez < 0) { ez += m; z += sz; }
        }
    }

    /** Cheap adapter around your existing logic (cubeClearAt). */
    private static final class Clearance {
        final Bitmask avoid;
        final int r, minY, maxY;
        Clearance(Bitmask avoid, int r, int minY, int maxY) {
            this.avoid = avoid; this.r = r; this.minY = minY; this.maxY = maxY;
        }
        boolean centerClear(int x, int y, int z) {
            if (y - r < minY || y + r > maxY) return false;
            for (int dy = -r; dy <= r; dy++) {
                int yy = y + dy;
                for (int dx = -r; dx <= r; dx++) {
                    int xx = x + dx;
                    for (int dz = -r; dz <= r; dz++) {
                        int zz = z + dz;
                        if (avoid.contains(BlockPos.asLong(xx, yy, zz))) return false;
                    }
                }
            }
            return true;
        }
        boolean centerClear(BlockPos p) { return centerClear(p.getX(), p.getY(), p.getZ()); }
    }

    private static List<Integer> hardestFirstOrder(List<Net> nets) {
        final int n = nets.size();
        int[] idx = new int[n];
        for (int i = 0; i < n; i++) idx[i] = i;

        it.unimi.dsi.fastutil.ints.IntArrays.quickSort(
                idx, 0, n,
                (i, j) -> {
                    Net a = nets.get(i), b = nets.get(j);
                    int da = manhattan(a.a, a.b), db = manhattan(b.a, b.b);
                    return Integer.compare(db, da); // descending by distance
                }
        );

        // keep the public API as List<Integer> without boxing at sort-time
        List<Integer> out = new ArrayList<>(n);
        for (int v : idx) out.add(v);
        return out;
    }

    private static int manhattan(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }

    private static int totalOveruse(Long2IntOpenHashMap present) {
        int sum = 0;
        for (Long2IntMap.Entry e : ((Long2IntMap.FastEntrySet) present.long2IntEntrySet())) {
            int use = e.getIntValue();
            if (use > 1) sum += (use - 1);
        }
        return sum;
    }

    private static void addHistory(Long2IntOpenHashMap history, Long2IntOpenHashMap present, int add) {
        for (Long2IntMap.Entry e : ((Long2IntMap.FastEntrySet) present.long2IntEntrySet())) {
            if (e.getIntValue() > 1) history.addTo(e.getLongKey(), add);
        }
    }

    // small launch bundle that mirrors your single-corridor seeds (center + ±1 laterals)
    private static List<BlockPos> buildLaunchBundle(BlockPos origin, Direction dir, int desiredLen, Clearance c) {
        int r = c.r;
        int minLen = Math.max(2, r + 1);
        int maxLen = Math.max(minLen, desiredLen + r + 2);

        ArrayList<BlockPos> seeds = new ArrayList<>(12);
        Direction[] laterals = lateral4(dir);

        for (int len = maxLen; len >= minLen; len--) {
            BlockPos base = origin.relative(dir, len);
            base = new BlockPos(base.getX(), Mth.clamp(base.getY(), c.minY, c.maxY), base.getZ());

            if (oneSidedClear(base, dir, c)) {
                BlockPos pushed = pushOutUntilClear(base, dir, c, 48);
                if (c.centerClear(pushed)) seeds.add(pushed);
            }
            for (Direction lat : laterals) {
                BlockPos s = base.relative(lat, 1);
                if (oneSidedClear(s, dir, c)) {
                    BlockPos pushed = pushOutUntilClear(s, dir, c, 48);
                    if (c.centerClear(pushed)) seeds.add(pushed);
                }
            }
            if (!seeds.isEmpty()) break;
        }
        if (seeds.isEmpty()) seeds.add(origin); // fallback
        return seeds;
    }

    private static Direction[] lateral4(Direction forward) {
        return switch (forward.getAxis()) {
            case X -> new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN};
            case Y -> new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
            case Z -> new Direction[]{Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN};
        };
    }

    private static boolean oneSidedClear(BlockPos p, Direction forward, Clearance c) {
        int r = c.r;
        int x = p.getX(), y = p.getY(), z = p.getZ();
        if (y - r < c.minY || y + r > c.maxY) return false;
        int fx = forward.getStepX(), fy = forward.getStepY(), fz = forward.getStepZ();
        for (int dy = -r; dy <= r; dy++) {
            int yy = y + dy;
            for (int dx = -r; dx <= r; dx++) {
                int xx = x + dx;
                for (int dz = -r; dz <= r; dz++) {
                    int zz = z + dz;
                    int dot = dx * fx + dy * fy + dz * fz;
                    if (dot < 0) continue;
                    if (c.avoid.contains(BlockPos.asLong(xx, yy, zz))) return false;
                }
            }
        }
        return true;
    }

    private static BlockPos pushOutUntilClear(BlockPos start, Direction fwd, Clearance c, int maxSteps) {
        BlockPos cur = start;
        for (int i = 0; i <= maxSteps; i++) {
            if (c.centerClear(cur)) return cur;
            cur = cur.relative(fwd, 1);
            if (cur.getY() < c.minY || cur.getY() > c.maxY) break;
        }
        return start;
    }

    // ---- A* cores ------------------------------------------------------------

    private static final int[][] DIRS26 = {
            { 1, 0, 0},{-1, 0, 0},{ 0, 1, 0},{ 0,-1, 0},{ 0, 0, 1},{ 0, 0,-1},
            { 1, 1, 0},{ 1,-1, 0},{-1, 1, 0},{-1,-1, 0},
            { 1, 0, 1},{ 1, 0,-1},{-1, 0, 1},{-1, 0,-1},
            { 0, 1, 1},{ 0, 1,-1},{ 0,-1, 1},{ 0,-1,-1},
            { 1, 1, 1},{ 1, 1,-1},{ 1,-1, 1},{ 1,-1,-1},
            {-1, 1, 1},{-1, 1,-1},{-1,-1, 1},{-1,-1,-1}
    };

    private static final class Node { final long pos; final int g; final double f;
        Node(long p,int g,double f){this.pos=p;this.g=g;this.f=f;} }

    private static final class Tube {
        final int ax, ay, az, bx, by, bz; final double abLen2;
        Tube(BlockPos a, BlockPos b) {
            this.ax=a.getX(); this.ay=a.getY(); this.az=a.getZ();
            this.bx=b.getX(); this.by=b.getY(); this.bz=b.getZ();
            long dx=(long)bx-ax, dy=(long)by-ay, dz=(long)bz-az;
            this.abLen2 = (double)dx*dx + (double)dy*dy + (double)dz*dz;
        }
        double dist2ToSeg(int px,int py,int pz){
            long apx=(long)px-ax, apy=(long)py-ay, apz=(long)pz-az;
            long abx=(long)bx-ax, aby=(long)by-ay, abz=(long)bz-az;
            double t = abLen2 <= 1e-9 ? 0.0 : (apx*abx + apy*aby + apz*abz) / abLen2;
            if (t<0) t=0; else if (t>1) t=1;
            double cx = ax + t*abx, cy = ay + t*aby, cz = az + t*abz;
            double dx = px - cx, dy = py - cy, dz = pz - cz;
            return dx*dx + dy*dy + dz*dz;
        }
    }

    private static int manhattan(int x,int y,int z,int gx,int gy,int gz) {
        return Math.abs(x-gx) + Math.abs(y-gy) + Math.abs(z-gz);
    }

    /** Plain tube A* without congestion costs (used for first plan / fallback). */
    private static List<BlockPos> aStarTube(Clearance c,
                                            List<BlockPos> seedsA, List<BlockPos> seedsB,
                                            BlockPos A, BlockPos B, int tubeR, double W, int budgetScale) {

        Tube tube = new Tube(A, B);
        double tubeR2 = (double) tubeR * tubeR;
        int gx = B.getX(), gy = B.getY(), gz = B.getZ();
        int baseD = manhattan(A, B);
        final int BUDGET = Math.min(1_000_000, Math.max(20_000, baseD * budgetScale));

        LongOpenHashSet targets = new LongOpenHashSet(seedsB.size() * 8);
        for (BlockPos t : seedsB) {
            if (c.centerClear(t) && tube.dist2ToSeg(t.getX(), t.getY(), t.getZ()) <= tubeR2) targets.add(t.asLong());
            for (int[] d : DIRS26) {
                int nx = t.getX() + d[0], ny = t.getY() + d[1], nz = t.getZ() + d[2];
                if (ny < c.minY || ny > c.maxY) continue;
                if (tube.dist2ToSeg(nx, ny, nz) > tubeR2) continue;
                if (c.centerClear(nx, ny, nz)) targets.add(BlockPos.asLong(nx, ny, nz));
            }
        }
        if (targets.isEmpty()) return Collections.emptyList();

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        LongOpenHashSet closed = new LongOpenHashSet(4096);
        Long2LongOpenHashMap parent = new Long2LongOpenHashMap(4096);
        Long2IntOpenHashMap gScore = new Long2IntOpenHashMap(4096);
        gScore.defaultReturnValue(Integer.MAX_VALUE);

        for (BlockPos s : seedsA) {
            int x = s.getX(), y = s.getY(), z = s.getZ();
            if (!c.centerClear(x, y, z)) continue;
            if (tube.dist2ToSeg(x, y, z) > tubeR2) continue;
            long key = s.asLong();
            int h = manhattan(x, y, z, gx, gy, gz);
            open.add(new Node(key, 0, W * h));
            gScore.put(key, 0);
        }
        if (open.isEmpty()) return Collections.emptyList();

        long found = 0L;
        int expanded = 0;

        while (!open.isEmpty() && expanded < BUDGET) {
            Node cur = open.poll();
            if (closed.contains(cur.pos)) continue;
            if (targets.contains(cur.pos)) { found = cur.pos; break; }
            closed.add(cur.pos);
            expanded++;

            int cx = BlockPos.getX(cur.pos), cy = BlockPos.getY(cur.pos), cz = BlockPos.getZ(cur.pos);
            int g = gScore.get(cur.pos);

            for (int[] d : DIRS26) {
                int nx = cx + d[0], ny = cy + d[1], nz = cz + d[2];
                if (ny < c.minY || ny > c.maxY) continue;
                if (tube.dist2ToSeg(nx, ny, nz) > tubeR2) continue;
                if (!c.centerClear(nx, ny, nz)) continue;
                long np = BlockPos.asLong(nx, ny, nz);
                if (closed.contains(np)) continue;

                int ng = g + 1;
                if (ng >= gScore.get(np)) continue;

                gScore.put(np, ng);
                parent.put(np, cur.pos);
                int h = manhattan(nx, ny, nz, gx, gy, gz);
                open.add(new Node(np, ng, ng + W * h));
            }
        }

        if (found == 0L) return Collections.emptyList();
        return reconstruct(parent, found);
    }

    /** Congestion-aware tube A*. Costs: 1 + presentK*useMax + histMax, with O(1) lookups. */
    private static List<BlockPos> aStarTubeWithCosts(Clearance c,
                                                     List<BlockPos> seedsA, List<BlockPos> seedsB,
                                                     BlockPos A, BlockPos B, int tubeR,
                                                     double W, int budgetScale,
                                                     Long2IntOpenHashMap dilatedPresent,
                                                     Long2IntOpenHashMap dilatedHistory,
                                                     double presentK) {

        Tube tube = new Tube(A, B);
        double tubeR2 = (double)tubeR * tubeR;
        int gx=B.getX(), gy=B.getY(), gz=B.getZ();
        int baseD = manhattan(A, B);
        final int BUDGET = Math.min(1_000_000, Math.max(20_000, baseD * budgetScale));

        LongOpenHashSet targets = new LongOpenHashSet(seedsB.size() * 8);
        for (BlockPos t : seedsB) {
            if (c.centerClear(t) && tube.dist2ToSeg(t.getX(),t.getY(),t.getZ()) <= tubeR2) targets.add(t.asLong());
            for (int[] d: DIRS26) {
                int nx=t.getX()+d[0], ny=t.getY()+d[1], nz=t.getZ()+d[2];
                if (ny<c.minY || ny>c.maxY) continue;
                if (tube.dist2ToSeg(nx,ny,nz) > tubeR2) continue;
                if (c.centerClear(nx,ny,nz)) targets.add(BlockPos.asLong(nx,ny,nz));
            }
        }
        if (targets.isEmpty()) return Collections.emptyList();

        final Comparator<Node> CMP = Comparator.comparingDouble(n -> n.f);
        PriorityQueue<Node> open = new PriorityQueue<>(CMP);
        LongOpenHashSet closed = new LongOpenHashSet(4096);
        Long2LongOpenHashMap parent = new Long2LongOpenHashMap(4096);
        Long2IntOpenHashMap gScore = new Long2IntOpenHashMap(4096);
        gScore.defaultReturnValue(Integer.MAX_VALUE);

        for (BlockPos s : seedsA) {
            int x=s.getX(), y=s.getY(), z=s.getZ();
            if (!c.centerClear(x,y,z)) continue;
            if (tube.dist2ToSeg(x,y,z) > tubeR2) continue;
            long key = s.asLong();
            int h = manhattan(x,y,z, gx,gy,gz);
            open.add(new Node(key, 0, W*h));
            gScore.put(key, 0);
        }
        if (open.isEmpty()) return Collections.emptyList();

        long found = 0L;
        int expanded = 0;

        while (!open.isEmpty() && expanded < BUDGET) {
            Node cur = open.poll();
            if (closed.contains(cur.pos)) continue;
            if (targets.contains(cur.pos)) { found = cur.pos; break; }
            closed.add(cur.pos);
            expanded++;

            int cx=BlockPos.getX(cur.pos), cy=BlockPos.getY(cur.pos), cz=BlockPos.getZ(cur.pos);
            int g = gScore.get(cur.pos);

            for (int[] d: DIRS26) {
                int nx=cx+d[0], ny=cy+d[1], nz=cz+d[2];
                if (ny<c.minY || ny>c.maxY) continue;
                if (tube.dist2ToSeg(nx,ny,nz) > tubeR2) continue;
                if (!c.centerClear(nx,ny,nz)) continue;
                long np = BlockPos.asLong(nx,ny,nz);
                if (closed.contains(np)) continue;

                // O(1) congestion & history via dilated maps
                int useMax  = dilatedPresent.getOrDefault(np, 0);
                int histMax = dilatedHistory.getOrDefault(np, 0);
                double stepCost = 1.0 + (useMax > 0 ? presentK * useMax : 0.0) + histMax;

                int ng = g + (int)Math.ceil(stepCost);
                int old = gScore.getOrDefault(np, Integer.MAX_VALUE);
                if (ng >= old) continue;

                gScore.put(np, ng);
                parent.put(np, cur.pos);
                int h = manhattan(nx,ny,nz, gx,gy,gz);
                double f = ng + W*h;
                open.add(new Node(np, ng, f));
            }
        }

        if (found == 0L) return Collections.emptyList();
        return reconstruct(parent, found);
    }

    // Collect unique voxels for this net (preflights + path), once.
    private static LongOpenHashSet collectVoxelsForNet(Net net,
                                                       List<BlockPos> path,
                                                       int r, int minY, int maxY) {
        LongOpenHashSet vox = new LongOpenHashSet();
        if (path == null || path.isEmpty()) {
            rasterizeLineToSet(net.a, net.b, r, minY, maxY, vox);
            return vox;
        }
        rasterizeLineToSet(net.a, path.get(0), r, minY, maxY, vox);
        rasterizePathToSet(path, r, minY, maxY, vox);
        rasterizeLineToSet(path.get(path.size() - 1), net.b, r, minY, maxY, vox);
        return vox;
    }

    // Add +delta to present/history for every voxel in 'vox'
    private static void applyVoxelSetIncrement(LongOpenHashSet vox, Long2IntOpenHashMap counts, int delta) {
        LongIterator it = vox.iterator();
        while (it.hasNext()) {
            long k = it.nextLong();
            counts.addTo(k, delta);
        }
    }

    // Incrementally inject the neighborhood max into target dilated map
    private static void injectIntoDilatedMax(LongOpenHashSet newVoxels,
                                             Long2IntOpenHashMap sourceRaw,
                                             Long2IntOpenHashMap targetDilated,
                                             int r) {
        LongIterator it = newVoxels.iterator();
        while (it.hasNext()) {
            long k = it.nextLong();
            int cx = BlockPos.getX(k), cy = BlockPos.getY(k), cz = BlockPos.getZ(k);
            int val = sourceRaw.get(k);
            // Push 'val' to all neighbors; keep max
            for (int dy=-r; dy<=r; dy++) {
                for (int dx=-r; dx<=r; dx++) {
                    for (int dz=-r; dz<=r; dz++) {
                        long nk = BlockPos.asLong(cx+dx, cy+dy, cz+dz);
                        int old = targetDilated.getOrDefault(nk, 0);
                        if (val > old) targetDilated.put(nk, val);
                    }
                }
            }
        }
    }

    // Full (re)build of a dilated “max” field (use once per iteration for history)
    private static void dilateMaxInPlace(Long2IntOpenHashMap sourceRaw,
                                         int r,
                                         Long2IntOpenHashMap outDilated) {
        outDilated.clear();
        LongIterator it = sourceRaw.keySet().iterator();
        while (it.hasNext()) {
            long k = it.nextLong();
            int cx = BlockPos.getX(k), cy = BlockPos.getY(k), cz = BlockPos.getZ(k);
            int val = sourceRaw.get(k);
            for (int dy=-r; dy<=r; dy++) {
                for (int dx=-r; dx<=r; dx++) {
                    for (int dz=-r; dz<=r; dz++) {
                        long nk = BlockPos.asLong(cx+dx, cy+dy, cz+dz);
                        int old = outDilated.getOrDefault(nk, 0);
                        if (val > old) outDilated.put(nk, val);
                    }
                }
            }
        }
    }

    // SET rasterizers to build unique-voxel sets (no double work)
    private static void rasterizePathToSet(List<BlockPos> path, int r, int minY, int maxY, LongOpenHashSet out) {
        if (path == null || path.isEmpty()) return;
        for (BlockPos p : path) {
            int px = p.getX(), py = p.getY(), pz = p.getZ();
            for (int dy=-r; dy<=r; dy++) {
                int yy = py + dy; if (yy < minY || yy > maxY) continue;
                for (int dx=-r; dx<=r; dx++) {
                    int xx = px + dx;
                    for (int dz=-r; dz<=r; dz++) {
                        int zz = pz + dz;
                        out.add(BlockPos.asLong(xx, yy, zz));
                    }
                }
            }
        }
    }

    private static void rasterizeLineToSet(BlockPos a, BlockPos b, int r, int minY, int maxY, LongOpenHashSet out) {
        int x = a.getX(), y = a.getY(), z = a.getZ();
        int dx = b.getX() - x, dy = b.getY() - y, dz = b.getZ() - z;

        int ax = Math.abs(dx), ay = Math.abs(dy), az = Math.abs(dz);
        int sx = Integer.signum(dx), sy = Integer.signum(dy), sz = Integer.signum(dz);

        int m = Math.max(ax, Math.max(ay, az));
        int ex = m >> 1, ey = m >> 1, ez = m >> 1;

        for (int i = 0; i <= m; i++) {
            for (int oy = -r; oy <= r; oy++) {
                int yy = y + oy; if (yy < minY || yy > maxY) continue;
                for (int ox = -r; ox <= r; ox++) {
                    int xx = x + ox;
                    for (int oz = -r; oz <= r; oz++) {
                        int zz = z + oz;
                        out.add(BlockPos.asLong(xx, yy, zz));
                    }
                }
            }
            ex -= ax; if (ex < 0) { ex += m; x += sx; }
            ey -= ay; if (ey < 0) { ey += m; y += sy; }
            ez -= az; if (ez < 0) { ez += m; z += sz; }
        }
    }

    private static ArrayList<BlockPos> reconstruct(Long2LongOpenHashMap parent, long found) {
        ArrayList<BlockPos> rev = new ArrayList<>();
        long cur = found; rev.add(BlockPos.of(cur));
        while (parent.containsKey(cur)) { cur = parent.get(cur); rev.add(BlockPos.of(cur)); }
        Collections.reverse(rev);
        return rev;
    }

    // ---- rasterization -------------------------------------------------------

    /** Add (2r+1)^3 voxels around each path center to 'out'. */
    public static void rasterizePathToBitmask(List<BlockPos> path, int r, int minY, int maxY, Bitmask out) {
        if (path == null || path.isEmpty()) return;
        for (BlockPos p : path) {
            int px = p.getX(), py = p.getY(), pz = p.getZ();
            for (int dy=-r; dy<=r; dy++) {
                int yy = py + dy; if (yy < minY || yy > maxY) continue;
                for (int dx=-r; dx<=r; dx++) {
                    int xx = px + dx;
                    for (int dz=-r; dz<=r; dz++) {
                        int zz = pz + dz;
                        out.add(BlockPos.asLong(xx, yy, zz));
                    }
                }
            }
        }
    }
}