package dev.galacticraft.mod.world.gen.dungeon.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class BoxSampling {

    /** A bias knot at normalized height t in [0,1] with weight w (>0). */
    public record Knot(double t, double w) {}

    /**
     * Sample a random point inside an AABB with Y distributed according to a piecewise-linear bias.
     * Knots define (t in [0,1], weight). The PDF is proportional to the linear interpolation between knots.
     * Missing endpoints are auto-filled with weight=1 at t=0/1.
     */
    public static Vec3 randomPointInAabbBiasedY(AABB box, RandomSource rnd, List<Knot> knotsIn) {
        if (box == null || rnd == null) throw new IllegalArgumentException("box/rnd null");

        // --- Build a clean, sorted knot list with endpoints ---
        List<Knot> ks = new ArrayList<>();
        if (knotsIn != null) {
            for (Knot k : knotsIn) {
                if (k == null) continue;
                double t = clamp01(k.t);
                double w = Math.max(1e-9, k.w); // keep positive
                ks.add(new Knot(t, w));
            }
        }
        if (ks.stream().noneMatch(k -> k.t == 0.0)) ks.add(new Knot(0.0, 1.0));
        if (ks.stream().noneMatch(k -> k.t == 1.0)) ks.add(new Knot(1.0, 1.0));
        ks.sort(Comparator.comparingDouble(Knot::t));

        // Collapse duplicates at same t (keep last)
        List<Knot> knots = new ArrayList<>();
        Knot prev = null;
        for (Knot k : ks) {
            if (prev != null && Math.abs(k.t - prev.t) < 1e-12) {
                prev = k; // replace
                if (!knots.isEmpty()) knots.set(knots.size() - 1, k);
            } else {
                knots.add(k);
                prev = k;
            }
        }

        // --- Precompute segment areas (integral of linear weight) and total area ---
        int n = knots.size() - 1;
        double[] segStartArea = new double[n];
        double totalArea = 0.0;

        for (int i = 0; i < n; i++) {
            Knot a = knots.get(i);
            Knot b = knots.get(i + 1);
            double dt = b.t - a.t;
            if (dt <= 0) continue; // should not happen after sort

            // Linear weight w(t) from a.w -> b.w across dt
            // Area under w across this segment = trapezoid area:
            double area = 0.5 * (a.w + b.w) * dt;
            segStartArea[i] = totalArea;
            totalArea += area;
        }

        if (totalArea <= 0) {
            // Degenerate bias; fall back to uniform
            double x = lerp(box.minX, box.maxX, rnd.nextDouble());
            double y = lerp(box.minY, box.maxY, rnd.nextDouble());
            double z = lerp(box.minZ, box.maxZ, rnd.nextDouble());
            return new Vec3(x, y, z);
        }

        // --- Draw a segment by area, then invert the local quadratic CDF to get t ---
        double r = rnd.nextDouble() * totalArea;

        int seg = 0;
        while (seg + 1 < n && r >= segStartArea[seg + 1]) seg++;

        Knot a = knots.get(seg);
        Knot b = knots.get(seg + 1);
        double dt = b.t - a.t;
        double a0 = a.w;                 // weight at start
        double a1 = b.w;                 // weight at end
        double rLocal = r - segStartArea[seg];

        // For linear weight over t in [0, dt], the CDF within the segment is:
        // S(s) = a0*s + (a1 - a0)*s^2 / (2*dt),  s in [0, dt]
        // Solve S(s) = rLocal for s.
        double s;
        double diff = (a1 - a0);
        if (Math.abs(diff) < 1e-12) {
            // Nearly constant weight
            s = rLocal / a0; // since S(s) = a0*s
        } else {
            // Quadratic: (diff/(2*dt)) * s^2 + a0*s - rLocal = 0
            double A = diff / (2.0 * dt);
            double B = a0;
            double C = -rLocal;
            double disc = B * B - 4 * A * C;      // should be >= 0
            double sqrt = Math.sqrt(Math.max(0.0, disc));
            // Take the positive root in [0, dt]
            double s1 = (-B + sqrt) / (2 * A);
            double s2 = (-B - sqrt) / (2 * A);
            s = (s1 >= 0 && s1 <= dt) ? s1 : s2;
            if (s < 0 || s > dt) {
                // numerical fallback
                s = Math.max(0.0, Math.min(dt, rLocal / Math.max(1e-12, a0)));
            }
        }

        double t = a.t + (s / dt) * dt; // = a.t + s, but written this way for clarity
        t = clamp01(t);

        double x = lerp(box.minX, box.maxX, rnd.nextDouble());
        double y = lerp(box.minY, box.maxY, t);
        double z = lerp(box.minZ, box.maxZ, rnd.nextDouble());
        return new Vec3(x, y, z);
    }

    private static double clamp01(double v) { return v < 0 ? 0 : (v > 1 ? 1 : v); }
    private static double lerp(double a, double b, double t) { return a + (b - a) * t; }
}