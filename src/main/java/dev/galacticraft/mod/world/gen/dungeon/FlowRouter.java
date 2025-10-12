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
     * Now with: (1) quick distance cull, (2) limit routing to top-K closest pairs to avoid O(N^2) routes.
     */
    static List<EdgeCand> candidateEdges(List<PNode> P, FreeGraph unusedGraph, ProcConfig cfg) {
        ArrayList<EdgeCand> E = new ArrayList<>();

        final int stepOut = Math.max(1, cfg.effectiveRadius() + 1);
        final int MAX_MD  = Math.max(96, cfg.effectiveRadius() * 16);
        final int TOP_K   = 12;

        int rejSameRoom = 0, rejPolarity = 0, rejDist = 0;

        record Quick(int ai, int bj, int md, float dirPenalty) {}
        ArrayList<Quick> quick = new ArrayList<>();

        for (int i = 0; i < P.size(); i++) {
            for (int j = 0; j < P.size(); j++) {
                if (i == j) continue;

                PNode a0 = P.get(i), b0 = P.get(j);
                if (a0.room == b0.room) { rejSameRoom++; continue; }

                // STRICT: EXIT(A) -> ENTR(B) only
                if (!DungeonWorldBuilder.isExitNode(a0) || !DungeonWorldBuilder.isEntranceNode(b0)) {
                    rejPolarity++; continue;
                }

                BlockPos aStart = a0.worldPos.relative(a0.facing, stepOut);
                BlockPos bStart = b0.worldPos.relative(b0.facing, stepOut);

                int md = Math.abs(aStart.getX() - bStart.getX())
                        + Math.abs(aStart.getY() - bStart.getY())
                        + Math.abs(aStart.getZ() - bStart.getZ());
                if (md > MAX_MD) { rejDist++; continue; }

                // Soft facing penalty (prefer opposing)
                float dirPenalty = 0f;
                boolean horizA = a0.facing.getAxis().isHorizontal();
                boolean horizB = b0.facing.getAxis().isHorizontal();
                if (horizA && horizB) {
                    int dot = a0.facing.getStepX() * -b0.facing.getStepX()
                            + a0.facing.getStepZ() * -b0.facing.getStepZ(); // +2 best (opposite), -2 worst (same)
                    if (dot < 2) dirPenalty = (2 - dot) * 8f; // 0,8,16,24
                } else {
                    dirPenalty = 12f;
                }

                quick.add(new Quick(i, j, md, dirPenalty));
            }
        }

        // Route only the TOP_K closest by Manhattan distance
        quick.sort(Comparator.comparingInt(q -> q.md));
        int routed = 0;
        for (Quick q : quick) {
            if (routed >= TOP_K) break;
            // weight is just a heuristic for later tie-breaks
            float w = q.dirPenalty + (float) q.md;
            E.add(new EdgeCand(q.ai, q.bj, w));
            routed++;
        }

        LOGGER.info("(FlowRouter)[Cand] portals={} edges={} rej[sameRoom={}] rej[polarity={}] rej[distMD>{}={}]",
                P.size(), E.size(), rejSameRoom, rejPolarity, MAX_MD, rejDist);
        return E;
    }

    record PNode(RoomPlacer.Placed room, Port port, Rotation rot, BlockPos worldPos, Direction facing) {
    }

    record EdgeCand(int a, int b, float w) {
    }

    record RoutedPair(PNode A, PNode B) {
    }
}