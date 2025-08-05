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
import com.mojang.math.Axis;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

public class MoonSkyRenderer extends SpaceSkyRenderer {
    public static final MoonSkyRenderer INSTANCE = new MoonSkyRenderer();

    @Override
    public void render(WorldRenderContext context) {
        RenderSystem.disableBlend();
        RenderSystem.depthMask(false);

        float partialTicks = context.tickCounter().getGameTimeDeltaPartialTick(true);
        PoseStack matrices = new PoseStack();
        matrices.mulPose(context.positionMatrix());

        context.profiler().push("celestial_render");
        matrices.pushPose();
        matrices.mulPose(Axis.ZP.rotationDegrees(context.world().getTimeOfDay(partialTicks) * 360.0F));

        // Update camera position for star rendering
        this.celestialBodyRendererManager.updateSolarPosition(
                0, // Math.cos(System.currentTimeMillis() / 5000.0 * Math.PI * 2) * 30,
                0, // -10,
                0  // Math.sin(System.currentTimeMillis() / 5000.0 * Math.PI * 2) * 30
        );

        this.celestialBodyRendererManager.render(context);

        context.profiler().pop();
        RenderSystem.setShaderColor(1.0f, 1.0F, 1.0F, 1.0F);

        context.profiler().push("sun");

        Matrix4f matrix = matrices.last().pose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );
        float size = 30.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, Constant.Skybox.SUN_MOON);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -size, 100.0F, -size).setUv(0.0F, 0.0F)
                .addVertex(matrix, size, 100.0F, -size).setUv(1.0F, 0.0F)
                .addVertex(matrix, size, 100.0F, size).setUv(1.0F, 1.0F)
                .addVertex(matrix, -size, 100.0F, size).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        size /= 4.0F;
        buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -size, 100.0F, -size).setUv(0.375F, 0.375F)
                .addVertex(matrix, size, 100.0F, -size).setUv(0.625F, 0.375F)
                .addVertex(matrix, size, 100.0F, size).setUv(0.625F, 0.625F)
                .addVertex(matrix, -size, 100.0F, size).setUv(0.375F, 0.625F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.disableBlend();
        matrices.popPose();
        context.profiler().pop();

        context.profiler().push("earth");
        matrices.pushPose();
        matrix = matrices.last().pose();

        size = 10.0F;
        assert Minecraft.getInstance().player != null;
        float earthRotation = (float) (context.world().getSharedSpawnPos().getZ() - Minecraft.getInstance().player.getZ()) * 0.01F;
        matrices.scale(0.6F, 0.6F, 0.6F);
        matrices.mulPose(Axis.XP.rotationDegrees((context.world().getTimeOfDay(context.tickCounter().getRealtimeDeltaTicks()) * 360.0F) * 0.001F));
        matrices.mulPose(Axis.XP.rotationDegrees(earthRotation + 200.0F));
        matrices.mulPose(Axis.YP.rotationDegrees(180.0F));

        RenderSystem.setShaderTexture(0, Constant.CelestialBody.EARTH);

        buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -size, -100.0F, size).setUv(0.0F, 1.0F)
                .addVertex(matrix, size, -100.0F, size).setUv(1.0F, 1.0F)
                .addVertex(matrix, size, -100.0F, -size).setUv(1.0F, 0.0F)
                .addVertex(matrix, -size, -100.0F, -size).setUv(0.0F, 0.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        context.profiler().pop();
        matrices.popPose();

        RenderSystem.depthMask(true);
    }
}
