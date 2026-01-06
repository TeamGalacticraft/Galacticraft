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

package dev.galacticraft.mod.client.gui.widget;

import dev.galacticraft.mod.Constant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RadioButton extends AbstractWidget {
    private static final ResourceLocation BUTTON_TEX = Constant.id("textures/gui/radiobutton_gear_inventory_buttons.png");
    private static final int BTN_WIDTH = 11;
    private static final int BTN_HEIGHT = 10;
    private boolean isBottomButtonActive = false;

    public boolean getIsBottomButtonActive() {
        return isBottomButtonActive;
    }

    public Runnable radioButtonOnClick;

    public RadioButton(int x, int y) {
        super(x, y, BTN_WIDTH, BTN_HEIGHT * 2, Component.empty());
    }

    public void setIsBottomButtonActive(boolean newActive) {
        isBottomButtonActive = newActive;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int hovered = getButtonHovered(mouseX, mouseY);
        boolean isTopHovered = hovered == 0;
        boolean isBottomHovered = hovered == 1;
        graphics.blit(BUTTON_TEX, getX(), getY(), getArrowBlitCoordsU(isBottomButtonActive ? (isTopHovered ? 1 : 0) : 2), getArrowBlitCoordsV(false), BTN_WIDTH, BTN_HEIGHT, 33, 20);
        graphics.blit(BUTTON_TEX, getX(), getY() + BTN_HEIGHT, getArrowBlitCoordsU(isBottomButtonActive ? 2 : (isBottomHovered ? 1 : 0)), getArrowBlitCoordsV(true), BTN_WIDTH, BTN_HEIGHT, 33, 20);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }

    // style - 0: default 1: active 2: inactive
    private int getArrowBlitCoordsV(boolean isBottomTex) {
        return isBottomTex ? BTN_HEIGHT : 0;
    }

    // style - 0: default 1: active 2: inactive
    private int getArrowBlitCoordsU(int style) {
        return BTN_WIDTH * style;
    }

    public int getButtonHovered(int mouseX, int mouseY) {
        if (mouseX >= getX() && mouseX < getX() + BTN_WIDTH &&
                mouseY >= getY() && mouseY < getY() + BTN_HEIGHT) {
            return 0;
        }

        if (mouseX >= getX() && mouseX < getX() + BTN_WIDTH &&
                mouseY >= getY() + BTN_HEIGHT && mouseY < getY() + BTN_HEIGHT * 2) {
            return 1;
        }
        return -1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(button)) {
                int hovered = getButtonHovered((int)mouseX, (int)mouseY);
                boolean isTopHovered = hovered == 0;
                boolean isBottomHovered = hovered == 1;
                if (!isBottomButtonActive ? isBottomHovered : isTopHovered) {
                    isBottomButtonActive = !isBottomButtonActive;
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    this.onClick(mouseX, mouseY);
                    if (radioButtonOnClick != null) {
                        radioButtonOnClick.run();
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
