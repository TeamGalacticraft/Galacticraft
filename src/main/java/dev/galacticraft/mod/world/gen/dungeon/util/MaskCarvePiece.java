package dev.galacticraft.mod.world.gen.dungeon.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;

import java.util.ArrayList;
import java.util.List;

/**
 * Carves semi-natural antlike tunnels from a voxel mask used as a centerline.
 * Pass 1: jittered spherical widening around each mask voxel (organic radius).
 * Pass 2: coat exposed tunnel walls with dirt.
 * <br>
 * <br>
 * Serialization: stores only the centerline voxels (as before) + a few floats/ints for knobs.
 */
public class MaskCarvePiece extends StructurePiece {
    public static StructurePieceType TYPE;

    // Mean tunnel radius in blocks (carved as spheres around each center voxel).
    private float meanRadius = 1.25f;

    // Max +/- random jitter added to radius at each voxel (uniform). (e.g., 0.6 => 1.25±0.6)
    private float radiusJitter = 0.6f;

    // How "bumpy" the wall is: extra sub-voxel noise inflation (0..1). 0 = smooth spheres.
    private float roughness = 0.25f;

    // Chance per-mask-voxel to create a small "bulge" (gallery); set to 0 for none.
    private float bulgeChance = 0.07f;

    // Extra radius added when bulge triggers (randomized up to this).
    private float maxBulgeExtra = 1.75f;

    // Limit coating to these many blocks thick (normally 1 ring is enough).
    private int coatThickness = 1;

    private List<Long> voxels; // packed BlockPos.asLong()

    private static final int STRAIGHT_SPAN_LIMIT = 12;  // how far to ray to find walls

    private static final Block OLIANT_NEST_BLOCK = Blocks.COARSE_DIRT;
    private static final Block OLIANT_FERTILE_NEST_BLOCK = Blocks.MOSS_BLOCK;
    private static final Block OLIANT_DISSOLVED_NEST_BLOCK = Blocks.MUD;
    private static final Block OLIANT_ACID_BLOCK = Blocks.WATER;
    private static final Block OLIANT_WEB = Blocks.COBWEB;

    public MaskCarvePiece(Bitmask mask) {
        super(TYPE, 0, computeBoundingBox(mask));
        this.voxels = toList(mask);
    }

    private static final class XSec {
        // for axis X we measure YZ plane; for axis Z we measure XY; for axis Y we measure XZ
        int aL, aR;   // first perpendicular axis walls (e.g., Z-)
        int bL, bR;   // second perpendicular axis walls (e.g., Y-)
        int wA, wB;   // interior widths along those axes
        int cA, cB;   // centers along those axes
    }

    private static final class Spans {
        int xL, xR, zL, zR, yD, yU; // wall coordinates
        int wX, wZ, hY;
    }

