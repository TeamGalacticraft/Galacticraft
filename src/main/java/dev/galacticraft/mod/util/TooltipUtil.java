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

package dev.galacticraft.mod.util;

import dev.galacticraft.mod.Constant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.List;

public class TooltipUtil {
    public static final Style DEFAULT_STYLE = Constant.Text.Color.GRAY_STYLE;
    private static final Component PRESS_SHIFT = Component.translatable(Translations.Tooltip.PRESS_SHIFT).withStyle(Constant.Text.Color.DARK_GRAY_STYLE);

    private TooltipUtil() {}

    public static void appendLshiftTooltip(String resourceId, List<Component> tooltip) {
        Minecraft minecraft = Minecraft.getInstance();
        Component description = Component.translatable(resourceId);
        List list = minecraft.font.getSplitter().splitLines(description, 150, Style.EMPTY).stream().map(formattedText -> Component.literal(formattedText.getString()).withStyle(DEFAULT_STYLE)).toList();
        appendLshiftTooltip(list, tooltip);
    }

    public static void appendLshiftTooltip(List<Component> list, List<Component> tooltip) {
        if (Screen.hasShiftDown()) {
            tooltip.addAll(list);
        } else {
            tooltip.add(PRESS_SHIFT);
        }
    }

    public static void appendCreativeTooltip(List<Component> tooltip, Style style) {
        tooltip.add(Component.translatable(Translations.Tooltip.CREATIVE_ONLY).withStyle(style));
    }

    public static void appendCreativeTooltip(List<Component> tooltip) {
        appendCreativeTooltip(tooltip, Constant.Text.Color.RED_STYLE);
    }
}
