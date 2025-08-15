/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.api.wire;

import com.google.common.collect.ImmutableList;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.WireNetworkAccessor;
import dev.galacticraft.mod.api.block.WireBlock;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;

public class WireNetworkManager {
    private final ServerLevel level;
    private final List<Wire> pendingWires = new ArrayList<>();
    private final Object2ObjectMap<NetworkId, // network id to
                        Long2ObjectMap< // chunk position (OF ADJACENT WIRE) to
                                List< // list of blocks connected to wires (in the chunk) with associated direction data FACING WIRE (for direct use in search)
                                        BlockData>>> chunkData = new Object2ObjectOpenHashMap<>();

    public WireNetworkManager(ServerLevel level) {
        this.level = level;
    }

    public static void registerHooks() {
        // BEFORE block entities unloaded, AFTER chunk is marked as unloaded
        ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> {
            long pos = chunk.getPos().toLong();
            ((WireNetworkAccessor) level).galacticraft$getWireNetworkManager().chunkData.values().removeIf(m -> m.remove(pos) != null && m.isEmpty());
        });

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            for (ServerLevel level : server.getAllLevels()) {
                ((WireNetworkAccessor) level).galacticraft$getWireNetworkManager().loadPendingWires();
            }
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerLevel level : server.getAllLevels()) {
                ((WireNetworkAccessor) level).galacticraft$getWireNetworkManager().tick();
            }
        });
    }

    private void loadPendingWires() {
        ImmutableList<Wire> wires = ImmutableList.copyOf(this.pendingWires);
        this.pendingWires.clear();
        for (Wire wire : wires) {
            this.wirePlaced(wire.getBlockPos(), wire);
        }
    }

    private void tick() {
        this.loadPendingWires();
        Object2LongMap<EnergyStorage> consumers = new Object2LongOpenHashMap<>();
        Object2LongMap<EnergyStorage> producers = new Object2LongOpenHashMap<>();

        for (Map.Entry<NetworkId, Long2ObjectMap<List<BlockData>>> e : this.chunkData.entrySet()) {
            final long maxTransfer = e.getKey().throughput();
            final Long2ObjectMap<List<BlockData>> chunks = e.getValue();

            long totalRequested = 0;
            long totalAvailable = 0;
            try (Transaction transaction = Transaction.openOuter()) {
                // calculate total requested amount
                try (Transaction test = Transaction.openNested(transaction)) {
                    for (List<BlockData> chunk : chunks.values()) {
                        for (BlockData block : chunk) {
                            for (Direction direction : block.directions) {
                                EnergyStorage energyStorage = block.block.find(direction);
                                if (energyStorage != null && energyStorage.supportsInsertion()) {
                                    long requested = energyStorage.insert(maxTransfer, test);
                                    if (requested > 0) {
                                        totalRequested += requested;
                                        consumers.put(energyStorage, requested);
                                    }
                                }
                            }
                        }
                    }
                }

                // calculate total extractable amount
                try (Transaction test = Transaction.openNested(transaction)) {
                    for (Long2ObjectMap.Entry<List<BlockData>> chunk : chunks.long2ObjectEntrySet()) {
                        for (BlockData block : chunk.getValue()) {
                            for (Direction direction : block.directions) {
                                EnergyStorage energyStorage = block.block.find(direction);
                                if (energyStorage != null && energyStorage.supportsExtraction()) {
                                    long available = energyStorage.extract(maxTransfer, test);
                                    if (available > 0) {
                                        totalAvailable += available;
                                        producers.put(energyStorage, available);
                                    }
                                }
                            }
                        }
                    }
                }

                if (totalAvailable == 0 || totalRequested == 0) continue;

                long actual = 0;
                if (totalAvailable < totalRequested) {
                    double brownoutFactor = (double) totalAvailable / totalRequested;
                    Constant.LOGGER.warn("brownout factor {}", brownoutFactor);
                    for (Object2LongMap.Entry<EnergyStorage> consumer : consumers.object2LongEntrySet()) {
                        actual += consumer.getKey().insert(Mth.floor(consumer.getLongValue() * brownoutFactor), transaction);
                    }
                } else {
                    for (Object2LongMap.Entry<EnergyStorage> consumer : consumers.object2LongEntrySet()) {
                        actual += consumer.getKey().insert(Mth.floor(consumer.getLongValue()), transaction);
                    }
                }
                assert actual <= totalAvailable;
                if (actual < totalAvailable) {
                    double factor = (double) actual / totalAvailable;
                    Constant.LOGGER.warn("factor {}", factor);
                    for (Object2LongMap.Entry<EnergyStorage> producer : producers.object2LongEntrySet()) {
                        actual -= producer.getKey().extract((int)(producer.getLongValue() * factor), transaction);
                    }
                } else {
                    for (Object2LongMap.Entry<EnergyStorage> producer : producers.object2LongEntrySet()) {
                        actual -= producer.getKey().extract(producer.getLongValue(), transaction);
                    }
                }
                //fixme not necessarily equal due to rounding probably
                if (actual != 0) {
                    Constant.LOGGER.warn("Delta {}", actual);
                }
                transaction.commit();
            }
            consumers.clear();
            producers.clear();
        }
    }

    public void wirePlaced(BlockPos pos, Wire wire) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            if (wire.isConnected(direction)) {
                this.tryConnectToEndpoint(pos, wire, direction, mutable);
            }
        }
    }

    public void wireLoaded(BlockPos pos, Wire wire) {
        Constant.LOGGER.info("Loaded Wire {}", pos);
    }

    public void enqueueWireLoaded(BlockPos pos, Wire wire) {
        if (wire.getNetwork() == null) return; // never connected to anything, so don't care.
        this.pendingWires.add(wire);
    }

    public void wireUpdated(BlockPos pos, Wire wire, Direction direction) {
        Constant.LOGGER.info("Updating wire [{}] towards {} [{}] ({})", pos.toShortString(), direction, pos.relative(direction).toShortString(), wire.isConnected(direction) ? "create" : "destroy");
        if (wire.isConnected(direction)) { // new connection
            if (this.level.getBlockEntity(pos.relative(direction)) instanceof Wire adj) {
                if (((WireBlock) adj.getBlockState().getBlock()).getThroughput() != ((WireBlock) wire.getBlockState().getBlock()).getThroughput()) throw new AssertionError("Connected to wire with different transfer rate!?");
                if (wire.getNetwork() == null) {
                    if (adj.getNetwork() == null) {
                        Constant.LOGGER.info("Both invalid - new network");
                        // both invalid - make a new network (no contents)
                        wire.setNetwork(new NetworkId(UUID.randomUUID(), ((WireBlock) wire.getBlockState().getBlock()).getThroughput()));
                        adj.setNetwork(wire.getNetwork());
                    } else {
                        Constant.LOGGER.info("New wire has network - joining {}", adj.getNetwork());
                        wire.setNetwork(adj.getNetwork()); // invalid networks have no contents
                    }
                } else if (adj.getNetwork() == null) {
                    Constant.LOGGER.info("New wire has no network - joining {}", wire.getNetwork());
                    adj.setNetwork(wire.getNetwork()); // invalid networks have no contents
                } else if (!wire.getNetwork().equals(adj.getNetwork())){ // both networks have different contents - merge
                    Constant.LOGGER.info("New has different network - merging");
                    this.merge(adj.getNetwork(), wire.getNetwork(), wire);
                } // they're the same - do nothing
            } else { // not a wire, must be an endpoint
                if (wire.getNetwork() == null) {
                    Constant.LOGGER.info("Creating new network for endpoint");
                    wire.setNetwork(new NetworkId(UUID.randomUUID(), ((WireBlock) wire.getBlockState().getBlock()).getThroughput())); // create network if necessary
                }
                Constant.LOGGER.info("Connecting to endpoint");
                this.tryConnectToEndpoint(pos, wire, direction, new BlockPos.MutableBlockPos());
            }
        } else { // destroyed connection
            if (wire.getNetwork() == null) return; // nothing to do.
            BlockPos adjPos = pos.relative(direction);
            if (this.level.getBlockEntity(adjPos) instanceof Wire adj) {
                Constant.LOGGER.info("Adjacent wire destroyed.");
                assert wire.getNetwork().equals(adj.getNetwork()); // if it was connected it must be the same network.
            } else { // not a wire, must be an endpoint
                Constant.LOGGER.info("Adjacent endpoint destroyed");
                Long2ObjectMap<List<BlockData>> chunks = this.chunkData.get(wire.getNetwork());
                if (chunks == null) {
                    Constant.LOGGER.warn("Chunks not found");
                    return;
                }
                List<BlockData> blocks = chunks.get(ChunkPos.asLong(pos));
                if (blocks == null) {
                    Constant.LOGGER.warn("Blocks not found");
                    return;
                }
                int size = blocks.size();
                for (int i = 0; i < size; i++) {
                    BlockData block = blocks.get(i);
                    if (adjPos.equals(block.block.getPos())) {
                        if (block.remove(direction.getOpposite())) {
                            blocks.remove(i);
                        }
                        break;
                    }
                }
            }
        }
    }

    private void merge(NetworkId into, NetworkId from, Wire fromWire) {
        assert into.throughput() == from.throughput();
        Constant.LOGGER.info("Merging network {} into {}", from, into);
        Long2ObjectMap<List<BlockData>> target = this.chunkData.computeIfAbsent(into, l -> new Long2ObjectOpenHashMap<>(4));
        Long2ObjectMap<List<BlockData>> remove = this.chunkData.remove(from);
        if (remove != null) {
            for (Long2ObjectMap.Entry<List<BlockData>> chunk : remove.long2ObjectEntrySet()) {
                List<BlockData> blocks = target.computeIfAbsent(chunk.getLongKey(), l -> new ArrayList<>());
                Set<BlockPos> contents = new HashSet<>(blocks.size() + chunk.getValue().size());
                for (BlockData block : blocks) {
                    contents.add(block.block.getPos());
                }
                for (BlockData blockData : chunk.getValue()) {
                    if (contents.add(blockData.block.getPos())) {
                        blocks.add(blockData);
                    } else {
                        for (BlockData blockData2 : blocks) {
                            if (blockData2.block.getPos().equals(blockData.block.getPos())) {
                                for (Direction direction : blockData.directions) {
                                    blockData2.add(direction);
                                }
                            }
                        }
                    }
                }
                blocks.addAll(chunk.getValue());
            }
        }
        this.updateNetwork(fromWire, into);
    }

    private void tryConnectToEndpoint(BlockPos pos, Wire wire, Direction direction, BlockPos.MutableBlockPos mutable) {
        EnergyStorage energyStorage = EnergyStorage.SIDED.find(this.level, mutable.setWithOffset(pos, direction), direction.getOpposite());
        if (energyStorage != null && (energyStorage.supportsExtraction() || energyStorage.supportsInsertion())) {
            List<BlockData> blocks = this.chunkData.computeIfAbsent(wire.getNetwork(), l -> new Long2ObjectOpenHashMap<>(4))
                    .computeIfAbsent(ChunkPos.asLong(pos), l -> new ArrayList<>());
            BlockData target = null;
            for (BlockData blockData : blocks) {
                if (mutable.equals(blockData.block.getPos())) {
                    target = blockData;
                    break;
                }
            }
            if (target == null) {
                target = new BlockData(BlockApiCache.create(EnergyStorage.SIDED, this.level, mutable.immutable()), new ArrayList<>());
                blocks.add(target);
            }
            target.add(direction.getOpposite());
        }
    }

    public void wireRemoved(BlockPos pos) {
        Constant.LOGGER.info("Wire broken at [{}]", pos.toShortString());
        Wire wire = (Wire) level.getBlockEntity(pos);
        assert wire != null; // should be called BEFORE actual removal

        NetworkId network = wire.getNetwork();
        if (network == null) return; // no need to do anything if there's no network
        wire.setNetwork(null); // invalidate block entity data, in case of swap

        List<Wire> adjacent = new ArrayList<>(6);
        boolean[] adjacentEndpoints = new boolean[6];
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            if (wire.isConnected(direction)) {
                wire.setConnected(direction, false); // invalidate block entity data, in case of swap
                mutable.setWithOffset(pos, direction);
                if (level.getBlockEntity(mutable) instanceof Wire wire1 && network.equals(wire1.getNetwork())) {
                    adjacent.add(wire1); // adjacent wire - will need to check if it's connected to other adjacent wires (to maintain the same network)
                    wire1.setConnected(direction.getOpposite(), false); // remove connection from other end to simplify search later
                    this.level.sendBlockUpdated(mutable, wire.getBlockState(), wire.getBlockState(), Block.UPDATE_IMMEDIATE);
                } else {
                    // non-wire connection - should be a producer or consumer.
                    adjacentEndpoints[direction.get3DDataValue()] = true;
                }
            }
        }

        Long2ObjectMap<List<BlockData>> chunks = this.chunkData.get(network);
        if (chunks != null) {
            List<BlockData> data = chunks.get(ChunkPos.asLong(pos));
            if (data != null) {
                int r = 0;
                for (int i = 0; i < adjacentEndpoints.length; i++) {
                    if (adjacentEndpoints[i]) {
                        Direction direction = Constant.Misc.DIRECTIONS[i];
                        mutable.setWithOffset(pos, direction);
                        direction = direction.getOpposite();
                        for (Iterator<BlockData> iterator = data.iterator(); iterator.hasNext(); ) {
                            BlockData e = iterator.next();
                            if (mutable.equals(e.block.getPos())) {
                                if (e.directions.remove(direction) && e.directions.isEmpty()) {
                                    iterator.remove();
                                    r++;
                                    break;
                                }
                            }
                        }
                    }
                }
                Constant.LOGGER.info("Removed {} adjacent endpoints", r);
                if (data.isEmpty()) {
                    chunks.remove(ChunkPos.asLong(mutable));
                }
            }
        }

        if (adjacent.size() > 1) {
            repairNetwork(mutable, adjacent, network);
        } else if (adjacent.size() == 1) {
            Constant.LOGGER.info("Wire on end. Nothing to repair.");
        } else {
            if (this.chunkData.get(network).isEmpty()) {
                Constant.LOGGER.info("Last wire broken - destroying all references to network");
                this.chunkData.remove(network);
            } else {
                Constant.LOGGER.error("Last wire broken, but dangling references remain??");
            }
        }
    }

    // remember to drop center connections on all targets before calling
    private void repairNetwork(BlockPos.MutableBlockPos mutable, List<Wire> targets, NetworkId network) {
        Constant.LOGGER.info("Attempting to repair network {}", network);
        Set<Wire> visited = new HashSet<>(); // wires that have already been searched
        List<Wire> queue = new ArrayList<>(6); // wires that have yet to be searched (guaranteed to be a wire in the same network)

        while (!targets.isEmpty()) {
            Wire e = targets.removeLast();
            queue.add(e);
            visited.add(e);

            // loop until we exhaust the queue or prove that the network is intact
            while (!queue.isEmpty() && !targets.isEmpty()) {
                Wire current = queue.removeLast();
                assert network.equals(current.getNetwork());

                for (Direction direction : Constant.Misc.DIRECTIONS) {
                    if (current.isConnected(direction)) { // check if there is a WIRE connection
                        if (this.level.getBlockEntity(mutable.setWithOffset(current.getBlockPos(), direction)) instanceof Wire wire) {
                            assert network.equals(wire.getNetwork()); // it should not be connected if it's in a different network
                            if (visited.add(wire)) { // prevent infinite recursion
                                queue.add(wire);
                                targets.remove(wire);
                            }
                        }
                    }
                }
            }

            // just exhausted the connected wire search - is everything still connected?
            if (!targets.isEmpty()) { // NO - make a new network
                NetworkId newNetwork = new NetworkId(UUID.randomUUID(), network.throughput());
                Constant.LOGGER.info("Repair: Split off new network {}, with {} wires.", newNetwork, visited.size());
                for (Wire wire : visited) {
                    wire.setNetwork(newNetwork);
                }
                visited.clear();
            } // otherwise we can reuse the network id - last iteration of a breakup is ok too as everyone else has migrated
        }
        if (!visited.isEmpty()) {
            Constant.LOGGER.info("Repair: Old network reused.");
        }
    }

    private void updateNetwork(Wire target, NetworkId newNetwork) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        NetworkId network = target.getNetwork();
        Set<Wire> visited = new HashSet<>(); // wires that have already been searched
        List<Wire> queue = new ArrayList<>(6); // wires that have yet to be searched (guaranteed to be a wire in the same network)

        queue.add(target);
        visited.add(target);

        int updated = 0;

        // loop until we exhaust the queue
        while (!queue.isEmpty()) {
            Wire current = queue.removeLast();
            assert current.getNetwork().equals(network);
            current.setNetwork(newNetwork); // update the wire's network
            updated++;

            for (Direction direction : Constant.Misc.DIRECTIONS) {
                if (current.isConnected(direction)) { // check if there is a WIRE connection
                    if (this.level.getBlockEntity(mutable.setWithOffset(current.getBlockPos(), direction)) instanceof Wire wire) {
                        assert wire.getNetwork().equals(network); // it should not be connected if it's in a different network
                        if (visited.add(wire)) { // prevent infinite recursion
                            queue.add(wire);
                        }
                    }
                }
            }
        }
        Constant.LOGGER.info("Updated {} wires to network: {}", updated, newNetwork);
    }

    private record BlockData(BlockApiCache<EnergyStorage, Direction> block, List<Direction> directions) {
        private void add(Direction direction) {
            if (!this.directions.contains(direction)) this.directions.add(direction);
        }

        private boolean remove(Direction direction) {
            boolean removed = this.directions.remove(direction);
            assert removed;
            return this.directions.isEmpty();
        }
    }
}
