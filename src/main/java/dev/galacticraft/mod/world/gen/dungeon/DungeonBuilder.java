package dev.galacticraft.mod.world.gen.dungeon;

import com.mojang.logging.LogUtils;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.world.gen.dungeon.config.DungeonConfig;
import dev.galacticraft.mod.world.gen.dungeon.enums.RoomType;
import dev.galacticraft.mod.world.gen.dungeon.records.*;
import dev.galacticraft.mod.world.gen.dungeon.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DungeonBuilder {
    private final DungeonConfig config;
    private final RandomSource random;

    private static final Logger LOGGER = LogUtils.getLogger();
    final double ROOM_MARGIN = 8.0;
    final int MAX_TRIES_PER_ROOM = 200;
    final int CORRIDOR_PLACEMENT_OFFSET = 1 ;
    final int CORRIDOR_RADIUS = 1;
    final int CORRIDOR_PREFLIGHT = 1;
    final int DILATION = 1;

    public record Room(AABB aabb, Rotation rotation, RoomDef def) {}

    public DungeonBuilder(DungeonConfig config, RandomSource random) {
        this.config = config;
        this.random = random;
    }

    private DungeonResult generateDungeon(DungeonInput input) {
        List<Room> dungeonRooms = new ArrayList<>();
        Bitmask mask = new Bitmask();
        List<NegotiatedRouter.Net> nets = new ArrayList<>();
        List<AABB> placedRoomBoxes = new ArrayList<>();
        HashMap<SectionPos, List<BlockData>> blockData = new HashMap<SectionPos, List<BlockData>>();

        // Calculate entrance room
        BlockPos entrancePosition;
        Room entranceRoom;
        try {
            RoomDef def = Galacticraft.ROOM_REGISTRY.pick(random, RoomType.ENTRANCE, roomDef -> true);
            int entranceY = input.surface().getY() - (10 + def.sizeY());
            int entranceX = input.surface().getX() + random.nextInt(-5, 5);
            int entranceZ = input.surface().getZ() + random.nextInt(-5, 5);
            entrancePosition = new BlockPos(entranceX, entranceY, entranceZ);

            addData(blockData, new RoomGenerator(def, entrancePosition, Rotation.NONE).getBlocks(Galacticraft.SCANNER));

            Vec3i size = new Vec3i(def.sizeX(), def.sizeY(), def.sizeZ());
            AABB aabb = rotatedRoomAabb(entrancePosition, size, Rotation.NONE);
            placedRoomBoxes.add(aabb);
            mask.add(aabb);
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
            int endY = input.minBuildHeight() + random.nextInt(20, 30);
            int endX = input.surface().getX() + random.nextInt(-20, 20);
            int endZ = input.surface().getZ() + random.nextInt(-20, 20);
            endPosition = new BlockPos(endX, endY, endZ);
            endRotation = Rotation.getRandom(random);

            addData(blockData, new RoomGenerator(def, endPosition, endRotation).getBlocks(Galacticraft.SCANNER));

            dungeonCenter = new BlockPos(input.surface().getX(), Math.ceilDiv((entrancePosition.getY() - endPosition.getY()), 2) + endPosition.getY(), input.surface().getZ());
            Vec3i size = new Vec3i(def.sizeX(), def.sizeY(), def.sizeZ());
            AABB aabb = rotatedRoomAabb(endPosition, size, endRotation);
            placedRoomBoxes.add(aabb);
            mask.add(aabb);
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

                addData(blockData, new RoomGenerator(def, roomPosition, rotation).getBlocks(Galacticraft.SCANNER));

                Vec3i size = new Vec3i(def.sizeX(), def.sizeY(), def.sizeZ());
                AABB aabb = rotatedRoomAabb(roomPosition, size, rotation);
                placedRoomBoxes.add(aabb);
                mask.add(aabb);
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
            for (int i = 0; i < input.criticalPathRooms(); i++) {
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
                    boolean intersects = mask.contains(expanded);
                    if (intersects) continue;
                    // candidate keeps margin from prior

                    // 5) Accept: save AABB and add piece

                    addData(blockData, new RoomGenerator(def, roomPos, rot).getBlocks(Galacticraft.SCANNER));

                    placedRoomBoxes.add(roomAabb);
                    mask.add(roomAabb);
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
                for (int j = 0; j < input.criticalPaths().get(i); j++) {
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
                Direction exitPortRotation = queenRoom.rotation().rotate(exitPort.facing());
                Optional<PortDef> entrancePort = Arrays.stream(endRoom.def().entrances())
                        .filter(p -> endRoom.rotation().rotate(p.facing()) == exitPortRotation.getOpposite())
                        .findFirst();

                if (entrancePort.isPresent()) {
                    // Get the center position of the entrance and exit port
                    BlockPos exitPortPosition = PortGeom.localToWorld(exitPort.localCenterBlock(), queenRoom.def().sizeX(), queenRoom.def().sizeY(), queenRoom.def().sizeZ(), queenRoom.aabb(), queenRoom.rotation());
                    BlockPos entrancePortPosition = PortGeom.localToWorld(entrancePort.get().localCenterBlock(), endRoom.def().sizeX(), endRoom.def().sizeY(), endRoom.def().sizeZ(), endRoom.aabb(), endRoom.rotation());

                    // Move 2 blocks outwards from port before carving
                    exitPortPosition = exitPortPosition.offset(exitPortRotation.getNormal().multiply(CORRIDOR_PLACEMENT_OFFSET));
                    Direction entrancePortRotation = endRoom.rotation().rotate(entrancePort.get().facing());
                    entrancePortPosition = entrancePortPosition.offset(entrancePortRotation.getNormal().multiply(CORRIDOR_PLACEMENT_OFFSET));

                    // Add to shared carver
                    nets.add(new NegotiatedRouter.Net(
                            exitPortPosition, exitPortRotation,
                            entrancePortPosition, entrancePortRotation,
                            CORRIDOR_PREFLIGHT
                    ));
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
                    Direction exitPortRotation = room1.rotation().rotate(exitPort.facing());
                    exitPortPosition = exitPortPosition.offset(exitPortRotation.getNormal().multiply(CORRIDOR_PLACEMENT_OFFSET));
                    Direction entrancePortRotation = room2.rotation().rotate(entrancePort.facing());
                    entrancePortPosition = entrancePortPosition.offset(room2.rotation().rotate(entrancePort.facing()).getNormal().multiply(CORRIDOR_PLACEMENT_OFFSET));

                    // Add to shared carver
                    nets.add(new NegotiatedRouter.Net(
                            exitPortPosition, exitPortRotation,
                            entrancePortPosition, entrancePortRotation,
                            CORRIDOR_PREFLIGHT
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Find all unused exits and branch off until we reach room limit
        record PortRoomMapping(PortDef port, Room room) {};
        List<PortRoomMapping> availableExitPorts = new ArrayList<>();
        try {
            // Find all unused exits on the critical paths
            for (int i = 0; i < orderedCriticalPaths.size(); i++) {
                List<Room> rooms = orderedCriticalPaths.get(i);
                for (int j = 0; j < rooms.size() - 1; j++) {
                    if (rooms.get(j).def.hasMoreThanOneExit()) {
                        RoomHamiltonianPath.PathResult pathResult = criticalPathResults.get(i);
                        List<PortDef> exits = new ArrayList<>(Arrays.stream(rooms.get(j).def.exits()).toList());
                        exits.remove(pathResult.portPairs.get(j)[0]);
                        for (PortDef exit : exits) {
                            availableExitPorts.add(new PortRoomMapping(exit, rooms.get(j)));
                        }
                    }
                }
            }

            // Start branching randomly
            while (!availableExitPorts.isEmpty() && placedRoomBoxes.size() < input.targetRooms()) {
                PortRoomMapping availableExitPort = availableExitPorts.removeFirst();
                Direction exitPortRotation = availableExitPort.room().rotation().rotate(availableExitPort.port().facing());
                BlockPos exitPortPosition = PortGeom.localToWorld(availableExitPort.port().localCenterBlock(), availableExitPort.room().def().sizeX(), availableExitPort.room().def().sizeY(), availableExitPort.room().def().sizeZ(), availableExitPort.room().aabb(), availableExitPort.room().rotation());
                boolean treasure = false;
                if (random.nextFloat() < config.treasureRooms()) {
                    treasure = true;
                }
                RoomDef newRoomDef;
                if (treasure) {
                    newRoomDef = Galacticraft.ROOM_REGISTRY.pick(random, RoomType.TREASURE, roomDef -> true);
                } else {
                    newRoomDef = Galacticraft.ROOM_REGISTRY.pick(random, RoomType.BASIC, RoomDef::hasMoreThanOneExit);
                }
                Direction forward = availableExitPort.room().rotation().rotate(availableExitPort.port().facing());
                boolean placed = false;
                for (int attempt = 0; attempt < MAX_TRIES_PER_ROOM && !placed; attempt++) {
                    int dist = random.nextInt(15, 35);
                    Direction left  = forward.getCounterClockWise();
                    Direction right = forward.getClockWise();
                    int mode = random.nextInt(8);
                    double vx = 0, vy = 0, vz = 0;
                    int fx = forward.getStepX(), fz = forward.getStepZ();
                    int lx = left.getStepX(),    lz = left.getStepZ();
                    int rx = right.getStepX(),   rz = right.getStepZ();
                    switch (mode) {
                        case 0 -> { vx = fx;            vy = 0; vz = fz; }               // straight
                        case 1 -> { vx = fx + lx;       vy = 0; vz = fz + lz; }           // 45° left
                        case 2 -> { vx = fx + rx;       vy = 0; vz = fz + rz; }           // 45° right
                        case 3 -> { vx = fx;            vy = 1; vz = fz; }                // slope up
                        case 4 -> { vx = fx;            vy = -1; vz = fz; }               // slope down
                        case 5 -> { vx = fx + lx;       vy = 1; vz = fz + lz; }           // 45° left + up
                        case 6 -> { vx = fx + rx;       vy = 1; vz = fz + rz; }           // 45° right + up
                        default -> { vx = fx + rx;      vy = -1; vz = fz + rz; }          // 45° right + down
                    }
                    double vlen = Math.sqrt(vx*vx + vy*vy + vz*vz);
                    if (vlen < 1e-6) { vx = fx; vy = 0; vz = fz; vlen = Math.sqrt(vx*vx + vz*vz); }
                    vx /= vlen; vy /= vlen; vz /= vlen;
                    BlockPos targetRoomPos = new BlockPos((int) (exitPortPosition.getX() + vx * dist), (int) (exitPortPosition.getY() + vy * dist), (int) (exitPortPosition.getZ() + vz * dist));
                    Direction requiredEntranceFacing = forward.getOpposite();
                    Rotation newRoomRotation = rotationNeededToMatch(Arrays.stream(newRoomDef.entrances()).findFirst().get().facing(), requiredEntranceFacing);
                    Vec3i size = new Vec3i(newRoomDef.sizeX(), newRoomDef.sizeY(), newRoomDef.sizeZ());
                    AABB roomAabb = rotatedRoomAabb(targetRoomPos, size, newRoomRotation);
                    AABB expanded = roomAabb.inflate(ROOM_MARGIN);
                    if (roomAabb.maxY > dungeonBox.maxY) continue; // dont go too high in dungeon
                    if (roomAabb.minY < (input.minBuildHeight() + 10)) continue; // Dont go too close to bedrock
                    boolean intersects = mask.contains(expanded);
                    if (intersects) continue;

                    addData(blockData, new RoomGenerator(newRoomDef, targetRoomPos, newRoomRotation).getBlocks(Galacticraft.SCANNER));

                    placedRoomBoxes.add(roomAabb);
                    mask.add(roomAabb);
                    dungeonRooms.add(new Room(roomAabb, newRoomRotation, newRoomDef));
                    placed = true;
                    // Add new room exits to available port exits
                    if (!treasure) {
                        for (PortDef exitPort : newRoomDef.exits()) {
                            availableExitPorts.add(new PortRoomMapping(exitPort, new Room(roomAabb, newRoomRotation, newRoomDef)));
                        }
                    }
                    // carve corridor with pathfinder
                    Direction entrancePortRotation = newRoomRotation.rotate(Arrays.stream(newRoomDef.entrances()).findFirst().get().facing());
                    BlockPos entrancePortPosition = PortGeom.localToWorld(Arrays.stream(newRoomDef.entrances()).findFirst().get().localCenterBlock(), newRoomDef.sizeX(), newRoomDef.sizeY(), newRoomDef.sizeZ(), roomAabb, newRoomRotation);
                    nets.add(new NegotiatedRouter.Net(
                            exitPortPosition, exitPortRotation,
                            entrancePortPosition, entrancePortRotation,
                            CORRIDOR_PREFLIGHT
                    ));
                }

                if (!placed) {
                    LOGGER.error("Could not place branch room. Used {} tries", MAX_TRIES_PER_ROOM);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            Bitmask staticMask = mask.dilated(DILATION);
            int minY = input.minBuildHeight();
            int maxY = input.maxBuildHeight() - 1;

            NegotiatedRouter.Result routed = NegotiatedRouter.routeAll(
                    nets,
                    staticMask,
                    CORRIDOR_RADIUS,
                    minY, maxY
            );

            // Keep global avoidance up-to-date
            mask.add(routed.unionMask());

            // Carve everything at once

            addData(blockData, new MaskCarvePiece(routed.unionMask()).getBlocks(random, input.minBuildHeight(), input.maxBuildHeight()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new DungeonResult(blockData);
    }

    private void addData(HashMap<SectionPos, List<BlockData>> original, HashMap<SectionPos, List<BlockData>> additional) {
        for (SectionPos sectionPos : additional.keySet()) {
            List<BlockData> data = original.getOrDefault(sectionPos, new ArrayList<>());
            data.addAll(additional.get(sectionPos));
            original.put(sectionPos, data);
        }
    }

    private void placeDungeon(DungeonResult result) {
        LOGGER.info("Dungeon algorithm run. PLACING DUNGEON!");
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

        // Run asynchronous
        DungeonInput input = new DungeonInput(
                surface,
                ctx.heightAccessor().getMinBuildHeight(),
                criticalPathRooms,
                criticalPaths,
                targetRooms,
                ctx.heightAccessor().getMaxBuildHeight()
        );
        CompletableFuture
                .supplyAsync(() -> generateDungeon(input))
                .thenAccept(this::placeDungeon)
                .exceptionally(e -> {throw new RuntimeException(e);});

        return true;
    }

    private Rotation rotationNeededToMatch(Direction originalDirection, Direction desiredDirection) {
        if (originalDirection == null || desiredDirection == null) {
            throw new IllegalArgumentException("Directions must not be null.");
        }

        if (originalDirection == Direction.UP || originalDirection == Direction.DOWN) {
            return Rotation.NONE;
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
