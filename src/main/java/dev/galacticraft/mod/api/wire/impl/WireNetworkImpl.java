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

package dev.galacticraft.mod.api.wire.impl;

import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.Simulation;
import dev.galacticraft.energy.GalacticraftEnergy;
import dev.galacticraft.energy.api.EnergyInsertable;
import dev.galacticraft.energy.impl.DefaultEnergyType;
import dev.galacticraft.energy.impl.RejectingEnergyInsertable;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.api.wire.WireNetwork;
import dev.galacticraft.mod.util.EnergyUtil;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.*;
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
public class WireNetworkImpl implements WireNetwork {
    private final @NotNull ServerWorld world;
    private final @NotNull Object2ObjectOpenHashMap<BlockPos, EnergyInsertable> insertable = new Object2ObjectOpenHashMap<>();
    private final @NotNull ObjectSet<BlockPos> wires = new ObjectLinkedOpenHashSet<>(1);
    private final @NotNull ObjectSet<WireNetwork> peerNetworks = new ObjectLinkedOpenHashSet<>(0);
    private boolean markedForRemoval = false;
    private final int maxTransferRate;
    private int tickId;
    private int transferred = 0;

    public WireNetworkImpl(@NotNull ServerWorld world, int maxTransferRate) {
        this.world = world;
        this.maxTransferRate = maxTransferRate;
        this.tickId = world.getServer().getTicks();
    }

