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

package dev.galacticraft.mod.client.gl;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * More flexible version of {@link com.mojang.blaze3d.vertex.VertexBuffer}
 */
public class MeshBuffer {
    private GlVertexBuffer vbo;
    private GlElementArrayBuffer ebo;
    private GlVertexArray vao;
    private final VertexFormat format;
    public final BufferWriter buffer;
    public final BufferWriter indicesBuffer;

    public MeshBuffer(VertexFormat format) {
        RenderSystem.assertOnRenderThread();
        this.vbo = new GlVertexBuffer();
        this.vao = new GlVertexArray();
        this.ebo = new GlElementArrayBuffer();

        this.format = format;

        this.buffer = new BufferWriter(format, 786432);
        this.indicesBuffer = new BufferWriter(format, 786432);
    }

    public void upload(boolean dynamic) {
        this.vbo.bind();
        this.vao.bind();
        this.ebo.bind();
        format.setupBufferState();

        this.vbo.upload(this.buffer, dynamic);
        this.ebo.upload(this.indicesBuffer, dynamic);
    }

    /**
     * Render this mesh to the screen.
     * @param mode The current vertex mode to use (avoid using {@link GL11#GL_QUADS} and {@link GL11#GL_QUAD_STRIP} as they are deprecated)
     * @param modelMatrix The models transform to use in the shader
     */
    public void draw(int mode, PoseStack modelMatrix, ShaderInstance shader) {
        this.vao.bind();
        for(int i = 0; i < 12; ++i) {
            int j = RenderSystem.getShaderTexture(i);
            shader.setSampler("Sampler" + i, j);
        }

        if (shader.MODEL_VIEW_MATRIX != null) {
            shader.MODEL_VIEW_MATRIX.set(modelMatrix.last().pose());
        }

        if (shader.PROJECTION_MATRIX != null) {
            shader.PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());
        }

        if (shader.INVERSE_VIEW_ROTATION_MATRIX != null) {
            shader.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
        }

        if (shader.COLOR_MODULATOR != null) {
            shader.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
        }

        if (shader.GLINT_ALPHA != null) {
            shader.GLINT_ALPHA.set(RenderSystem.getShaderGlintAlpha());
        }

        if (shader.FOG_START != null) {
            shader.FOG_START.set(RenderSystem.getShaderFogStart());
        }

        if (shader.FOG_END != null) {
            shader.FOG_END.set(RenderSystem.getShaderFogEnd());
        }

        if (shader.FOG_COLOR != null) {
            shader.FOG_COLOR.set(RenderSystem.getShaderFogColor());
        }

        if (shader.FOG_SHAPE != null) {
            shader.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
        }

        if (shader.TEXTURE_MATRIX != null) {
            shader.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
        }

        if (shader.GAME_TIME != null) {
            shader.GAME_TIME.set(RenderSystem.getShaderGameTime());
        }

        if (shader.SCREEN_SIZE != null) {
            Window window = Minecraft.getInstance().getWindow();
            shader.SCREEN_SIZE.set((float)window.getWidth(), (float)window.getHeight());
        }

        if (shader.LINE_WIDTH != null && (mode == GlConst.GL_LINES || mode == GlConst.GL_LINE_STRIP)) {
            shader.LINE_WIDTH.set(RenderSystem.getShaderLineWidth());
        }

        RenderSystem.setupShaderLights(shader);
        shader.apply();

        this.ebo.bind();
        RenderSystem.drawElements(mode, this.indicesBuffer.data.position() / 4, GlConst.GL_UNSIGNED_INT);
        shader.clear();
        this.vao.unbind();
    }

    /**
     * Render this mesh to the screen.
     * @param modelMatrix The models transform to use in the shader
     */
    public void draw(PoseStack modelMatrix, ShaderInstance shader) {
//        RenderSystem.setShader(() -> shader);
        draw(GlConst.GL_TRIANGLES, modelMatrix, shader);
    }

    public void delete() {
        this.vao.delete();
        this.ebo.delete();
        this.vbo.delete();
        this.buffer.free();
        this.indicesBuffer.free();
    }
}
