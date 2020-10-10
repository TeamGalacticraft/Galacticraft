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

package com.hrznstudio.galacticraft.api.wire;

import com.google.common.graph.Graphs;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.WireBlock;
import com.hrznstudio.galacticraft.api.block.entity.WireBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import io.github.cottonmc.component.UniversalComponents;
import io.github.cottonmc.component.api.ActionType;
import io.github.cottonmc.component.energy.CapacitorComponent;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class WireNetwork {
    @SuppressWarnings("UnstableApiUsage")
    private final MutableValueGraph<BlockPos, WireConnectionType> graph = ValueGraphBuilder.directed().allowsSelfLoops(false).build();
    private final Map<BlockPos, CapacitorComponent> capList = new LinkedHashMap<>();
    private boolean invalid = false;

    private final ServerWorld world;

    public WireNetwork(BlockPos pos, ServerWorld world, WireBlockEntity be) {
        this.world = world;
        addWire(pos.toImmutable(), be);
    }

    private WireNetwork(Set<BlockPos> set, ServerWorld world) {
        this.world = world;
        for (BlockPos pos : set) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof WireBlockEntity) {
                this.addWire(pos, (WireBlockEntity) entity);
            }
        }
    }

    public void addWire(BlockPos pos, @Nullable WireBlockEntity wireBlockEntity) {
        pos = pos.toImmutable();
        WireBlockEntity be = wireBlockEntity == null ? (WireBlockEntity)world.getBlockEntity(pos) : wireBlockEntity;
        be.setNetwork(this);
        node(pos);
        for (Direction direction : Direction.values()) {
            BlockPos cap = pos.offset(direction);
            BlockState state = world.getBlockState(cap);
            if (state.getBlock() instanceof WireBlock) {
                WireBlockEntity entity = (WireBlockEntity)world.getBlockEntity(cap);
                if (entity.getNetwork() != this) {
                    this.addWire(cap, entity);
                } else {
                    graph.putEdgeValue(pos, cap, WireConnectionType.WIRE);
                    graph.putEdgeValue(cap, pos, WireConnectionType.WIRE);
                }
                continue;
            }
            CapacitorComponent component = ((BlockComponentProvider) state.getBlock()).getComponent(world, cap, UniversalComponents.CAPACITOR_COMPONENT, direction.getOpposite());
            if (component != null) {
                if (component.canInsertEnergy()) {
                    node(cap);
                    if (component.canExtractEnergy()) {
                        graph.putEdgeValue(pos, cap, WireConnectionType.ENERGY_IO);
                    } else {
                        graph.putEdgeValue(pos, cap, WireConnectionType.ENERGY_INPUT);
                    }
                    this.capList.put(cap, component);
                } else if (component.canExtractEnergy()) {
                    node(cap);
                    graph.putEdgeValue(pos, cap, WireConnectionType.ENERGY_OUTPUT);
                    this.capList.put(cap, component);
                }
            }
        }
    }

    private void invalidate() {
        Galacticraft.logger.debug("Invalidated network.");
        this.invalid = true;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void removeWire(BlockPos blockPos) {
        Set<BlockPos> set = graph.adjacentNodes(blockPos);
        Deque<BlockPos> wires = new LinkedList<>();
        for (BlockPos pos : set) {
            WireConnectionType type = graph.edgeValue(blockPos, pos);
            if (type == WireConnectionType.WIRE) {
                wires.push(pos);
            } else if (type != null && type != WireConnectionType.NONE) {
                graph.removeEdge(blockPos, pos);
                if (graph.adjacentNodes(pos).size() == 0) {
                    graph.removeNode(pos);
                    this.capList.remove(pos);
                }
            } else {
                Galacticraft.logger.debug("Node claimed to be adjacent to other node, but edge was empty or not found!");
            }
        }
        this.graph.removeNode(blockPos);
        this.capList.remove(blockPos);
        wires.add(BlockPos.ORIGIN);

        boolean stillConnected = true;
        boolean changed = false;
        if (wires.size() >= 3) { // if its connected to one wire, this wire being removed is not vital to the graph staying connected
            while (wires.size() > 2) {
                BlockPos pos = wires.pop();
                BlockPos pos1 = wires.pop();
                if (pos == BlockPos.ORIGIN) {
                    if (changed) {
                        pos = wires.pop();
                        wires.addLast(BlockPos.ORIGIN);
                        changed = false;
                    } else {
                        wires.addLast(pos);
                        wires.addLast(pos1);
                        stillConnected = false;
                        break;
                    }
                } else if (pos1 == BlockPos.ORIGIN) {
                    if (changed) {
                        pos1 = wires.pop();
                        wires.addLast(BlockPos.ORIGIN);
                        changed = false;
                    } else {
                        wires.add(pos);
                        wires.add(pos1);
                        stillConnected = false;
                        break;
                    }
                }
                wires.addLast(pos1);

                if (!checkConnected(pos, pos1)) {
                    wires.addLast(pos);
                } else {
                    changed = true;
                }
            }
        }

        wires.remove(BlockPos.ORIGIN);

        if (!stillConnected) {
            if (wires.size() < 2) {
                throw new RuntimeException("marcus8448 did a bad");
            }
            this.invalidate();
            while (!wires.isEmpty()) {
                new WireNetwork(Graphs.reachableNodes(this.graph, wires.pop()), this.world);
            }
        }
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
    }

    public void updateConnections(BlockPos neighbor, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);

        if (blockState.isAir() && this.graph.nodes().contains(pos)) {
            Set<BlockPos> conn = this.graph.adjacentNodes(pos);
            boolean b = false;
            for (BlockPos pos1 : conn) {
                if (this.graph.edgeValue(pos, pos1) == WireConnectionType.WIRE) {
                    b = true;
                    break;
                }
            }
            if (b) {
                removeWire(pos);
            } else {
                this.graph.removeEdge(neighbor, pos);
                this.graph.removeEdge(pos, neighbor);
            }
        }

        if (blockState.getBlock() instanceof WireBlock) {
            if (world.getBlockEntity(pos) instanceof WireBlockEntity) {
                if (((WireBlockEntity) world.getBlockEntity(pos)).getNetwork() != this) throw new RuntimeException();
            }
        } else if (!blockState.isAir()) {
            this.graph.removeNode(pos);
            this.capList.remove(pos);
            if (((BlockComponentProvider) blockState.getBlock()).hasComponent(world, pos, UniversalComponents.CAPACITOR_COMPONENT, null)) {
                for (Direction direction : Direction.values()) {
                    if (this.graph.nodes().contains(pos.offset(direction))) {
                        CapacitorComponent component = ((BlockComponentProvider) blockState.getBlock()).getComponent(world, pos, UniversalComponents.CAPACITOR_COMPONENT, direction);
                        if (component != null) {
                            if (component.canExtractEnergy() || component.canInsertEnergy()) {
                                this.graph.addNode(pos);
                            }
                            if (component.canExtractEnergy() && component.canInsertEnergy()) {
                                this.graph.putEdgeValue(pos.offset(direction), pos, WireConnectionType.ENERGY_IO);
                                this.capList.put(pos, component);
                            } else if (component.canExtractEnergy()) {
                                this.graph.putEdgeValue(pos.offset(direction), pos, WireConnectionType.ENERGY_OUTPUT);
                                this.capList.put(pos, component);
                            } else if (component.canInsertEnergy()) {
                                this.graph.putEdgeValue(pos.offset(direction), pos, WireConnectionType.ENERGY_INPUT);
                                this.capList.put(pos, component);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "WireNetwork{" +
                "graph=" + graph +
                ", invalid=" + invalid +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WireNetwork network = (WireNetwork) o;
        return isInvalid() == network.isInvalid() &&
                Objects.equals(graph, network.graph) &&
                Objects.equals(world, network.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graph, isInvalid(), world);
    }

    public int spreadEnergy(BlockPos pos, int amount, ActionType actionType) {
        if (!graph.nodes().contains(pos)) throw new RuntimeException();
        Set<BlockPos> visitedNodes = new LinkedHashSet<>();
        Queue<BlockPos> queuedNodes = new ArrayDeque<>();
        visitedNodes.add(pos);
        queuedNodes.add(pos);
        // BFS traversal
        while (!queuedNodes.isEmpty()) {
            BlockPos currentNode = queuedNodes.remove();
            for (BlockPos successor : graph.successors(currentNode)) {
                if (visitedNodes.add(successor)) {
                    CapacitorComponent component = this.capList.get(successor);
                    if (component != null && component.canInsertEnergy()) {
                        amount = component.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amount, actionType);
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
}
