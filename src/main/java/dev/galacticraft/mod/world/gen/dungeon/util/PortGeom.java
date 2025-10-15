package dev.galacticraft.mod.world.gen.dungeon.util;

import dev.galacticraft.mod.world.gen.dungeon.DungeonBuilder;
import dev.galacticraft.mod.world.gen.dungeon.records.PortDef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Single source of truth for room/port transforms.
 */
public final class PortGeom {
    private PortGeom() {
    }

    // ---------- public API ----------

    /**
     * Integer-floor pivot == template “center”; identical everywhere.
     */
    public static BlockPos pivot(int sx, int sy, int sz) {
        return new BlockPos(sx / 2, sy / 2, sz / 2);
    }

    /**
     * Local-space AABB (min & size) after applying rot around pivot(size/2).
     */
    public static LocalAabb rotatedLocalAabb(int sx, int sy, int sz, Rotation rot) {
        BlockPos p = pivot(sx, sy, sz);
        return rotatedLocalAabb(sx, sy, sz, rot, p);
    }

    public static Vec3 safeUnit(Vec3 v, Vec3 fallback) {
        double len2 = v.lengthSqr();
        if (len2 < 1e-12) return fallback.normalize();  // never NaN
        return v.scale(1.0 / Math.sqrt(len2));
    }

    public static Direction facingFromXZ(Vec3 v, Direction fallback) {
        // pick major axis in XZ plane; if near-zero, use fallback
        double ax = Math.abs(v.x);
        double az = Math.abs(v.z);
        if (ax < 1e-9 && az < 1e-9) return fallback;
        if (ax >= az) return (v.x >= 0) ? Direction.EAST : Direction.WEST;
        return (v.z >= 0) ? Direction.SOUTH : Direction.NORTH;
    }

