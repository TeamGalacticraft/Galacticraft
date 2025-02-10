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

package dev.galacticraft.mod.client.gui.overlay;

import com.mojang.blaze3d.platform.Window;
import dev.galacticraft.mod.content.entity.orbital.lander.LanderEntity;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FastColor;
import net.minecraft.world.TickRateManager;

public class LanderOverlay {
    public static final TickRateManager TICKS = new TickRateManager();
    private static long tickCount;

    public static void onRenderHud(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        final Window scaledresolution = mc.getWindow();
        final int width = scaledresolution.getGuiScaledWidth();
        final int height = scaledresolution.getGuiScaledHeight();
        if (mc.player.getVehicle() instanceof LanderEntity lander) {
            double motionY = lander.getDeltaMovement().y();
            graphics.pose().pushPose();
            graphics.pose().scale(2.0F, 2.0F, 0.0F);

            if (motionY < -2.0) {
                graphics.drawString(mc.font, Component.translatable(Translations.Ui.LANDER_WARNING), width / 4 - mc.font.width(Component.translatable(Translations.Ui.LANDER_WARNING)) / 2, height / 8 - 20,
                        FastColor.ARGB32.color(255, 255, 0, 0), false);
                final int alpha = (int) (200 * (Math.sin(tickCount) * 0.5F + 0.5F)) + 5;
                final Component press_key = Component.translatable(Translations.Ui.LANDER_CONTROLS, mc.options.keyJump.getTranslatedKeyMessage());
                graphics.drawString(mc.font, press_key,
                        width / 4 - mc.font.width(press_key) / 2, height / 8,
                        FastColor.ARGB32.color(alpha, alpha, alpha, alpha), false);
            }

            graphics.pose().popPose();

            if (mc.player.getVehicle().getDeltaMovement().y() != 0.0D) {
                final Component string = Component.translatable(Translations.Ui.LANDER_VELOCITY, String.format("%.2f", mc.player.getVehicle().getDeltaMovement().y() * TICKS.tickrate()));
                final int red = Math.min((int) Math.floor(Math.abs(motionY) * 85.0D), 255);
                final int color = FastColor.ARGB32.color(255, red, 255 - red, 0);
                graphics.drawString(mc.font, string, width / 2 - mc.font.width(string) / 2, height / 3, color, false);
            }
        }
    }

    public static void clientTick() {
        tickCount++;
    }
}
