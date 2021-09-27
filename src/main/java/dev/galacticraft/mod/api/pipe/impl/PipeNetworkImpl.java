/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.RejectingFluidInsertable;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.api.pipe.PipeNetwork;
import dev.galacticraft.mod.util.FluidUtil;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluid;
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
    private final @NotNull Object2ObjectOpenHashMap<BlockPos, FluidInsertable> insertable = new Object2ObjectOpenHashMap<>();
    private final @NotNull ObjectSet<BlockPos> pipes = new ObjectLinkedOpenHashSet<>(1);
    private final @NotNull ObjectSet<PipeNetwork> peerNetworks = new ObjectLinkedOpenHashSet<>(0);
    private boolean markedForRemoval = false;
    private final FluidAmount maxTransferRate;
    private int tickId;
    private @NotNull FluidAmount transferred = FluidAmount.ZERO;
    private @Nullable Fluid fluidTransferred = null; //can transfer <maxTransferRate> amount of fluid of 1 type per tick

    public PipeNetworkImpl(@NotNull ServerWorld world, FluidAmount maxTransferRate) {
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
                                        assert adjacentPipe.getNetwork().getMaxTransferRate().equals(this.getMaxTransferRate());
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
                    FluidInsertable insertable = FluidUtil.getInsertable(world, pos.offset(direction), direction);
                    if (insertable != RejectingFluidInsertable.NULL) {
                        this.insertable.put(pos.offset(direction), insertable);
                    }
                }
            }
            return true;
        } else {
            this.peerNetworks.add(pipe.getOrCreateNetwork());
            return true;
        }
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
        this.reattachAdjacent(removedPos, this.insertable, (blockPos, direction) -> FluidAttributes.INSERTABLE.getFirst(this.world, blockPos, SearchOptions.inDirection(direction)), adjacent);
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
        FluidInsertable insertable = FluidUtil.getInsertable(world, updatedPos, direction);
        if (insertable != RejectingFluidInsertable.NULL) {
            this.insertable.put(updatedPos, insertable);
            return true;
        }
        return false;
    }

    @Override
    public FluidVolume insert(@NotNull BlockPos fromPipe, FluidVolume volume, Direction direction, @NotNull Simulation simulate) {
        BlockPos source = fromPipe.offset(direction.getOpposite());
        if (this.tickId != (this.tickId = world.getServer().getTicks())) {
            this.transferred = FluidAmount.ZERO;
            this.fluidTransferred = null;
        } else {
            if (this.fluidTransferred != null && volume.getRawFluid() != this.fluidTransferred || this.transferred.isGreaterThanOrEqual(this.maxTransferRate)) {
                return volume;
            }
        }
        FluidAmount skipped = volume.amount().sub(this.getMaxTransferRate().sub(this.transferred).min(volume.amount()));
        volume = volume.withAmount(volume.amount().sub(skipped));
        if (volume.isEmpty()) return volume.withAmount(volume.amount().add(skipped));

        Object2ObjectArrayMap<PipeNetwork, FluidAmount> nonFullInsertables = new Object2ObjectArrayMap<>(1 + this.peerNetworks.size());
        this.getNonFullInsertables(nonFullInsertables, source, volume);
        FluidAmount requested = FluidAmount.ZERO;
        for (FluidAmount fluidAmount : nonFullInsertables.values()) {
            requested = requested.add(fluidAmount);
        }
        if (requested.isLessThanOrEqual(FluidAmount.ZERO)) return volume.withAmount(volume.amount().add(skipped));
        var ref = new Object() {
            FluidVolume available = FluidVolumeUtil.EMPTY;
        };
        ref.available = volume;
        FluidAmount ratio = volume.amount().div(requested).min(FluidAmount.ONE);

        FluidVolume finalAmount = volume;
        nonFullInsertables.forEach((pipeNetwork, integer) -> ref.available = pipeNetwork.insertInternal(finalAmount, ratio, ref.available, simulate));

        return volume.withAmount(ref.available.amount().add(skipped));
    }

    @Override
    public FluidVolume insertInternal(FluidVolume amount, FluidAmount ratio, FluidVolume available, Simulation simulate) {
        if (this.tickId != (this.tickId = world.getServer().getTicks())) {
            this.transferred = FluidAmount.ZERO;
            this.fluidTransferred = null;
        } else {
            if (this.fluidTransferred != null && amount.getRawFluid() != this.fluidTransferred) {
                return amount;
            }
        }
        final FluidAmount min = amount.amount().min(this.maxTransferRate.sub(this.transferred));
        FluidAmount removed = amount.amount().sub(min);
        amount = amount.withAmount(min);
        for (FluidInsertable insertable : this.insertable.values()) {
            FluidAmount consumed = available.amount().min(amount.amount().mul(ratio)).min(this.getMaxTransferRate().sub(this.transferred));
            FluidAmount skipped = available.amount().mul(ratio).sub(consumed);
            if (consumed.isNegative() || consumed.isZero()) continue;
            consumed = consumed.sub(insertable.attemptInsertion(available.withAmount(consumed), simulate).amount());
            available = available.withAmount(available.amount().sub(consumed).add(skipped));
            this.transferred = this.transferred.add(consumed);
        }
        return available.withAmount(available.amount().add(removed));
    }

    @Override
    public void getNonFullInsertables(Object2ObjectMap<PipeNetwork, FluidAmount> fluidRequirement, BlockPos source, FluidVolume amount) {
        if (this.tickId != (this.tickId = world.getServer().getTicks())) {
            this.transferred = FluidAmount.ZERO;
            this.fluidTransferred = null;
        } else {
            if (this.fluidTransferred != null && amount.getRawFluid() != this.fluidTransferred) {
                fluidRequirement.putIfAbsent(this, FluidAmount.ZERO);
                return;
            }
        }
        final FluidAmount min = amount.amount().min(this.maxTransferRate.sub(this.transferred));
        FluidAmount removed = amount.amount().sub(min);
        amount = amount.withAmount(min);
        if (fluidRequirement.putIfAbsent(this, FluidAmount.ZERO) == null) {
            FluidAmount requested = FluidAmount.ZERO;
            for (ObjectIterator<Object2ObjectMap.Entry<BlockPos, FluidInsertable>> it = this.getInsertable().object2ObjectEntrySet().fastIterator(); it.hasNext(); ) {
                Map.Entry<BlockPos, FluidInsertable> entry = it.next();
                if (entry.getKey().equals(source)) continue;
                FluidAmount failed = entry.getValue().attemptInsertion(amount, Simulation.SIMULATE).amount();
                if (!failed.equals(amount.amount())) {
                    requested = requested.add(amount.amount().sub(failed));
                    if (requested.isGreaterThanOrEqual(this.maxTransferRate.sub(this.transferred))) {
                        requested = this.maxTransferRate.sub(this.transferred);
                        break;
                    }
                }
            }
            for (PipeNetwork peerNetwork : this.peerNetworks) {
                if (!fluidRequirement.containsKey(peerNetwork)) {
                    peerNetwork.getNonFullInsertables(fluidRequirement, source, amount);
                }
            }
            fluidRequirement.put(this, requested);
        }
    }

    @Override
    public FluidAmount getMaxTransferRate() {
        return this.maxTransferRate;
    }

    @Override
    public Collection<BlockPos> getAllPipes() {
        return this.pipes;
    }

    @Override
    public @NotNull Object2ObjectOpenHashMap<BlockPos, FluidInsertable> getInsertable() {
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
        return this.getMaxTransferRate().equals(pipe.getMaxTransferRate());
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