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

package dev.galacticraft.mod.api.pipe.impl;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.api.pipe.PipeNetwork;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PipeNetworkImpl extends SnapshotParticipant<PipeNetworkImpl.PipeSnapshot> implements PipeNetwork {
    private final @NotNull ServerLevel level;
    private final @NotNull Object2ObjectOpenHashMap<BlockPos, Storage<FluidVariant> @Nullable []> pipes = new Object2ObjectOpenHashMap<>(1);
    private final long maxTransferRate;
    private boolean activeTransaction = false;
    private boolean markedForRemoval = false;
    private int tickId;
    private long transferred = 0;
    private @Nullable FluidVariant currentVariant = null; //can transfer <maxTransferRate> amount of fluid of 1 type per tick

    public PipeNetworkImpl(@NotNull ServerLevel level, long maxTransferRate, @NotNull BlockPos pos) {
        this.level = level;
        this.maxTransferRate = maxTransferRate;
        this.tickId = level.getServer().getTickCount();
        this.addPipe(pos, null);
    }

    private void addPipe(@NotNull BlockPos pos, @Nullable Pipe pipe) {
        assert !this.markedForRemoval;
        if (pipe == null) {
            pipe = (Pipe) this.level.getBlockEntity(pos);
        }
        assert pipe != null : "Attempted to add pipe that does not exist!";
        assert pos.equals(((BlockEntity) pipe).getBlockPos());
        assert this.isCompatibleWith(pipe);

        if (pipe.getNetwork() != null) {
            if (pipe.getNetwork() != this && !pipe.getNetwork().markedForRemoval()) {
                pipe.getNetwork().markForRemoval();
                this.pipes.putAll(((PipeNetworkImpl) pipe.getNetwork()).pipes);
            }
        }
        pipe.setNetwork(this);
        this.pipes.put(pos, null);

        for (Direction direction : Constant.Misc.DIRECTIONS) {
            if (pipe.canConnect(direction)) {
                BlockPos adjacentPos = pos.relative(direction);
                BlockEntity blockEntity = level.getBlockEntity(adjacentPos);
                if (blockEntity != null && !blockEntity.isRemoved()) {
                    if (blockEntity instanceof Pipe adjacent) {
                        if (!pipe.isColorCompatible(adjacent)) continue;
                        if (this.isCompatibleWith(adjacent)) {
                            if (adjacent.getNetwork() != this && adjacent.canConnect(direction.getOpposite())) {
                                this.addPipe(adjacentPos, adjacent);
                            }
                            continue;
                        }
                    }
                }

                Storage<FluidVariant> storage = FluidStorage.SIDED.find(this.level, adjacentPos, direction.getOpposite());
                if (storage != null && storage.supportsInsertion()) {
                    //noinspection Java8MapApi
                    if (this.pipes.get(pos) == null) this.pipes.put(pos, new Storage[6]);
                    Objects.requireNonNull(this.pipes.get(pos))[direction.get3DDataValue()] = storage;
                }
            }
        }
    }
    
    public void removePipe(@NotNull BlockPos removedPos) {
        if (!this.level.isLoaded(removedPos)) {
            Constant.LOGGER.debug("Removing pipe from unloaded chunk, removing entire network");
            this.markForRemoval();
            return;
        }

        assert !this.markedForRemoval;
        assert this.pipes.containsKey(removedPos) : "Tried to remove pipe that does not exist!";

        this.pipes.remove(removedPos);
        if (this.pipes.isEmpty()) {
            this.markForRemoval();
            return;
        }

        List<Pipe> adjacent = new ArrayList<>(6);

        for (Direction direction : Constant.Misc.DIRECTIONS) {
            BlockPos adjacentPipePos = removedPos.relative(direction);
            if (this.pipes.containsKey(adjacentPipePos)) {
                Pipe pipe1 = (Pipe) Objects.requireNonNull(this.level.getBlockEntity(adjacentPipePos));
                if (pipe1.canConnect(direction.getOpposite())) {
                    adjacent.add(pipe1); // Don't bother testing if it was unable to connect
                }
            }
        }

        assert !adjacent.isEmpty() : "Pipe was removed but no adjacent pipes were found";
        if (adjacent.size() == 1) {
            return;
        }

        this.markForRemoval();

        for (Pipe pipe1 : adjacent) {
            if (pipe1.getNetwork() == this) {
                pipe1.setNetwork(null);
                pipe1.forceCreateNetwork();
            }
        }
    }

    @Override
    public void updateConnection(@NotNull BlockPos pipePos, @NotNull BlockPos adjacentPos, @NotNull Direction direction) {
        assert this.pipes.containsKey(pipePos);
        assert !this.markedForRemoval;

        if (this.level.getBlockEntity(adjacentPos) instanceof Pipe pipe && this.isCompatibleWith(pipe)) {
            if (!this.pipes.containsKey(adjacentPos)) {
                this.addPipe(adjacentPos, pipe);
            }
        } else {
            if (this.pipes.containsKey(adjacentPos)) {
                this.removePipe(adjacentPos);
            }

            Storage<FluidVariant> storage = FluidStorage.SIDED.find(this.level, adjacentPos, direction.getOpposite());
            if (storage != null && storage.supportsInsertion()) {
                //noinspection Java8MapApi
                if (this.pipes.get(pipePos) == null) this.pipes.put(pipePos, new Storage[6]);
                Objects.requireNonNull(this.pipes.get(pipePos))[direction.get3DDataValue()] = storage;
            } else if (this.pipes.get(pipePos) != null) {
                Objects.requireNonNull(this.pipes.get(pipePos))[direction.get3DDataValue()] = null;
            }
        }
    }

    @Override
    public long insert(@NotNull FluidVariant resource, long amount, @NotNull TransactionContext transaction) {
        if (this.activeTransaction) return 0;
        this.activeTransaction = true;

        if (this.tickId != level.getServer().getTickCount()) {
            this.tickId = level.getServer().getTickCount();
            this.transferred = 0;
            this.currentVariant = null;
        }

        amount = Math.min(amount, this.maxTransferRate - this.transferred);
        if (amount == 0 || (this.currentVariant != null && !this.currentVariant.equals(resource))) {
            this.activeTransaction = false;
            return 0;
        }

        long totalRequested = 0;
        Object2LongMap<Storage<FluidVariant>> requests = new Object2LongOpenHashMap<>();

        for (Storage<FluidVariant>[] storages : this.pipes.values()) {
            if (storages != null) {
                for (Storage<FluidVariant> storage : storages) {
                    if (storage != null) {
                        try (Transaction simulation = Transaction.openNested(transaction)) {
                            long inserted = storage.insert(resource, amount, simulation);
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

        this.currentVariant = resource;
        requests.forEach((storage, requested) -> {
            long insert = (long) (requested * ratio);
            if (insert > 0) {
                insert = storage.insert(resource, insert, transaction);
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
    public boolean isCompatibleWith(@NotNull Pipe pipe) {
        return this.getMaxTransferRate() == pipe.getMaxTransferRate();
    }

    @Override
    public String toString() {
        return "PipeNetworkImpl{" +
                "level=" + level.dimension().location() +
                ", pipes=" + pipes +
                ", markedForRemoval=" + markedForRemoval +
                ", maxTransferRate=" + maxTransferRate +
                ", tickId=" + tickId +
                ", transferred=" + transferred +
                ", currentVariant=" + currentVariant +
                '}';
    }

    @Override
    protected PipeSnapshot createSnapshot() {
        return new PipeSnapshot(this.currentVariant, this.transferred);
    }

    @Override
    protected void readSnapshot(PipeSnapshot snapshot) {
        this.currentVariant = snapshot.variant;
        this.transferred = snapshot.transferred;
    }

    @ApiStatus.Internal
    @VisibleForTesting
    public @NotNull Object2ObjectOpenHashMap<BlockPos, Storage<FluidVariant>[]> getPipes() {
        return pipes;
    }

    public record PipeSnapshot(FluidVariant variant, long transferred) {}
}