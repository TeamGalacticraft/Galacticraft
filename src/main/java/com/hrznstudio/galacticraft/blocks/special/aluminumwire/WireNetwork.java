package com.hrznstudio.galacticraft.blocks.special.aluminumwire;

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.blocks.WireBlock;
import com.hrznstudio.galacticraft.blocks.machines.MachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import io.github.cottonmc.energy.impl.SimpleEnergyAttribute;
import io.github.prospector.silk.util.ActionType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WireNetwork {

    public static ConcurrentMap<WireNetwork, BlockPos> networkMap = new ConcurrentHashMap<>();
    private long id;
    private ConcurrentMap<BlockEntity, WireConnectionType> wires = new ConcurrentHashMap<>();
    private ConcurrentMap<BlockEntity, Integer> energyNeed = new ConcurrentHashMap<>();
    private int energyAvailable;
    private int energyFulfilled;
    private int energyLeft;
    private long lowestWireId;
    private ConcurrentMap<BlockEntity, Integer> energy = new ConcurrentHashMap<>();

    WireNetwork(BlockEntity source) {
        lowestWireId = Long.MAX_VALUE;
        networkMap.forEach((wireNetwork, blockPos) -> lowestWireId = Math.min(wireNetwork.getId(), lowestWireId));
        networkMap.forEach((wireNetwork, blockPos) -> {
            if (blockPos == source.getPos()) lowestWireId = wireNetwork.getId() - 1;
        });
        if (lowestWireId == Long.MAX_VALUE) { //Nothing is in the networkMap (unless there are 9,223,372,036,854,775,807 (9.223 quintillion) wire networks (PLEASE, NEVER DO THIS)
            lowestWireId = (-1);
        }
        id = lowestWireId + 1;
        networkMap.put(this, source.getPos());
        wires.put(source, WireConnectionType.WIRE);
    }

    private void blockPlacedLogic(BlockEntity blockEntity) {
        BlockEntity[] adjacentWires = WireUtils.getAdjacentWires(blockEntity.getPos(), blockEntity.getWorld());
        for (BlockEntity entity : adjacentWires) {
            if (entity != null) {
                if (((AluminumWireBlockEntity)entity).getNetworkId() != this.getId()) {
                    if (this.wires.get(entity) == null) {
                        this.wires.put(entity, WireConnectionType.WIRE);
                    } else {
                        this.wires.replace(entity, WireConnectionType.WIRE);
                    }
                    try {
                        networkMap.remove(WireUtils.getNetworkFromId(((AluminumWireBlockEntity) entity).networkId)); //remove old one
                    } catch (NullPointerException ignore) {
                    } finally {
                        ((AluminumWireBlockEntity) entity).networkId = this.getId(); //NOT COMPATIBLE WITH OTHER MODS (YET)
                        this.blockPlacedLogic(entity); //recursively do this
                    }
                }
            }
        }
    }

    public static void blockPlaced() {
        networkMap.forEach((wireNetwork, blockPos) -> wireNetwork.wires.forEach((blockEntity, wireConnectionType) -> { //Every wire in every network
            wireNetwork.blockPlacedLogic(blockEntity);
        }));
    }

    /*public boolean addWire(World world, BlockPos pos) {
        if (world.getBlockEntity(pos) == null) return false;
        wires.put(world.getBlockEntity(pos), WireConnectionType.WIRE);
        ((AluminumWireBlockEntity)world.getBlockEntity(pos)).networkId = getId();
        return true;
    }*/

    public void update() {
        energyNeed.clear();
        energy.clear();
        energyFulfilled = 0;
        energyAvailable = 0;
        energyLeft = 0;

        wires.forEach((blockEntity, wireConnectionType) -> Galacticraft.logger.debug("Pos: " + blockEntity.getPos() + " ID: " + getId() + " Type: " + wireConnectionType + " BE ID: " + ((AluminumWireBlockEntity)blockEntity).getNetworkId()));

        wires.forEach((blockEntity, wireConnectionType) -> {
            if (!(blockEntity.getWorld().getBlockState(blockEntity.getPos()).getBlock() instanceof WireBlock)) {
                wires.remove(blockEntity, wireConnectionType);
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
                        }
                        if (producerEnergy.getCurrentEnergy() >= 10) {
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
                    ((MachineBlockEntity)consumer).energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyFulfilled, ActionType.PERFORM);
                    energyFulfilled = 0;
                }
            }
        });
    }

    public long getId() {
        return id;
    }

}
