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

public class MarsSkyRenderer implements DimensionRenderingRegistry.SkyRenderer {
    public static final MarsSkyRenderer INSTANCE = new MarsSkyRenderer();


    private final Minecraft minecraft = Minecraft.getInstance();
    public VertexBuffer starBuffer;
    public VertexBuffer skyBuffer;
    public VertexBuffer darkBuffer;
    private final float sunSize;

    public MarsSkyRenderer() {
        this.sunSize = 7.0F * (1.0F / 0.75F);

        this.starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);

        // Bind stars to display list
        this.starBuffer.bind();
        this.starBuffer.upload(this.renderStars());
        VertexBuffer.unbind();

        this.skyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        final byte byte2 = 64;
        final int i = 256 / byte2 + 2;
        float f = 16F;
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        RenderSystem.setShader(GameRenderer::getPositionShader);
        for (int j = -byte2 * i; j <= byte2 * i; j += byte2) {
            for (int l = -byte2 * i; l <= byte2 * i; l += byte2) {

                buffer.addVertex(j, f, l)
                        .addVertex(j + byte2, f, l)
                        .addVertex(j + byte2, f, l + byte2)
                        .addVertex(j, f, l + byte2);
            }
        }
        this.skyBuffer.bind();
        this.skyBuffer.upload(buffer.buildOrThrow());
        VertexBuffer.unbind();

        this.darkBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        f = -16F;
        buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        for (int k = -byte2 * i; k <= byte2 * i; k += byte2) {
            for (int i1 = -byte2 * i; i1 <= byte2 * i; i1 += byte2) {
                buffer.addVertex(k + byte2, f, i1 + 0)
                        .addVertex(k + 0, f, i1 + 0)
                        .addVertex(k + 0, f, i1 + byte2)
                        .addVertex(k + byte2, f, i1 + byte2);
            }
        }

        this.darkBuffer.bind();
        this.darkBuffer.upload(buffer.buildOrThrow());
        VertexBuffer.unbind();
    }

    private MeshData renderStars() {
        RandomSource rand = RandomSource.create(10842L);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

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
                    buffer.addVertex((float) (var14 + var57), (float) (var16 + var53), (float) (var18 + var61));
                }
            }
        }

        return buffer.buildOrThrow();
    }

    @Override
    public void render(WorldRenderContext context) {
        ClientLevel level = context.world();
        float partialTicks = context.tickCounter().getGameTimeDeltaPartialTick(true);

        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(context.positionMatrix());

        Vec3 skyCol = level.getSkyColor(context.camera().getPosition(), partialTicks);
        float rSky = (float) skyCol.x;
        float gSky = (float) skyCol.y;
        float bSky = (float) skyCol.z;

        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(rSky, gSky, bSky, 1.0F);

        ShaderInstance skyShader = RenderSystem.getShader();
        this.skyBuffer.bind();
        this.skyBuffer.drawWithShader(poseStack.last().pose(), context.projectionMatrix(), skyShader);
        VertexBuffer.unbind();

        float time = level.getTimeOfDay(partialTicks);

        // Stars
        float night = MarsDimensionEffects.marsNightFactor(level, partialTicks);
        float starBrightness = night * night;
        if (starBrightness > 0.0F) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(time * 360.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(-19.0F));

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.setShaderColor(starBrightness, starBrightness, starBrightness, starBrightness);

            FogRenderer.setupNoFog();
            this.starBuffer.bind();
            this.starBuffer.drawWithShader(poseStack.last().pose(), context.projectionMatrix(), GameRenderer.getPositionShader());
            VertexBuffer.unbind();
            RenderSystem.disableBlend();
            poseStack.popPose();
        }

        // TODO: Add Mars halo, adjust Sun size if needed, adjust Earth size/position if needed
        // Sun + Earth
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(time * 360.0F));

        Matrix4f mat = poseStack.last().pose();

        // Sun
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, Constant.Skybox.SUN_MARS);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        float sunSize = this.sunSize;
        BufferBuilder bufSun = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX
        );
        bufSun.addVertex(mat, -sunSize, 100.0F, -sunSize).setUv(0.0F, 0.0F);
        bufSun.addVertex(mat,  sunSize, 100.0F, -sunSize).setUv(1.0F, 0.0F);
        bufSun.addVertex(mat,  sunSize, 100.0F,  sunSize).setUv(1.0F, 1.0F);
        bufSun.addVertex(mat, -sunSize, 100.0F,  sunSize).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(bufSun.buildOrThrow());


        // Earth
        RenderSystem.disableCull();  // make sure it never disappears from facing the wrong way

        float earthSize = 0.5F;

        // offset relative to the sun's center
        float offX = -sunSize * -6.0F;
        float offZ = -sunSize * 3.0F;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, Constant.CelestialBody.EARTH);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        BufferBuilder earthBuf = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX
        );

        // +offX / +offZ relative to the sun
        earthBuf.addVertex(mat, offX - earthSize, 100.0F, offZ + earthSize).setUv(0.0F, 1.0F);
        earthBuf.addVertex(mat, offX + earthSize, 100.0F, offZ + earthSize).setUv(1.0F, 1.0F);
        earthBuf.addVertex(mat, offX + earthSize, 100.0F, offZ - earthSize).setUv(1.0F, 0.0F);
        earthBuf.addVertex(mat, offX - earthSize, 100.0F, offZ - earthSize).setUv(0.0F, 0.0F);

        BufferUploader.drawWithShader(earthBuf.buildOrThrow());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        poseStack.popPose();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
}
