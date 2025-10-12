package dev.galacticraft.mod.world.gen.dungeon;

import com.mojang.logging.LogUtils;
import dev.galacticraft.mod.ServerHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

import java.util.*;

/**
 * Places exactly 3 Queen rooms relative to the End room's entrances.
 * - For each unique horizontal entrance direction on the End:
 *   - rotate a Queen so its **exit** faces the opposite plane,
 *   - position the Queen so its exit port center is 15..30 blocks out along that direction,
 *     with tiny bias in the orthogonal axes,
 *   - enforce mask bounds, room clearance, and at-least-one-portal viability.
 *
 * Extremely verbose logs to diagnose any failure.
 */
final class QueenPlanner {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final ProcConfig pc;
    private final Random jr;

    QueenPlanner(ProcConfig pc, Random jr) {
        this.pc = Objects.requireNonNull(pc, "pc");
        this.jr = Objects.requireNonNull(jr, "jr");
    }

    /** Public entry called by DungeonWorldBuilder. Returns the Queens actually placed (0..3). */
    public List<RoomPlacer.Placed> placeQueens(RoomPlacer roomPlacer,
                                               List<RoomPlacer.Placed> rooms,
                                               VoxelMask3D mRoom,
                                               RoomPlacer.Placed endPlaced,
                                               List<RoomTemplateDef> queenDefs,
                                               int worldMinY,
                                               int worldMaxY) {
        ArrayList<RoomPlacer.Placed> out = new ArrayList<>();
        if (endPlaced == null) {
            LOGGER.error("[QueenPlanner] endPlaced is null");
            return out;
        }

        // 1) Collect End entrances in world space + facing
        ArrayList<FlowRouter.PNode> endEntrs = new ArrayList<>();
        for (Port port : endPlaced.scan().entrances()) {
            BlockPos wp = Transforms.worldOfLocalMin(port.localPos(), endPlaced.origin(),
                    endPlaced.scan().size(), endPlaced.rot());
            Direction f = Transforms.rotateFacingYaw(port.facing(), endPlaced.rot());
            endEntrs.add(new FlowRouter.PNode(endPlaced, port, endPlaced.rot(), wp, f));
        }
        LOGGER.info("[QueenPlanner] End entrances found: {}", endEntrs.size());

        // 2) Choose up to 3 unique horizontal planes from End entrances.
        //    If fewer are present, synthesize missing directions to satisfy your spec.
        LinkedHashSet<Direction> wanted = new LinkedHashSet<>();
        for (var pn : endEntrs) if (pn.facing().getAxis().isHorizontal()) wanted.add(pn.facing());
        // ensure different planes (avoid duplicates on same axis same sign)
        // (we still accept opposite directions if End has them)
        Direction[] fallback = new Direction[]{Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.NORTH};
        int i = 0;
        while (wanted.size() < 3 && i < fallback.length) {
            wanted.add(fallback[i++]);
        }
        ArrayList<Direction> planDirs = new ArrayList<>(wanted);
        if (planDirs.size() > 3) planDirs = new ArrayList<>(planDirs.subList(0, 3));
        LOGGER.info("[QueenPlanner] Planned queen axes (up to 3): {}", planDirs);

        // 3) Place one Queen for each planned direction
        int qIdx = 0;
        for (Direction endEntranceDir : planDirs) {
            Direction desiredQueenExit = endEntranceDir.getOpposite(); // spec: opposite plane
            int distance = 15 + jr.nextInt(16); // 15..30

            // pick target point ~d away from End center along entrance direction,
            // small jitter on other axes.
            BlockPos endCenter = centerOf(endPlaced.aabb());
            int jx = (endEntranceDir.getAxis() == Direction.Axis.Z) ? jr.nextInt(-2, 3) : 0;
            int jz = (endEntranceDir.getAxis() == Direction.Axis.X) ? jr.nextInt(-2, 3) : 0;
            int jy = jr.nextInt(-1, 2);

            BlockPos targetExitCenter = endCenter.relative(endEntranceDir, distance).offset(jx, jy, jz);

            LOGGER.info("[QueenPlanner] Target #{}: endCenter={} endEntranceDir={} -> desiredQueenExit={} dist={} jitter=({},{},{}) targetExitCenter={}",
                    qIdx, endCenter, endEntranceDir, desiredQueenExit, distance, jx, jy, jz, targetExitCenter);

            RoomPlacer.Placed placed = tryPlaceOneQueenFacing(roomPlacer, queenDefs, desiredQueenExit,
                    targetExitCenter, rooms, mRoom, worldMinY, worldMaxY);

            if (placed != null) {
                out.add(placed);
                rooms.add(placed);
                writeRoomToMask(mRoom, placed);
                LOGGER.info("[QueenPlanner] Placed Queen #{}: id={} origin={} rot={} aabb={}",
                        qIdx, placed.def().id(), placed.origin(), placed.rot(), placed.aabb());
            } else {
                LOGGER.error("[QueenPlanner] FAILED to place Queen #{} for endEntranceDir={}", qIdx, endEntranceDir);
            }
            qIdx++;
            if (out.size() >= 3) break;
        }

        LOGGER.info("[QueenPlanner] DONE: queensPlaced={}", out.size());
        return out;
    }

