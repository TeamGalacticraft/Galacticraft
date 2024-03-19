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

package dev.galacticraft.mod.client.render.dimension;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class VenusSkyRenderer implements DimensionRenderingRegistry.SkyRenderer {
    public static final VenusSkyRenderer INSTANCE = new VenusSkyRenderer();


    private final Minecraft minecraft = Minecraft.getInstance();
    public VertexBuffer starBuffer;
    public VertexBuffer skyBuffer;
    public VertexBuffer darkBuffer;
    private float sunSize;

    public VenusSkyRenderer() {
        this.sunSize = 30.0F * (1.0F / 0.75F);

        this.starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);

        // Bind stars to display list
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuilder();
        this.starBuffer.bind();
        this.starBuffer.upload(this.renderStars(worldRenderer));
        VertexBuffer.unbind();

        this.skyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        final byte byte2 = 64;
        final int i = 256 / byte2 + 2;
        float f = 16F;
        RenderSystem.setShader(GameRenderer::getPositionShader);
        worldRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        for (int j = -byte2 * i; j <= byte2 * i; j += byte2) {
            for (int l = -byte2 * i; l <= byte2 * i; l += byte2) {

                worldRenderer.vertex(j, f, l).endVertex();
                worldRenderer.vertex(j + byte2, f, l).endVertex();
                worldRenderer.vertex(j + byte2, f, l + byte2).endVertex();
                worldRenderer.vertex(j, f, l + byte2).endVertex();
            }
        }
        this.skyBuffer.bind();
        this.skyBuffer.upload(worldRenderer.end());
        VertexBuffer.unbind();

        this.darkBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        f = -16F;
        worldRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        for (int k = -byte2 * i; k <= byte2 * i; k += byte2) {
            for (int i1 = -byte2 * i; i1 <= byte2 * i; i1 += byte2) {
                worldRenderer.vertex(k + byte2, f, i1 + 0).endVertex();
                worldRenderer.vertex(k + 0, f, i1 + 0).endVertex();
                worldRenderer.vertex(k + 0, f, i1 + byte2).endVertex();
                worldRenderer.vertex(k + byte2, f, i1 + byte2).endVertex();
            }
        }

        this.darkBuffer.bind();
        this.darkBuffer.upload(worldRenderer.end());
        VertexBuffer.unbind();
    }

    private BufferBuilder.RenderedBuffer renderStars(BufferBuilder worldRenderer) {
        RandomSource rand = RandomSource.create(10842L);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        worldRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        for (int starIndex = 0; starIndex < 35000; ++starIndex) {
            double var4 = rand.nextFloat() * 2.0F - 1.0F;
            double var6 = rand.nextFloat() * 2.0F - 1.0F;
            double var8 = rand.nextFloat() * 2.0F - 1.0F;
            final double var10 = 0.15F + rand.nextFloat() * 0.1F;
            double var12 = var4 * var4 + var6 * var6 + var8 * var8;

            if (var12 < 1.0D && var12 > 0.01D) {
                var12 = 1.0D / Math.sqrt(var12);
                var4 *= var12;
                var6 *= var12;
                var8 *= var12;
                final double var14 = var4 * (rand.nextDouble() * 150D + 130D);
                final double var16 = var6 * (rand.nextDouble() * 150D + 130D);
                final double var18 = var8 * (rand.nextDouble() * 150D + 130D);
                final double var20 = Math.atan2(var4, var8);
                final double var22 = Math.sin(var20);
                final double var24 = Math.cos(var20);
                final double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
                final double var28 = Math.sin(var26);
                final double var30 = Math.cos(var26);
                final double var32 = rand.nextDouble() * Math.PI * 2.0D;
                final double var34 = Math.sin(var32);
                final double var36 = Math.cos(var32);

                for (int var38 = 0; var38 < 4; ++var38) {
                    final double var39 = 0.0D;
                    final double var41 = ((var38 & 2) - 1) * var10;
                    final double var43 = ((var38 + 1 & 2) - 1) * var10;
                    final double var47 = var41 * var36 - var43 * var34;
                    final double var49 = var43 * var36 + var41 * var34;
                    final double var53 = var47 * var28 + var39 * var30;
                    final double var55 = var39 * var28 - var47 * var30;
                    final double var57 = var55 * var22 - var49 * var24;
                    final double var61 = var49 * var22 + var55 * var24;
                    worldRenderer.vertex(var14 + var57, var16 + var53, var18 + var61).endVertex();
                }
            }
        }

        return worldRenderer.end();
    }

    @Override
    public void render(WorldRenderContext context) {
        ClientLevel level = context.world();
        float partialTicks = context.tickDelta();
        PoseStack poseStack = context.matrixStack();
        Vec3 vec3 = level.getSkyColor(context.camera().getPosition(), partialTicks);
        float f1 = (float) vec3.x;
        float f2 = (float) vec3.y;
        float f3 = (float) vec3.z;
        float f6;

        Tesselator tessellator1 = Tesselator.getInstance();
        BufferBuilder worldRenderer1 = tessellator1.getBuilder();
        RenderSystem.depthMask(false);
        FogRenderer.levelFogColor();
        RenderSystem.setShaderColor(f1, f2, f3, 1.0F);
        ShaderInstance shader = RenderSystem.getShader();
        this.skyBuffer.bind();
        this.skyBuffer.drawWithShader(poseStack.last().pose(), context.projectionMatrix(), shader);
        VertexBuffer.unbind();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        float f7;
        float f8;
        float f9;
        float f10;

        float starBrightness = level.getStarBrightness(partialTicks);

        if (starBrightness > 0.0F) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YN.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.XN.rotationDegrees(level.getTimeOfDay(partialTicks) * 360.0F));
            poseStack.mulPose(Axis.YN.rotationDegrees(-19.0F));
            RenderSystem.setShaderColor(starBrightness, starBrightness, starBrightness, starBrightness);
            FogRenderer.setupNoFog();
            this.starBuffer.bind();
            this.starBuffer.drawWithShader(poseStack.last().pose(), context.projectionMatrix(), GameRenderer.getPositionShader());
            VertexBuffer.unbind();
            poseStack.popPose();
        }

        float[] afloat = new float[4];
        poseStack.pushPose();
        poseStack.mulPose(Axis.YN.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.XN.rotationDegrees(level.getTimeOfDay(partialTicks) * 360.0F));
        afloat[0] = 255 / 255.0F;
        afloat[1] = 194 / 255.0F;
        afloat[2] = 180 / 255.0F;
        afloat[3] = 0.3F;
        f6 = afloat[0];
        f7 = afloat[1];
        f8 = afloat[2];

        starBrightness = 1.0F - starBrightness;

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        worldRenderer1.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        float r = f6 * starBrightness;
        float g = f7 * starBrightness;
        float b = f8 * starBrightness;
        float a = afloat[3] * 2 / starBrightness;
        worldRenderer1.vertex(poseStack.last().pose(), 0.0F, 100.0F, 0.0F).color(r, g, b, a).endVertex();
        r = afloat[0] * starBrightness;
        g = afloat[1] * starBrightness;
        b = afloat[2] * starBrightness / 20.0F;
        a = 0.0F;

        // Render sun aura
        f10 = 20.0F;
        Matrix4f last = poseStack.last().pose();
        worldRenderer1.vertex(last, -f10, 100.0F, -f10).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, 0, 100.0F, -f10 * 1.5F).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, f10, 100.0F, -f10).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, f10 * 1.5F, 100.0F, 0).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, f10, 100.0F, f10).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, 0, 100.0F, f10 * 1.5F).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, -f10, 100.0F, f10).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, -f10 * 1.5F, 100.0F, 0).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, -f10, 100.0F, -f10).color(r, g, b, a).endVertex();

        tessellator1.end();

        worldRenderer1.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        r = f6 * starBrightness;
        g = f7 * starBrightness;
        b = f8 * starBrightness;
        a = afloat[3] * starBrightness;
        worldRenderer1.vertex(last, 0.0F, 100.0F, 0.0F).color(r, g, b, a).endVertex();
        r = afloat[0] * starBrightness;
        g = afloat[1] * starBrightness;
        b = afloat[2] * starBrightness;
        a = 0.0F;

        // Render larger sun aura
        f10 = 40.0F;
        worldRenderer1.vertex(last, -f10, 100.0F, -f10).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, 0, 100.0F, -f10 * 1.5F).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, f10, 100.0F, -f10).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, f10 * 1.5F, 100.0F, 0).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, f10, 100.0F, f10).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, 0, 100.0F, f10 * 1.5F).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, -f10, 100.0F, f10).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, -f10 * 1.5F, 100.0F, 0).color(r, g, b, a).endVertex();
        worldRenderer1.vertex(last, -f10, 100.0F, -f10).color(r, g, b, a).endVertex();

        tessellator1.end();
        poseStack.popPose();

        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        poseStack.pushPose();
        f7 = 0.0F;
        f8 = 0.0F;
        f9 = 0.0F;
        poseStack.translate(f7, f8, f9);
        poseStack.mulPose(Axis.YN.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(-level.getTimeOfDay(partialTicks) * 360.0F));
        // Render sun
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
        // Some blanking to conceal the stars
        f10 = this.sunSize / 3.5F;
        Matrix4f last2 = poseStack.last().pose();
        worldRenderer1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        worldRenderer1.vertex(last2, -f10, 99.9F, -f10).endVertex();
        worldRenderer1.vertex(last2, f10, 99.9F, -f10).endVertex();
        worldRenderer1.vertex(last2, f10, 99.9F, f10).endVertex();
        worldRenderer1.vertex(last2, -f10, 99.9F, f10).endVertex();
        tessellator1.end();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.1F);
        f10 = this.sunSize;
        RenderSystem.setShaderTexture(0, CelestialBodyTextures.ATMOSPHERIC_SUN);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        worldRenderer1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        worldRenderer1.vertex(last2, -f10, 100.0F, -f10).uv(0.0F, 0.0F).endVertex();
        worldRenderer1.vertex(last2, f10, 100.0F, -f10).uv(1.0F, 0.0F).endVertex();
        worldRenderer1.vertex(last2, f10, 100.0F, f10).uv(1.0F, 1.0F).endVertex();
        worldRenderer1.vertex(last2, -f10, 100.0F, f10).uv(0.0F, 1.0F).endVertex();
        tessellator1.end();

        // Render earth
        f10 = 0.5F;
        poseStack.scale(0.6F, 0.6F, 0.6F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(40.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(200F));
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1F);
        RenderSystem.setShaderTexture(0, CelestialBodyTextures.EARTH);
        worldRenderer1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        worldRenderer1.vertex(last2, -f10, -100.0F, f10).uv(0, 1.0F).endVertex();
        worldRenderer1.vertex(last2, f10, -100.0F, f10).uv(1.0F, 1.0F).endVertex();
        worldRenderer1.vertex(last2, f10, -100.0F, -f10).uv(1.0F, 0).endVertex();
        worldRenderer1.vertex(last2, -f10, -100.0F, -f10).uv(0, 0).endVertex();
        tessellator1.end();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