    // AABB of a rotated template placed with world MIN corner = minAfterRot.
    // Uses the same pivot logic as RoomGenerator (pivot = floor(size/2)).
    public static AABB rotatedRoomAabb(BlockPos minAfterRot, Vec3i size, Rotation rot) {
        int sx = size.getX(), sy = size.getY(), sz = size.getZ();
        BlockPos pivot = new BlockPos(sx / 2, sy / 2, sz / 2);

        // rotate the 8 local corners, find min/max in local-rotated space
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        BlockPos[] corners = {
                new BlockPos(0, 0, 0), new BlockPos(sx - 1, 0, 0), new BlockPos(0, sy - 1, 0), new BlockPos(0, 0, sz - 1),
                new BlockPos(sx - 1, sy - 1, 0), new BlockPos(sx - 1, 0, sz - 1), new BlockPos(0, sy - 1, sz - 1), new BlockPos(sx - 1, sy - 1, sz - 1)
        };
        for (BlockPos c : corners) {
            BlockPos rc = net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
                    .transform(c, net.minecraft.world.level.block.Mirror.NONE, rot, pivot);
            if (rc.getX() < minX) minX = rc.getX();
            if (rc.getY() < minY) minY = rc.getY();
            if (rc.getZ() < minZ) minZ = rc.getZ();
            if (rc.getX() > maxX) maxX = rc.getX();
            if (rc.getY() > maxY) maxY = rc.getY();
            if (rc.getZ() > maxZ) maxZ = rc.getZ();
        }

        // world min/max after rotation
        BlockPos wMin = minAfterRot;
        BlockPos sizeRot = new BlockPos(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
        BlockPos wMax = wMin.offset(sizeRot.getX(), sizeRot.getY(), sizeRot.getZ());

        // AABB uses half-open block bounds [min, max) → use doubles at block edges
        return new AABB(
                wMin.getX(), wMin.getY(), wMin.getZ(),
                wMax.getX(), wMax.getY(), wMax.getZ()
        );
    }

    // Vanilla-congruent “how many quarter turns from from→to” (horizontal only)
    public static @Nullable Rotation yRotationBetween(Direction from, Direction to) {
        if (!from.getAxis().isHorizontal() || !to.getAxis().isHorizontal()) return null;
        int turns = (to.get2DDataValue() - from.get2DDataValue()) & 3;
        return switch (turns) {
            case 0 -> Rotation.NONE;
            case 1 -> Rotation.CLOCKWISE_90;
            case 2 -> Rotation.CLOCKWISE_180;
            case 3 -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    // “Pick the Y-rotation that maps originalDirection → desiredDirection”
    public static Rotation rotationNeededToMatch(Direction originalDirection, Direction desiredDirection) {
        if (originalDirection == null || desiredDirection == null) throw new IllegalArgumentException("null facing");
        if (!originalDirection.getAxis().isHorizontal() || !desiredDirection.getAxis().isHorizontal())
            return Rotation.NONE;
        Rotation r = yRotationBetween(originalDirection, desiredDirection);
        if (r != null) return r;
        throw new IllegalArgumentException("Cannot rotate " + originalDirection + " to " + desiredDirection + " via Y only");
    }

    /**
     * World MIN for a rotated placement + the corresponding local->world origin.
     */
    public static PlacementOrigin originForPlacedMin(int sx, int sy, int sz, Rotation rot, BlockPos placedMin) {
        LocalAabb rl = rotatedLocalAabb(sx, sy, sz, rot);
        BlockPos origin = placedMin.subtract(rl.min());
        return new PlacementOrigin(pivot(sx, sy, sz), origin);
    }

    /**
     * Transform a local position to world using placed MIN-corner and rotation (integer-grid safe).
     */
    public static BlockPos localToWorld(BlockPos local, int sx, int sy, int sz, BlockPos placedMin, Rotation rot) {
        PlacementOrigin po = originForPlacedMin(sx, sy, sz, rot, placedMin);
        BlockPos rotated = StructureTemplate.transform(local, Mirror.NONE, rot, po.pivot());
        return rotated.offset(po.origin());
    }

    /**
     * Overload that accepts an AABB (floors its min corner).
     */
    public static BlockPos localToWorld(BlockPos local, int sx, int sy, int sz, AABB placedAabb, Rotation rot) {
        BlockPos placedMin = new BlockPos(floor(placedAabb.minX), floor(placedAabb.minY), floor(placedAabb.minZ));
        return localToWorld(local, sx, sy, sz, placedMin, rot);
    }

    /**
     * Compute a BoundingBox for a rotated placement anchored at world MIN.
     */
    public static BoundingBox computeBoundingBox(int sx, int sy, int sz, BlockPos placedMin, Rotation rot) {
        LocalAabb rl = rotatedLocalAabb(sx, sy, sz, rot);
        BlockPos minW = placedMin;
        BlockPos maxW = placedMin.offset(rl.sizeX() - 1, rl.sizeY() - 1, rl.sizeZ() - 1);
        return BoundingBox.fromCorners(minW, maxW);
    }

    /**
     * Center of a (possibly multi-cell) port rectangle in world coords (sub-block).
     */
    public static Vec3 worldCenter(DungeonBuilder.Room room, PortDef port, int sx, int sy, int sz) {
        BlockPos wMin = localToWorld(port.min(), sx, sy, sz, room.aabb(), room.rotation());
        BlockPos wMax = localToWorld(port.max(), sx, sy, sz, room.aabb(), room.rotation());
        return new Vec3(
                (wMin.getX() + wMax.getX()) * 0.5 + 0.5,
                (wMin.getY() + wMax.getY()) * 0.5 + 0.5,
                (wMin.getZ() + wMax.getZ()) * 0.5 + 0.5
        );
    }

    // ---------- internals ----------

    private static int floor(double d) {
        return (int) Math.floor(d);
    }

    private static LocalAabb rotatedLocalAabb(int sx, int sy, int sz, Rotation rot, BlockPos pivot) {
        BlockPos[] corners = {
                new BlockPos(0, 0, 0),
                new BlockPos(sx - 1, 0, 0),
                new BlockPos(0, sy - 1, 0),
                new BlockPos(0, 0, sz - 1),
                new BlockPos(sx - 1, sy - 1, 0),
                new BlockPos(sx - 1, 0, sz - 1),
                new BlockPos(0, sy - 1, sz - 1),
                new BlockPos(sx - 1, sy - 1, sz - 1)
        };

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for (BlockPos c : corners) {
            BlockPos rc = StructureTemplate.transform(c, Mirror.NONE, rot, pivot);
            if (rc.getX() < minX) minX = rc.getX();
            if (rc.getY() < minY) minY = rc.getY();
            if (rc.getZ() < minZ) minZ = rc.getZ();
            if (rc.getX() > maxX) maxX = rc.getX();
            if (rc.getY() > maxY) maxY = rc.getY();
            if (rc.getZ() > maxZ) maxZ = rc.getZ();
        }

        BlockPos min = new BlockPos(minX, minY, minZ);
        BlockPos size = new BlockPos(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
        return new LocalAabb(min, size);
    }

    // tiny value classes
    public record LocalAabb(BlockPos min, BlockPos size) {
        public int sizeX() {
            return size.getX();
        }

        public int sizeY() {
            return size.getY();
        }

        public int sizeZ() {
            return size.getZ();
        }
    }

    public record PlacementOrigin(BlockPos pivot, BlockPos origin) {
    }
}