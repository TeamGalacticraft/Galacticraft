package com.hrznstudio.galacticraft.blocks.special.aluminumwire;

import alexiil.mc.lib.attributes.AttributeProvider;
import com.hrznstudio.galacticraft.api.blocks.MachineBlock;
import com.hrznstudio.galacticraft.api.blocks.WireBlock;
import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.blocks.machines.MachineBlockEntity;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergy;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.util.WireConnectable;
import io.github.cottonmc.energy.api.EnergyType;
import io.github.prospector.silk.util.ActionType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class AluminumWireBlockEntity extends BlockEntity implements Tickable {
    public AluminumWireBlockEntity() {
        super(GalacticraftBlockEntities.ALUMINUM_WIRE_TYPE);
    }

    private WireConnectionType NORTH = WireConnectionType.NONE;
    private WireConnectionType SOUTH = WireConnectionType.NONE;
    private WireConnectionType EAST = WireConnectionType.NONE;
    private WireConnectionType WEST = WireConnectionType.NONE;
    private WireConnectionType UP = WireConnectionType.NONE;
    private WireConnectionType DOWN = WireConnectionType.NONE;

    private int possibleEnergy = 0;

    private Map<Direction, WireConnectionType> connections = new HashMap<>();
    private Map<Direction, Integer> connectionEnergyCount = new HashMap<>();

    private long currentTick = -1;

    @Override
    public void tick() {
        connectionEnergyCount.forEach((direction, integer) -> {
            connectionEnergyCount.replace(direction, 0);
            ((MachineBlockEntity)world.getBlockEntity(getPosFromDirection(direction))).getEnergy().insertEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, integer, ActionType.PERFORM);
        });
        possibleEnergy = 0;
        if (world.getBlockState(pos).getBlock() == GalacticraftBlocks.ALUMINUM_WIRE_BLOCK) {
            for (Direction direction : Direction.values()) {
                if (world.getBlockState(pos).get(BooleanProperty.create("attached_" + direction.getName()))) {
                    if (world.getBlockState(getPosFromDirection(direction)).getBlock() instanceof WireBlock) {
                        connections.replace(direction, WireConnectionType.WIRE);
                    } else if (world.getBlockState(getPosFromDirection(direction)).getBlock() instanceof MachineBlock) {
                        connections.replace(direction, ((WireConnectable)world.getBlockState(getPosFromDirection(direction)).getBlock()).canWireConnect(world, direction.getOpposite(), pos, getPosFromDirection(direction)));
                    } else {
                        connections.replace(direction, WireConnectionType.NONE);
                    }
                } else {
                    connections.replace(direction, WireConnectionType.NONE);
                }
            }

            for (Direction direction : Direction.values()) {
                if (connections.get(direction) == WireConnectionType.ENERGY_OUTPUT) {
                    if (((MachineBlockEntity)world.getBlockEntity(getPosFromDirection(direction))).getEnergy().getCurrentEnergy() > 100) {
                        possibleEnergy += 100;
                        ((MachineBlockEntity)world.getBlockEntity(getPosFromDirection(direction))).getEnergy().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 100, ActionType.PERFORM);
                    } else if (((MachineBlockEntity)world.getBlockEntity(getPosFromDirection(direction))).getEnergy().getCurrentEnergy() > 50) {
                        possibleEnergy += 50;
                        ((MachineBlockEntity)world.getBlockEntity(getPosFromDirection(direction))).getEnergy().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 50, ActionType.PERFORM);
                    } else if (((MachineBlockEntity)world.getBlockEntity(getPosFromDirection(direction))).getEnergy().getCurrentEnergy() > 10) {
                        possibleEnergy += 10;
                        ((MachineBlockEntity)world.getBlockEntity(getPosFromDirection(direction))).getEnergy().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 10, ActionType.PERFORM);
                    } else if (((MachineBlockEntity)world.getBlockEntity(getPosFromDirection(direction))).getEnergy().getCurrentEnergy() > 1) {
                        possibleEnergy += 1;
                        ((MachineBlockEntity)world.getBlockEntity(getPosFromDirection(direction))).getEnergy().extractEnergy(GalacticraftEnergy.GALACTICRAFT_JOULES, 1, ActionType.PERFORM);
                    }
                }
            }
            for (Direction direction : Direction.values()) {
                if (connections.get(direction) == WireConnectionType.WIRE) {
                    ((AluminumWireBlockEntity)world.getBlockEntity(getPosFromDirection(direction))).pushEnergy();
                }
            }
        }
    }

    protected void pushEnergy() {
        if (AWU.getTickID() != currentTick) {
            currentTick = AWU.getTickID();

        }
    }

    protected BlockPos getPosFromDirection(Direction direction) {

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

    protected void init() {
        for (Direction direction : Direction.values()) {
            connections.put(direction, WireConnectionType.NONE);
        }
    }

}
