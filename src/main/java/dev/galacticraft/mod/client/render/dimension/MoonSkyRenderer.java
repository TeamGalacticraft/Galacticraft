/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.Random;

public enum MoonSkyRenderer implements DimensionRenderingRegistry.SkyRenderer {
    INSTANCE;

    private static final ResourceLocation EARTH_TEXTURE = new ResourceLocation(Constant.MOD_ID, "textures/gui/celestialbodies/earth.png");
    private static final ResourceLocation SUN_TEXTURE = new ResourceLocation(Constant.MOD_ID, "textures/gui/celestialbodies/sun.png");

    private VertexBuffer starBuffer = null;

    @Override
    public void render(WorldRenderContext context) {
        if (starBuffer == null) { //cannot be done in init as the gl context has not been created yet.
            starBuffer = new VertexBuffer();
            final Random random = new Random(27893L);
            final BufferBuilder buffer = Tesselator.getInstance().getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionShader);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            for (int i = 0; i < 12000; ++i) {
                double j = random.nextFloat() * 2.0F - 1.0F;
                double k = random.nextFloat() * 2.0F - 1.0F;
                double l = random.nextFloat() * 2.0F - 1.0F;
                double m = 0.15F + random.nextFloat() * 0.1F;
                double n = j * j + k * k + l * l;

                if (n < 1.0D && n > 0.01D) {
                    n = 1.0D / Math.sqrt(n);
                    j *= n;
                    k *= n;
                    l *= n;
                    double o = j * 100.0D;
                    double p = k * 100.0D;
                    double q = l * 100.0D;
                    double r = Math.atan2(j, l);
                    double s = Math.sin(r);
                    double t = Math.cos(r);
                    double u = Math.atan2(Math.sqrt(j * j + l * l), k);
                    double v = Math.sin(u);
                    double w = Math.cos(u);
                    double x = random.nextDouble() * Math.PI * 2.0D;
                    double y = Math.sin(x);
                    double z = Math.cos(x);

                    for (int a = 0; a < 4; ++a) {
                        double b = 0.0D;
                        double c = ((a & 2) - 1) * m;
                        double d = ((a + 1 & 2) - 1) * m;
                        double e = c * z - d * y;
                        double f = d * z + c * y;
                        double g = e * v + b * w;
                        double h = b * v - e * w;
                        double aa = h * s - f * t;
                        double ab = f * s + h * t;
                        buffer.vertex((o + aa) * (i > 6000 ? -1 : 1), (p + g) * (i > 6000 ? -1 : 1), (q + ab) * (i > 6000 ? -1 : 1)).endVertex();
                    }
                }
            }
            this.starBuffer.bind();
            this.starBuffer.upload(buffer.end());
            VertexBuffer.unbind();
        }
        context.profiler().push("moon_sky_render");
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(false);

        final PoseStack matrices = context.matrixStack();
        final BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        float starBrightness = this.getStarBrightness(context.world(), context.tickDelta());

        context.profiler().push("stars");
        matrices.pushPose();
        matrices.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
        matrices.mulPose(Vector3f.XP.rotationDegrees(context.world().getTimeOfDay(context.tickDelta()) * 360.0f));
        matrices.mulPose(Vector3f.YP.rotationDegrees(-19.0F));
        RenderSystem.setShaderColor(1.0F, 0.95F, 0.9F, starBrightness);
        RenderSystem.disableTexture();

        FogRenderer.setupNoFog();
        this.starBuffer.bind();
        this.starBuffer.drawWithShader(matrices.last().pose(), context.projectionMatrix(), GameRenderer.getPositionShader());
        VertexBuffer.unbind();

        matrices.popPose();
        context.profiler().pop();

        context.profiler().push("sun");
        matrices.pushPose();

        matrices.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
        matrices.mulPose(Vector3f.XP.rotationDegrees(context.world().getTimeOfDay(context.tickDelta()) * 360.0f));

        Matrix4f matrix = matrices.last().pose();
        RenderSystem.enableTexture();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        float size = 15.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUN_TEXTURE);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, -size, 100.0F, -size).uv(0.0F, 0.0F).endVertex();
        buffer.vertex(matrix, size, 100.0F, -size).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, size, 100.0F, size).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(matrix, -size, 100.0F, size).uv(0.0F, 1.0F).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        matrices.popPose();
        context.profiler().pop();

        context.profiler().push("earth");
        matrices.pushPose();
        matrix = matrices.last().pose();

        size = 10.0F;
        assert Minecraft.getInstance().player != null;
        float earthRotation = (float) (context.world().getSharedSpawnPos().getZ() - Minecraft.getInstance().player.getZ()) * 0.01F;
        matrices.scale(0.6F, 0.6F, 0.6F);
        matrices.mulPose(Vector3f.XP.rotationDegrees((context.world().getTimeOfDay(context.tickDelta()) * 360.0F) * 0.001F));
        matrices.mulPose(Vector3f.XP.rotationDegrees(earthRotation + 200.0F));

        RenderSystem.setShaderTexture(0, EARTH_TEXTURE);

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, -size, -100.0F, size).uv(0.0F, 1.0F).endVertex();
        buffer.vertex(matrix, size, -100.0F, size).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(matrix, size, -100.0F, -size).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, -size, -100.0F, -size).uv(0.0F, 0.0F).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        context.profiler().pop();
        matrices.popPose();

        RenderSystem.disableTexture();
        RenderSystem.depthMask(true);
        context.profiler().pop();
    }


    private float getStarBrightness(Level world, float delta) {
        final float skyAngle = world.getTimeOfDay(delta);
        float brightness = 1.0F - (Mth.cos((float) (skyAngle * Math.PI * 2.0D) * 2.0F + 0.25F));

        if (brightness < 0.0F) {
            brightness = 0.0F;
        }

        if (brightness > 1.0F) {
            brightness = 1.0F;
        }

        return brightness * brightness * 0.5F + 0.3F;
    }
}
