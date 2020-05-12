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

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class WireNetwork {
    private final Map<BlockPos, List<BlockPos>> adjacentVertices = new ConcurrentHashMap<>();
    private final List<BlockPos> consumers = new ArrayList<>();
    private final List<BlockPos> producers = new ArrayList<>();
    private final List<BlockPos> query = new ArrayList<>();

    private final int dimId;

    public WireNetwork(BlockPos start, int dimId) {
        this(dimId);
        addVertex(new BlockPos(start));
        NetworkManager.getManagerForDimension(dimId).addWire(start, this);
    }

    public WireNetwork(int dimId) {
        this.dimId = dimId;
    }

    public void addWire(BlockPos pos) {
        addVertex(pos);
        NetworkManager.getManagerForDimension(dimId).addWire(pos, this);
        for (Direction dir : Direction.values()) {
            if (adjacentVertices.containsKey(new BlockPos(pos.offset(dir)))) {
                addEdge(pos, new BlockPos(pos.offset(dir)));
            }
        }
    }

    public void removeWire(BlockPos blockPos) {
        ArrayList<BlockPos> skip = new ArrayList<>();
        ArrayList<BlockPos> visited = new ArrayList<>();

        List<BlockPos> adjacent = new ArrayList<>(adjacentVertices.get(blockPos));
        for (BlockPos pos : adjacent) {
            if (skip.contains(pos)) continue;
            NetworkManager.getManagerForDimension(dimId).removeWire(pos);
            WireNetwork g = new WireNetwork(pos, dimId);
            Queue<BlockPos> queue = new LinkedList<>(adjacentVertices.get(pos));
            while (!queue.isEmpty()) {
                BlockPos position = queue.poll();
                if (visited.contains(position)) continue;
                visited.add(position);
                queue.addAll(adjacentVertices.get(new BlockPos(position)));
                NetworkManager.getManagerForDimension(dimId).removeWire(position);
                g.addWire(position);
                removeVertexNC(new BlockPos(position));
                if (adjacent.contains(position)) {
                    skip.add(position);
                }
            }

        }
    }

    private void addEdge(BlockPos value1, BlockPos value2) {
        adjacentVertices.get(value1).add(value2);
        adjacentVertices.get(value2).add(value1);
    }

    private void addVertex(BlockPos value) {
        adjacentVertices.putIfAbsent(value, new ArrayList<>());
    }
//
//    private void removeVertex(BlockPos value) {
//        boolean b = adjacentVertices.remove(value) != null;
//        Iterator<Map.Entry<BlockPos, List<BlockPos>>> iterator = adjacentVertices.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<BlockPos, List<BlockPos>> entry = iterator.next();
//            BlockPos info = entry.getKey();
//            List<BlockPos> connections = entry.getValue();
//            connections.remove(value);
//            if (adjacentVertices.get(info) != null) {
//                if (adjacentVertices.size() == 0) {
//                    NetworkManager.getManagerForDimension(dimId).removeWire(info);
//                    new WireNetwork(info, dimId);
//                    iterator.remove();
//                    removeVertex(info);
//                }
//            }
//        }
//    }

    private boolean removeVertexNC(BlockPos value) {
        boolean b = adjacentVertices.remove(value) != null;
        for (List<BlockPos> connections : adjacentVertices.values()) {
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
        WireNetwork wireNetwork = (WireNetwork) o;
        return Objects.equals(adjacentVertices, wireNetwork.adjacentVertices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adjacentVertices);
    }

    public WireNetwork merge(WireNetwork network) {
        if (this.adjacentVertices.size() < network.adjacentVertices.size()) {
            for (BlockPos pos : this.adjacentVertices.keySet()) {
                NetworkManager.getManagerForDimension(dimId).removeWire(pos);
                network.addWire(pos);
            }
            network.producers.addAll(this.producers);
            network.consumers.addAll(this.consumers);
            network.query.addAll(this.query);
            this.adjacentVertices.clear();
            this.producers.clear();
            this.consumers.clear();
            this.query.clear();
            return network;
        } else {
            for (BlockPos pos : network.adjacentVertices.keySet()) {
                NetworkManager.getManagerForDimension(dimId).removeWire(pos);
                this.addWire(pos);
            }
            this.producers.addAll(network.producers);
            this.consumers.addAll(network.consumers);
            this.query.addAll(network.query);
            network.adjacentVertices.clear();
            network.producers.clear();
            network.consumers.clear();
            network.query.clear();
            return this;
        }
    }

    public void addConsumer(BlockPos pos) {
        consumers.add(pos);
    }

    public void addProducer(BlockPos pos) {
        producers.add(pos);
    }

    public List<BlockPos> getQuery() {
        return new ArrayList<>(query);
    }

    public void clearQuery() {
        query.clear();
    }

    public boolean removeConsumer(BlockPos pos) {
        query.add(pos);
        return consumers.remove(pos);
    }

    public boolean removeProducer(BlockPos pos) {
        query.add(pos);
        return producers.remove(pos);
    }

    public List<BlockPos> getConsumers() {
        return new ArrayList<>(consumers);
    }

    public List<BlockPos> getProducers() {
        return new ArrayList<>(producers);
    }
}
