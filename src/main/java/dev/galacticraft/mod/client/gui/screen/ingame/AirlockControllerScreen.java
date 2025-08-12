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

package dev.galacticraft.mod.client.gui.screen.ingame;

import dev.galacticraft.mod.network.c2s.AirlockSetProximityPayload;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.AirlockControllerBlockEntity;
import dev.galacticraft.mod.screen.AirlockControllerMenu;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AirlockControllerScreen extends MachineScreen<AirlockControllerBlockEntity, AirlockControllerMenu> {
    public AirlockControllerScreen(AirlockControllerMenu menu, Inventory inv, Component title) {
        super(menu, title, Constant.ScreenTexture.AIRLOCK_CONTROLLER_SCREEN);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX += 20;
        // If MachineLib has its own redstone mode button bar, add it here if needed.
    }

    @Override
    protected void renderMachineBackground(GuiGraphics g, int mouseX, int mouseY, float delta) {
        // Status
        boolean enabled = this.menu.enabled;
        var label = enabled ? Component.translatable(Translations.Ui.AIRLOCK_ENABLED)
                : Component.translatable(Translations.Ui.AIRLOCK_DISABLED);
        int color = enabled ? ChatFormatting.GREEN.getColor() : ChatFormatting.RED.getColor();
        g.drawString(this.font, label, this.leftPos + 60, this.topPos + 18, color, false);

        // Proximity (up/down)
        int upX = this.leftPos + 158, upY = this.topPos + 59;
        int downX = this.leftPos + 158, downY = this.topPos + 69;
        boolean hoverUp = DrawableUtil.isWithin(mouseX, mouseY, upX, upY, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);
        boolean hoverDown = DrawableUtil.isWithin(mouseX, mouseY, downX, downY, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);

        g.blit(Constant.ScreenTexture.OVERLAY, upX, upY,
                hoverUp ? Constant.TextureCoordinate.ARROW_UP_HOVER_X : Constant.TextureCoordinate.ARROW_UP_X,
                hoverUp ? Constant.TextureCoordinate.ARROW_UP_HOVER_Y : Constant.TextureCoordinate.ARROW_UP_Y,
                Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);

        g.blit(Constant.ScreenTexture.OVERLAY, downX, downY,
                hoverDown ? Constant.TextureCoordinate.ARROW_DOWN_HOVER_X : Constant.TextureCoordinate.ARROW_DOWN_X,
                hoverDown ? Constant.TextureCoordinate.ARROW_DOWN_HOVER_Y : Constant.TextureCoordinate.ARROW_DOWN_Y,
                Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);

        g.drawString(this.font,
                Component.translatable(Translations.Ui.AIRLOCK_OPEN_WHEN_NEAR, this.menu.proximityOpen),
                this.leftPos + 60, this.topPos + 64, ChatFormatting.DARK_GRAY.getColor(), false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // Up
            int upX = this.leftPos + 158, upY = this.topPos + 59;
            if (DrawableUtil.isWithin(mouseX, mouseY, upX, upY, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
                if (this.menu.proximityOpen < 5) {
                    byte next = (byte)(this.menu.proximityOpen + 1);
                    this.menu.proximityOpen = next;
                    ClientPlayNetworking.send(new AirlockSetProximityPayload(next));
                    this.playButtonSound();
                    return true;
                }
            }
            // Down
            int downX = this.leftPos + 158, downY = this.topPos + 69;
            if (DrawableUtil.isWithin(mouseX, mouseY, downX, downY, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
                if (this.menu.proximityOpen > 0) {
                    byte next = (byte)(this.menu.proximityOpen - 1);
                    this.menu.proximityOpen = next;
                    ClientPlayNetworking.send(new AirlockSetProximityPayload(next));
                    this.playButtonSound();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}