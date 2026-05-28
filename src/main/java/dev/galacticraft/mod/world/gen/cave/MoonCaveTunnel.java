package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public record MoonCaveTunnel(
        BlockPos start,
        BlockPos end,
        double radius,
        double curve,
        int seed
) {
    public boolean contains(double x, double y, double z) {
        return this.distance(x, y, z) <= this.radius;
    }

    public boolean innerShell(double x, double y, double z) {
        double distance = this.distance(x, y, z);
        return distance > this.radius && distance <= this.radius + 1.25D;
    }

    public boolean outerShell(double x, double y, double z) {
        double distance = this.distance(x, y, z);
        return distance > this.radius + 1.25D && distance <= this.radius + 2.5D;
    }

    private double distance(double x, double y, double z) {
        Vec3 point = new Vec3(x + 0.5D, y + 0.5D, z + 0.5D);

        double best = Double.MAX_VALUE;
        Vec3 previous = this.point(0.0D);

        for (int i = 1; i <= 12; i++) {
            Vec3 next = this.point(i / 12.0D);
            best = Math.min(best, distanceToSegment(point, previous, next));
            previous = next;
        }

        return Math.sqrt(best);
    }

    private Vec3 point(double t) {
        Vec3 a = Vec3.atCenterOf(this.start);
        Vec3 b = Vec3.atCenterOf(this.end);
        Vec3 base = a.lerp(b, t);

        double wave = Math.sin(t * Math.PI);
        double x = Math.sin((t * 7.0D) + this.seed) * this.curve * wave;
        double y = Math.sin((t * 5.0D) + this.seed * 0.7D) * this.curve * 0.45D * wave;
        double z = Math.cos((t * 6.0D) + this.seed) * this.curve * wave;

        return base.add(x, y, z);
    }

    private static double distanceToSegment(Vec3 point, Vec3 a, Vec3 b) {
        Vec3 ab = b.subtract(a);
        double len = ab.lengthSqr();

        if (len <= 0.0001D) {
            return point.distanceToSqr(a);
        }

        double t = point.subtract(a).dot(ab) / len;
        t = Math.max(0.0D, Math.min(1.0D, t));

        return point.distanceToSqr(a.add(ab.scale(t)));
    }
}