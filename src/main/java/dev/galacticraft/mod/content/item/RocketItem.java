/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import dev.galacticraft.api.rocket.RocketPrefabs;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartTypes;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.block.special.launchpad.AbstractLaunchPad;
import dev.galacticraft.mod.content.block.special.launchpad.LaunchPadBlockEntity;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import dev.galacticraft.mod.util.TooltipUtil;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class RocketItem extends Item {
    public RocketItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        if (!level.isClientSide && level.getBlockState(clickedPos).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD
                && level.getBlockState(clickedPos).getValue(AbstractLaunchPad.PART) != AbstractLaunchPad.Part.NONE) {
            BlockPos pos = new BlockPos(clickedPos).offset(AbstractLaunchPad.partToCenterPos(level.getBlockState(clickedPos).getValue(AbstractLaunchPad.PART)));
            assert level.getBlockState(pos).getBlock() == GCBlocks.ROCKET_LAUNCH_PAD;
            LaunchPadBlockEntity pad = (LaunchPadBlockEntity) level.getBlockEntity(pos);
            if (pad.hasDockedEntity()) return InteractionResult.FAIL;

            if (level instanceof ServerLevel) {
                RocketEntity rocket = new RocketEntity(GCEntityTypes.ROCKET, level);
                Player player = context.getPlayer();
                ItemStack held = context.getItemInHand();
                RocketData data = held.has(GCDataComponents.ROCKET_DATA) ? held.get(GCDataComponents.ROCKET_DATA) : RocketPrefabs.TIER_1;
                rocket.setData(data);
                rocket.setPad(pad);
                rocket.setOldPosAndRot();
                rocket.absMoveTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                rocket.absRotateTo(180.0F + player.getDirection().toYRot(), 0.0F);
                boolean creative = held.getComponents().getOrDefault(GCDataComponents.CREATIVE, false);
                if (creative) {
                    rocket.setFuel(Long.MAX_VALUE);
                }
                level.addFreshEntity(rocket);

                if (!player.isCreative()) {
                    ItemStack stack = held.copy();
                    stack.shrink(1);
                    player.setItemInHand(context.getHand(), stack);
                }
                pad.setDockedEntity(rocket);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag options) {
        boolean creative = stack.getComponents().getOrDefault(GCDataComponents.CREATIVE, false);
        if (creative) {
            TooltipUtil.appendCreativeTooltip(tooltip);
        }

        Style style = TooltipUtil.DEFAULT_STYLE;
        RocketData data = stack.has(GCDataComponents.ROCKET_DATA) ? stack.get(GCDataComponents.ROCKET_DATA) : RocketPrefabs.MISSING;
        List<Component> list = new ArrayList<Component>();
        list.add(Component.translatable(Translations.Ui.COLOR).append(": ").withStyle(style).append(Component.literal("#" + Integer.toHexString(data.color())).withColor(data.color())));
        if (data.cone().isPresent())
            list.add(RocketPartTypes.CONE.name.copy().append(": ").append(RocketPart.getName(data.cone().get().key())).withStyle(style));
        if (data.body().isPresent())
            list.add(RocketPartTypes.BODY.name.copy().append(": ").append(RocketPart.getName(data.body().get().key())).withStyle(style));
        if (data.fin().isPresent())
            list.add(RocketPartTypes.FIN.name.copy().append(": ").append(RocketPart.getName(data.fin().get().key())).withStyle(style));
        if (data.booster().isPresent())
            list.add(RocketPartTypes.BOOSTER.name.copy().append(": ").append(RocketPart.getName(data.booster().get().key())).withStyle(style));
        if (data.engine().isPresent())
            list.add(RocketPartTypes.ENGINE.name.copy().append(": ").append(RocketPart.getName(data.engine().get().key())).withStyle(style));
        if (data.upgrade().isPresent())
            list.add(RocketPartTypes.UPGRADE.name.copy().append(": ").append(RocketPart.getName(data.upgrade().get().key())).withStyle(style));

        TooltipUtil.appendLshiftTooltip(list, tooltip);
        super.appendHoverText(stack, context, tooltip, options);
    }
}
