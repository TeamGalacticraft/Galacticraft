package dev.galacticraft.mod.world.gen.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

import java.util.List;

public final class CorridorRouter {
    public static void carveWith(WorldGenLevel level, List<BlockPos> path, int aperture,
                                 net.minecraft.world.level.block.Block block) {
        carveGradientWith(level, path, aperture, aperture, block);
    }

    public static void carveGradientWith(WorldGenLevel level, List<BlockPos> path,
                                         int aAperture, int bAperture,
                                         net.minecraft.world.level.block.Block block) {
        if (level == null || path == null || path.isEmpty()) return;

        final int n = path.size();
        // helper: force odd >= 3
        java.util.function.IntUnaryOperator norm = ap -> {
            int v = Math.max(3, ap);
            return (v & 1) == 1 ? v : v + 1;
        };
        final int aAp = norm.applyAsInt(aAperture);
        final int bAp = norm.applyAsInt(bAperture);

        for (int i = 0; i < n; i++) {
            BlockPos cur = path.get(i);

            // t in [0,1], linear ramp from aAp → bAp
            float t = (n <= 1) ? 0f : (i / (float) (n - 1));
            int apHere = Math.round(aAp + t * (bAp - aAp));
            apHere = norm.applyAsInt(apHere);

            carveCubeWith(level, cur, apHere, block);

            // densify between points to avoid gaps on diagonals
            if (i == 0) continue;
            BlockPos prev = path.get(i - 1);
            int dx = Integer.compare(cur.getX(), prev.getX());
            int dy = Integer.compare(cur.getY(), prev.getY());
            int dz = Integer.compare(cur.getZ(), prev.getZ());
            int steps = Math.max(Math.max(Math.abs(cur.getX() - prev.getX()),
                            Math.abs(cur.getY() - prev.getY())),
                    Math.abs(cur.getZ() - prev.getZ()));
            for (int s = 1; s < steps; s++) {
                // interpolate aperture for the substep too
                float ts = (n <= 1) ? 0f : ((i - 1 + (s / (float) steps)) / (float) (n - 1));
                int apStep = Math.round(aAp + ts * (bAp - aAp));
                apStep = norm.applyAsInt(apStep);
                carveCubeWith(level, prev.offset(dx * s, dy * s, dz * s), apStep, block);
            }
        }
    }

    private static void carveCubeWith(WorldGenLevel level, BlockPos center, int aperture,
                                      net.minecraft.world.level.block.Block block) {
        int half = Math.max(0, (aperture - 1) / 2);
        BlockPos min = center.offset(-half, -half, -half);
        for (int x = 0; x < aperture; x++) {
            for (int y = 0; y < aperture; y++) {
                for (int z = 0; z < aperture; z++) {
                    level.setBlock(min.offset(x, y, z), block.defaultBlockState(), 2);
                }
            }
        }
    }
}