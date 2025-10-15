package dev.galacticraft.mod.world.gen.dungeon.util;

import dev.galacticraft.mod.world.gen.dungeon.records.BlockData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

import java.util.*;

/**
 * Pure, non-placing carver simulation with NO Level and NO ChunkPos.
 * Everything outside the mask is treated as "solid stone" by default.
 * Pipeline: mask carve -> noise push/pull -> coat surfaces -> acid pockets -> webs.
 *
 * Use:
 *   MaskCarvePiece carver = new MaskCarvePiece(mask);
 *   HashMap<SectionPos, List<MaskCarvePiece.BlockData>> blocks =
 *       carver.getBlocks(random, minY, maxY);
 */
public class MaskCarvePiece {

    // ====== Config knobs (kept for parity) ======
    private float meanRadius = 1.25f;
    private float radiusJitter = 0.6f;
    private float roughness = 0.25f;
    private float bulgeChance = 0.07f;
    private float maxBulgeExtra = 1.75f;
    private int coatThickness = 1;

    // Input
    private final List<Long> voxels;          // centerline voxels (BlockPos.asLong)
    private final BoundingBox boundingBox;

    // Constants
    private static final int STRAIGHT_SPAN_LIMIT = 12;

    private static final Block OLIANT_NEST_BLOCK            = Blocks.COARSE_DIRT;
    private static final Block OLIANT_FERTILE_NEST_BLOCK    = Blocks.MOSS_BLOCK;
    private static final Block OLIANT_DISSOLVED_NEST_BLOCK  = Blocks.MUD;
    private static final Block OLIANT_ACID_BLOCK            = Blocks.WATER;
    private static final Block OLIANT_WEB                   = Blocks.COBWEB;

    /** Default solid for simulated world. Change if you want a different host. */
    private static final BlockState DEFAULT_SOLID = Blocks.STONE.defaultBlockState();

    // ====== Construction ======
    public MaskCarvePiece(Bitmask mask) {
        this.voxels = toList(mask);
        this.boundingBox = computeBoundingBox(mask);
    }

    public MaskCarvePiece(Bitmask mask,
                          float meanRadius, float radiusJitter, float roughness,
                          float bulgeChance, float maxBulgeExtra,
                          int coatThickness) {
        this(mask);
        this.meanRadius = meanRadius;
        this.radiusJitter = radiusJitter;
        this.roughness = roughness;
        this.bulgeChance = bulgeChance;
        this.maxBulgeExtra = maxBulgeExtra;
        this.coatThickness = coatThickness;
    }

    public BoundingBox getBoundingBox() { return this.boundingBox; }

    // ====== Public API (no Level, no ChunkPos) ======
    /**
     * Simulate carving within this piece's bounding box.
     * No Level access; initial state is "solid" everywhere except along the mask voxels (air).
     *
     * @param random RNG for stochastic choices (webs/coat). Noise field is seeded deterministically from the Box.
     * @param minY   min build height (inclusive)
     * @param maxY   max build height (inclusive)
     * @return SectionPos -> list of BlockData (packed coords + BlockState)
     */
    public HashMap<SectionPos, List<BlockData>> getBlocks(RandomSource random, int minY, int maxY) {
        var box = this.boundingBox;
        var out = new HashMap<SectionPos, List<BlockData>>();
        if (voxels == null || voxels.isEmpty()) return out;

        // Virtual world:
        // - baseAir: mask voxels initially carved to AIR
        // - ops:     all subsequent writes (AIR / coatings / acid / webs), overwriting earlier
        final Set<Long> baseAir = new HashSet<>();
        final Map<Long, BlockState> ops = new LinkedHashMap<>();

        // Deterministic noise for this piece (derived from its bounding box)
        PerlinNoise noise = makeNoiseForPiece(box);

        // Phase 0: carve the mask to air (clipped to build height and box)
        HashSet<Long> airMask = new HashSet<>(Math.max(16, voxels.size() * 2));
        for (long packed : voxels) {
            int x = BlockPos.getX(packed), y = BlockPos.getY(packed), z = BlockPos.getZ(packed);
            if (y < minY || y > maxY) continue;
            BlockPos p = new BlockPos(x, y, z);
            if (!box.isInside(p)) continue;
            baseAir.add(p.asLong());
            ops.put(p.asLong(), Blocks.AIR.defaultBlockState());
            airMask.add(packed);
        }

        // Phase 1: noise-driven +-1 push/pull (clipped to box & build height)
        applyNoisePushPull_VIRTUAL(box, random, noise, airMask, voxels, minY, maxY, baseAir, ops);

        // Phase 2: coat exposed surfaces (clipped)
        coatAllExposedSurfaces_VIRTUAL(box, random, airMask, baseAir, ops);

        // Phase 2.5: acid pockets (clipped)
        placeAcidPockets_VIRTUAL(box, random, airMask, baseAir, ops);

        // Phase 3: webs (clipped)
        placeWebs_VIRTUAL(box, random, voxels, baseAir, ops);

        // Bucket ops -> sections with packed local coords
        for (var e : ops.entrySet()) {
            BlockPos pos = BlockPos.of(e.getKey());
            BlockState st = e.getValue();
            SectionPos sec = SectionPos.of(pos);
            out.computeIfAbsent(sec, k -> new ArrayList<>()).add(BlockData.ofWorld(sec, pos, st));
        }
        return out;
    }

