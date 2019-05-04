package com.hrznstudio.galacticraft.blocks.special.aluminumwire;

import com.hrznstudio.galacticraft.api.entity.WireBlockEntity;
import com.hrznstudio.galacticraft.util.WireConnectable;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WireUtils {

    public static BlockPos getPosFromDirection(Direction direction, BlockPos pos) {
        if (pos == null || direction == null) return null;
        if (direction == Direction.NORTH) {
            return pos.north();
        } else if (direction == Direction.SOUTH) {
            return pos.south();
        } else if (direction == Direction.EAST) {
            return pos.east();
        } else if (direction == Direction.WEST) {
            return pos.west();
        } else if (direction == Direction.UP) {
            return pos.up();
        } else {
            return pos.down();
        }
    }

    private static WireNetwork network;

    public static WireNetwork getNetworkFromId(long id) {
        network = null;
        WireNetwork.networkMap.forEach((wireNetwork, blockPos) -> {
            if (wireNetwork.getId() == id) network = wireNetwork;
        });
        return network;
    }
    
    public static BlockEntity[] getAdjacentConsumers(BlockPos pos, World world) {
        final BlockEntity[] adjacentConnections = new BlockEntity[6];

        for (Direction direction : Direction.values()) {
            BlockPos adjacentBlockPos = getPosFromDirection(direction, pos);
            Block block = world.getBlockState(adjacentBlockPos).getBlock();

            if (block == null) {
                continue;
            }

            if (block instanceof WireConnectable) {
                if (((WireConnectable) block).canWireConnect(world, direction.getOpposite(), pos, adjacentBlockPos) == WireConnectionType.ENERGY_INPUT) {
                    adjacentConnections[direction.ordinal()] = world.getBlockEntity(getPosFromDirection(direction, pos));
                }
            }
        }
        return adjacentConnections;
    }

    public static BlockEntity[] getAdjacentProducers(BlockPos pos, World world) {

        final BlockEntity[] adjacentConnections = new BlockEntity[6];

        for (Direction direction : Direction.values()) {
            BlockPos adjacentBlockPos = getPosFromDirection(direction, pos);
            Block block = world.getBlockState(adjacentBlockPos).getBlock();

            if (block == null) {
                continue;
            }

            if (block instanceof WireConnectable) {
                if (((WireConnectable) block).canWireConnect(world, direction.getOpposite(), pos, adjacentBlockPos) == WireConnectionType.ENERGY_OUTPUT) {
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
}
