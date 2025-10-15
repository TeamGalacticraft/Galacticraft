package dev.galacticraft.mod.world.gen.dungeon.util;

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.gen.dungeon.records.BlockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

import java.util.*;

/**
 * Corridor carver/finisher that works entirely in-memory (no Level).
 *
 * Pipeline:
 *   0) seed air using centerline mask
 *   1) Perlin push/pull shell by ±1 (roof/walls/floor), with anti-choke & room respect
 *   1.5) random 1-deep sink holes (1–3 tiles)
 *   2) full coating pass on ALL exposed surfaces touching corridor air
 *   2.5) acid pockets (1–5 tiles) with relaxed sealing rules + slimy rim
 *   3) webs across LOCAL corridor normal (membranes/diagonals/singletons)
 *
 * Protection:
 *   - protectedMask: exact cells that must not modify (rooms)
 *   - doorwayWhitelist: cells allowed even if protected (door apertures)
 */
public final class MaskCarvePiece {
    // ===== materials =====
    private static final Block OLIANT_NEST_BLOCK           = GCBlocks.OLIANT_NEST_BLOCK;
    private static final Block OLIANT_FERTILE_NEST_BLOCK   = GCBlocks.OLIANT_FERTILE_NEST_BLOCK;
    private static final Block OLIANT_DISSOLVED_NEST_BLOCK = GCBlocks.OLIANT_DISSOLVED_NEST_BLOCK;
    private static final Block OLIANT_ACID_BLOCK           = GCBlocks.OLIANT_ACID;
    private static final Block OLIANT_WEB                  = GCBlocks.OLIANT_WEB;

    private static final BlockState DEFAULT_SOLID          = Blocks.STONE.defaultBlockState();

    // ===== inputs =====
    private final List<Long> voxels;   // mask centerline (order matters to estimate local tangent)
    private final BoundingBox box;

    // ===== constants / tuning =====
    private static final int STRAIGHT_SPAN_LIMIT = 12; // for cross-section probes

    // push/pull
    private static final double NOISE_SCALE     = 1.0 / 6.0;
    private static final double PUSH_THRESH     = 0.35;   // grow outwards if > thresh
    private static final double PULL_THRESH     = -0.35;  // nibble inwards if < thresh

    // coatings
    private static final float P_FLOOR_FERTILE  = 0.18f;
    private static final float P_FLOOR_DISSOLVE = 0.12f;
    private static final float P_WALL_FERTILE   = 0.12f;
    private static final float P_ROOF_FERTILE   = 0.10f;

    // sink holes
    private static final float SINK_ATTEMPT_P   = 0.045f;
    private static final int   SINK_STRIDE_MIN  = 4;
    private static final int   SINK_STRIDE_VAR  = 4;      // 4..7
    private static final int   SINK_DEPTH       = 1;      // exactly 1 deep
    private static final int   SINK_PATCH_MAX   = 3;      // 1..3 tiles

    // acid pockets
    private static final int   ACID_MAX_SIZE    = 6;
    private static final int   ACID_MAX_OPEN    = 0;      //1 makes leaks but relaxes a little.

    // webs
    private static final float WEB_SINGLE_P     = 0.020f;
    private static final float WEB_MEMBRANE_P   = 0.010f;
    private static final float WEB_DIAGONAL_P   = 0.015f;
    private static final float WEB_CORNER_P     = 0.025f;
    private static final int   WEB_STRIDE_MIN   = 3;
    private static final int   WEB_STRIDE_VAR   = 3;      // 3..5

    public MaskCarvePiece(Bitmask mask) {
        this.voxels = toList(mask);            // keep order
        this.box    = computeBoundingBox(mask);
    }

    public BoundingBox getBoundingBox() { return box; }

    // ----------------------------- public API --------------------------------

    public HashMap<SectionPos, List<BlockData>> getBlocks(RandomSource random, int minY, int maxY) {
        return getBlocks(random, minY, maxY, List.of(), null, Set.of());
    }

    public HashMap<SectionPos, List<BlockData>> getBlocks(RandomSource random, int minY, int maxY,
                                                          Bitmask protectedMask, Set<Long> doorwayWhitelist) {
        return getBlocks(random, minY, maxY, List.of(), protectedMask, doorwayWhitelist);
    }

