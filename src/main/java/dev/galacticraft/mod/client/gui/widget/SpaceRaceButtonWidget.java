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

import dev.galacticraft.mod.util.Translations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SpaceRaceButtonWidget extends SpaceRaceButton {
    private final Font textRenderer;

    public SpaceRaceButtonWidget(Minecraft minecraft, int x, int y, int buttonWidth, int buttonHeight, int screenWidth, int screenHeight) {
        super(CommonComponents.EMPTY, x, y, buttonWidth, buttonHeight, (button) -> minecraft.setScreen(new dev.galacticraft.mod.client.gui.screen.ingame.SpaceRaceScreen()));
        this.textRenderer = minecraft.font;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int i, int j, float f) {
        this.renderFrame(graphics);

        int spaceBetweenLines = 1;
        int lineHeight = this.textRenderer.lineHeight;
        int totalTextHeight = lineHeight * 2 + spaceBetweenLines;
        int textY = this.getY() + (this.getHeight() - totalTextHeight) / 2 + 1;
        int centerX = this.getX() + this.getWidth() / 2;

        graphics.drawCenteredString(this.textRenderer, Component.translatable(Translations.SpaceRace.BUTTON), centerX, textY, 0xFFFFFFFF);
        graphics.drawCenteredString(this.textRenderer, Component.translatable(Translations.SpaceRace.BUTTON_2), centerX, textY + lineHeight + spaceBetweenLines, 0xFFFFFFFF);
    }
}
