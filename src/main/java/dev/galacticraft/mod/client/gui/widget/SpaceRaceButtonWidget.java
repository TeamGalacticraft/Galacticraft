/*
 * Copyright (c) 2019-2021 Team Galacticraft
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class SpaceRaceButtonWidget extends ButtonWidget {
    private final TextRenderer textRenderer;
    private final int screenWidth;
    private final int screenHeight;

    public SpaceRaceButtonWidget(MinecraftClient minecraft, int x, int y, int buttonWidth, int buttonHeight, int screenWidth, int screenHeight) {
        super(x, y, buttonWidth, buttonHeight, LiteralText.EMPTY, (button) -> minecraft.openScreen(new SpaceRaceScreen()));
        this.textRenderer = minecraft.textRenderer;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int screenWidth = this.screenWidth;
        int screenHeight = this.screenHeight;
        int buttonWidth = 100;
        int buttonHeight = 35;
        int x = screenWidth - buttonWidth;
        int y = screenHeight - buttonHeight;

        int spaceBetweenLines = 1;
        int lineHeight = textRenderer.fontHeight;
        int textYOffset = 9;

        this.fillGradient(matrices, x, y, x + buttonWidth, y + buttonHeight, 0xF0151515, 0xF00C0C0C);
        this.drawHorizontalLine(matrices, x, screenWidth, y, 0xFF000000);
        this.drawVerticalLine(matrices, x, screenHeight, y, 0xFF000000);

        drawCenteredText(matrices, textRenderer, I18n.translate("ui.galacticraft.space_race_manager.button"), x + buttonWidth / 2, y + textYOffset, 0xFFFFFFFF);
        drawCenteredText(matrices, textRenderer, I18n.translate("ui.galacticraft.space_race_manager.button_2"), x + buttonWidth / 2, y + textYOffset + lineHeight + spaceBetweenLines, 0xFFFFFFFF);
    }
}