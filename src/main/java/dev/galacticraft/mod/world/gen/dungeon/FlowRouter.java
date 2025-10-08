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
            List<Port> ports = p.scan().exits().isEmpty() ? p.scan().entrances() : p.scan().exits();
            if (ports.isEmpty()) ports = p.scan().entrances();
            for (Port port : ports) {
                BlockPos wp = Transforms.worldOfLocalMin(port.localPos(), p.origin(), p.scan().size(), p.rot());
                Direction f = Transforms.rotateFacingYaw(port.facing(), p.rot());
                Vec3i rs = Transforms.rotatedSize(p.scan().size(), p.rot());
                int minX = p.origin().getX(), maxX = p.origin().getX() + rs.getX() - 1;
                int minY = p.origin().getY(), maxY = p.origin().getY() + rs.getY() - 1;
                int minZ = p.origin().getZ(), maxZ = p.origin().getZ() + rs.getZ() - 1;

                boolean onFace =
                        (f == Direction.WEST && wp.getX() == minX) ||
                                (f == Direction.EAST && wp.getX() == maxX) ||
                                (f == Direction.NORTH && wp.getZ() == minZ) ||
                                (f == Direction.SOUTH && wp.getZ() == maxZ) ||
                                (f == Direction.DOWN && wp.getY() == minY) ||
                                (f == Direction.UP && wp.getY() == maxY);

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

        for (int i = 0; i < P.size(); i++)
            for (int j = i + 1; j < P.size(); j++) {
                PNode a = P.get(i), b = P.get(j);
                if (a.room == b.room) continue; // don’t connect a room to itself

                boolean horizA = a.facing.getAxis().isHorizontal();
                boolean horizB = b.facing.getAxis().isHorizontal();

                // If both horizontal, require opposite-ish (east<->west or north<->south).
                if (horizA && horizB) {
                    int dot = a.facing.getStepX() * -b.facing.getStepX()
                            + a.facing.getStepZ() * -b.facing.getStepZ();
                    if (dot <= 0) continue; // reject orthogonal / same-facing
                }
                // If any vertical is involved, allow it (entrance shaft etc.). Optionally,
                // you could prefer down<->up or vertical<->horizontal by adding cost.

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

                // simple weight: geodesic length + small penalties
                float w = (float) coarse.size()
                        + cfg.proxPenalty * (float) Math.sqrt(a.worldPos.distSqr(b.worldPos));

                // Add BOTH directions so index ordering never blocks flow:
                E.add(new EdgeCand(i, j, w));
                E.add(new EdgeCand(j, i, w));
            }

        LOGGER.info("(FlowRouter) [Cand] portals={} edges={}", P.size(), E.size());
        return E;
    }

    static List<RoutedPair> pickKDisjoint(List<PNode> P, List<EdgeCand> E, RoomPlacer.Placed startRoom, RoomPlacer.Placed endRoom, int K) {
        record QN(int v, float k) {
        }

        class Edge {
            final int u;
            final int v;
            final float cost;
            int cap;
            Edge rev;

            Edge(int u, int v, int cap, float cost) {
                this.u = u;
                this.v = v;
                this.cap = cap;
                this.cost = cost;
            }
        }

        int n = P.size();
        Net net = new Net();

        // node-split each portal: in[i] -> out[i] (cap 1)
        int[] in = new int[n];
        int[] out = new int[n];
        for (int i = 0; i < n; i++) {
            in[i] = net.addNode();
            out[i] = net.addNode();
            net.addEdge(in[i], out[i], 1, 0f);
        }

        // edges between portals
        for (EdgeCand ec : E) net.addEdge(out[ec.a], in[ec.b], 1, ec.w);

        // super source/sink
        int S = net.addNode(), T = net.addNode();
        for (int i = 0; i < n; i++) {
            if (P.get(i).room == startRoom) net.addEdge(S, in[i], 1, 0f);
            if (P.get(i).room == endRoom) net.addEdge(out[i], T, 1, 0f);
        }

        // maps for quick reverse lookup from graph-node -> portal index
        HashMap<Integer, Integer> inToIdx = new HashMap<>(n * 2);
        HashMap<Integer, Integer> outToIdx = new HashMap<>(n * 2);
        for (int i = 0; i < n; i++) {
            inToIdx.put(in[i], i);
            outToIdx.put(out[i], i);
        }

        final int N = net.g.size();
        float[] dist = new float[N];
        float[] pot = new float[N]; // potentials for reduced costs
        Edge[] prev = new Edge[N];

        class Net {
            final ArrayList<ArrayList<Edge>> g = new ArrayList<>();

            int addNode() {
                g.add(new ArrayList<>());
                return g.size() - 1;
            }

            void addEdge(int u, int v, int cap, float cost) {
                Edge a = new Edge(u, v, cap, cost), b = new Edge(v, u, 0, -cost);
                a.rev = b;
                b.rev = a;
                g.get(u).add(a);
                g.get(v).add(b);
            }
        }
        Comparator<QN> cmp = Comparator.comparingDouble(a -> a.k);

        ArrayList<RoutedPair> pairs = new ArrayList<>(Math.min(K, n));
        float totalCost = 0f;
        int flow = 0;

        while (flow < K) {
            Arrays.fill(dist, Float.POSITIVE_INFINITY);
            Arrays.fill(prev, null);
            dist[S] = 0f;

            PriorityQueue<QN> pq = new PriorityQueue<>(cmp);
            pq.add(new QN(S, 0f));

            boolean[] closed = new boolean[N];
            while (!pq.isEmpty()) {
                QN q = pq.poll();
                int u = q.v;
                if (closed[u]) continue;
                closed[u] = true;
                if (u == T) break;

                for (Edge e : net.g.get(u)) {
                    if (e.cap <= 0) continue;
                    float rcost = e.cost + pot[u] - pot[e.v]; // reduced cost
                    float nd = dist[u] + rcost;
                    if (nd < dist[e.v]) {
                        dist[e.v] = nd;
                        prev[e.v] = e;
                        pq.add(new QN(e.v, nd));
                    }
                }
            }
            if (prev[T] == null) break; // no more augmenting paths

            // update potentials
            for (int i = 0; i < N; i++) if (dist[i] < Float.POSITIVE_INFINITY) pot[i] += dist[i];

            // reconstruct node path S -> ... -> T
            ArrayDeque<Integer> nodePathRev = new ArrayDeque<>();
            int v = T;
            while (v != -1) {
                nodePathRev.add(v);
                if (v == S) break;
                Edge e = prev[v];
                if (e == null) break;
                v = e.u;
            }
            ArrayList<Integer> nodePath = new ArrayList<>(nodePathRev.size());
            while (!nodePathRev.isEmpty()) nodePath.add(nodePathRev.removeLast());
            if (nodePath.size() < 3) break; // should be at least S, something, T

            // the first node after S is in[startIdx]; the last before T is out[endIdx]
            int startInNode = nodePath.get(1);
            int endOutNode = nodePath.get(nodePath.size() - 2);

            Integer startIdx = inToIdx.get(startInNode);
            Integer endIdx = outToIdx.get(endOutNode);
            if (startIdx != null && endIdx != null) {
                pairs.add(new RoutedPair(P.get(startIdx), P.get(endIdx)));
            }

            // push 1 unit of flow along the path
            v = T;
            while (v != S) {
                Edge e = prev[v];
                e.cap -= 1;
                e.rev.cap += 1;
                v = e.u;
            }
            totalCost += pot[T];
            flow++;
        }

        LOGGER.info("(FlowRouter) [Flow] targetK={}  actualFlow={}  cost={}", K, flow, totalCost);
        return pairs;
    }

    static List<RoutedPair> connectAllRooms(List<PNode> P,
                                            List<EdgeCand> E,
                                            List<RoutedPair> mainPairs) {
        // Rooms already on the main path (entrance→end)
        HashSet<RoomPlacer.Placed> connected = new HashSet<>();
        for (RoutedPair rp : mainPairs) {
            connected.add(rp.A.room);
            connected.add(rp.B.room);
        }

        // If somehow mainPairs is empty, seed with the first portal's room so we still build a tree.
        if (connected.isEmpty() && !P.isEmpty()) {
            connected.add(P.get(0).room);
        }

        // Prefer short / cheap edges first (greedy MST-like growth from the main component)
        ArrayList<EdgeCand> edges = new ArrayList<>(E);
        edges.sort(Comparator.comparingDouble(ec -> ec.w));

        ArrayList<RoutedPair> extra = new ArrayList<>();
        HashSet<RoomPlacer.Placed> seen = new HashSet<>(connected);

        for (EdgeCand ec : edges) {
            PNode a = P.get(ec.a), b = P.get(ec.b);
            if (a.room == b.room) continue;               // ignore same-room edges

            boolean aConn = connected.contains(a.room);
            boolean bConn = connected.contains(b.room);

            // Only add edges that attach a new room to the already-connected set
            if (aConn ^ bConn) {
                // Connect the unconnected room to the connected one
                extra.add(new RoutedPair(aConn ? a : b, aConn ? b : a));
                connected.add(a.room);
                connected.add(b.room);

                // Early-out if every room has joined the component
                seen.add(a.room);
                seen.add(b.room);
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