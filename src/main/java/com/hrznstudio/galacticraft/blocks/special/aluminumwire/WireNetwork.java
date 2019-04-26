package com.hrznstudio.galacticraft.blocks.special.aluminumwire;

import com.hrznstudio.galacticraft.util.WireConnectable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WireNetwork {

    public static ConcurrentMap<WireNetwork, BlockPos> networkMap = new ConcurrentHashMap<>();
    private static WireNetwork n;
    private int id;
    private ConcurrentMap<BlockEntity, WireConnectionType> wires = new ConcurrentHashMap<>();
    private int z = Integer.MAX_VALUE;

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
                        networkMap.remove(getNetworkFromId(((AluminumWireBlockEntity) entity).networkId)); //remove old one
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
            BlockEntity blockEntity = world.getBlockEntity(getPosFromDirection(direction, pos));

            if (blockEntity == null) {
                continue;
            }

            if (blockEntity instanceof WireConnectable) {
                if (((WireConnectable) blockEntity).canWireConnect(world, direction.getOpposite(), pos, blockEntity.getPos()) == WireConnectionType.ENERGY_INPUT) {
                    adjacentConnections[direction.ordinal()] = blockEntity;
                }
            }
        }
        return adjacentConnections;
    }

    public static BlockEntity[] getAdjacentProducers(BlockPos pos, IWorld world) {
        final BlockEntity[] adjacentConnections = new BlockEntity[6];

        for (Direction direction : Direction.values()) {
            BlockEntity blockEntity = world.getBlockEntity(getPosFromDirection(direction, pos));

            if (blockEntity == null) {
                continue;
            }

            if (blockEntity instanceof WireConnectable) {
                if (((WireConnectable) blockEntity).canWireConnect(world, direction.getOpposite(), pos, blockEntity.getPos()) == WireConnectionType.ENERGY_OUTPUT) {
                    adjacentConnections[direction.ordinal()] = blockEntity;
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

            if (blockEntity instanceof WireConnectable) {
                if (((WireConnectable) blockEntity).canWireConnect(world, direction.getOpposite(), pos, blockEntity.getPos()) == WireConnectionType.WIRE) {
                    adjacentConnections[direction.ordinal()] = blockEntity;
                }
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

    private Map<BlockEntity, WireConnectionType> getWires() {
        return wires;
    }

    public void update() {

    }

    public int getId() {
        return id;
    }

}
