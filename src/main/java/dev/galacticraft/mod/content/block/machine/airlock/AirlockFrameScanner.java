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
 * <p>All geometrically distinct valid rectangles containing the
 * supplied controller are returned across all three planes.
 *
 * <p>There is no fixed limit on the number of rectangles.
 */
public final class AirlockFrameScanner {

    /**
     * Safety guard against pathological or effectively unbounded rows
     * of airlock blocks.
     *
     * <p>This limit is deliberately large and is comparable to the
     * guard used by the previous flood-fill implementation.
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
     * Logical identifiers for the four corners of a rectangle.
     *
     * <p>U and V represent the two axes within the selected plane:
     *
     * <ul>
     *     <li>XY: U = X, V = Y</li>
     *     <li>XZ: U = X, V = Z</li>
     *     <li>YZ: U = Y, V = Z</li>
     * </ul>
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
     * <p>Missing corners simply do not appear in {@link Result#corners()}.
     *
     * @param kind the logical position of the corner within the rectangle
     * @param pos the world position of the corner block
     * @param state the block state of the corner block
     */
    public record Corner(
            CornerKind kind,
            BlockPos pos,
            BlockState state
    ) {
    }

    /**
     * Represents a valid rectangular airlock frame discovered by the scanner.
     *
     * <p>The rectangle is described by its minimum and maximum world
     * coordinates, inclusive of the perimeter blocks.
     *
     * <p>Only corners occupied by airlock frame blocks are included
     * in {@code corners}. Missing corners are permitted.
     *
     * @param plane the axis-aligned plane containing the rectangle
     * @param minX the minimum X coordinate of the rectangle
     * @param minY the minimum Y coordinate of the rectangle
     * @param minZ the minimum Z coordinate of the rectangle
     * @param maxX the maximum X coordinate of the rectangle
     * @param maxY the maximum Y coordinate of the rectangle
     * @param maxZ the maximum Z coordinate of the rectangle
     * @param corners the occupied corners of the rectangle
     */
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
         * Constructs a rectangle result without corner information.
         *
         * <p>This constructor is retained for compatibility with code
         * that only requires the rectangle's bounds.
         *
         * @param plane the axis-aligned plane containing the rectangle
         * @param minX the minimum X coordinate
         * @param minY the minimum Y coordinate
         * @param minZ the minimum Z coordinate
         * @param maxX the maximum X coordinate
         * @param maxY the maximum Y coordinate
         * @param maxZ the maximum Z coordinate
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

        /**
         * Calculates the area of the rectangle in its containing plane.
         *
         * <p>The area includes the perimeter blocks and is measured
         * in square blocks.
         *
         * @return the area of the rectangle in square blocks
         */
        public long area() {
            return switch (this.plane) {
                case XY -> (long) (this.maxX - this.minX + 1)
                        * (long) (this.maxY - this.minY + 1);

                case XZ -> (long) (this.maxX - this.minX + 1)
                        * (long) (this.maxZ - this.minZ + 1);

                case YZ -> (long) (this.maxY - this.minY + 1)
                        * (long) (this.maxZ - this.minZ + 1);
            };
        }

