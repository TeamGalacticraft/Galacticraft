package com.hrznstudio.galacticraft.api.wire;

import alexiil.mc.lib.attributes.Simulation;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.entity.WireBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import io.github.cottonmc.energy.api.EnergyAttribute;
import io.github.cottonmc.energy.api.EnergyAttributeProvider;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class WireNetwork {

    /**
     * A map containing all the networks in the current world.
     * Cleared on world close.
     *
     * @see com.hrznstudio.galacticraft.mixin.ServerWorldMixin
     */
    public static ConcurrentMap<WireNetwork, BlockPos> networkMap = new ConcurrentHashMap<>();

    protected static ConcurrentMap<BlockPos, WireNetwork> networkMap_TEMP = new ConcurrentHashMap<>();

    /**
     * A set containing all the wires inside of a network.
     */
    protected final ConcurrentSet<WireBlockEntity> wires = new ConcurrentSet<>();

    /**
     * The id of this network.
     */
    private UUID id;

    /**
     * Creates a new wire network.
     *
     * @param source The (Wire)BlockEntity that created the network
     */
    public WireNetwork(WireBlockEntity source) {
        id = UUID.randomUUID();
        networkMap_TEMP.put(source.getPos(), this);
        wires.add(source);
    }

    /**
     * Called when a wire is placed.
     */
    public static void blockPlaced() {
        //Every wire in every network
        networkMap.forEach((key, value) -> key.wires.forEach(key::blockPlacedLogic));

        if (!networkMap_TEMP.isEmpty()) {
            networkMap_TEMP.forEach(((blockPos, network) -> networkMap.put(network, blockPos)));
        }
        networkMap_TEMP.clear();
    }

    private void blockPlacedLogic(WireBlockEntity source) {
        List<WireBlockEntity> sourceWires = new ArrayList<>();
        sourceWires.add(source);
        do {
            for (WireBlockEntity wire : WireUtils.getAdjacentWires(sourceWires.get(0).getPos(), sourceWires.get(0).getWorld())) {
                if (wire != null) {
                    if (wire.networkId != this.getId()) {
                        this.wires.add(wire);
                        try {
                            if (WireUtils.getNetworkFromId(wire.networkId) != null) {
                                WireNetwork network = WireUtils.getNetworkFromId(wire.networkId);
                                networkMap.remove(WireUtils.getNetworkFromId(wire.networkId));
                                for (WireBlockEntity blockEntity : network.wires) {
                                    blockEntity.networkId = this.getId();
                                }
                            }
                        } catch (NullPointerException ignore) {
                        }

                        if (networkMap_TEMP.get(wire.getPos()) != null) {
                            networkMap_TEMP.remove(wire.getPos());
                        }

                        wire.networkId = this.getId();
                        sourceWires.add(wire);
                    }
                }
            }
            BlockEntity e = sourceWires.get(0);
            sourceWires.remove(e);
        } while (sourceWires.size() > 0);
    }

    /**
     * Handles the energy transfer in a network.
     * Runs every tick.
     */
    public void update() {
        ConcurrentSet<BlockEntity> consumerPowerRequirement = new ConcurrentSet<>();
        ConcurrentSet<BlockEntity> producerPower = new ConcurrentSet<>();
        int energyAvailable = 0;
        int energyNeeded = 0;
        int energyLeft = 0;

        if (wires.isEmpty()) {
            networkMap.remove(this);
            return;
        }

        for (WireBlockEntity wire : wires) {
            if (!(wire.getWorld().getBlockEntity(wire.getPos()) instanceof WireBlockEntity)) {
                wires.remove(wire);
                Galacticraft.logger.debug("Removed wire at {}.", wire.getPos());
                for (WireBlockEntity blockEntity1 : wires) {
                    blockEntity1.onPlaced();
                }
                wires.clear();
                return;
            } else {
                wire.networkId = getId();
            }
            for (BlockEntity consumer : WireUtils.getAdjacentConsumers(wire.getPos(), wire.getWorld())) {
                if (consumer != null) {
                    EnergyAttribute consumerEnergy = ((EnergyAttributeProvider) consumer).getEnergyAttribute();
                    if (consumerEnergy.getCurrentEnergy() < consumerEnergy.getMaxEnergy()) {
                        consumerPowerRequirement.add(consumer); //Amount the machine needs
                        energyNeeded += (consumerEnergy.getMaxEnergy() - consumerEnergy.getCurrentEnergy());
                    }
                }
            }
            energyLeft = energyNeeded;

            for (BlockEntity producer : WireUtils.getAdjacentProducers(wire.getPos(), wire.getWorld())) {
                if (producer != null) {
                    producerPower.add(producer);
                    energyAvailable += ((EnergyAttributeProvider) producer).getEnergyAttribute().getCurrentEnergy();
                }
            }
        }

        if (energyLeft > 0) {
            int amountPerConsumer = energyAvailable / consumerPowerRequirement.size();
            for (BlockEntity consumer : consumerPowerRequirement) {
                energyAvailable -= amountPerConsumer;
                int amountExtracted = 0;
                for (BlockEntity producer : producerPower) {
                    amountExtracted += ((EnergyAttributeProvider) producer).getEnergyAttribute().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amountPerConsumer - amountExtracted, Simulation.ACTION);

                    if (amountExtracted <= amountPerConsumer) {
                        if (((EnergyAttributeProvider) producer).getEnergyAttribute().getCurrentEnergy() <= 0) {
                            producerPower.remove(producer);
                        }
                    }

                    if (amountExtracted == amountPerConsumer) {
                        break;
                    }
                }
                energyAvailable += ((EnergyAttributeProvider) consumer).getEnergyAttribute().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amountPerConsumer, Simulation.ACTION);
                consumerPowerRequirement.remove(consumer);
            }
        }
    }

    /**
     * @return The ID of the network
     */
    public UUID getId() {
        return id;
    }

}
