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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.WireNetworkAccessor;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class WireNetworkManager {
    public static final long INVALID_NETWORK_ID = Long.MIN_VALUE; // FIXME: CHANGE ID TO OBJECT W/ POWER RATING
    // Atomic as I think there are mods that try to thread the game logic (although it might just fall apart anyway)
    private final AtomicLong counter = new AtomicLong(0); //TODO: save/load to server
    private final ServerLevel level;
    private final Long2ObjectMap<List<Wire>> pendingWires = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectMap< // network id to
                    Long2ObjectMap< // chunk position (OF ADJACENT WIRE) to
                            List< // list of blocks connected to wires (in the chunk) with associated direction data FACING WIRE (for direct use in search)
                                    BlockData>>> chunkData = new Long2ObjectOpenHashMap<>();
    private boolean loaded = false;

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
                ((WireNetworkAccessor) level).galacticraft$getWireNetworkManager().finishLoad();
            }
        });

        ServerChunkEvents.CHUNK_LOAD.register((level, chunk) -> {
            ((WireNetworkAccessor) level).galacticraft$getWireNetworkManager().chunkLoaded(chunk);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerLevel level : server.getAllLevels()) {
                ((WireNetworkAccessor) level).galacticraft$getWireNetworkManager().tick();
            }
        });
    }

    private void finishLoad() {
        this.loaded = true;
        this.pendingWires.forEach(((aLong, wires) -> {
            for (Wire wire : wires) {
                this.wireLoaded(wire.getBlockPos(), wire);
            }
        }));
    }

    private void tick() {
        Object2LongMap<EnergyStorage> consumers = new Object2LongOpenHashMap<>();
        Object2LongMap<EnergyStorage> producers = new Object2LongOpenHashMap<>();

        for (Long2ObjectMap<List<BlockData>> chunks : this.chunkData.values()) {
            long totalRequested = 0;
            long totalAvailable = 0;
            final long maxTransfer = 1000L; //fixme
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
                        for (BlockData entry : chunk.getValue()) {
                            for (Direction direction : entry.directions) {
                                EnergyStorage energyStorage = entry.block.find(direction);
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
                    for (Object2LongMap.Entry<EnergyStorage> entry : consumers.object2LongEntrySet()) {
                        actual += entry.getKey().insert(Mth.floor(entry.getLongValue() * brownoutFactor), transaction);
                    }
                } else {
                    for (Object2LongMap.Entry<EnergyStorage> entry : consumers.object2LongEntrySet()) {
                        actual += entry.getKey().insert(Mth.floor(entry.getLongValue()), transaction);
                    }
                }
                assert actual <= totalAvailable;
                if (actual < totalAvailable) {
                    double factor = (double) actual / totalAvailable;
                    Constant.LOGGER.warn("factor {}", factor);
                    for (Object2LongMap.Entry<EnergyStorage> entry : producers.object2LongEntrySet()) {
                        actual -= entry.getKey().extract((int)(entry.getLongValue() * factor), transaction);
                    }
                } else {
                    for (Object2LongMap.Entry<EnergyStorage> entry : producers.object2LongEntrySet()) {
                        actual -= entry.getKey().extract(entry.getLongValue(), transaction);
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
        if (wire.getNetwork() == INVALID_NETWORK_ID) return; // never connected to anything, so don't care.

        this.wirePlaced(pos, wire);
    }

    public void enqueueWireLoaded(BlockPos pos, Wire wire) {
        if (wire.getNetwork() == INVALID_NETWORK_ID) return; // never connected to anything, so don't care.
        if (!this.loaded) {
            int x = SectionPos.blockToSectionCoord(pos.getX());
            int z = SectionPos.blockToSectionCoord(pos.getZ());
            this.pendingWires.computeIfAbsent(ChunkPos.asLong(x, z), k -> new ArrayList<>()).add(wire);
        } else {
            this.wireLoaded(pos, wire);
        }
    }

    private void chunkLoaded(LevelChunk chunk) {
        if (this.loaded) {
            List<Wire> wires = this.pendingWires.remove(chunk.getPos().toLong());
            if (wires != null) {
                for (Wire wire : wires) {
                    this.wireLoaded(wire.getBlockPos(), wire);
                }
            }
        }
    }

    public void wireUpdated(BlockPos pos, Wire wire, Direction direction) {
        Constant.LOGGER.info("Updated Wire {} towards {} ({})", pos, direction, pos.relative(direction));
        if (wire.isConnected(direction)) { // new connection
            if (this.level.getBlockEntity(pos.relative(direction)) instanceof Wire adj) {
                Constant.LOGGER.info("New wire");
                if (adj.getNetwork() != wire.getNetwork()) {
                    if (wire.getNetwork() == INVALID_NETWORK_ID) {
                        wire.setNetwork(adj.getNetwork()); // invalid networks have no contents
                        Constant.LOGGER.info("Joining network {}", wire.getNetwork());
                    } else if (adj.getNetwork() == INVALID_NETWORK_ID) {
                        adj.setNetwork(wire.getNetwork()); // invalid networks have no contents
                        Constant.LOGGER.info("Joining network {}", adj.getNetwork());
                    } else {
                        // merge the networks
                        this.merge(adj.getNetwork(), wire.getNetwork(), wire);
                    }
                } else if (adj.getNetwork() == INVALID_NETWORK_ID) {
                    Constant.LOGGER.info("Both invalid - new network");
                    // both invalid - group (no contents)
                    wire.setNetwork(this.counter.getAndIncrement());
                    adj.setNetwork(wire.getNetwork());
                } // otherwise we're already the same network - no change
            } else { // not a wire, must be an endpoint
                if (wire.getNetwork() == INVALID_NETWORK_ID) {
                    Constant.LOGGER.info("Creating new network for endpoint");
                    wire.setNetwork(this.counter.getAndIncrement()); // create network if necessary
                }
                Constant.LOGGER.info("Connecting to endpoint");
                this.tryConnectToEndpoint(pos, wire, direction, new BlockPos.MutableBlockPos());
            }
        } else { // destroyed connection
            Constant.LOGGER.info("Connection destroyed?");
            if (wire.getNetwork() == INVALID_NETWORK_ID) return; // nothing to do.
            BlockPos adjPos = pos.relative(direction);
            if (this.level.getBlockEntity(adjPos) instanceof Wire adj) {
                assert adj.getNetwork() == wire.getNetwork(); // if it was connected it must be the same network.
            } else { // not a wire, must be an endpoint
                Constant.LOGGER.info("Endpoint destroyed");
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

    private void merge(long into, long from, Wire fromWire) {
        Constant.LOGGER.info("Merging network {} into {}", from, into);
        Long2ObjectMap<List<BlockData>> target = this.chunkData.computeIfAbsent(into, l -> new Long2ObjectOpenHashMap<>(4));
        Long2ObjectMap<List<BlockData>> remove = this.chunkData.remove(from);
        if (remove != null) {
            for (Long2ObjectMap.Entry<List<BlockData>> entry : remove.long2ObjectEntrySet()) {
                List<BlockData> blocks = target.computeIfAbsent(entry.getLongKey(), l -> new ArrayList<>());
                Set<BlockPos> contents = new HashSet<>(blocks.size() + entry.getValue().size());
                for (BlockData block : blocks) {
                    contents.add(block.block.getPos());
                }
                for (BlockData blockData : entry.getValue()) {
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
                blocks.addAll(entry.getValue());
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
        Constant.LOGGER.info("Wire removed! {}", pos);
        Wire wire = (Wire) level.getBlockEntity(pos);
        assert wire != null; // should be called BEFORE actual removal

        long network = wire.getNetwork();
        wire.setNetwork(INVALID_NETWORK_ID); // invalidate block entity data, in case of swap

        List<Wire> adjacent = new ArrayList<>(6);
        boolean[] adjacentEndpoints = new boolean[6];
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            if (wire.isConnected(direction)) {
                wire.setConnected(direction, false); // invalidate block entity data, in case of swap
                mutable.setWithOffset(pos, direction);
                if (level.getBlockEntity(mutable) instanceof Wire wire1 && wire1.getNetwork() == network) {
                    adjacent.add(wire1); // adjacent wire - will need to check if it's connected to other adjacent wires (to maintain the same network)
                    wire1.setConnected(direction.getOpposite(), false); // remove connection from other end to simplify search later
                    this.level.sendBlockUpdated(mutable, ((BlockEntity) wire).getBlockState(), ((BlockEntity) wire).getBlockState(), Block.UPDATE_IMMEDIATE);
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

        repairNetwork(mutable, adjacent, network);
    }

    // remember to drop center connections on all targets before calling
    private void repairNetwork(BlockPos.MutableBlockPos mutable, List<Wire> targets, long network) {
        Set<Wire> visited = new HashSet<>(); // wires that have already been searched
        List<Wire> queue = new ArrayList<>(6); // wires that have yet to be searched (guaranteed to be a wire in the same network)

        while (!targets.isEmpty()) {
            Wire e = targets.removeLast();
            queue.add(e);
            visited.add(e);

            // loop until we exhaust the queue or prove that the network is intact
            while (!queue.isEmpty() && !targets.isEmpty()) {
                Wire current = queue.removeLast();
                assert current.getNetwork() == network;

                for (Direction direction : Constant.Misc.DIRECTIONS) {
                    if (current.isConnected(direction)) { // check if there is a WIRE connection
                        if (this.level.getBlockEntity(mutable.setWithOffset(((BlockEntity) current).getBlockPos(), direction)) instanceof Wire wire) {
                            assert wire.getNetwork() == network; // it should not be connected if it's in a different network
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
                long newNetwork = this.counter.getAndIncrement();
                for (Wire wire : visited) {
                    wire.setNetwork(newNetwork);
                }
                visited.clear();
            } // otherwise we can reuse the network id - last iteration of a breakup is ok too as everyone else has migrated
        }
    }

    private void updateNetwork(Wire target, long newNetwork) {
        Constant.LOGGER.info("Updating to network: {}", newNetwork);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        long network = target.getNetwork();
        Set<Wire> visited = new HashSet<>(); // wires that have already been searched
        List<Wire> queue = new ArrayList<>(6); // wires that have yet to be searched (guaranteed to be a wire in the same network)

        queue.add(target);
        visited.add(target);

        // loop until we exhaust the queue
        while (!queue.isEmpty()) {
            Wire current = queue.removeLast();
            assert current.getNetwork() == network;
            current.setNetwork(newNetwork); // update the wire's network

            for (Direction direction : Constant.Misc.DIRECTIONS) {
                if (current.isConnected(direction)) { // check if there is a WIRE connection
                    if (this.level.getBlockEntity(mutable.setWithOffset(((BlockEntity) current).getBlockPos(), direction)) instanceof Wire wire) {
                        assert wire.getNetwork() == network; // it should not be connected if it's in a different network
                        if (visited.add(wire)) { // prevent infinite recursion
                            queue.add(wire);
                        }
                    }
                }
            }
        }
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