    public HashMap<SectionPos, List<BlockData>> getBlocks(RandomSource rnd, int minY, int maxY,
                                                          Collection<BoundingBox> protectedRoomsAabbs,
                                                          Bitmask protectedMask,
                                                          Set<Long> doorwayWhitelist) {
        var out = new HashMap<SectionPos, List<BlockData>>();
        if (voxels.isEmpty()) return out;

        // Virtual world state
        final Set<Long> baseAir = new HashSet<>();           // cells currently considered air
        final Map<Long, BlockState> ops = new LinkedHashMap<>(); // final writes (last write wins)

        // deterministic noise seeded by piece bounds
        final PerlinNoise noise = makeNoiseForPiece(box);

        // 0) seed corridor air from mask (clamped & protected)
        final ArrayList<Long> airFrontier = new ArrayList<>(Math.max(16, voxels.size()));
        for (long packed : voxels) {
            int x = BlockPos.getX(packed), y = BlockPos.getY(packed), z = BlockPos.getZ(packed);
            if (y < minY || y > maxY) continue;
            BlockPos p = new BlockPos(x, y, z);
            if (!box.isInside(p)) continue;
            if (isProtectedCell(p, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;
            baseAir.add(p.asLong());
            ops.put(p.asLong(), Blocks.AIR.defaultBlockState());
            airFrontier.add(packed); // for a few passes that only need seed points
        }

        // 1) push/pull the shell by ±1 using 3D noise (including roof/walls/floor)
        applyNoisePushPull(rnd, noise, airFrontier, minY, maxY,
                baseAir, ops, protectedRoomsAabbs, protectedMask, doorwayWhitelist);

        // 1.5) shallow sink-holes
        openSinkHoles(rnd, airFrontier, minY, maxY,
                baseAir, ops, protectedRoomsAabbs, protectedMask, doorwayWhitelist);

        // 2) coat ALL exposed surfaces around every current air cell (no gaps)
        coatAllExposedSurfaces(rnd, baseAir, ops, protectedRoomsAabbs, protectedMask, doorwayWhitelist);

        // 2.5) acid pockets (1–5)
        placeAcidPockets(rnd, baseAir, ops, protectedRoomsAabbs, protectedMask, doorwayWhitelist);

        // 3) webs across LOCAL normal
        placeWebsLocal(rnd, airFrontier, baseAir, ops, protectedRoomsAabbs, protectedMask, doorwayWhitelist);

        // bucket -> sections
        for (var e : ops.entrySet()) {
            BlockPos pos = BlockPos.of(e.getKey());
            SectionPos sec = SectionPos.of(pos);
            out.computeIfAbsent(sec, k -> new ArrayList<>()).add(BlockData.ofWorld(sec, pos, e.getValue()));
        }
        return out;
    }

    // ----------------------------- protection/view ----------------------------

    private boolean isProtectedCell(BlockPos p,
                                    Collection<BoundingBox> protectedRoomsAabbs,
                                    Bitmask protectedMask,
                                    Set<Long> doorwayWhitelist) {
        long pl = p.asLong();
        if (doorwayWhitelist != null && doorwayWhitelist.contains(pl)) return false;
        if (protectedMask != null && protectedMask.contains(pl)) return true;
        if (protectedRoomsAabbs != null) {
            for (BoundingBox b : protectedRoomsAabbs) if (b.isInside(p)) return true;
        }
        return false;
    }

    private BlockState viewAt(Set<Long> baseAir, Map<Long, BlockState> ops, BlockPos p,
                              Collection<BoundingBox> protectedRoomsAabbs,
                              Bitmask protectedMask,
                              Set<Long> doorwayWhitelist) {
        if (isProtectedCell(p, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) return DEFAULT_SOLID;
        BlockState w = ops.get(p.asLong());
        if (w != null) return w;
        return baseAir.contains(p.asLong()) ? Blocks.AIR.defaultBlockState() : DEFAULT_SOLID;
    }

    private boolean vIsAir(Set<Long> baseAir, Map<Long, BlockState> ops, BlockPos p,
                           Collection<BoundingBox> protectedRoomsAabbs,
                           Bitmask protectedMask,
                           Set<Long> doorwayWhitelist) {
        return viewAt(baseAir, ops, p, protectedRoomsAabbs, protectedMask, doorwayWhitelist).isAir();
    }

    private boolean vIsSolidWall(Set<Long> baseAir, Map<Long, BlockState> ops, BlockPos p,
                                 Collection<BoundingBox> protectedRoomsAabbs,
                                 Bitmask protectedMask,
                                 Set<Long> doorwayWhitelist) {
        var st = viewAt(baseAir, ops, p, protectedRoomsAabbs, protectedMask, doorwayWhitelist);
        return !st.isAir() && st.getFluidState().isEmpty();
    }

    private void vSet(Map<Long, BlockState> ops, BlockPos p, BlockState st,
                      Collection<BoundingBox> protectedRoomsAabbs,
                      Bitmask protectedMask,
                      Set<Long> doorwayWhitelist) {
        if (isProtectedCell(p, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) return;
        ops.put(p.asLong(), st);
    }

    private boolean vPlaceIfAir(Set<Long> baseAir, Map<Long, BlockState> ops, BlockPos p, BlockState st,
                                Collection<BoundingBox> protectedRoomsAabbs,
                                Bitmask protectedMask,
                                Set<Long> doorwayWhitelist) {
        if (isProtectedCell(p, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) return false;
        if (vIsAir(baseAir, ops, p, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) {
            ops.put(p.asLong(), st);
            return true;
        }
        return false;
    }

    // ----------------------------- phase 1: push/pull -------------------------

    private void applyNoisePushPull(RandomSource rnd, PerlinNoise noise,
                                    List<Long> centers, int minY, int maxY,
                                    Set<Long> baseAir, Map<Long, BlockState> ops,
                                    Collection<BoundingBox> protectedRoomsAabbs,
                                    Bitmask protectedMask, Set<Long> doorwayWhitelist) {

        final Direction[] laterals = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
        final Direction[] allDirs  = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN};

        ArrayList<BlockPos> addAir = new ArrayList<>();
        ArrayList<BlockPos> addWall = new ArrayList<>();

        for (long packed : centers) {
            int x = BlockPos.getX(packed), y = BlockPos.getY(packed), z = BlockPos.getZ(packed);
            if (y < minY || y > maxY) continue;
            BlockPos center = new BlockPos(x, y, z);
            if (!box.isInside(center)) continue;
            if (!vIsAir(baseAir, ops, center, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;

            double n = noise.getValue(x * NOISE_SCALE, y * (NOISE_SCALE * 0.6), z * NOISE_SCALE);

            if (n > PUSH_THRESH) {
                Direction[] order = rnd.nextFloat() < 0.85f ? laterals : allDirs;
                for (Direction d : order) {
                    BlockPos tgt = center.relative(d);
                    if (!box.isInside(tgt) || tgt.getY() < minY || tgt.getY() > maxY) continue;
                    BlockPos behind = tgt.relative(d);
                    if (!box.isInside(behind)) continue;

                    // cannot carve into rooms; also avoid obvious corridor collisions by requiring double solid
                    if (isProtectedCell(tgt, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;
                    if (isProtectedCell(behind, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;
                    if (!vIsSolidWall(baseAir, ops, tgt, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;
                    if (!vIsSolidWall(baseAir, ops, behind, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;

                    addAir.add(tgt);
                    break;
                }
            } else if (n < PULL_THRESH) {
                Direction[] order = rnd.nextFloat() < 0.85f ? laterals : allDirs;
                for (Direction d : order) {
                    BlockPos a = center.relative(d);
                    if (!box.isInside(a)) continue;
                    if (!vIsAir(baseAir, ops, a, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;

                    BlockPos back = a.relative(d);
                    if (!box.isInside(back)) continue;
                    if (!vIsSolidWall(baseAir, ops, back, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;

                    if (!wouldChokeIfFilled(baseAir, ops, a, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) {
                        addWall.add(a);
                        break;
                    }
                }
            }
        }

        for (BlockPos p : addAir) {
            vSet(ops, p, Blocks.AIR.defaultBlockState(), protectedRoomsAabbs, protectedMask, doorwayWhitelist);
            baseAir.add(p.asLong());
        }
        for (BlockPos p : addWall) {
            vSet(ops, p, OLIANT_NEST_BLOCK.defaultBlockState(), protectedRoomsAabbs, protectedMask, doorwayWhitelist);
            baseAir.remove(p.asLong());
        }
    }

    private static final Direction[] LATS = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    private boolean wouldChokeIfFilled(Set<Long> baseAir, Map<Long, BlockState> ops, BlockPos airCell,
                                       Collection<BoundingBox> protectedRoomsAabbs,
                                       Bitmask protectedMask, Set<Long> doorwayWhitelist) {
        int lateralAir = 0;
        for (Direction d : LATS) {
            if (viewAt(baseAir, ops, airCell.relative(d), protectedRoomsAabbs, protectedMask, doorwayWhitelist).isAir()) {
                if (++lateralAir > 1) return false;
            }
        }
        return true; // <=1 lateral air → would choke
    }

    // ----------------------------- 1.5: sink holes ----------------------------

    private void openSinkHoles(RandomSource rnd, List<Long> centers, int minY, int maxY,
                               Set<Long> baseAir, Map<Long, BlockState> ops,
                               Collection<BoundingBox> protectedRoomsAabbs,
                               Bitmask protectedMask, Set<Long> doorwayWhitelist) {
        if (centers.isEmpty()) return;

        final int stride = SINK_STRIDE_MIN + rnd.nextInt(SINK_STRIDE_VAR);

        for (int i = 0; i < centers.size(); i += stride) {
            if (rnd.nextFloat() >= SINK_ATTEMPT_P) continue;

            BlockPos anchor = BlockPos.of(centers.get(i));
            if (!box.isInside(anchor)) continue;
            if (!vIsAir(baseAir, ops, anchor, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;

            BlockPos floor = anchor.below();
            if (!inRange(floor.getY(), minY, maxY) || !box.isInside(floor)) continue;

            // need 1 solid floor + 1 solid support below
            if (!vIsSolidWall(baseAir, ops, floor, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;
            BlockPos support = floor.below(SINK_DEPTH);
            if (!box.isInside(support) || support.getY() < minY) continue;
            if (!vIsSolidWall(baseAir, ops, support, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;

            // grow tiny lateral patch 1..3
            int need = 1 + rnd.nextInt(SINK_PATCH_MAX);
            ArrayDeque<BlockPos> q = new ArrayDeque<>();
            HashSet<Long> seen = new HashSet<>();
            ArrayList<BlockPos> patch = new ArrayList<>(need);

            q.add(floor); seen.add(floor.asLong());
            while (!q.isEmpty() && patch.size() < need) {
                BlockPos f = q.pollFirst();
                if (!box.isInside(f)) continue;
                if (isProtectedCell(f, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;

                BlockPos above = f.above();
                if (!vIsAir(baseAir, ops, above, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;
                if (!vIsSolidWall(baseAir, ops, f, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;
                BlockPos sup = f.below(SINK_DEPTH);
                if (!box.isInside(sup) || !vIsSolidWall(baseAir, ops, sup, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;

                patch.add(f);

                for (Direction d : LATS) {
                    if (rnd.nextFloat() < 0.6f) {
                        BlockPos nf = f.relative(d);
                        if (seen.add(nf.asLong())) q.add(nf);
                    }
                }
            }

            for (BlockPos f : patch) {
                // carve depth exactly 1: floor becomes air, then the new “hole” cell also air
                vSet(ops, f, Blocks.AIR.defaultBlockState(), protectedRoomsAabbs, protectedMask, doorwayWhitelist);
                baseAir.add(f.asLong());
                BlockPos hole = f.below();
                vSet(ops, hole, Blocks.AIR.defaultBlockState(), protectedRoomsAabbs, protectedMask, doorwayWhitelist);
                baseAir.add(hole.asLong());

                // optional contrast on support
                BlockPos sup = hole.below();
                if (box.isInside(sup) && vIsSolidWall(baseAir, ops, sup, protectedRoomsAabbs, protectedMask, doorwayWhitelist)
                        && rnd.nextFloat() < 0.35f) {
                    vSet(ops, sup, (rnd.nextFloat() < 0.5f ? OLIANT_DISSOLVED_NEST_BLOCK : OLIANT_NEST_BLOCK).defaultBlockState(),
                            protectedRoomsAabbs, protectedMask, doorwayWhitelist);
                }
            }
        }
    }

    // ----------------------------- phase 2: coating ---------------------------

    private void coatAllExposedSurfaces(RandomSource rnd,
                                        Set<Long> baseAir, Map<Long, BlockState> ops,
                                        Collection<BoundingBox> protectedRoomsAabbs,
                                        Bitmask protectedMask, Set<Long> doorwayWhitelist) {
        if (baseAir.isEmpty()) return;

        BlockPos.MutableBlockPos air = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos nb  = new BlockPos.MutableBlockPos();

        // IMPORTANT: iterate over a snapshot of ALL current air, not just seed voxels
        ArrayList<Long> snapshot = new ArrayList<>(baseAir);

        for (long a : snapshot) {
            air.set(BlockPos.getX(a), BlockPos.getY(a), BlockPos.getZ(a));
            if (!box.isInside(air)) continue;

            // Floor
            nb.set(air).move(Direction.DOWN);
            if (box.isInside(nb) && vIsSolidWall(baseAir, ops, nb, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) {
                Block pick;
                float r = rnd.nextFloat();
                if (r < P_FLOOR_DISSOLVE) pick = OLIANT_DISSOLVED_NEST_BLOCK;
                else if (r < P_FLOOR_DISSOLVE + P_FLOOR_FERTILE) pick = OLIANT_FERTILE_NEST_BLOCK;
                else pick = OLIANT_NEST_BLOCK;
                vSet(ops, nb, pick.defaultBlockState(), protectedRoomsAabbs, protectedMask, doorwayWhitelist);
            }

            // Roof
            nb.set(air).move(Direction.UP);
            if (box.isInside(nb) && vIsSolidWall(baseAir, ops, nb, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) {
                Block pick = rnd.nextFloat() < P_ROOF_FERTILE ? OLIANT_FERTILE_NEST_BLOCK : OLIANT_NEST_BLOCK;
                vSet(ops, nb, pick.defaultBlockState(), protectedRoomsAabbs, protectedMask, doorwayWhitelist);
            }

            // Walls
            for (Direction d : LATS) {
                nb.set(air).move(d);
                if (box.isInside(nb) && vIsSolidWall(baseAir, ops, nb, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) {
                    Block pick = rnd.nextFloat() < P_WALL_FERTILE ? OLIANT_FERTILE_NEST_BLOCK : OLIANT_NEST_BLOCK;
                    vSet(ops, nb, pick.defaultBlockState(), protectedRoomsAabbs, protectedMask, doorwayWhitelist);
                }
            }
        }
    }

    // ----------------------------- 2.5: acid pockets (SUNKEN, SEALED) -----------
    private void placeAcidPockets(RandomSource rnd,
                                  Set<Long> baseAir, Map<Long, BlockState> ops,
                                  Collection<BoundingBox> protectedRoomsAabbs,
                                  Bitmask protectedMask, Set<Long> doorwayWhitelist) {

        if (baseAir.isEmpty()) return;

        // Work on a snapshot of corridor air (y layer)
        ArrayList<Long> airSnapshot = new ArrayList<>(baseAir);
        HashSet<Long> used = new HashSet<>(); // don't start twice inside same pocket

        for (long al : airSnapshot) {
            if (!used.add(al)) continue;

            BlockPos top = BlockPos.of(al);       // corridor air at y
            if (!box.isInside(top)) continue;

            // Pre-check: top must be air (corridor), floor at y-1 must be solid, support at y-2 solid
            BlockPos basin = top.below();         // y-1
            BlockPos support = top.below(2);      // y-2
            if (!box.isInside(basin) || !box.isInside(support)) continue;
            if (isProtectedCell(basin, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;
            if (!vIsSolidWall(baseAir, ops, basin, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;
            if (!vIsSolidWall(baseAir, ops, support, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;

            // Grow a small, flat basin patch (1..5) at *top* positions, constrained by valid basin/support below.
            int target = 1 + rnd.nextInt(ACID_MAX_SIZE); // 1..5
            ArrayDeque<BlockPos> q = new ArrayDeque<>();
            HashSet<Long> seenTop = new HashSet<>();
            ArrayList<BlockPos> patchTop = new ArrayList<>(target);

            q.add(top);
            seenTop.add(top.asLong());

            while (!q.isEmpty() && patchTop.size() < target) {
                BlockPos t = q.pollFirst();
                if (!box.isInside(t)) continue;
                if (!vIsAir(baseAir, ops, t, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue; // still corridor air?

                BlockPos bsn = t.below();      // candidate basin cell (y-1)
                BlockPos sup = t.below(2);     // support (y-2)
                if (!box.isInside(bsn) || !box.isInside(sup)) continue;

                // Basin must currently be solid (don’t reuse existing holes), support must be solid.
                if (!vIsSolidWall(baseAir, ops, bsn, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;
                if (!vIsSolidWall(baseAir, ops, sup, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;
                if (isProtectedCell(bsn, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;
                if (isProtectedCell(sup, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;

                patchTop.add(t);

                // Expand laterally along corridor floor (stay on same Y)
                for (Direction d : LATS) {
                    if (rnd.nextFloat() < 0.75f) {
                        BlockPos nt = t.relative(d);
                        if (nt.getY() != top.getY()) continue;
                        long nl = nt.asLong();
                        if (seenTop.add(nl)) q.add(nt);
                    }
                }
            }

            if (patchTop.isEmpty() || patchTop.size() > ACID_MAX_SIZE) continue;

            // Check perimeter *at basin Y*: every neighbor at y-1 that is NOT in our basin must be solid & non-fluid.
            HashSet<Long> patchBasin = new HashSet<>(patchTop.size());
            for (BlockPos t : patchTop) patchBasin.add(t.below().asLong());

            boolean leaks = false;
            int openSides = 0; // allow up to ACID_MAX_OPEN tiny openings, otherwise reject
            outer:
            for (BlockPos t : patchTop) {
                BlockPos bsn = t.below(); // y-1
                for (Direction d : LATS) {
                    BlockPos nb = bsn.relative(d);
                    if (!box.isInside(nb)) { leaks = true; break outer; }
                    long nbl = nb.asLong();
                    if (patchBasin.contains(nbl)) continue; // interior neighbor

                    BlockState st = viewAt(baseAir, ops, nb, protectedRoomsAabbs, protectedMask, doorwayWhitelist);
                    if (!st.getFluidState().isEmpty()) { leaks = true; break outer; } // next to another liquid → reject
                    if (st.isAir()) {
                        // basin would spill laterally into another void at same level
                        if (++openSides > ACID_MAX_OPEN) { leaks = true; break outer; }
                    }
                }
                // also ensure basin cell itself won’t drain downwards
                if (!vIsSolidWall(baseAir, ops, bsn.below(), protectedRoomsAabbs, protectedMask, doorwayWhitelist)) {
                    leaks = true; break;
                }
            }
            if (leaks) continue;

            // SINK & FILL:
            //  - set support (y-2) to dissolved for slimy look (keep it solid)
            //  - convert basin (y-1) to ACID directly (don’t add to baseAir so later passes won’t treat it as air)
            for (BlockPos t : patchTop) {
                BlockPos bsn = t.below();      // y-1
                BlockPos sup = t.below(2);     // y-2

                vSet(ops, sup, OLIANT_DISSOLVED_NEST_BLOCK.defaultBlockState(), protectedRoomsAabbs, protectedMask, doorwayWhitelist);
                vSet(ops, bsn, OLIANT_ACID_BLOCK.defaultBlockState(),           protectedRoomsAabbs, protectedMask, doorwayWhitelist);

                // Important: do NOT add basin to baseAir; it’s liquid now.
                // The corridor top cell (t at y) remains air → open pool.
            }

            // Cosmetic: coat the *ring* around the basin at y-1
            ArrayList<BlockPos> basinCells = new ArrayList<>(patchTop.size());
            for (BlockPos t : patchTop) basinCells.add(t.below());
            coatRingAround(basinCells, rnd, baseAir, ops, protectedRoomsAabbs, protectedMask, doorwayWhitelist);

            // Mark tops as used so we don’t try to start another pool overlapping this one.
            for (BlockPos t : patchTop) used.add(t.asLong());
        }
    }

    private void coatRingAround(List<BlockPos> cells, RandomSource rnd,
                                Set<Long> baseAir, Map<Long, BlockState> ops,
                                Collection<BoundingBox> protectedRoomsAabbs,
                                Bitmask protectedMask, Set<Long> doorwayWhitelist) {
        for (BlockPos w : cells) {
            for (Direction d : Direction.values()) {
                BlockPos n = w.relative(d);
                if (!box.isInside(n)) continue;
                if (isProtectedCell(n, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;
                var st = viewAt(baseAir, ops, n, protectedRoomsAabbs, protectedMask, doorwayWhitelist);
                if (!st.isAir() && st.getFluidState().isEmpty()) {
                    Block b = switch (d) {
                        case DOWN -> OLIANT_DISSOLVED_NEST_BLOCK;
                        case UP   -> (rnd.nextFloat() < 0.10f) ? OLIANT_FERTILE_NEST_BLOCK : OLIANT_NEST_BLOCK;
                        default   -> (rnd.nextFloat() < 0.12f) ? OLIANT_FERTILE_NEST_BLOCK : OLIANT_NEST_BLOCK;
                    };
                    vSet(ops, n, b.defaultBlockState(), protectedRoomsAabbs, protectedMask, doorwayWhitelist);
                }
            }
        }
    }

    // ----------------------------- phase 3: webs (local normal) ---------------

    private void placeWebsLocal(RandomSource rnd, List<Long> centers,
                                Set<Long> baseAir, Map<Long, BlockState> ops,
                                Collection<BoundingBox> protectedRoomsAabbs,
                                Bitmask protectedMask, Set<Long> doorwayWhitelist) {
        if (centers.isEmpty()) return;

        final int stride = WEB_STRIDE_MIN + rnd.nextInt(WEB_STRIDE_VAR);

        for (int i = 0; i < centers.size(); i += stride) {
            BlockPos anchor = BlockPos.of(centers.get(i));
            if (!box.isInside(anchor)) continue;
            if (!vIsAir(baseAir, ops, anchor, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;

            // Estimate local tangent from previous/next mask points (gives us corridor direction)
            Vec3f tangent = estimateTangent(i);
            if (tangent.len2() < 1e-3f) continue;
            tangent.normalize();

            // Build an orthonormal-ish basis (u,v) perpendicular to tangent
            Vec3f u = anyPerp(tangent);
            Vec3f v = tangent.cross(u).normalize();

            float r = rnd.nextFloat();
            if (r < WEB_MEMBRANE_P) {
                drawWebMembranePlane(anchor, u, v, rnd, baseAir, ops, protectedRoomsAabbs, protectedMask, doorwayWhitelist);
            } else if (r < WEB_MEMBRANE_P + WEB_DIAGONAL_P) {
                drawWebDiagonalPlane(anchor, u, v, rnd, baseAir, ops, protectedRoomsAabbs, protectedMask, doorwayWhitelist);
            } else if (r < WEB_MEMBRANE_P + WEB_DIAGONAL_P + WEB_CORNER_P) {
                tryWebCornerClump(anchor, rnd, baseAir, ops, protectedRoomsAabbs, protectedMask, doorwayWhitelist);
            } else if (rnd.nextFloat() < WEB_SINGLE_P) {
                vPlaceIfAir(baseAir, ops, anchor, OLIANT_WEB.defaultBlockState(), protectedRoomsAabbs, protectedMask, doorwayWhitelist);
            }
        }
    }

    private void drawWebMembranePlane(BlockPos center, Vec3f u, Vec3f v, RandomSource rnd,
                                      Set<Long> baseAir, Map<Long, BlockState> ops,
                                      Collection<BoundingBox> protectedRoomsAabbs,
                                      Bitmask protectedMask, Set<Long> doorwayWhitelist) {
        // small rectangle within cross-section bounds
        int halfU = 1 + rnd.nextInt(2); // 1..2
        int halfV = 1 + rnd.nextInt(2); // 1..2

        int placed = 0, attempted = 0;
        for (int iu = -halfU; iu <= halfU; iu++) {
            for (int iv = -halfV; iv <= halfV; iv++) {
                BlockPos p = center.offset(round(u.x * iu + v.x * iv),
                        round(u.y * iu + v.y * iv),
                        round(u.z * iu + v.z * iv));
                if (!box.isInside(p)) continue;
                attempted++;
                if (vPlaceIfAir(baseAir, ops, p, OLIANT_WEB.defaultBlockState(), protectedRoomsAabbs, protectedMask, doorwayWhitelist))
                    placed++;
            }
        }
        // heuristically prune sparse membranes
        if (placed < Math.max(3, attempted / 3)) {
            // do nothing (let singles fill elsewhere)
        }
    }

    private void drawWebDiagonalPlane(BlockPos center, Vec3f u, Vec3f v, RandomSource rnd,
                                      Set<Long> baseAir, Map<Long, BlockState> ops,
                                      Collection<BoundingBox> protectedRoomsAabbs,
                                      Bitmask protectedMask, Set<Long> doorwayWhitelist) {
        // draw a diagonal from one corner to the opposite in the (u,v) plane
        int len = 2 + rnd.nextInt(3); // 2..4
        int placed = 0;
        for (int t = -len; t <= len; t++) {
            BlockPos p = center.offset(round(u.x * t + v.x * t),
                    round(u.y * t + v.y * t),
                    round(u.z * t + v.z * t));
            if (!box.isInside(p)) continue;
            if (rnd.nextFloat() < 0.9f) {
                if (vPlaceIfAir(baseAir, ops, p, OLIANT_WEB.defaultBlockState(), protectedRoomsAabbs, protectedMask, doorwayWhitelist))
                    placed++;
                if (rnd.nextFloat() < 0.25f) {
                    BlockPos q = p.above(rnd.nextBoolean() ? 1 : -1);
                    if (box.isInside(q))
                        vPlaceIfAir(baseAir, ops, q, OLIANT_WEB.defaultBlockState(), protectedRoomsAabbs, protectedMask, doorwayWhitelist);
                }
            }
        }
        // no strict minimum; diagonals can be wispy
    }

    private void tryWebCornerClump(BlockPos anchor, RandomSource rnd,
                                   Set<Long> baseAir, Map<Long, BlockState> ops,
                                   Collection<BoundingBox> protectedRoomsAabbs,
                                   Bitmask protectedMask, Set<Long> doorwayWhitelist) {
        Direction[][] pairs = {
                {Direction.NORTH, Direction.WEST},
                {Direction.NORTH, Direction.EAST},
                {Direction.SOUTH, Direction.WEST},
                {Direction.SOUTH, Direction.EAST}
        };
        Collections.shuffle(Arrays.asList(pairs), new java.util.Random(rnd.nextLong()));
        for (Direction[] pair : pairs) {
            BlockPos w1 = anchor.relative(pair[0]);
            BlockPos w2 = anchor.relative(pair[1]);
            if (!box.isInside(w1) || !box.isInside(w2)) continue;
            if (!vIsSolidWall(baseAir, ops, w1, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;
            if (!vIsSolidWall(baseAir, ops, w2, protectedRoomsAabbs, protectedMask, doorwayWhitelist)) continue;

            int placed = 0;
            for (int dx = 0; dx <= 1; dx++) {
                for (int dy = 0; dy <= 1; dy++) {
                    BlockPos p = anchor.above(dy)
                            .relative(pair[0].getOpposite(), dx)
                            .relative(pair[1].getOpposite(), dx);
                    if (!box.isInside(p)) continue;
                    if (rnd.nextFloat() < 0.85f) {
                        if (vPlaceIfAir(baseAir, ops, p, OLIANT_WEB.defaultBlockState(), protectedRoomsAabbs, protectedMask, doorwayWhitelist)) placed++;
                    }
                }
            }
            for (Direction d : new Direction[]{pair[0].getOpposite(), pair[1].getOpposite(), Direction.UP}) {
                BlockPos q = anchor.relative(d);
                if (box.isInside(q))
                    vPlaceIfAir(baseAir, ops, q, OLIANT_WEB.defaultBlockState(), protectedRoomsAabbs, protectedMask, doorwayWhitelist);
            }
            if (placed >= 2) return;
        }
    }

    // ----------------------------- helpers -----------------------------------

    private static boolean inRange(int y, int lo, int hi) { return y >= lo && y <= hi; }

    private static int round(double v) { return (int) Math.round(v); }

    /** crude tangent from ordered mask (previous/next) */
    private Vec3f estimateTangent(int idx) {
        int i0 = Math.max(0, idx - 1);
        int i1 = Math.min(voxels.size() - 1, idx + 1);
        BlockPos a = BlockPos.of(voxels.get(i0));
        BlockPos b = BlockPos.of(voxels.get(i1));
        return new Vec3f(b.getX() - a.getX(), b.getY() - a.getY(), b.getZ() - a.getZ());
    }

    /** any vector perpendicular to n (gridish) */
    private static Vec3f anyPerp(Vec3f n) {
        // pick the smallest component axis to cross with for numeric stability
        float ax = Math.abs(n.x), ay = Math.abs(n.y), az = Math.abs(n.z);
        Vec3f k = (ax <= ay && ax <= az) ? new Vec3f(1,0,0) : (ay <= ax && ay <= az) ? new Vec3f(0,1,0) : new Vec3f(0,0,1);
        Vec3f u = n.cross(k);
        if (u.len2() < 1e-6f) u = new Vec3f(0,1,0); // fallback
        return u.normalize();
    }

    private PerlinNoise makeNoiseForPiece(BoundingBox b) {
        long seed = 0x9E3779B97F4A7C15L;
        seed ^= (long)b.minX() * 0xBF58476D1CE4E5B9L;
        seed ^= (long)b.minY() * 0x94D049BB133111EBL;
        seed ^= (long)b.minZ() * 0x632BE59BD9B4E019L;
        seed ^= (long)b.maxX() * 0xAEF17502108EF2D9L;
        seed ^= (long)b.maxY() * 0xD1B54A32D192ED03L;
        seed ^= (long)b.maxZ() * 0x94D049BB133111EBL;
        RandomSource rs = RandomSource.create(seed);
        return PerlinNoise.create(rs, List.of(-2, -1, 0));
    }

    private static BoundingBox computeBoundingBox(Bitmask mask) {
        final int[] minX = {Integer.MAX_VALUE};
        final int[] minY = { Integer.MAX_VALUE };
        final int[] minZ = { Integer.MAX_VALUE };
        final int[] maxX = {Integer.MIN_VALUE};
        final int[] maxY = { Integer.MIN_VALUE };
        final int[] maxZ = { Integer.MIN_VALUE };
        final int[] empty = new int[1];
        mask.forEachLong(p -> {
            empty[0] = 1;
            int x = BlockPos.getX(p), y = BlockPos.getY(p), z = BlockPos.getZ(p);
            if (x < minX[0]) minX[0] = x; if (y < minY[0]) minY[0] = y; if (z < minZ[0]) minZ[0] = z;
            if (x > maxX[0]) maxX[0] = x; if (y > maxY[0]) maxY[0] = y; if (z > maxZ[0]) maxZ[0] = z;
        });
        if (empty[0] == 0) return new BoundingBox(0,0,0,0,0,0);
        return new BoundingBox(minX[0], minY[0], minZ[0], maxX[0], maxY[0], maxZ[0]);
    }

    private static List<Long> toList(Bitmask mask) {
        ArrayList<Long> out = new ArrayList<>(mask.size());
        mask.forEachLong(out::add);
        return out;
    }

    // tiny float vec helper
    private static final class Vec3f {
        float x, y, z;
        Vec3f(float x, float y, float z){ this.x=x; this.y=y; this.z=z; }
        Vec3f cross(Vec3f o){ return new Vec3f(y*o.z - z*o.y, z*o.x - x*o.z, x*o.y - y*o.x); }
        float len2(){ return x*x + y*y + z*z; }
        Vec3f normalize(){ float l = (float)Math.sqrt(len2()); if (l>1e-6f){ x/=l; y/=l; z/=l; } return this; }
    }
}