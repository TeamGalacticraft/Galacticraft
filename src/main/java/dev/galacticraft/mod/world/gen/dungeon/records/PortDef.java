package dev.galacticraft.mod.world.gen.dungeon.records;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * Port defined in LOCAL template coordinates (relative to template min origin).
 * The port is a rectangular plane on a template face, spanning [min..max] (inclusive).
 */
public record PortDef(
        String name,            // optional label, e.g. "north_exit_1"
        boolean entrance,       // true = entrance port
        boolean exit,           // true = exit port
        Direction facing,       // face normal in LOCAL template frame
        BlockPos min,           // local min corner (inclusive)
        BlockPos max            // local max corner (inclusive)
) {
    /** Integer center in local coords (block center). */
    public BlockPos localCenterBlock() {
        return new BlockPos(
                (min.getX() + max.getX()) >> 1,
                (min.getY() + max.getY()) >> 1,
                (min.getZ() + max.getZ()) >> 1
        );
    }

    /** Sub-block center in local coords (cube center of the plane area). */
    public net.minecraft.world.phys.Vec3 localCenter() {
        return new net.minecraft.world.phys.Vec3(
                (min.getX() + max.getX()) * 0.5 + 0.5,
                (min.getY() + max.getY()) * 0.5 + 0.5,
                (min.getZ() + max.getZ()) * 0.5 + 0.5
        );
    }

    /** Width × height of the port rectangle (in blocks). */
    public int area() {
        int dx = (max.getX() - min.getX()) + 1;
        int dy = (max.getY() - min.getY()) + 1;
        int dz = (max.getZ() - min.getZ()) + 1;
        // On a face, one of dx/dy/dz must be 1; area = product of the other two.
        return switch (facing.getAxis()) {
            case X -> dy * dz;
            case Y -> dx * dz;
            case Z -> dx * dy;
        };
    }
}