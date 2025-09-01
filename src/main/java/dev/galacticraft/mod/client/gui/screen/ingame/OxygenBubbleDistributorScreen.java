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

import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.machine.OxygenBubbleDistributorBlockEntity;
import dev.galacticraft.mod.network.c2s.BubbleMaxPayload;
import dev.galacticraft.mod.network.c2s.BubbleVisibilityPayload;
import dev.galacticraft.mod.screen.OxygenBubbleDistributorMenu;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.DecimalFormat;

public class OxygenBubbleDistributorScreen extends MachineScreen<OxygenBubbleDistributorBlockEntity, OxygenBubbleDistributorMenu> {
    private static final DecimalFormat FORMAT = new DecimalFormat();
    private EditBox textField;

    public OxygenBubbleDistributorScreen(OxygenBubbleDistributorMenu handler, Inventory inv, Component title) {
        super(handler, title, Constant.ScreenTexture.BUBBLE_DISTRIBUTOR_SCREEN);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX += 20;

        this.textField = new EditBox(this.font, this.leftPos + 132, this.topPos + 59, 26, 20, Component.literal(String.valueOf(this.menu.targetSize)));
        this.textField.setResponder((s -> {
            if (s.isBlank()) return;
            int value = NumberUtils.toInt(s, -1);
            if (value >= 0 && value <= OxygenBubbleDistributorBlockEntity.MAX_SIZE) {
                this.menu.targetSize = value;
                ClientPlayNetworking.send(new BubbleMaxPayload(value));
            }
        }));

        this.textField.setFilter((s -> {
            int value = NumberUtils.toInt(s, -1);
            return s.isBlank() || value >= 0 && value <= OxygenBubbleDistributorBlockEntity.MAX_SIZE;
        }));
        this.textField.setValue(String.valueOf(this.menu.targetSize));

        this.addRenderableWidget(this.textField);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (!this.textField.getValue().isBlank() && NumberUtils.toInt(this.textField.getValue(), -1) != this.menu.targetSize) {
            this.textField.setValue(String.valueOf(this.menu.targetSize));
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (!this.menu.bubbleVisible) {
            if (!DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT)) {
                graphics.blit(Constant.ScreenTexture.OVERLAY, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_RED_X, Constant.TextureCoordinate.BUTTON_RED_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            } else {
                graphics.blit(Constant.ScreenTexture.OVERLAY, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_RED_HOVER_X, Constant.TextureCoordinate.BUTTON_RED_HOVER_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            }
            graphics.drawString(this.font, Component.translatable(Translations.Ui.BUBBLE_NOT_VISIBLE), this.leftPos + 60, this.topPos + 18, ChatFormatting.RED.getColor(), false);
        } else {
            if (!DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT)) {
                graphics.blit(Constant.ScreenTexture.OVERLAY, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_GREEN_X, Constant.TextureCoordinate.BUTTON_GREEN_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            } else {
                graphics.blit(Constant.ScreenTexture.OVERLAY, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_GREEN_HOVER_X, Constant.TextureCoordinate.BUTTON_GREEN_HOVER_Y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT);
            }
            graphics.drawString(this.font, Component.translatable(Translations.Ui.BUBBLE_VISIBLE), this.leftPos + 60, this.topPos + 18, ChatFormatting.GREEN.getColor(), false);
        }

        if (!DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 158, this.topPos + 59, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
            graphics.blit(Constant.ScreenTexture.OVERLAY, this.leftPos + 158, this.topPos + 59, Constant.TextureCoordinate.ARROW_UP_X, Constant.TextureCoordinate.ARROW_UP_Y, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);
        } else {
            graphics.blit(Constant.ScreenTexture.OVERLAY, this.leftPos + 158, this.topPos + 59, Constant.TextureCoordinate.ARROW_UP_HOVER_X, Constant.TextureCoordinate.ARROW_UP_HOVER_Y, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);
        }
        if (!DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 158, this.topPos + 69, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
            graphics.blit(Constant.ScreenTexture.OVERLAY, this.leftPos + 158, this.topPos + 69, Constant.TextureCoordinate.ARROW_DOWN_X, Constant.TextureCoordinate.ARROW_DOWN_Y, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);
        } else {
            graphics.blit(Constant.ScreenTexture.OVERLAY, this.leftPos + 158, this.topPos + 69, Constant.TextureCoordinate.ARROW_DOWN_HOVER_X, Constant.TextureCoordinate.ARROW_DOWN_HOVER_Y, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);
        }

        graphics.drawString(this.font, Component.translatable(Translations.Ui.BUBBLE_TARGET_SIZE), this.leftPos + 70, this.topPos + 64, ChatFormatting.DARK_GRAY.getColor(), false);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderForeground(graphics, mouseX, mouseY, delta);

        MachineStatus status = this.menu.state.getStatus();
        graphics.drawString(this.font, Component.translatable(Translations.Ui.MACHINE_STATUS, status != null ? status.getText() : Component.empty()), this.leftPos + 60, this.topPos + 30, ChatFormatting.DARK_GRAY.getColor(), false);

        if (this.menu.state.isActive()) {
            graphics.drawString(this.font, Component.translatable(Translations.Ui.BUBBLE_CURRENT_SIZE, FORMAT.format(this.menu.size)).setStyle(Constant.Text.DARK_GRAY_STYLE), this.leftPos + 60, this.topPos + 42, ChatFormatting.DARK_GRAY.getColor(), false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button) | this.checkClick(mouseX, mouseY, button);
    }

    private boolean checkClick(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 156, this.topPos + 16, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT)) {
                this.menu.bubbleVisible = !this.menu.bubbleVisible;
                ClientPlayNetworking.send(new BubbleVisibilityPayload(this.menu.bubbleVisible));
                this.playButtonSound();
                return true;
            }

            if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 158, this.topPos + 59, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
                if (this.menu.targetSize < OxygenBubbleDistributorBlockEntity.MAX_SIZE) {
                    this.menu.targetSize = this.menu.targetSize + 1;
                    this.textField.setValue(String.valueOf(this.menu.targetSize));
                    this.playButtonSound();
                    return true;
                }
            }

            if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 158, this.topPos + 69, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
                if (this.menu.targetSize > 0) {
                    this.menu.targetSize = this.menu.targetSize - 1;
                    this.textField.setValue(String.valueOf(this.menu.targetSize));
                    this.playButtonSound();
                    return true;
                }
            }
        }
        return false;
    }
}
