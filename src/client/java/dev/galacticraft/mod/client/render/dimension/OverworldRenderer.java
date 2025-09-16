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
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

// TODO: Allow support for more planets
public class OverworldRenderer extends SpaceSkyRenderer {
    @Nullable
    private VertexBuffer skyBuffer;
    private Minecraft minecraft = Minecraft.getInstance();
    protected final EarthManager earthManager = new EarthManager();

    public OverworldRenderer() {
        this.createSky();
    }

    private void createSky() {
        if (this.skyBuffer != null) {
            this.skyBuffer.close();
        }
        this.skyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.skyBuffer.bind();
        this.skyBuffer.upload(OverworldRenderer.buildSkyDisc(Tesselator.getInstance(), 16.0F));
        VertexBuffer.unbind();
    }

    private static MeshData buildSkyDisc(Tesselator tesselator, float f) {
        float f2 = Math.signum(f) * 512.0F;
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        bufferBuilder.addVertex(0.0F, f, 0.0F);
        for (int i = -180; i <= 180; i += 45) {
            bufferBuilder.addVertex(f2 * Mth.cos(((float) i) * Mth.DEG_TO_RAD), f, 512.0F * Mth.sin(((float) i) * Mth.DEG_TO_RAD));
        }
        return bufferBuilder.buildOrThrow();
    }

    public void renderOverworldSky(Player player, Matrix4f matrix4f, Matrix4f projectionMatrix, float partialTicks, Camera camera, boolean bl, Runnable runnable) {
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(matrix4f);
        Tesselator tesselator = Tesselator.getInstance();

        final Vec3 skyColor = this.getFogColor(this.minecraft.level, partialTicks, camera.getPosition());
        float x = (float) skyColor.x;
        float y = (float) skyColor.y;
        float z = (float) skyColor.z;

        FogRenderer.levelFogColor();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(x, y, z, 1.0F); // Top half of the sky
        this.skyBuffer.bind();
        this.skyBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, RenderSystem.getShader());
        VertexBuffer.unbind();
        RenderSystem.enableBlend();
        // RenderSystem.defaultBlendFunc();
        // final float[] sunriseColors = this.minecraft.level.effects().getSunriseColor(this.minecraft.level.getTimeOfDay(partialTicks), partialTicks);

        // if (sunriseColors != null) {
        //     final float sunsetModInv = Math.min(1.0F, Math.max(1.0F - theta * 50.0F, 0.0F));

        //     RenderSystem.setShader(GameRenderer::getPositionColorShader);
        //     poseStack.pushPose();
        //     poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        //     poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(this.minecraft.level.getSunAngle(partialTicks)) < 0.0F ? 180.0F : 0.0F));
        //     poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        //     x = sunriseColors[0] * sunsetModInv;
        //     y = sunriseColors[1] * sunsetModInv;
        //     z = sunriseColors[2] * sunsetModInv;

        //     BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

        //     buffer.addVertex(0.0F, 100.0F, 0.0F).setColor(x * sunsetModInv, y * sunsetModInv, z * sunsetModInv, sunriseColors[3]);

        //     float angle;
        //     for (int i = 0; i <= 16; ++i) {
        //         angle = i * Mth.TWO_PI / 16.0F;
        //         final float s = Mth.sin(angle);
        //         final float c = Mth.cos(angle);
        //         buffer.addVertex(s * 120.0F, c * 120.0F, -c * 40.0F * sunriseColors[3]).setColor(x, y, z, 0.0F);
        //     }

        //     BufferUploader.drawWithShader(buffer.buildOrThrow());
        //     poseStack.popPose();
        // }

        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        poseStack.pushPose();
        // float alpha = 1.0F - this.minecraft.level.getRainLevel(partialTicks);
        // RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // alpha);
        poseStack.mulPose(Axis.ZP.rotationDegrees(this.minecraft.level.getTimeOfDay(partialTicks) * 360.0F));

