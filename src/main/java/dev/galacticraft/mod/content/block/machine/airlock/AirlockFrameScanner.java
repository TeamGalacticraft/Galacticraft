/*
 * Copyright (c) 2019-2026 Team Galacticraft
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
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Finds rectangular airlock frames on each axis-aligned plane containing
 * the supplied controller.
 *
 * <p>The four corners of a frame are completely optional. Only the
 * non-corner portions of all four sides are required to consist of
 * {@link GCBlockTags#AIRLOCK_BLOCKS}.
 *
 * <p>Consequently all of the following are valid:
 *
 * <pre>
 * F F F F F       F F F       F F F
 * F       F       F     F     F     F
 * F       F       F     F     F     F
 * F F F F F       F F F         F F
 *
 * 4 corners       3 corners     1 corner
 * </pre>
 *
 * <p>The controller may be anywhere on the perimeter, including a corner.
 *
 * <p>Up to two smallest distinct rectangles are returned per plane.
 */
public final class AirlockFrameScanner {

    /**
     * Safety guard against pathological/infinite-looking rows of airlock
     * blocks.
     *
     * This is deliberately very large and is comparable to the guard used
     * by the previous flood-fill implementation.
     */
    private static final int MAX_SCAN_DISTANCE = 32768;

    private static final Plane[] PLANES = Plane.values();

    public static final Comparator<Result> ORDER = Comparator
            .comparing((Result result) -> result.plane().ordinal())
            .thenComparingInt(Result::minX)
            .thenComparingInt(Result::minY)
            .thenComparingInt(Result::minZ)
            .thenComparingInt(Result::maxX)
            .thenComparingInt(Result::maxY)
            .thenComparingInt(Result::maxZ);

    private static final Comparator<Result> AREA_ORDER =
            Comparator.comparingLong(AirlockFrameScanner::area)
                    .thenComparing(ORDER);

    private AirlockFrameScanner() {
    }

    public enum Plane {
        XY(Axis.Z),
        XZ(Axis.Y),
        YZ(Axis.X);

        private final Axis normal;

        Plane(Axis normal) {
            this.normal = normal;
        }

        public Axis normal() {
            return this.normal;
        }
    }

    /**
     * Logical corner identifiers.
     *
     * U/V refer to the two axes within the selected plane.
     *
     * XY: U=X, V=Y
     * XZ: U=X, V=Z
     * YZ: U=Y, V=Z
     */
    public enum CornerKind {
        MIN_U_MIN_V,
        MAX_U_MIN_V,
        MIN_U_MAX_V,
        MAX_U_MAX_V
    }

    /**
     * Information about a corner that is actually occupied by an airlock
     * frame block.
     *
     * Missing corners simply do not appear in {@link Result#corners()}.
     */
    public record Corner(
            CornerKind kind,
            BlockPos pos,
            BlockState state
    ) {
    }

    public record Result(
            Plane plane,
            int minX,
            int minY,
            int minZ,
            int maxX,
            int maxY,
            int maxZ,
            List<Corner> corners
    ) {
        public Result {
            corners = List.copyOf(corners);
        }

        /**
         * Compatibility constructor for code that only needs the rectangle.
         */
        public Result(
                Plane plane,
                int minX,
                int minY,
                int minZ,
                int maxX,
                int maxY,
                int maxZ
        ) {
            this(
                    plane,
                    minX,
                    minY,
                    minZ,
                    maxX,
                    maxY,
                    maxZ,
                    List.of()
            );
        }

        public int cornerCount() {
            return this.corners.size();
        }

        public boolean hasCorner(CornerKind kind) {
            for (Corner corner : this.corners) {
                if (corner.kind() == kind) {
                    return true;
                }
            }

            return false;
        }
    }

    private enum EdgeKind {
        VMIN,
        VMAX,
        UMIN,
        UMAX
    }

    /**
     * Scan all three axis-aligned planes containing the controller.
     */
    public static List<Result> scanAll(Level level, BlockPos controller) {
        if (!isFrame(level, controller)) {
            return List.of();
        }

        List<Result> result = new ArrayList<>(6);

        for (Plane plane : PLANES) {
            result.addAll(scanPlane(level, controller, plane));
        }

        result.sort(ORDER);
        return result;
    }

    // ---------------------------------------------------------------------
    // Plane scanning
    // ---------------------------------------------------------------------

