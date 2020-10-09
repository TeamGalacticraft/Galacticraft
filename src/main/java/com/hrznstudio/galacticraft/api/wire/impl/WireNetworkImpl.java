package com.hrznstudio.galacticraft.api.wire.impl;

import com.google.common.graph.Graphs;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import com.hrznstudio.galacticraft.api.wire.Wire;
import com.hrznstudio.galacticraft.api.wire.WireConnectionType;
import com.hrznstudio.galacticraft.api.wire.WireNetwork;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.CapacitorComponent;
import io.github.cottonmc.component.energy.CapacitorComponentHelper;
import io.github.cottonmc.component.energy.type.EnergyType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class WireNetworkImpl implements WireNetwork {
    private final MutableValueGraph<BlockPos, WireConnectionType> graph;
    private final ServerWorld world;

    public WireNetworkImpl(ServerWorld world) {
        this(ValueGraphBuilder.directed().allowsSelfLoops(false).build(), world);
    }

    private WireNetworkImpl(MutableValueGraph<BlockPos, WireConnectionType> graph, ServerWorld world) {
        this.graph = graph;
        this.world = world;
    }

    @Override
    public void addWire(@NotNull BlockPos pos, @Nullable Wire wire) {
        node(pos);
        if (wire == null) {
            wire = (Wire) world.getBlockEntity(pos);
            if (wire == null) throw new RuntimeException("Tried to add wire that didn't exist!");
        }
        wire.setNetwork(this);
        for (Direction direction : Direction.values()) {
            BlockPos conn = pos.offset(direction);
            BlockEntity entity = world.getBlockEntity(conn);
            WireConnectionType type = wire.getConnection(direction, entity);
            if (type == WireConnectionType.WIRE) {
                if (((Wire) entity).getNetwork() != this) {
                    addWire(conn, (Wire)entity);
                }
                edge(pos, conn, WireConnectionType.WIRE);
                edge(conn, pos, WireConnectionType.WIRE);
            } else if (type != WireConnectionType.NONE) {
                node(conn);
                edge(pos, conn, type);
            } else {
                removeEdge(pos, conn);
                removeEdge(conn, pos);
            }
        }
    }

    @Override
    public void removeWire(@NotNull BlockPos pos) {
        if (contains(pos)) {
            Deque<BlockPos> wires = new LinkedList<>();
            for (Direction direction : Direction.values()) {
                BlockPos conn = pos.offset(direction);
                WireConnectionType type = getConnection(pos, conn);
                if (type != WireConnectionType.NONE) {
                    if (type == WireConnectionType.WIRE) {
                        wires.add(conn);
                        removeEdge(conn, pos);
                    }
                    removeEdge(pos, conn, type != WireConnectionType.WIRE);
                }
            }

            remove(pos);

            BlockPos c = null;
            while (wires.size() >= 2) {
                BlockPos last = wires.removeLast();
                if (!canReach(last, wires.getLast())) {
                    wires.addFirst(last);
                    if (c == last) {
                        break;
                    }
                    if (c == null) c = last;
                } else {
                    c = null;
                }
            }

            if (c != null) {
                while (!wires.isEmpty()) {
                    MutableValueGraph<BlockPos, WireConnectionType> graph = Graphs.inducedSubgraph(this.graph, Graphs.reachableNodes(this.graph, wires.removeLast()));
                    WireNetworkImpl network = new WireNetworkImpl(graph, this.world);
                    BlockEntity blockEntity;
                    for (BlockPos p : graph.nodes()) {
                        blockEntity = world.getBlockEntity(p);
                        if (blockEntity instanceof Wire) {
                            ((Wire) blockEntity).setNetwork(network);
                        }
                    }
                }
            }
        } else {
            throw new RuntimeException("Tried to remove wire that doesn't exist!");
        }
    }

    @Override
    public void updateConnections(@NotNull BlockPos theWireThatReceivedANeighborUpdate, @NotNull BlockPos theBlockThatWasActuallyChanged) {
        if (world.getBlockEntity(theBlockThatWasActuallyChanged) instanceof Wire) return;
        //wires should call #removeWire before all the other blocks get updated
        //so we just need to check for machine block changes

        removeEdge(theWireThatReceivedANeighborUpdate, theBlockThatWasActuallyChanged, true);
        BlockPos poss = theBlockThatWasActuallyChanged.subtract(theWireThatReceivedANeighborUpdate);
        Direction opposite = Direction.fromVector(poss.getX(), poss.getY(), poss.getZ()).getOpposite();
        CapacitorComponent component = CapacitorComponentHelper.INSTANCE.getComponent(world, theBlockThatWasActuallyChanged, opposite);
        if (component != null) {
            if (component.canInsertEnergy() && component.canExtractEnergy()) {
                node(theBlockThatWasActuallyChanged);
                edge(theWireThatReceivedANeighborUpdate, theBlockThatWasActuallyChanged, WireConnectionType.ENERGY_IO);
            } else if (component.canInsertEnergy()) {
                node(theBlockThatWasActuallyChanged);
                edge(theWireThatReceivedANeighborUpdate, theBlockThatWasActuallyChanged, WireConnectionType.ENERGY_INPUT);
            } else if (component.canExtractEnergy()) {
                node(theBlockThatWasActuallyChanged);
                edge(theWireThatReceivedANeighborUpdate, theBlockThatWasActuallyChanged, WireConnectionType.ENERGY_OUTPUT);
            }
        }
    }

    @Override
    public @NotNull WireConnectionType getConnection(BlockPos from, BlockPos to) {
        return this.graph.edgeValueOrDefault(from, to, WireConnectionType.NONE);
    }

    @Override
    public int insertEnergy(@NotNull BlockPos fromWire, @Nullable BlockPos fromBlock, @NotNull EnergyType energyType, int amount, @NotNull ActionType type) {
        if (!graph.nodes().contains(fromWire)) throw new RuntimeException("Inserted energy from non-existent wire?!");
        if (amount <= 0) return amount;
        Set<BlockPos> visitedNodes = new HashSet<>();
        Queue<BlockPos> queuedNodes = new LinkedList<>();
        visitedNodes.add(fromWire);
        queuedNodes.add(fromWire);
        while (!queuedNodes.isEmpty()) {
            BlockPos currentNode = queuedNodes.remove();
            for (BlockPos successor : graph.successors(currentNode)) {
                if (visitedNodes.add(successor)) {
                    if (!(world.getBlockEntity(successor) instanceof Wire)) {
                        BlockPos poss = successor.subtract(currentNode);
                        Direction opposite = Direction.fromVector(poss.getX(), poss.getY(), poss.getZ()).getOpposite();
                        CapacitorComponent component = CapacitorComponentHelper.INSTANCE.getComponent(world, successor, opposite);
                        if (component != null && component.canInsertEnergy()) {
                            amount = component.insertEnergy(energyType, amount, type);
                            if (amount == 0) {
                                return 0;
                            }
                        }
                    }
                    queuedNodes.add(successor);
                }
            }
        }
        return amount;
    }

    @Override
    public @NotNull Map<Direction, @NotNull WireConnectionType> getAdjacent(BlockPos from) {
        Map<Direction, WireConnectionType> map = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.values()) {
            map.put(direction, getConnection(from, from.offset(direction)));
        }

        return map;
    }

    @Override
    public boolean canReach(@NotNull BlockPos from, @NotNull BlockPos to) {
        if (!graph.nodes().contains(from) || !graph.nodes().contains(to)) throw new RuntimeException();
        Set<BlockPos> visitedNodes = new HashSet<>();
        Queue<BlockPos> queuedNodes = new LinkedList<>();
        visitedNodes.add(from);
        queuedNodes.add(from);
        // BFS traversal
        while (!queuedNodes.isEmpty()) {
            BlockPos currentNode = queuedNodes.remove();
            for (BlockPos successor : graph.successors(currentNode)) {
                if (successor.equals(to)) return true;
                if (visitedNodes.add(successor)) {
                    queuedNodes.add(successor);
                }
            }
        }
        return false;
    }

    private void node(BlockPos pos) {
        this.graph.addNode(pos);
    }

    private void remove(BlockPos pos) {
        this.graph.removeNode(pos);
    }

    private void edge(BlockPos from, BlockPos to, WireConnectionType value) {
        this.graph.putEdgeValue(from, to, value);
    }

    private void removeEdge(BlockPos from, BlockPos to) {
        removeEdge(from, to, false);
    }

    private void removeEdge(BlockPos from, BlockPos to, boolean removeUnused) {
        this.graph.removeEdge(from, to);
        if (removeUnused) {
            Collection<WireConnectionType> connections = getAdjacent(to).values();
            for (WireConnectionType type : connections) {
                if (type != WireConnectionType.NONE) remove(to);
            }
        }
    }

    private boolean contains(BlockPos pos) {
        return this.graph.nodes().contains(pos);
    }

    @Override
    public String toString() {
        return "WireNetworkImpl{" +
                "graph=" + graph +
                '}';
    }
}
