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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.TooltipUtil;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class HotThrowableMeteorChunkItem extends ThrowableMeteorChunkItem {
    public static int MAX_TICKS = 45 * 20;

    public HotThrowableMeteorChunkItem(Properties settings) {
        super(settings);
    }

    @Override
    public boolean allowComponentsUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        Integer maybeTicks = stack.get(GCDataComponents.TICKS_UNTIL_COOL);
        if (maybeTicks != null) {
            int ticksUntilCool = maybeTicks;
            double secondsUntilCool = ticksUntilCool / 20.0;
            Style style = Constant.Text.getCoolingStyle((double)ticksUntilCool/(double)MAX_TICKS);
            Component remaining = Component.translatable(Translations.Tooltip.SECONDS_UNIT, String.format("%.1f", secondsUntilCool)).setStyle(style);
            TooltipUtil.appendLabeledTooltip(Translations.Tooltip.TIME_UNTIL_COOL, remaining, tooltip);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide())
            return;

        Integer i = stack.get(GCDataComponents.TICKS_UNTIL_COOL);
        int ticks = Objects.requireNonNullElse(i, MAX_TICKS);

        if (ticks == 0) {
            if (entity instanceof Player player) {
                stack.remove(GCDataComponents.TICKS_UNTIL_COOL);
                ItemStack cooledStack = new ItemStack(GCItems.THROWABLE_METEOR_CHUNK, stack.getCount());
                cooledStack.applyComponents(stack.getComponents());

                Inventory inventory = player.getInventory();
                inventory.setItem(slotId, new ItemStack(GCItems.THROWABLE_METEOR_CHUNK, stack.getCount()));
                inventory.setChanged();

                player.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.4F); // Entity::playEntityOnFireExtinguishedSound()
            }
        }
        stack.set(GCDataComponents.TICKS_UNTIL_COOL, ticks - 1);
    }
}