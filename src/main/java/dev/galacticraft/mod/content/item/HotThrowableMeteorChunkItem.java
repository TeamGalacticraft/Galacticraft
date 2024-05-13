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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HotThrowableMeteorChunkItem extends ThrowableMeteorChunkItem {
    public static String TICKS_UNTIL_COOL = "TicksUntilCool";
    public static int MAX_TICKS = 45 * 20;

    public HotThrowableMeteorChunkItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            int ticksUntilCool = tag.getInt(TICKS_UNTIL_COOL);
            double secondsUntilCool = ticksUntilCool / 20.0;
            Style style = Constant.Text.Color.getStorageLevelStyle(1.0 - (double)ticksUntilCool/(double)MAX_TICKS);
            tooltipComponents.add(Component.translatable(Translations.Tooltip.TIME_UNTIL_COOL, String.format("%.1f", secondsUntilCool)).setStyle(style));
        }
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide())
            return;

        CompoundTag stackTag = stack.getOrCreateTag();

        if (!stackTag.contains(TICKS_UNTIL_COOL, CompoundTag.TAG_INT))
            stackTag.putInt(TICKS_UNTIL_COOL, MAX_TICKS);

        int ticksUntilCool = stackTag.getInt(TICKS_UNTIL_COOL);
        if (ticksUntilCool == 0) {
            if (entity instanceof Player player) {
                ItemStack cooledStack = new ItemStack(GCItems.THROWABLE_METEOR_CHUNK, stack.getCount());
                cooledStack.setTag(stackTag);
                Inventory inventory = player.getInventory();
                inventory.setItem(slotId, new ItemStack(GCItems.THROWABLE_METEOR_CHUNK, stack.getCount()));
                inventory.setChanged();
            }
        }
        stackTag.putInt(TICKS_UNTIL_COOL, ticksUntilCool - 1);
    }
}