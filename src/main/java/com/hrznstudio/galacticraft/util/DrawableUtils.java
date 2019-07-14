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