//        GL11.glEnable(GL11.GL_FOG);
        poseStack.popPose();
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
        double horizon = minecraft.player.getEyePosition().y - level.getLevelData().getHorizonHeight(level);

        if (horizon < 0.0D) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 12.0F, 0.0F);
            this.darkBuffer.bind();
            this.darkBuffer.drawWithShader(poseStack.last().pose(), context.projectionMatrix(), shader);
            VertexBuffer.unbind();
            poseStack.popPose();
            f8 = 1.0F;
            f9 = -((float) (horizon + 65.0D));
            f10 = -f8;
            Matrix4f last3 = poseStack.last().pose();
            worldRenderer1.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            worldRenderer1.vertex(last3, -f8, f9, f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, f8, f9, f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, f8, f10, f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, -f8, f10, f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, -f8, f10, -f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, f8, f10, -f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, f8, f9, -f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, -f8, f9, -f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, f8, f10, -f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, f8, f10, f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, f8, f9, f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, f8, f9, -f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, -f8, f9, -f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, -f8, f9, f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, -f8, f10, f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, -f8, f10, -f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, -f8, f10, -f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, -f8, f10, f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, f8, f10, f8).color(0, 0, 0, 1.0F).endVertex();
            worldRenderer1.vertex(last3, f8, f10, -f8).color(0, 0, 0, 1.0F).endVertex();
            tessellator1.end();
        }

        poseStack.pushPose();
        poseStack.translate(0.0F, -((float) (horizon - 16.0D)), 0.0F);
        this.darkBuffer.bind();
        this.darkBuffer.drawWithShader(poseStack.last().pose(), context.projectionMatrix(), shader);
        VertexBuffer.unbind();
        poseStack.popPose();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend();
    }
}
