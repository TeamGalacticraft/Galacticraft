package com.hrznstudio.galacticraft.api.wire;

import alexiil.mc.lib.attributes.Simulation;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.entity.WireBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private static ConcurrentMap<BlockPos, WireNetwork> networkMap_TEMP = new ConcurrentHashMap<>();

    /**
     * A set containing all the wires inside of a network.
     */
    private ConcurrentSet<BlockEntity> wires = new ConcurrentSet<>();

    /**
     * The id of this network.
     */
    private long id;

    private ConcurrentMap<BlockEntity, Integer> energy = new ConcurrentHashMap<>();

    /**
     * Creates a new wire network.
     *
     * @param source The BlockEntity that created the network
     */
    public WireNetwork(BlockEntity source) {
        long highestWireId = Long.MIN_VALUE;
        long lowestWireId = Long.MAX_VALUE;
        //The next one after the lowest
        for (Map.Entry<WireNetwork, BlockPos> entry : networkMap.entrySet()) {
            WireNetwork wireNetwork = entry.getKey();
            highestWireId = Math.max(wireNetwork.getId(), highestWireId); //This method will only fail if there are already 9,223,372,036,854,775,806 wire networks.
            lowestWireId = Math.min(wireNetwork.getId(), lowestWireId);
        }
        for (Map.Entry<BlockPos, WireNetwork> entry : networkMap_TEMP.entrySet()) {
            WireNetwork wireNetwork = entry.getValue();
            highestWireId = Math.max(wireNetwork.getId(), highestWireId); //This method will only fail if there are already 9,223,372,036,854,775,806 wire networks.
            lowestWireId = Math.min(wireNetwork.getId(), lowestWireId);
        }
        if (highestWireId == Long.MIN_VALUE) { //Nothing is in the networkMap - Impossible to have negative wire ids.
            highestWireId = 0;
        }
        if (lowestWireId == Long.MAX_VALUE) {
            lowestWireId = Long.MIN_VALUE;
        }
        if (lowestWireId > 0) {
            id = lowestWireId - 1;
        } else {
            id = 1 + highestWireId;
        }
        networkMap_TEMP.put(source.getPos(), this);
        wires.add(source);
    }

    /**
     * Called when a wire is placed.
     */
    public static void blockPlaced() {
        //Every wire in every network
        for (Map.Entry<WireNetwork, BlockPos> entry : networkMap.entrySet()) {
            entry.getKey().wires.stream().findFirst().ifPresent(entry.getKey()::blockPlacedLogic);
        }
        if (!networkMap_TEMP.isEmpty()) {
            networkMap_TEMP.forEach(((blockPos, network) -> networkMap.put(network, blockPos)));
        }
        networkMap_TEMP.clear();
    }

    private void blockPlacedLogic(BlockEntity source) {
        List<BlockEntity> sourceWires = new ArrayList<>();
        sourceWires.add(source);
        do {
            for (BlockEntity wire : WireUtils.getAdjacentWires(sourceWires.get(0).getPos(), sourceWires.get(0).getWorld())) {
                if (wire != null) {
                    if (((WireBlockEntity) wire).networkId != this.getId()) {
                        this.wires.add(wire);
                        try {
                            if (WireUtils.getNetworkFromId(((WireBlockEntity) wire).networkId) != null) {
                                WireNetwork network = WireUtils.getNetworkFromId(((WireBlockEntity) wire).networkId);
                                networkMap.remove(WireUtils.getNetworkFromId(((WireBlockEntity) wire).networkId));
                                for (BlockEntity blockEntity : network.wires) {
                                    if (blockEntity instanceof WireBlockEntity) {
                                        ((WireBlockEntity) blockEntity).networkId = this.getId();
                                    }
                                }
                            }
                        } catch (NullPointerException ignore) {
                        }

                        if (networkMap_TEMP.get(wire.getPos()) != null) {
                            networkMap_TEMP.remove(wire.getPos());
                        }

                        ((WireBlockEntity) wire).networkId = this.getId();
//                        this.blockPlacedLogic(wire); //recursively do this
                        sourceWires.add(wire);
                    }
                }
            }
            BlockEntity e = sourceWires.get(0);
            sourceWires.remove(e);
            System.out.println(sourceWires.size());
        } while (sourceWires.size() > 0);
    }

    /**
     * Handles the energy transfer in a network.
     * Runs every tick.
     */
    public void update() {
        ConcurrentMap<BlockEntity, Integer> energyNeed = new ConcurrentHashMap<>();
        energy.clear();
        int energyFulfilled = 0;
        int energyAvailable = 0;
        int energyLeft;

        for (BlockEntity wire : wires) {
            if ((wire.getWorld().getBlockState(wire.getPos()).getBlock() == Blocks.AIR || wire.getWorld().getBlockState(wire.getPos()).getBlock() == Blocks.CAVE_AIR ||
                    wire.getWorld().getBlockState(wire.getPos()).getBlock() == Blocks.VOID_AIR ||
                    wire.getWorld().getBlockState(wire.getPos()).getBlock() == null) ||
                    !(wire.getWorld().getBlockEntity(wire.getPos()) instanceof WireBlockEntity)) {
                wires.remove(wire);
                Galacticraft.logger.debug("Removed wire at {}.", wire.getPos());
                for (BlockEntity blockEntity1 : wires) {
                    ((WireBlockEntity) blockEntity1).onPlaced();
                }
                wires.clear();
                return;
            } else {
                ((WireBlockEntity) wire).networkId = getId();
            }
            for (BlockEntity consumer : WireUtils.getAdjacentConsumers(wire.getPos(), wire.getWorld())) {
                if (consumer != null) {
                    SimpleEnergyAttribute consumerEnergy = ((ConfigurableElectricMachineBlockEntity) consumer).getEnergy();
                    if (consumerEnergy.getMaxEnergy() <= consumerEnergy.getCurrentEnergy()) {
                        if (consumerEnergy.getMaxEnergy() < consumerEnergy.getCurrentEnergy()) {
                            consumerEnergy.setCurrentEnergy(consumerEnergy.getMaxEnergy());
                        }
                    } else {
                        energyNeed.put(consumer, (consumerEnergy.getMaxEnergy() - consumerEnergy.getCurrentEnergy())); //Amount the machine needs
                    }
                }
            }

            for (BlockEntity producer : WireUtils.getAdjacentProducers(wire.getPos(), wire.getWorld())) {
                if (producer != null) {
                    SimpleEnergyAttribute producerEnergy = ((ConfigurableElectricMachineBlockEntity) producer).getEnergy();
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
        }

        for (int amount : energy.values()) {
            energyAvailable += amount;
        }
        for (Map.Entry<BlockEntity, Integer> e : energyNeed.entrySet()) {
            BlockEntity consumer = e.getKey();
            Integer energyNeeded = e.getValue();
            energyLeft = energyNeeded;
            if (energyNeeded > 0) {
                if (energyAvailable >= energyNeeded) {
                    for (ConcurrentMap.Entry<BlockEntity, Integer> entry : energy.entrySet()) {
                        ConfigurableElectricMachineBlockEntity blockEntity = ((ConfigurableElectricMachineBlockEntity) entry.getKey());
                        if (blockEntity.energy.getCurrentEnergy() >= energyLeft) {
                            blockEntity.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyLeft, Simulation.ACTION);
                            if (blockEntity.getEnergy().getCurrentEnergy() < 1) {
                                blockEntity.energy.setCurrentEnergy(0);
                                energy.remove(entry.getKey(), entry.getValue());
                            }
                            break;
                        } else if (blockEntity.getEnergy().getCurrentEnergy() >= energyLeft) {
                            if (blockEntity.getEnergy().getCurrentEnergy() > 0) {
                                int amount = blockEntity.energy.getCurrentEnergy();
                                blockEntity.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amount, Simulation.ACTION);
                                energyNeeded -= amount;
                                energy.remove(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                    energyAvailable -= energyNeeded;
                    ((ConfigurableElectricMachineBlockEntity) consumer).energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyNeeded, Simulation.ACTION);
                } else {
                    for (ConcurrentMap.Entry<BlockEntity, Integer> entry : energy.entrySet()) {
                        ConfigurableElectricMachineBlockEntity blockEntity = ((ConfigurableElectricMachineBlockEntity) entry.getKey());
                        if (blockEntity.energy.getCurrentEnergy() >= energyLeft) {
                            energyFulfilled += energyNeeded;
                            blockEntity.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyLeft, Simulation.ACTION);
                            if (blockEntity.getEnergy().getCurrentEnergy() < 1) {
                                blockEntity.energy.setCurrentEnergy(0);
                                energy.remove(entry.getKey(), entry.getValue());
                            }
                            break;
                        } else {
                            if (blockEntity.getEnergy().getCurrentEnergy() > 0) {
                                energyFulfilled += blockEntity.energy.getCurrentEnergy();
                                int amount = blockEntity.energy.getCurrentEnergy(); //all energy
                                blockEntity.energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, amount, Simulation.ACTION);
                                energyNeeded -= energyFulfilled;
                                energy.remove(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                    energyAvailable -= energyFulfilled;
                    if (energyAvailable < 0) {
                        energyAvailable = 0;
                    }
                    ((ConfigurableElectricMachineBlockEntity) consumer).energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyFulfilled, Simulation.ACTION);
                    energyFulfilled = 0;
                }
            }
        }
    }

    /**
     * @return The ID of the network
     */
    public long getId() {
        return id;
    }

}