    @Override
    public boolean addWire(@NotNull BlockPos pos, @Nullable Wire wire) {
        assert !this.markedForRemoval();
        if (wire == null) {
            wire = (Wire) world.getBlockEntity(pos);
        }
        assert wire != null : "Attempted to add wire that does not exist!";
        assert pos.equals(((BlockEntity) wire).getPos());
        if (this.isCompatibleWith(wire)) {
            wire.setNetwork(this);
            this.wires.add(pos);
            for (Direction direction : Constant.Misc.DIRECTIONS) {
                if (wire.canConnect(direction)) {
                    BlockEntity blockEntity = world.getBlockEntity(pos.offset(direction));
                    if (blockEntity != null && !blockEntity.isRemoved()) {
                        if (blockEntity instanceof Wire adjacentWire) {
                            if (adjacentWire.canConnect(direction.getOpposite())) {
                                if (this.isCompatibleWith(adjacentWire)) {
                                    if (adjacentWire.getNetwork() == null || adjacentWire.getNetwork().markedForRemoval()) {
                                        this.addWire(pos.offset(direction), adjacentWire);
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
                    EnergyInsertable insertable = EnergyUtil.getEnergyInsertable(world, pos.offset(direction), direction);
                    if (insertable != RejectingEnergyInsertable.NULL) {
                        this.insertable.put(pos.offset(direction), insertable);
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

        this.insertable.putAll(network.getInsertable());
        network.markForRemoval();
    }

    @Override
    public void removeWire(Wire wire, @NotNull BlockPos removedPos) {
        if (this.markedForRemoval()) {
            this.wires.clear();
            Galacticraft.LOGGER.warn("Tried to remove wire from removed network!");
            return;
        }
        assert this.wires.contains(removedPos) : "Tried to remove wire that does not exist!";
        this.wires.remove(removedPos);
        if (this.wires.isEmpty()) {
            this.markForRemoval();
            return;
        }

        List<BlockPos> adjacent = new LinkedList<>();
        this.reattachAdjacent(removedPos, this.insertable, (blockPos, direction) -> GalacticraftEnergy.INSERTABLE.getFirst(this.world, blockPos, SearchOptions.inDirection(direction)), adjacent);
        adjacent.clear();

        for (Direction direction : Constant.Misc.DIRECTIONS) {
            if (wire.canConnect(direction)) {
                BlockPos adjacentWirePos = removedPos.offset(direction);
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
                pos1 = pos.offset(direction);
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
            BlockPos adjacentPos = pos.offset(direction);
            if (map.remove(adjacentPos) != null) {
                for (Direction direction1 : Constant.Misc.DIRECTIONS) {
                    if (direction1 == direction.getOpposite()) continue;
                    if (this.wires.contains(adjacentPos.offset(direction1))) {
                        if (((Wire) world.getBlockEntity(adjacentPos.offset(direction1))).canConnect(direction1.getOpposite())) {
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
        assert !(world.getBlockEntity(updatedPos) instanceof Wire);
        this.insertable.remove(updatedPos);
        BlockPos vector = updatedPos.subtract(adjacentToUpdated);
        Direction direction = Direction.fromVector(vector.getX(), vector.getY(), vector.getZ());
        EnergyInsertable insertable = EnergyUtil.getEnergyInsertable(world, updatedPos, direction);
        if (insertable != RejectingEnergyInsertable.NULL) {
            this.insertable.put(updatedPos, insertable);
            return true;
        }
        return false;
    }

    @Override
    public int insert(@NotNull BlockPos fromWire, int amount, Direction direction, @NotNull Simulation simulate) {
        BlockPos source = fromWire.offset(direction.getOpposite());
        if (this.tickId != (this.tickId = world.getServer().getTicks())) {
            this.transferred = 0;
        }
        amount = Math.min(amount, this.getMaxTransferRate() - this.transferred);
        if (amount <= 0) return amount;

        Object2IntArrayMap<WireNetwork> nonFullInsertables = new Object2IntArrayMap<>(1 + this.peerNetworks.size());
        nonFullInsertables.defaultReturnValue(-1);
        this.getNonFullInsertables(nonFullInsertables, source, amount);
        int requested = 0;
        for (IntIterator it = nonFullInsertables.values().iterator(); it.hasNext(); ) {
            requested += it.nextInt();
        }
        if (requested == 0) return amount;
        var ref = new Object() {
            int available = 0;
        };
        ref.available = amount;
        double ratio = (double)amount / (double)requested;
        if (ratio > 1) ratio = 1;

        int finalAmount = amount;
        double finalRatio = ratio;
        nonFullInsertables.forEach((wireNetwork, integer) -> ref.available = wireNetwork.insertInternal(finalAmount, finalRatio, ref.available, simulate));

        return ref.available;
    }

    @Override
    public int insertInternal(int amount, double ratio, int available, Simulation simulate) {
        if (this.tickId != (this.tickId = world.getServer().getTicks())) {
            this.transferred = 0;
        }
        int removed = amount - Math.min(amount, this.maxTransferRate - this.transferred);
        amount -= removed;
        for (EnergyInsertable insertable : this.insertable.values()) {
            int consumed = Math.min(Math.min(available, (int) (amount * ratio)), this.getMaxTransferRate() - this.transferred);
            if (consumed == 0) continue;
            consumed -= insertable.attemptInsertion(DefaultEnergyType.INSTANCE, consumed, simulate);
            available -= consumed;
            this.transferred += consumed;
        }
        return available + removed;
    }

    @Override
    public void getNonFullInsertables(Object2IntMap<WireNetwork> energyRequirement, BlockPos source, int amount) {
        if (this.tickId != (this.tickId = world.getServer().getTicks())) {
            this.transferred = 0;
        }
        amount = Math.min(amount, this.maxTransferRate - this.transferred);
        if (energyRequirement.putIfAbsent(this, 0) == -1) {
            int requested = 0;
            for (ObjectIterator<Object2ObjectMap.Entry<BlockPos, EnergyInsertable>> it = this.getInsertable().object2ObjectEntrySet().fastIterator(); it.hasNext(); ) {
                Map.Entry<BlockPos, EnergyInsertable> entry = it.next();
                if (entry.getKey().equals(source)) continue;
                int failed = entry.getValue().attemptInsertion(DefaultEnergyType.INSTANCE, amount, Simulation.SIMULATE);
                if (failed != amount) {
                    requested += (amount - failed);
                }
            }
            for (WireNetwork peerNetwork : this.peerNetworks) {
                if (!energyRequirement.containsKey(peerNetwork)) {
                    peerNetwork.getNonFullInsertables(energyRequirement, source, amount);
                }
            }
            energyRequirement.put(this, requested);
        }
    }

    @Override
    public int getMaxTransferRate() {
        return this.maxTransferRate;
    }

    @Override
    public Collection<BlockPos> getAllWires() {
        return this.wires;
    }

    @Override
    public @NotNull Object2ObjectOpenHashMap<BlockPos, EnergyInsertable> getInsertable() {
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
    public boolean isCompatibleWith(Wire wire) {
        return this.getMaxTransferRate() == wire.getMaxTransferRate();
    }

    @Override
    public String toString() {
        return "WireNetworkImpl{" +
                "world=" + world.getRegistryKey().getValue() +
                ", insertable=" + insertable +
                ", wires=" + wires +
                ", markedForRemoval=" + markedForRemoval +
                ", maxTransferRate=" + maxTransferRate +
                ", tickId=" + tickId +
                ", transferred=" + transferred +
                '}';
    }
}
