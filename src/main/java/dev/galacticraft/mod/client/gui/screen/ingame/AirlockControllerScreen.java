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

import dev.galacticraft.mod.content.AirlockState;
import dev.galacticraft.mod.network.c2s.AirlockSetProximityPayload;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.AirlockControllerBlockEntity;
import dev.galacticraft.mod.screen.AirlockControllerMenu;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class AirlockControllerScreen extends MachineScreen<AirlockControllerBlockEntity, AirlockControllerMenu> {
    private final EditBox textField;

    public AirlockControllerScreen(AirlockControllerMenu menu, Inventory inv, Component title) {
        super(menu, title, Constant.ScreenTexture.AIRLOCK_CONTROLLER_SCREEN);

        this.textField = new EditBox(
                Minecraft.getInstance().font,
                this.leftPos + 132, this.topPos + 65,
                26, 20,
                Component.empty()
        );
        this.textField.setValue(String.valueOf(this.menu.proximityOpen));

        this.textField.setFilter(s -> {
            if (s.isEmpty()) return true;
            try {
                int v = Integer.parseInt(s);
                return v >= 0 && v <= 5;
            } catch (NumberFormatException ignore) {
                return false;
            }
        });

        this.textField.setResponder(s -> {
            if (s.isEmpty()) return;
            byte value;
            try {
                value = Byte.parseByte(s);
            } catch (NumberFormatException ignore) {
                return;
            }
            if (value < 0) value = 0;
            if (value > 5) value = 5;

            String clamped = String.valueOf(value);
            if (!s.equals(clamped)) {
                this.textField.setValue(clamped);
                return;
            }

            if (value != this.menu.proximityOpen) {
                this.menu.proximityOpen = value;
                ClientPlayNetworking.send(new AirlockSetProximityPayload(value));
            }
        });
    }

    @Override
    protected void init() {
        super.init();
        this.imageHeight = 171;
        this.titleLabelX = 90;

        this.textField.setX(this.leftPos + 132);
        this.textField.setY(this.topPos + 65);
        this.addRenderableWidget(this.textField);
    }

    @Override
    protected void renderMachineBackground(GuiGraphics g, int mouseX, int mouseY, float delta) {
        AirlockState enabled = this.menu.state;
        Component label;
        int color;
        if (enabled.equals(AirlockState.ALL)) {
            label = Component.translatable(Translations.Ui.AIRLOCK_ENABLED);
            color = ChatFormatting.DARK_GREEN.getColor();
        } else if (enabled.equals(AirlockState.PARTIAL)) {
            label = Component.translatable(Translations.Ui.AIRLOCK_PARTIAL);
            color = ChatFormatting.DARK_PURPLE.getColor();
        } else {
            label = Component.translatable(Translations.Ui.AIRLOCK_DISABLED);
            color = ChatFormatting.RED.getColor();
        }

        drawCenteredString(g, this.font, label, this.leftPos + 90, this.topPos + 22, color, false);

        int upX = this.leftPos + 158, upY = this.topPos + 65;
        int downX = this.leftPos + 158, downY = this.topPos + 75;

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

        drawStringAlignedRight(g, this.font, Component.translatable(Translations.Ui.AIRLOCK_PROXIMITY_LABEL), this.leftPos + 130, this.topPos + 71, ChatFormatting.DARK_GRAY.getColor(), false);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderForeground(graphics, mouseX, mouseY, delta);

        this.textField.setX(this.leftPos + 132);
        this.textField.setY(this.topPos + 65);

        String shouldBe = String.valueOf(this.menu.proximityOpen);
        if (!this.textField.getValue().equals(shouldBe) && !this.textField.isFocused()) {
            this.textField.setValue(shouldBe);
        }
    }

    @Override
    protected void drawTitle(@NotNull GuiGraphics graphics) {
        drawCenteredString(graphics, this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int upX = this.leftPos + 158, upY = this.topPos + 65;
            int downX = this.leftPos + 158, downY = this.topPos + 75;

            if (DrawableUtil.isWithin(mouseX, mouseY, upX, upY, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
                if (this.menu.proximityOpen < 5) {
                    byte next = (byte) (this.menu.proximityOpen + 1);
                    this.menu.proximityOpen = next;
                    this.textField.setValue(String.valueOf(next));
                    ClientPlayNetworking.send(new AirlockSetProximityPayload(next));
                    this.playButtonSound();
                    return true;
                }
            }

            if (DrawableUtil.isWithin(mouseX, mouseY, downX, downY, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
                if (this.menu.proximityOpen > 0) {
                    byte next = (byte) (this.menu.proximityOpen - 1);
                    this.menu.proximityOpen = next;
                    this.textField.setValue(String.valueOf(next));
                    ClientPlayNetworking.send(new AirlockSetProximityPayload(next));
                    this.playButtonSound();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void drawCenteredString(GuiGraphics g, Font font, Component text, int centerX, int y, int color, boolean shadow) {
        g.drawString(font, text, centerX - font.width(text) / 2, y, color, shadow);
    }

    public void drawStringAlignedRight(GuiGraphics g, Font font, Component text, int x, int y, int color, boolean shadow) {
        g.drawString(font, text, x - this.font.width(text), y, color, shadow);
    }
}