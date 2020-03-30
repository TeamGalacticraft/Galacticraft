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
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.entity.WireBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.api.EnergyAttributeProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class WireNetwork {

    /**
     * A set containing all the wires inside of a network.
     */
    public final LinkedList<BlockPos> wires = new LinkedList<>();

    /**
     * The id of this network.
     */
    private final UUID id = UUID.randomUUID();
    private final World world;
    private boolean resetQueued = false;

    /**
     * Creates a new wire network.
     *
     * @param source The (Wire)BlockEntity that created the network
     */
    public WireNetwork(WireBlockEntity source) {
        assert source != null && !source.isInvalid() && !source.getWorld().isClient;
        this.world = source.getWorld();
        NetworkManager.getManagerForDimension(source.getWorld().dimension.getType()).addNetwork(this);
        wires.add(source.getPos());

    }

    public WireNetwork join(WireNetwork other) {
        assert this.world.dimension.getType().getRawId() == other.world.dimension.getType().getRawId();
        assert !other.getId().equals(this.getId());
        if (this.wires.size() >= other.wires.size()) {
            for (BlockPos pos : other.wires) {
                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof WireBlockEntity && !entity.isInvalid()) {
                    ((WireBlockEntity) world.getBlockEntity(pos)).networkId = this.getId();
                    this.wires.add(pos);
                }
            }
            other.wires.clear();
            NetworkManager.getManagerForDimension(this.world.dimension.getType()).removeNetwork(other.id);
            return this;
        } else {
            for (BlockPos pos : this.wires) {
                BlockEntity entity = world.getBlockEntity(pos);
                if (entity instanceof WireBlockEntity && !entity.isInvalid()) {
                    ((WireBlockEntity) world.getBlockEntity(pos)).networkId = other.getId();
                    other.wires.add(pos);
                }
            }
            this.wires.clear();
            NetworkManager.getManagerForDimension(this.world.dimension.getType()).removeNetwork(this.id);
            return other;
        }
    }

    /**
     * Handles the energy transfer in a network.
     * Runs every tick.
     */
    public void tick() {
        List<BlockEntity> consumers = new LinkedList<>();
        List<BlockEntity> producers = new LinkedList<>();
        List<BlockEntity> storage = new LinkedList<>();
        int energyAvailable = 0;
        int energyNeeded = 0;

        if (wires.isEmpty()) {
            NetworkManager.getManagerForDimension(this.world.dimension.getType()).removeNetwork(this.getId());
            return;
        }

        if (resetQueued) {
            resetQueued = false;
            for (BlockPos pos : wires) {
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof WireBlockEntity && !be.isInvalid()) {
                    ((WireBlockEntity) be).resetNetworkId();
                }
            }

            while (!wires.isEmpty()) {
                BlockEntity be = world.getBlockEntity(wires.pop());
                if (be instanceof WireBlockEntity && !be.isInvalid()) {
                    ((WireBlockEntity) be).onNetworkUpdate();
                }
            }

            NetworkManager.getManagerForDimension(world.dimension.getType()).removeNetwork(this.getId());
            return;
        }

        boolean resetAll = false;

        for (BlockPos pos : wires) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof WireBlockEntity) {
                if (entity.isInvalid()) {
                    Galacticraft.logger.error("An invalid wire was found at {}. This shouldn't have happened!", pos);
                    resetAll = true;
                    continue;
                } else if (!this.getId().equals(((WireBlockEntity) entity).getNetworkId()) && ((WireBlockEntity) entity).getNetworkId() != null) {
                    Galacticraft.logger.warn("A wire with a different network id was found at {}. This shouldn't have happened!", pos);
                    resetQueued = true;
                    NetworkManager.getManagerForDimension(this.world.dimension.getType()).getNetwork(((WireBlockEntity) entity).networkId).resetQueued = true;
                }
                for (BlockEntity consumer : WireUtils.getAdjacentConsumers(pos, world)) {
                    if (consumer != null) {
                        EnergyAttribute consumerEnergy = ((EnergyAttributeProvider) consumer).getEnergyAttribute();
                        if (consumerEnergy.getCurrentEnergy() < consumerEnergy.getMaxEnergy()) {
                            consumers.add(consumer); //Amount the machine needs
                            energyNeeded += (consumerEnergy.getMaxEnergy() - consumerEnergy.getCurrentEnergy());
                        }
                    }
                }

                for (BlockEntity producer : WireUtils.getAdjacentProducers(pos, world)) {
                    if (producer != null) {
                        if (consumers.contains(producer)) {
                            consumers.remove(producer);
                            storage.add(producer);
                        } else {
                            producers.add(producer);
                            energyAvailable += ((EnergyAttributeProvider) producer).getEnergyAttribute().getCurrentEnergy();
                        }
                    }
                }
            } else {
                Galacticraft.logger.error("INVALID WIRE (NOT A WIRE OR NULL)! {}", pos);
                resetAll = true;
            }
        }

        if (resetAll) {
            wireRemoved();
            return;
        }

        List<BlockEntity> cons = new ArrayList<>(consumers);
        List<BlockEntity> prod = new ArrayList<>(producers);
        List<BlockEntity> sto = new ArrayList<>(storage);

        if (energyNeeded > 0) {
            int amountPerConsumer = (consumers.size() > 0 && energyAvailable > 0) ? energyAvailable / consumers.size() : 0;
            for (BlockEntity consumer : cons) {
                energyAvailable -= amountPerConsumer;
                int amountExtracted = 0;
                for (BlockEntity producer : prod) {
                    amountExtracted += ((EnergyAttributeProvider) producer).getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amountPerConsumer - amountExtracted, Simulation.ACTION);

                    if (amountExtracted <= amountPerConsumer) {
                        if (((EnergyAttributeProvider) producer).getEnergyAttribute().getCurrentEnergy() <= 0) {
                            producers.remove(producer);
                        }
                    }

                    if (amountExtracted == amountPerConsumer) {
                        break;
                    }
                }
                energyAvailable += ((EnergyAttributeProvider) consumer).getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amountPerConsumer, Simulation.ACTION);
                if (((EnergyAttributeProvider) consumer).getEnergyAttribute().getCurrentEnergy() >= ((EnergyAttributeProvider) consumer).getEnergyAttribute().getMaxEnergy()) {
                    consumers.remove(consumer);
                }
            }

            cons = new ArrayList<>(consumers);

            if (!consumers.isEmpty() && producers.isEmpty()) {
                energyAvailable = 0;
                for (BlockEntity battery : storage) {
                    if (battery != null) {
                        energyAvailable += ((EnergyAttributeProvider) battery).getEnergyAttribute().getCurrentEnergy();
                    }
                }

                amountPerConsumer = (consumers.size() > 0 && energyAvailable > 0) ? energyAvailable / consumers.size() : 0;
                for (BlockEntity consumer : cons) {
                    energyAvailable -= amountPerConsumer;
                    int amountExtracted = 0;
                    for (BlockEntity battery : sto) {
                        amountExtracted += ((EnergyAttributeProvider) battery).getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amountPerConsumer - amountExtracted, Simulation.ACTION);

                        if (amountExtracted <= amountPerConsumer) {
                            if (((EnergyAttributeProvider) battery).getEnergyAttribute().getCurrentEnergy() <= 0) {
                                storage.remove(battery);
                            }
                        }

                        if (amountExtracted == amountPerConsumer) {
                            break;
                        }
                    }
                    energyAvailable += ((EnergyAttributeProvider) consumer).getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amountPerConsumer, Simulation.ACTION);
                    if (((EnergyAttributeProvider) consumer).getEnergyAttribute().getCurrentEnergy() >= ((EnergyAttributeProvider) consumer).getEnergyAttribute().getMaxEnergy()) {
                        consumers.remove(consumer);
                    }
                }
            } else if (!producers.isEmpty() && consumers.isEmpty()) {
                energyAvailable = 0;
                for (BlockEntity producer : producers) {
                    if (producer != null) {
                        energyAvailable += ((EnergyAttributeProvider) producer).getEnergyAttribute().getCurrentEnergy();
                    }
                }
                prod = new ArrayList<>(producers);
                sto = new ArrayList<>(storage);
                amountPerConsumer = (storage.size() > 0 && energyAvailable > 0) ? energyAvailable / storage.size() : 0;
                for (BlockEntity battery : sto) {
                    energyAvailable -= amountPerConsumer;
                    int amountExtracted = 0;
                    for (BlockEntity producer : prod) {
                        amountExtracted += ((EnergyAttributeProvider) producer).getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amountPerConsumer - amountExtracted, Simulation.ACTION);

                        if (amountExtracted <= amountPerConsumer) {
                            if (((EnergyAttributeProvider) producer).getEnergyAttribute().getCurrentEnergy() <= 0) {
                                producers.remove(producer);
                            }
                        }

                        if (amountExtracted == amountPerConsumer) {
                            break;
                        }
                    }
                    energyAvailable += ((EnergyAttributeProvider) battery).getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amountPerConsumer, Simulation.ACTION);
                    if (((EnergyAttributeProvider) battery).getEnergyAttribute().getCurrentEnergy() >= ((EnergyAttributeProvider) battery).getEnergyAttribute().getMaxEnergy()) {
                        storage.remove(battery);
                    }
                }
            }
        }
    }

    /**
     * @return The ID of the network
     */
    public UUID getId() {
        return id;
    }

    public void wireRemoved() {
        if (!world.isClient) {
            resetQueued = true;
        }
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
