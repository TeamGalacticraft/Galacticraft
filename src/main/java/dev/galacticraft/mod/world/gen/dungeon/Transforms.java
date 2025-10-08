package dev.galacticraft.mod.world.gen.dungeon;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;

final class Transforms {
    private Transforms() {
    }

    /**
     * Rotate local (template) coords around MIN corner and translate by world origin.
     */
    static BlockPos worldOfLocalMin(BlockPos local, BlockPos origin, Vec3i size, Rotation rot) {
        int ox = origin.getX(), oy = origin.getY(), oz = origin.getZ();
        int lx = local.getX(), ly = local.getY(), lz = local.getZ();
        int sx = size.getX(), sz = size.getZ();

        return switch (rot) {
            case NONE -> new BlockPos(ox + lx, oy + ly, oz + lz);
            case CLOCKWISE_90 ->       // (x,z) = (sz-1 - lz, lx)
                    new BlockPos(ox + (sz - 1 - lz), oy + ly, oz + lx);
            case CLOCKWISE_180 ->      // (x,z) = (sx-1 - lx, sz-1 - lz)
                    new BlockPos(ox + (sx - 1 - lx), oy + ly, oz + (sz - 1 - lz));
            case COUNTERCLOCKWISE_90 ->// (x,z) = (lz, sx-1 - lx)
                    new BlockPos(ox + lz, oy + ly, oz + (sx - 1 - lx));
        };
    }

    /**
     * Rotate a horizontal facing by the same yaw. Vertical facings pass through.
     */
    static Direction rotateFacingYaw(Direction d, Rotation r) {
        if (!d.getAxis().isHorizontal()) return d;
        return switch (r) {
            case NONE -> d;
            case CLOCKWISE_90 -> d.getClockWise();
            case CLOCKWISE_180 -> d.getClockWise().getClockWise();
            case COUNTERCLOCKWISE_90 -> d.getCounterClockWise();
        };
    }

    static Vec3i rotatedSize(Vec3i s, Rotation r) {
        return switch (r) {
            case NONE, CLOCKWISE_180 -> s;
            default -> new Vec3i(s.getZ(), s.getY(), s.getX());
        };
    }
}