package com.hrznstudio.galacticraft.blocks.special.aluminumwire;

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.blocks.WireBlock;
import com.hrznstudio.galacticraft.api.entity.WireBlockEntity;
import com.hrznstudio.galacticraft.blocks.machines.MachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.github.prospector.silk.util.ActionType;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

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

    /**
     * A set containing all the wires inside of a network.
     */
    private ConcurrentSet<BlockEntity> wires = new ConcurrentSet<>();

    /**
     * The id of this network.
     */
    private long id;
    private long highestWireId;
    private long lowestWireId;

    private ConcurrentMap<BlockEntity, Integer> energyNeed = new ConcurrentHashMap<>();
    private ConcurrentMap<BlockEntity, Integer> energy = new ConcurrentHashMap<>();

    private int energyAvailable;
    private int energyFulfilled;
    private int energyLeft;

    /**
     * Creates a new wire network.
     *
     * @param source The BlockEntity that created the network
     */
    public WireNetwork(BlockEntity source) {
        highestWireId = Long.MIN_VALUE;
        lowestWireId = Long.MAX_VALUE;
        networkMap.forEach((wireNetwork, blockPos) -> {
            highestWireId = Math.max(wireNetwork.getId(), highestWireId) + 1;
            lowestWireId = Math.min(wireNetwork.getId(), lowestWireId);
        }); //The next one after the lowest
        if (highestWireId == Long.MIN_VALUE) { //Nothing is in the networkMap - Impossible to have negative wire ids.
            highestWireId = 0;
        }
        if (lowestWireId == Long.MAX_VALUE) { //Nothing is in the networkMap - Impossible to have negative wire ids.
            lowestWireId = 0;
        }
        if (lowestWireId > 0) {
            id = lowestWireId - 1;
        } else {
            id = highestWireId;
        }
        networkMap.put(this, source.getPos());
        wires.add(source);
    }

    /**
     * Called when a wire is placed.
     */
    public static void blockPlaced() {
        networkMap.forEach((wireNetwork, blockPos) -> wireNetwork.wires.forEach(wireNetwork::blockPlacedLogic)); //Every wire in every network
    }

    ///**
     //* Called when a wire is broken.
     //* @param pos The position of the removed wire
     //*/
    /*public static void blockBroken(BlockPos pos) {
        networkMap.forEach(((wireNetwork, blockPos) -> {
            System.out.println("nm fe");
            wireNetwork.wires.forEach(blockEntity -> {
                System.out.println("wi fr");
                if (blockEntity.getPos() == pos) {
                    System.out.println("p = po");
                    networkMap.remove(wireNetwork);
                    System.out.println("rem");
                    wireNetwork.wires.forEach(blockEntity1 -> wireNetwork.wires.remove(blockEntity1));
                    System.out.println("wfe r");
                }
            });
        }));
    }*/

    public static void blockBroken(WireNetwork network) {
        if (network != null) {
            System.out.println("notnull");
            network.wires.forEach(blockEntity -> {
                ((WireBlockEntity)blockEntity).onPlaced();
                System.out.println("notnull");
            });
            network.wires.clear();
            networkMap.remove(network);
        }
    }

    private void blockPlacedLogic(BlockEntity source) {
        BlockEntity[] adjacentWires = WireUtils.getAdjacentWires(source.getPos(), source.getWorld());
        for (BlockEntity wire : adjacentWires) {
            if (wire != null) {
                if (((WireBlockEntity) wire).networkId != this.getId()) {
                    if (!this.wires.contains(wire)) {
                        this.wires.add(wire);
                    } else {
                        this.wires.remove(wire); //refresh (is it really necessary?)
                        this.wires.add(wire);
                    }
                    try {
                        networkMap.remove(WireUtils.getNetworkFromId(((WireBlockEntity) wire).networkId)); //remove old one
                    } catch (NullPointerException ignore) {
                    } finally {
                        ((WireBlockEntity) wire).networkId = this.getId();
                        this.blockPlacedLogic(wire); //recursively do this
                    }
                }
            }
        }
    }

    /**
     * Handles the energy transfer in a network.
     * Runs every tick.
     */
    public void update() {
        energyNeed.clear();
        energy.clear();
        energyFulfilled = 0;
        energyAvailable = 0;
        energyLeft = 0;

        wires.forEach((blockEntity) -> {
            if (!(blockEntity.getWorld().getBlockState(blockEntity.getPos()).getBlock() instanceof WireBlock) || blockEntity.getWorld().getBlockEntity(blockEntity.getPos()) == null) {
                wires.remove(blockEntity);
                Galacticraft.logger.debug("Removed wire at {}.", blockEntity.getPos());
            }
            for (BlockEntity consumer : WireUtils.getAdjacentConsumers(blockEntity.getPos(), blockEntity.getWorld())) {
                if (consumer != null) {
                    SimpleEnergyAttribute consumerEnergy = ((MachineBlockEntity) consumer).getEnergy();
                    if (consumerEnergy.getMaxEnergy() <= consumerEnergy.getCurrentEnergy()) {
                        if (consumerEnergy.getMaxEnergy() < consumerEnergy.getCurrentEnergy()) {
                            consumerEnergy.setCurrentEnergy(consumerEnergy.getMaxEnergy());
                        }
                    } else {
                        energyNeed.put(consumer, (consumerEnergy.getMaxEnergy() - consumerEnergy.getCurrentEnergy())); //Amount the machine needs
                    }
                }
            }

            for (BlockEntity producer : WireUtils.getAdjacentProducers(blockEntity.getPos(), blockEntity.getWorld())) {
                if (producer != null) {
                    SimpleEnergyAttribute producerEnergy = ((MachineBlockEntity) producer).getEnergy();
                    if (producerEnergy.getCurrentEnergy() > 0) {
                        if (producerEnergy.getCurrentEnergy() >= 100) {
                            energy.put(producer, 100);
                        } else if (producerEnergy.getCurrentEnergy() >= 50) {
                            energy.put(producer, 50);
                        } else if (producerEnergy.getCurrentEnergy() >= 25) {
                            energy.put(producer, 25);
                        } else if (producerEnergy.getCurrentEnergy() >= 10) {
                            energy.put(producer, 10);
                        } else if (producerEnergy.getCurrentEnergy() >= 1) {
                            energy.put(producer, 1);
                        }
                    }
                }
            }
        });

        for (int amount : energy.values()) {
            energyAvailable += amount;
        }
        energyNeed.forEach((consumer, energyNeeded) -> {
            energyLeft = energyNeeded;
            if (energyNeeded > 0) {
                if (energyAvailable >= energyNeeded) {
                    for (ConcurrentMap.Entry<BlockEntity, Integer> entry : energy.entrySet()) {
                        MachineBlockEntity blockEntity = ((MachineBlockEntity) entry.getKey());
                        if (blockEntity.energy.getCurrentEnergy() >= energyLeft) {
                            blockEntity.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyLeft, ActionType.PERFORM);
                            if (blockEntity.getEnergy().getCurrentEnergy() < 1) {
                                blockEntity.energy.setCurrentEnergy(0);
                                energy.remove(entry.getKey(), entry.getValue());
                            }
                            break;
                        } else if (blockEntity.getEnergy().getCurrentEnergy() >= energyLeft) {
                            if (blockEntity.getEnergy().getCurrentEnergy() > 0) {
                                int amount = blockEntity.energy.getCurrentEnergy();
                                blockEntity.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amount, ActionType.PERFORM);
                                energyNeeded -= amount;
                                energy.remove(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                    energyAvailable -= energyNeeded;
                    ((MachineBlockEntity) consumer).energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyNeeded, ActionType.PERFORM);
                } else {
                    for (ConcurrentMap.Entry<BlockEntity, Integer> entry : energy.entrySet()) {
                        MachineBlockEntity blockEntity = ((MachineBlockEntity) entry.getKey());
                        if (blockEntity.energy.getCurrentEnergy() >= energyLeft) {
                            energyFulfilled += energyNeeded;
                            blockEntity.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyLeft, ActionType.PERFORM);
                            if (blockEntity.getEnergy().getCurrentEnergy() < 1) {
                                blockEntity.energy.setCurrentEnergy(0);
                                energy.remove(entry.getKey(), entry.getValue());
                            }
                            break;
                        } else {
                            if (blockEntity.getEnergy().getCurrentEnergy() > 0) {
                                energyFulfilled += blockEntity.energy.getCurrentEnergy();
                                int amount = blockEntity.energy.getCurrentEnergy(); //all energy
                                blockEntity.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amount, ActionType.PERFORM);
                                energyNeeded -= energyFulfilled;
                                energy.remove(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                    energyAvailable -= energyFulfilled;
                    if (energyAvailable < 0) {
                        energyAvailable = 0;
                    }
                    ((MachineBlockEntity) consumer).energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyFulfilled, ActionType.PERFORM);
                    energyFulfilled = 0;
                }
            }
        });
    }

    /**
     * @return The ID of the network
     */
    public long getId() {
        return id;
    }

}
