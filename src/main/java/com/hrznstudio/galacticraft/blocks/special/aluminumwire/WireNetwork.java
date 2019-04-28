package com.hrznstudio.galacticraft.blocks.special.aluminumwire;

import com.hrznstudio.galacticraft.api.blocks.WireBlock;
import com.hrznstudio.galacticraft.api.entity.WireBlockEntity;
import com.hrznstudio.galacticraft.blocks.machines.MachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.util.WireConnectable;
import io.github.prospector.silk.util.ActionType;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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
    private ConcurrentMap<BlockEntity, Integer> energy = new ConcurrentHashMap<>();

    WireNetwork(BlockEntity source) {
        z = Long.MAX_VALUE;
        networkMap.forEach((wireNetwork, blockPos) -> z = Math.min(wireNetwork.getId(), z));
        networkMap.forEach((wireNetwork, blockPos) -> {
            if (blockPos == source.getPos()) z = wireNetwork.getId() - 1;
        });
        if (z == Long.MAX_VALUE) { //Nothing is in the networkMap (unless there are 9,223,372,036,854,775,806* (9.223 quintillion) wire networks (PLEASE, NEVER DO THIS)
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

    public static void blockPlaced() {
        networkMap.forEach((wireNetwork, blockPos) -> wireNetwork.wires.forEach((blockEntity, wireConnectionType) -> { //Every wire in every network
            BlockEntity[] adjacentWires = getAdjacentWires(blockPos, blockEntity.getWorld());
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
                        }
                    }
                }
            }

        }));
    }

    public static BlockPos getPosFromDirection(Direction direction, BlockPos pos) {
        if (direction == Direction.NORTH) {
            return pos.add(0, 0, -1);
        } else if (direction == Direction.SOUTH) {
            return pos.add(0, 0, 1);
        } else if (direction == Direction.EAST) {
            return pos.add(1, 0, 0);
        } else if (direction == Direction.WEST) {
            return pos.add(-1, 0, 0);
        } else if (direction == Direction.UP) {
            return pos.add(0, 1, 0);
        } else if (direction == Direction.DOWN) {
            return pos.add(0, -1, 0);
        } else {
            return null;
        }
    }

    public static BlockEntity[] getAdjacentConsumers(BlockPos pos, World world) {
        final BlockEntity[] adjacentConnections = new BlockEntity[6];

        for (Direction direction : Direction.values()) {
            BlockPos bp = getPosFromDirection(direction, pos);
            Block blockEntity = world.getBlockState(bp).getBlock();

            if (blockEntity == null) {
                continue;
            }

            if (blockEntity instanceof WireConnectable) {
                if (((WireConnectable) blockEntity).canWireConnect(world, direction.getOpposite(), pos, bp) == WireConnectionType.ENERGY_INPUT) {
                    System.out.println("consumer added to adjacent consumers");
                    adjacentConnections[direction.ordinal()] = world.getBlockEntity(getPosFromDirection(direction, pos));
                }
            }
        }
        return adjacentConnections;
    }

    public static BlockEntity[] getAdjacentProducers(BlockPos pos, World world) {

        final BlockEntity[] adjacentConnections = new BlockEntity[6];

        for (Direction direction : Direction.values()) {
            BlockPos bp = getPosFromDirection(direction, pos);
            Block blockEntity = world.getBlockState(bp).getBlock();

            if (blockEntity == null) {
                continue;
            }

            if (blockEntity instanceof WireConnectable) {
                if (((WireConnectable) blockEntity).canWireConnect(world, direction.getOpposite(), pos, bp) == WireConnectionType.ENERGY_OUTPUT) {
                    System.out.println("producer added to adjacent producers");
                    adjacentConnections[direction.ordinal()] = world.getBlockEntity(getPosFromDirection(direction, pos));
                }
            }
        }
        return adjacentConnections;
    }

    public static BlockEntity[] getAdjacentWires(BlockPos pos, World world) {
        final BlockEntity[] adjacentConnections = new BlockEntity[6];

        for (Direction direction : Direction.values()) {
            BlockEntity blockEntity = world.getBlockEntity(getPosFromDirection(direction, pos));

            if (blockEntity == null) {
                continue;
            }

            if (blockEntity instanceof WireBlockEntity) {
                System.out.println("wire added to adjacent wires");
                adjacentConnections[direction.ordinal()] = blockEntity;
            }
        }
        return adjacentConnections;
    }

    public static BlockEntity[] getAdjacentConnections(BlockPos pos, World world) {
        final BlockEntity[] adjacentConnections = new BlockEntity[6];

        for (Direction direction : Direction.values()) {
            BlockEntity blockEntity = world.getBlockEntity(getPosFromDirection(direction, pos));

            if (blockEntity == null) {
                continue;
            }

            if (blockEntity instanceof WireConnectable) {
                if (((WireConnectable) blockEntity).canWireConnect(world, direction.getOpposite(), pos, blockEntity.getPos()) != WireConnectionType.NONE) {
                    adjacentConnections[direction.ordinal()] = blockEntity;
                }
            }
        }
        return adjacentConnections;
    }

    public static BlockPos getPosFromInt(BlockPos pos, int i) {
        if (i == 0) {
            return pos.down();
        } else if (i == 1) {
            return pos.up();
        } else if (i == 2) {
            return pos.north();
        } else if (i == 3) {
            return pos.south();
        } else if (i == 4) {
            return pos.east();
        } else if (i == 5) {
            return pos.west();
        }
        return null;
    }

    public void update() {
        energyNeed.clear();
        energy.clear();
        wires.forEach((blockEntity, wireConnectionType) -> System.out.println("BE: " + blockEntity + " Pos: " + blockEntity.getPos() + " ID: " + getId() + " Type: " + wireConnectionType + " BE ID: " + ((AluminumWireBlockEntity)blockEntity).getNetworkId()));
        wires.forEach((blockEntity, wireConnectionType) -> {
            if (!(blockEntity.getWorld().getBlockState(blockEntity.getPos()).getBlock() instanceof WireBlock)) {
                wires.remove(blockEntity, wireConnectionType);
                System.out.println("removed wire at " + blockEntity.getPos());
            }
            for (BlockEntity consumer : getAdjacentConsumers(blockEntity.getPos(), blockEntity.getWorld())) {
                if (consumer != null) {
                    if (((MachineBlockEntity) consumer).getEnergy().getMaxEnergy() <= ((MachineBlockEntity) consumer).getEnergy().getCurrentEnergy()) {
                        if (((MachineBlockEntity) consumer).getEnergy().getMaxEnergy() < ((MachineBlockEntity) consumer).getEnergy().getCurrentEnergy()) {
                            ((MachineBlockEntity) consumer).getEnergy().setCurrentEnergy(((MachineBlockEntity) consumer).getEnergy().getMaxEnergy());
                        }
                    } else {
                        System.out.println("a");
                        energyNeed.put(consumer, (((MachineBlockEntity) consumer).getEnergy().getMaxEnergy() - ((MachineBlockEntity) consumer).getEnergy().getCurrentEnergy())); //Amount the machine needs
                    }
                }
            }

            for (BlockEntity producer : getAdjacentProducers(blockEntity.getPos(), blockEntity.getWorld())) {
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
            System.err.println("x");
            if (energyNeeded > 0) {
                if (energyAvailable >= energyNeeded) {
                    System.out.println("energyAvailable >= energyNeeded");
                    for (ConcurrentMap.Entry<BlockEntity, Integer> entry : energy.entrySet()) {
                        if (((MachineBlockEntity) entry.getKey()).energy.getCurrentEnergy() >= energyLeft) {
                            ((MachineBlockEntity) entry.getKey()).energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyLeft, ActionType.PERFORM);
                            if (((MachineBlockEntity) entry.getKey()).getEnergy().getCurrentEnergy() < 1) {
                                ((MachineBlockEntity) entry.getKey()).energy.setCurrentEnergy(0);
                                energy.remove(entry.getKey(), entry.getValue());
                            }
                            break;
                        } else if (((MachineBlockEntity) entry.getKey()).getEnergy().getCurrentEnergy() >= energyLeft) {
                            System.out.println("energyAvailable !>= energyNeeded");
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
            } else {
                System.err.println("Energy needed is less than 0!");
            }
        });
        /*energy.forEach((blockEntity, integer) -> {
            if (integer > 0) {
                ((MachineBlockEntity)blockEntity).energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, integer, ActionType.PERFORM);
                energyFulfilled += integer;
            }
        });
        energyNeed.forEach((blockEntity, integer) -> {
            if (integer > 0 ) {
                if (integer <= energyFulfilled) {
                    energyFulfilled -= integer;
                    ((MachineBlockEntity) blockEntity).energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, integer, ActionType.PERFORM);
                } else {
                    ((MachineBlockEntity) blockEntity).energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyFulfilled, ActionType.PERFORM);
                    energyFulfilled = 0;
                }
            }
        });*/
    }

    public long getId() {
        return id;
    }

}
