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

package dev.galacticraft.mod.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class SpaceRaceButton extends Button {
    public SpaceRaceButton(Component component, int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, component, onPress, DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderFrame(graphics);

        Font font = Minecraft.getInstance().font;
        int textPadding = 4;
        int maxLineWidth = Math.max(0, this.getWidth() - textPadding * 2);
        List<FormattedCharSequence> lines = font.split(this.getMessage(), maxLineWidth);
        if (lines.isEmpty()) {
            return;
        }

        int lineSpacing = 1;
        int maxLines = Math.max(1, (this.getHeight() - textPadding * 2 + lineSpacing) / (font.lineHeight + lineSpacing));
        int lineCount = Math.min(lines.size(), maxLines);
        int totalTextHeight = lineCount * font.lineHeight + (lineCount - 1) * lineSpacing;
        int textY = this.getY() + (this.getHeight() - totalTextHeight) / 2 + 1; //"Little engines can do big things" ahh pixel :D
        int centerX = this.getX() + this.getWidth() / 2;

        for (int i = 0; i < lineCount; i++) {
            graphics.drawCenteredString(font, lines.get(i), centerX, textY + i * (font.lineHeight + lineSpacing), 0xFFFFFFFF);
        }
    }

    protected void renderFrame(GuiGraphics graphics) {
        int x = this.getX();
        int y = this.getY();
        int width = this.getWidth();
        int height = this.getHeight();
        int backgroundColor = this.isHoveredOrFocused() ? 0xAA1E1E1E : 0xAA000000;
        int lineColor = this.isHoveredOrFocused() ? 0xFF3C3C3C : 0xFF2D2D2D;
        graphics.fill(x, y, x + width, y + height, backgroundColor);
        graphics.renderOutline(x, y, width, height, lineColor);
    }
}
