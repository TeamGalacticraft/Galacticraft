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
 *
 */

package com.hrznstudio.galacticraft.api.pipe;

import com.google.common.graph.Graphs;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.FluidPipe;
import com.hrznstudio.galacticraft.block.special.fluidpipe.FluidPipeBlockEntity;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.fluid.TankComponent;
import io.github.fablabsmc.fablabs.api.fluidvolume.v1.FluidVolume;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class PipeNetwork {
    @SuppressWarnings("UnstableApiUsage")
    private final MutableValueGraph<BlockPos, PipeConnectionType> graph = ValueGraphBuilder.directed().allowsSelfLoops(false).build();
    private final Map<BlockPos, TankComponent> capList = new LinkedHashMap<>();
    private boolean invalid = false;
    private long modCount = 0;

    private final ServerWorld world;

    public PipeNetwork(BlockPos pos, ServerWorld world, FluidPipeBlockEntity be) {
        this.world = world;
        addPipe(pos.toImmutable(), be);
    }

    private PipeNetwork(Set<BlockPos> set, ServerWorld world) {
        this.world = world;
        for (BlockPos pos : set) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof FluidPipeBlockEntity) {
                this.addPipe(pos, (FluidPipeBlockEntity) entity);
            }
        }
    }

    public void addPipe(BlockPos pos, @Nullable FluidPipeBlockEntity pipe) {
        pos = pos.toImmutable();
        FluidPipeBlockEntity be = pipe == null ? (FluidPipeBlockEntity)world.getBlockEntity(pos) : pipe;
        be.setNetwork(this);
        node(pos);
        for (Direction direction : Direction.values()) {
            BlockPos cap = pos.offset(direction);
            BlockState state = world.getBlockState(cap);
            if (state.getBlock() instanceof FluidPipe) {
                FluidPipeBlockEntity entity = (FluidPipeBlockEntity)world.getBlockEntity(cap);
                if (entity.getNetwork() != this) {
                    this.addPipe(cap, entity);
                } else {
                    edge(pos, cap, PipeConnectionType.PIPE);
                    edge(cap, pos, PipeConnectionType.PIPE);
                }
            } else {
                TankComponent component = ((BlockComponentProvider) state.getBlock()).getComponent(world, cap, UniversalComponents.TANK_COMPONENT, direction.getOpposite());
                if (component != null) {
                    if (component.canInsert(0)) {
                        node(cap);
                        if (component.canExtract(0)) {
                            edge(pos, cap, PipeConnectionType.FLUID_IO);
                        } else {
                            edge(pos, cap, PipeConnectionType.FLUID_INPUT);
                        }
                        this.capList.put(cap, component);
                    } else if (component.canExtract(0)) {
                        node(cap);
                        edge(pos, cap, PipeConnectionType.FLUID_OUTPUT);
                        this.capList.put(cap, component);
                    }
                }
            }
        }
    }
    
    public void edge(BlockPos pos, BlockPos pos2, PipeConnectionType type) {
        if (this.graph.putEdgeValue(pos, pos2, type) != type) {
            this.modCount++;
        }
    }

    private void invalidate() {
        Galacticraft.logger.debug("Invalidated network.");
        this.invalid = true;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void removePipe(BlockPos blockPos) {
        Set<BlockPos> set = graph.adjacentNodes(blockPos);
        Deque<BlockPos> pipes = new LinkedList<>();
        for (BlockPos pos : set) {
            PipeConnectionType type = graph.edgeValue(blockPos, pos);
            if (type == PipeConnectionType.PIPE) {
                pipes.push(pos);
            } else if (type != null && type != PipeConnectionType.NONE) {
                graph.removeEdge(blockPos, pos);
                if (graph.adjacentNodes(pos).size() == 0) {
                    remove(pos);
                }
            } else {
                Galacticraft.logger.debug("Node claimed to be adjacent to other node, but edge was empty or not found!");
            }
        }
        this.remove(blockPos);
        pipes.add(BlockPos.ORIGIN);

        boolean stillConnected = true;
        boolean changed = false;
        if (pipes.size() >= 3) { // if its connected to one pipe, this pipe being removed is not vital to the graph staying connected
            while (pipes.size() > 2) {
                BlockPos pos = pipes.pop();
                BlockPos pos1 = pipes.pop();
                if (pos == BlockPos.ORIGIN) {
                    if (changed) {
                        pos = pipes.pop();
                        pipes.addLast(BlockPos.ORIGIN);
                        changed = false;
                    } else {
                        pipes.addLast(pos);
                        pipes.addLast(pos1);
                        stillConnected = false;
                        break;
                    }
                } else if (pos1 == BlockPos.ORIGIN) {
                    if (changed) {
                        pos1 = pipes.pop();
                        pipes.addLast(BlockPos.ORIGIN);
                        changed = false;
                    } else {
                        pipes.add(pos);
                        pipes.add(pos1);
                        stillConnected = false;
                        break;
                    }
                }
                pipes.addLast(pos1);

                if (!checkConnected(pos, pos1)) {
                    pipes.addLast(pos);
                } else {
                    changed = true;
                }
            }
        }

        pipes.remove(BlockPos.ORIGIN);

        if (!stillConnected) {
            if (pipes.size() < 2) {
                throw new RuntimeException("marcus8448 did a bad");
            }
            this.invalidate();
            while (!pipes.isEmpty()) {
                new PipeNetwork(Graphs.reachableNodes(this.graph, pipes.pop()), this.world);
            }
        }
    }
    private void remove(BlockPos pos) {
        this.graph.removeNode(pos);
        this.capList.remove(pos);
        this.modCount++;
    }

    public boolean checkConnected(BlockPos from, BlockPos to) {
        if (!graph.nodes().contains(from) || !graph.nodes().contains(to)) throw new RuntimeException();
        Set<BlockPos> visitedNodes = new LinkedHashSet<>();
        Queue<BlockPos> queuedNodes = new ArrayDeque<>();
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



    @SuppressWarnings("UnstableApiUsage")
    private void node(BlockPos value) {
        BlockPos immutable = value.toImmutable();
        graph.addNode(immutable);
        this.modCount++;
    }

    public void updateConnections(BlockPos nextToUpdated, BlockPos updated) {
        BlockState blockState = world.getBlockState(updated);

        if (this.graph.nodes().contains(updated)) {
            Set<BlockPos> conn = this.graph.adjacentNodes(updated);
            boolean isPipe = false;
            for (BlockPos pos1 : conn) {
                if (this.graph.edgeValue(updated, pos1) == PipeConnectionType.PIPE) {
                    isPipe = true;
                    break;
                }
            }
            if (isPipe) {
                if (!(blockState.getBlock() instanceof FluidPipe)) {
                    removePipe(updated);
                } else {
                    this.graph.putEdgeValue(nextToUpdated, updated, PipeConnectionType.PIPE);
                    this.graph.putEdgeValue(updated, nextToUpdated, PipeConnectionType.PIPE);
                }
            } else {
                test(updated, blockState);
            }
        } else {
            if (blockState.getBlock() instanceof FluidPipe) {
                if (world.getBlockEntity(updated) instanceof FluidPipeBlockEntity) {
                    if (((FluidPipeBlockEntity) world.getBlockEntity(updated)).getNetwork() != this)
                        throw new RuntimeException();
                }
            } else if (!blockState.isAir()) {
                test(updated, blockState);
            }
        }
    }

    protected void test(BlockPos updated, BlockState blockState) {
        remove(updated);
        capList.remove(updated);
        for (Direction direction : Direction.values()) {
            BlockPos from = updated.offset(direction);
            BlockEntity entity = world.getBlockEntity(from);
            if (entity instanceof FluidPipeBlockEntity) {
                if (((FluidPipeBlockEntity) entity).getNetwork() == this) {
                    TankComponent component = ((BlockComponentProvider)blockState.getBlock()).getComponent(world, updated, UniversalComponents.TANK_COMPONENT, direction);
                    if (component != null) {
                        PipeConnectionType type = null;
                        if (component.canInsert(0) && component.canExtract(0)) {
                            type = PipeConnectionType.FLUID_IO;
                        } else if (component.canInsert(0)) {
                            type = PipeConnectionType.FLUID_INPUT;
                        } else if (component.canExtract(0)) {
                            type = PipeConnectionType.FLUID_OUTPUT;
                        } else {
                            continue;
                        }
                        this.capList.putIfAbsent(updated, component);
                        this.node(updated);
                        this.edge(from, updated, type);
                    }
                }
            }
        }
    }

    public long getModCount() {
        return modCount;
    }

    @Override
    public String toString() {
        return "PipeNetwork{" +
                "graph=" + graph +
                ", invalid=" + invalid +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PipeNetwork network = (PipeNetwork) o;
        return isInvalid() == network.isInvalid() &&
                Objects.equals(graph, network.graph) &&
                Objects.equals(world, network.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graph, isInvalid(), world);
    }

    public FluidPipeBlockEntity.FluidData spreadFluid(BlockPos pos, FluidVolume amount, ActionType actionType) {
        if (!graph.nodes().contains(pos)) throw new RuntimeException();

        return successor(pos, Util.make(new LinkedHashSet<>(), (l) -> l.add(pos)), new LinkedList<>(), amount, actionType);
    }

    public FluidPipeBlockEntity.FluidData successor(BlockPos pos, LinkedHashSet<BlockPos> visited, LinkedList<BlockPos> steps, FluidVolume amount, ActionType actionType) {
        steps.push(pos);
        List<BlockPos> other = new LinkedList<>();
        for (BlockPos successor : graph.successors(pos)) {
            if (visited.add(successor)) {
                TankComponent component = this.capList.get(successor);
                if (component != null) {
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
}