        // Draw stars
        // RenderSystem.disableBlend();
        // RenderSystem.defaultBlendFunc();
        // RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        // Vec3 vec = getFogColor(this.minecraft.level, partialTicks, camera.getPosition());
        // float threshold = Math.max(0.1F, (float) vec.length() - 0.1F);
        double playerHeight = player.getY();
        float fade = Math.min(((float) playerHeight - Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT) / ((float) Constant.SPACE_HEIGHT - Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT), 1.0F);
        // float bright1 = Math.min(0.9F, Mth.sqrt(fade) * 3);

        ClientLevel level = (ClientLevel) player.level();
        float starBrightness = Math.max(level.getStarBrightness(partialTicks), fade);
        this.starManager.render(poseStack, projectionMatrix, level, partialTicks, starBrightness);

        // if (bright1 > threshold) {
            // RenderSystem.setShaderColor(bright1, bright1, bright1, 1.0F);

        // Draw sun's halo
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );
        float size = 30.0F;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F - fade);
        RenderSystem.setShaderTexture(0, Constant.Skybox.SUN);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -size, 100.0F, -size).setUv(0.0F, 0.0F)
                .addVertex(matrix, size, 100.0F, -size).setUv(1.0F, 0.0F)
                .addVertex(matrix, size, 100.0F, size).setUv(1.0F, 1.0F)
                .addVertex(matrix, -size, 100.0F, size).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        // Draw sun
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, Math.min(5 * fade, 1.0F));
        RenderSystem.setShaderTexture(0, Constant.CelestialBody.SOL_FROM_MOON);
        size /= 4.0F;

        buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -size, 100.0F, -size).setUv(0.0F, 0.0F)
                .addVertex(matrix, size, 100.0F, -size).setUv(1.0F, 0.0F)
                .addVertex(matrix, size, 100.0F, size).setUv(1.0F, 1.0F)
                .addVertex(matrix, -size, 100.0F, size).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        // Draw moon's halo
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F - fade);
        size = 20.0F;
        RenderSystem.setShaderTexture(0, Constant.Skybox.MOON_PHASES);
        final float phase = this.minecraft.level.getMoonPhase();
        final int u = (int) (phase % 4);
        final int v = (int) (phase / 4 % 2);
        float u0 = u / 4.0F;
        float u1 = (u + 1) / 4.0F;
        float v0 = v / 2.0F;
        float v1 = (v + 1) / 2.0F;
        buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -size, -100.0F, size).setUv(u0, v1)
                .addVertex(matrix, size, -100.0F, size).setUv(u0, v0)
                .addVertex(matrix, size, -100.0F, -size).setUv(u1, v0)
                .addVertex(matrix, -size, -100.0F, -size).setUv(u1, v1);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        // Draw moon
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        size /= 4.0F;
        u0 += 0.375F / 4.0F;
        u1 -= 0.375F / 4.0F;
        v0 += 0.375F / 2.0F;
        v1 -= 0.375F / 2.0F;

        buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -size, -100.0F, size).setUv(u0, v1)
                .addVertex(matrix, size, -100.0F, size).setUv(u0, v0)
                .addVertex(matrix, size, -100.0F, -size).setUv(u1, v0)
                .addVertex(matrix, -size, -100.0F, -size).setUv(u1, v1);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        poseStack.popPose();

        float light = Mth.clamp(Mth.cos(this.minecraft.level.getTimeOfDay(partialTicks) * Mth.TWO_PI) * 2.0F + 1.0F, 0.5F, 1.0F);
        RenderSystem.setShaderColor(light, light, light, 1.0F);
        this.earthManager.render(poseStack, this.minecraft.level, 2.0D * (player.getY() - 64.0D), partialTicks, camera);

        poseStack.popPose();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static Vec3 getFogColor(ClientLevel level, float partialTicks, Vec3 cameraPos) {
        float heightOffset = ((float) (cameraPos.y()) - Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT) / 200.0F;
        heightOffset = Math.max(1.0F - 0.75F * Mth.sqrt(heightOffset), 0.0F);

        Vec3 skyColor = level.getSkyColor(cameraPos, partialTicks);
        return skyColor.scale(heightOffset);
    }
}
