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

package dev.galacticraft.mod.util;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.mod.Constants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public interface DrawableUtil {
    default void drawCenteredString(MatrixStack matrices, TextRenderer textRenderer, Text text, int x, int y, int color) {
        textRenderer.draw(matrices, text.asOrderedText(), (float) (x - textRenderer.getWidth(text) / 2), (float) y, color);
    }

    default void drawOxygenBuffer(MatrixStack matrices, int x, int y, int z, int oxygen, int capacity) {
        if (oxygen == 0 && capacity == 0) capacity = 1;
        drawOxygenBuffer(matrices, x, y, z, (float) oxygen / (float) capacity);
    }

    default void drawOxygenBuffer(MatrixStack matrices, int x, int y, int z, float scale) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(Constants.ScreenTexture.OVERLAY);
        texturedQuad(matrices.peek().getModel(), x, y, z, Constants.TextureCoordinate.OXYGEN_DARK_X, Constants.TextureCoordinate.OXYGEN_DARK_Y, Constants.TextureCoordinate.OVERLAY_WIDTH, Constants.TextureCoordinate.OVERLAY_HEIGHT);
        texturedQuad(matrices.peek().getModel(), x, y + Constants.TextureCoordinate.OVERLAY_HEIGHT - (Constants.TextureCoordinate.OVERLAY_HEIGHT * scale), z, Constants.TextureCoordinate.OXYGEN_LIGHT_X, Constants.TextureCoordinate.OXYGEN_LIGHT_Y, Constants.TextureCoordinate.OVERLAY_WIDTH, Constants.TextureCoordinate.OVERLAY_HEIGHT * scale);
    }

    default void texturedQuad(Matrix4f matrices, float x, float y, float z, float u, float v, float width, float height) {
        float x1 = x + width;
        float y1 = y + height;
        float u0 = u / 128f;
        float v0 = v / 128f;
        float u1 = (u + width) / 128f;
        float v1 = (v + height) / 128f;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrices, x, y1, z).texture(u0, v1).next();
        bufferBuilder.vertex(matrices, x1, y1, z).texture(u1, v1).next();
        bufferBuilder.vertex(matrices, x1, y, z).texture(u1, v0).next();
        bufferBuilder.vertex(matrices, x, y, z).texture(u0, v0).next();
        bufferBuilder.end();
        RenderSystem.enableAlphaTest();
        BufferRenderer.draw(bufferBuilder);
    }

    default boolean check(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }
}
