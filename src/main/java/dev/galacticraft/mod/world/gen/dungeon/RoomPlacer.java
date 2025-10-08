package dev.galacticraft.mod.world.gen.dungeon;

import com.mojang.logging.LogUtils;
import dev.galacticraft.mod.ServerHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

import java.util.*;

final class RoomPlacer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ProcConfig cfg;
    private final Random rnd;
    RoomPlacer(ProcConfig cfg, Random rnd) {
        this.cfg = cfg;
        this.rnd = rnd;
    }

    // Simple, safe no-op so DungeonWorldBuilder can call it without compile errors.
    // Return false if you don't support swapping after placement yet.
    static boolean trySwapRoomTemplate(Placed room, List<RoomTemplateDef> choices, Random rnd) {
        LOGGER.info("(RoomPlacer) trySwapRoomTemplate: no-op (room={}, choices={})",
                room.def.id(), (choices == null ? 0 : choices.size()));
        return false;
    }

    private static RoomTemplateDef pick(List<RoomTemplateDef> pool, Random rnd) {
        int sum = 0;
        for (var d : pool) sum += d.weight();
        int k = rnd.nextInt(sum), acc = 0;
        for (var d : pool) {
            acc += d.weight();
            if (k < acc) return d;
        }
        return pool.get(pool.size() - 1);
    }

    private static Vec3i rotSize(Vec3i s, Rotation r) {
        return switch (r) {
            case NONE, CLOCKWISE_180 -> s;
            default -> new Vec3i(s.getZ(), s.getY(), s.getX());
        };
    }

    // same helpers as elsewhere
    private static BlockPos localToWorldMinCorner(BlockPos local, BlockPos origin, Vec3i size, Rotation rot) {
        int ox = origin.getX(), oy = origin.getY(), oz = origin.getZ();
        int lx = local.getX(), ly = local.getY(), lz = local.getZ();
        int sx = size.getX(), sz = size.getZ();

        // Yaw-only rotation around MIN corner (origin)
        return switch (rot) {
            case NONE -> new BlockPos(ox + lx, oy + ly, oz + lz);
            case CLOCKWISE_90 ->      // (x,z) = (sz-1 - lz, lx)
                    new BlockPos(ox + (sz - 1 - lz), oy + ly, oz + lx);
            case CLOCKWISE_180 ->     // (x,z) = (sx-1 - lx, sz-1 - lz)
                    new BlockPos(ox + (sx - 1 - lx), oy + ly, oz + (sz - 1 - lz));
            case COUNTERCLOCKWISE_90 -> // (x,z) = (lz, sx-1 - lx)
                    new BlockPos(ox + lz, oy + ly, oz + (sx - 1 - lx));
        };
    }

    public static List<FlowRouter.PNode> ensureQueensNearEnds(
            List<Placed> placed,
            List<FlowRouter.PNode> allPorts,
            List<FlowRouter.PNode> endPorts,
            int need,
            Random rnd
    ) {
        ArrayList<FlowRouter.PNode> picked = new ArrayList<>();
        if (need <= 0 || endPorts == null || endPorts.isEmpty() || allPorts == null || allPorts.isEmpty()) {
            return picked;
        }

        // Gather all queen ports from the known portal set
        java.util.Set<ResourceLocation> queenIds =
                RoomDefs.QUEEN.stream().map(RoomTemplateDef::id).collect(java.util.stream.Collectors.toSet());

        ArrayList<FlowRouter.PNode> queenPorts = new ArrayList<>();
        for (FlowRouter.PNode pn : allPorts) {
            if (queenIds.contains(pn.room().def.id())) {
                queenPorts.add(pn);
            }
        }
        if (queenPorts.isEmpty()) return picked;

        // Prefer unique rooms and the queens nearest to each end
        HashSet<RoomPlacer.Placed> usedQueenRooms = new HashSet<>();
        ArrayList<FlowRouter.PNode> ends = new ArrayList<>(endPorts);
        ends.sort(Comparator.comparingInt(pn -> pn.worldPos().getY())); // prefer lower ends first

        for (FlowRouter.PNode end : ends) {
            if (picked.size() >= need) break;

            FlowRouter.PNode best = null;
            double bestD = Double.MAX_VALUE;
            for (FlowRouter.PNode q : queenPorts) {
                if (usedQueenRooms.contains(q.room())) continue; // keep queens unique per path
                double d = q.worldPos().distSqr(end.worldPos());
                if (d < bestD) {
                    bestD = d;
                    best = q;
                }
            }
            if (best != null) {
                picked.add(best);
                usedQueenRooms.add(best.room());
            }
        }

        // If we still need more, fill with any remaining queens (random order, unique rooms)
        if (picked.size() < need) {
            Collections.shuffle(queenPorts, rnd);
            for (FlowRouter.PNode q : queenPorts) {
                if (picked.size() >= need) break;
                if (usedQueenRooms.add(q.room())) picked.add(q);
            }
        }
        return picked;
    }

    /**
     * Try to place rooms (entrance, end, basics, treasure) with baked corridor clearance.
     */
    List<Placed> placeAll(List<RoomTemplateDef> pool, ScannedTemplate entrance, ScannedTemplate end,
                          BlockPos surface, BlockPos targetEndCenter,
                          int worldMinY, int worldMaxY, VoxelMask3D mRoomOut) {

        ArrayList<Placed> out = new ArrayList<>();

        // 1) Entrance: anchor directly under surface (centered)
        BlockPos entOrigin = surface.offset(-entrance.size().getX() / 2, -30, -entrance.size().getZ() / 2);
        Placed ent = placeFixed(entrance, RoomDefs.ENTRANCE, Rotation.NONE, entOrigin);
        out.add(ent);
        writeRoomToMask(mRoomOut, ent);
        LOGGER.info("(RoomPlacer) [Proc/Place] ENTRANCE placed at {} rot={}", ent.origin, ent.rot);

        // 2) End: try rotations + small position jitter around target center
        Placed endPlaced = tryPlaceEnd(end, targetEndCenter, worldMinY, worldMaxY, out, mRoomOut);
        if (endPlaced == null) {
            LOGGER.info("(RoomPlacer) [Proc/Place] END placement failed (all tries)");
            return out; // let caller decide relaxation; we’ll return current list (likely just entrance)
        }
        out.add(endPlaced);
        writeRoomToMask(mRoomOut, endPlaced);

        // 3) Others — sample anchors in a shell; bias lower for treasures
        int tries = Math.max(200, cfg.anchorAttempts);
        for (int t = 0; t < tries && out.size() < cfg.maxRooms; t++) {
            RoomTemplateDef d = pick(pool, rnd);
            ScannedTemplate s = TemplatePortScanner.scan(ServerHolder.get().getStructureManager(), d.id());
            if (s == null) continue;

            Rotation r = Rotation.values()[rnd.nextInt(4)];
            Vec3i rs = rotSize(s.size(), r);

            // spread around entrance; bias downwards
            int rx = surface.getX() + rnd.nextInt(-(int) cfg.shellMax, (int) cfg.shellMax);
            int rz = surface.getZ() + rnd.nextInt(-(int) cfg.shellMax, (int) cfg.shellMax);
            int ry = surface.getY() - rnd.nextInt(16, 48);

            BlockPos o = new BlockPos(
                    rx - rs.getX() / 2,
                    Math.max(worldMinY + 4, Math.min(ry, worldMaxY - rs.getY() - 2)),
                    rz - rs.getZ() / 2
            );
            Placed p = placeFixed(s, d, r, o);

            // clearance vs existing rooms
            if (!passesClearance(out, p, cfg.cRoom)) continue;

            // ensure at least one portal has an outward free voxel beyond dilation
            VoxelMask3D mRoomTmp = mRoomOut.copy();
            writeRoomToMask(mRoomTmp, p);
            VoxelMask3D mFreePrime = Morph3D.freeMaskFromRooms(mRoomTmp, cfg.effectiveRadius());
            if (!portalsViable(p, mFreePrime, /*minGood*/1, /*stepOut*/cfg.effectiveRadius() + 1)) {
                LOGGER.info("(RoomPlacer) [Proc/Place] {} rejected: portals not viable at {} rot={}",
                        p.def.id(), p.origin, p.rot);
                continue;
            }

            out.add(p);
            writeRoomToMask(mRoomOut, p);
            LOGGER.info("(RoomPlacer) [Proc/Place] {} placed at {} rot={}", p.def.id(), p.origin, p.rot);
        }

        return out;
    }

    /**
     * End room placement with retries: rotations + jitter; detailed failure stats.
     */
    private Placed tryPlaceEnd(ScannedTemplate endScan,
                               BlockPos targetCenter,
                               int worldMinY, int worldMaxY,
                               List<Placed> existing, VoxelMask3D mRoomOut) {

        int rotFailsPortal = 0, rotFailsBroad = 0, rotFailsBounds = 0;

        Rotation[] rots = Rotation.values();
        // try a few rings of jitter
        int attempts = Math.max(32, cfg.orientTries * 2);
        for (int a = 0; a < attempts; a++) {
            Rotation r = rots[rnd.nextInt(rots.length)];
            Vec3i rs = rotSize(endScan.size(), r);

            // small jitter around target center (±6 blocks), slight bias downward
            int jx = rnd.nextInt(-6, 6);
            int jz = rnd.nextInt(-6, 6);
            int jy = rnd.nextInt(-2, 3) - 1;

            BlockPos center = targetCenter.offset(jx, jy, jz);
            BlockPos origin = center.offset(-rs.getX() / 2, -rs.getY() / 2, -rs.getZ() / 2);
            origin = new BlockPos(
                    origin.getX(),
                    Math.max(worldMinY + 4, Math.min(origin.getY(), worldMaxY - rs.getY() - 2)),
                    origin.getZ()
            );

            Placed cand = placeFixed(endScan, RoomDefs.pickEnd(new java.util.Random(rnd.nextLong())), r, origin);

            // world bounds: check mask indexing will be valid
            if (!withinMaskBounds(mRoomOut, cand)) {
                rotFailsBounds++;
                continue;
            }

            // clearance vs existing rooms
            if (!passesClearance(existing, cand, cfg.cRoom)) {
                rotFailsBroad++;
                continue;
            }

            // portals: at least 1 must be viable, checked beyond dilation radius
            VoxelMask3D mRoomTmp = mRoomOut.copy();
            writeRoomToMask(mRoomTmp, cand);
            VoxelMask3D mFreePrime = Morph3D.freeMaskFromRooms(mRoomTmp, cfg.effectiveRadius());
            if (!portalsViable(cand, mFreePrime, /*minGood*/1, /*stepOut*/cfg.effectiveRadius() + 1)) {
                rotFailsPortal++;
                continue;
            }

            LOGGER.info("(RoomPlacer) [Proc/Place] END placed at {} rot={}  (tries={}, broadFails={}, portalFails={}, boundsFails={})",
                    cand.origin, cand.rot, a + 1, rotFailsBroad, rotFailsPortal, rotFailsBounds);
            return cand;
        }

        LOGGER.info("(RoomPlacer) [Proc/Place] END placement failed (broad={} portal={} bounds={})",
                rotFailsBroad, rotFailsPortal, rotFailsBounds);
        return null;
    }

    private boolean withinMaskBounds(VoxelMask3D mask, Placed p) {
        Vec3i sz = rotSize(p.scan.size(), p.rot);
        int x0 = mask.gx(p.origin.getX());
        int y0 = mask.gy(p.origin.getY());
        int z0 = mask.gz(p.origin.getZ());
        int x1 = x0 + sz.getX() - 1;
        int y1 = y0 + sz.getY() - 1;
        int z1 = z0 + sz.getZ() - 1;
        boolean ok = mask.in(x0, y0, z0) && mask.in(x1, y1, z1);
        if (!ok) {
            LOGGER.info("(RoomPlacer) [Bounds] Out-of-bounds AABB gx=[{}..{}] gy=[{}..{}] gz=[{}..{}]", x0, x1, y0, y1, z0, z1);
        }
        return ok;
    }

    private Placed placeFixed(ScannedTemplate s, RoomTemplateDef def, Rotation r, BlockPos origin) {
        Vec3i rs = rotSize(s.size(), r);
        AABB box = new AABB(origin.getX(), origin.getY(), origin.getZ(),
                origin.getX() + rs.getX(), origin.getY() + rs.getY(), origin.getZ() + rs.getZ());
        return new Placed(s, def, r, origin, box);
    }

    private boolean passesClearance(List<Placed> cur, Placed cand, int gap) {
        AABB infl = cand.aabb.inflate(gap);
        for (Placed p : cur) {
            if (infl.intersects(p.aabb.inflate(gap))) {
                // keep this verbose so we can see who clashes
                LOGGER.info("(RoomPlacer) [Clearance] {} at {} overlaps {} at {} (gap={})",
                        cand.def.id(), cand.origin, p.def.id(), p.origin, gap);
                return false;
            }
        }
        return true;
    }

    /**
     * Portal viability: succeed if at least {@code minGood} portals have an outward voxel
     * that is free in {@code mFreePrime}. We project by {@code stepOut} which must be > dilation radius.
     */
    private boolean portalsViable(Placed p, VoxelMask3D mFreePrime, int minGood, int stepOut) {
        List<Port> ports = p.scan.exits().isEmpty() ? p.scan.entrances() : p.scan.exits();
        if (ports.isEmpty()) ports = p.scan.entrances();
        int good = 0;
        for (Port port : ports) {
            BlockPos wp = localToWorldMinCorner(port.localPos(), p.origin, p.scan.size(), p.rot);
            Direction face = Transforms.rotateFacingYaw(port.facing(), p.rot);
            BlockPos outward = wp.relative(face, Math.max(1, stepOut));

            int gx = mFreePrime.gx(outward.getX());
            int gy = mFreePrime.gy(outward.getY());
            int gz = mFreePrime.gz(outward.getZ());

            boolean ok = mFreePrime.in(gx, gy, gz) && mFreePrime.get(gx, gy, gz);
            LOGGER.info("(RoomPlacer) [PortalChk] room={} rot={} portFacing={} wp={} -> outward={}  stepOut={}  ok={}",
                    p.def.id(), p.rot, face, wp, outward, stepOut, ok);

            if (ok) {
                good++;
                if (good >= minGood) return true;
            }
        }
        LOGGER.info("(RoomPlacer) [PortalChk] room={} insufficient viable portals (got {}, need {})",
                p.def.id(), good, minGood);
        return false;
    }

    private void writeRoomToMask(VoxelMask3D mask, Placed p) {
        Vec3i sz = rotSize(p.scan.size(), p.rot);
        int x0 = mask.gx(p.origin.getX());
        int y0 = mask.gy(p.origin.getY());
        int z0 = mask.gz(p.origin.getZ());
        mask.fillAABB(x0, y0, z0, x0 + sz.getX() - 1, y0 + sz.getY() - 1, z0 + sz.getZ() - 1, true);
    }

    /**
     * @param origin world min corner
     * @param aabb   axis-aligned (yaw only)
     */
    record Placed(ScannedTemplate scan, RoomTemplateDef def, Rotation rot, BlockPos origin, AABB aabb) {
    }
}