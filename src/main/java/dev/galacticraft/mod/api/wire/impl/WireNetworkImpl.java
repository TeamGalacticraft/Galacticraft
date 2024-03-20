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
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WireNetworkImpl extends SnapshotParticipant<Long> implements WireNetwork {
    private final @NotNull ServerLevel level;
    private final @NotNull Object2ObjectOpenHashMap<BlockPos, EnergyStorage @Nullable []> wires = new Object2ObjectOpenHashMap<>(1);
    private final long maxTransferRate;
    private boolean markedForRemoval = false;
    private boolean activeTransaction = false;
    private long tickId;
    private long transferred = 0;

    public WireNetworkImpl(@NotNull ServerLevel level, long maxTransferRate, @NotNull BlockPos pos) {
        this.level = level;
        this.maxTransferRate = maxTransferRate;
        this.tickId = this.level.getServer().getTickCount();
        this.addWire(pos, null);
    }

    private void addWire(@NotNull BlockPos pos, @Nullable Wire wire) {
        assert !this.markedForRemoval;
        if (wire == null) {
            wire = (Wire) this.level.getBlockEntity(pos);
        }
        assert wire != null : "Attempted to add wire that does not exist!";
        assert pos.equals(((BlockEntity) wire).getBlockPos());
        assert this.isCompatibleWith(wire);

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
                    if (blockEntity instanceof Wire adjacent && this.isCompatibleWith(adjacent)) {
                        if (adjacent.getNetwork() != this && adjacent.canConnect(direction.getOpposite())) {
                            this.addWire(adjacentPos, adjacent);
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
    }

    public void removeWire(@NotNull BlockPos removedPos) {
        if (!this.level.isLoaded(removedPos)) {
            Constant.LOGGER.debug("Removing wire from unloaded chunk, removing entire network");
            this.markForRemoval();
            return;
        }

        assert !this.markedForRemoval;
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
    public void updateConnection(@NotNull BlockPos wirePos, @NotNull BlockPos adjacentPos, @NotNull Direction direction) {
        assert this.wires.containsKey(wirePos);
        assert !this.markedForRemoval;

        if (this.level.getBlockEntity(adjacentPos) instanceof Wire wire && this.isCompatibleWith(wire)) {
            if (!this.wires.containsKey(adjacentPos)) {
                this.addWire(adjacentPos, wire);
            }
        } else {
            if (this.wires.containsKey(adjacentPos)) {
                this.removeWire(adjacentPos);
            }

            EnergyStorage storage = EnergyStorage.SIDED.find(this.level, adjacentPos, direction.getOpposite());
            if (storage != null && storage.supportsInsertion()) {
                //noinspection Java8MapApi
                if (this.wires.get(wirePos) == null) this.wires.put(wirePos, new EnergyStorage[6]);
                Objects.requireNonNull(this.wires.get(wirePos))[direction.get3DDataValue()] = storage;
            } else if (this.wires.get(wirePos) != null) {
                Objects.requireNonNull(this.wires.get(wirePos))[direction.get3DDataValue()] = null;
            }
        }
    }

    @Override
    public long insert(long amount, @NotNull TransactionContext transaction) {
        if (this.activeTransaction) return 0;
        this.activeTransaction = true;

        if (this.tickId != level.getServer().getTickCount()) {
            this.tickId = level.getServer().getTickCount();
            this.transferred = 0;
        }

        amount = Math.min(amount, this.maxTransferRate - this.transferred);
        if (amount == 0) {
            this.activeTransaction = false;
            return 0;
        }
        long totalRequested = 0;
        Object2LongMap<EnergyStorage> requests = new Object2LongOpenHashMap<>();

        for (EnergyStorage[] storages : this.wires.values()) {
            if (storages != null) {
                for (EnergyStorage storage : storages) {
                    if (storage != null) {
                        try (Transaction simulation = Transaction.openNested(transaction)) {
                            long inserted = storage.insert(amount, simulation);
                            if (inserted > 0) {
                                totalRequested += inserted;
                                requests.put(storage, inserted);
                            }
                            simulation.abort();
                        }
                    }
                }
            }
        }

        if (totalRequested == 0) {
            this.activeTransaction = false;
            return 0;
        }

        double ratio = Math.min(1.0, (double)amount / (double)totalRequested);
        final long baseTransferred = this.transferred;

        this.updateSnapshots(transaction);
        requests.forEach((storage, requested) -> {
            long insert = (long) (requested * ratio);
            if (insert > 0) {
                insert = storage.insert(insert, transaction);
                this.transferred += insert;
            }
        });

        this.activeTransaction = false;
        return this.transferred - baseTransferred;
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
    public boolean isCompatibleWith(@NotNull Wire wire) {
        return this.getMaxTransferRate() == wire.getMaxTransferRate();
    }

    @Override
    public String toString() {
        return "WireNetworkImpl{" +
                "level=" + level.dimension().location() +
                ", wires=" + wires +
                ", markedForRemoval=" + markedForRemoval +
                ", maxTransferRate=" + maxTransferRate +
                ", tickId=" + tickId +
                ", transferred=" + transferred +
                '}';
    }

    @VisibleForTesting
    @ApiStatus.Internal
    public @NotNull Object2ObjectOpenHashMap<BlockPos, EnergyStorage[]> getWires() {
        return wires;
    }

    @Override
    protected Long createSnapshot() {
        return this.transferred;
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        this.transferred = snapshot;
    }
}
