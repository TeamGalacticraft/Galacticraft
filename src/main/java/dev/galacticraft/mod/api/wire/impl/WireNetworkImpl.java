/*
 * Copyright (c) 2019-2024 Team Galacticraft
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
import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
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
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class WireNetworkImpl implements WireNetwork {
    private final @NotNull ServerLevel level;
    private final @NotNull Object2ObjectOpenHashMap<BlockPos, EnergyStorage @Nullable[]> wires = new Object2ObjectOpenHashMap<>(1);
    private final @NotNull ObjectSet<WireNetwork> peerNetworks = new ObjectLinkedOpenHashSet<>(0);
    private boolean markedForRemoval = false;
    private final long maxTransferRate;
    private long tickId;
    private long transferred = 0;

    public WireNetworkImpl(@NotNull ServerLevel level, long maxTransferRate, @NotNull BlockPos pos) {
        this.level = level;
        this.maxTransferRate = maxTransferRate;
        this.tickId = level.getServer().getTickCount();
        this.addWireRecursive(pos, null);
    }

    @Override
    public void addWire(@NotNull BlockPos pos, @Nullable Wire wire) {
        assert !this.markedForRemoval();
        if (wire == null) {
            wire = (Wire) this.level.getBlockEntity(pos);
        }
        assert wire != null : "Attempted to add wire that does not exist!";
        assert pos.equals(((BlockEntity) wire).getBlockPos());
        if (this.isCompatibleWith(wire)) {
            if (wire.getNetwork() != null) {
                if (wire.getNetwork() != this && !wire.getNetwork().markedForRemoval()) {
                    wire.getNetwork().markForRemoval();
                    this.wires.putAll(((WireNetworkImpl) wire.getNetwork()).wires);
                    for (Direction direction : Constant.Misc.DIRECTIONS) {
                        if (wire.canConnect(direction)) {
                            BlockPos adjacentPos = pos.relative(direction);
                            if (!(level.getBlockEntity(adjacentPos) instanceof Wire)) {
                                EnergyStorage storage = EnergyStorage.SIDED.find(this.level, adjacentPos, direction.getOpposite());
                                if (storage != null && storage.supportsInsertion()) {
                                    //noinspection Java8MapApi
                                    if (this.wires.get(pos) == null) this.wires.put(pos, new EnergyStorage[6]);
                                    Objects.requireNonNull(this.wires.get(pos))[direction.get3DDataValue()] = storage;
                                }
                            }
                        }
                    }
                    return;
                }
            }
            wire.setNetwork(this);
            this.wires.put(pos, null);
        } else {

        }
    }

    public void addWireRecursive(@NotNull BlockPos pos, @Nullable Wire wire) {
        assert !this.markedForRemoval();
        if (wire == null) {
            wire = (Wire) this.level.getBlockEntity(pos);
        }
        assert wire != null : "Attempted to add wire that does not exist!";
        assert pos.equals(((BlockEntity) wire).getBlockPos());
        if (this.isCompatibleWith(wire)) {
            if (wire.getNetwork() != null) {
                if (wire.getNetwork() != this && !wire.getNetwork().markedForRemoval()) {
                    wire.getNetwork().markForRemoval();
                    this.wires.putAll(((WireNetworkImpl) wire.getNetwork()).wires);
                }
            }
            wire.setNetwork(this);
            this.wires.put(pos, null);

            for (Direction direction : Constant.Misc.DIRECTIONS) {
                if (wire.canConnect(direction)) {
                    BlockPos adjacentPos = pos.relative(direction);
                    BlockEntity blockEntity = level.getBlockEntity(adjacentPos);
                    if (blockEntity != null && !blockEntity.isRemoved()) {
                        if (blockEntity instanceof Wire adjacent) {
                            if (adjacent.getNetwork() != this && adjacent.canConnect(direction.getOpposite())) {
                                this.addWireRecursive(adjacentPos, adjacent);
                            }
                            continue;
                        }
                    }

                    EnergyStorage storage = EnergyStorage.SIDED.find(this.level, adjacentPos, direction.getOpposite());
                    if (storage != null && storage.supportsInsertion()) {
                        //noinspection Java8MapApi
                        if (this.wires.get(pos) == null) this.wires.put(pos, new EnergyStorage[6]);
                        Objects.requireNonNull(this.wires.get(pos))[direction.get3DDataValue()] = storage;
                    }
                }
            }
        } else {

        }
    }

    @Override
    public void removeWire(@NotNull BlockPos removedPos) {
        if (!this.level.isLoaded(removedPos)) {
            Constant.LOGGER.debug("Removing wire from unloaded chunk, removing entire network");
            this.markForRemoval();
            return;
        }

        assert !this.markedForRemoval();
        assert this.wires.containsKey(removedPos) : "Tried to remove wire that does not exist!";

        this.wires.remove(removedPos);
        if (this.wires.isEmpty()) {
            this.markForRemoval();
            return;
        }

        List<Wire> adjacent = new ArrayList<>(6);

        for (Direction direction : Constant.Misc.DIRECTIONS) {
            BlockPos adjacentWirePos = removedPos.relative(direction);
            if (this.wires.containsKey(adjacentWirePos)) {
                Wire wire1 = (Wire) Objects.requireNonNull(this.level.getBlockEntity(adjacentWirePos));
                if (wire1.canConnect(direction.getOpposite())) {
                    adjacent.add(wire1); // Don't bother testing if it was unable to connect
                }
            }
        }

        assert !adjacent.isEmpty() : "Wire was removed but no adjacent wires were found";
        if (adjacent.size() == 1) {
            return;
        }

        this.markForRemoval();

        for (Wire wire1 : adjacent) {
            if (wire1.getNetwork() == this) {
                wire1.setNetwork(null);
                ((WireBlockEntity) wire1).createNetwork();
            }
        }
    }

    @Override
    public boolean updateConnection(@NotNull BlockPos wirePos, @NotNull BlockPos adjacentPos, Direction direction) {
        assert this.wires.containsKey(wirePos);
        if (this.level.getBlockEntity(adjacentPos) instanceof Wire wire) {
            if (!this.wires.containsKey(adjacentPos)) {
                this.addWire(adjacentPos, wire);
            }
            return true;
        } else {
            if (this.wires.containsKey(adjacentPos)) {
                this.removeWire(adjacentPos);
            }
        }
        if (!this.markedForRemoval) {
            BlockPos vector = adjacentPos.subtract(wirePos);
            assert DirectionUtil.fromNormal(vector.getX(), vector.getY(), vector.getZ()) == direction;
            EnergyStorage storage = EnergyStorage.SIDED.find(this.level, adjacentPos, direction.getOpposite());
            if (storage != null && storage.supportsInsertion()) {
                //noinspection Java8MapApi
                if (this.wires.get(wirePos) == null) this.wires.put(wirePos, new EnergyStorage[6]);
                Objects.requireNonNull(this.wires.get(wirePos))[direction.get3DDataValue()] = storage;
                return true;
            }

            if (this.wires.get(wirePos) != null) {
                Objects.requireNonNull(this.wires.get(wirePos))[direction.get3DDataValue()] = null;
            }
        }
        return false;
    }

    @Override
    public long insert(long amount, @NotNull TransactionContext transaction) {
        if (this.tickId != (this.tickId = level.getServer().getTickCount())) {
            this.transferred = 0;
        }
        amount = Math.min(amount, this.getMaxTransferRate() - this.transferred);
        if (amount <= 0) return amount;

        Object2LongArrayMap<WireNetwork> nonFullInsertables = new Object2LongArrayMap<>(1 + this.peerNetworks.size());
        nonFullInsertables.defaultReturnValue(-1);
        this.getNonFullInsertables(nonFullInsertables, amount, transaction);
        long requested = 0;
        LongIterator it = nonFullInsertables.values().longIterator();
        while (it.hasNext()) {
            requested += it.nextLong();
        }
        if (requested == 0) return 0;
        var ref = new Object() {
            private long available = 0;
        };
        ref.available = amount;
        double ratio = (double)amount / (double)requested;
        if (ratio > 1) ratio = 1;

        long finalAmount = amount;
        double finalRatio = ratio;
        nonFullInsertables.forEach((wireNetwork, integer) -> ref.available = wireNetwork.insertInternal(finalAmount, finalRatio, ref.available, transaction));

        return amount - ref.available;
    }

    @Override
    public long insertInternal(long amount, double ratio, long available, TransactionContext context) {
        if (this.tickId != (this.tickId = level.getServer().getTickCount())) {
            this.transferred = 0;
        }
        long removed = amount - Math.min(amount, this.maxTransferRate - this.transferred);
        amount -= removed;
        for (EnergyStorage[] storages : this.wires.values()) {
            if (storages != null) {
                for (EnergyStorage storage : storages) {
                    if (storage != null) {
                        if (!storage.supportsInsertion()) continue; // why can't I call this?e
                        long consumed = Math.min(Math.min(available, (long) (amount * ratio)), this.getMaxTransferRate() - this.transferred);
                        if (consumed == 0) continue;
                        long inserted;
                        try (Transaction transaction = Transaction.openNested(context)) {
                            inserted = storage.insert(consumed, transaction);
                            transaction.commit();
                        }
                        available -= inserted;
                        this.transferred += inserted;
                    }
                }
            }
        }
        return available + removed;
    }

    @Override
    public void getNonFullInsertables(Object2LongMap<WireNetwork> energyRequirement, long amount, @NotNull TransactionContext transaction) {
        if (this.tickId != (this.tickId = level.getServer().getTickCount())) {
            this.transferred = 0;
        }
        amount = Math.min(amount, this.maxTransferRate - this.transferred);
        if (energyRequirement.putIfAbsent(this, 0) == -1) {
            long requested = 0;

            for (EnergyStorage[] storages : this.wires.values()) {
                if (storages != null) {
                    for (EnergyStorage storage : storages) {
                        if (storage != null) {
                            try (Transaction simulation = Transaction.openNested(transaction)) {
                                requested += storage.insert(amount, simulation);
                                simulation.abort();
                            }
                        }
                    }
                }
            }

            for (WireNetwork peerNetwork : this.peerNetworks) {
                if (!energyRequirement.containsKey(peerNetwork)) {
                    peerNetwork.getNonFullInsertables(energyRequirement, amount, transaction);
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
                "world=" + level.dimension().location() +
                ", wires=" + wires +
                ", markedForRemoval=" + markedForRemoval +
                ", maxTransferRate=" + maxTransferRate +
                ", tickId=" + tickId +
                ", transferred=" + transferred +
                '}';
    }
}
