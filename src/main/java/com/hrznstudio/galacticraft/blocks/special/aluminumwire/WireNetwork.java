package com.hrznstudio.galacticraft.blocks.special.aluminumwire;

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.blocks.WireBlock;
import com.hrznstudio.galacticraft.blocks.machines.MachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import io.github.prospector.silk.util.ActionType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WireNetwork {

    public static ConcurrentMap<WireNetwork, BlockPos> networkMap = new ConcurrentHashMap<>();
    private static WireNetwork n;
    private long id;
    private ConcurrentMap<BlockEntity, WireConnectionType> wires = new ConcurrentHashMap<>();
    private ConcurrentMap<BlockEntity, Integer> energyNeed = new ConcurrentHashMap<>();
    private int energyAvailable;
    private int energyFulfilled;
    private int energyLeft;
    private long z;
    private boolean az;
    private ConcurrentMap<BlockEntity, Integer> energy = new ConcurrentHashMap<>();

    WireNetwork(BlockEntity source) {
        z = Long.MAX_VALUE;
        networkMap.forEach((wireNetwork, blockPos) -> z = Math.min(wireNetwork.getId(), z));
        networkMap.forEach((wireNetwork, blockPos) -> {
            if (blockPos == source.getPos()) z = wireNetwork.getId() - 1;
        });
        if (z == Long.MAX_VALUE) { //Nothing is in the networkMap (unless there are 9,223,372,036,854,775,807 (9.223 quintillion) wire networks (PLEASE, NEVER DO THIS)
            z = (-1);
        }
        id = z + 1;
        networkMap.put(this, source.getPos());
        wires.put(source, WireConnectionType.WIRE);
    }

    public static WireNetwork getNetworkFromId(long id) {
        n = null;
        networkMap.forEach((wireNetwork, blockPos) -> {
            if (wireNetwork.id == id) n = wireNetwork;
        });
        return n;
    }

    private static void blockPlacedLogics(WireNetwork wireNetwork, BlockEntity blockEntity) {
        BlockEntity[] adjacentWires = WireUtils.getAdjacentWires(blockEntity.getPos(), blockEntity.getWorld());
        for (BlockEntity entity : adjacentWires) {
            if (entity != null) {
                if (((AluminumWireBlockEntity)entity).getNetworkId() != wireNetwork.getId()) {
                    if (wireNetwork.wires.get(entity) == null) {
                        wireNetwork.wires.put(entity, WireConnectionType.WIRE);
                    } else {
                        wireNetwork.wires.replace(entity, WireConnectionType.WIRE);
                    }
                    try {
                        networkMap.remove(getNetworkFromId(((AluminumWireBlockEntity) entity).networkId)); //remove old one
                    } catch (NullPointerException ignore) {
                    } finally {
                        ((AluminumWireBlockEntity) entity).networkId = wireNetwork.getId(); //NOT COMPATIBLE WITH OTHER MODS (YET)
                        blockPlacedLogics(wireNetwork, entity); //Try this
                    }
                }
            }
        }
    }

    public static void blockPlaced() {
        networkMap.forEach((wireNetwork, blockPos) -> wireNetwork.wires.forEach((blockEntity, wireConnectionType) -> { //Every wire in every network
            blockPlacedLogics(wireNetwork, blockEntity);

        }));
    }

    public void update() {
        energyNeed.clear();
        energy.clear();
        wires.forEach((blockEntity, wireConnectionType) -> Galacticraft.logger.info("Pos: " + blockEntity.getPos() + " ID: " + getId() + " Type: " + wireConnectionType + " BE ID: " + ((AluminumWireBlockEntity)blockEntity).getNetworkId()));
        wires.forEach((blockEntity, wireConnectionType) -> {
            if (!(blockEntity.getWorld().getBlockState(blockEntity.getPos()).getBlock() instanceof WireBlock)) {
                wires.remove(blockEntity, wireConnectionType);
                System.out.println("removed wire at " + blockEntity.getPos());
            }
            for (BlockEntity consumer : WireUtils.getAdjacentConsumers(blockEntity.getPos(), blockEntity.getWorld())) {
                if (consumer != null) {
                    if (((MachineBlockEntity) consumer).getEnergy().getMaxEnergy() <= ((MachineBlockEntity) consumer).getEnergy().getCurrentEnergy()) {
                        if (((MachineBlockEntity) consumer).getEnergy().getMaxEnergy() < ((MachineBlockEntity) consumer).getEnergy().getCurrentEnergy()) {
                            ((MachineBlockEntity) consumer).getEnergy().setCurrentEnergy(((MachineBlockEntity) consumer).getEnergy().getMaxEnergy());
                        }
                    } else {
                        energyNeed.put(consumer, (((MachineBlockEntity) consumer).getEnergy().getMaxEnergy() - ((MachineBlockEntity) consumer).getEnergy().getCurrentEnergy())); //Amount the machine needs
                    }
                }
            }

            for (BlockEntity producer : WireUtils.getAdjacentProducers(blockEntity.getPos(), blockEntity.getWorld())) {
                if (producer != null) {
                    if (((MachineBlockEntity) producer).getEnergy().getCurrentEnergy() > 0) {
                        if (((MachineBlockEntity) producer).getEnergy().getCurrentEnergy() >= 100) {
                            energy.put(producer, 100);
                        } else if (((MachineBlockEntity) producer).getEnergy().getCurrentEnergy() >= 50) {
                            energy.put(producer, 50);
                        } else if (((MachineBlockEntity) producer).getEnergy().getCurrentEnergy() >= 25) {
                            energy.put(producer, 25);
                        }
                        if (((MachineBlockEntity) producer).getEnergy().getCurrentEnergy() >= 10) {
                            energy.put(producer, 10);
                        } else if (((MachineBlockEntity) producer).getEnergy().getCurrentEnergy() >= 1) {
                            energy.put(producer, 1);
                        }
                    }
                }
            }
        });

        energyFulfilled = 0;
        energyAvailable = 0;
        energyLeft = 0;
        for (int i : energy.values()) {
            energyAvailable += i;
        }
        energyNeed.forEach((consumer, energyNeeded) -> {
            energyLeft = energyNeeded;
            if (energyNeeded > 0) {
                if (energyAvailable >= energyNeeded) {
                    for (ConcurrentMap.Entry<BlockEntity, Integer> entry : energy.entrySet()) {
                        if (((MachineBlockEntity) entry.getKey()).energy.getCurrentEnergy() >= energyLeft) {
                            ((MachineBlockEntity) entry.getKey()).energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyLeft, ActionType.PERFORM);
                            if (((MachineBlockEntity) entry.getKey()).getEnergy().getCurrentEnergy() < 1) {
                                ((MachineBlockEntity) entry.getKey()).energy.setCurrentEnergy(0);
                                energy.remove(entry.getKey(), entry.getValue());
                            }
                            break;
                        } else if (((MachineBlockEntity) entry.getKey()).getEnergy().getCurrentEnergy() >= energyLeft) {
                            if (((MachineBlockEntity) entry.getKey()).getEnergy().getCurrentEnergy() > 0) {
                                int e = ((MachineBlockEntity) entry.getKey()).energy.getCurrentEnergy();
                                ((MachineBlockEntity) entry.getKey()).energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, e, ActionType.PERFORM);
                                energyNeeded -= e;
                                energy.remove(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                    energyAvailable -= energyNeeded;
                    ((MachineBlockEntity) consumer).energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyNeeded, ActionType.PERFORM);
                } else {
                    for (ConcurrentMap.Entry<BlockEntity, Integer> entry : energy.entrySet()) {
                        if (((MachineBlockEntity) entry.getKey()).energy.getCurrentEnergy() >= energyLeft) {
                            energyFulfilled += energyNeeded;
                            ((MachineBlockEntity) entry.getKey()).energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyLeft, ActionType.PERFORM);
                            if (((MachineBlockEntity) entry.getKey()).getEnergy().getCurrentEnergy() < 1) {
                                ((MachineBlockEntity) entry.getKey()).energy.setCurrentEnergy(0);
                                energy.remove(entry.getKey(), entry.getValue());
                            }
                            break;
                        } else {
                            if (((MachineBlockEntity) entry.getKey()).getEnergy().getCurrentEnergy() > 0) {
                                energyFulfilled += ((MachineBlockEntity) entry.getKey()).energy.getCurrentEnergy();
                                int e = ((MachineBlockEntity) entry.getKey()).energy.getCurrentEnergy(); //all energy
                                ((MachineBlockEntity) entry.getKey()).energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, e, ActionType.PERFORM);
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
