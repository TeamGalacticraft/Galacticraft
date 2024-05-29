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

package dev.galacticraft.mod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.galacticraft.mod.Constant;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class BatchedColorRenderer implements AutoCloseable {
    private final BufferBuilder buffer;
    private final PoseStack pose;
    private final int textureWidth;
    private final int textureHeight;

    public BatchedColorRenderer(BufferBuilder buffer, PoseStack pose, ResourceLocation texture, int textureWidth, int textureHeight) {
        assert !buffer.building();

        this.buffer = buffer;
        this.pose = pose;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
    }

    public BatchedColorRenderer(GuiGraphics graphics, ResourceLocation texture, int textureWidth, int textureHeight) {
        this(Tesselator.getInstance().getBuilder(), graphics.pose(), texture, textureWidth, textureHeight);
    }

    public void blit(int x, int y, int width, int height, int uOffset, int vOffset, int uWidth, int vHeight, int color) {
        innerBlit(x, x + width, y, y + height, uOffset, vOffset, uWidth, vHeight, color);
    }

    public void blit(int x, int y, int width, int height, int u, int v, int uWidth, int vHeight, int color, boolean invertX, boolean invertY) {
        innerBlit(x, x + width, y, y + height, u + (invertX ? uWidth : 0), v + (invertY ? vHeight : 0), invertX ? -uWidth : uWidth, invertY ? -vHeight : vHeight, color);
    }

    public void blit(int x, int y, int u, int v, int width, int height, int color) {
        blit(x, y, width, height, u, v, width, height, color);
    }

    public void innerBlit(int x1, int x2, int y1, int y2, int uOffset, int vOffset, int uWidth, int vHeight, int color) {
        this.innerBlit(x1, x2, y1, y2, (float)uOffset / (float) this.textureWidth, (float)(uOffset + uWidth) / (float) this.textureWidth, (float)vOffset / (float) this.textureHeight, (float) (vOffset + vHeight) / (float) this.textureHeight, color);
    }

    private void innerBlit(int x1, int x2, int y1, int y2, float u1, float u2, float v1, float v2, int color) {
        Matrix4f matrix = this.pose.last().pose();
        this.buffer.vertex(matrix, (float) x1, (float) y1, -10).uv(u1, v1).color(color).endVertex();
        this.buffer.vertex(matrix, (float) x1, (float) y2, -10).uv(u1, v2).color(color).endVertex();
        this.buffer.vertex(matrix, (float) x2, (float) y2, -10).uv(u2, v2).color(color).endVertex();
        this.buffer.vertex(matrix, (float) x2, (float) y1, -10).uv(u2, v1).color(color).endVertex();
    }

    public PoseStack pose() {
        return this.pose;
    }

    @Override
    public void close() {
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, Constant.id("textures/gui/celestial_selection_0.png"));
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        BufferUploader.drawWithShader(this.buffer.end());
    }
}
