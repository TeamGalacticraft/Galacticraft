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
    public static final ResourceLocation MOON_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");
    @Nullable
    private VertexBuffer starBuffer;
    @Nullable
    private VertexBuffer starBuffer2;
    @Nullable
    private VertexBuffer starBuffer3;
    @Nullable
    private VertexBuffer starBuffer4;
    private Minecraft minecraft = Minecraft.getInstance();
    protected final EarthManager earthManager = new EarthManager();

    public OverworldRenderer() {
        RandomSource rand = RandomSource.create(10842L);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        if (this.starBuffer != null) {
            this.starBuffer.close();
        }

        this.starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.starBuffer.bind();
        this.starBuffer.upload(this.renderStars(rand));
        VertexBuffer.unbind();
        this.starBuffer2 = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.starBuffer2.bind();
        this.starBuffer2.upload(this.renderStars(rand));
        VertexBuffer.unbind();
        this.starBuffer3 = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.starBuffer3.bind();
        this.starBuffer3.upload(this.renderStars(rand));
        VertexBuffer.unbind();
        this.starBuffer4 = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.starBuffer4.bind();
        this.starBuffer4.upload(this.renderStars(rand));
        VertexBuffer.unbind();
    }

    public void renderOverworldSky(Player player, PoseStack poseStack, Matrix4f matrix4f, float partialTicks, Camera camera, boolean bl, Runnable runnable) {
        float theta = Mth.sqrt(((float) (player.getY()) - Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT) / ((float) Constant.ESCAPE_HEIGHT - Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT));
        final float var21 = Math.max(1.0F - theta * 4.0F, 0.0F);

        final Vec3 skyColor = this.minecraft.level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), partialTicks);
        float x = (float) skyColor.x * var21;
        float y = (float) skyColor.y * var21;
        float z = (float) skyColor.z * var21;

        FogRenderer.levelFogColor();
        RenderSystem.setShaderColor(x, y, z, 1.0F);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        final float[] sunriseColors = this.minecraft.level.effects().getSunriseColor(this.minecraft.level.getTimeOfDay(partialTicks), partialTicks);

        if (sunriseColors != null) {
            final float sunsetModInv = Math.min(1.0F, Math.max(1.0F - theta * 50.0F, 0.0F));

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(this.minecraft.level.getSunAngle(partialTicks)) < 0.0F ? 180.0F : 0.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            x = sunriseColors[0] * sunsetModInv;
            y = sunriseColors[1] * sunsetModInv;
            z = sunriseColors[2] * sunsetModInv;

            BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

            buffer.addVertex(0.0F, 100.0F, 0.0F).setColor(x * sunsetModInv, y * sunsetModInv, z * sunsetModInv, sunriseColors[3]);

            float angle;
            for (int i = 0; i <= 16; ++i) {
                angle = i * Mth.TWO_PI / 16.0F;
                final float s = Mth.sin(angle);
                final float c = Mth.cos(angle);
                buffer.addVertex(s * 120.0F, c * 120.0F, -c * 40.0F * sunriseColors[3]).setColor(x, y, z, 0.0F);
            }

            BufferUploader.drawWithShader(buffer.buildOrThrow());
            poseStack.popPose();
        }

        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        poseStack.pushPose();
        float alpha = 1.0F - this.minecraft.level.getRainLevel(partialTicks);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));

        poseStack.mulPose(Axis.XP.rotationDegrees(this.minecraft.level.getTimeOfDay(partialTicks) * 360.0F));
        double playerHeight = player.getY();

        // Draw stars
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        Vec3 vec = getFogColor(this.minecraft.level, camera, partialTicks);
        float threshold = Math.max(0.1F, (float) vec.length() - 0.1F);
        float var20 = ((float) playerHeight - Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT) / ((float) Constant.ESCAPE_HEIGHT - Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT);
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
        float size = 30.0F;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, Constant.Skybox.SUN);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f1 = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix4f1, -size, 100.0F, -size).setUv(0.0F, 0.0F)
                .addVertex(matrix4f1, size, 100.0F, -size).setUv(1.0F, 0.0F)
                .addVertex(matrix4f1, size, 100.0F, size).setUv(1.0F, 1.0F)
                .addVertex(matrix4f1, -size, 100.0F, size).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        // Draw moon
        size = 40.0F;
        RenderSystem.setShaderTexture(0, MOON_LOCATION);
        final float phase = this.minecraft.level.getMoonPhase();
        final int u = (int) (phase % 4);
        final int v = (int) (phase / 4 % 2);
        final float u0 = u / 4.0F;
        final float u1 = (u + 1) / 4.0F;
        final float v0 = v / 2.0F;
        final float v1 = (v + 1) / 2.0F;
        buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix4f1, -size, -100.0F, size).setUv(u1, v1)
                .addVertex(matrix4f1, size, -100.0F, size).setUv(u0, v1)
                .addVertex(matrix4f1, size, -100.0F, -size).setUv(u0, v0)
                .addVertex(matrix4f1, -size, -100.0F, -size).setUv(u1, v0);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        poseStack.popPose();

        float f = Mth.clamp(3.0F * theta, 0.0F, 1.0F);
        Vec3 vec3 = this.minecraft.level.getSkyColor(camera.getPosition(), partialTicks);
        float color = Mth.clamp(Mth.cos(this.minecraft.level.getTimeOfDay(partialTicks) * Mth.TWO_PI) * 2.0F + 0.5F, 0.25F, 1.0F);
        x = Mth.clampedLerp(f, (float) vec3.x, color);
        y = Mth.clampedLerp(f, (float) vec3.y, color);
        z = Mth.clampedLerp(f, (float) vec3.z, color);
        RenderSystem.setShaderColor(x, y, z, 1.0F);
        this.earthManager.render(poseStack, this.minecraft.level, 2.0D * (playerHeight - 64.0D), partialTicks);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    }

    public static Vec3 getFogColor(ClientLevel level, Camera camera, float f) {
        Player player = Minecraft.getInstance().player;
        float heightOffset = ((float) (player.getY()) - Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT) / ((float) Constant.ESCAPE_HEIGHT - Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT);
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
    private MeshData renderStars(RandomSource rand) {
        RenderSystem.setShader(GameRenderer::getPositionShader);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

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
                    buffer.addVertex((float) (xx + dx), (float) (yy + dy), (float) (zz + dz));
                }
            }
        }
        return buffer.buildOrThrow();
    }
}
