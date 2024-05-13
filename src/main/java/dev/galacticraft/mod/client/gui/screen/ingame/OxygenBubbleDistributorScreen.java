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

package dev.galacticraft.mod.client.gui.screen.ingame;

import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.machine.OxygenBubbleDistributorBlockEntity;
import dev.galacticraft.mod.network.packets.BubbleMaxPacket;
import dev.galacticraft.mod.network.packets.ToggleBubbleVisibilityPacket;
import dev.galacticraft.mod.screen.OxygenBubbleDistributorMenu;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.text.DecimalFormat;

public class OxygenBubbleDistributorScreen extends MachineScreen<OxygenBubbleDistributorBlockEntity, OxygenBubbleDistributorMenu> {
    private static final DecimalFormat FORMAT = new DecimalFormat();
    private final EditBox textField;

    public OxygenBubbleDistributorScreen(OxygenBubbleDistributorMenu handler, Inventory inv, Component title) {
        super(handler, title, Constant.ScreenTexture.BUBBLE_DISTRIBUTOR_SCREEN);
        this.textField = new EditBox(Minecraft.getInstance().font, this.leftPos + 132, this.topPos + 59, 26, 20, Component.literal(String.valueOf(this.menu.size)));
        this.textField.setResponder((s -> {
            try {
                if (Byte.parseByte(s) < 1) {
                    textField.setValue(String.valueOf(this.menu.targetSize));
                }
            } catch (NumberFormatException ignore) {
                textField.setValue(String.valueOf(this.menu.targetSize));
            }
        }));

        this.textField.setFilter((s -> {
            try {
                return Byte.parseByte(s) >= 1;
            } catch (NumberFormatException ignore) {
                return false;
            }
        }));
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX += 20;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (!this.menu.bubbleVisible) {
            if (!DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 156, this.topPos + 16, 13, 13)) {
                graphics.blit(Constant.ScreenTexture.OVERLAY, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_RED_X, Constant.TextureCoordinate.BUTTON_RED_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            } else {
                graphics.blit(Constant.ScreenTexture.OVERLAY, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_RED_HOVER_X, Constant.TextureCoordinate.BUTTON_RED_HOVER_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            }
            graphics.drawString(this.font, Component.translatable(Translations.Ui.BUBBLE_NOT_VISIBLE), this.leftPos + 60 , this.topPos + 18, ChatFormatting.RED.getColor(), false);
        } else {
            if (!DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 156, this.topPos + 16, 13, 13)) {
                graphics.blit(Constant.ScreenTexture.OVERLAY, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_GREEN_X, Constant.TextureCoordinate.BUTTON_GREEN_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            } else {
                graphics.blit(Constant.ScreenTexture.OVERLAY, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_GREEN_HOVER_X, Constant.TextureCoordinate.BUTTON_GREEN_HOVER_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            }
            graphics.drawString(this.font, Component.translatable(Translations.Ui.BUBBLE_VISIBLE), this.leftPos + 60, this.topPos + 18, ChatFormatting.GREEN.getColor(), false);
        }
        if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 158, this.topPos + 59, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
            graphics.blit(Constant.ScreenTexture.OVERLAY, this.leftPos + 158, this.topPos + 59, Constant.TextureCoordinate.ARROW_UP_HOVER_X, Constant.TextureCoordinate.ARROW_UP_HOVER_Y, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);
        }
        if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 158, this.topPos + 69, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
            graphics.blit(Constant.ScreenTexture.OVERLAY, this.leftPos + 158, this.topPos + 69, Constant.TextureCoordinate.ARROW_DOWN_HOVER_X, Constant.TextureCoordinate.ARROW_DOWN_HOVER_Y, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);
        }

        graphics.drawString(this.font, Component.translatable(Translations.Ui.BUBBLE_TARGET_SIZE), this.leftPos + 70, this.topPos + 64, ChatFormatting.DARK_GRAY.getColor(), false);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderForeground(graphics, mouseX, mouseY, delta);
        textField.setValue(String.valueOf(this.menu.targetSize));

        MachineStatus status = this.menu.state.getStatus();
        graphics.drawString(this.font, Component.translatable(Translations.Ui.MACHINE_STATUS).append(status != null ? status.getText() : Component.empty()), this.leftPos + 60, this.topPos + 30, ChatFormatting.DARK_GRAY.getColor(), false);

        this.textField.render(graphics, mouseX, mouseY, delta);

        this.textField.setX(this.leftPos + 132);
        this.textField.setY(this.topPos + 59);

        if (this.menu.state.isActive()) {
            graphics.drawString(this.font, Component.translatable(Translations.Ui.BUBBLE_CURRENT_SIZE, FORMAT.format(this.menu.size)).setStyle(Constant.Text.Color.DARK_GRAY_STYLE), this.leftPos + 60, this.topPos + 42, ChatFormatting.DARK_GRAY.getColor(), false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button) | checkClick(mouseX, mouseY, button) | textField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers) | textField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers) | textField.keyReleased(keyCode, scanCode, modifiers);
    }

    private boolean checkClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT)) {
                this.menu.bubbleVisible = ! this.menu.bubbleVisible;
                ClientPlayNetworking.send(new ToggleBubbleVisibilityPacket(this.menu.bubbleVisible));
                return true;
            }

            if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 158, this.topPos + 59, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
                if (this.menu.targetSize != Byte.MAX_VALUE) {
                    this.menu.targetSize = ((byte) (this.menu.targetSize + 1));
                    textField.setValue(String.valueOf(this.menu.targetSize));
                    ClientPlayNetworking.send(new BubbleMaxPacket(this.menu.targetSize));
                    return true;
                }
            }

            if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 158, this.topPos + 69, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
                if (this.menu.targetSize > 1) {
                    this.menu.targetSize = (byte) (this.menu.targetSize - 1);
                    textField.setValue(String.valueOf(this.menu.targetSize));
                    ClientPlayNetworking.send(new BubbleMaxPacket(this.menu.targetSize));
                    return true;
                }
            }
        }
        return false;
    }
}