    private static List<Result> scanPlane(
            Level level,
            BlockPos controller,
            Plane plane
    ) {
        FrameView view = new FrameView(level, plane, controller.get(plane.normal()));

        int u0 = projectU(controller, plane);
        int v0 = projectV(controller, plane);

        /*
         * Each search assumes that the controller lies on one of the four
         * possible sides.
         *
         * The controller may also be a corner. In that case the same
         * rectangle can be discovered from two adjacent EdgeKinds; the
         * duplicate is removed below.
         */
        Result vMin = findBestRectWithFixedEdge(
                view,
                controller,
                EdgeKind.VMIN,
                u0,
                v0
        );

        Result vMax = findBestRectWithFixedEdge(
                view,
                controller,
                EdgeKind.VMAX,
                u0,
                v0
        );

        Result uMin = findBestRectWithFixedEdge(
                view,
                controller,
                EdgeKind.UMIN,
                u0,
                v0
        );

        Result uMax = findBestRectWithFixedEdge(
                view,
                controller,
                EdgeKind.UMAX,
                u0,
                v0
        );

        List<Result> candidates = new ArrayList<>(4);

        addDistinct(candidates, vMin);
        addDistinct(candidates, vMax);
        addDistinct(candidates, uMin);
        addDistinct(candidates, uMax);

        if (candidates.isEmpty()) {
            return List.of();
        }

        /*
         * The class promises the two smallest rectangles on this plane.
         *
         * Sorting by area first makes that statement actually true rather
         * than depending on EdgeKind search order.
         */
        candidates.sort(AREA_ORDER);

        if (candidates.size() > 2) {
            candidates.subList(2, candidates.size()).clear();
        }

        candidates.sort(ORDER);
        return candidates;
    }

    private static void addDistinct(List<Result> list, Result result) {
        if (result == null) {
            return;
        }

        for (Result existing : list) {
            if (sameBounds(existing, result)) {
                return;
            }
        }

        list.add(result);
    }

    private static boolean sameBounds(Result a, Result b) {
        return a.plane() == b.plane()
                && a.minX() == b.minX()
                && a.minY() == b.minY()
                && a.minZ() == b.minZ()
                && a.maxX() == b.maxX()
                && a.maxY() == b.maxY()
                && a.maxZ() == b.maxZ();
    }

    /**
     * Searches for the smallest rectangle for which {@code controller}
     * belongs to the specified side.
     *
     * <p>The search operates in logical coordinates:
     *
     * <ul>
     *     <li>{@code along}: coordinate running along the controller side</li>
     *     <li>{@code perpendicular}: coordinate running toward the opposite side</li>
     * </ul>
     *
     * <p>This means all four EdgeKinds can use exactly the same algorithm.
     */
    private static Result findBestRectWithFixedEdge(
            FrameView view,
            BlockPos controller,
            EdgeKind edge,
            int u0,
            int v0
    ) {
        boolean alongU = edge == EdgeKind.VMIN || edge == EdgeKind.VMAX;

        int along0 = alongU ? u0 : v0;
        int perpendicular0 = alongU ? v0 : u0;

        /*
         * Direction from the controller side toward the rectangle interior.
         */
        int perpendicularDirection = switch (edge) {
            case VMIN, UMIN -> 1;
            case VMAX, UMAX -> -1;
        };

        /*
         * Find the contiguous frame run passing through the controller.
         *
         * This run tells us every possible location for the two corners of
         * the controller-side edge:
         *
         * - Any occupied position in the run can itself be a corner.
         * - Exactly one position beyond the run can be a missing corner.
         *
         * Nothing farther away can be part of this rectangle because that
         * would introduce a missing non-corner edge block.
         */
        int runMin = along0;
        int runMax = along0;

        for (int distance = 1; distance <= MAX_SCAN_DISTANCE; distance++) {
            int along = along0 - distance;

            if (!isFrameLogical(
                    view,
                    alongU,
                    along,
                    perpendicular0
            )) {
                break;
            }

            runMin = along;
        }

        for (int distance = 1; distance <= MAX_SCAN_DISTANCE; distance++) {
            int along = along0 + distance;

            if (!isFrameLogical(
                    view,
                    alongU,
                    along,
                    perpendicular0
            )) {
                break;
            }

            runMax = along;
        }

        /*
         * One position outside the contiguous run is allowed because that
         * position may simply be an omitted corner.
         */
        int candidateMin = runMin - 1;
        int candidateMax = runMax + 1;

        long bestArea = Long.MAX_VALUE;
        LogicalRect best = null;

        /*
         * Controller may be:
         *
         * - somewhere inside the edge,
         * - the minimum corner,
         * - the maximum corner.
         *
         * Therefore minAlong may equal along0 and maxAlong may equal along0.
         */
        for (int minAlong = candidateMin; minAlong <= along0; minAlong++) {

            /*
             * If a result already exists, the minimum possible height is 3.
             * A width this large can no longer improve the result.
             */
            int smallestWidth = Math.max(3, along0 - minAlong + 1);

            if (bestArea != Long.MAX_VALUE
                    && (long) smallestWidth * 3L > bestArea) {
                continue;
            }

            int firstMax = Math.max(along0, minAlong + 2);

            for (int maxAlong = firstMax;
                 maxAlong <= candidateMax;
                 maxAlong++) {

                int width = maxAlong - minAlong + 1;

                if (bestArea != Long.MAX_VALUE
                        && (long) width * 3L > bestArea) {
                    break;
                }

                /*
                 * Everything between the two corners of the controller side
                 * must be frame.
                 *
                 * The corner cells themselves are intentionally ignored.
                 */
                if (!edgeInteriorIsFrame(
                        view,
                        alongU,
                        minAlong,
                        maxAlong,
                        perpendicular0
                )) {
                    continue;
                }

                LogicalRect candidate = findNearestOppositeEdge(
                        view,
                        controller,
                        alongU,
                        minAlong,
                        maxAlong,
                        perpendicular0,
                        perpendicularDirection
                );

                if (candidate == null) {
                    continue;
                }

                long candidateArea = candidate.area();

                if (candidateArea < bestArea) {
                    bestArea = candidateArea;
                    best = candidate;
                }
            }
        }

        if (best == null) {
            return null;
        }

        return createResult(view, best);
    }

