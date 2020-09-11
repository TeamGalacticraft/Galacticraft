/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.api.block;

import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.api.wire.WireConnectable;
import com.hrznstudio.galacticraft.api.wire.WireConnectionType;
import com.hrznstudio.galacticraft.block.entity.CircuitFabricatorBlockEntity;
import io.github.cottonmc.component.item.impl.SimpleInventoryComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class ConfigurableMachineBlock extends BlockWithEntity implements WireConnectable {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private final Property<SideOption> front;
    private final Property<SideOption> back;
    private final Property<SideOption> left;
    private final Property<SideOption> right;
    private final Property<SideOption> top;
    private final Property<SideOption> bottom;

    public ConfigurableMachineBlock(Settings settings, Property<SideOption> front, Property<SideOption> back, Property<SideOption> right, Property<SideOption> left, Property<SideOption> top, Property<SideOption> bottom) {
        super(settings);
        this.front = front;
        this.back = back;
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public static void dropItems(World world, BlockPos pos, SimpleInventoryComponent inventory, ConfigurableMachineBlockEntity be) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getStack(i);

            if (stack != null) {
                world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), stack));
            }
        }
    }

    public Property<SideOption> getProperty(@NotNull BlockFace direction) {
        switch (direction) {
            case FRONT:
                return front;
            case RIGHT:
                return right;
            case LEFT:
                return left;
            case BACK:
                return back;
            case TOP:
                return top;
            case BOTTOM:
                return bottom;
        }
        throw new AssertionError();
    }

    public final SideOption getOption(BlockState state, BlockFace direction) {
        return state.get(getProperty(direction));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    public abstract ConfigurableMachineBlockEntity createBlockEntity(BlockView var1);

    public abstract boolean consumesFluids();

    public abstract boolean generatesFluids();

    public abstract boolean consumesOxygen();

    public abstract boolean generatesOxygen();

    public abstract boolean consumesPower();

    public abstract boolean generatesPower();

    @NotNull
    @Override
    public WireConnectionType canWireConnect(WorldAccess world, Direction opposite, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        BlockState state = world.getBlockState(connectionTargetPos);

        SideOption option = getOption(state, BlockFace.toFace(state.get(FACING), opposite));

        if (option == SideOption.POWER_INPUT) {
            return WireConnectionType.ENERGY_INPUT;
        } else if (option == SideOption.POWER_OUTPUT) {
            return WireConnectionType.ENERGY_OUTPUT;
        }

        return WireConnectionType.NONE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public final void appendTooltip(ItemStack stack, BlockView view, List<Text> lines, TooltipContext context) {
        Text text = machineInfo(stack, view, context);
        if (text != null) {
            List<Text> info = new ArrayList<>();
            char[] line = text instanceof TranslatableText ? I18n.translate(((TranslatableText) text).getKey()).toCharArray() : text.getString().toCharArray();
            int len = 0;
            final int maxLength = 175;
            StringBuilder builder = new StringBuilder();
            for (char c : line) {
                len += MinecraftClient.getInstance().textRenderer.getWidth(String.valueOf(c));
                if (c == ' ' && len >= maxLength) {
                    len = 0;
                    info.add(new LiteralText(builder.toString()).setStyle(text.getStyle()));
                    builder = new StringBuilder();
                    continue;
                }
                builder.append(c);
            }
            info.add(new LiteralText(builder.toString()).setStyle(text.getStyle()));
            if (Screen.hasShiftDown()) {
                lines.addAll(info);
            } else {
                lines.add(new TranslatableText("tooltip.galacticraft-rewoven.press_shift").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
            }
        }

        if (stack != null && stack.getTag() != null && stack.getTag().contains("BlockEntityTag")) {
            CompoundTag tag = stack.getTag().getCompound("BlockEntityTag");
            lines.add(new LiteralText(""));
            lines.add(new TranslatableText("ui.galacticraft-rewoven.machine.current_energy", tag.getInt("Energy")).setStyle(Style.EMPTY.withColor(Formatting.AQUA)));
            lines.add(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.owner", tag.getString("OwnerUsername")).setStyle(Style.EMPTY.withColor(Formatting.BLUE)));
            if (tag.getBoolean("Public")) {
                lines.add(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config_2").setStyle(Style.EMPTY
                        .withColor(Formatting.GRAY)).append(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.public_2")
                        .setStyle(Style.EMPTY.withColor(Formatting.GREEN))));

            } else if (tag.getBoolean("Party")) {
                lines.add(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config_2").setStyle(Style.EMPTY
                        .withColor(Formatting.GRAY)).append(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.space_race_2")
                        .setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY))));

            } else {
                lines.add(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config_2").setStyle(Style.EMPTY
                        .withColor(Formatting.GRAY)).append(new TranslatableText("ui.galacticraft-rewoven.tabs.security_config.private_2")
                        .setStyle(Style.EMPTY.withColor(Formatting.DARK_RED))));

            }

            if (tag.getString("Redstone").equals("DISABLED")) {
                lines.add(new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config_2").setStyle(Style.EMPTY.withColor(Formatting.RED)).append(new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config.ignore_2").setStyle(Style.EMPTY.withColor(Formatting.GRAY))));
            } else if (tag.getString("Redstone").equals("OFF")) {
                lines.add(new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config_2").setStyle(Style.EMPTY.withColor(Formatting.RED)).append(new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config.redstone_means_off_2").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED))));
            } else {
                lines.add(new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config_2").setStyle(Style.EMPTY.withColor(Formatting.RED)).append(new TranslatableText("ui.galacticraft-rewoven.tabs.redstone_activation_config.redstone_means_on_2").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED))));
            }

        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(BlockView view, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(view, pos, state);
        CompoundTag tag = (stack.getTag() != null ? stack.getTag() : new CompoundTag());
        if (view.getBlockEntity(pos) != null) {
            tag.put("BlockEntityTag", view.getBlockEntity(pos).toTag(new CompoundTag()));
        }

        stack.setTag(tag);
        return stack;
    }

    public abstract Text machineInfo(ItemStack stack, BlockView view, TooltipContext context);

    public List<Direction> disabledSides() {
        return Collections.emptyList();
    }

    public enum BlockFace {
        FRONT,
        RIGHT,
        BACK,
        LEFT,
        TOP,
        BOTTOM;

        @NotNull
        public static BlockFace toFace(Direction facing, Direction target) {
            assert facing == Direction.NORTH || facing == Direction.SOUTH || facing == Direction.EAST || facing == Direction.WEST;

            if (target == Direction.DOWN) {
                return BOTTOM;
            } else if (target == Direction.UP) {
                return TOP;
            }

            switch (facing) {
                case NORTH:
                    switch (target) {
                        case NORTH:
                            return FRONT;
                        case EAST:
                            return RIGHT;
                        case SOUTH:
                            return BACK;
                        case WEST:
                            return LEFT;
                    }
                    break;
                case EAST:
                    switch (target) {
                        case EAST:
                            return FRONT;
                        case NORTH:
                            return LEFT;
                        case WEST:
                            return BACK;
                        case SOUTH:
                            return RIGHT;
                    }
                    break;
                case SOUTH:
                    switch (target) {
                        case SOUTH:
                            return FRONT;
                        case WEST:
                            return RIGHT;
                        case NORTH:
                            return BACK;
                        case EAST:
                            return LEFT;
                    }
                    break;
                case WEST:
                    switch (target) {
                        case WEST:
                            return FRONT;
                        case SOUTH:
                            return LEFT;
                        case EAST:
                            return BACK;
                        case NORTH:
                            return RIGHT;
                    }
                    break;
            }

            throw new RuntimeException();
        }

        @NotNull
        public Direction toDirection(Direction facing) {
            assert facing == Direction.NORTH || facing == Direction.SOUTH || facing == Direction.EAST || facing == Direction.WEST;

            if (this == BOTTOM) {
                return Direction.DOWN;
            } else if (this == TOP) {
                return Direction.UP;
            }

            switch (facing) {
                case NORTH:
                    switch (this) {
                        case FRONT:
                            return Direction.NORTH;
                        case RIGHT:
                            return Direction.EAST;
                        case BACK:
                            return Direction.SOUTH;
                        case LEFT:
                            return Direction.WEST;
                    }
                    break;
                case EAST:
                    switch (this) {
                        case RIGHT:
                            return Direction.NORTH;
                        case FRONT:
                            return Direction.EAST;
                        case LEFT:
                            return Direction.SOUTH;
                        case BACK:
                            return Direction.WEST;
                    }
                    break;
                case SOUTH:
                    switch (this) {
                        case BACK:
                            return Direction.NORTH;
                        case LEFT:
                            return Direction.EAST;
                        case FRONT:
                            return Direction.SOUTH;
                        case RIGHT:
                            return Direction.WEST;
                    }
                    break;
                case WEST:
                    switch (this) {
                        case LEFT:
                            return Direction.NORTH;
                        case BACK:
                            return Direction.EAST;
                        case RIGHT:
                            return Direction.SOUTH;
                        case FRONT:
                            return Direction.WEST;
                    }
                    break;
            }

            throw new RuntimeException();
        }

        public BlockFace getOpposite() {
            switch (this) {
                case BOTTOM:
                    return TOP;
                case TOP:
                    return BOTTOM;
                case BACK:
                    return FRONT;
                case LEFT:
                    return RIGHT;
                case RIGHT:
                    return LEFT;
                case FRONT:
                    return BACK;
            }
            throw new RuntimeException();
        }
    }
}
