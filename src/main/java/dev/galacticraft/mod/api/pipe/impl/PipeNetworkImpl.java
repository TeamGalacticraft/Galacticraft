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

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.RejectingFluidInsertable;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.api.pipe.PipeNetwork;
import dev.galacticraft.mod.util.FluidUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
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
    private final ServerWorld world;
    private final Object2ObjectOpenHashMap<BlockPos, FluidInsertable> insertable = new Object2ObjectOpenHashMap<>(0);
    private final ObjectSet<BlockPos> pipes = new ObjectLinkedOpenHashSet<>(1);
    private boolean markedForRemoval = false;

    public PipeNetworkImpl(ServerWorld world) {
        this.world = world;
    }

    @Override
    public void addPipe(@NotNull BlockPos pos, @Nullable Pipe pipe) {
        if (pipe == null) {
            pipe = (Pipe) world.getBlockEntity(pos);
        }
        assert pipe != null : "Attempted to add pipe that does not exist!";
        pipe.setNetwork(this);
        this.pipes.add(pos);
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            BlockEntity entity = world.getBlockEntity(pos.offset(direction));
            if (entity != null && !entity.isRemoved()) {
                if (entity instanceof Pipe pipe1) {
                    if (pipe1.getNetworkNullable() == null || pipe1.getNetwork().markedForRemoval()) {
                        this.addPipe(pos.offset(direction), pipe1);
                    } else {
                        if (pipe1.getNetworkNullable() != this) {
                            this.takeAll(pipe1.getNetwork());
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
    public void removePipe(@NotNull BlockPos pos) {
        assert this.pipes.contains(pos) : "Tried to remove pipe that does not exist!";
        this.pipes.remove(pos);
        if (this.pipes.size() == 0) return;

        List<BlockPos> list = new LinkedList<>();
        this.reattachAdjacent(pos, this.insertable, (blockPos, direction) -> FluidUtil.getInsertable(this.world, blockPos, direction), list);
        list.clear();

        for (Direction direction : Constant.Misc.DIRECTIONS) {
            BlockPos pos1 = pos.offset(direction);
            if (this.pipes.contains(pos1)) {
                if (((Pipe) this.world.getBlockEntity(pos1)).canConnect(direction.getOpposite())) list.add(pos1); //dont bother testing if it was unable to connect
            }
        }
        List<List<BlockPos>> mappedPipes = new LinkedList<>();

        for (BlockPos blockPos : list) {
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

        assert mappedPipes.size() > 0;
        if (mappedPipes.size() == 1) return;
        this.markForRemoval();
        for (List<BlockPos> positions : mappedPipes) {
            PipeNetwork network = new PipeNetworkImpl(this.world);
            network.addPipe(positions.get(0), null);
        }
    }

    private void traverse(List<BlockPos> list, BlockPos pos, @Nullable Direction ignore) {
        BlockPos pos1;
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            if (direction.getOpposite() == ignore) continue;
            pos1 = pos.offset(direction);
            if (this.pipes.contains(pos1)) {
                if (!list.contains(pos1)) {
                    list.add(pos1);
                    this.traverse(list, pos1, direction);
                }
            }
        }
    }

    private <T> void reattachAdjacent(BlockPos pos, Object2ObjectOpenHashMap<BlockPos, T> map, BiFunction<BlockPos, Direction, T> function, List<BlockPos> optionalList) {
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            BlockPos pos1 = pos.offset(direction);
            if (map.remove(pos1) != null) {
                for (Direction direction1 : Constant.Misc.DIRECTIONS) {
                    if (direction1 == direction.getOpposite()) continue;
                    if (this.pipes.contains(pos1.offset(direction1))) {
                        if (((Pipe) this.world.getBlockEntity(pos1)).canConnect(direction1.getOpposite())) {
                            T value = function.apply(pos1, direction1);
                            if (value != null) {
                                optionalList.add(pos1);
                                map.put(pos1, value);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateConnections(@NotNull BlockPos adjacentToUpdated, @NotNull BlockPos updatedPos) {
        if (world.getBlockEntity(updatedPos) instanceof Pipe) return;
        this.insertable.remove(updatedPos);
        BlockPos vector = adjacentToUpdated.subtract(updatedPos);
        Direction direction = Direction.fromVector(vector.getX(), vector.getY(), vector.getZ());
        FluidInsertable insertable = FluidUtil.getInsertable(world, updatedPos, direction);
        if (insertable != RejectingFluidInsertable.NULL) {
            this.insertable.put(updatedPos, insertable);
        }
    }

    @Override
    public FluidVolume insert(@NotNull BlockPos fromPipe, FluidVolume fluid, @NotNull Simulation simulate) {
        if (simulate.isSimulate()) {
            for (FluidInsertable insertable : this.getInsertable().values()) {
                fluid = insertable.attemptInsertion(fluid, simulate);
                if (fluid.isEmpty()) return FluidVolumeUtil.EMPTY;
            }
            return fluid;
        }
        List<FluidInsertable> nonFullInsertables = new ArrayList<>(this.getInsertable().values());
        FluidAmount requested = FluidAmount.ZERO;
        for (FluidInsertable insertable : this.getInsertable().values()) {
            FluidVolume failed = insertable.attemptInsertion(fluid, Simulation.SIMULATE);
            if (failed == fluid) nonFullInsertables.remove(insertable);
            else requested = requested.add(fluid.amount().sub(failed.amount()));
        }
        if (requested.isLessThanOrEqual(FluidAmount.ZERO)) return fluid;
        FluidVolume available = fluid;
        FluidAmount ratio = fluid.amount().div(requested);
        if (ratio.isGreaterThan(FluidAmount.ONE)) ratio = FluidAmount.ONE;

        for (FluidInsertable insertable : nonFullInsertables) {
            FluidAmount consumed = available.amount().min(fluid.amount().mul(ratio));
            consumed.sub(insertable.attemptInsertion(available.withAmount(consumed), simulate).amount());
            available = available.withAmount(available.amount().sub(consumed));
        }

        return available;
    }

    @Override
    public Collection<BlockPos> getAllPipes() {
        return this.pipes;
    }

    @Override
    public Map<BlockPos, FluidInsertable> getInsertable() {
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
    public String toString() {
        return "PipeNetworkImpl{" +
                "world=" + world +
                ", insertable=" + insertable +
                ", pipes=" + pipes +
                ", markedForRemoval=" + markedForRemoval +
                '}';
    }
}
