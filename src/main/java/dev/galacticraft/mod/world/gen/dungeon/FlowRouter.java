package dev.galacticraft.mod.world.gen.dungeon;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import org.slf4j.Logger;

import java.util.*;

final class FlowRouter {
    private static final Logger LOGGER = LogUtils.getLogger();

    static List<PNode> collectPortals(List<RoomPlacer.Placed> rooms) {
        ArrayList<PNode> out = new ArrayList<>();
        for (RoomPlacer.Placed p : rooms) {
            // add EXITS first…
            for (Port port : p.scan().exits()) {
                BlockPos wp = Transforms.worldOfLocalMin(port.localPos(), p.origin(), p.scan().size(), p.rot());
                Direction f = Transforms.rotateFacingYaw(port.facing(), p.rot());
                out.add(new PNode(p, port, p.rot(), wp, f));
            }
            // …then ENTRANCES
            for (Port port : p.scan().entrances()) {
                BlockPos wp = Transforms.worldOfLocalMin(port.localPos(), p.origin(), p.scan().size(), p.rot());
                Direction f = Transforms.rotateFacingYaw(port.facing(), p.rot());
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

        int rejSameRoom = 0, rejDir = 0, rejRouteThrow = 0, rejRouteEmpty = 0, rejPolarity = 0;

        for (int i = 0; i < P.size(); i++) {
            for (int j = 0; j < P.size(); j++) {
                if (i == j) continue;

                PNode a0 = P.get(i), b0 = P.get(j);
                if (a0.room == b0.room) { rejSameRoom++; continue; }

                // ---- Normalize polarity so we always store EXIT(A) -> ENTRANCE(B) ----
                boolean a0Exit = DungeonWorldBuilder.isExitNode(a0);
                boolean b0Entr = DungeonWorldBuilder.isEntranceNode(b0);
                boolean a0Entr = DungeonWorldBuilder.isEntranceNode(a0);
                boolean b0Exit = DungeonWorldBuilder.isExitNode(b0);

                PNode a = a0, b = b0;
                int ai = i, bj = j;

                if (a0Exit && b0Entr) {
                    // keep as-is
                } else if (a0Entr && b0Exit) {
                    // flip to keep graph semantics consistent: EXIT -> ENTRANCE
                    a = b0;  b = a0;
                    ai = j;  bj = i;
                } else {
                    // entrance->entrance or exit->exit is useless for routing
                    rejPolarity++;
                    continue;
                }

                // ---- Basic facing sanity (horizontal pairs must oppose) ----
                boolean horizA = a.facing.getAxis().isHorizontal();
                boolean horizB = b.facing.getAxis().isHorizontal();
                if (horizA && horizB) {
                    int dot = a.facing.getStepX() * -b.facing.getStepX()
                            + a.facing.getStepZ() * -b.facing.getStepZ();
                    if (dot <= 0) { rejDir++; continue; } // reject orthogonal/same-facing
                }
                // (Vertical/diagonal mixes are allowed; the router/weights will filter)

                // ---- Route between outward step points ----
                BlockPos aStart = a.worldPos.relative(a.facing, stepOut);
                BlockPos bStart = b.worldPos.relative(b.facing, stepOut);

                List<BlockPos> coarse;
                try {
                    coarse = G.route(aStart, bStart, null, 0, r);
                } catch (Throwable t) {
                    rejRouteThrow++;
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("(FlowRouter)[Cand.throw] a={}{} -> b={}{}  stepOut={}  ex={}",
                                a.worldPos, a.facing, b.worldPos, b.facing, stepOut, t.toString());
                    }
                    continue;
                }
                if (coarse == null || coarse.isEmpty()) { rejRouteEmpty++; continue; }

                float w = (float) coarse.size()
                        + cfg.proxPenalty * (float) Math.sqrt(a.worldPos.distSqr(b.worldPos));

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("(FlowRouter)[Cand.OK] EXIT a={}{} -> ENTR b={}{}  stepOut={}  len={}  w={}",
                            a.worldPos, a.facing, b.worldPos, b.facing, stepOut, coarse.size(), String.format("%.2f", w));
                }
                E.add(new EdgeCand(ai, bj, w));
            }
        }

        LOGGER.info("(FlowRouter)[Cand] portals={} edges={}  rej[sameRoom={}] rej[faceDir={}] rej[polarity={}] rej[routeThrow={}] rej[routeEmpty={}]",
                P.size(), E.size(), rejSameRoom, rejDir, rejPolarity, rejRouteThrow, rejRouteEmpty);

        return E;
    }

    static List<RoutedPair> connectAllRooms(List<PNode> P,
                                            List<EdgeCand> E,
                                            List<RoutedPair> seedPairs,
                                            Set<RoomPlacer.Placed> blockedRooms) {

        // REMOVE the old hardBlocked entrance/queen/end logic.
        HashSet<RoomPlacer.Placed> hardBlocked = new HashSet<>();
        if (blockedRooms != null) hardBlocked.addAll(blockedRooms);

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

            // NEW: don’t connect Queen ↔ Queen in the forest stitch
            var ta = a.room().def().type();
            var tb = b.room().def().type();
            if (ta == RoomTemplateDef.RoomType.QUEEN && tb == RoomTemplateDef.RoomType.QUEEN) continue;

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