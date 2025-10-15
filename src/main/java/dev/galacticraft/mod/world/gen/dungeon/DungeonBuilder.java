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
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class DungeonBuilder {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final BlockPos FORBIDDEN_ORIGIN = BlockPos.ZERO;
    private static final int MAX_NET_MANHATTAN = 4096;  // tune to dungeon size
    private static final int MAX_BRANCH_DIST = 35;
    final double ROOM_MARGIN = 8;
    final int MAX_TRIES_PER_ROOM = 500;
    final int CORRIDOR_PLACEMENT_OFFSET = 1;
    final int CORRIDOR_RADIUS = 1;
    final int CORRIDOR_PREFLIGHT = 1;
    final int DILATION = 3;
    private final DungeonConfig config;
    private final RandomSource random;

    public DungeonBuilder(DungeonConfig config, RandomSource random) {
        this.config = config;
        this.random = random.fork();
    }

    // Choose an entrance that can face `requiredFacing` using only Y-rotations.
    // - horizontal required: pick a horizontal entrance and return its rotation
    // - vertical required: only valid if entrance is the same vertical dir (no rotation possible)
    private static @Nullable EntranceMatch pickEntranceFor(Direction requiredFacing, RoomDef def) {
        PortDef[] entrances = def.entrances();
        PortDef best = null;
        Rotation rot = Rotation.NONE;

        if (requiredFacing.getAxis().isHorizontal()) {
            for (PortDef e : entrances) {
                if (!e.facing().getAxis().isHorizontal()) continue;
                Rotation r = PortGeom.yRotationBetween(e.facing(), requiredFacing);
                if (r != null) {
                    best = e;
                    rot = r;
                    break;
                }
            }
        } else {
            // UP/DOWN required: must have the same vertical entrance; rotation is NONE
            for (PortDef e : entrances) {
                if (e.facing() == requiredFacing) {
                    best = e;
                    rot = Rotation.NONE;
                    break;
                }
            }
        }

        return (best == null) ? null : new EntranceMatch(best, rot);
    }

    private static Direction[] lateralBasis(Direction forward, Direction fallbackYaw) {
        // Ensure fallbackYaw is horizontal
        Direction yaw = fallbackYaw.getAxis().isHorizontal() ? fallbackYaw : Direction.NORTH;

        if (forward.getAxis().isHorizontal()) {
            return new Direction[]{forward.getCounterClockWise(), forward.getClockWise()};
        } else if (forward == Direction.UP) {
            // Looking up: standard handedness
            return new Direction[]{yaw.getCounterClockWise(), yaw.getClockWise()};
        } else { // DOWN
            // Looking down: flip handedness so "left/right" remain intuitive
            return new Direction[]{yaw.getClockWise(), yaw.getCounterClockWise()};
        }
    }

    private static boolean dungeonTouchesOrigin(AABB box) {
        return box.minX <= 0 && box.maxX >= 0
                && box.minY <= 0 && box.maxY >= 0
                && box.minZ <= 0 && box.maxZ >= 0;
    }

    private static boolean looksLikeSentinel(BlockPos p, AABB dungeonBox) {
        return !dungeonTouchesOrigin(dungeonBox) && p.equals(FORBIDDEN_ORIGIN);
    }

    private static int manhattan(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }

    private static boolean netLooksInsane(BlockPos a, BlockPos b) {
        return manhattan(a, b) > MAX_NET_MANHATTAN;
    }

    private static double centerDist2(AABB a, AABB b) {
        var ca = a.getCenter();
        var cb = b.getCenter();
        double dx = ca.x - cb.x, dy = ca.y - cb.y, dz = ca.z - cb.z;
        return dx * dx + dy * dy + dz * dz;
    }

    // Wrapper that explodes early if a transform is bogus.
    private static BlockPos safeLocalToWorld(
            BlockPos local, int sx, int sy, int sz,
            AABB roomAabb, Rotation rot, AABB dungeonBox, String ctx) {
        BlockPos w = PortGeom.localToWorld(local, sx, sy, sz, roomAabb, rot);
        if (w == null) throw new IllegalStateException(ctx + ": localToWorld returned null");
        if (looksLikeSentinel(w, dungeonBox)) {
            throw new IllegalStateException(ctx + ": localToWorld produced (0,0,0) (bad template/rotation/AABB?)");
        }
        return w;
    }

    /**
     * Compute a queen room min-corner so that its `exitPort` world position equals `targetExitWorld`.
     */
    private static BlockPos solveMinForPortTarget(RoomDef def, Rotation rot, PortDef exitPort, BlockPos targetExitWorld) {
        Vec3i size = new Vec3i(def.sizeX(), def.sizeY(), def.sizeZ());

        // Provisional AABB at min=(0,0,0) with the desired rotation.
        AABB provisional = PortGeom.rotatedRoomAabb(new BlockPos(0, 0, 0), size, rot);

        // Where would the exit port land if min=(0,0,0)?
        BlockPos p0 = PortGeom.localToWorld(
                exitPort.localCenterBlock(),
                def.sizeX(), def.sizeY(), def.sizeZ(),
                provisional, rot
        );

        // Because the transform is affine (pure Y-rotation + translation),
        // translating the min-corner by Δ translates world positions by Δ.
        // So choose minCorner = target - p0.
        return new BlockPos(
                targetExitWorld.getX() - p0.getX(),
                targetExitWorld.getY() - p0.getY(),
                targetExitWorld.getZ() - p0.getZ()
        );
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
            int entranceX = input.surface().getX() - Math.floorDiv(def.sizeX(), 2);
            int entranceZ = input.surface().getZ() - Math.floorDiv(def.sizeZ(), 2);
            entrancePosition = new BlockPos(entranceX, entranceY, entranceZ);

            addData(blockData, new RoomGenerator(def, entrancePosition, Rotation.NONE).getBlocks(Galacticraft.SCANNER));

            Vec3i size = new Vec3i(def.sizeX(), def.sizeY(), def.sizeZ());
            AABB aabb = PortGeom.rotatedRoomAabb(entrancePosition, size, Rotation.NONE);
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
            AABB aabb = PortGeom.rotatedRoomAabb(endPosition, size, endRotation);
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
                // Pick a queen room template
                RoomDef def = Galacticraft.ROOM_REGISTRY.pick(random, RoomType.QUEEN, roomDef -> true);

                // End-room entrance port world pose
                Direction entranceFacingWorld = endRotation.rotate(entrance.facing());
                BlockPos entranceWorldPos = PortGeom.localToWorld(
                        entrance.localCenterBlock(),
                        endRoom.def().sizeX(), endRoom.def().sizeY(), endRoom.def().sizeZ(),
                        endRoom.aabb(), endRotation
                );

                // The queen EXIT port must be 20 blocks out along the entrance direction
                BlockPos targetExitWorld = entranceWorldPos.offset(entranceFacingWorld.getNormal().multiply(random.nextInt(20, 40)));
                targetExitWorld = targetExitWorld.offset(random.nextInt(-10, 10), random.nextInt(-10, 10), random.nextInt(-10, 10));

                // Choose which queen-room EXIT port you want to mate (first exit here, adjust as needed)
                PortDef queenExit = Arrays.stream(def.exits()).findFirst().orElse(null);
                if (queenExit == null) {
                    LOGGER.warn("Queen template {} has no exits; skipping.", def.template());
                    continue;
                }

                // Rotate queen so its EXIT faces back toward the entrance (ports face each other)
                Direction queenExitFacingLocal = queenExit.facing();
                Rotation rotation = PortGeom.rotationNeededToMatch(queenExitFacingLocal, entranceFacingWorld.getOpposite());

                // Solve min-corner so that queenExit maps to targetExitWorld
                BlockPos queenMin = solveMinForPortTarget(def, rotation, queenExit, targetExitWorld);

                // Build final AABB and place
                Vec3i size = new Vec3i(def.sizeX(), def.sizeY(), def.sizeZ());
                AABB aabb = PortGeom.rotatedRoomAabb(queenMin, size, rotation);

                // (Optional) quick sanity: verify the exit port really landed at targetExitWorld
                BlockPos checkWorld = PortGeom.localToWorld(
                        queenExit.localCenterBlock(), def.sizeX(), def.sizeY(), def.sizeZ(), aabb, rotation
                );
                if (!checkWorld.equals(targetExitWorld)) {
                    LOGGER.warn("Queen exit mismatch ({} vs {}). Minor rounding may occur; proceeding.",
                            checkWorld, targetExitWorld);
                }

                // Emit blocks & register room
                addData(blockData, new RoomGenerator(def, queenMin, rotation).getBlocks(Galacticraft.SCANNER));
                placedRoomBoxes.add(aabb);
                mask.add(aabb);
                queenRooms.add(new Room(aabb, rotation, def));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Build bounding box of dungeon
        AABB dungeonBox = new AABB(entrancePosition.getCenter(), endPosition.getCenter());
        //TODO make this take the max room basic room size, and the amount of rooms it wants to place to determine how big the box needs to be
        //Or just make it a soft box that inflates if it fails to place too many times
        dungeonBox = dungeonBox.inflate(random.nextInt(50, 80), 1, random.nextInt(50, 80)); //might need to increase this for more rooms or bigger rooms

        // Place critical path rooms
        try {
            for (int i = 0; i < input.criticalPathRooms(); i++) {
                boolean placed = false;

                int tries = 0;
                while (!placed) {
                    tries++;
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
                    AABB roomAabb = PortGeom.rotatedRoomAabb(roomPos, size, rot);

                    // 4) Margin check vs any already-placed room
                    AABB expanded = roomAabb.inflate(ROOM_MARGIN);
                    boolean intersects = mask.contains(expanded);
                    if (intersects) continue;
                    // candidate keeps margin from prior

                    addData(blockData, new RoomGenerator(def, roomPos, rot).getBlocks(Galacticraft.SCANNER));

                    placedRoomBoxes.add(roomAabb);
                    mask.add(roomAabb);
                    dungeonRooms.add(new Room(roomAabb, rot, def));
                    placed = true;
                }
                LOGGER.info("Tried {} times to place critical path room", tries);
            }
        } catch (Exception e) {
            LOGGER.info("Failed with {}", e.getMessage());
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
                    List<Room> orderedRooms = new ArrayList<>();
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
        {
            for (Room queenRoom : queenRooms) {
                PortDef exitPort = Arrays.stream(queenRoom.def().exits()).findFirst().orElse(null);
                if (exitPort == null) {
                    LOGGER.warn("Queen room has no exit port, skipping.");
                    continue;
                }

                Direction exitPortRotation = queenRoom.rotation().rotate(exitPort.facing());
                Optional<PortDef> entrancePort = Arrays.stream(endRoom.def().entrances())
                        .filter(p -> endRoom.rotation().rotate(p.facing()) == exitPortRotation.getOpposite())
                        .findFirst();
                if (entrancePort.isEmpty()) {
                    LOGGER.warn("Could not find matching entrance on end room for queen->end, skipping.");
                    continue;
                }

                BlockPos exitPortPosition;
                BlockPos entrancePortPosition;
                try {
                    exitPortPosition = safeLocalToWorld(
                            exitPort.localCenterBlock(),
                            queenRoom.def().sizeX(), queenRoom.def().sizeY(), queenRoom.def().sizeZ(),
                            queenRoom.aabb(), queenRoom.rotation(), dungeonBox, "queen->end exit"
                    );
                    entrancePortPosition = safeLocalToWorld(
                            entrancePort.get().localCenterBlock(),
                            endRoom.def().sizeX(), endRoom.def().sizeY(), endRoom.def().sizeZ(),
                            endRoom.aabb(), endRoom.rotation(), dungeonBox, "queen->end entrance"
                    );
                } catch (Exception ex) {
                    LOGGER.warn("Skipping queen->end net due to transform error: {}", ex.getMessage());
                    continue;
                }

                // Nudge outward
                exitPortPosition = exitPortPosition.offset(exitPortRotation.getNormal().multiply(CORRIDOR_PLACEMENT_OFFSET));
                Direction entrancePortRotation = endRoom.rotation().rotate(entrancePort.get().facing());
                entrancePortPosition = entrancePortPosition.offset(entrancePortRotation.getNormal().multiply(CORRIDOR_PLACEMENT_OFFSET));

                if (netLooksInsane(exitPortPosition, entrancePortPosition)) {
                    LOGGER.warn("Skipping queen->end net: insane distance ({}).", manhattan(exitPortPosition, entrancePortPosition));
                    continue;
                }

                nets.add(new NegotiatedRouter.Net(
                        exitPortPosition, exitPortRotation,
                        entrancePortPosition, entrancePortRotation,
                        CORRIDOR_PREFLIGHT
                ));
            }
        }

        // Pathfind and carve all critical paths
        {
            for (int i = 0; i < orderedCriticalPaths.size(); i++) {
                for (int j = 0; j < orderedCriticalPaths.get(i).size() - 1; j++) {
                    RoomHamiltonianPath.PathResult pathResult = criticalPathResults.get(i);
                    Room room1 = orderedCriticalPaths.get(i).get(j);
                    Room room2 = orderedCriticalPaths.get(i).get(j + 1);

                    PortDef exitPort = Arrays.stream(room1.def().exits()).toList()
                            .get(pathResult.portPairs.get(j)[0]);
                    PortDef entrancePort = Arrays.stream(room2.def().entrances()).toList()
                            .get(pathResult.portPairs.get(j)[1]);

                    BlockPos exitPortPosition, entrancePortPosition;
                    try {
                        exitPortPosition = safeLocalToWorld(
                                exitPort.localCenterBlock(),
                                room1.def().sizeX(), room1.def().sizeY(), room1.def().sizeZ(),
                                room1.aabb(), room1.rotation(), dungeonBox, "crit exit"
                        );
                        entrancePortPosition = safeLocalToWorld(
                                entrancePort.localCenterBlock(),
                                room2.def().sizeX(), room2.def().sizeY(), room2.def().sizeZ(),
                                room2.aabb(), room2.rotation(), dungeonBox, "crit entrance"
                        );
                    } catch (Exception ex) {
                        LOGGER.warn("Skipping critical-path net due to transform error: {}", ex.getMessage());
                        continue;
                    }

                    Direction exitPortRotation = room1.rotation().rotate(exitPort.facing());
                    Direction entrancePortRotation = room2.rotation().rotate(entrancePort.facing());
                    exitPortPosition = exitPortPosition.offset(exitPortRotation.getNormal().multiply(CORRIDOR_PLACEMENT_OFFSET));
                    entrancePortPosition = entrancePortPosition.offset(entrancePortRotation.getNormal().multiply(CORRIDOR_PLACEMENT_OFFSET));

                    if (netLooksInsane(exitPortPosition, entrancePortPosition)) {
                        LOGGER.warn("Skipping critical-path net: insane distance ({}).",
                                manhattan(exitPortPosition, entrancePortPosition));
                        continue;
                    }

                    nets.add(new NegotiatedRouter.Net(
                            exitPortPosition, exitPortRotation,
                            entrancePortPosition, entrancePortRotation,
                            CORRIDOR_PREFLIGHT
                    ));
                }
            }
        }

        // Find all unused exits and branch off until we reach room limit
        record PortRoomMapping(PortDef port, Room room) {
        }
        List<PortRoomMapping> availableExitPorts = new ArrayList<>();

        try {
            // Collect unused exits from the critical paths
            for (int i = 0; i < orderedCriticalPaths.size(); i++) {
                List<Room> rooms = orderedCriticalPaths.get(i);
                for (int j = 0; j < rooms.size() - 1; j++) {
                    if (rooms.get(j).def().hasMoreThanOneExit()) {
                        RoomHamiltonianPath.PathResult pathResult = criticalPathResults.get(i);
                        List<PortDef> exits = new ArrayList<>(Arrays.stream(rooms.get(j).def().exits()).toList());
                        exits.remove(pathResult.portPairs.get(j)[0]); // remove the one used by crit path
                        for (PortDef exit : exits) availableExitPorts.add(new PortRoomMapping(exit, rooms.get(j)));
                    }
                }
            }

            // Start branching randomly
            Direction lastYaw = Direction.NORTH;
            while (!availableExitPorts.isEmpty() && placedRoomBoxes.size() < input.targetRooms()) {
                PortRoomMapping availableExitPort = availableExitPorts.removeFirst();

                Room parentRoom = availableExitPort.room();
                Direction exitPortRotation = parentRoom.rotation().rotate(availableExitPort.port().facing());

                // World pos of the parent exit port
                BlockPos exitPortPosition;
                try {
                    exitPortPosition = safeLocalToWorld(
                            availableExitPort.port().localCenterBlock(),
                            parentRoom.def().sizeX(), parentRoom.def().sizeY(), parentRoom.def().sizeZ(),
                            parentRoom.aabb(), parentRoom.rotation(), dungeonBox, "branch exit"
                    );
                } catch (Exception ex) {
                    LOGGER.warn("Skipping branch (bad exit transform): {}", ex.getMessage());
                    continue;
                }

                boolean treasure = random.nextFloat() < config.treasureRooms();
                RoomDef newRoomDef = treasure
                        ? Galacticraft.ROOM_REGISTRY.pick(random, RoomType.TREASURE, r -> true)
                        : Galacticraft.ROOM_REGISTRY.pick(random, RoomType.BASIC, RoomDef::hasMoreThanOneExit);

                // We'll try a bunch of candidate directions/distances
                boolean placed = false;
                for (int attempt = 0; attempt < MAX_TRIES_PER_ROOM && !placed; attempt++) {
                    // distance in [35..50]
                    int dist = 35 + random.nextInt(16);

                    // Base direction: the exit facing
                    Vec3 base = new Vec3(exitPortRotation.getStepX(), exitPortRotation.getStepY(), exitPortRotation.getStepZ());

                    // If vertical exit, mix in the lastYaw so we get horizontal travel
                    if (!exitPortRotation.getAxis().isHorizontal()) {
                        base = base.add(lastYaw.getStepX(), 0, lastYaw.getStepZ());
                    }

                    // Add a bit of noise so we don't only shoot straight lines
                    Vec3 jitter = new Vec3(
                            random.nextGaussian() * 0.35,
                            random.nextGaussian() * 0.20,
                            random.nextGaussian() * 0.35
                    );

                    // Final unit direction (never NaN)
                    Vec3 dir = PortGeom.safeUnit(base.add(jitter),
                            new Vec3(lastYaw.getStepX(), 0, lastYaw.getStepZ()));
                    if (dir.lengthSqr() < 1e-12) dir = new Vec3(1, 0, 0); // double safety

                    // Compute integer target MIN corner
                    BlockPos targetRoomPos = new BlockPos(
                            (int)Math.floor(exitPortPosition.getX() + dir.x * dist),
                            (int)Math.floor(exitPortPosition.getY() + dir.y * dist),
                            (int)Math.floor(exitPortPosition.getZ() + dir.z * dist)
                    );
                    if (looksLikeSentinel(targetRoomPos, dungeonBox)) {
                        LOGGER.warn("Branch candidate hit origin—skipping.");
                        continue;
                    }

                    // Approach facing for the corridor: project to horizontal if needed
                    Direction approachFacing = PortGeom.facingFromXZ(dir, lastYaw);
                    if (approachFacing.getAxis().isHorizontal()) lastYaw = approachFacing;

                    Direction requiredEntranceFacing = approachFacing.getOpposite();
                    EntranceMatch match = pickEntranceFor(requiredEntranceFacing, newRoomDef);
                    if (match == null) {
                        // Try a different approach; many rooms only have horizontal entrances
                        continue;
                    }

                    PortDef chosenEntrance = match.entrance();
                    Rotation newRoomRotation = match.rotation();

                    // Place the room and check collisions
                    Vec3i size = new Vec3i(newRoomDef.sizeX(), newRoomDef.sizeY(), newRoomDef.sizeZ());
                    AABB roomAabb = PortGeom.rotatedRoomAabb(targetRoomPos, size, newRoomRotation);
                    AABB expanded = roomAabb.inflate(ROOM_MARGIN);

                    // Allow outside the dungeon box; only clamp against world floor
                    if (roomAabb.minY < (input.minBuildHeight() + 10)) continue;
                    if (mask.contains(expanded)) continue;

                    // We already chose dist in [35..50] from the parent port, so no extra range check needed.

                    // Accept placement
                    addData(blockData, new RoomGenerator(newRoomDef, targetRoomPos, newRoomRotation).getBlocks(Galacticraft.SCANNER));
                    placedRoomBoxes.add(roomAabb);
                    mask.add(roomAabb);
                    Room newRoom = new Room(roomAabb, newRoomRotation, newRoomDef);
                    dungeonRooms.add(newRoom);
                    placed = true;

                    // Add new room exits to future candidates (unless treasure)
                    if (!treasure) {
                        for (PortDef exit : newRoomDef.exits()) {
                            availableExitPorts.add(new PortRoomMapping(exit, newRoom));
                        }
                    }

                    // Corridor endpoints (nudge outward)
                    BlockPos entrancePortPosition;
                    Direction entrancePortRotation = newRoomRotation.rotate(chosenEntrance.facing());
                    try {
                        entrancePortPosition = safeLocalToWorld(
                                chosenEntrance.localCenterBlock(),
                                newRoomDef.sizeX(), newRoomDef.sizeY(), newRoomDef.sizeZ(),
                                roomAabb, newRoomRotation, dungeonBox, "branch entrance"
                        );
                    } catch (Exception ex) {
                        LOGGER.warn("Placed branch room, but entrance transform failed; skipping corridor: {}", ex.getMessage());
                        break;
                    }

                    BlockPos exitN = exitPortPosition.offset(exitPortRotation.getNormal().multiply(CORRIDOR_PLACEMENT_OFFSET));
                    BlockPos entrN = entrancePortPosition.offset(entrancePortRotation.getNormal().multiply(CORRIDOR_PLACEMENT_OFFSET));

                    if (!netLooksInsane(exitN, entrN)) {
                        nets.add(new NegotiatedRouter.Net(
                                exitN, exitPortRotation,
                                entrN, entrancePortRotation,
                                CORRIDOR_PREFLIGHT
                        ));
                    } else {
                        LOGGER.warn("Skipping branch net (insane distance {}), but keeping room.",
                                manhattan(exitN, entrN));
                    }
                }

                if (!placed) {
                    LOGGER.error("Could not place branch room. Used {} tries", MAX_TRIES_PER_ROOM);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("FINISHED GENERATING BRANCHES");

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

            // Build a precise room protection mask (fast: Bitmask supports AABB)
            Bitmask roomsMask = new Bitmask();
            for (AABB roomBox : placedRoomBoxes) {
                roomsMask.add(roomBox); // protects all room interiors from carver writes
            }

            // Build a whitelist of doorway cells (aperture + 1..N cells outward)
            Set<Long> doorwayWhitelist = new HashSet<>();

            // Helper to add all doorway cells for a room/port
            BiConsumer<Room, PortDef> addDoor = (room, port) -> {
                Direction facingW = room.rotation().rotate(port.facing());
                // iterate the local rectangle cmin..cmax on the face and map to world
                BlockPos cmin = port.min();
                BlockPos cmax = port.max();
                for (int x = cmin.getX(); x <= cmax.getX(); x++) {
                    for (int y = cmin.getY(); y <= cmax.getY(); y++) {
                        for (int z = cmin.getZ(); z <= cmax.getZ(); z++) {
                            BlockPos local = new BlockPos(x, y, z);
                            BlockPos world = PortGeom.localToWorld(local,
                                    room.def().sizeX(), room.def().sizeY(), room.def().sizeZ(),
                                    room.aabb(), room.rotation());
                            // aperture itself
                            doorwayWhitelist.add(world.asLong());
                            // also whitelist first few cells outside (match your CORRIDOR_PLACEMENT_OFFSET)
                            for (int s = 1; s <= CORRIDOR_PLACEMENT_OFFSET; s++) {
                                BlockPos out = world.offset(facingW.getNormal().multiply(s));
                                doorwayWhitelist.add(out.asLong());
                            }
                        }
                    }
                }
            };

            // Add doorways for all rooms you’ve placed
            addDoor.accept(entranceRoom, Arrays.stream(entranceRoom.def().exits()).findFirst().orElse(null));
            for (PortDef p : endRoom.def().entrances()) addDoor.accept(endRoom, p);
            for (Room r : queenRooms) {
                for (PortDef p : r.def().entrances()) addDoor.accept(r, p);
                for (PortDef p : r.def().exits()) addDoor.accept(r, p);
            }
            for (Room r : dungeonRooms) {
                for (PortDef p : r.def().entrances()) addDoor.accept(r, p);
                for (PortDef p : r.def().exits()) addDoor.accept(r, p);
            }

            addData(blockData,
                    new MaskCarvePiece(routed.unionMask())
                            .getBlocks(random, input.minBuildHeight(), input.maxBuildHeight(), roomsMask, doorwayWhitelist)
            );
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

    private void placeDungeon(ResourceKey<Level> dimension, DungeonResult result) {
        LOGGER.info("Dungeon building complete. Enqueuing dungeon placement");
        DungeonPlacementManager.enqueue(dimension, result);
    }

    public boolean build(WorldGenLevel gen, BlockPos surface) {
        LOGGER.info("Building Dungeon");
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
                gen.getMinBuildHeight(),
                criticalPathRooms,
                criticalPaths,
                targetRooms,
                gen.getMaxBuildHeight()
        );
        CompletableFuture
                .supplyAsync(() -> generateDungeon(input))
                .thenAccept(result -> placeDungeon(gen.getLevel().dimension(), result))
                .exceptionally(e -> {
                    throw new RuntimeException(e);
                });

        return true;
    }

    public record Room(AABB aabb, Rotation rotation, RoomDef def) {
    }

    private record EntranceMatch(PortDef entrance, Rotation rotation) {
    }
}
