package com.hrznstudio.galacticraft.api.block;

import com.hrznstudio.galacticraft.api.configurable.SideOption;
import com.hrznstudio.galacticraft.blocks.special.aluminumwire.WireConnectionType;
import com.hrznstudio.galacticraft.util.WireConnectable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;


public abstract class ConfigurableElectricMachineBlock extends BlockWithEntity implements MachineBlock, WireConnectable {

    public ConfigurableElectricMachineBlock(Settings block$Settings_1) {
        super(block$Settings_1);
    }

    public BlockState blankConfig(BlockState state) {
        return state.with(SideOption.FRONT_SIDE_OPTION, SideOption.BLANK).with(SideOption.BACK_SIDE_OPTION, SideOption.BLANK)
                .with(SideOption.RIGHT_SIDE_OPTION, SideOption.BLANK).with(SideOption.LEFT_SIDE_OPTION, SideOption.BLANK)
                .with(SideOption.TOP_SIDE_OPTION, SideOption.BLANK).with(SideOption.BOTTOM_SIDE_OPTION, SideOption.BLANK);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateFactory$Builder_1) {
        super.appendProperties(stateFactory$Builder_1);
        stateFactory$Builder_1.add(SideOption.FRONT_SIDE_OPTION);
        stateFactory$Builder_1.add(SideOption.BACK_SIDE_OPTION);
        stateFactory$Builder_1.add(SideOption.RIGHT_SIDE_OPTION);
        stateFactory$Builder_1.add(SideOption.LEFT_SIDE_OPTION);
        stateFactory$Builder_1.add(SideOption.TOP_SIDE_OPTION);
        stateFactory$Builder_1.add(SideOption.BOTTOM_SIDE_OPTION);
    }

    public static SideOption[] optionsToArray(BlockState state) {
        return new SideOption[]{state.get(SideOption.FRONT_SIDE_OPTION), state.get(SideOption.BACK_SIDE_OPTION), state.get(SideOption.RIGHT_SIDE_OPTION), state.get(SideOption.LEFT_SIDE_OPTION), state.get(SideOption.TOP_SIDE_OPTION), state.get(SideOption.BOTTOM_SIDE_OPTION)};
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