    private XSec measureCrossSection(WorldGenLevel level, BoundingBox box, BlockPos at, Direction.Axis axis) {
        XSec s = new XSec();
        if (axis == Direction.Axis.X) {
            // plane = YZ
            s.aL = spanToWall(level, box, at, Direction.NORTH, STRAIGHT_SPAN_LIMIT);
            s.aR = spanToWall(level, box, at, Direction.SOUTH, STRAIGHT_SPAN_LIMIT);
            s.bL = spanToWall(level, box, at, Direction.DOWN,  STRAIGHT_SPAN_LIMIT);
            s.bR = spanToWall(level, box, at, Direction.UP,    STRAIGHT_SPAN_LIMIT);
            if (s.aL < 0 || s.aR < 0 || s.bL < 0 || s.bR < 0) return null;
            s.wA = Math.max(0, s.aR - s.aL - 1);
            s.wB = Math.max(0, s.bR - s.bL - 1);
            s.cA = (s.aL + s.aR) >> 1;
            s.cB = (s.bL + s.bR) >> 1;
        } else if (axis == Direction.Axis.Z) {
            // plane = XY
            s.aL = spanToWall(level, box, at, Direction.WEST,  STRAIGHT_SPAN_LIMIT);
            s.aR = spanToWall(level, box, at, Direction.EAST,  STRAIGHT_SPAN_LIMIT);
            s.bL = spanToWall(level, box, at, Direction.DOWN,  STRAIGHT_SPAN_LIMIT);
            s.bR = spanToWall(level, box, at, Direction.UP,    STRAIGHT_SPAN_LIMIT);
            if (s.aL < 0 || s.aR < 0 || s.bL < 0 || s.bR < 0) return null;
            s.wA = Math.max(0, s.aR - s.aL - 1);
            s.wB = Math.max(0, s.bR - s.bL - 1);
            s.cA = (s.aL + s.aR) >> 1;
            s.cB = (s.bL + s.bR) >> 1;
        } else { // Y (vertical)
            // plane = XZ
            s.aL = spanToWall(level, box, at, Direction.WEST,  STRAIGHT_SPAN_LIMIT);
            s.aR = spanToWall(level, box, at, Direction.EAST,  STRAIGHT_SPAN_LIMIT);
            s.bL = spanToWall(level, box, at, Direction.NORTH, STRAIGHT_SPAN_LIMIT);
            s.bR = spanToWall(level, box, at, Direction.SOUTH, STRAIGHT_SPAN_LIMIT);
            if (s.aL < 0 || s.aR < 0 || s.bL < 0 || s.bR < 0) return null;
            s.wA = Math.max(0, s.aR - s.aL - 1);
            s.wB = Math.max(0, s.bR - s.bL - 1);
            s.cA = (s.aL + s.aR) >> 1;
            s.cB = (s.bL + s.bR) >> 1;
        }
        return s;
    }

    /** Is the tunnel locally straight along the given axis? We compare cross-sections ahead/behind. */
    private boolean isStraightAlongAxis(WorldGenLevel level, BoundingBox box, BlockPos anchor, Direction.Axis axis) {
        XSec c0 = measureCrossSection(level, box, anchor, axis);
        if (c0 == null) return false;

        BlockPos fwd = switch (axis) {
            case X -> anchor.east();
            case Y -> anchor.above();
            case Z -> anchor.south();
        };
        XSec cF = measureCrossSection(level, box, fwd, axis);
        if (cF == null) return false;

        // tolerate small changes
        if (Math.abs(c0.wA - cF.wA) > 2) return false;
        if (Math.abs(c0.wB - cF.wB) > 2) return false;
        if (Math.abs(c0.cA - cF.cA) > 1) return false;
        if (Math.abs(c0.cB - cF.cB) > 1) return false;

        // avoid big room cross-sections
        return c0.wA <= 10 && c0.wB <= 6;
    }

    /** Optional ctor with parameters if we want per-dungeon control. */
    public MaskCarvePiece(Bitmask mask,
                          float meanRadius, float radiusJitter, float roughness,
                          float bulgeChance, float maxBulgeExtra,
                          int coatThickness, Block wallCoat) {
        super(TYPE, 0, computeBoundingBox(mask));
        this.voxels = toList(mask);
        this.meanRadius = meanRadius;
        this.radiusJitter = radiusJitter;
        this.roughness = roughness;
        this.bulgeChance = bulgeChance;
        this.maxBulgeExtra = maxBulgeExtra;
        this.coatThickness = coatThickness;
    }