    // ====== Virtual world helpers (NO Level) ======
    private BlockState viewAt(Set<Long> baseAir, Map<Long, BlockState> ops, BlockPos p) {
        BlockState v = ops.get(p.asLong());
        if (v != null) return v;
        return baseAir.contains(p.asLong()) ? Blocks.AIR.defaultBlockState() : DEFAULT_SOLID;
    }
    private boolean vIsAir(Set<Long> baseAir, Map<Long, BlockState> ops, BlockPos p) {
        return viewAt(baseAir, ops, p).isAir();
    }
    private boolean vIsSolidWall(Set<Long> baseAir, Map<Long, BlockState> ops, BlockPos p) {
        var st = viewAt(baseAir, ops, p);
        return !st.isAir() && st.getFluidState().isEmpty();
    }
    private boolean vPlaceIfAir(Set<Long> baseAir, Map<Long, BlockState> ops, BlockPos p, BlockState st) {
        if (vIsAir(baseAir, ops, p)) { ops.put(p.asLong(), st); return true; }
        return false;
    }
    private void vSet(Map<Long, BlockState> ops, BlockPos p, BlockState st) {
        ops.put(p.asLong(), st);
    }

    // ====== Phase 1: noise push/pull (virtual) ======
    private void applyNoisePushPull_VIRTUAL(BoundingBox box,
                                            RandomSource rnd,
                                            PerlinNoise noise,
                                            Set<Long> airMask,
                                            List<Long> centers,
                                            int minY, int maxY,
                                            Set<Long> baseAir,
                                            Map<Long, BlockState> ops) {

        final double scale = 1.0 / 6.0;
        final double pushThresh =  0.35;
        final double pullThresh = -0.35;

        final Direction[] laterals = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
        final Direction[] allDirs  = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN};

        ArrayList<BlockPos> addAir = new ArrayList<>();
        ArrayList<BlockPos> addWall = new ArrayList<>();

        for (long packed : centers) {
            int x = BlockPos.getX(packed), y = BlockPos.getY(packed), z = BlockPos.getZ(packed);
            if (y < minY || y > maxY) continue;

            BlockPos center = new BlockPos(x, y, z);
            if (!box.isInside(center)) continue;
            if (!vIsAir(baseAir, ops, center)) continue;

            double n = sampleNoise3D(noise, x, y, z, scale);

            if (n > pushThresh) {
                Direction[] order = rnd.nextFloat() < 0.85f ? laterals : allDirs;
                for (Direction d : order) {
                    BlockPos tgt = center.relative(d);
                    if (!box.isInside(tgt)) continue;
                    if (tgt.getY() < minY || tgt.getY() > maxY) continue;

                    BlockPos behind = tgt.relative(d);
                    if (!box.isInside(behind)) continue;

                    if (!vIsSolidWall(baseAir, ops, tgt) || !vIsSolidWall(baseAir, ops, behind)) continue;
                    if (isLikelyRoomVoid_VIRTUAL(baseAir, ops, behind)) continue;

                    addAir.add(tgt);
                    break;
                }
            } else if (n < pullThresh) {
                Direction[] order = rnd.nextFloat() < 0.85f ? laterals : allDirs;
                for (Direction d : order) {
                    BlockPos a = center.relative(d);
                    if (!box.isInside(a)) continue;

                    if (!vIsAir(baseAir, ops, a)) continue;
                    long al = a.asLong();
                    if (airMask.contains(al)) continue;

                    BlockPos back = a.relative(d);
                    if (!box.isInside(back)) continue;

                    if (!vIsSolidWall(baseAir, ops, back)) continue;
                    if (!wouldChokeIfFilled_VIRTUAL(baseAir, ops, a)) {
                        addWall.add(a);
                        break;
                    }
                }
            }
        }

