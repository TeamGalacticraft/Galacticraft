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

    public static FlowRouter.RoutedPair pickEdgeFromRoomToRoomAvoidingAAndUsedB(
            RoomPlacer.Placed fromRoom,
            RoomPlacer.Placed toRoom,
            FlowRouter.PNode avoidA,
            Set<FlowRouter.PNode> disallowB,
            List<FlowRouter.PNode> P,
            List<FlowRouter.EdgeCand> CE
    ) {
        FlowRouter.EdgeCand best = null;
        float bestW = Float.POSITIVE_INFINITY;
        for (FlowRouter.EdgeCand ec : CE) {
            FlowRouter.PNode A = P.get(ec.a()), B = P.get(ec.b());
            if (A.room() != fromRoom || B.room() != toRoom) continue;
            if (avoidA != null && A == avoidA) continue;
            if (!DungeonWorldBuilder.isExitNode(A) || !DungeonWorldBuilder.isEntranceNode(B)) continue;
            if (disallowB != null && disallowB.contains(B)) continue;
            if (ec.w() < bestW) { bestW = ec.w(); best = ec; }
        }
        return (best == null) ? null : new FlowRouter.RoutedPair(P.get(best.a()), P.get(best.b()));
    }

    /**
     * Try to place rooms (entrance, end, basics, treasure) with baked corridor clearance.
     */
    List<Placed> placeAll(List<RoomTemplateDef> pool, ScannedTemplate entrance, ScannedTemplate end,
                          BlockPos surface, BlockPos targetEndCenter,
                          int worldMinY, int worldMaxY, VoxelMask3D mRoomOut) {

        ArrayList<Placed> out = new ArrayList<>();

        // 1) Entrance: fixed under surface
        BlockPos entOrigin = surface.offset(-entrance.size().getX() / 2, -30, -entrance.size().getZ() / 2);
        Placed ent = placeFixed(entrance, RoomDefs.ENTRANCE, Rotation.NONE, entOrigin);
        out.add(ent);
        writeRoomToMask(mRoomOut, ent);
        LOGGER.info("(RoomPlacer) [Proc/Place] ENTRANCE placed at {} rot={}", ent.origin, ent.rot);

        // 2) End room near the target
        Placed endPlaced = tryPlaceEnd(end, targetEndCenter, worldMinY, worldMaxY, out, mRoomOut);
        if (endPlaced == null) {
            LOGGER.info("(RoomPlacer) [Proc/Place] END placement failed (all tries)");
            return out;
        }
        out.add(endPlaced);
        writeRoomToMask(mRoomOut, endPlaced);

        // 3) Ensure 3 QUEEN rooms near the end
        int queensPlaced = placeQueensNearEnd(out, mRoomOut, targetEndCenter, worldMinY, worldMaxY, /*need*/3);
        LOGGER.info("(RoomPlacer) [Proc/Place] Queens placed near end: {}", queensPlaced);

        // 4) Remaining rooms — truncated-cone distribution; rarer/branch_end lower
        int tries = Math.max(200, cfg.anchorAttempts);
        for (int t = 0; t < tries && out.size() < cfg.maxRooms; t++) {
            RoomTemplateDef d = pick(pool, rnd);
            ScannedTemplate s = TemplatePortScanner.scan(ServerHolder.get().getStructureManager(), d.id());
            if (s == null) continue;

            Rotation r = Rotation.values()[rnd.nextInt(4)];
            Vec3i rs = Transforms.rotatedSize(s.size(), r);

            // --- truncated cone: radius grows with depth from surface ---
            int yTop = surface.getY();
            int yBot = Math.max(worldMinY + 8, endPlaced.origin.getY()); // bottom reference
            int ry = rnd.nextInt(yBot, Math.min(yTop - 2, yBot + 48));      // biased toward bottom
            float depthT = (float) Math.max(0.0, Math.min(1.0, (yTop - ry) / (double) (yTop - (worldMinY + 8))));
            // radius between 0.35..1.0 of shellMax
            double coneR = cfg.shellMax * (0.35 + 0.65 * depthT);

            int rx = surface.getX() + rnd.nextInt((int) -coneR, (int) coneR);
            int rz = surface.getZ() + rnd.nextInt((int) -coneR, (int) coneR);

            BlockPos o = new BlockPos(
                    rx - rs.getX() / 2,
                    Math.max(worldMinY + 4, Math.min(ry, worldMaxY - rs.getY() - 2)),
                    rz - rs.getZ() / 2
            );
            Placed p = placeFixed(s, d, r, o);

            // "rare lower" bias: if BRANCH_END or QUEEN types are high up, reject often
            boolean rare = (d.type() == RoomTemplateDef.RoomType.BRANCH_END);
            float rareKeep = rare ? (0.15f + 0.85f * depthT) : 1.0f; // near top ~15%, bottom ~100%
            if (rnd.nextFloat() > rareKeep) continue;

            if (!passesClearance(out, p, cfg.cRoom)) continue;

            VoxelMask3D mRoomTmp = mRoomOut.copy();
            writeRoomToMask(mRoomTmp, p);
            VoxelMask3D mFreePrime = Morph3D.freeMaskFromRooms(mRoomTmp, cfg.effectiveRadius());
            if (!portalsViable(p, mFreePrime, /*minGood*/1, /*stepOut*/cfg.effectiveRadius() + 1)) continue;

            out.add(p);
            writeRoomToMask(mRoomOut, p);
            LOGGER.info("(RoomPlacer) [Proc/Place] {} placed at {} rot={}", p.def.id(), p.origin, p.rot);
        }

        return out;
    }

    /**
     * NEW: Place an exact multiset of rooms (consumes each def at most once).
     * The list should contain only BASIC and BRANCH_END templates; Entrance, End,
     * and three Queens are still placed by this placer.
     */
    List<Placed> placeAllExact(List<RoomTemplateDef> exactPool, ScannedTemplate entrance, ScannedTemplate end,
                               BlockPos surface, BlockPos targetEndCenter,
                               int worldMinY, int worldMaxY, VoxelMask3D mRoomOut) {

        ArrayList<Placed> out = new ArrayList<>();

        // 1) Entrance (fixed)
        BlockPos entOrigin = surface.offset(-entrance.size().getX() / 2, -30, -entrance.size().getZ() / 2);
        Placed ent = placeFixed(entrance, RoomDefs.ENTRANCE, Rotation.NONE, entOrigin);
        out.add(ent);
        writeRoomToMask(mRoomOut, ent);
        LOGGER.info("(RoomPlacerExact) [Proc/Place] ENTRANCE placed at {} rot={}", ent.origin, ent.rot);

        // 2) End room (near hint)
        Placed endPlaced = tryPlaceEnd(end, targetEndCenter, worldMinY, worldMaxY, out, mRoomOut);
        if (endPlaced == null) {
            LOGGER.info("(RoomPlacerExact) [Proc/Place] END placement failed (all tries)");
            return out;
        }
        out.add(endPlaced);
        writeRoomToMask(mRoomOut, endPlaced);

        // 3) Three QUEEN rooms near the end
        int queensPlaced = placeQueensNearEnd(out, mRoomOut, targetEndCenter, worldMinY, worldMaxY, /*need*/3);
        LOGGER.info("(RoomPlacerExact) [Proc/Place] Queens placed near end: {}", queensPlaced);

        // 4) Consume the exact pool in random order with bounded retries per def
        ArrayDeque<RoomTemplateDef> queue = new ArrayDeque<>(exactPool);
        ArrayList<RoomTemplateDef> shuffled = new ArrayList<>(queue);
        Collections.shuffle(shuffled, rnd);
        queue.clear();
        queue.addAll(shuffled);

        Map<ResourceLocation, Integer> attempts = new HashMap<>();
        int maxAttemptsPerDef = Math.max(8, cfg.perRoomTries / 3);

        while (!queue.isEmpty() && out.size() < cfg.maxRooms) {
            RoomTemplateDef d = queue.removeFirst();
            ScannedTemplate s = TemplatePortScanner.scan(ServerHolder.get().getStructureManager(), d.id());
            if (s == null) continue;

            int tried = attempts.getOrDefault(d.id(), 0);
            if (tried >= maxAttemptsPerDef) {
                LOGGER.info("(RoomPlacerExact) drop {} after {} attempts", d.id(), tried);
                continue;
            }

            // biased cone around lower depths close-ish to endPlaced
            Rotation r = Rotation.values()[rnd.nextInt(4)];
            Vec3i rs = Transforms.rotatedSize(s.size(), r);

            int yTop = surface.getY();
            int yBot = Math.max(worldMinY + 8, endPlaced.origin.getY());
            int ry = rnd.nextInt(yBot, Math.min(yTop - 2, yBot + 48));
            float depthT = (float) Math.max(0.0, Math.min(1.0, (yTop - ry) / (double) (yTop - (worldMinY + 8))));
            double coneR = cfg.shellMax * (0.35 + 0.65 * depthT);

            // small lateral bias toward the end center
            int bx = targetEndCenter.getX();
            int bz = targetEndCenter.getZ();
            int rx = (int) Math.round(bx + rnd.nextGaussian() * coneR * 0.65);
            int rz = (int) Math.round(bz + rnd.nextGaussian() * coneR * 0.65);

            BlockPos o = new BlockPos(
                    rx - rs.getX() / 2,
                    Math.max(worldMinY + 4, Math.min(ry, worldMaxY - rs.getY() - 2)),
                    rz - rs.getZ() / 2
            );
            Placed p = placeFixed(s, d, r, o);

            // "rare lower" bias for BRANCH_END
            boolean rare = (d.type() == RoomTemplateDef.RoomType.BRANCH_END);
            float rareKeep = rare ? (0.15f + 0.85f * depthT) : 1.0f;
            if (rnd.nextFloat() > rareKeep) {
                // retry later
                attempts.put(d.id(), tried + 1);
                queue.addLast(d);
                continue;
            }

            if (!passesClearance(out, p, cfg.cRoom)) {
                attempts.put(d.id(), tried + 1);
                queue.addLast(d);
                continue;
            }

            VoxelMask3D mRoomTmp = mRoomOut.copy();
            writeRoomToMask(mRoomTmp, p);
            VoxelMask3D mFreePrime = Morph3D.freeMaskFromRooms(mRoomTmp, cfg.effectiveRadius());
            if (!portalsViable(p, mFreePrime, /*minGood*/1, /*stepOut*/cfg.effectiveRadius() + 1)) {
                attempts.put(d.id(), tried + 1);
                queue.addLast(d);
                continue;
            }

            out.add(p);
            writeRoomToMask(mRoomOut, p);
            LOGGER.info("(RoomPlacerExact) [Proc/Place] {} placed at {} rot={}", p.def.id(), p.origin, p.rot);
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
            Vec3i rs = Transforms.rotatedSize(endScan.size(), r);

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
        Vec3i sz = Transforms.rotatedSize(p.scan.size(), p.rot);
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
        Vec3i rs = Transforms.rotatedSize(s.size(), r);
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
            BlockPos wp = Transforms.worldOfLocalMin(port.localPos(), p.origin, p.scan.size(), p.rot);
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
        Vec3i sz = Transforms.rotatedSize(p.scan.size(), p.rot);
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

    /**
     * Try to place up to {@code need} queen rooms clustered around the end center.
     */
    private int placeQueensNearEnd(List<Placed> out, VoxelMask3D mRoomOut,
                                   BlockPos endCenter, int worldMinY, int worldMaxY, int need) {
        int placed = 0;
        if (need <= 0) return 0;

        ArrayList<RoomTemplateDef> qdefs = new ArrayList<>(RoomDefs.QUEEN);
        Collections.shuffle(qdefs, rnd);

        // small rings around the end center
        int[] radii = {6, 10, 14, 18, 22};
        int attempts = 0;

        for (RoomTemplateDef qdef : qdefs) {
            if (placed >= need) break;

            ScannedTemplate s = TemplatePortScanner.scan(ServerHolder.get().getStructureManager(), qdef.id());
            if (s == null) continue;

            // try several rings and rotations
            outer:
            for (int ring : radii) {
                for (int a = 0; a < 12; a++) { // 30-degree steps
                    if (placed >= need) break outer;
                    attempts++;

                    double theta = (Math.PI * 2.0 / 12.0) * a + rnd.nextDouble() * 0.15;
                    int cx = endCenter.getX() + (int) Math.round(ring * Math.cos(theta));
                    int cz = endCenter.getZ() + (int) Math.round(ring * Math.sin(theta));
                    int cy = Math.max(worldMinY + 4, Math.min(endCenter.getY() + rnd.nextInt(-2, 3), worldMaxY - s.size().getY() - 2));

                    Rotation r = Rotation.values()[rnd.nextInt(4)];
                    Vec3i rs = Transforms.rotatedSize(s.size(), r);
                    BlockPos origin = new BlockPos(cx - rs.getX() / 2, cy - rs.getY() / 2, cz - rs.getZ() / 2);

                    Placed cand = placeFixed(s, qdef, r, origin);
                    if (!passesClearance(out, cand, cfg.cRoom)) continue;

                    VoxelMask3D mTmp = mRoomOut.copy();
                    writeRoomToMask(mTmp, cand);
                    VoxelMask3D mFreePrime = Morph3D.freeMaskFromRooms(mTmp, cfg.effectiveRadius());
                    if (!portalsViable(cand, mFreePrime, /*minGood*/1, /*stepOut*/cfg.effectiveRadius() + 1)) continue;

                    out.add(cand);
                    writeRoomToMask(mRoomOut, cand);
                    placed++;
                    LOGGER.info("(RoomPlacer) [QueenNearEnd] {} placed at {} rot={} (attempts={})",
                            qdef.id(), cand.origin, cand.rot, attempts);
                }
            }
        }
        return placed;
    }
}