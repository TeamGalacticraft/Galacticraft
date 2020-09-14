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
import com.hrznstudio.galacticraft.api.block.util.BlockFace;
import com.hrznstudio.galacticraft.api.wire.WireConnectionType;
import com.hrznstudio.galacticraft.misc.TriFunction;
import com.hrznstudio.galacticraft.screen.MachineScreenHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ConfigurableMachineBlock extends BlockWithEntity {
    private final ScreenHandlerRegistry.ExtendedClientHandlerFactory<? extends MachineScreenHandler<? extends ConfigurableMachineBlockEntity>> factory;
    private final Function<BlockView, ? extends ConfigurableMachineBlockEntity> beFunction;
    private final TriFunction<ItemStack, BlockView, TooltipContext, Text> machineInfo;

    protected ConfigurableMachineBlock(Settings settings, ScreenHandlerRegistry.ExtendedClientHandlerFactory<? extends MachineScreenHandler<? extends ConfigurableMachineBlockEntity>> factory) {
        this(settings, factory, (view) -> null, (itemStack, blockView, context) -> new LiteralText(""));
    }

    public ConfigurableMachineBlock(Settings settings, ScreenHandlerRegistry.ExtendedClientHandlerFactory<? extends MachineScreenHandler<? extends ConfigurableMachineBlockEntity>> factory, Function<BlockView, ? extends ConfigurableMachineBlockEntity> beFunction, TriFunction<ItemStack, BlockView, TooltipContext, Text> machineInfo) {
        super(settings);
        this.factory = factory;
        this.beFunction = beFunction;
        this.machineInfo = machineInfo;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.HORIZONTAL_FACING);
    }

    public ConfigurableMachineBlockEntity createBlockEntity(BlockView view){
        return beFunction.apply(view);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, context.getPlayerFacing().getOpposite());
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
            if (Screen.hasShiftDown()) {
                char[] line = text instanceof TranslatableText ? I18n.translate(((TranslatableText) text).getKey()).toCharArray() : text.getString().toCharArray();
                int len = 0;
                final int maxLength = 175;
                StringBuilder builder = new StringBuilder();
                for (char c : line) {
                    len += MinecraftClient.getInstance().textRenderer.getWidth(String.valueOf(c));
                    if (c == ' ' && len >= maxLength) {
                        len = 0;
                        lines.add(new LiteralText(builder.toString()).setStyle(text.getStyle()));
                        builder = new StringBuilder();
                        continue;
                    }
                    builder.append(c);
                }
                lines.add(new LiteralText(builder.toString()).setStyle(text.getStyle()));
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
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.BLOCK;
    }

    @Override
    public final ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) player.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                buf.writeBlockPos(pos);
            }

            @Override
            public Text getDisplayName() {
                return new LiteralText("");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeBlockPos(pos); // idk why we have to do this again, might want to look into it
                //TODO: Look into why we have to create a new PacketByteBuf.
                return factory.create(syncId, inv, buf);
            }
        });

        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof ConfigurableMachineBlockEntity) {
            for (ItemStack stack : ((ConfigurableMachineBlockEntity) entity).getInventory().getStacks()) {
                if (stack != null) {
                    world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), stack));
                }
            }
            ((ConfigurableMachineBlockEntity) entity).getInventory().clear();
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

    public Text machineInfo(ItemStack stack, BlockView view, TooltipContext context) {
        return machineInfo.apply(stack, view, context);
    }
}
