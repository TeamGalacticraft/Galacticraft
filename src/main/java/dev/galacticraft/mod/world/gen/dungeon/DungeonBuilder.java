package dev.galacticraft.mod.world.gen.dungeon;

import com.mojang.logging.LogUtils;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.world.gen.dungeon.config.DungeonConfig;
import dev.galacticraft.mod.world.gen.dungeon.enums.RoomType;
import dev.galacticraft.mod.world.gen.dungeon.records.PortDef;
import dev.galacticraft.mod.world.gen.dungeon.records.RoomDef;
import dev.galacticraft.mod.world.gen.dungeon.util.BoxSampling;
import dev.galacticraft.mod.world.gen.dungeon.util.DeferredCarvePiece;
import dev.galacticraft.mod.world.gen.dungeon.util.PortGeom;
import dev.galacticraft.mod.world.gen.dungeon.util.RoomHamiltonianPath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DungeonBuilder {
    private final DungeonConfig config;
    private final RandomSource random;

    private static final Logger LOGGER = LogUtils.getLogger();
    final double ROOM_MARGIN = 8.0;
    final int MAX_TRIES_PER_ROOM = 200;

    public record Room(AABB aabb, Rotation rotation, RoomDef def) {}

    public DungeonBuilder(DungeonConfig config, RandomSource random) {
        this.config = config;
        this.random = random;
    }

    public boolean build(Structure.GenerationContext ctx, StructurePiecesBuilder piecesBuilder, BlockPos surface) {
        // Initiate per dungeon values
        int targetRooms = random.nextInt(config.minRooms(), config.maxRooms());
        int criticalPathRooms = Math.round(targetRooms * config.criticalPathRooms());
        List<Integer> criticalPaths = new ArrayList<>();
        int roomsLeft = criticalPathRooms;
        int pathsleft = config.criticalPaths();
        for (int i = 0; i < config.criticalPaths(); i++) {
            int roomsInPath = Math.ceilDiv(roomsLeft, pathsleft);
            roomsLeft -= roomsInPath;
            pathsleft -= 1;
            criticalPaths.add(roomsInPath);
        }
        List<Room> dungeonRooms = new ArrayList<>();

        // Place entrance room
        List<AABB> placedRoomBoxes = new ArrayList<>();
        BlockPos entrancePosition;
        Room entranceRoom;
        try {
            RoomDef def = Galacticraft.ROOM_REGISTRY.pick(random, RoomType.ENTRANCE, roomDef -> true);
            int entranceY = surface.getY() - (10 + def.sizeY());
            int entranceX = surface.getX() + random.nextInt(-5, 5);
            int entranceZ = surface.getZ() + random.nextInt(-5, 5);
            entrancePosition = new BlockPos(entranceX, entranceY, entranceZ);
            piecesBuilder.addPiece(new TemplatePiece(def, entrancePosition, Rotation.NONE));
            Vec3i size = new Vec3i(def.sizeX(), def.sizeY(), def.sizeZ());
            AABB aabb = rotatedRoomAabb(entrancePosition, size, Rotation.NONE);
            placedRoomBoxes.add(aabb);
            entranceRoom = new Room(aabb, Rotation.NONE, def);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Place exit room
        PortDef[] endRoomEntrances;
        BlockPos endPosition;
        Rotation endRotation;
        BlockPos dungeonCenter;
        Room endRoom;
        try {
            RoomDef def = Galacticraft.ROOM_REGISTRY.pick(random, RoomType.END, roomDef -> true);
            endRoomEntrances = def.entrances();
            int endY = ctx.heightAccessor().getMinBuildHeight() + random.nextInt(20, 30);
            int endX = surface.getX() + random.nextInt(-20, 20);
            int endZ = surface.getZ() + random.nextInt(-20, 20);
            endPosition = new BlockPos(endX, endY, endZ);
            endRotation = Rotation.getRandom(random);
            piecesBuilder.addPiece(new TemplatePiece(def, endPosition, endRotation));
            dungeonCenter = new BlockPos(surface.getX(), Math.ceilDiv((entrancePosition.getY() - endPosition.getY()), 2) + endPosition.getY(), surface.getZ());
            Vec3i size = new Vec3i(def.sizeX(), def.sizeY(), def.sizeZ());
            AABB aabb = rotatedRoomAabb(endPosition, size, endRotation);
            placedRoomBoxes.add(aabb);
            endRoom = new Room(aabb, endRotation, def);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Place queen rooms
        List<Room> queenRooms = new ArrayList<>();
        try {
            for (PortDef entrance : endRoomEntrances) {
                RoomDef def = Galacticraft.ROOM_REGISTRY.pick(random, RoomType.QUEEN, roomDef -> true);
                Direction entranceDirection = endRotation.rotate(entrance.facing());
                BlockPos roomPosition = endPosition.offset(entranceDirection.getNormal().multiply(random.nextInt(20, 40)));
                roomPosition = roomPosition.offset(random.nextInt(-10, 10), random.nextInt(-10, 10), random.nextInt(-10, 10));
                Direction roomExitFacing = Arrays.stream(def.exits()).findFirst().get().facing();
                Rotation rotation = rotationNeededToMatch(roomExitFacing, entranceDirection.getOpposite());
                piecesBuilder.addPiece(new TemplatePiece(def, roomPosition, rotation));
                Vec3i size = new Vec3i(def.sizeX(), def.sizeY(), def.sizeZ());
                AABB aabb = rotatedRoomAabb(roomPosition, size, rotation);
                placedRoomBoxes.add(aabb);
                Room room = new Room(aabb, rotation, def);
                queenRooms.add(room);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Build bounding box of dungeon
        AABB dungeonBox = new AABB(entrancePosition.getCenter(), endPosition.getCenter());
        dungeonBox = dungeonBox.inflate(random.nextInt(30, 50), 1, random.nextInt(30, 50));

        // Place critical path rooms
        try {
            for (int i = 0; i < criticalPathRooms; i++) {
                boolean placed = false;

                for (int tries = 0; tries < MAX_TRIES_PER_ROOM && !placed; tries++) {
                    RoomDef def = Galacticraft.ROOM_REGISTRY.pick(random, RoomType.BASIC, roomDef -> true);
                    // 1) Biased random position inside dungeonBox
                    Vec3 position = BoxSampling.randomPointInAabbBiasedY(
                            dungeonBox, random,
                            List.of(
                                    new BoxSampling.Knot(0.00, 1.20), // min Y
                                    new BoxSampling.Knot(0.20, 1.60), // 20% above min
                                    new BoxSampling.Knot(1.00, 1.00)  // max Y
                            )
                    );

                    // Use block-aligned min-corner placement (adjust if your anchor differs)
                    BlockPos roomPos = new BlockPos((int) position.x, (int) position.y, (int) position.z);

                    // 2) Random rotation
                    Rotation rot = Rotation.getRandom(random);

                    // 3) Build rotated AABB around its center
                    Vec3i size = new Vec3i(def.sizeX(), def.sizeY(), def.sizeZ());
                    AABB roomAabb = rotatedRoomAabb(roomPos, size, rot);

                    // 4) Margin check vs any already-placed room
                    AABB expanded = roomAabb.inflate(ROOM_MARGIN);
                    boolean intersects = false;
                    for (AABB prior : placedRoomBoxes) {
                        if (expanded.intersects(prior)) { // candidate keeps margin from prior
                            intersects = true;
                            break;
                        }
                    }
                    if (intersects) continue;

                    // 5) Accept: save AABB and add piece
                    piecesBuilder.addPiece(new TemplatePiece(def, roomPos, rot));
                    placedRoomBoxes.add(roomAabb);
                    dungeonRooms.add(new Room(roomAabb, rot, def));
                    placed = true;
                }

                if (!placed) {
                    LOGGER.error("Could not place room. Used {} tries", MAX_TRIES_PER_ROOM);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Create all critical paths
        List<List<Room>> orderedCriticalPaths = new ArrayList<>();
        List<RoomHamiltonianPath.PathResult> criticalPathResults = new ArrayList<>();
        try {
            List<Room> dungeonRoomsCopy = new ArrayList<>(dungeonRooms);
            List<Room> queenRoomsCopy = new ArrayList<>(queenRooms);
            for (int i = 0; i < config.criticalPaths(); i++) {
                List<Room> roomsInPath = new ArrayList<>();
                roomsInPath.add(entranceRoom); //index 0
                for (int j = 0; j < criticalPaths.get(i); j++) {
                    roomsInPath.add(dungeonRoomsCopy.remove(random.nextInt(0, dungeonRoomsCopy.size())));
                }
                // last index
                roomsInPath.add(queenRoomsCopy.remove(random.nextInt(0, queenRoomsCopy.size())));
                Optional<RoomHamiltonianPath.PathResult> solved = RoomHamiltonianPath.solve(roomsInPath, 0, roomsInPath.size() - 1);
                if (solved.isPresent()) {
                    List<Room> orderedRooms =  new ArrayList<>();
                    for (int k = 0; k < roomsInPath.toArray().length; k++) {
                        orderedRooms.add(roomsInPath.get(solved.get().order.get(k)));
                    }
                    orderedCriticalPaths.add(orderedRooms);
                    criticalPathResults.add(solved.get());
                } else {
                    LOGGER.error("Could not solve path");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Pathfind and carve from queen rooms to end room
        try {
            for (Room queenRoom : queenRooms) {
                PortDef exitPort = Arrays.stream(queenRoom.def.exits()).findFirst().get();

                // Find the entrance port that matches the queen rooms exit port
                Optional<PortDef> entrancePort = Arrays.stream(endRoom.def().entrances())
                        .filter(p -> endRoom.rotation().rotate(p.facing()) == queenRoom.rotation().rotate(exitPort.facing()).getOpposite())
                        .findFirst();

                if (entrancePort.isPresent()) {
                    // Get the center position of the entrance and exit port
                    BlockPos exitPortPosition = PortGeom.localToWorld(exitPort.localCenterBlock(), queenRoom.def().sizeX(), queenRoom.def().sizeY(), queenRoom.def().sizeZ(), queenRoom.aabb(), queenRoom.rotation());
                    BlockPos entrancePortPosition = PortGeom.localToWorld(entrancePort.get().localCenterBlock(), endRoom.def().sizeX(), endRoom.def().sizeY(), endRoom.def().sizeZ(), endRoom.aabb(), endRoom.rotation());

                    // Move 2 blocks outwards from port before carving
                    exitPortPosition = exitPortPosition.offset(queenRoom.rotation().rotate(exitPort.facing()).getNormal().multiply(2));
                    entrancePortPosition = entrancePortPosition.offset(endRoom.rotation().rotate(entrancePort.get().facing()).getNormal().multiply(2));

                    // Carve straight line
                    piecesBuilder.addPiece(new DeferredCarvePiece(exitPortPosition, entrancePortPosition, 1));
                } else {
                    LOGGER.error("Could not find entrance port");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Pathfind and carve all critical paths
        try {
            for (int i = 0; i < orderedCriticalPaths.size(); i++) {
                for (int j = 0; j < orderedCriticalPaths.get(i).size() - 1; j++) {
                    RoomHamiltonianPath.PathResult pathResult = criticalPathResults.get(i);
                    // Get the rooms we are carving between
                    Room room1 = orderedCriticalPaths.get(i).get(j);
                    Room room2 = orderedCriticalPaths.get(i).get(j + 1);

                    // Get the exit port from room 1 and entrance port of room 2
                    PortDef exitPort = Arrays.stream(room1.def().exits()).toList().get(pathResult.portPairs.get(j)[0]);
                    PortDef entrancePort = Arrays.stream(room2.def().entrances()).toList().get(pathResult.portPairs.get(j)[1]);

                    // Get the center position of the entrance and exit port
                    BlockPos exitPortPosition = PortGeom.localToWorld(exitPort.localCenterBlock(), room1.def().sizeX(), room1.def().sizeY(), room1.def().sizeZ(), room1.aabb(), room1.rotation());
                    BlockPos entrancePortPosition = PortGeom.localToWorld(entrancePort.localCenterBlock(), room2.def().sizeX(), room2.def().sizeY(), room2.def().sizeZ(), room2.aabb(), room2.rotation());

                    // Move 2 blocks outwards from port before carving
                    exitPortPosition = exitPortPosition.offset(room1.rotation().rotate(exitPort.facing()).getNormal().multiply(2));
                    entrancePortPosition = entrancePortPosition.offset(room2.rotation().rotate(entrancePort.facing()).getNormal().multiply(2));

                    // Carve straight line
                    piecesBuilder.addPiece(new DeferredCarvePiece(exitPortPosition, entrancePortPosition, 1));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return true;
    }

    private Rotation rotationNeededToMatch(Direction originalDirection, Direction desiredDirection) {
        if (originalDirection == null || desiredDirection == null) {
            throw new IllegalArgumentException("Directions must not be null.");
        }

        // No change needed
        if (originalDirection == desiredDirection) {
            return Rotation.NONE;
        }

        // Try the three non-trivial Y-rotations in any order
        Rotation[] candidates = {
                Rotation.CLOCKWISE_90,
                Rotation.CLOCKWISE_180,
                Rotation.COUNTERCLOCKWISE_90
        };

        for (Rotation rot : candidates) {
            if (rot.rotate(originalDirection) == desiredDirection) {
                return rot;
            }
        }

        // If we get here, the mapping isn't possible with Y-rotation (e.g., UP/DOWN to horizontal, or vice versa)
        throw new IllegalArgumentException("Cannot rotate " + originalDirection + " to " + desiredDirection + " with Y-axis rotations.");
    }

    /**
     * Compute a world-space AABB for a room placed with its MIN-corner at `minCorner`,
     * rotated about the box center by the given Y-rotation.
     *
     * For Y-rotations (NONE/90/180/270), rotating an axis-aligned box about its center
     * simply swaps X/Z extents on 90/270; center stays the same.
     */
    private static AABB rotatedRoomAabb(BlockPos minCorner, Vec3i size, Rotation rot) {
        // Unrotated box from min-corner
        BlockPos maxCorner = minCorner.offset(size); // or: minCorner.offset(size.getX(), size.getY(), size.getZ())
        AABB base = new AABB(minCorner.getCenter(), maxCorner.getCenter());

        // Center of the unrotated box
        Vec3 c = base.getCenter();

        // Half extents; swap X/Z on 90° or 270° (COUNTERCLOCKWISE_90)
        double hx, hy, hz;
        boolean swapXZ = (rot == Rotation.CLOCKWISE_90) || (rot == Rotation.COUNTERCLOCKWISE_90);
        hx = (swapXZ ? size.getZ() : size.getX()) / 2.0;
        hy = size.getY() / 2.0;
        hz = (swapXZ ? size.getX() : size.getZ()) / 2.0;

        // Rebuild AABB from center ± half extents
        return new AABB(
                c.x - hx, c.y - hy, c.z - hz,
                c.x + hx, c.y + hy, c.z + hz
        );
    }
}
