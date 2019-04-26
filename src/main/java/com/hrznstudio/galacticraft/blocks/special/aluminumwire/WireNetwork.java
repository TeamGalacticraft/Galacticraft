package com.hrznstudio.galacticraft.blocks.special.aluminumwire;

import com.hrznstudio.galacticraft.api.entity.WireBlockEntity;
import com.hrznstudio.galacticraft.blocks.machines.MachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.util.WireConnectable;
import io.github.prospector.silk.util.ActionType;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WireNetwork {

    public static ConcurrentMap<WireNetwork, BlockPos> networkMap = new ConcurrentHashMap<>();
    private static WireNetwork n;
    private int id;
    private ConcurrentMap<BlockEntity, WireConnectionType> wires = new ConcurrentHashMap<>();
    private ConcurrentMap<BlockEntity, Integer> energyNeed = new ConcurrentHashMap<>();
    private int energyRequired;
    private int energyFufilled;
    private int z = Integer.MAX_VALUE;
    private ConcurrentMap<BlockEntity, Integer> energy = new ConcurrentHashMap<>();

    WireNetwork(BlockEntity source) {
        networkMap.forEach((wireNetwork, blockPos) -> {
            z = Math.min(wireNetwork.getId(), z);
        });
        networkMap.forEach((wireNetwork, blockPos) -> {
            if (blockPos == source.getPos()) z = wireNetwork.getId();
        });
        if (z == 0x7fffffff) { //No wires (unless there are 2,147,483,647 wire networks :P )
            z = 0;
        }
        id = z;
        z = Integer.MAX_VALUE;
        networkMap.put(this, source.getPos());
        wires.put(source, WireConnectionType.WIRE);
    }

    public static WireNetwork getNetworkFromId(int id) {
        n = null;
        networkMap.forEach((wireNetwork, blockPos) -> {
            if (wireNetwork.id == id) n = wireNetwork;
        });
        return n;
    }

    public static void blockPlaced() {
        System.out.println("Placed!");

        networkMap.forEach((wireNetwork, blockPos) -> {
            wireNetwork.wires.forEach((blockEntity, wireConnectionType) -> {
                BlockEntity[] adjacentWires = getAdjacentWires(blockPos, blockEntity.getWorld());
                for (BlockEntity entity : adjacentWires) {
                    if (entity != null) {
                        wireNetwork.wires.putIfAbsent(entity, WireConnectionType.WIRE);
                        try {
                            networkMap.remove(getNetworkFromId(((AluminumWireBlockEntity) entity).networkId)); //remove old one
                        } catch (NullPointerException ignore) {}
                        ((AluminumWireBlockEntity) entity).networkId = wireNetwork.getId();
                    }
                }

            });
        });
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

    public static BlockEntity[] getAdjacentConsumers(BlockPos pos, IWorld world) {
        final BlockEntity[] adjacentConnections = new BlockEntity[6];

        for (Direction direction : Direction.values()) {
            Block blockEntity = world.getBlockState(getPosFromDirection(direction, pos)).getBlock();
            System.out.println("," + pos.getX() +"," + pos.getY() +","+pos.getZ());

            if (blockEntity == null) {
                continue;
            }

            if (blockEntity instanceof WireConnectable) {
                System.out.println("adhjkhjkahkjhjfdhjafsdhjjhkfd-con");
                if (((WireConnectable) blockEntity).canWireConnect(world, direction, pos, pos) == WireConnectionType.ENERGY_INPUT) {
                    adjacentConnections[direction.ordinal()] = world.getBlockEntity(getPosFromDirection(direction, pos));
                }
            }
        }
        return adjacentConnections;
    }

    public static BlockEntity[] getAdjacentProducers(BlockPos pos, IWorld world) {
        final BlockEntity[] adjacentConnections = new BlockEntity[6];

        for (Direction direction : Direction.values()) {
            Block blockEntity = world.getBlockState(getPosFromDirection(direction, pos)).getBlock();
            System.out.println("," + pos.getX() +"," + pos.getY() +","+pos.getZ());

            if (blockEntity == null) {
                continue;
            }

            if (blockEntity instanceof WireConnectable) {
                System.out.println("adhjkhjkahkjhjfdhjafsdhjjhkfd-pro");
                if (((WireConnectable) blockEntity).canWireConnect(world, direction, pos, pos) == WireConnectionType.ENERGY_OUTPUT) {
                    adjacentConnections[direction.ordinal()] = world.getBlockEntity(getPosFromDirection(direction, pos));
                }
            }
        }
        return adjacentConnections;
    }

    public static BlockEntity[] getAdjacentWires(BlockPos pos, IWorld world) {
        final BlockEntity[] adjacentConnections = new BlockEntity[6];

        for (Direction direction : Direction.values()) {
            BlockEntity blockEntity = world.getBlockEntity(getPosFromDirection(direction, pos));

            if (blockEntity == null) {
                continue;
            }

            if (blockEntity instanceof WireBlockEntity) {
                System.out.println("adhjkhjkahkjhjfdhjafsdhjjhkfd-wire");
                adjacentConnections[direction.ordinal()] = blockEntity;
            }
        }
        return adjacentConnections;
    }

    public static BlockEntity[] getAdjacentConnections(BlockPos pos, IWorld world) {
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

    private Map<BlockEntity, WireConnectionType> getWires() {
        return wires;
    }

    public void update() {
        System.out.println("update");
        energyNeed.clear();
        energy.clear();
        wires.forEach((blockEntity, wireConnectionType) -> {
            if (!(blockEntity.getWorld().getBlockState(blockEntity.getPos()).getBlock() instanceof WireBlockEntity)) {
                wires.remove(blockEntity, wireConnectionType);
            }
            System.out.println("wires.foreach");
            for (BlockEntity consumer : getAdjacentConsumers(blockEntity.getPos(), blockEntity.getWorld())) {
                if (consumer != null) {
                    System.out.println("notnull.foreach");
                    if (((MachineBlockEntity) consumer).getEnergy().getMaxEnergy() <= ((MachineBlockEntity) consumer).getEnergy().getCurrentEnergy()) {
                        if (((MachineBlockEntity) consumer).getEnergy().getMaxEnergy() < ((MachineBlockEntity) consumer).getEnergy().getCurrentEnergy()) {
                            ((MachineBlockEntity) consumer).getEnergy().setCurrentEnergy(((MachineBlockEntity) consumer).getEnergy().getMaxEnergy());
                        }
                    } else {
                        energyNeed.put(consumer, (((MachineBlockEntity) consumer).getEnergy().getMaxEnergy() - ((MachineBlockEntity) consumer).getEnergy().getCurrentEnergy()));
                    }
                }
            }

            for (BlockEntity producer : getAdjacentProducers(blockEntity.getPos(), blockEntity.getWorld())) {
                System.out.println(producer);
                if (producer != null) {
                    System.out.println("for producer");
                    if (((MachineBlockEntity) producer).getEnergy().getCurrentEnergy() != 0) {
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

        energyRequired = 0;
        energyFufilled = 0;
        for (int i : energyNeed.values()) {
            energyRequired += i;
        }
        energy.forEach((blockEntity, integer) -> {
            System.out.println("energy.foreach energyfufilled");
            if (integer > 0) {
                ((MachineBlockEntity)blockEntity).energy.extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, integer, ActionType.PERFORM);
                energyFufilled += integer;
            }
        });
        energyNeed.forEach((blockEntity, integer) -> {
            System.out.println("filling consumer");
            if (integer > 0 ) {
                if (integer <= energyFufilled) {
                    energyFufilled -= integer;
                    ((MachineBlockEntity) blockEntity).energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, integer, ActionType.PERFORM);
                } else {
                    ((MachineBlockEntity) blockEntity).energy.insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, energyFufilled, ActionType.PERFORM);
                    energyFufilled = 0;
                }
            }
        });
    }

    public int getId() {
        return id;
    }

}
