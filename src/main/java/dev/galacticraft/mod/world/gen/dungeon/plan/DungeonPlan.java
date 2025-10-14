package dev.galacticraft.mod.world.gen.dungeon.plan;

import dev.galacticraft.mod.world.gen.dungeon.util.Bitmask;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import dev.galacticraft.mod.world.gen.dungeon.records.RoomDef;

import java.util.List;

public final class DungeonPlan {
    public static final class PlannedRoom {
        public final RoomDef def;
        public final Rotation rotation;
        public final BlockPos minCorner;   // the same anchor you already use to place TemplatePiece
        public final AABB aabb;            // world-space box (rotatedRoomAabb result)

        public PlannedRoom(RoomDef def, Rotation rotation, BlockPos minCorner, AABB aabb) {
            this.def = def;
            this.rotation = rotation;
            this.minCorner = minCorner;
            this.aabb = aabb;
        }
    }

    public final List<PlannedRoom> rooms;
    public final Bitmask corridors;   // rasterized union of all routed corridors
    public final AABB dungeonAabb;    // big umbrella box

    public DungeonPlan(List<PlannedRoom> rooms, Bitmask corridors, AABB dungeonAabb) {
        this.rooms = rooms;
        this.corridors = corridors;
        this.dungeonAabb = dungeonAabb;
    }
}