    public MaskCarvePiece(StructurePieceSerializationContext ctx, CompoundTag tag) {
        super(TYPE, tag);
        long[] arr = tag.getLongArray("vox");
        this.voxels = new ArrayList<>(arr.length);
        for (long v : arr) this.voxels.add(v);

        // knobs (defaults if absent for backwards compat)
        this.meanRadius = tag.contains("meanR") ? tag.getFloat("meanR") : this.meanRadius;
        this.radiusJitter = tag.contains("jitR") ? tag.getFloat("jitR") : this.radiusJitter;
        this.roughness = tag.contains("rough") ? tag.getFloat("rough") : this.roughness;
        this.bulgeChance = tag.contains("bulgeP") ? tag.getFloat("bulgeP") : this.bulgeChance;
        this.maxBulgeExtra = tag.contains("bulgeMax") ? tag.getFloat("bulgeMax") : this.maxBulgeExtra;
        this.coatThickness = tag.contains("coatT") ? tag.getInt("coatT") : this.coatThickness;
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
        long[] arr = new long[voxels.size()];
        for (int i = 0; i < voxels.size(); i++) arr[i] = voxels.get(i);
        tag.putLongArray("vox", arr);

        tag.putFloat("meanR", this.meanRadius);
        tag.putFloat("jitR", this.radiusJitter);
        tag.putFloat("rough", this.roughness);
        tag.putFloat("bulgeP", this.bulgeChance);
        tag.putFloat("bulgeMax", this.maxBulgeExtra);
        tag.putInt("coatT", this.coatThickness);
    }

    @Override
    public void postProcess(WorldGenLevel level,
                            net.minecraft.world.level.StructureManager structureManager,
                            net.minecraft.world.level.chunk.ChunkGenerator chunkGenerator,
                            net.minecraft.util.RandomSource random,
                            BoundingBox box,
                            net.minecraft.world.level.ChunkPos chunkPos,
                            BlockPos pivot) {
        if (voxels == null || voxels.isEmpty()) return;

        final int minY = level.getMinBuildHeight();
        final int maxY = level.getMaxBuildHeight() - 1;

        // Deterministic noise for this CHUNK call
        PerlinNoise noise = makeNoiseForPiece(random, box, chunkPos);

        // Phase 0: carve exactly the mask
        java.util.HashSet<Long> airMask = new java.util.HashSet<>(Math.max(16, voxels.size() * 2));
        for (long packed : voxels) {
            int x = BlockPos.getX(packed);
            int y = BlockPos.getY(packed);
            int z = BlockPos.getZ(packed);
            if (y < minY || y > maxY) continue;
            BlockPos p = new BlockPos(x, y, z);

            // BUG: was `this.boundingBox.isInside(p)` — hits unloaded chunks
            if (!box.isInside(p)) continue;

            if (!level.getBlockState(p).isAir()) {
                level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
            }
            airMask.add(packed);
        }

        // Phase 1: noise-driven +-1 push/pull (CLIPPED)
        applyNoisePushPull(level, box, chunkPos, random, noise, airMask, voxels, minY, maxY);

        // Phase 2: coat exposed surfaces (CLIPPED)
        coatAllExposedSurfaces(level, box, chunkPos, random, airMask);

        // Phase 2.5: acid pockets (CLIPPED)
        placeAcidPockets(level, box, chunkPos, random, airMask);

        // Phase 3: webs (CLIPPED)
        placeWebs(level, box, chunkPos, random, voxels);
    }

// ---------- helpers ----------

    private void applyNoisePushPull(WorldGenLevel level,
                                    BoundingBox box,
                                    net.minecraft.world.level.ChunkPos chunkPos,
                                    RandomSource rnd,
                                    PerlinNoise noise,
                                    java.util.Set<Long> airMask,
                                    List<Long> centers,
                                    int minY, int maxY) {
        final double scale = 1.0 / 6.0;
        final double pushThresh =  0.35;
        final double pullThresh = -0.35;

        final Direction[] laterals = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
        final Direction[] allDirs  = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN};

        java.util.ArrayList<BlockPos> addAir = new java.util.ArrayList<>();
        java.util.ArrayList<BlockPos> addWall = new java.util.ArrayList<>();

