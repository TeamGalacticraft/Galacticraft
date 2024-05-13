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

package dev.galacticraft.mod.client.gui.widget;

import dev.galacticraft.mod.client.gui.screen.ingame.SpaceRaceScreen;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class SpaceRaceButtonWidget extends Button {
    private final Font textRenderer;
    private final int screenWidth;
    private final int screenHeight;

    public SpaceRaceButtonWidget(Minecraft minecraft, int x, int y, int buttonWidth, int buttonHeight, int screenWidth, int screenHeight) {
        super(x, y, buttonWidth, buttonHeight, Component.empty(), (button) -> minecraft.setScreen(new SpaceRaceScreen()), Supplier::get);
        this.textRenderer = minecraft.font;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int i, int j, float f) {
        int screenWidth = this.screenWidth;
        int screenHeight = this.screenHeight;
        int buttonWidth = 100;
        int buttonHeight = 35;
        int x = screenWidth - buttonWidth;
        int y = screenHeight - buttonHeight;

        int spaceBetweenLines = 1;
        int lineHeight = textRenderer.lineHeight;
        int textYOffset = 9;

        graphics.fillGradient(x, y, x + buttonWidth, y + buttonHeight, 0xF0151515, 0xF00C0C0C);
        graphics.hLine(x, screenWidth, y, 0xFF000000);
        graphics.vLine(x, screenHeight, y, 0xFF000000);

        graphics.drawCenteredString(textRenderer, Component.translatable(Translations.SpaceRace.BUTTON), x + buttonWidth / 2, y + textYOffset, 0xFFFFFFFF);
        graphics.drawCenteredString(textRenderer, Component.translatable(Translations.SpaceRace.BUTTON_2), x + buttonWidth / 2, y + textYOffset + lineHeight + spaceBetweenLines, 0xFFFFFFFF);
    }
}