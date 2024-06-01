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

package dev.galacticraft.mod.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Graphics implements AutoCloseable {
    private final PoseStack pose;
    private final BufferBuilder buffer;
    private final MultiBufferSource.BufferSource bufferSource;
    private final List<BatchedDrawable> renderers = new ArrayList<>(1);

    private Graphics(PoseStack pose, MultiBufferSource.BufferSource bufferSource) {
        this.pose = pose;
        this.bufferSource = bufferSource;
        this.buffer = Tesselator.getInstance().getBuilder();
        if (this.buffer.building()) throw new IllegalStateException();
    }

    public static Graphics managed(GuiGraphics graphics) {
        return new Graphics(graphics.pose(), graphics.bufferSource());
    }

    public static Graphics texOnly(Matrix4f matrix4f) {
        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(matrix4f);
        return new Graphics(poseStack, null);
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

    public Fill fill(RenderType type) {
        return new Fill(type);
    }

    public void cleanupState() {
        for (BatchedDrawable renderer : this.renderers) {
            renderer.draw();
        }
    }

    @Override
    public void close() {
        this.cleanupState();
    }

    public class Lines implements AutoCloseable, BatchedDrawable {
        private final RenderType type;
        private VertexConsumer consumer;
        private boolean open = true;

        private Lines(RenderType type) {
            this.type = type;
            this.consumer = Graphics.this.bufferSource.getBuffer(this.type);
            Graphics.this.renderers.add(this);
        }

        public void line(float x1, float y1, float x2, float y2, float z, int color) {
            this.lineGradient(x1, y1, x2, y2, z, color, color);
        }

        public void lineGradient(float x1, float y1, float x2, float y2, float z, int color1, int color2) {
            this.ensureOpen();

            Matrix4f matrix = Graphics.this.pose.last().pose();
            this.consumer.vertex(matrix, x1, y1, z).color(color1).endVertex();
            this.consumer.vertex(matrix, x2, y2, z).color(color2).endVertex();
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
        private boolean open = true;

        private Fill(RenderType type) {
            this.type = type;
            this.consumer = Graphics.this.bufferSource.getBuffer(this.type);
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

        public void fillGradientRaw(float x1, float y1, float x2, float y2, float z, int colorA, int colorB) {
            this.ensureOpen();

            Matrix4f matrix = Graphics.this.pose.last().pose();
            this.consumer.vertex(matrix, x1, y1, z).color(colorA).endVertex();
            this.consumer.vertex(matrix, x1, y2, z).color(colorB).endVertex();
            this.consumer.vertex(matrix, x2, y2, z).color(colorB).endVertex();
            this.consumer.vertex(matrix, x2, y1, z).color(colorA).endVertex();
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

    public class TextureColor implements AutoCloseable, BatchedDrawable {
        private final int texture;
        private final int textureWidth;
        private final int textureHeight;
        private boolean open = true;

        private TextureColor(int texture, int textureWidth, int textureHeight) {
            this.texture = texture;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            Graphics.this.renderers.add(this);
        }

        public void blit(int x, int y, int width, int height, int z, int u, int v, int color) {
            this.blit(x, y, width, height, z, u, v, width, height, color);
        }

        public void blit(int x, int y, int width, int height, int z, int u, int v, int uWidth, int vHeight, int color) {
            this.blitRaw(x, y, x + width, y + height, z, (float) u / this.textureWidth, (float) v / this.textureHeight, (float) (u + uWidth) / this.textureWidth, (float) (v + vHeight) / this.textureHeight, color);
        }

        public void blit(float x, float y, float width, float height, float z, float u, float v, float uWidth, float vHeight, int color) {
            this.blitRaw(x, y, x + width, y + height, z, u / this.textureWidth, v / this.textureHeight, (u + uWidth) / this.textureWidth, (v + vHeight) / this.textureHeight, color);
        }

        public void blitRaw(float x1, float y1, float x2, float y2, float z, float u1, float v1, float u2, float v2, int color) {
            this.ensureOpen();

            Matrix4f matrix = Graphics.this.pose.last().pose();
            Graphics.this.buffer.vertex(matrix, x1, y1, z).uv(u1, v1).color(color).endVertex();
            Graphics.this.buffer.vertex(matrix, x1, y2, z).uv(u1, v2).color(color).endVertex();
            Graphics.this.buffer.vertex(matrix, x2, y2, z).uv(u2, v2).color(color).endVertex();
            Graphics.this.buffer.vertex(matrix, x2, y1, z).uv(u2, v1).color(color).endVertex();
        }

        private void ensureOpen() {
            if (!this.open) {
                Graphics.this.cleanupState();
                Graphics.this.buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
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
                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, this.texture);
                RenderSystem.enableBlend();
                BufferUploader.drawWithShader(Graphics.this.buffer.end());
                this.open = false;
            }
        }
    }

    public class Texture implements AutoCloseable, BatchedDrawable {
        private final int texture;
        private final int textureWidth;
        private final int textureHeight;
        private boolean open = true;

        private Texture(int texture, int textureWidth, int textureHeight) {
            this.texture = texture;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            Graphics.this.renderers.add(this);
        }

        public void blit(int x, int y, int width, int height, int z, int u, int v) {
            this.blit(x, y, width, height, z, u, v, width, height);
        }

        public void blit(int x, int y, int width, int height, int z, int u, int v, int uWidth, int vHeight) {
            this.blitRaw(x, y, x + width, y + height, z, (float) u / this.textureWidth, (float) v / this.textureHeight, (float) (u + uWidth) / this.textureWidth, (float) (v + vHeight) / this.textureHeight);
        }

        public void blit(float x, float y, float width, float height, float z, float u, float v, float uWidth, float vHeight) {
            this.blitRaw(x, y, x + width, y + height, z, u / this.textureWidth, v / this.textureHeight, (u + uWidth) / this.textureWidth, (v + vHeight) / this.textureHeight);
        }

        public void blitRaw(float x1, float y1, float x2, float y2, float z, float u1, float v1, float u2, float v2) {
            this.ensureOpen();

            Matrix4f matrix = Graphics.this.pose.last().pose();
            Graphics.this.buffer.vertex(matrix, x1, y1, z).uv(u1, v1).endVertex();
            Graphics.this.buffer.vertex(matrix, x1, y2, z).uv(u1, v2).endVertex();
            Graphics.this.buffer.vertex(matrix, x2, y2, z).uv(u2, v2).endVertex();
            Graphics.this.buffer.vertex(matrix, x2, y1, z).uv(u2, v1).endVertex();
        }

        private void ensureOpen() {
            if (!this.open) {
                Graphics.this.cleanupState();
                Graphics.this.buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
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
                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, this.texture);
                RenderSystem.enableBlend();
                BufferUploader.drawWithShader(Graphics.this.buffer.end());
                this.open = false;
            }
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
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(matrix, x1, y1, z).uv(u1, v1).endVertex();
        builder.vertex(matrix, x1, y2, z).uv(u1, v2).endVertex();
        builder.vertex(matrix, x2, y2, z).uv(u2, v2).endVertex();
        builder.vertex(matrix, x2, y1, z).uv(u2, v1).endVertex();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        BufferUploader.drawWithShader(builder.end());
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
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        builder.vertex(matrix, x1, y1, z).uv(u1, v1).color(color).endVertex();
        builder.vertex(matrix, x1, y2, z).uv(u1, v2).color(color).endVertex();
        builder.vertex(matrix, x2, y2, z).uv(u2, v2).color(color).endVertex();
        builder.vertex(matrix, x2, y1, z).uv(u2, v1).color(color).endVertex();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, texture);
        BufferUploader.drawWithShader(builder.end());
    }

    private interface BatchedDrawable {
        void draw();
    }
}
