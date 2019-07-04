package com.hrznstudio.galacticraft.blocks.special.aluminumwire;

import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.entity.WireBlockEntity;
import com.hrznstudio.galacticraft.util.WireConnectable;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 * @see WireNetwork
 */
public class WireUtils {

    private static WireNetwork network;

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

    /**
     * Attempts to find a WireNetwork with a certain ID.
     *
     * @param id The ID of the wanted WireNetwork
     * @return The wire network with the specified ID.
     */
    public static WireNetwork getNetworkFromId(long id) {
        network = null;
        WireNetwork.networkMap.forEach((wireNetwork, blockPos) -> {
            if (wireNetwork.getId() == id) network = wireNetwork;
        });
        return network;
    }

    /**
     * @param pos   The source block's position.
     * @param world The world the block is located in.
     * @return An array of all the adjacent consumers (BlockEntities that consume energy).
     */
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
                    if (world.getBlockEntity(adjacentBlockPos) instanceof ConfigurableElectricMachineBlockEntity) {
                        if (((ConfigurableElectricMachineBlockEntity) world.getBlockEntity(adjacentBlockPos)).active()) {
                            adjacentConnections[direction.getId()] = world.getBlockEntity(getPosFromDirection(direction, pos)); //Don't send energy to block that are not enabled
                        }
                    } else {
                        adjacentConnections[direction.getId()] = world.getBlockEntity(getPosFromDirection(direction, pos));
                    }
                }
            }
        }
        return adjacentConnections;
    }

    /**
     * @param pos   The source block's position.
     * @param world The world the block is located in.
     * @return An array of all the adjacent producers (BlockEntities that produce/generate energy).
     */
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
                    adjacentConnections[direction.getId()] = world.getBlockEntity(getPosFromDirection(direction, pos));
                }
            }
        }
        return adjacentConnections;
    }

    /**
     * @param pos   The source block's position.
     * @param world The world the block is located in.
     * @return An array of all the adjacent wires.
     */
    public static BlockEntity[] getAdjacentWires(BlockPos pos, World world) {
        final BlockEntity[] adjacentConnections = new BlockEntity[6];

        for (Direction direction : Direction.values()) {
            BlockEntity blockEntity = world.getBlockEntity(getPosFromDirection(direction, pos));
            if (blockEntity == null) {
                continue;
            }

            if (blockEntity instanceof WireBlockEntity) {
                adjacentConnections[direction.getId()] = blockEntity;
            }
        }
        return adjacentConnections;
    }

    /**
     * @param pos   The source block's position.
     * @param world The world the block is located in.
     * @return An array of all the adjacent connections
     */
    public static BlockEntity[] getAdjacentConnections(BlockPos pos, World world) {
        final BlockEntity[] adjacentConnections = new BlockEntity[6];

        for (Direction direction : Direction.values()) {
            BlockEntity blockEntity = world.getBlockEntity(getPosFromDirection(direction, pos));

            if (blockEntity == null) {
                continue;
            }

            if (blockEntity instanceof WireConnectable) {
                if (((WireConnectable) blockEntity).canWireConnect(world, direction.getOpposite(), pos, blockEntity.getPos()) != WireConnectionType.NONE) {
                    adjacentConnections[direction.getId()] = blockEntity;
                }
            }
        }
        return adjacentConnections;
    }
}
