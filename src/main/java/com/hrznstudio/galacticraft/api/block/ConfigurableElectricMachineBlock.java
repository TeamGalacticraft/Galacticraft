/*
 * Copyright (c) 2019 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hrznstudio.galacticraft.api.block;

import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.api.configurable.SideOption;
import com.hrznstudio.galacticraft.api.wire.WireConnectionType;
import com.hrznstudio.galacticraft.util.WireConnectable;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import team.reborn.energy.EnergySide;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
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
        return new SideOption[]{SideOption.BLANK, SideOption.BLANK, SideOption.BLANK, SideOption.BLANK, SideOption.BLANK, SideOption.BLANK};
    }

    public abstract ConfigurableElectricMachineBlockEntity createBlockEntity(BlockView var1);

    public abstract boolean consumesFluids();

    public abstract boolean generatesFluids();

    public abstract boolean consumesOxygen();

    public abstract boolean generatesOxygen();

    public abstract boolean consumesPower();

    public abstract boolean generatesPower();

    @Override
    public boolean hasBlockEntity() {
        return true;
    }

    @Nonnull
    @Override
    public WireConnectionType canWireConnect(IWorld world, Direction opposite, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        List<SideOption> values = SideOption.getApplicableValuesForMachine(world.getBlockState(connectionTargetPos).getBlock());

        EnumProperty<SideOption> FRONT_SIDE_OPTION = EnumProperty.of("north", SideOption.class, values);
        EnumProperty<SideOption> BACK_SIDE_OPTION = EnumProperty.of("south", SideOption.class, values);
        EnumProperty<SideOption> RIGHT_SIDE_OPTION = EnumProperty.of("east", SideOption.class, values);
        EnumProperty<SideOption> LEFT_SIDE_OPTION = EnumProperty.of("west", SideOption.class, values);
        EnumProperty<SideOption> TOP_SIDE_OPTION = EnumProperty.of("up", SideOption.class, values);
        EnumProperty<SideOption> BOTTOM_SIDE_OPTION = EnumProperty.of("down", SideOption.class, values);

        BlockState blockState = world.getBlockState(connectionTargetPos);

        if (opposite == Direction.UP) { //Always the same, no matter what 'facing' is
            if (blockState.get(TOP_SIDE_OPTION) == SideOption.POWER_INPUT) {
                return WireConnectionType.ENERGY_INPUT;
            } else if (blockState.get(FRONT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                return WireConnectionType.ENERGY_OUTPUT;
            } else {
                return WireConnectionType.NONE;
            }
        } else if (opposite == Direction.DOWN) {
            if (blockState.get(BOTTOM_SIDE_OPTION) == SideOption.POWER_INPUT) {
                return WireConnectionType.ENERGY_INPUT;
            } else if (blockState.get(BOTTOM_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                return WireConnectionType.ENERGY_OUTPUT;
            } else {
                return WireConnectionType.NONE;
            }
        }

        if (blockState.get(DirectionProperty.of("facing", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) == Direction.NORTH) { //Only N, S, E, W
            if (opposite == Direction.NORTH) {
                if (blockState.get(FRONT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(FRONT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.SOUTH) {
                if (blockState.get(BACK_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(BACK_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.EAST) {
                if (blockState.get(RIGHT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(RIGHT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.WEST) {
                if (blockState.get(LEFT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(LEFT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            }

        } else if (blockState.get(DirectionProperty.of("facing", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) == Direction.SOUTH) {
            if (opposite == Direction.NORTH) {
                if (blockState.get(BACK_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(BACK_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.SOUTH) {
                if (blockState.get(FRONT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(FRONT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.EAST) {
                if (blockState.get(LEFT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(LEFT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.WEST) {
                if (blockState.get(RIGHT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(RIGHT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            }
        } else if (blockState.get(DirectionProperty.of("facing", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) == Direction.EAST) {
            if (opposite == Direction.NORTH) {
                if (blockState.get(RIGHT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(RIGHT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.SOUTH) {
                if (blockState.get(LEFT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(LEFT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.EAST) {
                if (blockState.get(BACK_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(FRONT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.WEST) {
                if (blockState.get(FRONT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(BACK_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            }
        } else if (blockState.get(DirectionProperty.of("facing", Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) == Direction.WEST) {
            if (opposite == Direction.NORTH) {
                if (blockState.get(LEFT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(LEFT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.SOUTH) {
                if (blockState.get(RIGHT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(RIGHT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.EAST) {
                if (blockState.get(BACK_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(BACK_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            } else if (opposite == Direction.WEST) {
                if (blockState.get(FRONT_SIDE_OPTION) == SideOption.POWER_INPUT) {
                    return WireConnectionType.ENERGY_INPUT;
                } else if (blockState.get(FRONT_SIDE_OPTION) == SideOption.POWER_OUTPUT) {
                    return WireConnectionType.ENERGY_OUTPUT;
                } else {
                    return WireConnectionType.NONE;
                }
            }
        }
        return WireConnectionType.NONE;
    }

    @Override
    public final void buildTooltip(ItemStack itemStack_1, BlockView blockView_1, List<Text> list_1, TooltipContext tooltipContext_1) {
        Text text = machineInfo(itemStack_1, blockView_1, tooltipContext_1);
        if (text != null) {
            List<Text> info = new ArrayList<>();
            for (String s : MinecraftClient.getInstance().textRenderer.wrapStringToWidthAsList(text.asFormattedString(), 150)) {
                info.add(new LiteralText(s).setStyle(new Style().setColor(Formatting.DARK_GRAY)));
            }
            if (!info.isEmpty()) {
                if (Screen.hasShiftDown()) {
                    list_1.addAll(info);
                } else {
                    list_1.add(new TranslatableText("tooltip.galacticraft-rewoven.press_shift").setStyle(new Style().setColor(Formatting.DARK_GRAY)));
                }
            }
        }

        if (itemStack_1 != null && itemStack_1.getTag() != null && itemStack_1.getTag().contains("BlockEntityTag")) {
            list_1.add(new LiteralText(""));
            list_1.add(new TranslatableText("ui.galacticraft-rewoven.machine.current_energy", itemStack_1.getTag().getCompound("BlockEntityTag").getInt("Energy")).setStyle(new Style().setColor(Formatting.AQUA)));
            list_1.add(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.owner", itemStack_1.getTag().getCompound("BlockEntityTag").getString("OwnerUsername")).setStyle(new Style().setColor(Formatting.BLUE)));
            if (itemStack_1.getTag().getCompound("BlockEntityTag").getBoolean("Public")) {
                list_1.add(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config_2").setStyle(new Style()
                        .setColor(Formatting.GRAY)).append(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.public_2")
                        .setStyle(new Style().setColor(Formatting.GREEN))));

            } else if (itemStack_1.getTag().getCompound("BlockEntityTag").getBoolean("Party")) {
                list_1.add(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config_2").setStyle(new Style()
                        .setColor(Formatting.GRAY)).append(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.space_race_2")
                        .setStyle(new Style().setColor(Formatting.DARK_GRAY))));

            } else {
                list_1.add(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config_2").setStyle(new Style()
                        .setColor(Formatting.GRAY)).append(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.private_2")
                        .setStyle(new Style().setColor(Formatting.DARK_RED))));

            }

            if (itemStack_1.getTag().getCompound("BlockEntityTag").getString("Redstone").equals("DISABLED")) {
                list_1.add(new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config_2").setStyle(new Style().setColor(Formatting.RED)).append(new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config.ignore_2").setStyle(new Style().setColor(Formatting.GRAY))));
            } else if (itemStack_1.getTag().getCompound("BlockEntityTag").getString("Redstone").equals("OFF")) {
                list_1.add(new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config_2").setStyle(new Style().setColor(Formatting.RED)).append(new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config.redstone_means_off_2").setStyle(new Style().setColor(Formatting.DARK_RED))));
            } else {
                list_1.add(new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config_2").setStyle(new Style().setColor(Formatting.RED)).append(new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config.redstone_means_on_2").setStyle(new Style().setColor(Formatting.DARK_RED))));
            }

        }
    }

    @Override
    public ItemStack getPickStack(BlockView blockView_1, BlockPos blockPos_1, BlockState blockState_1) {
        ItemStack stack = super.getPickStack(blockView_1, blockPos_1, blockState_1);
        CompoundTag tag = (stack.getTag() != null ? stack.getTag() : new CompoundTag());
        if (blockView_1.getBlockEntity(blockPos_1) != null) {
            tag.put("BlockEntityTag", blockView_1.getBlockEntity(blockPos_1).toTag(new CompoundTag()));
        }

        stack.setTag(tag);
        return stack;
    }

    public abstract Text machineInfo(ItemStack itemStack_1, BlockView blockView_1, TooltipContext tooltipContext_1);

    public abstract List<Direction> disabledSides();

    public static Direction energySideToDirection(EnergySide side) {
        switch (side) {
            case NORTH:
                return Direction.NORTH;
            case SOUTH:
                return Direction.SOUTH;
            case EAST:
                return Direction.EAST;
            case DOWN:
                return Direction.DOWN;
            case UP:
                return Direction.UP;
            case WEST:
                return Direction.WEST;
        }
        return Direction.NORTH;
    }

}
