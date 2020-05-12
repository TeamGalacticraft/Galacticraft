/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.container.screen;

import com.hrznstudio.galacticraft.util.DrawableUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class SpaceRaceScreen extends Screen {
    private int widthSize = 0;
    private int heightSize = 0;

    public SpaceRaceScreen() {
        super(new TranslatableText("menu.space_race_manager"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private boolean isAnimationComplete() {
        int sideMargins = (int) (this.width * 0.16);
        int maxWidth = this.width - sideMargins * 2;

        int topMargins = (int) (this.width * 0.09);
        int maxHeight = this.height - topMargins * 2;

        return widthSize >= maxWidth
                && heightSize >= maxHeight;
    }

    @Override
    public void resize(MinecraftClient minecraftClient_1, int int_1, int int_2) {
        this.widthSize = 0;
        this.heightSize = 0;
        super.resize(minecraftClient_1, int_1, int_2);
    }

    @Override
    public void renderBackground(MatrixStack stack) {
        // 5% of width
        int maxWidth = this.width - getXMargins() * 2;
        if (widthSize < maxWidth) {
            widthSize += Math.min(3, maxWidth - widthSize);
        }

        int maxHeight = this.height - getYMargins() * 2;
        if (heightSize < maxHeight) {
            heightSize += Math.min(2, maxHeight - heightSize);
        }

        int midX = this.width / 2;
        int midY = this.height / 2;

        int x = midX - widthSize / 2;
        int y = midY - heightSize / 2;

        fill(stack, x, y, x + widthSize, y + heightSize, 0x80000000);
    }

    private void renderForeground(MatrixStack stack) {
        TextRenderer font = this.client.textRenderer;
        DrawableUtils.drawCenteredString(stack, font, "Space Race Manager", this.width / 2, getTop() + 2, 0xFFFFFF);
    }

    private int getYMargins() {
        return (int) (this.height * this.getMarginPercent());
    }

    private int getXMargins() {
        return (int) (this.width * this.getMarginPercent());
    }

    private float getMarginPercent() {
        return 0.17F;
    }

    private int getTop() {
        return getYMargins();
    }

    private int getBottom() {
        return this.height - getYMargins();
    }

    private int getLeft() {
        return getXMargins();
    }

    private int getRight() {
        return this.widthSize - getXMargins();
    }

    @Override
    public void render(MatrixStack stack, int x, int y, float lastFrameDuration) {
        this.renderBackground(stack);

        if (this.isAnimationComplete()) {
            this.renderForeground(stack);
        }

        super.render(stack, x, y, lastFrameDuration);
//        this.drawMouseoverTooltip(x, y);

//        this.mouseX = (float) x;
//        this.mouseY = (float)/*y*/ minecraft.window.getScaledHeight() / 2;
//
//        DiffuseLighting.enableForItems();
//        this.itemRenderer.renderGuiItem(Items.GRASS_BLOCK.getStackForRender(), this.x + 6, this.y - 20);
//        this.itemRenderer.renderGuiItem(GalacticraftItems.OXYGEN_FAN.getStackForRender(), this.x + 35, this.y - 20);
    }
}