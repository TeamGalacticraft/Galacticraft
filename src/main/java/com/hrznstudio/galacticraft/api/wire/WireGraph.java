/*
 * Copyright (c) 2019 HRZN LTD
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

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WireGraph {
    private final Map<ConnectionInfo, List<ConnectionInfo>> adjacentVertices = new ConcurrentHashMap<>();
    private final List<BlockPos> consumers = new ArrayList<>();
    private final List<BlockPos> producers = new ArrayList<>();

    private final int dimId;

    public WireGraph(BlockPos start, int dimId) {
        this(dimId);
        addVertex(new ConnectionInfo(start, WireConnectionType.WIRE));
        NetworkManager.getManagerForDimension(dimId).addWire(start, this);
    }

    public WireGraph(int dimId) {
        this.dimId = dimId;
    }

    public void addWire(BlockPos pos) {
        addVertex(new ConnectionInfo(pos, WireConnectionType.WIRE));
        NetworkManager.getManagerForDimension(dimId).addWire(pos, this);
        for (Direction dir : Direction.values()) {
            if (adjacentVertices.containsKey(new ConnectionInfo(pos.offset(dir), WireConnectionType.WIRE))) {
                addEdge(new ConnectionInfo(pos, WireConnectionType.WIRE), new ConnectionInfo(pos.offset(dir), WireConnectionType.WIRE));
            }
        }
    }

    public void removeWire(BlockPos pos) {
        ArrayList<ConnectionInfo> sl = new ArrayList<>();
        ArrayList<ConnectionInfo> hit = new ArrayList<>();

        List<ConnectionInfo> cil = new ArrayList<>(adjacentVertices.get(new ConnectionInfo(pos, WireConnectionType.WIRE)));
        for (ConnectionInfo ci : cil) {
            if (sl.contains(ci)) continue;
            NetworkManager.getManagerForDimension(dimId).removeWire(ci.getPos());
            WireGraph g = new WireGraph(ci.getPos(), dimId);
            Queue<ConnectionInfo> ciq = new LinkedList<>(adjacentVertices.get(new ConnectionInfo(ci.getPos(), WireConnectionType.WIRE)));
            while (!ciq.isEmpty()) {
                ConnectionInfo c = ciq.poll();
                if (hit.contains(c)) continue;
                hit.add(c);
                ciq.addAll(adjacentVertices.get(new ConnectionInfo(c.getPos(), WireConnectionType.WIRE)));
                NetworkManager.getManagerForDimension(dimId).removeWire(c.getPos());
                g.addWire(c.getPos());
                removeVertexNC(new ConnectionInfo(c.pos, WireConnectionType.WIRE));
                if (cil.contains(c)) {
                    sl.add(c);
                }
            }

        }
    }

    private void addEdge(ConnectionInfo value1, ConnectionInfo value2) {
        adjacentVertices.get(value1).add(value2);
        adjacentVertices.get(value2).add(value1);
    }

    private void addVertex(ConnectionInfo value) {
        adjacentVertices.putIfAbsent(value, new ArrayList<>());
    }

    private boolean removeVertex(ConnectionInfo value) {
        boolean b = adjacentVertices.remove(value) != null;
        Iterator<Map.Entry<ConnectionInfo, List<ConnectionInfo>>> iterator = adjacentVertices.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ConnectionInfo, List<ConnectionInfo>> entry = iterator.next();
            ConnectionInfo info = entry.getKey();
            List<ConnectionInfo> connections = entry.getValue();
            connections.remove(value);
            if (adjacentVertices.get(info) != null) {
                if (!adjacentVertices.get(info).contains(ConnectionInfo.WIRE_MATCHALL) && adjacentVertices.size() > 1) {
                    NetworkManager.getManagerForDimension(dimId).removeWire(info.getPos());
                    new WireGraph(info.getPos(), dimId);
                    iterator.remove();
                    removeVertex(info);
                }
            }
        }
        return b;
    }

    private boolean removeVertexNC(ConnectionInfo value) {
        boolean b = adjacentVertices.remove(value) != null;
        for (List<ConnectionInfo> connections : adjacentVertices.values()) {
            connections.remove(value);
        }
        return b;
    }

    @Override
    public String toString() {
        return "WireGraph{" +
                "adjacentVertices=" + adjacentVertices +
                ", consumers=" + consumers +
                ", producers=" + producers +
                ", dimId=" + dimId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WireGraph wireGraph = (WireGraph) o;
        return Objects.equals(adjacentVertices, wireGraph.adjacentVertices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adjacentVertices);
    }

    public WireGraph merge(WireGraph network) {
        if (this.adjacentVertices.size() < network.adjacentVertices.size()) {
            for (Map.Entry<ConnectionInfo, List<ConnectionInfo>> entry : this.adjacentVertices.entrySet()) {
                ConnectionInfo info = entry.getKey();
                List<ConnectionInfo> connectionInfos = entry.getValue();
                network.adjacentVertices.put(info, connectionInfos);
                NetworkManager.getManagerForDimension(dimId).transferWire(info.getPos(), network);
            }
            return network;
        } else {
            for (Map.Entry<ConnectionInfo, List<ConnectionInfo>> entry : network.adjacentVertices.entrySet()) {
                ConnectionInfo info = entry.getKey();
                List<ConnectionInfo> connectionInfos = entry.getValue();
                this.adjacentVertices.put(info, connectionInfos);
                NetworkManager.getManagerForDimension(dimId).transferWire(info.getPos(), this);
            }
            return this;
        }
    }

    public void addConsumer(BlockPos pos, BlockPos wireCon) {
        consumers.add(pos);
        addVertex(new ConnectionInfo(pos, WireConnectionType.ENERGY_INPUT));
        adjacentVertices.get(new ConnectionInfo(pos, WireConnectionType.ENERGY_INPUT)).add(new ConnectionInfo(wireCon, WireConnectionType.WIRE));
        adjacentVertices.get(new ConnectionInfo(wireCon, WireConnectionType.WIRE)).add(new ConnectionInfo(pos, WireConnectionType.ENERGY_INPUT));
    }

    public void addProducer(BlockPos pos, BlockPos wireCon) {
        producers.add(pos);
        addVertex(new ConnectionInfo(pos, WireConnectionType.ENERGY_OUTPUT));
        adjacentVertices.get(new ConnectionInfo(pos, WireConnectionType.ENERGY_OUTPUT)).add(new ConnectionInfo(wireCon, WireConnectionType.WIRE));
        adjacentVertices.get(new ConnectionInfo(wireCon, WireConnectionType.WIRE)).add(new ConnectionInfo(pos, WireConnectionType.ENERGY_OUTPUT));
    }

    public boolean removeConsumer(BlockPos pos) {
        consumers.remove(pos);
        return removeVertex(new ConnectionInfo(pos, WireConnectionType.ENERGY_INPUT));
    }

    public boolean removeProducer(BlockPos pos) {
        producers.remove(pos);
        return removeVertex(new ConnectionInfo(pos, WireConnectionType.ENERGY_OUTPUT));
    }

    public List<BlockPos> getConsumers() {
        return new ArrayList<>(consumers);
    }

    public List<BlockPos> getProducers() {
        return new ArrayList<>(producers);
    }

    public static final class ConnectionInfo {
        private static final ConnectionInfo WIRE_MATCHALL = new ConnectionInfo(new BlockPos(0, -256, 0), WireConnectionType.WIRE);
        private final BlockPos pos;
        private final WireConnectionType type;

        public ConnectionInfo(BlockPos pos, WireConnectionType type) {
            this.pos = pos;
            this.type = type;
        }

        public BlockPos getPos() {
            return pos;
        }

        public WireConnectionType getType() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConnectionInfo that = (ConnectionInfo) o;
            return (Objects.equals(pos, that.pos) &&
                    type == that.type) ||
                    ((this == WIRE_MATCHALL || that == WIRE_MATCHALL) && type == that.type); //make this less magic numbery?
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, type);
        }
    }
}
