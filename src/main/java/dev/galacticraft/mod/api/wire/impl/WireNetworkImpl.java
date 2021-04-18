/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.api.wire.impl;

import alexiil.mc.lib.attributes.Simulation;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.api.wire.WireConnectionType;
import dev.galacticraft.mod.api.wire.WireNetwork;
import dev.galacticraft.mod.energy.api.EnergyExtractable;
import dev.galacticraft.mod.energy.api.EnergyInsertable;
import dev.galacticraft.mod.energy.impl.DefaultEnergyType;
import dev.galacticraft.mod.energy.impl.EmptyEnergyExtractable;
import dev.galacticraft.mod.energy.impl.RejectingEnergyInsertable;
import dev.galacticraft.mod.util.EnergyUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
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
        for (Direction direction : Constants.Misc.DIRECTIONS) {
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
            for (Direction direction : Constants.Misc.DIRECTIONS) {
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
    public void updateConnections(@NotNull BlockPos adjacentToUpdated, @NotNull BlockPos updatedPos) {
        if (world.getBlockEntity(updatedPos) instanceof Wire) return;
        //wires should call #removeWire before all the other blocks get updated
        //so we just need to check for machine block changes

        this.removeEdge(adjacentToUpdated, updatedPos, true);
        BlockPos vector = adjacentToUpdated.subtract(updatedPos);
        Direction direction = Direction.fromVector(vector.getX(), vector.getY(), vector.getZ());
        EnergyInsertable insertable = EnergyUtils.getEnergyInsertable(world, updatedPos, direction);
        EnergyExtractable extractable = EnergyUtils.getEnergyExtractable(world, updatedPos, direction);
        if (insertable != RejectingEnergyInsertable.NULL && extractable != EmptyEnergyExtractable.NULL) {
            this.node(updatedPos);
            this.edge(adjacentToUpdated, updatedPos, WireConnectionType.ENERGY_IO);
        } else if (insertable != RejectingEnergyInsertable.NULL) {
            this.node(updatedPos);
            this.edge(adjacentToUpdated, updatedPos, WireConnectionType.ENERGY_INPUT);
        } else if (extractable != EmptyEnergyExtractable.NULL) {
            this.node(updatedPos);
            this.edge(adjacentToUpdated, updatedPos, WireConnectionType.ENERGY_OUTPUT);
        }
    }

    @Override
    public @NotNull WireConnectionType getConnection(BlockPos from, BlockPos to) {
        return this.graph.edgeValueOrDefault(from, to, WireConnectionType.NONE);
    }

    @Override
    public int insert(@NotNull BlockPos fromWire, @Nullable BlockPos fromBlock, int amount, @NotNull Simulation simulate) {
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
                        BlockPos vector = currentNode.subtract(successor);
                        Direction opposite = Direction.fromVector(vector.getX(), vector.getY(), vector.getZ());
                        EnergyInsertable handler = EnergyUtils.getEnergyInsertable(world, successor, opposite);
                        amount = handler.tryInsert(DefaultEnergyType.INSTANCE, amount, simulate);
                        if (amount == 0) {
                            return 0;
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
        for (Direction direction : Constants.Misc.DIRECTIONS) {
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
