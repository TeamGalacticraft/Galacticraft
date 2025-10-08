package dev.galacticraft.mod.world.gen.dungeon;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

import java.util.List;

public final class CorridorRouter {
    public static void carve(WorldGenLevel level, List<BlockPos> path, int aperture) {
        carveWith(level, path, aperture, net.minecraft.world.level.block.Blocks.AIR);
    }

    public static void carveWith(WorldGenLevel level, List<BlockPos> path, int aperture,
                                 net.minecraft.world.level.block.Block block) {
        if (level == null || path == null || path.isEmpty() || aperture <= 0) return;

        for (int i = 0; i < path.size(); i++) {
            BlockPos cur = path.get(i);
            carveCubeWith(level, cur, aperture, block);

            if (i == 0) continue;
            BlockPos prev = path.get(i - 1);
            int dx = Integer.compare(cur.getX(), prev.getX());
            int dy = Integer.compare(cur.getY(), prev.getY());
            int dz = Integer.compare(cur.getZ(), prev.getZ());
            int steps = Math.max(Math.max(Math.abs(cur.getX() - prev.getX()),
                            Math.abs(cur.getY() - prev.getY())),
                    Math.abs(cur.getZ() - prev.getZ()));
            for (int s = 1; s < steps; s++) {
                carveCubeWith(level, prev.offset(dx * s, dy * s, dz * s), aperture, block);
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