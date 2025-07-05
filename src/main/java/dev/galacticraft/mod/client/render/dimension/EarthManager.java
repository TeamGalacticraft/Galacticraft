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

package dev.galacticraft.mod.client.render.dimension;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.galacticraft.mod.Constant;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

public class EarthManager {
    public void render(PoseStack matrices, Level level, double y, float partialTicks) {
        float size = (float) (6000.0D * Math.tan(Math.asin(128.0D / (128.0D + y))));

        matrices.pushPose();
        Matrix4f matrix = matrices.last().pose();
        Tesselator tesselator = Tesselator.getInstance();

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );
        RenderSystem.setShaderTexture(0, Constant.Skybox.ATMOSPHERE);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -size, -101.0F, size).setUv(0.0F, 1.0F)
                .addVertex(matrix, size, -101.0F, size).setUv(1.0F, 1.0F)
                .addVertex(matrix, size, -101.0F, -size).setUv(1.0F, 0.0F)
                .addVertex(matrix, -size, -101.0F, -size).setUv(0.0F, 0.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        matrices.popPose();

        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();

        matrices.pushPose();
        matrix = matrices.last().pose();

        RenderSystem.setShaderTexture(0, Constant.Skybox.EARTH);
        size *= 0.5F;
        buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -size, -100.0F, size).setUv(0.0F, 1.0F)
                .addVertex(matrix, size, -100.0F, size).setUv(1.0F, 1.0F)
                .addVertex(matrix, size, -100.0F, -size).setUv(1.0F, 0.0F)
                .addVertex(matrix, -size, -100.0F, -size).setUv(0.0F, 0.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        matrices.popPose();

        matrices.pushPose();
        matrix = matrices.last().pose();

        RenderSystem.setShaderTexture(0, Constant.Skybox.CLOUDS);
        float height = -100.0F + (float) ((size * 25.0D) / y);
        float u0 = (-2.0F * level.getTimeOfDay(partialTicks)) % 1;
        float u1 = u0 + 1.0F;
        buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -size, height, size).setUv(u0, 1.0F)
                .addVertex(matrix, size, height, size).setUv(u1, 1.0F)
                .addVertex(matrix, size, height, -size).setUv(u1, 0.0F)
                .addVertex(matrix, -size, height, -size).setUv(u0, 0.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        matrices.popPose();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }
}