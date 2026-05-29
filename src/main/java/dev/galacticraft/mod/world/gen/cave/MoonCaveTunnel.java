package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * Planned curved tunnel connecting cave rooms.
 */
public record MoonCaveTunnel(
        BlockPos start,
        BlockPos end,
        double radius,
        double curve,
        int seed
) {
    public CaveZone zone(int x, int y, int z) {
        double distance = this.distance(x, y, z);

        if (distance <= this.radius) {
            return CaveZone.AIR;
        }

        if (distance <= this.radius + 1.15D) {
            return CaveZone.INNER_SHELL;
        }

        if (distance <= this.radius + 2.35D) {
            return CaveZone.OUTER_SHELL;
        }

        return CaveZone.NONE;
    }

    private double distance(int x, int y, int z) {
        Vec3 point = new Vec3(x + 0.5D, y + 0.5D, z + 0.5D);
        Vec3 previous = this.point(0.0D);
        double best = Double.MAX_VALUE;

        for (int i = 1; i <= 8; i++) {
            Vec3 next = this.point(i / 8.0D);
            best = Math.min(best, distanceToSegment(point, previous, next));
            previous = next;
        }

        return Math.sqrt(best);
    }

    private Vec3 point(double t) {
        Vec3 a = Vec3.atCenterOf(this.start);
        Vec3 b = Vec3.atCenterOf(this.end);
        Vec3 base = a.lerp(b, t);
        double fade = Math.sin(t * Math.PI);

        double x = Math.sin((t * 5.0D) + this.seed * 0.013D) * this.curve * fade;
        double y = Math.sin((t * 3.0D) + this.seed * 0.019D) * this.curve * 0.35D * fade;
        double z = Math.cos((t * 5.0D) + this.seed * 0.017D) * this.curve * fade;

        return base.add(x, y, z);
    }

    private static double distanceToSegment(Vec3 point, Vec3 a, Vec3 b) {
        Vec3 ab = b.subtract(a);
        double length = ab.lengthSqr();

        if (length <= 0.0001D) {
            return point.distanceToSqr(a);
        }

        double t = point.subtract(a).dot(ab) / length;
        t = Math.max(0.0D, Math.min(1.0D, t));

        return point.distanceToSqr(a.add(ab.scale(t)));
    }
}