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
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.IntSupplier;

public class TeamColorButton extends AbstractButton {
    private static final MutableComponent LINE_1 = Component.translatable(Translations.SpaceRace.TEAM_COLOR_1);
    private static final MutableComponent LINE_2 = Component.translatable(Translations.SpaceRace.TEAM_COLOR_2);
    private static final MutableComponent LINE_3 = Component.translatable(Translations.SpaceRace.TEAM_COLOR_3);
    private final IntSupplier teamColor;
    private final Runnable onPress;

    public TeamColorButton(int x, int y, int width, int height, IntSupplier teamColor, Runnable onPress) {
        super(x, y, width, height, LINE_1.copy().append(LINE_2).append(LINE_3));
        this.teamColor = teamColor;
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        this.onPress.run();
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Font font = Minecraft.getInstance().font;
        graphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), this.teamColor.getAsInt());
        int centerX = this.getX() + this.getWidth() / 2;
        int centerY = this.getY() + this.getHeight() / 2 - font.lineHeight / 2;
        graphics.drawCenteredString(font, LINE_1, centerX, centerY - font.lineHeight, 0xFFFFFFFF);
        graphics.drawCenteredString(font, LINE_2, centerX, centerY, 0xFFFFFFFF);
        graphics.drawCenteredString(font, LINE_3, centerX, centerY + font.lineHeight, 0xFFFFFFFF);
        graphics.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.isHoveredOrFocused() ? 0xFF3C3C3C : 0xFF2D2D2D);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        this.defaultButtonNarrationText(builder);
    }
}
