/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Graphics implements AutoCloseable {
    private final PoseStack pose;
    private final MultiBufferSource.BufferSource bufferSource;
    private final Font font;
    private final List<BatchedDrawable> renderers = new ArrayList<>(1);
    private @Nullable BufferBuilder buffer;

    private Graphics(PoseStack pose, MultiBufferSource.BufferSource bufferSource, Font font) {
        this.pose = pose;
        this.bufferSource = bufferSource;
        this.font = font;
        this.buffer = null;
    }

    public static Graphics managed(GuiGraphics graphics, Font font) {
        return new Graphics(graphics.pose(), graphics.bufferSource(), font);
    }

    public TextureColor textureColor(ResourceLocation texture) {
        return textureColor(texture, 256);
    }

    public TextureColor textureColor(ResourceLocation texture, int size) {
        return this.textureColor(texture, size, size);
    }

    public TextureColor textureColor(ResourceLocation texture, int width, int height) {
        return new TextureColor(Minecraft.getInstance().getTextureManager().getTexture(texture).getId(), width, height);
    }

    public Texture texture(ResourceLocation texture) {
        return texture(texture, 256);
    }

    public Texture texture(ResourceLocation texture, int size) {
        return this.texture(texture, size, size);
    }

    public Texture texture(ResourceLocation texture, int width, int height) {
        return new Texture(Minecraft.getInstance().getTextureManager().getTexture(texture).getId(), width, height);
    }

    public Fill fill() {
        return this.fill(RenderType.gui());
    }

    public Text text() {
        return new Text();
    }

    public Fill fill(RenderType type) {
        return new Fill(type);
    }

    public void cleanupState() {
        for (BatchedDrawable renderer : this.renderers) {
            renderer.draw();
        }
        assert this.buffer == null;
    }

    @Override
    public void close() {
        this.cleanupState();
    }

    public class Lines implements AutoCloseable, BatchedDrawable {
        private final RenderType type;
        private VertexConsumer consumer;
        private boolean open = false;

        private Lines(RenderType type) {
            this.type = type;
            Graphics.this.renderers.add(this);
        }

        public void line(float x1, float y1, float x2, float y2, float z, int color) {
            this.lineGradient(x1, y1, x2, y2, z, color, color);
        }

        public void lineGradient(float x1, float y1, float x2, float y2, float z, int color1, int color2) {
            this.ensureOpen();

            Matrix4f matrix = Graphics.this.pose.last().pose();
            this.consumer
                    .addVertex(matrix, x1, y1, z).setColor(color1)
                    .addVertex(matrix, x2, y2, z).setColor(color2);
        }

        private void ensureOpen() {
            if (!this.open) {
                Graphics.this.cleanupState();
                this.consumer = Graphics.this.bufferSource.getBuffer(this.type);
                this.open = true;
            }
        }

        @Override
        public void close() {
            this.draw();
            Graphics.this.renderers.remove(this);
        }

        @Override
        public void draw() {
            if (this.open) {
                Graphics.this.bufferSource.endBatch();
                this.open = false;
            }
        }
    }

    public class Fill implements AutoCloseable, BatchedDrawable {
        private final RenderType type;
        private VertexConsumer consumer;
        private boolean open = false;

        private Fill(RenderType type) {
            this.type = type;
            Graphics.this.renderers.add(this);
        }

        public void fill(int x, int y, int width, int height, int z, int color) {
            this.fillGradient(x, y, x + width, y + height, z, color, color);
        }

        public void fill(float x, float y, float width, float height, float z, int color) {
            this.fillGradient(x, y, x + width, y + height, z, color, color);
        }

        public void fillGradient(int x, int y, int width, int height, int z, int colorA, int colorB) {
            this.fillGradientRaw(x, y, x + width, y + height, z, colorA, colorB);
        }

        public void fillGradient(float x, float y, float width, float height, float z, int colorA, int colorB) {
            this.fillGradientRaw(x, y, x + width, y + height, z, colorA, colorB);
        }

        public void fillGradientRaw(float x1, float y1, float x2, float y2, int colorA, int colorB) {
            this.fillGradientRaw(x1, y1, x2, y2, 0.0f, colorA, colorB);
        }

        public void fillGradientRaw(float x1, float y1, float x2, float y2, float z, int colorA, int colorB) {
            this.ensureOpen();

            Matrix4f matrix = Graphics.this.pose.last().pose();
            this.consumer
                    .addVertex(matrix, x1, y1, z).setColor(colorA)
                    .addVertex(matrix, x1, y2, z).setColor(colorB)
                    .addVertex(matrix, x2, y2, z).setColor(colorB)
                    .addVertex(matrix, x2, y1, z).setColor(colorA);
        }

        private void ensureOpen() {
            if (!this.open) {
                Graphics.this.cleanupState();
                this.consumer = Graphics.this.bufferSource.getBuffer(this.type);
                this.open = true;
            }
        }

        @Override
        public void close() {
            this.draw();
            Graphics.this.renderers.remove(this);
        }

        @Override
        public void draw() {
            if (this.open) {
                Graphics.this.bufferSource.endBatch();
                this.open = false;
            }
        }
    }

    public class TextureColor extends Text implements AutoCloseable, BatchedDrawable {
        private final int texture;
        private final int textureWidth;
        private final int textureHeight;
        private boolean open = false;

        private TextureColor(int texture, int textureWidth, int textureHeight) {
            this.texture = texture;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }

        public void blit(int x, int y, int width, int height, int z, int u, int v, int color) {
            this.blit(x, y, width, height, z, u, v, width, height, color);
        }

        public void blit(int x, int y, int width, int height, int z, int u, int v, int uWidth, int vHeight, int color) {
            this.blitRaw(x, y, x + width, y + height, z, (float) u / this.textureWidth, (float) v / this.textureHeight, (float) (u + uWidth) / this.textureWidth, (float) (v + vHeight) / this.textureHeight, color);
        }

        public void blit(float x, float y, float width, float height, float z, float u, float v, int color) {
            this.blit(x, y, width, height, z, u, v, width, height, color);
        }

        public void blit(float x, float y, float width, float height, float z, float u, float v, float uWidth, float vHeight, int color) {
            this.blitRaw(x, y, x + width, y + height, z, u / this.textureWidth, v / this.textureHeight, (u + uWidth) / this.textureWidth, (v + vHeight) / this.textureHeight, color);
        }

        public void blitRaw(float x1, float y1, float x2, float y2, float z, float u1, float v1, float u2, float v2, int color) {
            this.ensureOpen();

            Matrix4f matrix = Graphics.this.pose.last().pose();
            Graphics.this.buffer
                    .addVertex(matrix, x1, y1, z).setUv(u1, v1).setColor(color)
                    .addVertex(matrix, x1, y2, z).setUv(u1, v2).setColor(color)
                    .addVertex(matrix, x2, y2, z).setUv(u2, v2).setColor(color)
                    .addVertex(matrix, x2, y1, z).setUv(u2, v1).setColor(color);
        }

        public void blit(int x, int y, int width, int height, int u, int v, int color) {
            this.blit(x, y, width, height, 0, u, v, width, height, color);
        }

        public void blit(int x, int y, int width, int height, int u, int v, int uWidth, int vHeight, int color) {
            this.blit(x, y, width, height, 0, u, v, uWidth, vHeight, color);
        }

        public void blit(float x, float y, float width, float height, float u, float v, int color) {
            this.blit(x, y, width, height, 0.0f, u, v, width, height, color);
        }

        public void blit(float x, float y, float width, float height, float u, float v, float uWidth, float vHeight, int color) {
            this.blit(x, y, width, height, 0.0f, u, v, uWidth, vHeight, color);
        }

        private void ensureOpen() {
            if (!this.open) {
                Graphics.this.cleanupState();
                Graphics.this.buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                this.open = true;
            }
        }

        @Override
        public void close() {
            this.draw();
            super.close();
            Graphics.this.renderers.remove(this);
        }

        @Override
        public void draw() {
            if (this.open) {
                assert Graphics.this.buffer != null;
                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, this.texture);
                RenderSystem.enableBlend();
                BufferUploader.drawWithShader(Graphics.this.buffer.build());
                RenderSystem.disableBlend();
                this.open = false;
                Graphics.this.buffer = null;
            }
        }
    }

    public class Texture extends Text implements AutoCloseable, BatchedDrawable {
        private final int texture;
        private final int textureWidth;
        private final int textureHeight;
        private boolean open;

        private Texture(int texture, int textureWidth, int textureHeight) {
            this.texture = texture;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            Graphics.this.renderers.add(this);
        }

        public void blit(int x, int y, int z, int u, int v, int width, int height) {
            this.blit(x, y, z, width, height, u, v, width, height);
        }

        public void blit(int x, int y, int z, int width, int height, int u, int v, int uWidth, int vHeight) {
            this.blitRaw(x, y, x + width, y + height, z, (float) u / this.textureWidth, (float) v / this.textureHeight, (float) (u + uWidth) / this.textureWidth, (float) (v + vHeight) / this.textureHeight);
        }

        public void blit(float x, float y, float z, float width, float height, float u, float v, float uWidth, float vHeight) {
            this.blitRaw(x, y, x + width, y + height, z, u / this.textureWidth, v / this.textureHeight, (u + uWidth) / this.textureWidth, (v + vHeight) / this.textureHeight);
        }

        public void blitRaw(float x1, float y1, float x2, float y2, float z, float u1, float v1, float u2, float v2) {
            this.ensureOpen();

            Matrix4f matrix = Graphics.this.pose.last().pose();
            Graphics.this.buffer
                    .addVertex(matrix, x1, y1, z).setUv(u1, v1)
                    .addVertex(matrix, x1, y2, z).setUv(u1, v2)
                    .addVertex(matrix, x2, y2, z).setUv(u2, v2)
                    .addVertex(matrix, x2, y1, z).setUv(u2, v1);
        }

        public void blit(int x, int y, int u, int v, int width, int height) {
            this.blit(x, y, 0, width, height, u, v, width, height);
        }

        public void blit(int x, int y, int width, int height, int u, int v, int uWidth, int vHeight) {
            this.blit(x, y, 0, width, height, u, v, uWidth, vHeight);
        }

        public void blit(float x, float y, float u, float v, float width, float height) {
            this.blit(x, y, 0.0f, width, height, u, v, width, height);
        }

        public void blit(float x, float y, float width, float height, float u, float v, float uWidth, float vHeight) {
            this.blit(x, y, 0.0f, width, height, u, v, uWidth, vHeight);
        }

        private void ensureOpen() {
            if (!this.open) {
                Graphics.this.cleanupState();
                Graphics.this.buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                this.open = true;
            }
        }

        @Override
        public void close() {
            this.draw();
            super.close();
            Graphics.this.renderers.remove(this);
        }

        @Override
        public void draw() {
            if (this.open) {
                assert Graphics.this.buffer != null;
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, this.texture);
                RenderSystem.enableBlend();
                BufferUploader.drawWithShader(Graphics.this.buffer.build());
                RenderSystem.disableBlend();
                this.open = false;
                Graphics.this.buffer = null;
            }
        }
    }

    public class Text implements AutoCloseable{
        private boolean textDirty = false;

        public int getSplitStringLines(String text, int width) {
            return this.getSplitStringLines(Component.literal(text), width);
        }

        public int getSplitStringLines(FormattedText text, int width) {
            return Graphics.this.font.split(text, width).size();
        }

        public int drawSplitText(String string, int x, int y, int width, int color) {
            return this.renderSplitString(string, x, y, width, color);
        }

        protected int renderSplitString(String str, int x, int y, int width, int color) {
            List<FormattedCharSequence> list = Graphics.this.font.split(Component.translatable(str), width);

            for (FormattedCharSequence line : list) {
                this.drawCenteredText(line, x, y, color);
                y += Graphics.this.font.lineHeight;
            }

            return list.size();
        }

        public int drawCenteredText(String text, int centerX, int y, int color) {
            return this.drawCenteredText(text, centerX, y, color, false);
        }

        public int drawCenteredText(String text, int centerX, int y, int color, boolean shadow) {
            return this.drawText(text, centerX - Graphics.this.font.width(text) / 2.0f, (float)y, color, shadow);
        }

        public int drawCenteredText(Component text, int centerX, int y, int color) {
            return this.drawCenteredText(text, centerX, y, color, false);
        }

        public int drawCenteredText(Component text, int centerX, int y, int color, boolean shadow) {
            return this.drawText(text, centerX - Graphics.this.font.width(text) / 2.0f, (float)y, color, shadow);
        }

        public int drawCenteredText(FormattedCharSequence text, int centerX, int y, int color) {
            return this.drawCenteredText(text, centerX, y, color, false);
        }

        public int drawCenteredText(FormattedCharSequence text, int centerX, int y, int color, boolean shadow) {
            return this.drawText(text, centerX - Graphics.this.font.width(text) / 2.0f, (float)y, color, shadow);
        }

        public int drawText(String text, int x, int y, int color) {
            return this.drawText(text, x, y, color, false);
        }

        public int drawText(String text, float x, float y, int color) {
            return this.drawText(text, x, y, color, false);
        }

        public int drawText(String text, float x, float y, int color, boolean shadow) {
            if (text == null) return 0;
            this.textDirty = true;
            return Graphics.this.font.drawInBatch(
                    text, x, y, color, shadow, Graphics.this.pose.last().pose(), Graphics.this.bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0, Graphics.this.font.isBidirectional()
            );
        }

        public int drawText(Component text, int x, int y, int color) {
            return this.drawText(text, x, y, color, false);
        }

        public int drawText(Component text, float x, float y, int color) {
            return this.drawText(text, x, y, color, false);
        }

        public int drawText(Component text, float x, float y, int color, boolean shadow) {
            if (text == null) return 0;
            this.textDirty = true;
            return Graphics.this.font.drawInBatch(
                    text, x, y, color, shadow, Graphics.this.pose.last().pose(), Graphics.this.bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0
            );
        }

        public int drawText(FormattedCharSequence text, int x, int y, int color) {
            return this.drawText(text, x, y, color, false);
        }

        public int drawText(FormattedCharSequence text, float x, float y, int color) {
            return this.drawText(text, x, y, color, false);
        }

        public int drawText(FormattedCharSequence text, float x, float y, int color, boolean shadow) {
            if (text == null) return 0;
            this.textDirty = true;
            return Graphics.this.font.drawInBatch(
                    text, x, y, color, shadow, Graphics.this.pose.last().pose(), Graphics.this.bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0
            );
        }

        public void flushText() {
            if (this.textDirty) {
                Graphics.this.bufferSource.endBatch();
                this.textDirty = false;
            }
        }

        @Override
        public void close() {
            this.flushText();
        }
    }

    public static void blitCentered(Matrix4f matrix, float x, float y, float width, float height, float z, float u, float v, float uWidth, float vHeight, int textureWidth, int textureHeight, ResourceLocation texture) {
        Graphics.blitRaw(matrix, x - width / 2.0f, y - height / 2.0f, x + width / 2.0f, y + height / 2.0f, z, u / textureWidth, v / textureHeight, (u + uWidth) / textureWidth, (v + vHeight) / textureHeight, texture);
    }

    public static void blit(Matrix4f matrix, int x, int y, int width, int height, int z, int u, int v, int textureWidth, int textureHeight, ResourceLocation texture) {
        Graphics.blit(matrix, x, y, width, height, z, u, v, width, height, textureWidth, textureHeight, texture);
    }

    public static void blit(Matrix4f matrix, int x, int y, int width, int height, int z, int u, int v, int uWidth, int vHeight, int textureWidth, int textureHeight, ResourceLocation texture) {
        Graphics.blitRaw(matrix, x, y, x + width, y + height, z, (float) u / textureWidth, (float) v / textureHeight, (float) (u + uWidth) / textureWidth, (float) (v + vHeight) / textureHeight, texture);
    }

    public static void blit(Matrix4f matrix, float x, float y, float width, float height, float z, float u, float v, int textureWidth, int textureHeight, ResourceLocation texture) {
        Graphics.blit(matrix, x, y, width, height, z, u, v, width, height, textureWidth, textureHeight, texture);
    }

    public static void blit(Matrix4f matrix, float x, float y, float width, float height, float z, float u, float v, float uWidth, float vHeight, int textureWidth, int textureHeight, ResourceLocation texture) {
        Graphics.blitRaw(matrix, x, y, x + width, y + height, z, u / textureWidth, v / textureHeight, (u + uWidth) / textureWidth, (v + vHeight) / textureHeight, texture);
    }

    public static void blit(Matrix4f matrix, float x, float y, int width, int height, float z, int u, int v, int textureWidth, int textureHeight, ResourceLocation texture) {
        Graphics.blit(matrix, x, y, width, height, z, u, v, width, height, textureWidth, textureHeight, texture);
    }

    public static void blit(Matrix4f matrix, float x, float y, int width, int height, float z, int u, int v, int uWidth, int vHeight, int textureWidth, int textureHeight, ResourceLocation texture) {
        Graphics.blitRaw(matrix, x, y, x + width, y + height, z, (float) u / textureWidth, (float) v / textureHeight, (float) (u + uWidth) / textureWidth, (float) (v + vHeight) / textureHeight, texture);
    }

    public static void blitRaw(Matrix4f matrix, float x1, float y1, float x2, float y2, float z, float u1, float v1, float u2, float v2, ResourceLocation texture) {
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, x1, y1, z).setUv(u1, v1)
                .addVertex(matrix, x1, y2, z).setUv(u1, v2)
                .addVertex(matrix, x2, y2, z).setUv(u2, v2)
                .addVertex(matrix, x2, y1, z).setUv(u2, v1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    public static void blitCentered(Matrix4f matrix, float x, float y, float width, float height, float z, float u, float v, float uWidth, float vHeight, int textureWidth, int textureHeight, ResourceLocation texture, int color) {
        Graphics.blitRaw(matrix, x - width / 2.0f, y - height / 2.0f, x + width / 2.0f, y + height / 2.0f, z, u / textureWidth, v / textureHeight, (u + uWidth) / textureWidth, (v + vHeight) / textureHeight, texture, color);
    }

    public static void blit(Matrix4f matrix, int x, int y, int width, int height, int z, int u, int v, int textureWidth, int textureHeight, ResourceLocation texture, int color) {
        Graphics.blit(matrix, x, y, width, height, z, u, v, width, height, textureWidth, textureHeight, texture, color);
    }

    public static void blit(Matrix4f matrix, int x, int y, int width, int height, int z, int u, int v, int uWidth, int vHeight, int textureWidth, int textureHeight, ResourceLocation texture, int color) {
        Graphics.blitRaw(matrix, x, y, x + width, y + height, z, (float) u / textureWidth, (float) v / textureHeight, (float) (u + uWidth) / textureWidth, (float) (v + vHeight) / textureHeight, texture, color);
    }

    public static void blit(Matrix4f matrix, float x, float y, float width, float height, float z, float u, float v, int textureWidth, int textureHeight, ResourceLocation texture, int color) {
        Graphics.blit(matrix, x, y, width, height, z, u, v, width, height, textureWidth, textureHeight, texture, color);
    }

    public static void blit(Matrix4f matrix, float x, float y, float width, float height, float z, float u, float v, float uWidth, float vHeight, int textureWidth, int textureHeight, ResourceLocation texture, int color) {
        Graphics.blitRaw(matrix, x, y, x + width, y + height, z, u / textureWidth, v / textureHeight, (u + uWidth) / textureWidth, (v + vHeight) / textureHeight, texture, color);
    }

    public static void blit(Matrix4f matrix, float x, float y, int width, int height, float z, int u, int v, int textureWidth, int textureHeight, ResourceLocation texture, int color) {
        Graphics.blit(matrix, x, y, width, height, z, u, v, width, height, textureWidth, textureHeight, texture, color);
    }

    public static void blit(Matrix4f matrix, float x, float y, int width, int height, float z, int u, int v, int uWidth, int vHeight, int textureWidth, int textureHeight, ResourceLocation texture, int color) {
        Graphics.blitRaw(matrix, x, y, x + width, y + height, z, (float) u / textureWidth, (float) v / textureHeight, (float) (u + uWidth) / textureWidth, (float) (v + vHeight) / textureHeight, texture, color);
    }

    public static void blitRaw(Matrix4f matrix, float x1, float y1, float x2, float y2, float z, float u1, float v1, float u2, float v2, ResourceLocation texture, int color) {
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        builder.addVertex(matrix, x1, y1, z).setUv(u1, v1).setColor(color)
                .addVertex(matrix, x1, y2, z).setUv(u1, v2).setColor(color)
                .addVertex(matrix, x2, y2, z).setUv(u2, v2).setColor(color)
                .addVertex(matrix, x2, y1, z).setUv(u2, v1).setColor(color);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, texture);
        BufferUploader.drawWithShader(builder.build());
    }

    private interface BatchedDrawable {
        void draw();
    }
}