        for (long packed : centers) {
            int x = BlockPos.getX(packed);
            int y = BlockPos.getY(packed);
            int z = BlockPos.getZ(packed);
            if (y < minY || y > maxY) continue;
            if ((x >> 4) != chunkPos.x || (z >> 4) != chunkPos.z) continue;

            BlockPos center = new BlockPos(x, y, z);
            if (!box.isInside(center)) continue;
            if (!level.getBlockState(center).isAir()) continue;

            double n = sampleNoise3D(noise, x, y, z, scale);

            if (n > pushThresh) {
                Direction[] order = rnd.nextFloat() < 0.85f ? laterals : allDirs;
                for (Direction d : order) {
                    BlockPos tgt = center.relative(d);
                    if (!box.isInside(tgt)) continue;
                    if ((tgt.getX() >> 4) != chunkPos.x || (tgt.getZ() >> 4) != chunkPos.z) continue;
                    if (tgt.getY() < minY || tgt.getY() > maxY) continue;

                    BlockPos behind = tgt.relative(d);
                    if (!box.isInside(behind)) continue;
                    if ((behind.getX() >> 4) != chunkPos.x || (behind.getZ() >> 4) != chunkPos.z) continue;

                    if (!isSolidWall(level, tgt) || !isSolidWall(level, behind)) continue;
                    if (isLikelyRoomVoid(level, behind)) continue;

                    addAir.add(tgt);
                    break;
                }
            } else if (n < pullThresh) {
                Direction[] order = rnd.nextFloat() < 0.85f ? laterals : allDirs;
                for (Direction d : order) {
                    BlockPos a = center.relative(d);
                    if (!box.isInside(a)) continue;
                    if ((a.getX() >> 4) != chunkPos.x || (a.getZ() >> 4) != chunkPos.z) continue;

                    if (!level.getBlockState(a).isAir()) continue;
                    long al = a.asLong();
                    if (airMask.contains(al)) continue;

                    BlockPos back = a.relative(d);
                    if (!box.isInside(back)) continue;
                    if ((back.getX() >> 4) != chunkPos.x || (back.getZ() >> 4) != chunkPos.z) continue;

                    if (!isSolidWall(level, back)) continue;
                    if (!wouldChokeIfFilled(level, a)) {
                        addWall.add(a);
                        break;
                    }
                }
            }
        }

