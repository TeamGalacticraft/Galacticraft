/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import dev.galacticraft.impl.fluid.FluidStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.api.pipe.PipeNetwork;
import it.unimi.dsi.fastutil.objects.*;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class PipeNetworkImpl implements PipeNetwork {
    private final @NotNull ServerWorld world;
    private final @NotNull Object2ObjectOpenHashMap<BlockPos, Storage<FluidVariant>> insertable = new Object2ObjectOpenHashMap<>();
    private final @NotNull ObjectSet<BlockPos> pipes = new ObjectLinkedOpenHashSet<>(1);
    private final @NotNull ObjectSet<PipeNetwork> peerNetworks = new ObjectLinkedOpenHashSet<>(0);
    private boolean markedForRemoval = false;
    private final long maxTransferRate;
    private int tickId;
    private @NotNull long transferred = 0;
    private @Nullable FluidVariant fluidTransferred = null; //can transfer <maxTransferRate> amount of fluid of 1 type per tick

    public PipeNetworkImpl(@NotNull ServerWorld world, long maxTransferRate) {
        this.world = world;
        this.maxTransferRate = maxTransferRate;
        this.tickId = world.getServer().getTicks();
    }

    @Override
    public boolean addPipe(@NotNull BlockPos pos, @Nullable Pipe pipe) {
        assert !this.markedForRemoval();
        if (pipe == null) {
            pipe = (Pipe) world.getBlockEntity(pos);
        }
        assert pipe != null : "Attempted to add pipe that does not exist!";
        assert pos.equals(((BlockEntity) pipe).getPos());
        if (this.isCompatibleWith(pipe)) {
            pipe.setNetwork(this);
            this.pipes.add(pos);
            for (Direction direction : Constant.Misc.DIRECTIONS) {
                if (pipe.canConnect(direction)) {
                    BlockEntity blockEntity = world.getBlockEntity(pos.offset(direction));
                    if (blockEntity != null && !blockEntity.isRemoved()) {
                        if (blockEntity instanceof Pipe adjacentPipe) {
                            if (adjacentPipe.canConnect(direction.getOpposite())) {
                                if (this.isCompatibleWith(adjacentPipe)) {
                                    if (adjacentPipe.getNetwork() == null || adjacentPipe.getNetwork().markedForRemoval()) {
                                        this.addPipe(pos.offset(direction), adjacentPipe);
                                    } else {
                                        assert adjacentPipe.getNetwork().getMaxTransferRate() == this.getMaxTransferRate();
                                        if (adjacentPipe.getNetwork() != this) {
                                            this.takeAll(adjacentPipe.getNetwork());
                                        }
                                    }
                                } else {
                                    this.peerNetworks.add(adjacentPipe.getOrCreateNetwork());
                                }
                            }
                            continue;
                        }
                    }
                    Storage<FluidVariant> insertable = FluidStorage.SIDED.find(world, pos.offset(direction), direction.getOpposite());
                    if (insertable != null) {
                        this.insertable.put(pos.offset(direction), insertable);
                    }
                }
            }
        } else {
            this.peerNetworks.add(pipe.getOrCreateNetwork());
        }
        return true;
    }

    public void takeAll(@NotNull PipeNetwork network) {
        for (BlockPos pos : network.getAllPipes()) {
            BlockEntity entity = this.world.getBlockEntity(pos);
            if (entity instanceof Pipe pipe && !entity.isRemoved()) {
                pipe.setNetwork(this);
                this.pipes.add(pos);
            }
        }

        this.insertable.putAll(network.getInsertable());
        network.markForRemoval();
    }

    @Override
    public void removePipe(Pipe pipe, @NotNull BlockPos removedPos) {
        if (this.markedForRemoval()) {
            this.pipes.clear();
            Galacticraft.LOGGER.warn("Tried to remove pipe from removed network!");
            return;
        }
        assert this.pipes.contains(removedPos) : "Tried to remove pipe that does not exist!";
        this.pipes.remove(removedPos);
        if (this.pipes.isEmpty()) {
            this.markForRemoval();
            return;
        }

        List<BlockPos> adjacent = new LinkedList<>();
        this.reattachAdjacent(removedPos, this.insertable, (blockPos, direction) -> {
            Storage<FluidVariant> storage = FluidStorage.SIDED.find(this.world, blockPos, direction.getOpposite());
            return storage != null && storage.supportsInsertion() ? storage :  null;
        }, adjacent);
        adjacent.clear();

        for (Direction direction : Constant.Misc.DIRECTIONS) {
            if (pipe.canConnect(direction)) {
                BlockPos adjacentPipePos = removedPos.offset(direction);
                if (this.pipes.contains(adjacentPipePos)) {
                    if (((Pipe) Objects.requireNonNull(this.world.getBlockEntity(adjacentPipePos))).canConnect(direction.getOpposite())) {
                        adjacent.add(adjacentPipePos); // Don't bother testing if it was unable to connect
                    }
                }
            }
        }
        List<List<BlockPos>> mappedPipes = new LinkedList<>();

        for (BlockPos blockPos : adjacent) {
            boolean handled = false;
            for (List<BlockPos> mapped : mappedPipes) {
                handled = mapped.contains(blockPos);
                if (handled) break;
            }
            if (handled) continue;
            List<BlockPos> list1 = new LinkedList<>();
            list1.add(blockPos);
            this.traverse(list1, blockPos, null);
            mappedPipes.add(list1);
        }

        assert mappedPipes.size() > 0 : "A pipe was added that should never have been accepted";
        if (mappedPipes.size() == 1) return;
        this.markForRemoval();
        for (List<BlockPos> positions : mappedPipes) {
            PipeNetwork network = PipeNetwork.create(this.world, this.getMaxTransferRate());
            network.addPipe(positions.get(0), null);
            assert network.getAllPipes().containsAll(positions);
        }
    }

    private void traverse(List<BlockPos> list, BlockPos pos, @Nullable Direction ignore) {
        BlockPos pos1;
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            if (direction.getOpposite() == ignore) continue;
            Pipe pipe = (Pipe) world.getBlockEntity(pos);
            if (pipe.canConnect(direction)) {
                pos1 = pos.offset(direction);
                if (this.pipes.contains(pos1)) {
                    if (world.getBlockEntity(pos1) instanceof Pipe pipe1 && pipe1.canConnect(direction.getOpposite())) {
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
            BlockPos adjacentPos = pos.offset(direction);
            if (map.remove(adjacentPos) != null) {
                for (Direction direction1 : Constant.Misc.DIRECTIONS) {
                    if (direction1 == direction.getOpposite()) continue;
                    if (this.pipes.contains(adjacentPos.offset(direction1))) {
                        if (((Pipe) world.getBlockEntity(adjacentPos.offset(direction1))).canConnect(direction1.getOpposite())) {
                            T value = function.apply(adjacentPos, direction1);
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
        assert !(world.getBlockEntity(updatedPos) instanceof Pipe);
        this.insertable.remove(updatedPos);
        BlockPos vector = updatedPos.subtract(adjacentToUpdated);
        Direction direction = Direction.fromVector(vector.getX(), vector.getY(), vector.getZ());
        Storage<FluidVariant> insertable = FluidStorage.SIDED.find(world, updatedPos, direction.getOpposite());
        if (insertable != null) {
            this.insertable.put(updatedPos, insertable);
            return true;
        }
        return false;
    }

    @Override
    public long insert(@NotNull BlockPos fromPipe, FluidStack stack, Direction direction, @NotNull TransactionContext context) {
        BlockPos source = fromPipe.offset(direction.getOpposite());
        if (this.tickId != (this.tickId = world.getServer().getTicks())) {
            this.transferred = 0;
            this.fluidTransferred = null;
        } else {
            if (this.fluidTransferred != null && FluidVariant.of(stack.getFluid(), stack.getNbt()) != this.fluidTransferred || this.transferred >= this.maxTransferRate) {
                return stack.getAmount();
            }
        }
        long skipped = stack.getAmount() - Math.min(this.getMaxTransferRate() - this.transferred, stack.getAmount());
        stack.setAmount(stack.getAmount() - skipped);
        if (stack.isEmpty()) return stack.getAmount() + skipped;

        Object2LongArrayMap<PipeNetwork> nonFullInsertables = new Object2LongArrayMap<>(1 + this.peerNetworks.size());
        this.getNonFullInsertables(nonFullInsertables, source, stack, context);
        long requested = 0;
        for (long fluidAmount : nonFullInsertables.values()) {
            requested = requested + fluidAmount;
        }
        if (requested <= 0) {
            return stack.getAmount() + skipped;
        }
        var ref = new Object() {
            FluidStack available = FluidStack.EMPTY;
        };
        ref.available = stack;
        double ratio = Math.min((double)stack.getAmount() / (double)requested, 1.0);

        nonFullInsertables.forEach((pipeNetwork, integer) -> ref.available = pipeNetwork.insertInternal(stack, ratio, ref.available, context));
        return ref.available.getAmount() + skipped;
    }

    @Override
    public FluidStack insertInternal(FluidStack amount, double ratio, FluidStack available, TransactionContext context) {
        if (this.tickId != (this.tickId = world.getServer().getTicks())) {
            this.transferred = 0;
            this.fluidTransferred = null;
        } else {
            if (this.fluidTransferred != null && amount.getFluid() != this.fluidTransferred) {
                return amount;
            }
        }
        final long min = Math.min(amount.getAmount(), this.maxTransferRate - this.transferred);
        long removed = amount.getAmount() - min;
        amount.setAmount(min);
        for (Storage<FluidVariant> insertable : this.insertable.values()) {
            long consumed = Math.min((long)Math.min(available.getAmount(), amount.getAmount() * ratio), this.getMaxTransferRate() - this.transferred);
            long skipped = (long) (available.getAmount() * ratio) - consumed;
            if (consumed <= 0) continue;
            consumed = insertable.insert(FluidVariant.of(available.getFluid(), available.getNbt()), consumed, context);
             available.setAmount(available.getAmount() - consumed + skipped);
            this.transferred = this.transferred + consumed;
        }
        available.setAmount(available.getAmount() + removed);
        return available;
    }

    @Override
    public void getNonFullInsertables(Object2LongMap<PipeNetwork> fluidRequirement, BlockPos source, FluidStack stack, TransactionContext context) {
        if (this.tickId != (this.tickId = world.getServer().getTicks())) {
            this.transferred = 0;
            this.fluidTransferred = null;
        } else {
            if (this.fluidTransferred != null && FluidVariant.of(stack.getFluid(), stack.getNbt()) != this.fluidTransferred) {
                fluidRequirement.putIfAbsent(this, 0);
                return;
            }
        }
        final long min = Math.min(stack.getAmount(), this.maxTransferRate - this.transferred);
        long removed = stack.getAmount() - min;
        stack.setAmount(min);
        boolean b = fluidRequirement.containsKey(this);
        fluidRequirement.putIfAbsent(this, 0);
        if (!b) {
            long requested = 0;
            for (ObjectIterator<Object2ObjectMap.Entry<BlockPos, Storage<FluidVariant>>> it = this.getInsertable().object2ObjectEntrySet().fastIterator(); it.hasNext(); ) {
                Map.Entry<BlockPos, Storage<FluidVariant>> entry = it.next();
                if (entry.getKey().equals(source)) continue;
                long success = entry.getValue().simulateInsert(FluidVariant.of(stack.getFluid(), stack.getNbt()), stack.getAmount(), context);
                if (success > 0) {
                    requested = requested + success;
                    if (requested >= this.maxTransferRate - this.transferred) {
                        requested = this.maxTransferRate - this.transferred;
                        break;
                    }
                }
            }
            for (PipeNetwork peerNetwork : this.peerNetworks) {
                if (!fluidRequirement.containsKey(peerNetwork)) {
                    peerNetwork.getNonFullInsertables(fluidRequirement, source, stack, context);
                }
            }
            fluidRequirement.put(this, requested);
        }
    }

    @Override
    public long getMaxTransferRate() {
        return this.maxTransferRate;
    }

    @Override
    public Collection<BlockPos> getAllPipes() {
        return this.pipes;
    }

    @Override
    public @NotNull Object2ObjectOpenHashMap<BlockPos, Storage<FluidVariant>> getInsertable() {
        return this.insertable;
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
    public boolean isCompatibleWith(Pipe pipe) {
        return this.getMaxTransferRate() == pipe.getMaxTransferRate();
    }

    @Override
    public String toString() {
        return "PipeNetworkImpl{" +
                "world=" + world.getRegistryKey().getValue() +
                ", insertable=" + insertable +
                ", pipes=" + pipes +
                ", markedForRemoval=" + markedForRemoval +
                ", maxTransferRate=" + maxTransferRate +
                ", tickId=" + tickId +
                ", transferred=" + transferred +
                '}';
    }
}