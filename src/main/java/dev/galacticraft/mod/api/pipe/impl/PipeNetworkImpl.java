/*
 * Copyright (c) 2019-2026 Team Galacticraft
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
import dev.galacticraft.mod.api.block.FluidPipeBlock;
import dev.galacticraft.mod.api.pipe.FluidPipe;
import dev.galacticraft.mod.api.pipe.PipeNetwork;
import dev.galacticraft.mod.content.block.special.fluidpipe.PipeBlockEntity;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PipeNetworkImpl extends SnapshotParticipant<PipeNetworkImpl.PipeSnapshot> implements PipeNetwork {
    /**
     * The number of ticks without any transfer after which the pipes of a network are shown empty again.
     */
    private static final int DISPLAY_TIMEOUT = 10;
    /**
     * The networks whose pipes currently show a fluid, so they can be cleared when the flow stops.
     */
    private static final Set<PipeNetworkImpl> DISPLAYING = new ReferenceOpenHashSet<>();

    private final @NotNull ServerLevel level;
    private final @NotNull Object2ObjectOpenHashMap<BlockPos, Storage<FluidVariant> @Nullable []> pipes = new Object2ObjectOpenHashMap<>(1);
    private final long maxTransferRate;
    private boolean activeTransaction = false;
    private boolean markedForRemoval = false;
    private int tickId;
    private long transferred = 0;
    private @Nullable FluidVariant currentVariant = null; //can transfer <maxTransferRate> amount of fluid of 1 type per tick
    private @NotNull FluidVariant displayedVariant = FluidVariant.blank();
    private int lastTransferTick;

    public PipeNetworkImpl(@NotNull ServerLevel level, long maxTransferRate, @NotNull BlockPos pos) {
        this.level = level;
        this.maxTransferRate = maxTransferRate;
        this.tickId = this.level.getServer().getTickCount();
        this.addPipe(pos, null);
    }

    private void addPipe(@NotNull BlockPos pos, @Nullable FluidPipe pipe) {
        assert !this.markedForRemoval;
        if (pipe == null) {
            pipe = (FluidPipe) this.level.getBlockEntity(pos);
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
        if (pipe instanceof PipeBlockEntity pipeBlockEntity) {
            pipeBlockEntity.setDisplayedFluid(this.displayedVariant);
        }

        FluidPipeBlock pipeBlock = (FluidPipeBlock) this.level.getBlockState(pos).getBlock();

        for (Direction direction : Constant.Misc.DIRECTIONS) {
            if (pipe.canConnect(direction)) {
                BlockPos adjacentPos = pos.relative(direction);
                Block adjacentBlock = this.level.getBlockState(adjacentPos).getBlock();
                BlockEntity blockEntity = level.getBlockEntity(adjacentPos);
                if (blockEntity != null && !blockEntity.isRemoved()) {
                    if (blockEntity instanceof FluidPipe adjacent && adjacentBlock instanceof FluidPipeBlock adjacentPipe) {
                        if (!pipeBlock.color.canConnectTo(adjacentPipe.color)) continue;
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

        List<FluidPipe> adjacent = new ArrayList<>(6);

        for (Direction direction : Constant.Misc.DIRECTIONS) {
            BlockPos adjacentPipePos = removedPos.relative(direction);
            if (this.pipes.containsKey(adjacentPipePos)) {
                FluidPipe pipe1 = (FluidPipe) Objects.requireNonNull(this.level.getBlockEntity(adjacentPipePos));
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

        for (FluidPipe pipe1 : adjacent) {
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

        if (this.level.getBlockEntity(adjacentPos) instanceof FluidPipe pipe && this.isCompatibleWith(pipe)) {
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

        double ratio = Math.min(1.0, (double) amount / (double) totalRequested);
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
    protected void onFinalCommit() {
        if (this.transferred > 0 && this.currentVariant != null) {
            this.lastTransferTick = this.tickId;
            if (!this.currentVariant.equals(this.displayedVariant)) {
                this.setDisplayedVariant(this.currentVariant);
            }
        }
    }

    private void setDisplayedVariant(@NotNull FluidVariant variant) {
        this.displayedVariant = variant;
        if (variant.isBlank()) {
            DISPLAYING.remove(this);
        } else {
            DISPLAYING.add(this);
        }

        for (BlockPos pos : this.pipes.keySet()) {
            if (this.level.getBlockEntity(pos) instanceof PipeBlockEntity pipe) {
                // Pipes that have moved to another network show what that network is transferring
                PipeNetwork network = pipe.getNetwork();
                pipe.setDisplayedFluid(network == this || !(network instanceof PipeNetworkImpl other) ? variant : other.displayedVariant);
            }
        }
    }

    /**
     * Shows the pipes of networks that stopped transferring as empty again.
     * Networks that were marked for removal can still be transferring through pipes that weren't moved to another network yet,
     * so they are only cleared once they stop transferring too.
     *
     * @param server the server that is ticking
     */
    public static void tickDisplayedFluids(@NotNull MinecraftServer server) {
        if (DISPLAYING.isEmpty()) return;

        int tick = server.getTickCount();
        for (PipeNetworkImpl network : List.copyOf(DISPLAYING)) {
            if (tick - network.lastTransferTick > DISPLAY_TIMEOUT) {
                network.setDisplayedVariant(FluidVariant.blank());
            }
        }
    }

    /**
     * Forgets the networks of a server that stopped, as they are kept across worlds in singleplayer otherwise.
     */
    public static void clearDisplayedFluids() {
        DISPLAYING.clear();
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
    public boolean isCompatibleWith(@NotNull FluidPipe pipe) {
        return this.getMaxTransferRate() == pipe.getMaxTransferRate();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[").append(pipes.size()).append(";");
        for (BlockPos pos : pipes.keySet()) {
            builder.append(String.format(" (%d,%d,%d),", pos.getX(), pos.getY(), pos.getZ()));
        }
        builder.append("]");
        return "PipeNetworkImpl{" +
                "level=" + level.dimension().location() +
                ", pipes=" + builder +
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

    public record PipeSnapshot(FluidVariant variant, long transferred) {
    }
}