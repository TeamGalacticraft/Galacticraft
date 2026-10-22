/*
 * Copyright (c) 2019-2026 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.api.grid;

import com.google.common.collect.ImmutableSet;
import com.google.common.graph.EndpointPair;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.network.s2c.GridDebugPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Holds all grids of a level, saved with the level.
 * Ported from ThermalDynamics' {@code GridContainer} (by covers1624).
 */
public class GridContainer extends SavedData {
    public static final String ID = Constant.MOD_ID + "_grids";

    private static final String GRIDS = "Grids";
    private static final String GRID_ID = "Id";
    private static final String GRID_TYPE = "Type";
    private static final String BLOCKED = "Blocked";
    private static final String VERSION = "Version";
    /**
     * The version of the saved grids. Grids saved with another version are dropped on load and built again from the ducts,
     * for when the way ducts form grids changed.
     */
    private static final int DATA_VERSION = 2;

    private final ServerLevel level;
    /**
     * The grid at every duct position, both nodes and the ducts on edges. Rebuilt on load.
     */
    private final Map<BlockPos, Grid<?, ?>> gridPosLookup = new HashMap<>();
    private final Map<UUID, Grid<?, ?>> grids = new LinkedHashMap<>();
    private final Map<UUID, Grid<?, ?>> loadedGrids = new LinkedHashMap<>();
    /**
     * Ducts that were loaded without being in a grid, like in worlds from before grids existed.
     */
    private final Set<BlockPos> pendingDucts = new LinkedHashSet<>();
    /**
     * Pairs of adjacent ducts that could connect, but don't because of what their grids hold (like two different fluids).
     */
    private final Set<PosPair> blockedPairs = new HashSet<>();
    private final Set<Grid<?, ?>> retryRequests = new LinkedHashSet<>();

    private GridContainer(ServerLevel level) {
        this.level = level;
    }

