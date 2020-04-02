package com.hrznstudio.galacticraft.api.wire;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

public class WireGraph {
    private final Map<Vertex, List<Vertex>> adjacentVertices;
    private final int dimId;

    public WireGraph(BlockPos start, int dimId) {
        this.dimId = dimId;
        this.adjacentVertices = new HashMap<>();
        addVertex(start);
    }

    public void addWire(BlockPos pos) {
        addVertex(pos);
        for (Direction dir : Direction.values()) {
            if (adjacentVertices.containsKey(new Vertex(pos.offset(dir)))) {
                addEdge(pos, pos.offset(dir));
            }
        }
    }

    public void removeWire(BlockPos pos) {
        removeVertex(pos);
    }

    private void addEdge(BlockPos value1, BlockPos value2) {
        Vertex vertex = new Vertex(value1);
        Vertex vertex1 = new Vertex(value2);
        adjacentVertices.get(vertex).add(vertex1);
        adjacentVertices.get(vertex1).add(vertex);
    }

    private void addVertex(BlockPos value) {
        adjacentVertices.putIfAbsent(new Vertex(value), new ArrayList<>());
    }

    private void removeVertex(BlockPos value) {
        Vertex toBeRemoved = new Vertex(value);
        adjacentVertices.remove(toBeRemoved);
        Iterator<Map.Entry<Vertex, List<Vertex>>> iterator = adjacentVertices.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Vertex, List<Vertex>> entry = iterator.next();
            Vertex v = entry.getKey();
            List<Vertex> connections = entry.getValue();
            connections.remove(toBeRemoved);
            if (connections.size() == 0 && adjacentVertices.size() > 1) {
                NetworkManager.getManagerForDimension(dimId).addWire(v.getValue(), new WireGraph(v.getValue(), dimId));
                removeVertex(v.value);
                iterator.remove();
            }
        }
    }

    private void removeEdge(BlockPos value1, BlockPos value2) {
        Vertex v1 = new Vertex(value1);
        Vertex v2 = new Vertex(value2);
        List<Vertex> vertices = adjacentVertices.get(v1);
        List<Vertex> vertices1 = adjacentVertices.get(v2);
        if (vertices != null) {
            vertices.remove(v2);
        }
        if (vertices1 != null) {
            vertices1.remove(v1);
        }
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
            network.adjacentVertices.putAll(this.adjacentVertices);
            return network;
        } else {
            this.adjacentVertices.putAll(network.adjacentVertices);
            return this;
        }
    }

    public static class Vertex {
        private final BlockPos value;

        public Vertex(BlockPos value) {
            this.value = value;
        }

        public BlockPos getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Vertex{" +
                    "value=" + value +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vertex vertex = (Vertex) o;
            return Objects.equals(value, vertex.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public static final class ConnectionInfo {
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
            return Objects.equals(pos, that.pos) &&
                    type == that.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, type);
        }
    }
}
