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

package com.hrznstudio.galacticraft.client.gui.widget;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.client.gui.screen.ingame.SpaceRaceScreen;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class SpaceRaceButtonWidget extends ButtonWidget {
    private final TextRenderer font;
    private final int screenWidth;
    private final int screenHeight;

    public SpaceRaceButtonWidget(MinecraftClient client, int x, int y, int buttonWidth, int buttonHeight, int screenWidth, int screenHeight) {
        super(x, y, buttonWidth, buttonHeight, new LiteralText(""), (button) -> {
            client.openScreen(new SpaceRaceScreen());
            client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "request_scroll"), new PacketByteBuf(Unpooled.buffer(0))));
        });
        this.font = client.textRenderer;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void renderButton(MatrixStack stack, int int_1, int int_2, float float_1) {
        int screenWidth = this.screenWidth;
        int screenHeight = this.screenHeight;
        int buttonWidth = 100;
        int buttonHeight = 35;
        int x = screenWidth - buttonWidth;
        int y = screenHeight - buttonHeight;

        int spaceBetweenLines = 1;
        int lineHeight = font.fontHeight;
        int textYOffset = 9;

        this.fillGradient(stack, x, y, x + buttonWidth, y + buttonHeight, 0xF0151515, 0xF00C0C0C);
        this.drawHorizontalLine(stack, x, screenWidth, y, 0xFF000000);
        this.drawVerticalLine(stack, x, screenHeight, y, 0xFF000000);

        DrawableUtils.drawCenteredString(stack, font, "Space Race", x + buttonWidth / 2, y + textYOffset, 0xFFFFFFFF);
        DrawableUtils.drawCenteredString(stack, font, "Manager", x + buttonWidth / 2, y + textYOffset + lineHeight + spaceBetweenLines, 0xFFFFFFFF);
    }
}