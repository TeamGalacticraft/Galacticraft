package com.hrznstudio.galacticraft.api.block;

import com.hrznstudio.galacticraft.api.configurable.SideOption;
import com.hrznstudio.galacticraft.blocks.special.aluminumwire.WireConnectionType;
import com.hrznstudio.galacticraft.util.WireConnectable;
import javafx.geometry.Side;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;


public abstract class ConfigurableElectricMachineBlock extends BlockWithEntity implements MachineBlock, WireConnectable {

    public ConfigurableElectricMachineBlock(Settings block$Settings_1) {
        super(block$Settings_1);
    }

    public static SideOption[] optionsToArray(BlockState state) {
        if (state.getBlock() instanceof ConfigurableElectricMachineBlock) {
            return new SideOption[]{state.get(EnumProperty.of("north", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock()))),
                    state.get(EnumProperty.of("south", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock()))),
                    state.get(EnumProperty.of("east", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock()))),
                    state.get(EnumProperty.of("west", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock()))),
                    state.get(EnumProperty.of("up", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock()))),
                    state.get(EnumProperty.of("down", SideOption.class, SideOption.getApplicableValuesForMachine(state.getBlock())))
            };
        }
        return new SideOption[] {SideOption.BLANK, SideOption.BLANK, SideOption.BLANK, SideOption.BLANK, SideOption.BLANK, SideOption.BLANK};
    }

    abstract public boolean consumesOxygen();

    abstract public boolean generatesOxygen();

    abstract public boolean consumesPower();

    abstract public boolean generatesPower();

    @Override
    public WireConnectionType canWireConnect(IWorld world, Direction opposite, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        BlockState blockState = world.getBlockState(connectionTargetPos);

        if (opposite == Direction.UP) { //Always the same, no matter what 'facing' is
            if (blockState.get(SideOption.TOP_SIDE_OPTION) == SideOption.POWER_INPUT) {
                return WireConnectionType.ENERGY_INPUT;
            } else if (blockState.get(SideOption.FRONT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                return WireConnectionType.ENERGY_OUTPUT;
            } else {
                return WireConnectionType.NONE;
            }
        } else if (opposite == Direction.DOWN) {
            if (blockState.get(SideOption.BOTTOM_SIDE_OPTION) == SideOption.POWER_INPUT) {
                return WireConnectionType.ENERGY_INPUT;
            } else if (blockState.get(SideOption.BOTTOM_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                return WireConnectionType.ENERGY_OUTPUT;
            } else {
                return WireConnectionType.NONE;
            }
        }

        if (blockState.get(DirectionProperty.of("facing", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) == Direction.NORTH) { //Only N, S, E, W
            if (opposite == Direction.NORTH) {
                if (blockState.get(SideOption.FRONT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.FRONT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.SOUTH) {
                if (blockState.get(SideOption.BACK_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.BACK_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.EAST) {
                if (blockState.get(SideOption.RIGHT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.RIGHT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.WEST) {
                if (blockState.get(SideOption.LEFT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.LEFT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            }

        } else if (blockState.get(DirectionProperty.of("facing", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) == Direction.SOUTH) {
            if (opposite == Direction.NORTH) {
                if (blockState.get(SideOption.BACK_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.BACK_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.SOUTH) {
                if (blockState.get(SideOption.FRONT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.FRONT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.EAST) {
                if (blockState.get(SideOption.LEFT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.LEFT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.WEST) {
                if (blockState.get(SideOption.RIGHT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.RIGHT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            }
        } else if (blockState.get(DirectionProperty.of("facing", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) == Direction.EAST) {
            if (opposite == Direction.NORTH) {
                if (blockState.get(SideOption.RIGHT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.RIGHT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.SOUTH) {
                if (blockState.get(SideOption.LEFT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.LEFT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.EAST) {
                if (blockState.get(SideOption.BACK_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.FRONT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.WEST) {
                if (blockState.get(SideOption.FRONT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.BACK_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            }
        } else if (blockState.get(DirectionProperty.of("facing", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) == Direction.WEST) {
            if (opposite == Direction.NORTH) {
                if (blockState.get(SideOption.LEFT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.LEFT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.SOUTH) {
                if (blockState.get(SideOption.RIGHT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.RIGHT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.EAST) {
                if (blockState.get(SideOption.FRONT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.FRONT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.WEST) {
                if (blockState.get(SideOption.BACK_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(SideOption.BACK_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            }
        }
        return WireConnectionType.NONE;
    }
}
