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
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

// TODO: Allow support for more planets
public class OverworldRenderer {
    public static final ResourceLocation MOON_LOCATION = new ResourceLocation("textures/environment/moon_phases.png");
    @Nullable
    private VertexBuffer starBuffer;
    @Nullable
    private VertexBuffer starBuffer2;
    @Nullable
    private VertexBuffer starBuffer3;
    @Nullable
    private VertexBuffer starBuffer4;
    private Minecraft minecraft = Minecraft.getInstance();
    public OverworldRenderer() {
        RandomSource rand = RandomSource.create(10842L);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        if (this.starBuffer != null) {
            this.starBuffer.close();
        }

        this.starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.starBuffer.bind();
        this.starBuffer.upload(this.renderStars(worldrenderer, rand));
        VertexBuffer.unbind();
        this.starBuffer2 = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.starBuffer2.bind();
        this.starBuffer2.upload(this.renderStars(worldrenderer, rand));
        VertexBuffer.unbind();
        this.starBuffer3 = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.starBuffer3.bind();
        this.starBuffer3.upload(this.renderStars(worldrenderer, rand));
        VertexBuffer.unbind();
        this.starBuffer4 = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.starBuffer4.bind();
        this.starBuffer4.upload(this.renderStars(worldrenderer, rand));
        VertexBuffer.unbind();
    }
    public void renderOverworldSky(Player player, PoseStack poseStack, Matrix4f matrix4f, float partialTicks, Camera camera, boolean bl, Runnable runnable) {
        float theta = Mth.sqrt(((float) (player.getY()) - 200) / 1000.0F);
        final float var21 = Math.max(1.0F - theta * 4.0F, 0.0F);

        final Vec3 skyColor = this.minecraft.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), partialTicks);
        float i = (float) skyColor.x * var21;
        float x = (float) skyColor.y * var21;
        float var5 = (float) skyColor.z * var21;
        float z;

        FogRenderer.levelFogColor();
        RenderSystem.setShaderColor(i, x, var5, 1.0F);
        final Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder worldRenderer = tesselator.getBuilder();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(i, x, var5, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        final float[] sunriseColors = this.minecraft.level.effects().getSunriseColor(this.minecraft.level.getTimeOfDay(partialTicks), partialTicks);
        float var9;
        float size;
        float rand1;
        float r;

        if (sunriseColors != null) {
            final float sunsetModInv = Math.min(1.0F, Math.max(1.0F - theta * 50.0F, 0.0F));

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(this.minecraft.level.getSunAngle(partialTicks)) < 0.0F ? 180.0F : 0.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            z = sunriseColors[0] * sunsetModInv;
            var9 = sunriseColors[1] * sunsetModInv;
            size = sunriseColors[2] * sunsetModInv;
            float rand3;

            worldRenderer.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

            worldRenderer.vertex(0.0D, 100.0D, 0.0D).color(z * sunsetModInv, var9 * sunsetModInv, size * sunsetModInv, sunriseColors[3]).endVertex();
            final byte phi = 16;

            for (int var27 = 0; var27 <= phi; ++var27) {
                rand3 = (float) (var27 * (Math.PI * 2) / phi);
                final float xx = Mth.sin(rand3);
                final float rand5 = Mth.cos(rand3);
                worldRenderer.vertex(xx * 120.0F, rand5 * 120.0F, -rand5 * 40.0F * sunriseColors[3]).color(sunriseColors[0] * sunsetModInv, sunriseColors[1] * sunsetModInv, sunriseColors[2] * sunsetModInv, 0.0F).endVertex();
            }

            tesselator.end();
            poseStack.popPose();
        }

        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        poseStack.pushPose();
        z = 1.0F - this.minecraft.level.getRainLevel(partialTicks);
        var9 = 0.0F;
        size = 0.0F;
        rand1 = 0.0F;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, z);
        poseStack.translate(var9, size, rand1);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));

        poseStack.mulPose(Axis.XP.rotationDegrees(this.minecraft.level.getTimeOfDay(partialTicks) * 360.0F));
        double playerHeight = player.getY();

        // Draw stars
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        float threshold;
        Vec3 vec = getFogColor(this.minecraft.level, camera, partialTicks);
        threshold = Math.max(0.1F, (float) vec.length() - 0.1F);
        float var20 = ((float) playerHeight - 200) / 1000.0F;
        var20 = Mth.sqrt(var20);
        float bright1 = Math.min(0.9F, var20 * 3);

        if (bright1 > threshold) {
            RenderSystem.setShaderColor(bright1, bright1, bright1, 1.0F);
            FogRenderer.setupNoFog();
            this.starBuffer.bind();
            this.starBuffer.drawWithShader(poseStack.last().pose(), matrix4f, GameRenderer.getPositionShader());
            VertexBuffer.unbind();
            runnable.run();
        }

        FogRenderer.setupNoFog();
        this.starBuffer2.bind();
        this.starBuffer2.drawWithShader(poseStack.last().pose(), matrix4f, GameRenderer.getPositionShader());
        VertexBuffer.unbind();

        FogRenderer.setupNoFog();
        this.starBuffer3.bind();
        this.starBuffer3.drawWithShader(poseStack.last().pose(), matrix4f, GameRenderer.getPositionShader());
        VertexBuffer.unbind();

        FogRenderer.setupNoFog();
        this.starBuffer4.bind();
        this.starBuffer4.drawWithShader(poseStack.last().pose(), matrix4f, GameRenderer.getPositionShader());
        VertexBuffer.unbind();

        // Draw sun
        RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );
        r = 30.0F;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, CelestialBodyTextures.SUN);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f1 = poseStack.last().pose();
        worldRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        worldRenderer.vertex(matrix4f1, -r, 100.0F, -r).uv(0.0F, 0.0F).endVertex();
        worldRenderer.vertex(matrix4f1, r, 100.0F, -r).uv(1.0F, 0.0F).endVertex();
        worldRenderer.vertex(matrix4f1, r, 100.0F, r).uv(1.0F, 1.0F).endVertex();
        worldRenderer.vertex(matrix4f1, -r, 100.0F, r).uv(0.0F, 1.0F).endVertex();
        tesselator.end();

        // Draw moon
        r = 40.0F;
        RenderSystem.setShaderTexture(0, MOON_LOCATION);
        float sinphi = this.minecraft.level.getMoonPhase();
        final int cosphi = (int) (sinphi % 4);
        final int var29 = (int) (sinphi / 4 % 2);
        final float yy = (cosphi) / 4.0F;
        final float rand7 = (var29) / 2.0F;
        final float zz = (cosphi + 1) / 4.0F;
        final float rand9 = (var29 + 1) / 2.0F;
        worldRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        worldRenderer.vertex(matrix4f1, -r, -100.0F, r).uv(zz, rand9).endVertex();
        worldRenderer.vertex(matrix4f1, r, -100.0F, r).uv(yy, rand9).endVertex();
        worldRenderer.vertex(matrix4f1, r, -100.0F, -r).uv(yy, rand7).endVertex();
        worldRenderer.vertex(matrix4f1, -r, -100.0F, -r).uv(zz, rand7).endVertex();
        tesselator.end();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        poseStack.popPose();
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);

        double heightOffset = playerHeight - 64;

        if (heightOffset > this.minecraft.options.getEffectiveRenderDistance() * 16) {
            theta *= 400.0F;

            final float sinth = Math.max(Math.min(theta / 100.0F - 0.2F, 0.5F), 0.0F);

            poseStack.pushPose();
                        float scale = 850 * (0.25F - theta / 10000.0F);
            scale = Math.max(scale, 0.2F);
            poseStack.scale(scale, 1.0F, scale);
            poseStack.translate(0.0F, -(float) player.getY(), 0.0F);

            RenderSystem.setShaderTexture(0, CelestialBodyTextures.EARTH);

            size = 1.0F;

            RenderSystem.setShaderColor(sinth, sinth, sinth, 1.0F);
            worldRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

            float zoomIn = 0.0F;
            float cornerB = 1.0F - zoomIn;
            Matrix4f matrix4f2 = poseStack.last().pose();
            worldRenderer.vertex(matrix4f2, -size, 0, size).uv(zoomIn, cornerB).endVertex();
            worldRenderer.vertex(matrix4f2, size, 0, size).uv(cornerB, cornerB).endVertex();
            worldRenderer.vertex(matrix4f2, size, 0, -size).uv(cornerB, zoomIn).endVertex();
            worldRenderer.vertex(matrix4f2, -size, 0, -size).uv(zoomIn, zoomIn).endVertex();
            tesselator.end();
            poseStack.popPose();
        }

        RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, 1.0F);

                RenderSystem.depthMask(true);

        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend();
    }

    public static Vec3 getFogColor(ClientLevel level, Camera camera, float f) {
        Player player = Minecraft.getInstance().player;
        float heightOffset = ((float) (player.getY()) - 200) / 1000.0F;
        heightOffset = Mth.sqrt(heightOffset);

        float y = Mth.clamp(Mth.cos(level.getTimeOfDay(f) * (float) (Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);
        BiomeManager biomeManager = level.getBiomeManager();
        Vec3 vec32 = camera.getPosition().subtract(2.0, 2.0, 2.0).scale(0.25);
        Vec3 vec = CubicSampler.gaussianSampleVec3(
                vec32,
                (ix, j, k) -> level.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(biomeManager.getNoiseBiomeAtQuart(ix, j, k).value().getFogColor()), y)
        );

        return new Vec3(vec.x * Math.max(1.0F - heightOffset * 1.29F, 0.0F), vec.y * Math.max(1.0F - heightOffset * 1.29F, 0.0F), vec.z * Math.max(1.0F - heightOffset * 1.29F, 0.0F));
    }

    // TODO: Move this to a common place
    private BufferBuilder.RenderedBuffer renderStars(BufferBuilder worldRenderer, RandomSource rand) {
        RenderSystem.setShader(GameRenderer::getPositionShader);
        worldRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        for (int i = 0; i < 4000; ++i) {
            double x = rand.nextFloat() * 2.0F - 1.0F;
            double y = rand.nextFloat() * 2.0F - 1.0F;
            double z = rand.nextFloat() * 2.0F - 1.0F;
            final double size = 0.15F + rand.nextFloat() * 0.1F;
            double r = x * x + y * y + z * z;

            if (r < 1.0D && r > 0.01D) {
                r = 1.0D / Math.sqrt(r);
                x *= r;
                y *= r;
                z *= r;
                final double xx = x * 100D;
                final double zz = z * 100D;
                final double yy = y * 100D;
                final double theta = Math.atan2(x, z);
                final double sinth = Math.sin(theta);
                final double costh = Math.cos(theta);
                final double phi = Math.atan2(Math.sqrt(x * x + z * z), y);
                final double sinphi = Math.sin(phi);
                final double cosphi = Math.cos(phi);
                final double rho = rand.nextDouble() * Math.PI * 2.0D;
                final double sinrho = Math.sin(rho);
                final double cosrho = Math.cos(rho);

                for (int j = 0; j < 4; ++j) {
                    final double a = 0.0D;
                    final double b = ((j & 2) - 1) * size;
                    final double c = ((j + 1 & 2) - 1) * size;
                    final double d = b * cosrho - c * sinrho;
                    final double e = c * cosrho + b * sinrho;
                    final double dy = d * sinphi + a * cosphi;
                    final double ff = a * sinphi - d * cosphi;
                    final double dx = ff * sinth - e * costh;
                    final double dz = e * sinth + ff * costh;
                    worldRenderer.vertex(xx + dx, yy + dy, zz + dz).endVertex();
                }
            }
        }
        return worldRenderer.end();
    }
}