        /**
         * Compares this rectangle's plane and bounds with another result.
         *
         * <p>Corner metadata is intentionally ignored.
         *
         * @param other the rectangle to compare against
         * @return {@code true} if both rectangles have identical
         *         planes and bounds
         */
        public boolean sameBounds(Result other) {
            return this.plane == other.plane
                    && this.minX == other.minX
                    && this.minY == other.minY
                    && this.minZ == other.minZ
                    && this.maxX == other.maxX
                    && this.maxY == other.maxY
                    && this.maxZ == other.maxZ;
        }
    }

    private enum EdgeKind {
        VMIN,
        VMAX,
        UMIN,
        UMAX
    }

    /**
     * Scans all three axis-aligned planes containing the controller.
     *
     * <p>Every geometrically distinct valid rectangle containing
     * the controller is returned.
     *
     * <p>There is no fixed limit on the number of rectangles
     * discovered per plane.
     *
     * @param level the world containing the airlock frames
     * @param controller the position of the airlock controller
     * @return a deterministically ordered list of all valid
     *         rectangles containing the controller, or an empty
     *         list if none are found
     */
    public static List<Result> scanAll(Level level, BlockPos controller) {
        if (!isFrame(level, controller)) {
            return List.of();
        }

        List<Result> results = new ArrayList<>(12);

        for (Plane plane : PLANES) {
            results.addAll(scanPlane(level, controller, plane));
        }

        results.sort(ORDER);

        return results;
    }

    // ---------------------------------------------------------------------
    // Plane scanning
    // ---------------------------------------------------------------------

    private static List<Result> scanPlane(
            Level level,
            BlockPos controller,
            Plane plane
    ) {
        FrameView view = new FrameView(
                level,
                plane,
                controller.get(plane.normal())
        );

        int u0 = projectU(controller, plane);
        int v0 = projectV(controller, plane);

        List<Result> candidates = new ArrayList<>();

        /*
         * The controller may belong to any of the four sides
         * of a rectangle.
         *
         * Search every possible orientation and collect all
         * valid rectangles, not just the smallest.
         *
         * Rectangles discovered from multiple orientations
         * are deduplicated by their bounds.
         */
        for (EdgeKind edge : EdgeKind.values()) {
            findRectsWithFixedEdge(
                    view,
                    controller,
                    edge,
                    u0,
                    v0,
                    candidates
            );
        }

        candidates.sort(ORDER);

        return candidates;
    }

    private static void addDistinct(List<Result> list, Result result) {
        if (result == null) {
            return;
        }

        for (Result existing : list) {
            if (existing.sameBounds(result)) {
                return;
            }
        }

        list.add(result);
    }

    /**
     * Finds every valid rectangle for which the controller
     * belongs to the specified side.
     *
     * <p>The search operates in logical coordinates:
     *
     * <ul>
     *     <li>{@code along}: coordinate running along the controller side</li>
     *     <li>{@code perpendicular}: coordinate running toward the opposite side</li>
     * </ul>
     *
     * <p>All four EdgeKinds use the same algorithm.
     *
     * <p>Unlike the previous implementation, this method
     * collects every valid rectangle instead of selecting
     * only the smallest one.
     */
    private static void findRectsWithFixedEdge(
            FrameView view,
            BlockPos controller,
            EdgeKind edge,
            int u0,
            int v0,
            List<Result> results
    ) {
        boolean alongU =
                edge == EdgeKind.VMIN || edge == EdgeKind.VMAX;

        int along0 = alongU ? u0 : v0;
        int perpendicular0 = alongU ? v0 : u0;

        /*
         * Direction from the controller side toward
         * the rectangle interior.
         */
        int perpendicularDirection = switch (edge) {
            case VMIN, UMIN -> 1;
            case VMAX, UMAX -> -1;
        };

        /*
         * Find the contiguous frame run passing through
         * the controller.
         *
         * Every possible controller-side edge must be
         * contained within this run, with the exception
         * of its two optional corner positions.
         */
        int runMin = along0;
        int runMax = along0;

        for (int distance = 1;
             distance <= MAX_SCAN_DISTANCE;
             distance++) {

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

        for (int distance = 1;
             distance <= MAX_SCAN_DISTANCE;
             distance++) {

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
         * Allow one position beyond each end of the
         * contiguous run because corners are optional.
         */
        int candidateMin = runMin - 1;
        int candidateMax = runMax + 1;

        /*
         * Enumerate every possible pair of endpoints
         * for the controller-side edge.
         *
         * The controller must lie between the endpoints.
         *
         * A valid rectangle must be at least three blocks
         * wide to contain a non-corner edge block.
         */
        for (int minAlong = candidateMin;
             minAlong <= along0;
             minAlong++) {

            int firstMax = Math.max(
                    along0,
                    minAlong + 2
            );

            for (int maxAlong = firstMax;
                 maxAlong <= candidateMax;
                 maxAlong++) {

                /*
                 * Every non-corner block on the
                 * controller-side edge must be a frame.
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

                /*
                 * Both side edges must have at least one
                 * frame block immediately inside the
                 * rectangle.
                 *
                 * This is a cheap rejection test that avoids
                 * scanning outward for endpoint combinations
                 * that cannot possibly form a rectangle.
                 */
                int firstInterior =
                        perpendicular0 + perpendicularDirection;

                if (!isFrameLogical(
                        view,
                        alongU,
                        minAlong,
                        firstInterior
                )) {
                    continue;
                }

                if (!isFrameLogical(
                        view,
                        alongU,
                        maxAlong,
                        firstInterior
                )) {
                    continue;
                }

                /*
                 * Find the valid opposite edge for this
                 * particular pair of side endpoints.
                 *
                 * We no longer compare areas or discard
                 * rectangles simply because a smaller
                 * rectangle has already been discovered.
                 */
                LogicalRect rect = findNearestOppositeEdge(
                        view,
                        controller,
                        alongU,
                        minAlong,
                        maxAlong,
                        perpendicular0,
                        perpendicularDirection
                );

                if (rect == null) {
                    continue;
                }

                /*
                 * Different searches may discover the
                 * same rectangle, especially when the
                 * controller occupies a corner.
                 *
                 * Only add geometrically distinct results.
                 */
                addDistinct(
                        results,
                        createResult(view, rect)
                );
            }
        }
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