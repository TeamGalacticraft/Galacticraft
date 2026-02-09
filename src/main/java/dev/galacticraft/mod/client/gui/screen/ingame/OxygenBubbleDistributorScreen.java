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

import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.content.block.entity.machine.OxygenBubbleDistributorBlockEntity;
import dev.galacticraft.mod.network.c2s.BubbleMaxPayload;
import dev.galacticraft.mod.network.c2s.BubbleVisibilityPayload;
import dev.galacticraft.mod.screen.OxygenBubbleDistributorMenu;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Inventory;

import java.text.DecimalFormat;

import static dev.galacticraft.mod.Constant.BubbleDistributor.*;

public class OxygenBubbleDistributorScreen extends MachineScreen<OxygenBubbleDistributorBlockEntity, OxygenBubbleDistributorMenu> {
    private static final DecimalFormat FORMAT = new DecimalFormat();
    private final EditBox textField;

    public OxygenBubbleDistributorScreen(OxygenBubbleDistributorMenu handler, Inventory inv, Component title) {
        super(handler, title, SCREEN_TEXTURE);
        this.imageHeight = 176;
        this.imageWidth = 176;
        this.capacitorX = 8;
        this.capacitorY = 17;

        this.textField = new EditBox(Minecraft.getInstance().font, this.leftPos + TEXT_FIELD_X, this.topPos + TEXT_FIELD_Y, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT, Component.literal(String.valueOf(this.menu.size)));
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

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int buttonX = this.leftPos + BUTTON_X;
        int buttonY = this.topPos + BUTTON_Y;
        int buttonU, buttonV, color;
        Component text;

        if (this.menu.bubbleVisible) {
            color = ChatFormatting.DARK_GREEN.getColor();
            text = Component.translatable(Translations.Ui.BUBBLE_VISIBLE);
            if (DrawableUtil.isWithin(mouseX, mouseY, buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                buttonU = BUTTON_GREEN_HOVER_U;
                buttonV = BUTTON_GREEN_HOVER_V;
            } else {
                buttonU = BUTTON_GREEN_U;
                buttonV = BUTTON_GREEN_V;
            }
        } else {
            color = ChatFormatting.RED.getColor();
            text = Component.translatable(Translations.Ui.BUBBLE_NOT_VISIBLE);
            if (DrawableUtil.isWithin(mouseX, mouseY, buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                buttonU = BUTTON_RED_HOVER_U;
                buttonV = BUTTON_RED_HOVER_V;
            } else {
                buttonU = BUTTON_RED_U;
                buttonV = BUTTON_RED_V;
            }
        }

        graphics.blit(SCREEN_TEXTURE, buttonX, buttonY, buttonU, buttonV, BUTTON_WIDTH, BUTTON_HEIGHT);

        graphics.drawString(this.font, text, this.leftPos + TEXT_X, this.topPos + VISIBILITY_LABEL_Y, color, false);
        graphics.drawString(this.font, Component.translatable(Translations.Ui.MACHINE_STATUS, Component.empty()), this.leftPos + TEXT_X, this.topPos + STATUS_LABEL_Y, ChatFormatting.DARK_GRAY.getColor(), false);
        graphics.drawString(this.font, Component.translatable(Translations.Ui.BUBBLE_TARGET_SIZE), this.leftPos + TEXT_X, this.topPos + TEXT_FIELD_Y + 6, ChatFormatting.DARK_GRAY.getColor(), false);

        int arrowX = this.leftPos + ARROW_X;
        int arrowUpY = this.topPos + ARROW_UP_Y;
        int arrowDownY = this.topPos + ARROW_DOWN_Y;

        int arrowUpU = ARROW_UP_U;
        int arrowUpV = ARROW_UP_V;
        int arrowDownU = ARROW_DOWN_U;
        int arrowDownV = ARROW_DOWN_V;
        if (DrawableUtil.isWithin(mouseX, mouseY, arrowX, arrowUpY, ARROW_VERTICAL_WIDTH, ARROW_VERTICAL_HEIGHT)) {
            arrowUpU = ARROW_UP_HOVER_U;
            arrowUpV = ARROW_UP_HOVER_V;
        } else if (DrawableUtil.isWithin(mouseX, mouseY, arrowX, arrowDownY, ARROW_VERTICAL_WIDTH, ARROW_VERTICAL_HEIGHT)) {
            arrowDownU = ARROW_DOWN_HOVER_U;
            arrowDownV = ARROW_DOWN_HOVER_V;
        }

        graphics.blit(SCREEN_TEXTURE, arrowX, arrowUpY, arrowUpU, arrowUpV, ARROW_VERTICAL_WIDTH, ARROW_VERTICAL_HEIGHT);
        graphics.blit(SCREEN_TEXTURE, arrowX, arrowDownY, arrowDownU, arrowDownV, ARROW_VERTICAL_WIDTH, ARROW_VERTICAL_HEIGHT);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderForeground(graphics, mouseX, mouseY, delta);

        MutableComponent statusText = this.menu.state.getStatusText(this.menu.redstoneMode).copy();
        if (statusText.getStyle().getColor() == TextColor.fromLegacyFormat(ChatFormatting.GREEN)) {
            statusText.withStyle(ChatFormatting.DARK_GREEN);
        }
        graphics.drawString(this.font, statusText, this.leftPos + TEXT_X, this.topPos + STATUS_Y, -1, false);

        String currentSize = this.menu.state.isActive() ? FORMAT.format(this.menu.size) : "0";
        graphics.drawString(this.font, Component.translatable(Translations.Ui.BUBBLE_CURRENT_SIZE, currentSize), this.leftPos + TEXT_X, this.topPos + CURRENT_SIZE_Y, ChatFormatting.DARK_GRAY.getColor(), false);

        this.textField.setX(this.leftPos + TEXT_FIELD_X);
        this.textField.setY(this.topPos + TEXT_FIELD_Y);
        this.textField.setValue(String.valueOf(this.menu.targetSize));
        this.textField.render(graphics, mouseX, mouseY, delta);
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
            if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + BUTTON_X, this.topPos + BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                this.menu.bubbleVisible = !this.menu.bubbleVisible;
                ClientPlayNetworking.send(new BubbleVisibilityPayload(this.menu.bubbleVisible));
                this.playButtonSound();
                return true;
            }

            if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + ARROW_X, this.topPos + ARROW_UP_Y, ARROW_VERTICAL_WIDTH, ARROW_VERTICAL_HEIGHT)) {
                if (this.menu.targetSize != Byte.MAX_VALUE) {
                    this.menu.targetSize = ((byte) (this.menu.targetSize + 1));
                    textField.setValue(String.valueOf(this.menu.targetSize));
                    ClientPlayNetworking.send(new BubbleMaxPayload(this.menu.targetSize));
                    this.playButtonSound();
                    return true;
                }
            }

            if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + ARROW_X, this.topPos + ARROW_DOWN_Y, ARROW_VERTICAL_WIDTH, ARROW_VERTICAL_HEIGHT)) {
                if (this.menu.targetSize > 1) {
                    this.menu.targetSize = (byte) (this.menu.targetSize - 1);
                    textField.setValue(String.valueOf(this.menu.targetSize));
                    ClientPlayNetworking.send(new BubbleMaxPayload(this.menu.targetSize));
                    this.playButtonSound();
                    return true;
                }
            }
        }
        return false;
    }
}
