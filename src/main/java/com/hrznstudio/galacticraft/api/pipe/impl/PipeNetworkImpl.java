/*
 * Copyright (c) 2020 HRZN LTD
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

package com.hrznstudio.galacticraft.api.pipe.impl;

import com.google.common.graph.Graphs;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import com.hrznstudio.galacticraft.api.pipe.Pipe;
import com.hrznstudio.galacticraft.api.pipe.PipeConnectionType;
import com.hrznstudio.galacticraft.api.pipe.PipeNetwork;
import com.hrznstudio.galacticraft.block.special.fluidpipe.FluidPipeBlockEntity;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.api.ComponentHelper;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class PipeNetworkImpl implements PipeNetwork {
    private final MutableValueGraph<BlockPos, PipeConnectionType> graph;
    private final ServerWorld world;

    public PipeNetworkImpl(ServerWorld world) {
        this(ValueGraphBuilder.directed().allowsSelfLoops(false).build(), world);
    }

    private PipeNetworkImpl(MutableValueGraph<BlockPos, PipeConnectionType> graph, ServerWorld world) {
        this.graph = graph;
        this.world = world;
    }

    @Override
    public void addPipe(@NotNull BlockPos pos, @Nullable Pipe pipe) {
        node(pos);
        if (pipe == null) {
            pipe = (Pipe) world.getBlockEntity(pos);
            if (pipe == null) throw new RuntimeException("Tried to add a pipe that didn't exist!");
        }
        pipe.setNetwork(this);
        for (Direction direction : Direction.values()) {
            BlockPos conn = pos.offset(direction);
            BlockEntity entity = world.getBlockEntity(conn);
            PipeConnectionType type = pipe.getConnection(direction, entity);
            if (type == PipeConnectionType.PIPE) {
                if (((Pipe) entity).getNetwork() != this) {
                    addPipe(conn, (Pipe)entity);
                }
                edge(pos, conn, PipeConnectionType.PIPE);
                edge(conn, pos, PipeConnectionType.PIPE);
            } else if (type != PipeConnectionType.NONE) {
                node(conn);
                edge(pos, conn, type);
            } else {
                removeEdge(pos, conn);
                removeEdge(conn, pos);
            }
        }
    }

    @Override
    public void removePipe(@NotNull BlockPos pos) {
        if (contains(pos)) {
            Deque<BlockPos> pipes = new LinkedList<>();
            for (Direction direction : Direction.values()) {
                BlockPos conn = pos.offset(direction);
                PipeConnectionType type = getConnection(pos, conn);
                if (type != PipeConnectionType.NONE) {
                    if (type == PipeConnectionType.PIPE) {
                        pipes.add(conn);
                        removeEdge(conn, pos);
                    }
                    removeEdge(pos, conn, type != PipeConnectionType.PIPE);
                }
            }

            remove(pos);

            BlockPos c = null;
            while (pipes.size() >= 2) {
                BlockPos last = pipes.removeLast();
                if (!canReach(last, pipes.getLast())) {
                    pipes.addFirst(last);
                    if (c == last) {
                        break;
                    }
                    if (c == null) c = last;
                } else {
                    c = null;
                }
            }

            if (c != null) {
                while (!pipes.isEmpty()) {
                    MutableValueGraph<BlockPos, PipeConnectionType> graph = Graphs.inducedSubgraph(this.graph, Graphs.reachableNodes(this.graph, pipes.removeLast()));
                    PipeNetworkImpl network = new PipeNetworkImpl(graph, this.world);
                    BlockEntity blockEntity;
                    for (BlockPos p : graph.nodes()) {
                        blockEntity = world.getBlockEntity(p);
                        if (blockEntity instanceof Pipe) {
                            ((Pipe) blockEntity).setNetwork(network);
                        }
                    }
                }
            }
        } else {
            throw new RuntimeException("Tried to remove pipe that doesn't exist!");
        }
    }

    @Override
    public void updateConnections(@NotNull BlockPos neighborPipe, @NotNull BlockPos theBlockThatWasActuallyChanged) {
        if (world.getBlockEntity(theBlockThatWasActuallyChanged) instanceof Pipe) return;
        //pipes should call #removePipe before all the other blocks get updated
        //so we just need to check for machine block changes

        removeEdge(neighborPipe, theBlockThatWasActuallyChanged, true);
        BlockPos poss = theBlockThatWasActuallyChanged.subtract(neighborPipe);
        Direction opposite = Direction.fromVector(poss.getX(), poss.getY(), poss.getZ()).getOpposite();
        TankComponent component = ComponentHelper.TANK.getComponent(world, theBlockThatWasActuallyChanged, opposite);
        boolean insert = false;
        boolean extract = false;
        if (component != null) {
            for (int i = 0; i < component.getTanks(); i++) {
                insert |= component.canInsert(i);
                extract |= component.canExtract(i);
            }
            node(theBlockThatWasActuallyChanged);
            if (insert && extract) {
                edge(neighborPipe, theBlockThatWasActuallyChanged, PipeConnectionType.FLUID_IO);
            } else if (insert) {
                edge(neighborPipe, theBlockThatWasActuallyChanged, PipeConnectionType.FLUID_INPUT);
            } else if (extract) {
                edge(neighborPipe, theBlockThatWasActuallyChanged, PipeConnectionType.FLUID_OUTPUT);
            }
        }
    }

    @Override
    public @NotNull PipeConnectionType getConnection(BlockPos from, BlockPos to) {
        return this.graph.edgeValueOrDefault(from, to, PipeConnectionType.NONE);
    }

    @Override
    public Pipe.FluidData insertFluid(@NotNull BlockPos fromPipe, @Nullable BlockPos fromBlock, @NotNull FluidVolume amount, @NotNull ActionType type) {
        if (!graph.nodes().contains(fromPipe)) throw new RuntimeException("Inserted energy from non-existent pipe?!");
        return successor(fromPipe, Util.make(new LinkedHashSet<>(), (l) -> l.add(fromPipe)), new LinkedList<>(), amount, type);
    }

    public FluidPipeBlockEntity.FluidData successor(BlockPos pos, LinkedHashSet<BlockPos> visited, LinkedList<BlockPos> steps, FluidVolume amount, ActionType actionType) {
        steps.push(pos);
        List<BlockPos> other = new LinkedList<>();
        for (BlockPos successor : graph.successors(pos)) {
            if (visited.add(successor)) {
                BlockEntity entity = world.getBlockEntity(successor);
                BlockPos poss = successor.subtract(pos);
                Direction opposite = Direction.fromVector(poss.getX(), poss.getY(), poss.getZ()).getOpposite();
                TankComponent component = ComponentHelper.TANK.getComponent(world, successor, opposite);
                if (component != null && !(entity instanceof Pipe)) {
                    if (component.canInsert(0)) {
                        FluidVolume data = component.insertFluid(amount, actionType);

                        if (!(data.getAmount().equals(amount.getAmount()))) {
                            steps.push(successor);
                            Direction direction = Direction.fromVector(pos.getX() - successor.getX(), pos.getY() - successor.getY(), pos.getZ() - successor.getZ()).getOpposite();
                            return new FluidPipeBlockEntity.FluidData(pos, steps, new FluidVolume(amount.getFluid(), amount.getAmount().subtract(data.getAmount())), direction);
                        }
                    }
                } else {
                    other.add(successor);
                }
            }
        }
        for (BlockPos successor : other) {
            FluidPipeBlockEntity.FluidData data = successor(successor, visited, steps, amount, actionType);
            if (data != null) return data;
        }
        steps.pop();
        return null;
    }

    @Override
    public @NotNull Map<Direction, @NotNull PipeConnectionType> getAdjacent(BlockPos from) {
        Map<Direction, PipeConnectionType> map = new EnumMap<>(Direction.class);
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

    private void edge(BlockPos from, BlockPos to, PipeConnectionType value) {
        this.graph.putEdgeValue(from, to, value);
    }

    private void removeEdge(BlockPos from, BlockPos to) {
        removeEdge(from, to, false);
    }

    private void removeEdge(BlockPos from, BlockPos to, boolean removeUnused) {
        this.graph.removeEdge(from, to);
        if (removeUnused) {
            Collection<PipeConnectionType> connections = getAdjacent(to).values();
            for (PipeConnectionType type : connections) {
                if (type != PipeConnectionType.NONE) remove(to);
            }
        }
    }

    private boolean contains(BlockPos pos) {
        return this.graph.nodes().contains(pos);
    }

    @Override
    public String toString() {
        return "PipeNetworkImpl{" +
                "graph=" + graph +
                '}';
    }
}
