/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class LunarDungeonKeycardItem extends KeycardItem {
    public LunarDungeonKeycardItem(
            Properties properties
    ) {
        super(properties);
    }

    @Override
    protected boolean canPlayerBind() {
        return false;
    }

    @Override
    protected boolean consumeOnSuccessfulUse() {
        return true;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Item.TooltipContext context,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        tooltip.add(
                Component.literal(
                        "Lunar Facility Security Credential"
                ).withStyle(
                        ChatFormatting.AQUA
                )
        );

        tooltip.add(
                Component.literal(
                        "Facility: UNKNOWN"
                ).withStyle(
                        ChatFormatting.DARK_GRAY
                )
        );

        tooltip.add(
                Component.literal(
                        "Single Use"
                ).withStyle(
                        ChatFormatting.RED
                )
        );

        super.appendHoverText(
                stack,
                context,
                tooltip,
                flag
        );
    }
}