        for (BlockPos p : addAir) {
            level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
            airMask.add(p.asLong());
        }
        for (BlockPos p : addWall) {
            level.setBlock(p, OLIANT_NEST_BLOCK.defaultBlockState(), 2);
            airMask.remove(p.asLong());
        }
    }

    /**
     * Scan all floor cells (air with solid block directly below) and flood-fill
     * 4-connected components at the same Y. If a component is fully enclosed
     * (no adjacent air/fluid outside the component) and size < 10, fill with acid
     * and set the blocks beneath to dissolved nest. This prevents leaks even on irregular floors.
     */
    private void placeAcidPockets(WorldGenLevel level,
                                  BoundingBox box,
                                  ChunkPos chunkPos,
                                  RandomSource rnd,
                                  java.util.Set<Long> airMask) {
        if (airMask == null || airMask.isEmpty()) return;

        final int MAX_SIZE = 9;

        java.util.HashSet<Long> globalVisited = new java.util.HashSet<>();
        java.util.ArrayDeque<BlockPos> q = new java.util.ArrayDeque<>();

        for (long al : airMask) {
            if (globalVisited.contains(al)) continue;

            BlockPos start = BlockPos.of(al);
            if (!box.isInside(start)) { globalVisited.add(al); continue; }
            if ((start.getX() >> 4) != chunkPos.x || (start.getZ() >> 4) != chunkPos.z) { globalVisited.add(al); continue; }
            if (!level.getBlockState(start).isAir()) { globalVisited.add(al); continue; }

            BlockPos startBelow = start.below();
            if (!box.isInside(startBelow)) { globalVisited.add(al); continue; }
            if ((startBelow.getX() >> 4) != chunkPos.x || (startBelow.getZ() >> 4) != chunkPos.z) { globalVisited.add(al); continue; }
            if (!isSolidWall(level, startBelow)) { globalVisited.add(al); continue; }

            java.util.ArrayList<BlockPos> comp = new java.util.ArrayList<>(8);
            java.util.HashSet<Long> compSet = new java.util.HashSet<>();
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
                    if ((n.getX() >> 4) != chunkPos.x || (n.getZ() >> 4) != chunkPos.z) continue;
                    if (n.getY() != y0) continue;

                    var st = level.getBlockState(n);
                    if (!st.isAir()) continue;

                    BlockPos nb = n.below();
                    if (!box.isInside(nb)) continue;
                    if ((nb.getX() >> 4) != chunkPos.x || (nb.getZ() >> 4) != chunkPos.z) continue;
                    if (!isSolidWall(level, nb)) continue;

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
                    if ((n.getX() >> 4) != chunkPos.x || (n.getZ() >> 4) != chunkPos.z) { leaks = true; break; }
                    var st = level.getBlockState(n);
                    if (!st.getFluidState().isEmpty()) { leaks = true; break; }
                    if (st.isAir() && !compSet.contains(n.asLong())) { leaks = true; break; }
                }
                if (leaks) break;
            }
            if (leaks) continue;

            for (BlockPos p : comp) level.setBlock(p.below(), OLIANT_DISSOLVED_NEST_BLOCK.defaultBlockState(), 2);
            for (BlockPos p : comp) level.setBlock(p, OLIANT_ACID_BLOCK.defaultBlockState(), 2);

            coatRingAround(level, box, comp, rnd);
        }
    }

    private void coatRingAround(WorldGenLevel level, BoundingBox box, java.util.List<BlockPos> cells, RandomSource rnd) {
        for (BlockPos w : cells) {
            for (Direction d : Direction.values()) {
                BlockPos n = w.relative(d);
                if (!inBox(box, n)) continue;
                var st = level.getBlockState(n);
                if (!st.isAir() && st.getFluidState().isEmpty() && level.getBlockEntity(n) == null) {
                    Block pick;
                    if (d == Direction.DOWN) {
                        pick = OLIANT_DISSOLVED_NEST_BLOCK;
                    } else if (d == Direction.UP) {
                        pick = (rnd.nextFloat() < 0.10f) ? OLIANT_FERTILE_NEST_BLOCK : OLIANT_NEST_BLOCK;
                    } else {
                        pick = (rnd.nextFloat() < 0.12f) ? OLIANT_FERTILE_NEST_BLOCK : OLIANT_NEST_BLOCK;
                    }
                    level.setBlock(n, pick.defaultBlockState(), 2);
                }
            }
        }
    }

    private static boolean isWithin(BoundingBox box, int x, int y, int z) {
        return x >= box.minX() && x <= box.maxX()
                && y >= box.minY() && y <= box.maxY()
                && z >= box.minZ() && z <= box.maxZ();
    }

    /** Returns true if filling this air cell would choke the corridor (we require >=2 lateral air neighbors). */
    private static boolean wouldChokeIfFilled(WorldGenLevel level, BlockPos airCell) {
        int lateralAir = 0;
        for (Direction d : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
            if (level.getBlockState(airCell.relative(d)).isAir()) lateralAir++;
        }
        return lateralAir <= 1;
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

    private void placeWebs(WorldGenLevel level, BoundingBox box, ChunkPos chunkPos, RandomSource rnd, List<Long> centers) {
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
            if ((anchor.getX() >> 4) != chunkPos.x || (anchor.getZ() >> 4) != chunkPos.z) continue;
            if (!isAir(level, anchor)) continue;

            boolean straightX = isStraightAlongAxis(level, box, anchor, Direction.Axis.X);
            boolean straightY = isStraightAlongAxis(level, box, anchor, Direction.Axis.Y);
            boolean straightZ = isStraightAlongAxis(level, box, anchor, Direction.Axis.Z);

            float r = rnd.nextFloat();

            if (!(straightX || straightY || straightZ)) {
                if (rnd.nextFloat() < cornerP) tryWebCornerClump(level, box, rnd, anchor);
                if (rnd.nextFloat() < singleP) placeIfAir(level, anchor, OLIANT_WEB.defaultBlockState());
                continue;
            }

            if (r < membraneP) {
                java.util.ArrayList<Direction.Axis> axes = new java.util.ArrayList<>(3);
                if (straightX) axes.add(Direction.Axis.X);
                if (straightY) axes.add(Direction.Axis.Y);
                if (straightZ) axes.add(Direction.Axis.Z);
                Direction.Axis axis = axes.get(rnd.nextInt(axes.size()));
                if (tryWebMembraneAcross(level, box, rnd, anchor, axis)) continue;
            } else if (r < membraneP + diagonalP) {
                java.util.ArrayList<Direction.Axis> axes = new java.util.ArrayList<>(3);
                if (straightX) axes.add(Direction.Axis.X);
                if (straightY) axes.add(Direction.Axis.Y);
                if (straightZ) axes.add(Direction.Axis.Z);
                Direction.Axis axis = axes.get(rnd.nextInt(axes.size()));
                if (tryWebDiagonalAcross(level, box, rnd, anchor, axis)) continue;
            } else if (r < membraneP + diagonalP + cornerP) {
                if (tryWebCornerClump(level, box, rnd, anchor)) continue;
            }

            if (rnd.nextFloat() < singleP) {
                placeIfAir(level, anchor, OLIANT_WEB.defaultBlockState());
            }
        }
    }

    private boolean tryWebMembraneAcross(WorldGenLevel level, BoundingBox box, RandomSource rnd, BlockPos anchor, Direction.Axis axis) {
        XSec s = measureCrossSection(level, box, anchor, axis);
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
                    if (placeIfAir(level, p, OLIANT_WEB.defaultBlockState())) filled++;
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
                    if (placeIfAir(level, p, OLIANT_WEB.defaultBlockState())) filled++;
                }
            }
        } else {
            int yMid = anchor.getY();
            for (int z = s.bL + 1; z <= s.bR - 1; z++) {
                for (int x = s.aL + 1; x <= s.aR - 1; x++) {
                    BlockPos p = new BlockPos(x, yMid, z);
                    if (!inBox(box, p)) continue;
                    attempted++;
                    if (placeIfAir(level, p, OLIANT_WEB.defaultBlockState())) filled++;
                }
            }
        }
        return filled >= Math.max(3, attempted / 3);
    }

    private boolean tryWebDiagonalAcross(WorldGenLevel level, BoundingBox box, RandomSource rnd, BlockPos anchor, Direction.Axis axis) {
        XSec s = measureCrossSection(level, box, anchor, axis);
        if (s == null) return false;

        java.util.ArrayList<BlockPos> line = new java.util.ArrayList<>();

        if (axis == Direction.Axis.X) {
            // YZ plane
            BlockPos a = new BlockPos(anchor.getX(), s.bL + 1, s.aL + 1);
            BlockPos b = new BlockPos(anchor.getX(), s.bR - 1, s.aR - 1);
            for (BlockPos p : rasterLine(a, b)) line.add(p);
        } else if (axis == Direction.Axis.Z) {
            // XY plane
            BlockPos a = new BlockPos(s.aL + 1, s.bL + 1, anchor.getZ());
            BlockPos b = new BlockPos(s.aR - 1, s.bR - 1, anchor.getZ());
            for (BlockPos p : rasterLine(a, b)) line.add(p);
        } else { // Y: XZ plane
            BlockPos a = new BlockPos(s.aL + 1, anchor.getY(), s.bL + 1);
            BlockPos b = new BlockPos(s.aR - 1, anchor.getY(), s.bR - 1);
            for (BlockPos p : rasterLine(a, b)) line.add(p);
        }

        int placed = 0;
        for (BlockPos p : line) {
            if (!inBox(box, p)) continue;
            if (rnd.nextFloat() < 0.9f) {
                if (placeIfAir(level, p, OLIANT_WEB.defaultBlockState())) placed++;
                if (rnd.nextFloat() < 0.25f) {
                    BlockPos q = p.relative(rnd.nextBoolean() ? Direction.UP : Direction.DOWN);
                    if (inBox(box, q)) placeIfAir(level, q, OLIANT_WEB.defaultBlockState());
                }
            }
        }
        return placed >= 4;
    }

    private PerlinNoise makeNoiseForPiece(RandomSource rnd, BoundingBox box, net.minecraft.world.level.ChunkPos chunkPos) {
        long seed = 0x9E3779B97F4A7C15L;
        seed ^= (long) box.minX() * 0xBF58476D1CE4E5B9L;
        seed ^= (long) box.minY() * 0x94D049BB133111EBL;
        seed ^= (long) box.minZ() * 0x632BE59BD9B4E019L;
        seed ^= ((long) chunkPos.x) * 0xAEF17502108EF2D9L;
        seed ^= ((long) chunkPos.z) * 0xD1B54A32D192ED03L;

        RandomSource seeded = RandomSource.create(seed);
        java.util.List<Integer> octaves = java.util.List.of(-2, -1, 0);
        return PerlinNoise.create(seeded, octaves);
    }

    private double sampleNoise3D(PerlinNoise noise, double x, double y, double z, double scale) {
        return noise.getValue(x * scale, y * (scale * 0.6), z * scale);
    }

    private boolean isAir(WorldGenLevel level, BlockPos p) {
        return level.getBlockState(p).isAir();
    }
    private boolean isSolidWall(WorldGenLevel level, BlockPos p) {
        var st = level.getBlockState(p);
        return !st.isAir() && st.getFluidState().isEmpty() && level.getBlockEntity(p) == null;
    }
    private boolean placeIfAir(WorldGenLevel level, BlockPos p, net.minecraft.world.level.block.state.BlockState st) {
        if (level.getBlockState(p).isAir()) {
            level.setBlock(p, st, 2);
            return true;
        }
        return false;
    }

    private int spanToWall(WorldGenLevel level, BoundingBox box, BlockPos origin, Direction dir, int limit) {
        BlockPos p = origin;
        for (int i = 0; i <= limit; i++) {
            if (!inBox(box, p)) return -1;
            if (isSolidWall(level, p)) {
                if (dir.getAxis() == Direction.Axis.X) return p.getX();
                if (dir.getAxis() == Direction.Axis.Z) return p.getZ();
                if (dir.getAxis() == Direction.Axis.Y) return p.getY();
                return -1;
            }
            p = p.relative(dir);
        }
        return -1;
    }



    private void coatAllExposedSurfaces(WorldGenLevel level, BoundingBox box, ChunkPos chunkPos, RandomSource rnd, java.util.Set<Long> airMask) {
        final float floorFertileP = 0.18f;
        final float floorDissolvedP = 0.12f;
        final float wallFertileP  = 0.12f;
        final float roofFertileP  = 0.10f;

        BlockPos.MutableBlockPos air = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos nb  = new BlockPos.MutableBlockPos();

        for (long a : airMask) {
            int ax = BlockPos.getX(a), ay = BlockPos.getY(a), az = BlockPos.getZ(a);
            if ((ax >> 4) != chunkPos.x || (az >> 4) != chunkPos.z) continue;

            air.set(ax, ay, az);
            if (!box.isInside(air) || !level.getBlockState(air).isAir()) continue;

            // Floor
            nb.set(air).move(Direction.DOWN);
            if (box.isInside(nb) && (nb.getX() >> 4) == chunkPos.x && (nb.getZ() >> 4) == chunkPos.z && isSolidWall(level, nb)) {
                var floorPick = OLIANT_NEST_BLOCK;
                float r = rnd.nextFloat();
                if (r < floorDissolvedP) floorPick = OLIANT_DISSOLVED_NEST_BLOCK;
                else if (r < floorDissolvedP + floorFertileP) floorPick = OLIANT_FERTILE_NEST_BLOCK;
                level.setBlock(nb, floorPick.defaultBlockState(), 2);
            }

            // Roof
            nb.set(air).move(Direction.UP);
            if (box.isInside(nb) && (nb.getX() >> 4) == chunkPos.x && (nb.getZ() >> 4) == chunkPos.z && isSolidWall(level, nb)) {
                var roofPick = (rnd.nextFloat() < roofFertileP) ? OLIANT_FERTILE_NEST_BLOCK : OLIANT_NEST_BLOCK;
                level.setBlock(nb, roofPick.defaultBlockState(), 2);
            }

            // Walls
            for (Direction d : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
                nb.set(air).move(d);
                if (box.isInside(nb) && (nb.getX() >> 4) == chunkPos.x && (nb.getZ() >> 4) == chunkPos.z && isSolidWall(level, nb)) {
                    var wallPick = (rnd.nextFloat() < wallFertileP) ? OLIANT_FERTILE_NEST_BLOCK : OLIANT_NEST_BLOCK;
                    level.setBlock(nb, wallPick.defaultBlockState(), 2);
                }
            }
        }
    }

    private boolean inBox(BoundingBox box, BlockPos p) {
        return p.getX() >= box.minX() && p.getX() <= box.maxX()
                && p.getY() >= box.minY() && p.getY() <= box.maxY()
                && p.getZ() >= box.minZ() && p.getZ() <= box.maxZ();
    }

    private boolean isLikelyRoomVoid(WorldGenLevel level, BlockPos p) {
        int airCount = 0;
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    m.set(p.getX() + dx, p.getY() + dy, p.getZ() + dz);
                    if (level.getBlockState(m).isAir()) airCount++;
                }
            }
        }
        return airCount >= 20;
    }

    private java.lang.Iterable<BlockPos> rasterLine(BlockPos a, BlockPos b) {
        java.util.ArrayList<BlockPos> out = new java.util.ArrayList<>();
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

    /** Web clump tucked into a corner (needs two orthogonal walls). */
    private boolean tryWebCornerClump(WorldGenLevel level, BoundingBox box, net.minecraft.util.RandomSource rnd, BlockPos anchor) {
        Direction[][] pairs = new Direction[][]{
                {Direction.NORTH, Direction.WEST},
                {Direction.NORTH, Direction.EAST},
                {Direction.SOUTH, Direction.WEST},
                {Direction.SOUTH, Direction.EAST}
        };
        java.util.Collections.shuffle(java.util.Arrays.asList(pairs), new java.util.Random(rnd.nextLong()));

        for (Direction[] pair : pairs) {
            BlockPos w1 = anchor.relative(pair[0]);
            BlockPos w2 = anchor.relative(pair[1]);
            if (!inBox(box, w1) || !inBox(box, w2)) continue;
            if (!isSolidWall(level, w1) || !isSolidWall(level, w2)) continue;

            int placed = 0;
            for (int dx = 0; dx <= 1; dx++) {
                for (int dy = 0; dy <= 1; dy++) {
                    BlockPos p = anchor.above(dy).relative(pair[0].getOpposite(), dx).relative(pair[1].getOpposite(), dx);
                    if (!inBox(box, p)) continue;
                    if (rnd.nextFloat() < 0.85f) {
                        if (placeIfAir(level, p, OLIANT_WEB.defaultBlockState())) placed++;
                    }
                }
            }
            for (Direction d : new Direction[]{pair[0].getOpposite(), pair[1].getOpposite(), Direction.UP}) {
                BlockPos q = anchor.relative(d);
                if (inBox(box, q)) placeIfAir(level, q, OLIANT_WEB.defaultBlockState());
            }
            return placed >= 2;
        }
        return false;
    }
}