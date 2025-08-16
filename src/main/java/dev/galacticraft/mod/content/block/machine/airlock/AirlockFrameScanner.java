/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.content.block.machine.airlock;

import dev.galacticraft.mod.content.block.entity.AirlockControllerBlockEntity;
import dev.galacticraft.mod.tag.GCBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * Finds the smallest possible rectangular frame(s) on each axis-aligned plane that include the controller
 * on the perimeter (not a corner). Supports up to two rectangles per plane (controller edge used by two frames),
 * and returns all planes (XY, XZ, YZ).
 */
public final class AirlockFrameScanner {

    public static final Comparator<Result> ORDER = Comparator
            .comparing((Result r) -> r.plane.ordinal())
            .thenComparingInt(r -> r.minX).thenComparingInt(r -> r.minY).thenComparingInt(r -> r.minZ)
            .thenComparingInt(r -> r.maxX).thenComparingInt(r -> r.maxY).thenComparingInt(r -> r.maxZ);

    public enum Plane {
        XY(Axis.Z), // z is constant
        XZ(Axis.Y), // y is constant
        YZ(Axis.X); // x is constant
        final Axis normal;
        Plane(Axis normal) { this.normal = normal; }
    }

    public static final class Result {
        public final Plane plane;
        public final int minX, minY, minZ, maxX, maxY, maxZ;
        public final Direction sealFacing; // FACING for AIR_LOCK_SEAL

        Result(Plane plane,
               int minX, int minY, int minZ,
               int maxX, int maxY, int maxZ,
               Direction sealFacing) {
            this.plane = plane;
            this.minX = minX; this.minY = minY; this.minZ = minZ;
            this.maxX = maxX; this.maxY = maxY; this.maxZ = maxZ;
            this.sealFacing = sealFacing;
        }
    }

    private static boolean isFrame(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(GCBlockTags.AIRLOCK_BLOCKS);
    }

    private static boolean isController(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof AirlockControllerBlockEntity;
    }

    /** Scan all planes; return up to two smallest rectangles per plane. */
    public static List<Result> scanAll(Level level, BlockPos controller) {
        if (!isFrame(level, controller)) return List.of();
        List<Result> out = new ArrayList<>(6);
        for (Plane plane : Plane.values()) {
            out.addAll(scanPlane(level, controller, plane));
        }
        out.sort(ORDER); // stable order across ticks
        return out;
    }

    /** Back-compat helper if you still call scan(): returns first found or invalid. */
    public static Result scan(Level level, BlockPos controller) {
        List<Result> all = scanAll(level, controller);
        return all.isEmpty()
                ? new Result(Plane.XY, 0,0,0, 0,0,0, Direction.NORTH)
                : all.get(0);
    }

    // ---------- Plane scanning ----------

    private record Axes(Axis u, Axis v, Axis wConst) {}

    private static List<Result> scanPlane(Level level, BlockPos controller, Plane plane) {
        final int fixed = controller.get(plane.normal);
        final Axes axes = axesFor(plane); // u,v are the in-plane axes

        // 1) Gather all frame blocks connected to controller within this plane
        Set<BlockPos> frames = floodInPlane(level, controller, plane, fixed);
        if (frames.isEmpty()) return List.of();

        // Require exactly one controller in this connected set
        int controllers = 0;
        for (BlockPos p : frames) if (isController(level, p)) controllers++;
        if (controllers != 1) return List.of();

        // 2) in-plane coords of controller
        int u0 = proj(controller, axes.u);
        int v0 = proj(controller, axes.v);

        // Search bounds (tight box around connected set)
        int minU = Integer.MAX_VALUE, maxU = Integer.MIN_VALUE, minV = Integer.MAX_VALUE, maxV = Integer.MIN_VALUE;
        for (BlockPos p : frames) {
            int u = proj(p, axes.u), v = proj(p, axes.v);
            if (u < minU) minU = u; if (u > maxU) maxU = u;
            if (v < minV) minV = v; if (v > maxV) maxV = v;
        }

        // 3) Find up to two rectangles with controller on an edge
        Result bestVMin = findBestRectWithFixedEdge(level, frames, plane, axes, fixed, u0, v0, EdgeKind.VMIN, minU, maxU, minV, maxV);
        Result bestVMax = findBestRectWithFixedEdge(level, frames, plane, axes, fixed, u0, v0, EdgeKind.VMAX, minU, maxU, minV, maxV);
        Result bestUMin = findBestRectWithFixedEdge(level, frames, plane, axes, fixed, u0, v0, EdgeKind.UMIN, minU, maxU, minV, maxV);
        Result bestUMax = findBestRectWithFixedEdge(level, frames, plane, axes, fixed, u0, v0, EdgeKind.UMAX, minU, maxU, minV, maxV);

        List<Result> planeOut = new ArrayList<>(2);
        addIfNotNullDistinct(planeOut, bestVMin);
        addIfNotNullDistinct(planeOut, bestVMax);
        if (planeOut.size() < 2) addIfNotNullDistinct(planeOut, bestUMin);
        if (planeOut.size() < 2) addIfNotNullDistinct(planeOut, bestUMax);

        planeOut.sort(ORDER); // stable per plane
        return planeOut;
    }

