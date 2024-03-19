/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.content.item;

import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.impl.rocket.RocketDataImpl;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.content.block.special.rocketlaunchpad.RocketLaunchPadBlock;
import dev.galacticraft.mod.content.block.special.rocketlaunchpad.RocketLaunchPadBlockEntity;
import dev.galacticraft.mod.content.entity.RocketEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RocketItem extends Item {
    public RocketItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide && context.getLevel().getBlockState(context.getClickedPos()).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD
                && context.getLevel().getBlockState(context.getClickedPos()).getValue(RocketLaunchPadBlock.PART) != RocketLaunchPadBlock.Part.NONE) {
            BlockPos pos = new BlockPos(context.getClickedPos()).offset(RocketLaunchPadBlock.partToCenterPos(context.getLevel().getBlockState(context.getClickedPos()).getValue(RocketLaunchPadBlock.PART)));
            assert context.getLevel().getBlockState(pos).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD;
            RocketLaunchPadBlockEntity pad = (RocketLaunchPadBlockEntity) context.getLevel().getBlockEntity(pos);
            if (pad.hasRocket()) return InteractionResult.FAIL;

            if (context.getLevel() instanceof ServerLevel) {
                RocketEntity rocket = new RocketEntity(GCEntityTypes.ROCKET, context.getLevel());
                CompoundTag tag = context.getItemInHand().getTag();
                RocketData data = RocketData.fromNbt(tag);
                rocket.setData(data);
                rocket.setLinkedPad(pos);
                rocket.setOldPosAndRot();
                rocket.absMoveTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                if (tag.contains("creative")) {
                    try (Transaction tx = Transaction.openOuter()) {
                        rocket.getTank().insert(FluidVariant.of(GCFluids.FUEL), Long.MAX_VALUE, tx);
                        tx.commit();
                    }
                }
                context.getLevel().addFreshEntity(rocket);

                if (!context.getPlayer().isCreative()) {
                    ItemStack stack = context.getPlayer().getItemInHand(context.getHand()).copy();
                    stack.shrink(1);
                    context.getPlayer().setItemInHand(context.getHand(), stack);
                }
                pad.setLinkedRocket(rocket);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public ItemStack getDefaultInstance() {
        var itemStack = super.getDefaultInstance();
        itemStack.setTag(RocketDataImpl.DEFAULT_ROCKET.copy());
        return itemStack;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);

        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("creative"))
            tooltip.add(Component.literal("Creative Only").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        if (Screen.hasShiftDown()) {
            if (tag.contains("color") && tag.contains("cone")) {
//                tooltip.add(Component.translatable("tooltip.galacticraft.tier", tag.getInt("tier")).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)));
                tooltip.add(Component.translatable("tooltip.galacticraft.color"));
                tooltip.add(Component.translatable("tooltip.galacticraft.red", tag.getInt("color") >> 16 & 0xFF).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
                tooltip.add(Component.translatable("tooltip.galacticraft.green", tag.getInt("color") >> 8 & 0xFF).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)));
                tooltip.add(Component.translatable("tooltip.galacticraft.blue", tag.getInt("color") & 0xFF).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
                tooltip.add(Component.translatable("tooltip.galacticraft.alpha", tag.getInt("color") >> 24 & 0xFF).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));
                tooltip.add(Component.literal("-----").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)));
                ResourceLocation id = new ResourceLocation(tag.getString("cone"));
                tooltip.add(Component.translatable("tooltip.galacticraft.part_type.cone", Component.translatable("tooltip." + id.getNamespace() + ".cone." + id.getPath() + ".name")).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                id = new ResourceLocation(tag.getString("body"));
                tooltip.add(Component.translatable("tooltip.galacticraft.part_type.body", Component.translatable("tooltip." + id.getNamespace() + ".body." + id.getPath() + ".name")).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                id = new ResourceLocation(tag.getString("fin"));
                tooltip.add(Component.translatable("tooltip.galacticraft.part_type.fin", Component.translatable("tooltip." + id.getNamespace() + ".fin." + id.getPath() + ".name")).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                id = new ResourceLocation(tag.getString("body"));
                tooltip.add(Component.translatable("tooltip.galacticraft.part_type.booster", Component.translatable("tooltip." + id.getNamespace() + ".booster." + id.getPath() + ".name")).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                id = new ResourceLocation(tag.getString("body"));
                tooltip.add(Component.translatable("tooltip.galacticraft.part_type.engine", Component.translatable("tooltip." + id.getNamespace() + ".engine." + id.getPath() + ".name")).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                ListTag list = tag.getList("upgrades", Tag.TAG_STRING);
                for (int i = 0; i < list.size(); i++) {
                    id = new ResourceLocation(list.getString(i));
                    tooltip.add(Component.translatable("tooltip.galacticraft.part_type.upgrade", Component.translatable("tooltip." + id.getNamespace() + ".upgrade." + id.getPath() + ".name")).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                }
            }
        } else {
            tooltip.add(Component.translatable("tooltip.galacticraft.press_shift").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        }
    }
}
