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

package com.hrznstudio.galacticraft.block.machines;

import com.hrznstudio.galacticraft.api.block.AbstractHorizontalDirectionalBlock;
import com.hrznstudio.galacticraft.block.entity.CompressorBlockEntity;
import com.hrznstudio.galacticraft.screen.BasicSolarPanelScreenHandler;
import com.hrznstudio.galacticraft.screen.CompressorScreenHandler;
import com.hrznstudio.galacticraft.screen.GalacticraftScreenHandlerTypes;
import com.hrznstudio.galacticraft.util.Rotatable;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
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

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CompressorBlock extends AbstractHorizontalDirectionalBlock implements Rotatable, BlockEntityProvider {
    public CompressorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new CompressorBlockEntity();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public final void buildTooltip(ItemStack itemStack_1, BlockView blockView_1, List<Text> list_1, TooltipContext tooltipContext_1) {
        if (Screen.hasShiftDown()) {
            list_1.add(new TranslatableText("tooltip.galacticraft-rewoven.compressor").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        } else {
            list_1.add(new TranslatableText("tooltip.galacticraft-rewoven.press_shift").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public final ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        playerEntity.openHandledScreen(new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                buf.writeBlockPos(blockPos);
            }

            @Override
            public Text getDisplayName() {
                return new TranslatableText("block.galacticraft-rewoven.compressor");
            }

            @Nullable
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeBlockPos(blockPos); // idk why we have to do this again, might want to look into it
                //TODO: Look into why we have to create a new PacketByteBuf.
                return new CompressorScreenHandler(syncId, inv, buf);
            }
        });
        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        super.onBreak(world, blockPos, blockState, playerEntity);

        BlockEntity blockEntity = world.getBlockEntity(blockPos);

        if (blockEntity != null) {
            if (blockEntity instanceof CompressorBlockEntity) {
                CompressorBlockEntity be = (CompressorBlockEntity) blockEntity;

                for (int i = 0; i < be.getInventory().getSize(); i++) {
                    ItemStack itemStack = be.getInventory().getStack(i);

                    if (!itemStack.isEmpty()) {
                        world.spawnEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY() + 1, blockPos.getZ(), itemStack.copy()));
                    }
                }
            }
        }
    }

}