/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.api.wire;

import alexiil.mc.lib.attributes.Simulation;
import com.hrznstudio.galacticraft.api.block.WireBlock;
import com.hrznstudio.galacticraft.blocks.special.aluminumwire.tier1.AluminumWireBlock;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.util.WireConnectable;
import io.github.cottonmc.energy.api.EnergyAttributeProvider;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class WireNetwork {

    /**
     * A set containing all the wires inside of a network.
     */
    protected final ConcurrentSet<BlockPos> wires = new ConcurrentSet<>();

    private final List<BlockPos> producers = new ArrayList<>();
    private final List<BlockPos> consumers = new ArrayList<>();
    private final List<BlockPos> query = new ArrayList<>();

    /**
     * The id of this network.
     */
    private final UUID id = UUID.randomUUID();
    private final World world;

    /**
     * Creates a new wire network.
     */
    public WireNetwork(World world) {
        this.world = world;
    }

    public WireNetwork join(WireNetwork other) {
        assert this.world.dimension.getType().getRawId() == other.world.dimension.getType().getRawId();
        assert !other.getId().equals(this.getId());
        if (this.wires.size() >= other.wires.size()) {
            for (BlockPos pos : other.wires) {
                if (world.getBlockState(pos).getBlock() instanceof AluminumWireBlock) {
                    NetworkManager.getManagerForWorld(world).replace(pos, this);
                    this.wires.add(pos);
                }
            }
            this.consumers.addAll(other.consumers);
            this.producers.addAll(other.producers);
            this.query.addAll(other.query);
            other.wires.clear();
            other.consumers.clear();
            other.producers.clear();
            other.query.clear();
            return this;
        } else {
            for (BlockPos pos : this.wires) {
                if (world.getBlockState(pos).getBlock() instanceof AluminumWireBlock) {
                    NetworkManager.getManagerForWorld(world).replace(pos, other);
                    other.wires.add(pos);
                }
            }
            other.consumers.addAll(this.consumers);
            other.producers.addAll(this.producers);
            other.query.addAll(this.query);
            this.wires.clear();
            this.consumers.clear();
            this.producers.clear();
            this.query.clear();
            return other;
        }
    }

    public void addProducer(BlockPos pos) {
        this.producers.add(pos);
    }

    public void addConsumer(BlockPos pos) {
        this.consumers.add(pos);
    }

    public boolean removeProducer(BlockPos pos) {
        return this.producers.remove(pos);
    }

    public boolean removeConsumer(BlockPos pos) {
        return this.consumers.remove(pos);
    }

    public void addWire(BlockPos pos) {
        this.wires.add(pos);
        NetworkManager.getManagerForWorld(world).add(pos, this);
    }

    /**
     * Handles the energy transfer in a network.
     * Runs every tick.
     */
    public void tick() {
        for (BlockPos pos : query) {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof WireConnectable) {
                for (Direction dir : Direction.values()) {
                    BlockPos off = pos.offset(dir);
                    if (NetworkManager.getManagerForWorld(world).getNetwork(off).equals(this)) {
                        Block block = world.getBlockState(off).getBlock();
                        if (block instanceof WireBlock && block instanceof WireConnectable) {
                            WireConnectionType type = ((WireConnectable) state.getBlock()).canWireConnect(world, dir, off, pos);
                            if (type != WireConnectionType.NONE) {
                                if (type == WireConnectionType.ENERGY_INPUT) {
                                    this.consumers.add(pos);
                                } else if (type == WireConnectionType.ENERGY_OUTPUT) {
                                    this.producers.add(pos);
                                }
                            }
                        }
                    }
                }
            }
        }
        query.clear();

        if (consumers.size() == 0 || producers.size() == 0) return;
        int energyAvailable = 0;
        Iterator<BlockPos> iterator = producers.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof EnergyAttributeProvider) {
                if (((EnergyAttributeProvider) entity).getEnergyAttribute().canExtractEnergy()) {
                    energyAvailable += ((EnergyAttributeProvider) entity).getEnergyAttribute().getCurrentEnergy();
                } else {
                    iterator.remove();
                    if (consumers.size() == 0 || producers.size() == 0) return;
                }
            } else {
                iterator.remove();
                if (consumers.size() == 0 || producers.size() == 0) return;
            }
        }

        int energyPerMachine = energyAvailable / consumers.size();

        iterator = consumers.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof EnergyAttributeProvider) {
                if (((EnergyAttributeProvider) entity).getEnergyAttribute().canInsertEnergy()) {
                    energyAvailable -= (energyPerMachine - ((EnergyAttributeProvider) entity).getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyPerMachine, Simulation.ACTION));
                } else {
                    iterator.remove();
                    if (consumers.size() == 0 || producers.size() == 0) return;
                }
            } else {
                iterator.remove();
                if (consumers.size() == 0 || producers.size() == 0) return;
            }
            energyPerMachine = energyAvailable / consumers.size();
        }
    }

    /**
     * @return The ID of the network
     */
    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WireNetwork network = (WireNetwork) o;
        return id.equals(network.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void query(BlockPos pos) {
        this.query.add(pos);
    }

    public void removeWire(BlockPos pos) {
        wires.remove(pos);
    }

    public enum WireConnectionType {

        /**
         * The wire is not connected to anything.
         */
        NONE,

        /**
         * The wire is connected to another wire.
         */
        WIRE,

        /**
         * The wire is connected to some sort of energy consuming block.
         */
        ENERGY_INPUT,

        /**
         * The wire is connected to some sort of energy generating block.
         */
        ENERGY_OUTPUT

    }
}