    // ---------- Plane scanning ----------
    private enum EdgeKind { VMIN, VMAX, UMIN, UMAX }

    private static void addIfNotNullDistinct(List<Result> list, Result r) {
        if (r == null) return;
        for (Result e : list) {
            if (r.plane == e.plane &&
                    r.minX == e.minX && r.minY == e.minY && r.minZ == e.minZ &&
                    r.maxX == e.maxX && r.maxY == e.maxY && r.maxZ == e.maxZ) {
                return;
            }
        }
        if (list.size() < 2) list.add(r);
    }

    private static Result findBestRectWithFixedEdge(
            Level level, Set<BlockPos> frames, Plane plane, Axes axes, int fixed,
            int u0, int v0, EdgeKind kind,
            int minU, int maxU, int minV, int maxV
    ) {
        // Fix one edge to pass through the controller (so controller sits on that edge).
        final boolean edgeIsAlongU; // true if the fixed edge is a U-span (constant v), false if a V-span (constant u)
        switch (kind) {
            case VMIN, VMAX -> edgeIsAlongU = true;   // edge is v == v0, spans in U
            case UMIN, UMAX -> edgeIsAlongU = false;  // edge is u == u0, spans in V
            default -> throw new IllegalStateException();
        }

        long bestArea = Long.MAX_VALUE;
        int bestMinU = 0, bestMaxU = 0, bestMinV = 0, bestMaxV = 0;

        if (edgeIsAlongU) {
            // Controller sits on v == v0 edge; search vOpp on the other side.
            if (kind == EdgeKind.VMIN) {
                // expand toward +V (vOpp > v0)
                for (int vOpp = v0 + 1; vOpp <= maxV; vOpp++) {
                    // sweep U around the controller, ensuring the fixed edge is continuous
                    for (int uMin = u0; uMin >= minU; uMin--) {
                        if (!isFrame(level, unproj(uMin, v0, fixed, axes))) break;
                        for (int uMax = u0; uMax <= maxU; uMax++) {
                            if (!isFrame(level, unproj(uMax, v0, fixed, axes))) break;
                            if ((uMax - uMin + 1) < 3 || (vOpp - v0 + 1) < 3) continue;

                            int vMinRect = v0;
                            int vMaxRect = vOpp;

                            if (perimeterIsFrames(frames, axes, fixed, uMin, vMinRect, uMax, vMaxRect)
                                    && interiorHasNoFrames(frames, axes, fixed, uMin, vMinRect, uMax, vMaxRect)) {
                                long area = (long)(uMax - uMin + 1) * (long)(vMaxRect - vMinRect + 1);
                                if (area < bestArea) {
                                    bestArea = area;
                                    bestMinU = uMin; bestMaxU = uMax;
                                    bestMinV = vMinRect; bestMaxV = vMaxRect;
                                }
                            }
                        }
                    }
                }
            } else { // VMAX: expand toward -V (vOpp < v0)  --- FIXED LOOPS ---
                for (int vOpp = v0 - 1; vOpp >= minV; vOpp--) {
                    for (int uMin = u0; uMin >= minU; uMin--) {
                        if (!isFrame(level, unproj(uMin, v0, fixed, axes))) break;
                        for (int uMax = u0; uMax <= maxU; uMax++) {
                            if (!isFrame(level, unproj(uMax, v0, fixed, axes))) break;
                            if ((uMax - uMin + 1) < 3 || (v0 - vOpp + 1) < 3) continue;

                            int vMinRect = vOpp;
                            int vMaxRect = v0;

                            if (perimeterIsFrames(frames, axes, fixed, uMin, vMinRect, uMax, vMaxRect)
                                    && interiorHasNoFrames(frames, axes, fixed, uMin, vMinRect, uMax, vMaxRect)) {
                                long area = (long)(uMax - uMin + 1) * (long)(vMaxRect - vMinRect + 1);
                                if (area < bestArea) {
                                    bestArea = area;
                                    bestMinU = uMin; bestMaxU = uMax;
                                    bestMinV = vMinRect; bestMaxV = vMaxRect;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Controller sits on u == u0 edge; search uOpp on the other side.
            if (kind == EdgeKind.UMIN) {
                // expand toward +U (uOpp > u0)
                for (int uOpp = u0 + 1; uOpp <= maxU; uOpp++) {
                    for (int vMin2 = v0; vMin2 >= minV; vMin2--) {
                        if (!isFrame(level, unproj(u0, vMin2, fixed, axes))) break;
                        for (int vMax2 = v0; vMax2 <= maxV; vMax2++) {
                            if (!isFrame(level, unproj(u0, vMax2, fixed, axes))) break;
                            if ((uOpp - u0 + 1) < 3 || (vMax2 - vMin2 + 1) < 3) continue;

                            int uMinRect = u0;
                            int uMaxRect = uOpp;

                            if (perimeterIsFrames(frames, axes, fixed, uMinRect, vMin2, uMaxRect, vMax2)
                                    && interiorHasNoFrames(frames, axes, fixed, uMinRect, vMin2, uMaxRect, vMax2)) {
                                long area = (long)(uMaxRect - uMinRect + 1) * (long)(vMax2 - vMin2 + 1);
                                if (area < bestArea) {
                                    bestArea = area;
                                    bestMinU = uMinRect; bestMaxU = uMaxRect;
                                    bestMinV = vMin2;    bestMaxV = vMax2;
                                }
                            }
                        }
                    }
                }
            } else { // UMAX: expand toward -U (uOpp < u0)  --- FIXED LOOPS ---
                for (int uOpp = u0 - 1; uOpp >= minU; uOpp--) {
                    for (int vMin2 = v0; vMin2 >= minV; vMin2--) {
                        if (!isFrame(level, unproj(u0, vMin2, fixed, axes))) break;
                        for (int vMax2 = v0; vMax2 <= maxV; vMax2++) {
                            if (!isFrame(level, unproj(u0, vMax2, fixed, axes))) break;
                            if ((u0 - uOpp + 1) < 3 || (vMax2 - vMin2 + 1) < 3) continue;

                            int uMinRect = uOpp;
                            int uMaxRect = u0;

                            if (perimeterIsFrames(frames, axes, fixed, uMinRect, vMin2, uMaxRect, vMax2)
                                    && interiorHasNoFrames(frames, axes, fixed, uMinRect, vMin2, uMaxRect, vMax2)) {
                                long area = (long)(uMaxRect - uMinRect + 1) * (long)(vMax2 - vMin2 + 1);
                                if (area < bestArea) {
                                    bestArea = area;
                                    bestMinU = uMinRect; bestMaxU = uMaxRect;
                                    bestMinV = vMin2;    bestMaxV = vMax2;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (bestArea == Long.MAX_VALUE) return null;

        // Map u/v bounds back to xyz bounds
        int minX, minY, minZ, maxX, maxY, maxZ;
        switch (plane) {
            case XY -> {
                minX = bestMinU; maxX = bestMaxU;
                minY = bestMinV; maxY = bestMaxV;
                minZ = maxZ = fixed;
            }
            case XZ -> {
                minX = bestMinU; maxX = bestMaxU;
                minZ = bestMinV; maxZ = bestMaxV;
                minY = maxY = fixed;
            }
            case YZ -> {
                minY = bestMinU; maxY = bestMaxU;
                minZ = bestMinV; maxZ = bestMaxV;
                minX = maxX = fixed;
            }
            default -> throw new IllegalStateException();
        }

        // Seal facing = plane normal direction
        Direction facing = switch (plane) {
            case XY -> Direction.NORTH; // ⟂ Z
            case XZ -> Direction.UP;    // ⟂ Y
            case YZ -> Direction.EAST;  // ⟂ X
        };

        return new Result(plane, minX, minY, minZ, maxX, maxY, maxZ, facing);
    }

    // ---------- Geometry helpers ----------

    private static Axes axesFor(Plane plane) {
        return switch (plane) {
            case XY -> new Axes(Axis.X, Axis.Y, Axis.Z);
            case XZ -> new Axes(Axis.X, Axis.Z, Axis.Y);
            case YZ -> new Axes(Axis.Y, Axis.Z, Axis.X);
        };
    }

    private static Set<BlockPos> floodInPlane(Level level, BlockPos start, Plane plane, int fixed) {
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> q = new ArrayDeque<>();
        q.add(start);
        visited.add(start);
        for (int guard = 0; guard < 32768 && !q.isEmpty(); guard++) {
            BlockPos p = q.removeFirst();
            for (Direction d : inPlaneDirections(plane)) {
                BlockPos n = p.relative(d);
                if (n.get(plane.normal) != fixed) continue;
                if (!visited.contains(n) && isFrame(level, n)) {
                    visited.add(n);
                    q.add(n);
                }
            }
        }
        return visited;
    }

    private static Direction[] inPlaneDirections(Plane plane) {
        return switch (plane) {
            case XY -> new Direction[]{Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN};
            case XZ -> new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH};
            case YZ -> new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN};
        };
    }

    private static int proj(BlockPos p, Axis axis) {
        return p.get(axis);
    }

    private static BlockPos unproj(int u, int v, int wConst, Axes axes) {
        // inverse mapping from (u,v) back to (x,y,z)
        int x=0,y=0,z=0;
        for (Axis a : new Axis[]{axes.u, axes.v, axes.wConst}) {
            int val = (a == axes.u) ? u : (a == axes.v) ? v : wConst;
            switch (a) {
                case X -> x = val;
                case Y -> y = val;
                case Z -> z = val;
            }
        }
        return new BlockPos(x,y,z);
    }

    private static boolean perimeterIsFrames(Set<BlockPos> frames, Axes axes, int wConst,
                                             int uMin, int vMin, int uMax, int vMax) {
        // edges: u in [uMin..uMax] at v=vMin and v=vMax; v in [vMin..vMax] at u=uMin and u=uMax
        for (int u = uMin; u <= uMax; u++) {
            if (!frames.contains(unproj(u, vMin, wConst, axes))) return false;
            if (!frames.contains(unproj(u, vMax, wConst, axes))) return false;
        }
        for (int v = vMin; v <= vMax; v++) {
            if (!frames.contains(unproj(uMin, v, wConst, axes))) return false;
            if (!frames.contains(unproj(uMax, v, wConst, axes))) return false;
        }
        return true;
    }

    private static boolean interiorHasNoFrames(Set<BlockPos> frames, Axes axes, int wConst,
                                               int uMin, int vMin, int uMax, int vMax) {
        for (int u = uMin + 1; u <= uMax - 1; u++) {
            for (int v = vMin + 1; v <= vMax - 1; v++) {
                if (frames.contains(unproj(u, v, wConst, axes))) return false;
            }
        }
        return true;
    }
}