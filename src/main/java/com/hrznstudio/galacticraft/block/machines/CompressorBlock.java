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

package com.hrznstudio.galacticraft.block.machines;

import com.hrznstudio.galacticraft.api.block.AbstractHorizontalDirectionalBlock;
import com.hrznstudio.galacticraft.api.block.ConfigurableMachineBlock;
import com.hrznstudio.galacticraft.api.block.SideOption;
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.block.entity.CompressorBlockEntity;
import com.hrznstudio.galacticraft.screen.CompressorScreenHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CompressorBlock extends ConfigurableMachineBlock implements BlockEntityProvider {
    private static final EnumProperty<SideOption> FRONT_SIDE_OPTION = EnumProperty.of("north", SideOption.class, SideOption.DEFAULT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    private static final EnumProperty<SideOption> BACK_SIDE_OPTION = EnumProperty.of("south", SideOption.class, SideOption.DEFAULT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    private static final EnumProperty<SideOption> RIGHT_SIDE_OPTION = EnumProperty.of("east", SideOption.class, SideOption.DEFAULT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    private static final EnumProperty<SideOption> LEFT_SIDE_OPTION = EnumProperty.of("west", SideOption.class, SideOption.DEFAULT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    private static final EnumProperty<SideOption> TOP_SIDE_OPTION = EnumProperty.of("up", SideOption.class, SideOption.DEFAULT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);
    private static final EnumProperty<SideOption> BOTTOM_SIDE_OPTION = EnumProperty.of("down", SideOption.class, SideOption.DEFAULT, SideOption.ITEM_INPUT, SideOption.ITEM_OUTPUT);

    public CompressorBlock(Settings settings) {
        super(settings, FRONT_SIDE_OPTION, BACK_SIDE_OPTION, RIGHT_SIDE_OPTION, LEFT_SIDE_OPTION, TOP_SIDE_OPTION, BOTTOM_SIDE_OPTION);
    }

    @Override
    public ConfigurableMachineBlockEntity createBlockEntity(BlockView var1) {
        return new CompressorBlockEntity();
    }

    @Override
    public boolean consumesFluids() {
        return false;
    }

    @Override
    public boolean generatesFluids() {
        return false;
    }

    @Override
    public boolean consumesOxygen() {
        return false;
    }

    @Override
    public boolean generatesOxygen() {
        return false;
    }

    @Override
    public boolean consumesPower() {
        return false;
    }

    @Override
    public boolean generatesPower() {
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);

        builder.add(FRONT_SIDE_OPTION);
        builder.add(BACK_SIDE_OPTION);
        builder.add(RIGHT_SIDE_OPTION);
        builder.add(LEFT_SIDE_OPTION);
        builder.add(TOP_SIDE_OPTION);
        builder.add(BOTTOM_SIDE_OPTION);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Text machineInfo(ItemStack stack, BlockView view, TooltipContext context) {
        if (Screen.hasShiftDown()) {
            return (new TranslatableText("tooltip.galacticraft-rewoven.compressor").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        } else {
            return (new TranslatableText("tooltip.galacticraft-rewoven.press_shift").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        }
    }

    @Override
    public final ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        player.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                buf.writeBlockPos(pos);
            }

            @Override
            public Text getDisplayName() {
                return new TranslatableText("block.galacticraft-rewoven.compressor");
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeBlockPos(pos); // idk why we have to do this again, might want to look into it
                //TODO: Look into why we have to create a new PacketByteBuf.
                return new CompressorScreenHandler(syncId, inv, buf);
            }
        });
        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity != null) {
            if (blockEntity instanceof CompressorBlockEntity) {
                CompressorBlockEntity be = (CompressorBlockEntity) blockEntity;

                for (int i = 0; i < be.getInventory().getSize(); i++) {
                    ItemStack stack = be.getInventory().getStack(i);

                    if (!stack.isEmpty()) {
                        world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), stack.copy()));
                    }
                }
            }
        }
    }

}