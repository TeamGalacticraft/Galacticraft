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

import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import dev.galacticraft.mod.Constant;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A grid of ducts in a level, stored as a graph.
 * Only the ducts at ends, corners, junctions and next to blocks the grid connects to are nodes.
 * Straight runs of ducts between them are a single edge.
 * <p>
 * Parts of a grid can be in unloaded chunks. The graph still holds them, but they are skipped when the grid ticks.
 * <p>
 * Ported from ThermalDynamics' {@code Grid} (by covers1624).
 * Change the grid only through this class and {@link GridContainer}.
 */
public abstract class Grid<G extends Grid<G, N>, N extends GridNode<G>> {
    /**
     * Whether grid debugging is on: checking the invariants, logging and showing the grids to players. Turned on by the JVM flag {@code -ea}.
     */
    public static final boolean DEBUG = Grid.class.desiredAssertionStatus();

    private static final String NODES = "Nodes";
    private static final String POS = "Pos";
    private static final String EDGES = "Edges";
    private static final String UPDATEABLE = "Updateable";

    /**
     * The nodes of this grid and the edges between them.
     */
    public final MutableGraph<N> nodeGraph = GraphBuilder.undirected().nodeOrder(ElementOrder.<N>unordered()).build();
    /**
     * The same nodes, by position.
     */
    protected final Map<BlockPos, N> nodes = new Object2ObjectRBTreeMap<>();
    /**
     * The same nodes, by chunk.
     */
    protected final Long2ObjectMap<List<N>> nodesPerChunk = new Long2ObjectRBTreeMap<>();
    protected final LongSet loadedChunks = new LongOpenHashSet();
    protected final Set<BlockPos> updatableHosts = new HashSet<>();
    protected final GridType<G> type;
    protected final UUID id;
    protected final ServerLevel level;
    protected boolean isLoaded;
    private boolean removed;

    protected Grid(GridType<G> type, UUID id, ServerLevel level) {
        this.type = type;
        this.id = id;
        this.level = level;
    }

    public abstract N newNode();

    public abstract void tick();

    /**
     * Called when nodes or edges were added or removed.
     */
    public void onModified() {
        if (DEBUG) {
            this.checkInvariant();
        }
    }

    /**
     * Called when the given grid was merged into this one, to merge its contents.
     */
    public abstract void onMerge(G from);

    /**
     * Called when this grid was split into the given grids, to divide its contents.
     * This grid is removed afterwards.
     */
    public abstract void onSplit(List<G> others);

    /**
     * {@return whether this grid can connect to the block at the given position, on the given side of that block}
     * Other ducts are never connected this way.
     */
    public abstract boolean canConnectOnSide(BlockPos pos, Direction side);