    /**
     * Starting from a known controller-side edge, follows both side edges
     * outward until it finds the nearest valid opposite edge.
     *
     * <p>Side and opposite-edge corners are never required.
     */
    private static LogicalRect findNearestOppositeEdge(
            FrameView view,
            BlockPos controller,
            boolean alongU,
            int minAlong,
            int maxAlong,
            int fixedPerpendicular,
            int direction
    ) {
        /*
         * A frame must be at least 3 blocks in this dimension:
         *
         * fixed edge
         * one interior side block
         * opposite edge
         */
        for (int distance = 2;
             distance <= MAX_SCAN_DISTANCE;
             distance++) {

            int previousPerpendicular =
                    fixedPerpendicular + direction * (distance - 1);

            /*
             * These are non-corner cells of the two side edges.
             *
             * Once either one is absent, no larger rectangle using these
             * same two sides can ever be valid.
             */
            if (!isFrameLogical(
                    view,
                    alongU,
                    minAlong,
                    previousPerpendicular
            )) {
                return null;
            }

            if (!isFrameLogical(
                    view,
                    alongU,
                    maxAlong,
                    previousPerpendicular
            )) {
                return null;
            }

            int oppositePerpendicular =
                    fixedPerpendicular + direction * distance;

            /*
             * Only the INTERIOR of the opposite edge is required.
             *
             * opposite corners:
             *
             *   (minAlong, oppositePerpendicular)
             *   (maxAlong, oppositePerpendicular)
             *
             * may contain anything.
             */
            if (!edgeInteriorIsFrame(
                    view,
                    alongU,
                    minAlong,
                    maxAlong,
                    oppositePerpendicular
            )) {
                continue;
            }

            int minPerpendicular = Math.min(
                    fixedPerpendicular,
                    oppositePerpendicular
            );

            int maxPerpendicular = Math.max(
                    fixedPerpendicular,
                    oppositePerpendicular
            );

            LogicalRect rect;

            if (alongU) {
                rect = new LogicalRect(
                        minAlong,
                        minPerpendicular,
                        maxAlong,
                        maxPerpendicular
                );
            } else {
                rect = new LogicalRect(
                        minPerpendicular,
                        minAlong,
                        maxPerpendicular,
                        maxAlong
                );
            }

            /*
             * If this otherwise-complete edge has frame blocks inside the
             * rectangle, every larger rectangle in this direction would
             * contain those same blocks as interior frame blocks.
             *
             * We can therefore terminate this side search immediately.
             */
            if (!interiorHasNoFrames(view, rect)) {
                return null;
            }

            if (!containsControllerOnPerimeter(
                    rect,
                    projectU(controller, view.plane),
                    projectV(controller, view.plane)
            )) {
                continue;
            }

            /*
             * Preserve the old restriction that exactly one controller may
             * belong to a detected frame.
             */
            if (!hasExactlyOneController(view, rect)) {
                return null;
            }

            return rect;
        }

        return null;
    }

