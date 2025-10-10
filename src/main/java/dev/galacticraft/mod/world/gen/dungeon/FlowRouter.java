package dev.galacticraft.mod.world.gen.dungeon;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;
import org.slf4j.Logger;

import java.util.*;

final class FlowRouter {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Build portal set (all portals on each placed room, transformed to world).
     */
    static List<PNode> collectPortals(List<RoomPlacer.Placed> rooms) {
        ArrayList<PNode> out = new ArrayList<>();
        for (RoomPlacer.Placed p : rooms) {
            // prefer exits (if any), else entrances; fall back to entrances again if both empty
            List<Port> ports = p.scan().exits().isEmpty() ? p.scan().entrances() : p.scan().exits();
            if (ports.isEmpty()) ports = p.scan().entrances();

            Vec3i rs = Transforms.rotatedSize(p.scan().size(), p.rot());
            int minX = p.origin().getX(), maxX = p.origin().getX() + rs.getX() - 1;
            int minY = p.origin().getY(), maxY = p.origin().getY() + rs.getY() - 1;
            int minZ = p.origin().getZ(), maxZ = p.origin().getZ() + rs.getZ() - 1;

            for (Port port : ports) {
                BlockPos wp = Transforms.worldOfLocalMin(port.localPos(), p.origin(), p.scan().size(), p.rot());
                Direction f = Transforms.rotateFacingYaw(port.facing(), p.rot());

                boolean onFace =
                        (f == Direction.WEST  && wp.getX() == minX) ||
                                (f == Direction.EAST  && wp.getX() == maxX) ||
                                (f == Direction.NORTH && wp.getZ() == minZ) ||
                                (f == Direction.SOUTH && wp.getZ() == maxZ) ||
                                (f == Direction.DOWN  && wp.getY() == minY) ||
                                (f == Direction.UP    && wp.getY() == maxY);

                if (!onFace) {
                    LOGGER.warn("(PortalXform) MISMATCH room={} rot={} origin={} size(rot)={} local={} face={} world={} AABB[min={},max={}]",
                            p.def().id(), p.rot(), p.origin(), rs, port.localPos(), f, wp,
                            new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
                }

                LOGGER.info("(FlowRouter)[PortalXform] room={} rot={} origin={} size={} local={} face={} -> world={} faceR={}",
                        p.def().id(), p.rot(), p.origin(), p.scan().size(), port.localPos(), port.facing(), wp, f);
                out.add(new PNode(p, port, p.rot(), wp, f));
            }
        }
        LOGGER.info("(FlowRouter) [Portal] collected portals: {}", out.size());
        return out;
    }

    /**
     * Candidate edges: portals are connectable if facing opposite-ish and within geodesic reach on G_free.
     */
    static List<EdgeCand> candidateEdges(List<PNode> P, FreeGraph G, ProcConfig cfg) {
        ArrayList<EdgeCand> E = new ArrayList<>();
        Random r = new Random();

        final int stepOut = Math.max(1, cfg.effectiveRadius() + 1);

        for (int i = 0; i < P.size(); i++) {
            for (int j = 0; j < P.size(); j++) {
                if (i == j) continue;

                PNode a = P.get(i), b = P.get(j);
                if (a.room == b.room) continue; // never connect a room to itself

                boolean horizA = a.facing.getAxis().isHorizontal();
                boolean horizB = b.facing.getAxis().isHorizontal();

                // If both horizontal, require opposite-ish
                if (horizA && horizB) {
                    int dot = a.facing.getStepX() * -b.facing.getStepX()
                            + a.facing.getStepZ() * -b.facing.getStepZ();
                    if (dot <= 0) continue; // reject orthogonal/same-facing
                }
                // If vertical involved, allow (shafts/ramps)

                BlockPos aStart = a.worldPos.relative(a.facing, stepOut);
                BlockPos bStart = b.worldPos.relative(b.facing, stepOut);

                List<BlockPos> coarse;
                try {
                    coarse = G.route(aStart, bStart, null, 0, r);
                } catch (Throwable t) {
                    LOGGER.warn("(FlowRouter) [Cand] route threw: {}  a={} b={} aStart={} bStart={}",
                            t, a.worldPos, b.worldPos, aStart, bStart);
                    continue;
                }
                if (coarse == null || coarse.isEmpty()) continue;

                float w = (float) coarse.size()
                        + cfg.proxPenalty * (float) Math.sqrt(a.worldPos.distSqr(b.worldPos));

                // directed candidate (both directions will be considered across i/j loop)
                E.add(new EdgeCand(i, j, w));
            }
        }

        LOGGER.info("(FlowRouter) [Cand] portals={} edges={}", P.size(), E.size());
        return E;
    }

    static List<RoutedPair> connectAllRooms(List<PNode> P,
                                            List<EdgeCand> E,
                                            List<RoutedPair> seedPairs,
                                            Set<RoomPlacer.Placed> blockedRooms) {
        // Hard-block Entrance, all Queens, and End from receiving extra edges.
        HashSet<RoomPlacer.Placed> hardBlocked = new HashSet<>();
        if (blockedRooms != null) hardBlocked.addAll(blockedRooms);
        for (PNode pn : P) {
            RoomTemplateDef.RoomType t = pn.room().def().type();
            if (t == RoomTemplateDef.RoomType.ENTRANCE
                    || t == RoomTemplateDef.RoomType.QUEEN
                    || t == RoomTemplateDef.RoomType.END) {
                hardBlocked.add(pn.room());
            }
        }

        HashSet<RoomPlacer.Placed> connected = new HashSet<>();
        for (RoutedPair rp : seedPairs) {
            connected.add(rp.A.room());
            connected.add(rp.B.room());
        }
        if (connected.isEmpty() && !P.isEmpty()) connected.add(P.get(0).room());

        ArrayList<EdgeCand> edges = new ArrayList<>(E);
        edges.sort(Comparator.comparingDouble(ec -> ec.w));

        ArrayList<RoutedPair> extra = new ArrayList<>();

        for (EdgeCand ec : edges) {
            PNode a = P.get(ec.a), b = P.get(ec.b);
            if (a.room == b.room) continue;
            if (hardBlocked.contains(a.room) || hardBlocked.contains(b.room)) continue;

            boolean aConn = connected.contains(a.room);
            boolean bConn = connected.contains(b.room);
            if (aConn ^ bConn) {
                extra.add(new RoutedPair(aConn ? a : b, aConn ? b : a));
                connected.add(a.room);
                connected.add(b.room);
            }
        }
        return extra;
    }


    record PNode(RoomPlacer.Placed room, Port port, Rotation rot, BlockPos worldPos, Direction facing) {
    }

    record EdgeCand(int a, int b, float w) {
    }

    record RoutedPair(PNode A, PNode B) {
    }
}