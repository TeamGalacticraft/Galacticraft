package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;

/**
 * Curved cave tunnel represented as a small cached polyline.
 *
 * <p>The old tunnel implementation allocated {@code Vec3} objects and recomputed
 * curve points for every sampled block. This version precomputes the curve segments
 * once when the tunnel is created and performs allocation-free distance checks.</p>
 */
public final class MoonCaveTunnel implements MoonCaveElement {
    private static final int SEGMENT_COUNT = 8;

    private final BlockPos start;
    private final BlockPos end;
    private final double radius;
    private final double curve;
    private final int seed;
    private final MoonCaveBounds bounds;
    private final Segment[] segments;

    public MoonCaveTunnel(BlockPos start, BlockPos end, double radius, double curve, int seed) {
        this.start = start;
        this.end = end;
        this.radius = radius;
        this.curve = curve;
        this.seed = seed;
        this.bounds = createBounds(start, end, radius, curve);
        this.segments = this.createSegments();
    }

    public BlockPos start() {
        return this.start;
    }

    public BlockPos end() {
        return this.end;
    }

    public double radius() {
        return this.radius;
    }

    public double curve() {
        return this.curve;
    }

    public int seed() {
        return this.seed;
    }

    @Override
    public MoonCaveBounds bounds() {
        return this.bounds;
    }

    /**
     * Samples this tunnel at one block position.
     *
     * @return cave zone for the supplied block.
     */
    @Override
    public CaveZone zone(int x, int y, int z) {
        double distanceSqr = this.distanceSqr(x + 0.5D, y + 0.5D, z + 0.5D);
        double air = this.radius;
        double inner = this.radius + 1.15D;
        double outer = this.radius + 2.35D;

        if (distanceSqr <= air * air) {
            return CaveZone.AIR;
        }

        if (distanceSqr <= inner * inner) {
            return CaveZone.INNER_SHELL;
        }

        if (distanceSqr <= outer * outer) {
            return CaveZone.OUTER_SHELL;
        }

        return CaveZone.NONE;
    }

    private double distanceSqr(double px, double py, double pz) {
        double best = Double.MAX_VALUE;

        for (Segment segment : this.segments) {
            double distance = segment.distanceSqr(px, py, pz);

            if (distance < best) {
                best = distance;
            }
        }

        return best;
    }

    private Segment[] createSegments() {
        Segment[] result = new Segment[SEGMENT_COUNT];

        Point previous = this.point(0.0D);

        for (int i = 1; i <= SEGMENT_COUNT; i++) {
            Point next = this.point(i / (double) SEGMENT_COUNT);
            result[i - 1] = new Segment(previous.x, previous.y, previous.z, next.x, next.y, next.z);
            previous = next;
        }

        return result;
    }

    private Point point(double t) {
        double ax = this.start.getX() + 0.5D;
        double ay = this.start.getY() + 0.5D;
        double az = this.start.getZ() + 0.5D;

        double bx = this.end.getX() + 0.5D;
        double by = this.end.getY() + 0.5D;
        double bz = this.end.getZ() + 0.5D;

        double baseX = ax + (bx - ax) * t;
        double baseY = ay + (by - ay) * t;
        double baseZ = az + (bz - az) * t;
        double fade = Math.sin(t * Math.PI);

        double x = Math.sin((t * 5.0D) + this.seed * 0.013D) * this.curve * fade;
        double y = Math.sin((t * 3.0D) + this.seed * 0.019D) * this.curve * 0.35D * fade;
        double z = Math.cos((t * 5.0D) + this.seed * 0.017D) * this.curve * fade;

        return new Point(baseX + x, baseY + y, baseZ + z);
    }

    private static MoonCaveBounds createBounds(BlockPos start, BlockPos end, double radius, double curve) {
        MoonCaveBounds bounds = new MoonCaveBounds();
        bounds.includeTunnel(start, end, radius, curve, 5);
        return bounds;
    }

    private record Point(double x, double y, double z) {
    }

    private record Segment(double ax, double ay, double az, double bx, double by, double bz) {
        private double distanceSqr(double px, double py, double pz) {
            double abx = this.bx - this.ax;
            double aby = this.by - this.ay;
            double abz = this.bz - this.az;

            double apx = px - this.ax;
            double apy = py - this.ay;
            double apz = pz - this.az;

            double lengthSqr = abx * abx + aby * aby + abz * abz;

            if (lengthSqr <= 0.0001D) {
                double dx = px - this.ax;
                double dy = py - this.ay;
                double dz = pz - this.az;
                return dx * dx + dy * dy + dz * dz;
            }

            double t = (apx * abx + apy * aby + apz * abz) / lengthSqr;
            t = Math.max(0.0D, Math.min(1.0D, t));

            double closestX = this.ax + abx * t;
            double closestY = this.ay + aby * t;
            double closestZ = this.az + abz * t;

            double dx = px - closestX;
            double dy = py - closestY;
            double dz = pz - closestZ;

            return dx * dx + dy * dy + dz * dz;
        }
    }
}