        for (BlockPos p : addAir) {
            vSet(ops, p, Blocks.AIR.defaultBlockState());
            baseAir.add(p.asLong());
        }
        for (BlockPos p : addWall) {
            vSet(ops, p, OLIANT_NEST_BLOCK.defaultBlockState());
            baseAir.remove(p.asLong());
        }
    }

    // ====== Phase 2: coat exposed surfaces (virtual) ======
    private void coatAllExposedSurfaces_VIRTUAL(BoundingBox box,
                                                RandomSource rnd,
                                                Set<Long> airMask,
                                                Set<Long> baseAir,
                                                Map<Long, BlockState> ops) {
        final float floorFertileP = 0.18f;
        final float floorDissolvedP = 0.12f;
        final float wallFertileP  = 0.12f;
        final float roofFertileP  = 0.10f;

        BlockPos.MutableBlockPos air = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos nb  = new BlockPos.MutableBlockPos();

        for (long a : airMask) {
            int ax = BlockPos.getX(a), ay = BlockPos.getY(a), az = BlockPos.getZ(a);

            air.set(ax, ay, az);
            if (!box.isInside(air) || !vIsAir(baseAir, ops, air)) continue;

            // Floor
            nb.set(air).move(Direction.DOWN);
            if (box.isInside(nb) && vIsSolidWall(baseAir, ops, nb)) {
                Block pick = OLIANT_NEST_BLOCK;
                float r = rnd.nextFloat();
                if (r < floorDissolvedP) pick = OLIANT_DISSOLVED_NEST_BLOCK;
                else if (r < floorDissolvedP + floorFertileP) pick = OLIANT_FERTILE_NEST_BLOCK;
                vSet(ops, nb, pick.defaultBlockState());
            }

            // Roof
            nb.set(air).move(Direction.UP);
            if (box.isInside(nb) && vIsSolidWall(baseAir, ops, nb)) {
                Block pick = (rnd.nextFloat() < roofFertileP) ? OLIANT_FERTILE_NEST_BLOCK : OLIANT_NEST_BLOCK;
                vSet(ops, nb, pick.defaultBlockState());
            }

            // Walls
            for (Direction d : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
                nb.set(air).move(d);
                if (box.isInside(nb) && vIsSolidWall(baseAir, ops, nb)) {
                    Block pick = (rnd.nextFloat() < wallFertileP) ? OLIANT_FERTILE_NEST_BLOCK : OLIANT_NEST_BLOCK;
                    vSet(ops, nb, pick.defaultBlockState());
                }
            }
        }
    }

    // ====== Phase 2.5: acid pockets (virtual) ======
    private void placeAcidPockets_VIRTUAL(BoundingBox box,
                                          RandomSource rnd,
                                          Set<Long> airMask,
                                          Set<Long> baseAir,
                                          Map<Long, BlockState> ops) {
        if (airMask == null || airMask.isEmpty()) return;

        final int MAX_SIZE = 9;

        HashSet<Long> globalVisited = new HashSet<>();
        ArrayDeque<BlockPos> q = new ArrayDeque<>();

        for (long al : airMask) {
            if (globalVisited.contains(al)) continue;

            BlockPos start = BlockPos.of(al);
            if (!box.isInside(start)) { globalVisited.add(al); continue; }
            if (!vIsAir(baseAir, ops, start)) { globalVisited.add(al); continue; }

            BlockPos startBelow = start.below();
            if (!box.isInside(startBelow)) { globalVisited.add(al); continue; }
            if (!vIsSolidWall(baseAir, ops, startBelow)) { globalVisited.add(al); continue; }

            ArrayList<BlockPos> comp = new ArrayList<>(8);
            HashSet<Long> compSet = new HashSet<>();
            boolean overflow = false;

            q.clear();
            q.add(start);
            compSet.add(start.asLong());
            globalVisited.add(start.asLong());
            final int y0 = start.getY();

            while (!q.isEmpty()) {
                BlockPos p = q.pollFirst();
                comp.add(p);

                if (comp.size() > MAX_SIZE) { overflow = true; break; }

                for (Direction d : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
                    BlockPos n = p.relative(d);
                    if (!box.isInside(n)) continue;
                    if (n.getY() != y0) continue;

                    if (!vIsAir(baseAir, ops, n)) continue;

                    BlockPos nb = n.below();
                    if (!box.isInside(nb)) continue;
                    if (!vIsSolidWall(baseAir, ops, nb)) continue;

                    long nl = n.asLong();
                    if (compSet.add(nl)) {
                        q.add(n);
                        globalVisited.add(nl);
                    }
                }
            }

            if (overflow || comp.isEmpty()) continue;

            boolean leaks = false;
            for (BlockPos p : comp) {
                for (Direction d : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
                    BlockPos n = p.relative(d);
                    if (!box.isInside(n)) { leaks = true; break; }
                    var st = viewAt(baseAir, ops, n);
                    if (!st.getFluidState().isEmpty()) { leaks = true; break; }
                    if (st.isAir() && !compSet.contains(n.asLong())) { leaks = true; break; }
                }
                if (leaks) break;
            }
            if (leaks) continue;

            for (BlockPos p : comp) vSet(ops, p.below(), OLIANT_DISSOLVED_NEST_BLOCK.defaultBlockState());
            for (BlockPos p : comp) vSet(ops, p, OLIANT_ACID_BLOCK.defaultBlockState());

            coatRingAround_VIRTUAL(box, comp, rnd, baseAir, ops);
        }
    }

    private void coatRingAround_VIRTUAL(BoundingBox box, List<BlockPos> cells, RandomSource rnd,
                                        Set<Long> baseAir, Map<Long, BlockState> ops) {
        for (BlockPos w : cells) {
            for (Direction d : Direction.values()) {
                BlockPos n = w.relative(d);
                if (!inBox(box, n)) continue;
                var st = viewAt(baseAir, ops, n);
                if (!st.isAir() && st.getFluidState().isEmpty()) {
                    Block pick;
                    if (d == Direction.DOWN) pick = OLIANT_DISSOLVED_NEST_BLOCK;
                    else if (d == Direction.UP) pick = (rnd.nextFloat() < 0.10f) ? OLIANT_FERTILE_NEST_BLOCK : OLIANT_NEST_BLOCK;
                    else pick = (rnd.nextFloat() < 0.12f) ? OLIANT_FERTILE_NEST_BLOCK : OLIANT_NEST_BLOCK;
                    vSet(ops, n, pick.defaultBlockState());
                }
            }
        }
    }

    // ====== Phase 3: webs (virtual) ======
    private void placeWebs_VIRTUAL(BoundingBox box, RandomSource rnd,
                                   List<Long> centers,
                                   Set<Long> baseAir, Map<Long, BlockState> ops) {
        if (centers.isEmpty()) return;

        final float singleP   = 0.020f;
        final float membraneP = 0.010f;
        final float diagonalP = 0.015f;
        final float cornerP   = 0.025f;

        final int stride = 3 + rnd.nextInt(3);

        for (int i = 0; i < centers.size(); i += stride) {
            long packed = centers.get(i);
            BlockPos anchor = BlockPos.of(packed);
            if (!box.isInside(anchor)) continue;
            if (!vIsAir(baseAir, ops, anchor)) continue;

            boolean straightX = isStraightAlongAxis_VIRTUAL(box, anchor, Direction.Axis.X, baseAir, ops);
            boolean straightY = isStraightAlongAxis_VIRTUAL(box, anchor, Direction.Axis.Y, baseAir, ops);
            boolean straightZ = isStraightAlongAxis_VIRTUAL(box, anchor, Direction.Axis.Z, baseAir, ops);

            float r = rnd.nextFloat();

            if (!(straightX || straightY || straightZ)) {
                if (rnd.nextFloat() < cornerP) tryWebCornerClump_VIRTUAL(box, rnd, anchor, baseAir, ops);
                if (rnd.nextFloat() < singleP) vPlaceIfAir(baseAir, ops, anchor, OLIANT_WEB.defaultBlockState());
                continue;
            }

            if (r < membraneP) {
                ArrayList<Direction.Axis> axes = new ArrayList<>(3);
                if (straightX) axes.add(Direction.Axis.X);
                if (straightY) axes.add(Direction.Axis.Y);
                if (straightZ) axes.add(Direction.Axis.Z);
                if (tryWebMembraneAcross_VIRTUAL(box, rnd, anchor, axes.get(rnd.nextInt(axes.size())), baseAir, ops)) continue;
            } else if (r < membraneP + diagonalP) {
                ArrayList<Direction.Axis> axes = new ArrayList<>(3);
                if (straightX) axes.add(Direction.Axis.X);
                if (straightY) axes.add(Direction.Axis.Y);
                if (straightZ) axes.add(Direction.Axis.Z);
                if (tryWebDiagonalAcross_VIRTUAL(box, rnd, anchor, axes.get(rnd.nextInt(axes.size())), baseAir, ops)) continue;
            } else if (r < membraneP + diagonalP + cornerP) {
                if (tryWebCornerClump_VIRTUAL(box, rnd, anchor, baseAir, ops)) continue;
            }

            if (rnd.nextFloat() < singleP) {
                vPlaceIfAir(baseAir, ops, anchor, OLIANT_WEB.defaultBlockState());
            }
        }
    }

    // ====== Cross-section / straightness (virtual) ======
    private static final class XSec {
        int aL, aR; // first perpendicular axis walls
        int bL, bR; // second perpendicular axis walls
        int wA, wB; // interior widths
        int cA, cB; // centers
    }

    private XSec measureCrossSection_VIRTUAL(BoundingBox box, BlockPos at, Direction.Axis axis,
                                             Set<Long> baseAir, Map<Long, BlockState> ops) {
        XSec s = new XSec();
        if (axis == Direction.Axis.X) {
            s.aL = spanToWall_VIRTUAL(box, at, Direction.NORTH, STRAIGHT_SPAN_LIMIT, baseAir, ops);
            s.aR = spanToWall_VIRTUAL(box, at, Direction.SOUTH, STRAIGHT_SPAN_LIMIT, baseAir, ops);
            s.bL = spanToWall_VIRTUAL(box, at, Direction.DOWN,  STRAIGHT_SPAN_LIMIT, baseAir, ops);
            s.bR = spanToWall_VIRTUAL(box, at, Direction.UP,    STRAIGHT_SPAN_LIMIT, baseAir, ops);
        } else if (axis == Direction.Axis.Z) {
            s.aL = spanToWall_VIRTUAL(box, at, Direction.WEST,  STRAIGHT_SPAN_LIMIT, baseAir, ops);
            s.aR = spanToWall_VIRTUAL(box, at, Direction.EAST,  STRAIGHT_SPAN_LIMIT, baseAir, ops);
            s.bL = spanToWall_VIRTUAL(box, at, Direction.DOWN,  STRAIGHT_SPAN_LIMIT, baseAir, ops);
            s.bR = spanToWall_VIRTUAL(box, at, Direction.UP,    STRAIGHT_SPAN_LIMIT, baseAir, ops);
        } else {
            s.aL = spanToWall_VIRTUAL(box, at, Direction.WEST,  STRAIGHT_SPAN_LIMIT, baseAir, ops);
            s.aR = spanToWall_VIRTUAL(box, at, Direction.EAST,  STRAIGHT_SPAN_LIMIT, baseAir, ops);
            s.bL = spanToWall_VIRTUAL(box, at, Direction.NORTH, STRAIGHT_SPAN_LIMIT, baseAir, ops);
            s.bR = spanToWall_VIRTUAL(box, at, Direction.SOUTH, STRAIGHT_SPAN_LIMIT, baseAir, ops);
        }
        if (s.aL < 0 || s.aR < 0 || s.bL < 0 || s.bR < 0) return null;
        s.wA = Math.max(0, s.aR - s.aL - 1);
        s.wB = Math.max(0, s.bR - s.bL - 1);
        s.cA = (s.aL + s.aR) >> 1;
        s.cB = (s.bL + s.bR) >> 1;
        return s;
    }

    private boolean isStraightAlongAxis_VIRTUAL(BoundingBox box, BlockPos anchor, Direction.Axis axis,
                                                Set<Long> baseAir, Map<Long, BlockState> ops) {
        XSec c0 = measureCrossSection_VIRTUAL(box, anchor, axis, baseAir, ops);
        if (c0 == null) return false;

        BlockPos fwd = switch (axis) {
            case X -> anchor.east();
            case Y -> anchor.above();
            case Z -> anchor.south();
        };
        XSec cF = measureCrossSection_VIRTUAL(box, fwd, axis, baseAir, ops);
        if (cF == null) return false;

        if (Math.abs(c0.wA - cF.wA) > 2) return false;
        if (Math.abs(c0.wB - cF.wB) > 2) return false;
        if (Math.abs(c0.cA - cF.cA) > 1) return false;
        if (Math.abs(c0.cB - cF.cB) > 1) return false;

        return c0.wA <= 10 && c0.wB <= 6;
    }

    private int spanToWall_VIRTUAL(BoundingBox box, BlockPos origin, Direction dir, int limit,
                                   Set<Long> baseAir, Map<Long, BlockState> ops) {
        BlockPos p = origin;
        for (int i = 0; i <= limit; i++) {
            if (!inBox(box, p)) return -1;
            if (vIsSolidWall(baseAir, ops, p)) {
                return switch (dir.getAxis()) {
                    case X -> p.getX();
                    case Y -> p.getY();
                    case Z -> p.getZ();
                };
            }
            p = p.relative(dir);
        }
        return -1;
    }

    // ====== Web helpers (virtual) ======
    private boolean tryWebMembraneAcross_VIRTUAL(BoundingBox box, RandomSource rnd, BlockPos anchor, Direction.Axis axis,
                                                 Set<Long> baseAir, Map<Long, BlockState> ops) {
        XSec s = measureCrossSection_VIRTUAL(box, anchor, axis, baseAir, ops);
        if (s == null) return false;

        int filled = 0, attempted = 0;

        if (axis == Direction.Axis.X) {
            int xMid = anchor.getX();
            int y0 = Math.max(s.bL + 1, anchor.getY());
            int y1 = Math.min(s.bR - 1, y0 + (1 + rnd.nextInt(3)));
            for (int y = y0; y <= y1; y++) {
                for (int z = s.aL + 1; z <= s.aR - 1; z++) {
                    BlockPos p = new BlockPos(xMid, y, z);
                    if (!inBox(box, p)) continue;
                    attempted++;
                    if (vPlaceIfAir(baseAir, ops, p, OLIANT_WEB.defaultBlockState())) filled++;
                }
            }
        } else if (axis == Direction.Axis.Z) {
            int zMid = anchor.getZ();
            int y0 = Math.max(s.bL + 1, anchor.getY());
            int y1 = Math.min(s.bR - 1, y0 + (1 + rnd.nextInt(3)));
            for (int y = y0; y <= y1; y++) {
                for (int x = s.aL + 1; x <= s.aR - 1; x++) {
                    BlockPos p = new BlockPos(x, y, zMid);
                    if (!inBox(box, p)) continue;
                    attempted++;
                    if (vPlaceIfAir(baseAir, ops, p, OLIANT_WEB.defaultBlockState())) filled++;
                }
            }
        } else {
            int yMid = anchor.getY();
            for (int z = s.bL + 1; z <= s.bR - 1; z++) {
                for (int x = s.aL + 1; x <= s.aR - 1; x++) {
                    BlockPos p = new BlockPos(x, yMid, z);
                    if (!inBox(box, p)) continue;
                    attempted++;
                    if (vPlaceIfAir(baseAir, ops, p, OLIANT_WEB.defaultBlockState())) filled++;
                }
            }
        }
        return filled >= Math.max(3, attempted / 3);
    }

    private boolean tryWebDiagonalAcross_VIRTUAL(BoundingBox box, RandomSource rnd, BlockPos anchor, Direction.Axis axis,
                                                 Set<Long> baseAir, Map<Long, BlockState> ops) {
        XSec s = measureCrossSection_VIRTUAL(box, anchor, axis, baseAir, ops);
        if (s == null) return false;

        ArrayList<BlockPos> line = new ArrayList<>();

        if (axis == Direction.Axis.X) {
            BlockPos a = new BlockPos(anchor.getX(), s.bL + 1, s.aL + 1);
            BlockPos b = new BlockPos(anchor.getX(), s.bR - 1, s.aR - 1);
            for (BlockPos p : rasterLine(a, b)) line.add(p);
        } else if (axis == Direction.Axis.Z) {
            BlockPos a = new BlockPos(s.aL + 1, s.bL + 1, anchor.getZ());
            BlockPos b = new BlockPos(s.aR - 1, s.bR - 1, anchor.getZ());
            for (BlockPos p : rasterLine(a, b)) line.add(p);
        } else {
            BlockPos a = new BlockPos(s.aL + 1, anchor.getY(), s.bL + 1);
            BlockPos b = new BlockPos(s.aR - 1, anchor.getY(), s.bR - 1);
            for (BlockPos p : rasterLine(a, b)) line.add(p);
        }

        int placed = 0;
        for (BlockPos p : line) {
            if (!inBox(box, p)) continue;
            if (rnd.nextFloat() < 0.9f) {
                if (vPlaceIfAir(baseAir, ops, p, OLIANT_WEB.defaultBlockState())) placed++;
                if (rnd.nextFloat() < 0.25f) {
                    BlockPos q = p.relative(rnd.nextBoolean() ? Direction.UP : Direction.DOWN);
                    if (inBox(box, q)) vPlaceIfAir(baseAir, ops, q, OLIANT_WEB.defaultBlockState());
                }
            }
        }
        return placed >= 4;
    }

    private boolean tryWebCornerClump_VIRTUAL(BoundingBox box, RandomSource rnd, BlockPos anchor,
                                              Set<Long> baseAir, Map<Long, BlockState> ops) {
        Direction[][] pairs = new Direction[][]{
                {Direction.NORTH, Direction.WEST},
                {Direction.NORTH, Direction.EAST},
                {Direction.SOUTH, Direction.WEST},
                {Direction.SOUTH, Direction.EAST}
        };
        Collections.shuffle(Arrays.asList(pairs), new java.util.Random(rnd.nextLong()));

        for (Direction[] pair : pairs) {
            BlockPos w1 = anchor.relative(pair[0]);
            BlockPos w2 = anchor.relative(pair[1]);
            if (!inBox(box, w1) || !inBox(box, w2)) continue;
            if (!vIsSolidWall(baseAir, ops, w1) || !vIsSolidWall(baseAir, ops, w2)) continue;

            int placed = 0;
            for (int dx = 0; dx <= 1; dx++) {
                for (int dy = 0; dy <= 1; dy++) {
                    BlockPos p = anchor.above(dy).relative(pair[0].getOpposite(), dx).relative(pair[1].getOpposite(), dx);
                    if (!inBox(box, p)) continue;
                    if (rnd.nextFloat() < 0.85f) {
                        if (vPlaceIfAir(baseAir, ops, p, OLIANT_WEB.defaultBlockState())) placed++;
                    }
                }
            }
            for (Direction d : new Direction[]{pair[0].getOpposite(), pair[1].getOpposite(), Direction.UP}) {
                BlockPos q = anchor.relative(d);
                if (inBox(box, q)) vPlaceIfAir(baseAir, ops, q, OLIANT_WEB.defaultBlockState());
            }
            return placed >= 2;
        }
        return false;
    }

    // ====== Virtual room-logic helpers ======
    private static final Direction[] LATERALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    /** True if filling this air cell would choke the corridor (needs >=2 lateral air neighbors to avoid choking). */
    private boolean wouldChokeIfFilled_VIRTUAL(Set<Long> baseAir, Map<Long, BlockState> ops, BlockPos airCell) {
        int lateralAir = 0;
        for (Direction d : LATERALS) {
            BlockPos n = airCell.relative(d);
            if (viewAt(baseAir, ops, n).isAir()) {
                lateralAir++;
                if (lateralAir > 1) return false; // fast path: won’t choke
            }
        }
        return lateralAir <= 1;
    }

    private boolean isLikelyRoomVoid_VIRTUAL(Set<Long> baseAir, Map<Long, BlockState> ops, BlockPos p) {
        int airCount = 0;
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    m.set(p.getX() + dx, p.getY() + dy, p.getZ() + dz);
                    if (viewAt(baseAir, ops, m).isAir()) airCount++;
                }
            }
        }
        return airCount >= 20;
    }

    // ====== Misc helpers ======
    /** Deterministic noise field derived solely from the piece's bounding box (no RNG consumed). */
    private PerlinNoise makeNoiseForPiece(BoundingBox box) {
        long seed = 0x9E3779B97F4A7C15L;
        seed ^= (long) box.minX() * 0xBF58476D1CE4E5B9L;
        seed ^= (long) box.minY() * 0x94D049BB133111EBL;
        seed ^= (long) box.minZ() * 0x632BE59BD9B4E019L;
        seed ^= (long) box.maxX() * 0xAEF17502108EF2D9L;
        seed ^= (long) box.maxY() * 0xD1B54A32D192ED03L;
        seed ^= (long) box.maxZ() * 0x94D049BB133111EBL;

        RandomSource seeded = RandomSource.create(seed);
        List<Integer> octaves = List.of(-2, -1, 0);
        return PerlinNoise.create(seeded, octaves);
    }

    private double sampleNoise3D(PerlinNoise noise, double x, double y, double z, double scale) {
        return noise.getValue(x * scale, y * (scale * 0.6), z * scale);
    }

    private boolean inBox(BoundingBox box, BlockPos p) {
        return p.getX() >= box.minX() && p.getX() <= box.maxX()
                && p.getY() >= box.minY() && p.getY() <= box.maxY()
                && p.getZ() >= box.minZ() && p.getZ() <= box.maxZ();
    }

    private Iterable<BlockPos> rasterLine(BlockPos a, BlockPos b) {
        ArrayList<BlockPos> out = new ArrayList<>();
        int x1 = a.getX(), y1 = a.getY(), z1 = a.getZ();
        int x2 = b.getX(), y2 = b.getY(), z2 = b.getZ();
        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1), dz = Math.abs(z2 - z1);
        int n = 1 + dx + dy + dz;
        int x_inc = (x2 > x1) ? 1 : -1;
        int y_inc = (y2 > y1) ? 1 : -1;
        int z_inc = (z2 > z1) ? 1 : -1;
        int err_1 = dx - dy;
        int err_2 = dx - dz;
        int x = x1, y = y1, z = z1;

        for (; n > 0; --n) {
            out.add(new BlockPos(x, y, z));
            int e1 = err_1 << 1;
            int e2 = err_2 << 1;
            if (e1 > -dy) { err_1 -= dy; x += x_inc; }
            if (e1 <  dx) { err_1 += dx; y += y_inc; }
            if (e2 > -dz) { err_2 -= dz; x += x_inc; }
            if (e2 <  dx) { err_2 += dx; z += z_inc; }
        }
        return out;
    }

    private static BoundingBox computeBoundingBox(Bitmask mask) {
        final int[] minX = {Integer.MAX_VALUE};
        final int[] minY = {Integer.MAX_VALUE};
        final int[] minZ = {Integer.MAX_VALUE};
        final int[] maxX = {Integer.MIN_VALUE};
        final int[] maxY = {Integer.MIN_VALUE};
        final int[] maxZ = {Integer.MIN_VALUE};
        final int[] empty = new int[1];
        mask.forEachLong(packed -> {
            empty[0] = 1;
            int x = BlockPos.getX(packed);
            int y = BlockPos.getY(packed);
            int z = BlockPos.getZ(packed);
            if (x < minX[0]) minX[0] = x; if (y < minY[0]) minY[0] = y; if (z < minZ[0]) minZ[0] = z;
            if (x > maxX[0]) maxX[0] = x; if (y > maxY[0]) maxY[0] = y; if (z > maxZ[0]) maxZ[0] = z;
        });
        if (empty[0] == 0) {
            return new BoundingBox(0, 0, 0, 0, 0, 0);
        }
        return new BoundingBox(minX[0], minY[0], minZ[0], maxX[0], maxY[0], maxZ[0]);
    }

    private static List<Long> toList(Bitmask mask) {
        ArrayList<Long> out = new ArrayList<>(mask.size());
        mask.forEachLong(out::add);
        return out;
    }
}