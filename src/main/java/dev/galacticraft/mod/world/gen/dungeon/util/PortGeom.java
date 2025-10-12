package dev.galacticraft.mod.world.gen.dungeon.util;

import dev.galacticraft.mod.world.gen.dungeon.DungeonBuilder;
import dev.galacticraft.mod.world.gen.dungeon.records.PortDef;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Helpers for PortDef geometry transforms. */
public final class PortGeom {

    private PortGeom() {}

    /**
     * Convert a local (template) BlockPos to world BlockPos after applying Rotation and translating to room AABB.
     * Uses vanilla structure rotation mapping.
     */
    public static BlockPos localToWorld(BlockPos local, int sizeX, int sizeY, int sizeZ,
                                        AABB placedAabb, Rotation rot) {
        int lx = local.getX();
        int ly = local.getY();
        int lz = local.getZ();

        int rx = lx, rz = lz;
        switch (rot) {
            case NONE -> { /* rx=lx, rz=lz */ }
            case CLOCKWISE_90 -> { rx = sizeZ - 1 - lz; rz = lx; }
            case CLOCKWISE_180 -> { rx = sizeX - 1 - lx; rz = sizeZ - 1 - lz; }
            case COUNTERCLOCKWISE_90 -> { rx = lz; rz = sizeX - 1 - lx; }
        }
        // AABB min corner is the world origin of the placed template
        int wx = (int)Math.floor(placedAabb.minX) + rx;
        int wy = (int)Math.floor(placedAabb.minY) + ly;
        int wz = (int)Math.floor(placedAabb.minZ) + rz;
        return new BlockPos(wx, wy, wz);
    }

    /** World center of the port rectangle (sub-block precision). */
    public static Vec3 worldCenter(DungeonBuilder.Room room, PortDef port, int sizeX, int sizeY, int sizeZ) {
        // local sub-block center
        Vec3 lc = port.localCenter();
        // rotate point (floor/ceil nuance doesn’t matter since we’re sub-block)
        int lx = (int)Math.floor(lc.x);
        int ly = (int)Math.floor(lc.y);
        int lz = (int)Math.floor(lc.z);

        // rotate corners min/max then average in world for better precision:
        BlockPos wMin = localToWorld(port.min(), sizeX, sizeY, sizeZ, room.aabb(), room.rotation());
        BlockPos wMax = localToWorld(port.max(), sizeX, sizeY, sizeZ, room.aabb(), room.rotation());

        return new Vec3(
                (wMin.getX() + wMax.getX()) * 0.5 + 0.5,
                (wMin.getY() + wMax.getY()) * 0.5 + 0.5,
                (wMin.getZ() + wMax.getZ()) * 0.5 + 0.5
        );
    }
}