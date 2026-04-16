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

package dev.galacticraft.mod.client.gui.screen.ingame;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ScrollbarAxis;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ScrollbarInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

final class SpaceRaceScreenRenderers {
    private SpaceRaceScreenRenderers() {
    }

    static int getScaledLineHeight(SpaceRaceScreen screen, float scale) {
        return Math.max(1, Math.round(screen.fontRenderer().lineHeight * scale));
    }

    static int scaledTextWidth(SpaceRaceScreen screen, String text, float scale) {
        return Math.round(screen.fontRenderer().width(text) * scale);
    }

    static String truncateForScaledText(SpaceRaceScreen screen, String text, int maxWidth, float scale) {
        int unscaledWidth = Math.max(0, Mth.floor(maxWidth / scale));
        return screen.fontRenderer().plainSubstrByWidth(text, unscaledWidth);
    }

    static void drawScaledString(SpaceRaceScreen screen, GuiGraphics graphics, String text, int x, int y, int color, float scale) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(scale, scale, 1.0F);
        graphics.drawString(screen.fontRenderer(), text, 0, 0, color, false);
        pose.popPose();
    }

    static void drawScaledCenteredString(SpaceRaceScreen screen, GuiGraphics graphics, String text, int centerX, int y, int color, float scale) {
        int drawX = centerX - scaledTextWidth(screen, text, scale) / 2;
        drawScaledString(screen, graphics, text, drawX, y, color, scale);
    }

    static void renderScaledItem(GuiGraphics graphics, ItemStack icon, int x, int y, float scale) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(scale, scale, 1.0F);
        graphics.renderItem(icon, 0, 0);
        pose.popPose();
    }

    static void renderScrollbar(SpaceRaceScreen screen, GuiGraphics graphics, ScrollbarInfo scrollbar, int mouseX, int mouseY) {
        if (scrollbar == null || scrollbar.length() <= 0 || !scrollbar.isInteractive()) {
            return;
        }

        int x = scrollbar.x();
        int y = scrollbar.y();
        int length = scrollbar.length();
        if (scrollbar.axis() == ScrollbarAxis.VERTICAL) {
            graphics.fill(x, y, x + SpaceRaceScreen.SCROLLBAR_WIDTH, y + length, 0x44171717);
            graphics.renderOutline(x, y, SpaceRaceScreen.SCROLLBAR_WIDTH, length, 0x66454545);
        } else {
            graphics.fill(x, y, x + length, y + SpaceRaceScreen.SCROLLBAR_WIDTH, 0x44171717);
            graphics.renderOutline(x, y, length, SpaceRaceScreen.SCROLLBAR_WIDTH, 0x66454545);
        }

        int thumbPosition = scrollbar.thumbPosition();
        int thumbLength = scrollbar.thumbLength();
        boolean active = screen.activeScrollbar == scrollbar.type();
        boolean hovered = scrollbar.contains(mouseX, mouseY);
        int thumbColor = active ? 0xFFB9B9B9 : hovered ? 0xFFA3A3A3 : 0xFF8D8D8D;
        if (scrollbar.axis() == ScrollbarAxis.VERTICAL) {
            graphics.fill(x + 1, thumbPosition + 1, x + SpaceRaceScreen.SCROLLBAR_WIDTH - 1, thumbPosition + thumbLength - 1, thumbColor);
            graphics.renderOutline(x, thumbPosition, SpaceRaceScreen.SCROLLBAR_WIDTH, thumbLength, 0xFF5A5A5A);
        } else {
            graphics.fill(thumbPosition + 1, y + 1, thumbPosition + thumbLength - 1, y + SpaceRaceScreen.SCROLLBAR_WIDTH - 1, thumbColor);
            graphics.renderOutline(thumbPosition, y, thumbLength, SpaceRaceScreen.SCROLLBAR_WIDTH, 0xFF5A5A5A);
        }
    }

    static void renderPlayerHead(SpaceRaceScreen screen, GuiGraphics graphics, UUID playerId, int x, int y, int size) {
        if (screen.minecraftClient() != null && screen.minecraftClient().player != null && screen.minecraftClient().player.connection != null) {
            PlayerInfo playerInfo = screen.minecraftClient().player.connection.getPlayerInfo(playerId);
            if (playerInfo != null) {
                PlayerFaceRenderer.draw(graphics, playerInfo.getSkin(), x, y, size);
                return;
            }
        }

        PlayerFaceRenderer.draw(graphics, DefaultPlayerSkin.get(playerId), x, y, size);
    }
}