    // ---------------------------------------------------------------------
    // Rectangle validation
    // ---------------------------------------------------------------------

    /**
     * Tests the required, non-corner portion of an edge.
     */
    private static boolean edgeInteriorIsFrame(
            FrameView view,
            boolean alongU,
            int minAlong,
            int maxAlong,
            int perpendicular
    ) {
        for (int along = minAlong + 1;
             along < maxAlong;
             along++) {

            if (!isFrameLogical(
                    view,
                    alongU,
                    along,
                    perpendicular
            )) {
                return false;
            }
        }

        return true;
    }

    /**
     * The interior of an airlock may not contain other airlock-frame blocks.
     */
    private static boolean interiorHasNoFrames(
            FrameView view,
            LogicalRect rect
    ) {
        for (int u = rect.minU + 1; u < rect.maxU; u++) {
            for (int v = rect.minV + 1; v < rect.maxV; v++) {
                if (view.isFrame(u, v)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Ensures exactly one airlock controller exists on the complete
     * perimeter.
     *
     * Corners are visited exactly once.
     */
    private static boolean hasExactlyOneController(
            FrameView view,
            LogicalRect rect
    ) {
        int controllers = 0;

        /*
         * minV / maxV edges, including corners.
         */
        for (int u = rect.minU; u <= rect.maxU; u++) {
            if (view.isController(u, rect.minV)) {
                if (++controllers > 1) {
                    return false;
                }
            }

            if (view.isController(u, rect.maxV)) {
                if (++controllers > 1) {
                    return false;
                }
            }
        }

        /*
         * minU / maxU edges, excluding corners because those were already
         * checked above.
         */
        for (int v = rect.minV + 1; v < rect.maxV; v++) {
            if (view.isController(rect.minU, v)) {
                if (++controllers > 1) {
                    return false;
                }
            }

            if (view.isController(rect.maxU, v)) {
                if (++controllers > 1) {
                    return false;
                }
            }
        }

        return controllers == 1;
    }

    private static boolean containsControllerOnPerimeter(
            LogicalRect rect,
            int u,
            int v
    ) {
        if (u < rect.minU
                || u > rect.maxU
                || v < rect.minV
                || v > rect.maxV) {
            return false;
        }

        return u == rect.minU
                || u == rect.maxU
                || v == rect.minV
                || v == rect.maxV;
    }

    // ---------------------------------------------------------------------
    // Result construction / corner discovery
    // ---------------------------------------------------------------------

    private static Result createResult(
            FrameView view,
            LogicalRect rect
    ) {
        List<Corner> corners = new ArrayList<>(4);

        addCornerIfPresent(
                view,
                corners,
                CornerKind.MIN_U_MIN_V,
                rect.minU,
                rect.minV
        );

        addCornerIfPresent(
                view,
                corners,
                CornerKind.MAX_U_MIN_V,
                rect.maxU,
                rect.minV
        );

        addCornerIfPresent(
                view,
                corners,
                CornerKind.MIN_U_MAX_V,
                rect.minU,
                rect.maxV
        );

        addCornerIfPresent(
                view,
                corners,
                CornerKind.MAX_U_MAX_V,
                rect.maxU,
                rect.maxV
        );

        return switch (view.plane) {
            case XY -> new Result(
                    view.plane,
                    rect.minU,
                    rect.minV,
                    view.fixed,
                    rect.maxU,
                    rect.maxV,
                    view.fixed,
                    corners
            );

            case XZ -> new Result(
                    view.plane,
                    rect.minU,
                    view.fixed,
                    rect.minV,
                    rect.maxU,
                    view.fixed,
                    rect.maxV,
                    corners
            );

            case YZ -> new Result(
                    view.plane,
                    view.fixed,
                    rect.minU,
                    rect.minV,
                    view.fixed,
                    rect.maxU,
                    rect.maxV,
                    corners
            );
        };
    }

    private static void addCornerIfPresent(
            FrameView view,
            List<Corner> corners,
            CornerKind kind,
            int u,
            int v
    ) {
        if (!view.isFrame(u, v)) {
            return;
        }

        BlockPos pos = view.immutablePos(u, v);

        corners.add(new Corner(
                kind,
                pos,
                view.level.getBlockState(pos)
        ));
    }

    // ---------------------------------------------------------------------
    // Logical coordinates
    // ---------------------------------------------------------------------

    private record LogicalRect(
            int minU,
            int minV,
            int maxU,
            int maxV
    ) {
        long area() {
            return (long) (this.maxU - this.minU + 1)
                    * (long) (this.maxV - this.minV + 1);
        }
    }

    /**
     * Converts logical (along, perpendicular) coordinates back to U/V.
     */
    private static boolean isFrameLogical(
            FrameView view,
            boolean alongU,
            int along,
            int perpendicular
    ) {
        return alongU
                ? view.isFrame(along, perpendicular)
                : view.isFrame(perpendicular, along);
    }

    private static int projectU(BlockPos pos, Plane plane) {
        return switch (plane) {
            case XY, XZ -> pos.getX();
            case YZ -> pos.getY();
        };
    }

    private static int projectV(BlockPos pos, Plane plane) {
        return switch (plane) {
            case XY -> pos.getY();
            case XZ, YZ -> pos.getZ();
        };
    }

    private static long area(Result result) {
        return switch (result.plane()) {
            case XY -> (long) (result.maxX() - result.minX() + 1)
                    * (long) (result.maxY() - result.minY() + 1);

            case XZ -> (long) (result.maxX() - result.minX() + 1)
                    * (long) (result.maxZ() - result.minZ() + 1);

            case YZ -> (long) (result.maxY() - result.minY() + 1)
                    * (long) (result.maxZ() - result.minZ() + 1);
        };
    }

    // ---------------------------------------------------------------------
    // Cached world view
    // ---------------------------------------------------------------------

    /**
     * Provides cached frame/controller queries within one plane.
     *
     * <p>Primitive long keys avoid allocating BlockPos objects for every
     * repeated geometry check.
     */
    private static final class FrameView {

        private static final byte UNKNOWN = -1;
        private static final byte FALSE = 0;
        private static final byte TRUE = 1;

        private final Level level;
        private final Plane plane;
        private final int fixed;

        private final BlockPos.MutableBlockPos cursor =
                new BlockPos.MutableBlockPos();

        private final Long2ByteOpenHashMap frameCache =
                new Long2ByteOpenHashMap();

        private final Long2ByteOpenHashMap controllerCache =
                new Long2ByteOpenHashMap();

        private FrameView(
                Level level,
                Plane plane,
                int fixed
        ) {
            this.level = level;
            this.plane = plane;
            this.fixed = fixed;

            this.frameCache.defaultReturnValue(UNKNOWN);
            this.controllerCache.defaultReturnValue(UNKNOWN);
        }

        private boolean isFrame(int u, int v) {
            long key = key(u, v);

            byte cached = this.frameCache.get(key);

            if (cached != UNKNOWN) {
                return cached == TRUE;
            }

            setCursor(u, v);

            boolean frame = this.level
                    .getBlockState(this.cursor)
                    .is(GCBlockTags.AIRLOCK_BLOCKS);

            this.frameCache.put(
                    key,
                    frame ? TRUE : FALSE
            );

            return frame;
        }

        private boolean isController(int u, int v) {
            long key = key(u, v);

            byte cached = this.controllerCache.get(key);

            if (cached != UNKNOWN) {
                return cached == TRUE;
            }

            setCursor(u, v);

            boolean controller =
                    this.level.getBlockEntity(this.cursor)
                            instanceof AirlockControllerBlockEntity;

            this.controllerCache.put(
                    key,
                    controller ? TRUE : FALSE
            );

            return controller;
        }

        private BlockPos immutablePos(int u, int v) {
            return switch (this.plane) {
                case XY -> new BlockPos(u, v, this.fixed);
                case XZ -> new BlockPos(u, this.fixed, v);
                case YZ -> new BlockPos(this.fixed, u, v);
            };
        }

        private void setCursor(int u, int v) {
            switch (this.plane) {
                case XY -> this.cursor.set(
                        u,
                        v,
                        this.fixed
                );

                case XZ -> this.cursor.set(
                        u,
                        this.fixed,
                        v
                );

                case YZ -> this.cursor.set(
                        this.fixed,
                        u,
                        v
                );
            }
        }

        private static long key(int u, int v) {
            return ((long) u << 32)
                    ^ (v & 0xFFFFFFFFL);
        }
    }

    // ---------------------------------------------------------------------
    // Basic block tests
    // ---------------------------------------------------------------------

    private static boolean isFrame(
            Level level,
            BlockPos pos
    ) {
        return level.getBlockState(pos)
                .is(GCBlockTags.AIRLOCK_BLOCKS);
    }
}