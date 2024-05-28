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

package dev.galacticraft.mod.client.gui.overlay;

import dev.galacticraft.api.rocket.LaunchStage;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FastColor;

public class CountdownOverlay {
    public static void renderCountdown(GuiGraphics graphics, float tickDelta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player.getVehicle() instanceof RocketEntity rocket && rocket.getLaunchStage() == LaunchStage.IGNITED) {
            int count = (int) Math.floor(((float) rocket.getTimeAsState()) / 20.0f);

            final int width = mc.getWindow().getGuiScaledWidth();
            final int height = mc.getWindow().getGuiScaledHeight();

            graphics.pose().pushPose();

            count = 20 - count;

            if (count <= 10) {
                graphics.pose().scale(4.0F, 4.0F, 0.0F);
                graphics.drawString(mc.font, String.valueOf(count), width / 8 - mc.font.width(String.valueOf(count)) / 2, height / 20, FastColor.ARGB32.color(255, 255, 0, 0), false);
            } else {
                graphics.pose().scale(2.0F, 2.0F, 0.0F);
                graphics.drawString(mc.font, String.valueOf(count), width / 4 - mc.font.width(String.valueOf(count)) / 2, height / 8, FastColor.ARGB32.color(255, 255, 0, 0), false);
            }

            graphics.pose().popPose();
        }
    }
}
