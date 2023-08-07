/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.api.wire.WireNetwork;
import dev.galacticraft.mod.util.DirectionUtil;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.*;
import java.util.function.BiFunction;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class WireNetworkImpl implements WireNetwork {
    private final @NotNull ServerLevel world;
    private final @NotNull Object2ObjectOpenHashMap<BlockPos, EnergyStorage> storages = new Object2ObjectOpenHashMap<>();
    private final @NotNull ObjectSet<BlockPos> wires = new ObjectLinkedOpenHashSet<>(1);
    private final @NotNull ObjectSet<WireNetwork> peerNetworks = new ObjectLinkedOpenHashSet<>(0);
    private boolean markedForRemoval = false;
    private final long maxTransferRate;
    private long tickId;
    private long transferred = 0;

    public WireNetworkImpl(@NotNull ServerLevel world, long maxTransferRate) {
        this.world = world;
        this.maxTransferRate = maxTransferRate;
        this.tickId = world.getServer().getTickCount();
    }

    @Override
    public boolean addWire(@NotNull BlockPos pos, @Nullable Wire wire) {
        assert !this.markedForRemoval();
        if (wire == null) {
            wire = (Wire) world.getBlockEntity(pos);
        }
        assert wire != null : "Attempted to add wire that does not exist!";
        assert pos.equals(((BlockEntity) wire).getBlockPos());
        if (this.isCompatibleWith(wire)) {
            wire.setNetwork(this);
            this.wires.add(pos);
            for (Direction direction : Constant.Misc.DIRECTIONS) {
                if (wire.canConnect(direction)) {
                    BlockEntity blockEntity = world.getBlockEntity(pos.relative(direction));
                    if (blockEntity != null && !blockEntity.isRemoved()) {
                        if (blockEntity instanceof Wire adjacentWire) {
                            if (adjacentWire.canConnect(direction.getOpposite())) {
                                if (this.isCompatibleWith(adjacentWire)) {
                                    if (adjacentWire.getNetwork() == null || adjacentWire.getNetwork().markedForRemoval()) {
                                        this.addWire(pos.relative(direction), adjacentWire);
                                    } else {
                                        assert adjacentWire.getNetwork().getMaxTransferRate() == this.getMaxTransferRate();
                                        if (adjacentWire.getNetwork() != this) {
                                            this.takeAll(adjacentWire.getNetwork());
                                        }
                                    }
                                } else {
                                    this.peerNetworks.add(adjacentWire.getOrCreateNetwork());
                                }
                            }
                            continue;
                        }
                    }
                    EnergyStorage storage = EnergyStorage.SIDED.find(world, pos.relative(direction), direction.getOpposite());
                    if (storage != null && storage.supportsInsertion()) {
                        this.storages.put(pos.relative(direction), storage);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void takeAll(@NotNull WireNetwork network) {
        for (BlockPos pos : network.getAllWires()) {
            BlockEntity entity = this.world.getBlockEntity(pos);
            if (entity instanceof Wire wire && !entity.isRemoved()) {
                wire.setNetwork(this);
                this.wires.add(pos);
            }
        }

        this.storages.putAll(network.getStorages());
        network.markForRemoval();
    }

    @Override
    public void removeWire(Wire wire, @NotNull BlockPos removedPos) {
        if (this.markedForRemoval()) {
            this.wires.clear();
            Constant.LOGGER.warn("Tried to remove wire from removed network!");
            return;
        }
        assert this.wires.contains(removedPos) : "Tried to remove wire that does not exist!";
        this.wires.remove(removedPos);
        if (this.wires.isEmpty()) {
            this.markForRemoval();
            return;
        }

        List<BlockPos> adjacent = new LinkedList<>();
        this.reattachAdjacent(removedPos, this.storages, (blockPos, direction) -> EnergyStorage.SIDED.find(this.world, blockPos.relative(direction), direction.getOpposite()), adjacent);
        adjacent.clear();

        for (Direction direction : Constant.Misc.DIRECTIONS) {
            if (wire.canConnect(direction)) {
                BlockPos adjacentWirePos = removedPos.relative(direction);
                if (this.wires.contains(adjacentWirePos)) {
                    if (((Wire) Objects.requireNonNull(this.world.getBlockEntity(adjacentWirePos))).canConnect(direction.getOpposite())) {
                        adjacent.add(adjacentWirePos); // Don't bother testing if it was unable to connect
                    }
                }
            }
        }
        List<List<BlockPos>> mappedWires = new LinkedList<>();

        for (BlockPos blockPos : adjacent) {
            boolean handled = false;
            for (List<BlockPos> mapped : mappedWires) {
                handled = mapped.contains(blockPos);
                if (handled) break;
            }
            if (handled) continue;
            List<BlockPos> list1 = new LinkedList<>();
            list1.add(blockPos);
            this.traverse(list1, blockPos, null);
            mappedWires.add(list1);
        }

        assert mappedWires.size() > 0 : "A wire was added that should never have been accepted";
        if (mappedWires.size() == 1) return;
        this.markForRemoval();
        for (List<BlockPos> positions : mappedWires) {
            WireNetwork network = WireNetwork.create(this.world, this.getMaxTransferRate());
            network.addWire(positions.get(0), null);
            assert network.getAllWires().containsAll(positions);
        }
    }

    private void traverse(List<BlockPos> list, BlockPos pos, @Nullable Direction ignore) {
        BlockPos pos1;
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            if (direction.getOpposite() == ignore) continue;
            Wire wire = (Wire) world.getBlockEntity(pos);
            if (wire.canConnect(direction)) {
                pos1 = pos.relative(direction);
                if (this.wires.contains(pos1)) {
                    if (world.getBlockEntity(pos1) instanceof Wire wire1 && wire1.canConnect(direction.getOpposite())) {
                        if (!list.contains(pos1)) {
                            list.add(pos1);
                            this.traverse(list, pos1, direction);
                        }
                    }
                }
            }
        }
    }

    private <T> void reattachAdjacent(BlockPos pos, Object2ObjectOpenHashMap<BlockPos, T> map, BiFunction<BlockPos, Direction, T> function, List<BlockPos> optionalList) {
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            BlockPos adjacentPos = pos.relative(direction);
            if (map.remove(adjacentPos) != null) {
                for (Direction dir : Constant.Misc.DIRECTIONS) {
                    if (dir == direction.getOpposite()) continue;
                    if (this.wires.contains(adjacentPos.relative(dir))) {
                        if (((Wire) world.getBlockEntity(adjacentPos.relative(dir))).canConnect(dir.getOpposite())) {
                            T value = function.apply(adjacentPos, dir);
                            if (value != null) {
                                optionalList.add(adjacentPos);
                                map.put(adjacentPos, value);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean updateConnection(@NotNull BlockPos adjacentToUpdated, @NotNull BlockPos updatedPos) {
        assert !(world.getBlockEntity(updatedPos) instanceof Wire);
        this.storages.remove(updatedPos);
        BlockPos vector = updatedPos.subtract(adjacentToUpdated);
        Direction direction = DirectionUtil.fromNormal(vector.getX(), vector.getY(), vector.getZ());
        EnergyStorage storage = EnergyStorage.SIDED.find(world, updatedPos, direction.getOpposite());
        if (storage != null) {
            this.storages.put(updatedPos, storage);
            return true;
        }
        return false;
    }

    @Override
    public long insert(@NotNull BlockPos fromWire, long amount, Direction direction, @NotNull TransactionContext transaction) {
        BlockPos source = fromWire.relative(direction.getOpposite());
        if (this.tickId != (this.tickId = world.getServer().getTickCount())) {
            this.transferred = 0;
        }
        amount = Math.min(amount, this.getMaxTransferRate() - this.transferred);
        if (amount <= 0) return amount;

        Object2LongArrayMap<WireNetwork> nonFullInsertables = new Object2LongArrayMap<>(1 + this.peerNetworks.size());
        nonFullInsertables.defaultReturnValue(-1);
        this.getNonFullInsertables(nonFullInsertables, source, amount, transaction);
        long requested = 0;
        LongIterator it = nonFullInsertables.values().longIterator();
        while (it.hasNext()) {
            requested += it.nextLong();
        }
        if (requested == 0) return amount;
        var ref = new Object() {
            private long available = 0;
        };
        ref.available = amount;
        double ratio = (double)amount / (double)requested;
        if (ratio > 1) ratio = 1;

        long finalAmount = amount;
        double finalRatio = ratio;
        nonFullInsertables.forEach((wireNetwork, integer) -> ref.available = wireNetwork.insertInternal(finalAmount, finalRatio, ref.available, transaction));

        return ref.available;
    }

    @Override
    public long insertInternal(long amount, double ratio, long available, TransactionContext transaction) {
        if (this.tickId != (this.tickId = world.getServer().getTickCount())) {
            this.transferred = 0;
        }
        long removed = amount - Math.min(amount, this.maxTransferRate - this.transferred);
        amount -= removed;
        for (EnergyStorage storage : this.storages.values()) {
            long consumed = Math.min(Math.min(available, (long) (amount * ratio)), this.getMaxTransferRate() - this.transferred);
            if (consumed == 0) continue;
            available -= storage.insert(consumed, transaction);
            this.transferred += consumed;
        }
        return available + removed;
    }

    @Override
    public void getNonFullInsertables(Object2LongMap<WireNetwork> energyRequirement, BlockPos source, long amount, @NotNull TransactionContext transaction) {
        if (this.tickId != (this.tickId = world.getServer().getTickCount())) {
            this.transferred = 0;
        }
        amount = Math.min(amount, this.maxTransferRate - this.transferred);
        if (energyRequirement.putIfAbsent(this, 0) == -1) {
            long requested = 0;
            for (ObjectIterator<Object2ObjectMap.Entry<BlockPos, EnergyStorage>> it = this.getStorages().object2ObjectEntrySet().fastIterator(); it.hasNext(); ) {
                Map.Entry<BlockPos, EnergyStorage> entry = it.next();
                if (entry.getKey().equals(source)) continue;
                try (Transaction simulation = Transaction.openNested(transaction)){
                    requested += entry.getValue().insert(amount, simulation);
                }
            }
            for (WireNetwork peerNetwork : this.peerNetworks) {
                if (!energyRequirement.containsKey(peerNetwork)) {
                    peerNetwork.getNonFullInsertables(energyRequirement, source, amount, transaction);
                }
            }
            energyRequirement.put(this, requested);
        }
    }

    @Override
    public long getMaxTransferRate() {
        return this.maxTransferRate;
    }

    @Override
    public Collection<BlockPos> getAllWires() {
        return this.wires;
    }

    @Override
    public @NotNull Object2ObjectOpenHashMap<BlockPos, EnergyStorage> getStorages() {
        return this.storages;
    }

    @Override
    public boolean markedForRemoval() {
        return this.markedForRemoval;
    }

    @Override
    public void markForRemoval() {
        this.markedForRemoval = true;
    }

    @Override
    public boolean isCompatibleWith(Wire wire) {
        return this.getMaxTransferRate() == wire.getMaxTransferRate();
    }

    @Override
    public String toString() {
        return "WireNetworkImpl{" +
                "world=" + world.dimension().location() +
                ", insertable=" + storages +
                ", wires=" + wires +
                ", markedForRemoval=" + markedForRemoval +
                ", maxTransferRate=" + maxTransferRate +
                ", tickId=" + tickId +
                ", transferred=" + transferred +
                '}';
    }
}
