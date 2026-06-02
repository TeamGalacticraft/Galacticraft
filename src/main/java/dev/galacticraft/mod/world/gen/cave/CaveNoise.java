package dev.galacticraft.mod.world.gen.cave;

/**
 * Lightweight deterministic 3D value noise for cave shaping.
 */
public final class CaveNoise {
    private CaveNoise() {
    }

    /**
     * Samples fractal value noise.
     */
    public static double fbm(long seed, double x, double y, double z, double scale, int octaves, double persistence) {
        double value = 0.0D;
        double amplitude = 1.0D;
        double frequency = scale;
        double max = 0.0D;

        for (int i = 0; i < octaves; i++) {
            value += valueNoise(seed + i * 341873128712L, x * frequency, y * frequency, z * frequency) * amplitude;
            max += amplitude;
            amplitude *= persistence;
            frequency *= 2.0D;
        }

        return max <= 0.0D ? 0.0D : value / max;
    }

    /**
     * Samples domain-warped fractal noise.
     */
    public static double warpedFbm(long seed, double x, double y, double z, double scale, double warp, int octaves) {
        double wx = fbm(seed + 11L, x, y, z, scale * 0.55D, 2, 0.55D) * warp;
        double wy = fbm(seed + 23L, x, y, z, scale * 0.55D, 2, 0.55D) * warp;
        double wz = fbm(seed + 37L, x, y, z, scale * 0.55D, 2, 0.55D) * warp;

        return fbm(seed + 53L, x + wx, y + wy, z + wz, scale, octaves, 0.5D);
    }

    /**
     * Samples smooth 3D value noise.
     */
    public static double valueNoise(long seed, double x, double y, double z) {
        int x0 = fastFloor(x);
        int y0 = fastFloor(y);
        int z0 = fastFloor(z);

        double tx = smooth(x - x0);
        double ty = smooth(y - y0);
        double tz = smooth(z - z0);

        double c000 = random(seed, x0, y0, z0);
        double c100 = random(seed, x0 + 1, y0, z0);
        double c010 = random(seed, x0, y0 + 1, z0);
        double c110 = random(seed, x0 + 1, y0 + 1, z0);
        double c001 = random(seed, x0, y0, z0 + 1);
        double c101 = random(seed, x0 + 1, y0, z0 + 1);
        double c011 = random(seed, x0, y0 + 1, z0 + 1);
        double c111 = random(seed, x0 + 1, y0 + 1, z0 + 1);

        double x00 = lerp(c000, c100, tx);
        double x10 = lerp(c010, c110, tx);
        double x01 = lerp(c001, c101, tx);
        double x11 = lerp(c011, c111, tx);

        return lerp(lerp(x00, x10, ty), lerp(x01, x11, ty), tz);
    }

    private static double random(long seed, int x, int y, int z) {
        long h = seed;
        h ^= (long) x * 73428767L;
        h ^= (long) y * 91227153L;
        h ^= (long) z * 42317861L;
        h ^= h >>> 33;
        h *= 0xff51afd7ed558ccdL;
        h ^= h >>> 33;

        return ((h & 0xFFFFFFL) / (double) 0x7FFFFF) - 1.0D;
    }

    private static int fastFloor(double value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }

    private static double smooth(double value) {
        return value * value * value * (value * (value * 6.0D - 15.0D) + 10.0D);
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}