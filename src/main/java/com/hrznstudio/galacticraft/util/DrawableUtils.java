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

package com.hrznstudio.galacticraft.util;

import net.minecraft.client.font.TextRenderer;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class DrawableUtils {
    public static void drawCenteredString(TextRenderer textRenderer, String text, int x, int y, int color) {
        textRenderer.draw(text, (float) (x - textRenderer.getStringWidth(text) / 2), (float) y, color);
    }

    public static void drawRightAlignedString(TextRenderer textRenderer, String text, int x, int y, int color) {
        textRenderer.draw(text, (float) (x - textRenderer.getStringWidth(text)), (float) y, color);
    }

    public static void drawString(TextRenderer textRenderer, String text, int x, int y, int color) {
        textRenderer.draw(text, (float) x, (float) y, color);
    }
}