    /**
     * {@return whether this grid can connect to any block next to the given position}
     * A side in an unloaded chunk counts as connectable, so nodes next to it are kept until it can be checked.
     */
    public boolean canConnectExternally(BlockPos pos) {
        for (Direction dir : Constant.Misc.DIRECTIONS) {
            BlockPos neighbor = pos.relative(dir);
            if (!this.level.isLoaded(neighbor) || this.canConnectOnSide(neighbor, dir.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    public void onGridHostAdded(Duct<?, ?> duct) {
        if (duct instanceof GridHostUpdateable updateable) {
            this.updatableHosts.add(duct.getHostPos());
            updateable.update();
        }
    }

    public void onGridHostRemoved(Duct<?, ?> duct) {
        if (duct instanceof GridHostUpdateable) {
            this.updatableHosts.remove(duct.getHostPos());
        }
    }

    protected void updateHosts() {
        for (BlockPos pos : this.updatableHosts) {
            if (this.level.isLoaded(pos) && this.level.getBlockEntity(pos) instanceof GridHostUpdateable host) {
                host.update();
            }
        }
    }

    // returns true if this grid became loaded
    public boolean onChunkLoad(ChunkAccess chunk) {
        long pos = chunk.getPos().toLong();
        List<N> nodes = this.nodesPerChunk.get(pos);
        if (nodes == null || nodes.isEmpty() || !this.loadedChunks.add(pos)) {
            return false;
        }

        for (N node : nodes) {
            node.setLoaded(true);
        }
        boolean becameLoaded = !this.isLoaded;
        this.isLoaded = true;
        return becameLoaded;
    }

    // returns true if this grid became unloaded
    public boolean onChunkUnload(ChunkAccess chunk) {
        long pos = chunk.getPos().toLong();
        List<N> nodes = this.nodesPerChunk.get(pos);
        if (nodes == null || nodes.isEmpty() || !this.loadedChunks.remove(pos)) {
            return false;
        }

        for (N node : nodes) {
            node.setLoaded(false);
        }
        boolean becameUnloaded = this.loadedChunks.isEmpty();
        if (becameUnloaded) {
            this.isLoaded = false;
        }
        return becameUnloaded;
    }

    /**
     * Marks the chunks of this grid that are loaded right now, for grids that were read from disk.
     */
    void syncLoadedChunks() {
        for (Long2ObjectMap.Entry<List<N>> entry : this.nodesPerChunk.long2ObjectEntrySet()) {
            long chunk = entry.getLongKey();
            if (entry.getValue().isEmpty()) continue;
            boolean loaded = this.level.getChunkSource().getChunkNow(ChunkPos.getX(chunk), ChunkPos.getZ(chunk)) != null;
            if (loaded) {
                this.loadedChunks.add(chunk);
            } else {
                this.loadedChunks.remove(chunk);
            }
            for (N node : entry.getValue()) {
                node.setLoaded(loaded);
            }
        }
        this.isLoaded = !this.loadedChunks.isEmpty();
    }

    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        ListTag nodes = new ListTag();
        for (N node : this.nodeGraph.nodes()) {
            CompoundTag nodeTag = node.save();
            nodeTag.putLong(POS, node.getPos().asLong());
            nodes.add(nodeTag);
        }
        tag.put(NODES, nodes);

        ListTag edges = new ListTag();
        for (EndpointPair<N> edge : this.nodeGraph.edges()) {
            edges.add(new LongArrayTag(new long[]{edge.nodeU().getPos().asLong(), edge.nodeV().getPos().asLong()}));
        }
        tag.put(EDGES, edges);

        ListTag updateable = new ListTag();
        for (BlockPos pos : this.updatableHosts) {
            updateable.add(LongTag.valueOf(pos.asLong()));
        }
        tag.put(UPDATEABLE, updateable);
        return tag;
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag nodes = tag.getList(NODES, Tag.TAG_COMPOUND);
        for (int i = 0; i < nodes.size(); i++) {
            CompoundTag nodeTag = nodes.getCompound(i);
            N node = this.newNode(BlockPos.of(nodeTag.getLong(POS)), false);
            node.load(nodeTag);
        }

        ListTag edges = tag.getList(EDGES, Tag.TAG_LONG_ARRAY);
        for (int i = 0; i < edges.size(); i++) {
            long[] edge = edges.getLongArray(i);
            N u = this.nodes.get(BlockPos.of(edge[0]));
            N v = this.nodes.get(BlockPos.of(edge[1]));
            if (u != null && v != null) {
                this.nodeGraph.putEdge(u, v);
            }
        }

        ListTag updateable = tag.getList(UPDATEABLE, Tag.TAG_LONG);
        for (Tag pos : updateable) {
            this.updatableHosts.add(BlockPos.of(((LongTag) pos).getAsLong()));
        }
    }

    public final N newNode(BlockPos pos) {
        return this.newNode(pos, this.level.isLoaded(pos));
    }

    public final N newNode(BlockPos pos, boolean load) {
        assert !this.nodes.containsKey(pos);

        N node = this.newNode();
        node.setPos(pos);
        this.nodes.put(pos, node);
        this.nodeGraph.addNode(node);
        this.getNodesForChunk(pos).add(node);

        if (load) {
            node.setLoaded(true);
            this.loadedChunks.add(asChunkLong(pos));
            this.isLoaded = true;
        }
        this.onNodeAdded(node);
        return node;
    }

    /**
     * Called when a node was created or moved into this grid.
     */
    protected void onNodeAdded(N node) {
    }

    public final void removeNode(N node) {
        boolean removed = this.nodeGraph.removeNode(node);
        assert removed;
        removed = this.getNodesForChunk(node.getPos()).remove(node);
        assert removed;
        removed = this.nodes.remove(node.getPos(), node);
        assert removed;
    }

    public final void insertExistingNode(N node) {
        N existing = this.nodes.get(node.getPos());
        if (existing == node) return;
        assert existing == null;

        this.nodeGraph.addNode(node);
        this.getNodesForChunk(node.getPos()).add(node);
        this.nodes.put(node.getPos(), node);
        if (node.isLoaded()) {
            this.loadedChunks.add(asChunkLong(node.getPos()));
            this.isLoaded = true;
        }
        this.onNodeAdded(node);
    }

    /**
     * Moves all nodes and edges of the given grid into this one.
     */
    @SuppressWarnings("unchecked")
    public final void mergeFrom(G other) {
        assert this.type == other.type;
        if (DEBUG) {
            Constant.LOGGER.info("Merging {} nodes from grid {} into {}.", other.nodeGraph.nodes().size(), other.id, this.id);
        }

        // Collect every duct of the other grid, so they can be told about their new grid
        Set<BlockPos> positions = new HashSet<>();
        for (EndpointPair<N> edge : other.nodeGraph.edges()) {
            positions.add(edge.nodeU().getPos());
            positions.add(edge.nodeV().getPos());
            GridHelper.positionsBetween(edge.nodeU().getPos(), edge.nodeV().getPos()).forEach(pos -> positions.add(pos.immutable()));
        }
        for (N node : other.nodeGraph.nodes()) {
            positions.add(node.getPos());
        }

        for (N node : other.nodeGraph.nodes()) {
            this.insertExistingNode(node);
        }
        for (EndpointPair<N> edge : other.nodeGraph.edges()) {
            this.nodeGraph.putEdge(edge.nodeU(), edge.nodeV());
        }

        this.updateGridHosts(positions, (G) this);
        for (N node : other.nodeGraph.nodes()) {
            node.setGrid((G) this);
            node.onGridChange(other);
        }
        this.updatableHosts.addAll(other.updatableHosts);
        this.onMerge(other);
    }

    /**
     * Splits this grid into one grid per given set of nodes.
     * Each set must be a connected part of this grid.
     */
    @SuppressWarnings("unchecked")
    public final List<G> splitInto(List<Set<N>> parts, GridContainer container) {
        List<G> newGrids = new ArrayList<>();
        for (Set<N> part : parts) {
            G newGrid = container.createAndAddGrid(container.nextUUID(), this.type, true);
            Set<BlockPos> positions = new HashSet<>();

            for (N node : part) {
                positions.add(node.getPos());
                newGrid.insertExistingNode(node);
                for (N adj : this.nodeGraph.adjacentNodes(node)) {
                    newGrid.insertExistingNode(adj);
                    GridHelper.positionsBetween(node.getPos(), adj.getPos()).forEach(pos -> positions.add(pos.immutable()));
                    newGrid.nodeGraph.putEdge(node, adj);
                }
            }

            this.updateGridHosts(positions, newGrid);
            for (BlockPos pos : positions) {
                if (this.updatableHosts.remove(pos)) {
                    newGrid.updatableHosts.add(pos);
                }
            }
            for (N node : part) {
                node.setGrid(newGrid);
                node.onGridChange((G) this);
            }
            newGrids.add(newGrid);
        }

        this.onSplit(newGrids);
        return newGrids;
    }

    /**
     * Inserts a node in the middle of the edge the given position lies on.
     * If there already is a node at the position, that node is returned.
     */
    public final N getNodeOrSplitEdgeAndInsertNode(BlockPos pos) {
        N existing = this.nodes.get(pos);
        if (existing != null) return existing;

        EndpointPair<N> edge = this.findEdge(pos);
        if (edge == null) {
            throw new IllegalStateException("Position " + pos + " is neither a node nor on an edge of grid " + this.id);
        }

        N a = edge.nodeU();
        N b = edge.nodeV();
        N middle = this.newNode(pos);
        this.nodeGraph.putEdge(middle, a);
        this.nodeGraph.putEdge(middle, b);
        this.nodeGraph.removeEdge(a, b);
        if (DEBUG) {
            Constant.LOGGER.info("Node insertion. A: {}, B: {}, middle: {}", a.getPos(), b.getPos(), middle.getPos());
        }
        return middle;
    }

    public final @Nullable EndpointPair<N> findEdge(BlockPos pos) {
        for (EndpointPair<N> edge : this.nodeGraph.edges()) {
            if (isOnEdge(pos, edge)) {
                return edge;
            }
        }
        return null;
    }

    /**
     * {@return whether the ducts at the two given positions are connected directly, by an edge or a run of ducts}
     */
    public final boolean isConnectedTo(BlockPos a, BlockPos b) {
        N aNode = this.nodes.get(a);
        N bNode = this.nodes.get(b);
        if (aNode != null && bNode != null) {
            return this.nodeGraph.hasEdgeConnecting(aNode, bNode);
        }
        if (aNode == null && bNode == null) {
            EndpointPair<N> edge = this.findEdge(a);
            return edge != null && isOnEdge(b, edge);
        }
        N node = aNode != null ? aNode : bNode;
        BlockPos pos = aNode != null ? b : a;
        for (EndpointPair<N> edge : this.nodeGraph.incidentEdges(node)) {
            if (isOnEdge(pos, edge)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@return every duct position of this grid: the nodes and the ducts on the edges}
     */
    public final Set<BlockPos> getAllPositions() {
        Set<BlockPos> positions = new HashSet<>(this.nodes.keySet());
        for (EndpointPair<N> edge : this.nodeGraph.edges()) {
            GridHelper.positionsBetween(edge.nodeU().getPos(), edge.nodeV().getPos()).forEach(pos -> positions.add(pos.immutable()));
        }
        return positions;
    }

    private void updateGridHosts(Set<BlockPos> positions, G grid) {
        for (BlockPos pos : positions) {
            if (!this.level.isLoaded(pos)) continue;
            if (this.level.getBlockEntity(pos) instanceof Duct<?, ?> duct && duct.getGridType() == this.type) {
                @SuppressWarnings("unchecked") Duct<G, N> typed = (Duct<G, N>) duct;
                typed.setGrid(grid);
            } else {
                Constant.LOGGER.error("Grid position without a duct at {}. Chunk modified externally?", pos);
            }
        }
    }

    public void checkInvariant() {
        if (!DEBUG) {
            return;
        }
        GridContainer container = GridContainer.get(this.level);
        for (EndpointPair<N> edge : this.nodeGraph.edges()) {
            assert GridHelper.isOnSameAxis(edge.nodeU().getPos(), edge.nodeV().getPos()) : "Edge not on one axis " + edge;
            for (BlockPos pos : GridHelper.positionsBetween(edge.nodeU().getPos(), edge.nodeV().getPos())) {
                this.checkPos(pos, container);
            }
        }
        for (N node : this.nodeGraph.nodes()) {
            long chunk = asChunkLong(node.getPos());
            this.checkPos(node.getPos(), container);
            assert node.grid == this;
            assert this.nodes.get(node.getPos()) == node;
            assert this.nodesPerChunk.get(chunk) != null && this.nodesPerChunk.get(chunk).contains(node);
        }
    }

    private void checkPos(BlockPos pos, GridContainer container) {
        assert container.getGrid(pos) == this : "Lookup at " + pos + " is not grid " + this.id;
        if (!this.level.isLoaded(pos)) {
            return;
        }
        Duct<?, ?> duct = GridHelper.getDuct(this.level, pos);
        assert duct != null : "No duct at " + pos;
    }

    public final UUID getId() {
        return this.id;
    }

    public final ServerLevel getLevel() {
        return this.level;
    }

    public final GridType<G> getType() {
        return this.type;
    }

    public final Map<BlockPos, N> getNodes() {
        return this.nodes;
    }

    public final boolean isLoaded() {
        return this.isLoaded;
    }

    public final boolean isRemoved() {
        return this.removed;
    }

    final void markRemoved() {
        this.removed = true;
    }

    private static boolean isOnEdge(BlockPos pos, EndpointPair<?> edge) {
        return GridHelper.isOnEdge(pos, ((GridNode<?>) edge.nodeU()).getPos(), ((GridNode<?>) edge.nodeV()).getPos());
    }

    private List<N> getNodesForChunk(BlockPos pos) {
        return this.nodesPerChunk.computeIfAbsent(asChunkLong(pos), e -> new LinkedList<>());
    }

    protected static long asChunkLong(BlockPos pos) {
        return ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + this.id + ", nodes=" + this.nodes.size() + ", edges=" + this.nodeGraph.edges().size() + "}";
    }
}
