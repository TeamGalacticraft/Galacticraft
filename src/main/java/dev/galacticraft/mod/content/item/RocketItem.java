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

import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartTypes;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.block.special.launchpad.AbstractLaunchPad;
import dev.galacticraft.mod.content.block.special.launchpad.LaunchPadBlockEntity;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

public class RocketItem extends Item {
    public RocketItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide && context.getLevel().getBlockState(context.getClickedPos()).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD
                && context.getLevel().getBlockState(context.getClickedPos()).getValue(AbstractLaunchPad.PART) != AbstractLaunchPad.Part.NONE) {
            BlockPos pos = new BlockPos(context.getClickedPos()).offset(AbstractLaunchPad.partToCenterPos(context.getLevel().getBlockState(context.getClickedPos()).getValue(AbstractLaunchPad.PART)));
            assert context.getLevel().getBlockState(pos).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD;
            LaunchPadBlockEntity pad = (LaunchPadBlockEntity) context.getLevel().getBlockEntity(pos);
            if (pad.hasDockedEntity()) return InteractionResult.FAIL;

            if (context.getLevel() instanceof ServerLevel) {
                RocketEntity rocket = new RocketEntity(GCEntityTypes.ROCKET, context.getLevel());
                RocketData data = RocketData.fromPatch(context.getItemInHand().getComponentsPatch());
                rocket.setData(data);
                rocket.setPad(pad);
                rocket.setOldPosAndRot();
                rocket.absMoveTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                boolean creative = context.getItemInHand().getComponents().getOrDefault(GCDataComponents.CREATIVE, false);
                if (creative) {
                    rocket.setFuel(Long.MAX_VALUE);
                }
                context.getLevel().addFreshEntity(rocket);

                if (!context.getPlayer().isCreative()) {
                    ItemStack stack = context.getPlayer().getItemInHand(context.getHand()).copy();
                    stack.shrink(1);
                    context.getPlayer().setItemInHand(context.getHand(), stack);
                }
                pad.setDockedEntity(rocket);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);

        boolean creative = stack.getComponents().getOrDefault(GCDataComponents.CREATIVE, false);
        if (creative) {
            tooltip.add(Component.literal("Creative Only").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
        }
        if (Screen.hasShiftDown()) {
            RocketData data = RocketData.fromPatch(stack.getComponentsPatch());
            tooltip.add(Component.translatable(Translations.Ui.COLOR).append(Component.literal(": #" + Integer.toHexString(data.color())).withColor(data.color())));
            if (data.cone() != null) tooltip.add(RocketPartTypes.CONE.name.copy().append(" ").append(RocketPart.getName(data.cone().key())).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
            if (data.body() != null) tooltip.add(RocketPartTypes.BODY.name.copy().append(" ").append(RocketPart.getName(data.body().key())).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
            if (data.fin() != null) tooltip.add(RocketPartTypes.FIN.name.copy().append(" ").append(RocketPart.getName(data.fin().key())).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
            if (data.booster() != null) tooltip.add(RocketPartTypes.BOOSTER.name.copy().append(" ").append(RocketPart.getName(data.booster().key())).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
            if (data.engine() != null) tooltip.add(RocketPartTypes.ENGINE.name.copy().append(" ").append(RocketPart.getName(data.engine().key())).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
            if (data.upgrade() != null) tooltip.add(RocketPartTypes.UPGRADE.name.copy().append(" ").append(RocketPart.getName(data.upgrade().key())).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        } else {
            tooltip.add(Component.translatable(Translations.Tooltip.PRESS_SHIFT).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        }
    }
}
