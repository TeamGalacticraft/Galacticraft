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
                    //System.out.println("consumer added to adjacent consumers");
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
                    //System.out.println("producer added to adjacent producers");
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
                //System.out.println("wire added to adjacent wires");
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