    // ---- core attempt for a single queen ----
    private RoomPlacer.Placed tryPlaceOneQueenFacing(RoomPlacer roomPlacer,
                                                     List<RoomTemplateDef> queenDefs,
                                                     Direction desiredExitFacing,
                                                     BlockPos worldExitCenterTarget,
                                                     List<RoomPlacer.Placed> already,
                                                     VoxelMask3D mRoom,
                                                     int worldMinY,
                                                     int worldMaxY) {

        // Shuffle defs to vary choices
        ArrayList<RoomTemplateDef> defs = new ArrayList<>(queenDefs);
        Collections.shuffle(defs, jr);

        int maxDefTries = Math.max(8, pc.perRoomTries / 3);
        int placedFailsBounds = 0, placedFailsClear = 0, placedFailsPortal = 0, placedFailsNoExitMatch = 0;

        for (RoomTemplateDef qdef : defs) {
            var scan = TemplatePortScanner.scan(ServerHolder.get().getStructureManager(), qdef.id());
            if (scan == null) {
                LOGGER.warn("[QueenPlanner] scan null for {}", qdef.id());
                continue;
            }

            // choose an EXIT port in the template we will align to desiredExitFacing
            List<Port> exits = scan.exits();
            if (exits.isEmpty()) {
                LOGGER.warn("[QueenPlanner] queen template {} has no EXITs; skipping", qdef.id());
                continue;
            }

            int tries = 0;
            boolean placedOk = false;

            // For each exit port, test 4 rotations to match facing
            outer:
            for (Port localExit : exits) {
                for (Rotation rot : Rotation.values()) {
                    tries++;
                    Direction exitFacingWorld = Transforms.rotateFacingYaw(localExit.facing(), rot);
                    if (exitFacingWorld != desiredExitFacing) {
                        continue; // need exit to face opposite plane
                    }

                    // compute origin so that rotated exit center == worldExitCenterTarget
                    Vec3i size = scan.size();
                    // worldOfLocalMin(local, origin, size, rot) == target  => origin = target - rotated(local)
                    BlockPos rotatedLocal = Transforms.worldOfLocalMin(localExit.localPos(), BlockPos.ZERO, size, rot);
                    BlockPos origin = worldExitCenterTarget.subtract(rotatedLocal);

                    // clamp Y inside world bounds with a bit of safety
                    int oy = Math.max(worldMinY + 4, Math.min(origin.getY(), worldMaxY - size.getY() - 2));
                    origin = new BlockPos(origin.getX(), oy, origin.getZ());

                    // build AABB
                    Vec3i rsize = Transforms.rotatedSize(size, rot);
                    AABB box = new AABB(origin.getX(), origin.getY(), origin.getZ(),
                            origin.getX() + rsize.getX(), origin.getY() + rsize.getY(), origin.getZ() + rsize.getZ());
                    var cand = new RoomPlacer.Placed(scan, qdef, rot, origin, box);

                    // mask bounds
                    if (!maskBoundsOk(mRoom, cand)) { placedFailsBounds++; continue; }

                    // clearance vs existing rooms
                    AABB infl = box.inflate(pc.cRoom);
                    boolean clash = false;
                    for (var p : already) if (infl.intersects(p.aabb().inflate(pc.cRoom))) { clash = true; break; }
                    if (clash) { placedFailsClear++; continue; }

                    // portals viability
                    if (!portalsViableLikeRoomPlacer(cand, mRoom, pc)) { placedFailsPortal++; continue; }

                    // success
                    return cand;
                }
            }

            if (!placedOk && tries == 0) placedFailsNoExitMatch++;
            if (tries >= maxDefTries) {
                LOGGER.debug("[QueenPlanner] too many tries for {}, skipping", qdef.id());
            }
        }

        LOGGER.error("[QueenPlanner] tryPlaceOneQueenFacing FAILED: desiredExitFacing={}, target={}, failCounts[bounds={},clear={},portal={},noExitMatch={}]",
                desiredExitFacing, worldExitCenterTarget, placedFailsBounds, placedFailsClear, placedFailsPortal, placedFailsNoExitMatch);
        return null;
    }