    public static GridContainer get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(
                () -> new GridContainer(level),
                (tag, registries) -> load(level, tag, registries),
                null
        ), ID);
    }

    // ---- Public entry points for ducts ----

    /**
     * Adds a duct that was just placed to the grids, merging grids where needed.
     */
    public void onDuctPlaced(Duct<?, ?> duct) {
        if (this.gridPosLookup.containsKey(duct.getHostPos())) {
            return;
        }
        this.gridHostPlaced(duct);
    }

    /**
     * Removes a duct from its grid, splitting the grid where needed.
     */
    public void onDuctRemoved(Duct<?, ?> duct) {
        this.removeGridHost(duct);
    }

    /**
     * Updates the node at a duct after a block next to it changed.
     *
     * @return whether the node structure changed
     */
    public boolean onDuctNeighborChanged(Duct<?, ?> duct) {
        return this.gridNeighborChanged(duct);
    }

    /**
     * Queues a duct that was loaded, so it joins a grid if it isn't in one yet.
     */
    public void onDuctLoaded(Duct<?, ?> duct) {
        if (!this.gridPosLookup.containsKey(duct.getHostPos())) {
            this.pendingDucts.add(duct.getHostPos().immutable());
        }
    }

    /**
     * Asks to connect the blocked ducts of the given grid again at the end of this tick, as what it holds changed.
     */
    public void requestRetryBlocked(Grid<?, ?> grid) {
        this.retryRequests.add(grid);
    }

    public @Nullable Grid<?, ?> getGrid(BlockPos pos) {
        return this.gridPosLookup.get(pos);
    }

    @SuppressWarnings("unchecked")
    public <G extends Grid<G, ?>> @Nullable G getGrid(GridType<G> type, BlockPos pos) {
        Grid<?, ?> grid = this.gridPosLookup.get(pos);
        if (grid == null) {
            return null;
        }
        if (grid.getType() != type) {
            throw new IllegalStateException("Expected grid type " + type + " at " + pos + ", got " + grid.getType());
        }
        return (G) grid;
    }

    public @Nullable Grid<?, ?> getGrid(UUID id) {
        return this.grids.get(id);
    }

    public Iterable<Grid<?, ?>> getGrids() {
        return this.grids.values();
    }

    public Iterable<Grid<?, ?>> getLoadedGrids() {
        return this.loadedGrids.values();
    }

    // ---- Events ----

    public void tick() {
        if (!this.pendingDucts.isEmpty()) {
            List<BlockPos> pending = new ArrayList<>(this.pendingDucts);
            this.pendingDucts.clear();
            for (BlockPos pos : pending) {
                Duct<?, ?> duct = GridHelper.getDuct(this.level, pos);
                if (duct != null && !this.gridPosLookup.containsKey(pos)) {
                    this.gridHostPlaced(duct);
                }
            }
        }

        for (Grid<?, ?> grid : new ArrayList<>(this.loadedGrids.values())) {
            if (grid.isRemoved()) continue;
            try {
                grid.tick();
            } catch (Exception e) {
                Constant.LOGGER.error("Exception ticking grid {}", grid.getId(), e);
            }
        }

        if (Grid.DEBUG && this.level.getGameTime() % 10 == 0) {
            this.sendDebugShapes();
        }

        if (!this.retryRequests.isEmpty()) {
            List<Grid<?, ?>> requests = new ArrayList<>(this.retryRequests);
            this.retryRequests.clear();
            for (Grid<?, ?> grid : requests) {
                if (!grid.isRemoved()) {
                    this.retryBlocked(grid);
                }
            }
        }
    }

    public void onChunkLoad(ChunkAccess chunk) {
        for (Grid<?, ?> grid : this.grids.values()) {
            if (grid.onChunkLoad(chunk)) {
                this.loadedGrids.put(grid.getId(), grid);
            }
        }
    }

    public void onChunkUnload(ChunkAccess chunk) {
        this.loadedGrids.values().removeIf(grid -> grid.onChunkUnload(chunk));
    }

    private void sendDebugShapes() {
        if (this.level.players().isEmpty()) return;
        List<GridDebugPayload.GridShape> shapes = new ArrayList<>();
        for (Grid<?, ?> grid : this.loadedGrids.values()) {
            List<Long> nodes = new ArrayList<>();
            for (BlockPos pos : grid.getNodes().keySet()) {
                nodes.add(pos.asLong());
            }
            List<Long> edges = new ArrayList<>();
            for (EndpointPair<? extends GridNode<?>> edge : grid.nodeGraph.edges()) {
                edges.add(edge.nodeU().getPos().asLong());
                edges.add(edge.nodeV().getPos().asLong());
            }
            shapes.add(new GridDebugPayload.GridShape(grid.getId(), nodes, edges));
        }
        GridDebugPayload payload = new GridDebugPayload(shapes);
        for (ServerPlayer player : this.level.players()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    // ---- Grid structure ----

    private <G extends Grid<G, N>, N extends GridNode<G>> void gridHostPlaced(Duct<?, ?> placed) {
        @SuppressWarnings("unchecked") Duct<G, N> host = (Duct<G, N>) placed;
        this.constructNewGrid(host);

        for (Direction dir : Constant.Misc.DIRECTIONS) {
            Duct<?, ?> adjacent = GridHelper.getDuct(this.level, host.getHostPos().relative(dir));
            if (adjacent == null || adjacent.getGridType() != host.getGridType() || adjacent.getGrid() == null) {
                continue;
            }
            @SuppressWarnings("unchecked") Duct<G, N> other = (Duct<G, N>) adjacent;
            if (!canConnect(host, other, dir)) {
                this.blockIfOnlyContentsDiffer(host, other, dir);
                continue;
            }

            if (other.getGrid() != host.getGrid()) {
                this.mergeGrids(List.of(other.getGrid(), host.getGrid()));
            }
            G grid = other.getGrid();
            assert grid != null && grid == host.getGrid();
            N otherNode = grid.getNodeOrSplitEdgeAndInsertNode(other.getHostPos());
            N thisNode = grid.getNodeOrSplitEdgeAndInsertNode(host.getHostPos());
            grid.nodeGraph.putEdge(otherNode, thisNode);
            this.simplifyNode(otherNode);
            this.simplifyNode(thisNode);
        }

        G grid = host.getGrid();
        assert grid != null;
        grid.onModified();
    }

    private <G extends Grid<G, N>, N extends GridNode<G>> G constructNewGrid(Duct<G, N> host) {
        G grid = this.createAndAddGrid(this.nextUUID(), host.getGridType());
        host.setGrid(grid);
        grid.newNode(host.getHostPos());
        this.addGridLookup(grid, host.getHostPos());
        grid.onGridHostAdded(host);
        this.refreshLoaded(grid);
        grid.onModified();
        return grid;
    }

    private <G extends Grid<G, N>, N extends GridNode<G>> void mergeGrids(List<G> branches) {
        Set<G> grids = new LinkedHashSet<>(branches);
        if (grids.size() <= 1) return;

        G main = null;
        for (G grid : grids) {
            if (main == null || grid.nodeGraph.nodes().size() > main.nodeGraph.nodes().size()) {
                main = grid;
            }
        }
        for (G other : grids) {
            if (other == main) continue;
            Set<BlockPos> moved = other.getAllPositions();
            main.mergeFrom(other);
            for (BlockPos pos : moved) {
                this.replaceGridLookup(main, other, pos);
            }
            this.removeGrid(other);
        }
        this.refreshLoaded(main);
        main.onModified();
    }

    private <G extends Grid<G, N>, N extends GridNode<G>> void removeGridHost(Duct<?, ?> removedDuct) {
        @SuppressWarnings("unchecked") Duct<G, N> host = (Duct<G, N>) removedDuct;
        BlockPos hostPos = host.getHostPos();
        this.blockedPairs.removeIf(pair -> pair.contains(hostPos));
        this.pendingDucts.remove(hostPos);

        Grid<?, ?> lookup = this.gridPosLookup.get(hostPos);
        if (lookup == null) {
            return;
        }
        @SuppressWarnings("unchecked") G grid = (G) lookup;
        this.removeGridLookup(grid, hostPos);

        N removing = grid.getNodes().get(hostPos);
        if (removing != null) {
            // Put a node on the first duct along every edge, so the edges end next to the removed duct
            List<N> adjacentNodes = new LinkedList<>();
            Set<EndpointPair<N>> edges = ImmutableSet.copyOf(grid.nodeGraph.incidentEdges(removing));
            for (EndpointPair<N> edge : edges) {
                N other = edge.nodeU() == removing ? edge.nodeV() : edge.nodeU();
                adjacentNodes.add(grid.getNodeOrSplitEdgeAndInsertNode(GridHelper.stepTowards(hostPos, other.getPos())));
            }
            grid.removeNode(removing);
            for (N adjacent : adjacentNodes) {
                this.simplifyNode(adjacent);
            }
        } else {
            // The duct is on an edge: cut the edge on both sides of it
            EndpointPair<N> edge = grid.findEdge(hostPos);
            if (edge == null) {
                Constant.LOGGER.error("Duct at {} is in grid {} but not on any of its edges", hostPos, grid.getId());
                return;
            }
            N a = grid.getNodeOrSplitEdgeAndInsertNode(GridHelper.stepTowards(hostPos, edge.nodeU().getPos()));
            N b = grid.getNodeOrSplitEdgeAndInsertNode(GridHelper.stepTowards(hostPos, edge.nodeV().getPos()));
            grid.nodeGraph.removeEdge(a, b);
            this.simplifyNode(a);
            this.simplifyNode(b);
        }

        if (!grid.getNodes().isEmpty()) {
            grid.onGridHostRemoved(host);
            if (!this.separateGrids(grid)) {
                grid.onModified();
            }
        } else {
            this.removeGrid(grid);
        }
    }

    private <G extends Grid<G, N>, N extends GridNode<G>> boolean gridNeighborChanged(Duct<?, ?> changed) {
        @SuppressWarnings("unchecked") Duct<G, N> duct = (Duct<G, N>) changed;
        G grid = duct.getGrid();
        if (grid == null) {
            return false;
        }

        N node = grid.getNodes().get(duct.getHostPos());
        boolean external = grid.canConnectExternally(duct.getHostPos());
        if (node != null) {
            node.clearConnections();
            if (!external && this.simplifyNode(node)) {
                grid.onModified();
            }
            return true;
        } else if (external) {
            grid.getNodeOrSplitEdgeAndInsertNode(duct.getHostPos());
            grid.onModified();
            return true;
        }
        return false;
    }

    /**
     * Connects two adjacent ducts that are not connected yet, merging their grids if needed.
     */
    private <G extends Grid<G, N>, N extends GridNode<G>> void connectDucts(Duct<G, N> a, Duct<G, N> b) {
        G aGrid = a.getGrid();
        G bGrid = b.getGrid();
        if (aGrid == null || bGrid == null || aGrid == bGrid && aGrid.isConnectedTo(a.getHostPos(), b.getHostPos())) {
            return;
        }

        N aNode = aGrid.getNodeOrSplitEdgeAndInsertNode(a.getHostPos());
        N bNode = bGrid.getNodeOrSplitEdgeAndInsertNode(b.getHostPos());
        if (aGrid != bGrid) {
            this.mergeGrids(List.of(aGrid, bGrid));
        }
        G grid = a.getGrid();
        assert grid != null;
        grid.nodeGraph.putEdge(aNode, bNode);
        for (N adj : List.copyOf(grid.nodeGraph.adjacentNodes(aNode))) {
            if (adj != bNode) this.simplifyNode(adj);
        }
        for (N adj : List.copyOf(grid.nodeGraph.adjacentNodes(bNode))) {
            if (adj != aNode) this.simplifyNode(adj);
        }
        this.simplifyNode(aNode);
        this.simplifyNode(bNode);
        grid.onModified();
    }

    private <G extends Grid<G, N>, N extends GridNode<G>> void retryBlocked(Grid<?, ?> retrying) {
        for (PosPair pair : List.copyOf(this.blockedPairs)) {
            BlockPos aPos = BlockPos.of(pair.a());
            BlockPos bPos = BlockPos.of(pair.b());
            if (this.gridPosLookup.get(aPos) != retrying && this.gridPosLookup.get(bPos) != retrying) continue;

            Duct<?, ?> aDuct = GridHelper.getDuct(this.level, aPos);
            Duct<?, ?> bDuct = GridHelper.getDuct(this.level, bPos);
            if (aDuct == null || bDuct == null || aDuct.getGridType() != bDuct.getGridType()) continue;

            @SuppressWarnings("unchecked") Duct<G, N> a = (Duct<G, N>) aDuct;
            @SuppressWarnings("unchecked") Duct<G, N> b = (Duct<G, N>) bDuct;
            Direction dir = GridHelper.getSide(aPos, bPos);
            if (canConnect(a, b, dir)) {
                this.blockedPairs.remove(pair);
                this.connectDucts(a, b);
                a.onGridConnectionChanged(dir);
                b.onGridConnectionChanged(dir.getOpposite());
            }
        }
    }

    /**
     * Removes a node that is a straight pass-through between two other nodes, joining its two edges into one.
     *
     * @return whether the node was removed
     */
    private <G extends Grid<G, N>, N extends GridNode<G>> boolean simplifyNode(N node) {
        G grid = node.getGrid();
        if (!grid.nodeGraph.nodes().contains(node)) return false;

        // Only simplify where we can see the surrounding blocks
        if (!this.level.isLoaded(node.getPos()) || grid.canConnectExternally(node.getPos())) {
            return false;
        }

        List<N> edges = new ArrayList<>(grid.nodeGraph.adjacentNodes(node));
        if (edges.size() != 2) {
            return false;
        }
        N a = edges.get(0);
        N b = edges.get(1);
        if (!GridHelper.isOnSameAxis(a.getPos(), b.getPos())) {
            return false;
        }

        grid.removeNode(node);
        grid.nodeGraph.putEdge(a, b);
        return true;
    }

    private <G extends Grid<G, N>, N extends GridNode<G>> boolean separateGrids(G grid) {
        List<Set<N>> parts = GraphHelper.separateGraphs(grid.nodeGraph);
        if (parts.size() <= 1) {
            return false;
        }

        List<G> newGrids = grid.splitInto(parts, this);
        for (G newGrid : newGrids) {
            for (BlockPos pos : newGrid.getAllPositions()) {
                this.replaceGridLookup(newGrid, grid, pos);
            }
            this.refreshLoaded(newGrid);
            newGrid.onModified();
        }
        this.removeGrid(grid);
        return true;
    }

    // ---- Bookkeeping ----

    <G extends Grid<G, ?>> G createAndAddGrid(UUID id, GridType<G> type, boolean load) {
        G grid = this.createAndAddGrid(id, type);
        if (load) {
            this.refreshLoaded(grid);
        }
        return grid;
    }

    private <G extends Grid<G, ?>> G createAndAddGrid(UUID id, GridType<G> type) {
        G grid = type.create(id, this.level);
        Grid<?, ?> existing = this.grids.put(id, grid);
        assert existing == null;
        return grid;
    }

    private void refreshLoaded(Grid<?, ?> grid) {
        if (grid.isLoaded() && !grid.isRemoved()) {
            this.loadedGrids.put(grid.getId(), grid);
        } else {
            this.loadedGrids.remove(grid.getId());
        }
    }

    private void removeGrid(Grid<?, ?> grid) {
        this.grids.remove(grid.getId());
        this.loadedGrids.remove(grid.getId());
        this.retryRequests.remove(grid);
        grid.markRemoved();
    }

    UUID nextUUID() {
        UUID id;
        do {
            id = UUID.randomUUID();
        } while (this.grids.containsKey(id));
        return id;
    }

    private void addGridLookup(Grid<?, ?> grid, BlockPos pos) {
        Grid<?, ?> previous = this.gridPosLookup.put(pos.immutable(), grid);
        assert previous == null : "Duct at " + pos + " was already in grid " + previous;
    }

    private void removeGridLookup(Grid<?, ?> grid, BlockPos pos) {
        Grid<?, ?> previous = this.gridPosLookup.remove(pos);
        assert previous == grid : "Duct at " + pos + " was in grid " + previous + ", expected " + grid;
    }

    private void replaceGridLookup(Grid<?, ?> newGrid, Grid<?, ?> oldGrid, BlockPos pos) {
        Grid<?, ?> previous = this.gridPosLookup.put(pos.immutable(), newGrid);
        assert previous == oldGrid || previous == newGrid : "Duct at " + pos + " was in grid " + previous + ", expected " + oldGrid;
    }

    private static <G extends Grid<G, N>, N extends GridNode<G>> boolean canConnect(Duct<G, N> from, Duct<G, N> to, Direction dir) {
        return from.canConnectTo(to, dir) && to.canConnectTo(from, dir.getOpposite());
    }

    private <G extends Grid<G, N>, N extends GridNode<G>> void blockIfOnlyContentsDiffer(Duct<G, N> a, Duct<G, N> b, Direction dir) {
        if (a.canEverConnectTo(b, dir) && b.canEverConnectTo(a, dir.getOpposite())) {
            this.blockedPairs.add(PosPair.of(a.getHostPos(), b.getHostPos()));
        }
    }

    // ---- Saving ----

    @Override
    public boolean isDirty() {
        return true; // Grids change all the time, always save them
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt(VERSION, DATA_VERSION);
        ListTag grids = new ListTag();
        for (Grid<?, ?> grid : this.grids.values()) {
            CompoundTag gridTag = grid.save(registries);
            gridTag.putUUID(GRID_ID, grid.getId());
            gridTag.putString(GRID_TYPE, grid.getType().id().toString());
            grids.add(gridTag);
        }
        tag.put(GRIDS, grids);

        ListTag blocked = new ListTag();
        for (PosPair pair : this.blockedPairs) {
            blocked.add(new LongArrayTag(new long[]{pair.a(), pair.b()}));
        }
        tag.put(BLOCKED, blocked);
        return tag;
    }

    private static GridContainer load(ServerLevel level, CompoundTag tag, HolderLookup.Provider registries) {
        GridContainer container = new GridContainer(level);
        if (tag.getInt(VERSION) != DATA_VERSION) {
            // The ducts join new grids when they are loaded
            Constant.LOGGER.info("Rebuilding the grids of {}, they were saved by another version", level.dimension().location());
            return container;
        }
        ListTag grids = tag.getList(GRIDS, Tag.TAG_COMPOUND);
        for (int i = 0; i < grids.size(); i++) {
            CompoundTag gridTag = grids.getCompound(i);
            ResourceLocation typeId = ResourceLocation.tryParse(gridTag.getString(GRID_TYPE));
            GridType<?> type = typeId == null ? null : GridType.get(typeId);
            if (type == null) {
                Constant.LOGGER.error("Skipping grid with unknown type {}", gridTag.getString(GRID_TYPE));
                continue;
            }
            Grid<?, ?> grid = container.createAndAddGrid(gridTag.getUUID(GRID_ID), type);
            grid.load(gridTag, registries);
            for (BlockPos pos : grid.getAllPositions()) {
                Grid<?, ?> previous = container.gridPosLookup.put(pos, grid);
                if (previous != null) {
                    Constant.LOGGER.error("Duct at {} is in both grid {} and {}", pos, previous.getId(), grid.getId());
                }
            }
            grid.syncLoadedChunks();
            container.refreshLoaded(grid);
        }

        ListTag blocked = tag.getList(BLOCKED, Tag.TAG_LONG_ARRAY);
        for (int i = 0; i < blocked.size(); i++) {
            long[] pair = blocked.getLongArray(i);
            container.blockedPairs.add(new PosPair(pair[0], pair[1]));
        }
        return container;
    }

    /**
     * Two adjacent positions, in a fixed order.
     */
    private record PosPair(long a, long b) {
        static PosPair of(BlockPos x, BlockPos y) {
            long a = x.asLong();
            long b = y.asLong();
            return a < b ? new PosPair(a, b) : new PosPair(b, a);
        }

        boolean contains(BlockPos pos) {
            long p = pos.asLong();
            return this.a == p || this.b == p;
        }
    }
}