    // ---- utilities (duplicated here for logging isolation) ----

    static boolean portalsViableLikeRoomPlacer(RoomPlacer.Placed p, VoxelMask3D mRoom, ProcConfig pc) {
        // mimic RoomPlacer.portalsViable using a derived free mask
        VoxelMask3D mTmp = mRoom.copy();
        writeRoomToMask(mTmp, p);
        VoxelMask3D mFreePrime = Morph3D.freeMaskFromRooms(mTmp, pc.effectiveRadius());
        int minGood = 1;
        int stepOut = pc.effectiveRadius() + 1;

        List<Port> ports = p.scan().exits().isEmpty() ? p.scan().entrances() : p.scan().exits();
        if (ports.isEmpty()) ports = p.scan().entrances();

        int good = 0;
        for (Port port : ports) {
            BlockPos wp = Transforms.worldOfLocalMin(port.localPos(), p.origin(), p.scan().size(), p.rot());
            Direction face = Transforms.rotateFacingYaw(port.facing(), p.rot());
            BlockPos outward = wp.relative(face, Math.max(1, stepOut));
            int gx = mFreePrime.gx(outward.getX());
            int gy = mFreePrime.gy(outward.getY());
            int gz = mFreePrime.gz(outward.getZ());
            boolean ok = mFreePrime.in(gx, gy, gz) && mFreePrime.get(gx, gy, gz);
            if (ok && ++good >= minGood) return true;
        }
        return false;
    }

    private static boolean maskBoundsOk(VoxelMask3D mask, RoomPlacer.Placed p) {
        Vec3i sz = Transforms.rotatedSize(p.scan().size(), p.rot());
        int x0 = mask.gx(p.origin().getX());
        int y0 = mask.gy(p.origin().getY());
        int z0 = mask.gz(p.origin().getZ());
        int x1 = x0 + sz.getX() - 1;
        int y1 = y0 + sz.getY() - 1;
        int z1 = z0 + sz.getZ() - 1;
        return mask.in(x0,y0,z0) && mask.in(x1,y1,z1);
    }

    private static void writeRoomToMask(VoxelMask3D mask, RoomPlacer.Placed p) {
        Vec3i sz = Transforms.rotatedSize(p.scan().size(), p.rot());
        int x0 = mask.gx(p.origin().getX());
        int y0 = mask.gy(p.origin().getY());
        int z0 = mask.gz(p.origin().getZ());
        mask.fillAABB(x0, y0, z0, x0 + sz.getX() - 1, y0 + sz.getY() - 1, z0 + sz.getZ() - 1, true);
    }

    private static BlockPos centerOf(AABB a) {
        return new BlockPos((int)Math.floor((a.minX + a.maxX) * 0.5),
                (int)Math.floor((a.minY + a.maxY) * 0.5),
                (int)Math.floor((a.minZ + a.maxZ) * 0.5));
    }
}