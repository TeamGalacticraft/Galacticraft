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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class VenusWeatherRenderer implements DimensionRenderingRegistry.WeatherRenderer {
    public static final VenusWeatherRenderer INSTANCE = new VenusWeatherRenderer();
    private final float[] rainSizeX = new float[1024];
    private final float[] rainSizeZ = new float[1024];
    private static final ResourceLocation RAIN_TEXTURES = Constant.id("textures/misc/rain_venus.png");

    public VenusWeatherRenderer() {
        for (int i = 0; i < 32; ++i) {
            float f1 = (float) (i - 16);
            for (int j = 0; j < 32; ++j) {
                float f = (float) (j - 16);
                float f2 = Mth.sqrt(f * f + f1 * f1);
                this.rainSizeX[i << 5 | j] = -f1 / f2;
                this.rainSizeZ[i << 5 | j] = f / f2;
            }
        }
    }

    @Override
    public void render(WorldRenderContext context) {
        Vec3 camPos = context.camera().getPosition();
        double camX = camPos.x();
        double camY = camPos.y();
        double camZ = camPos.z();
        ClientLevel level = context.world();
        float partialTicks = context.tickDelta();
        float strength = level.getRainLevel(partialTicks);

        if (strength > 0.0F) {
            context.lightmapTextureManager().turnOnLightLayer();

            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder worldrenderer = tessellator.getBuilder();
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();

            int r = 4;
            if (Minecraft.useFancyGraphics()) {
                r = 8;
            }

            RenderSystem.depthMask(Minecraft.useShaderTransparency());
            int drawFlag = -1;
            RenderSystem.setShader(GameRenderer::getParticleShader);
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            int px = Mth.floor(camX);
            int py = Mth.floor(camY);
            int pz = Mth.floor(camZ);

            for (int z = pz - r; z <= pz + r; ++z) {
                int indexZ = (z - pz + 16) * 32;
                for (int x = px - r; x <= px + r; ++x) {
                    mutablePos.set(x, camY, z);
                    int yHeight = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) + 4 - (int) (4.8F * strength);
                    int y = py - r;
                    int ymax = py + r;

                    if (y < yHeight) {
                        y = yHeight;
                    }

                    if (ymax < yHeight) {
                        ymax = yHeight;
                    }

                    int yBase = yHeight;

                    if (yHeight < py) {
                        yBase = py;
                    }

                    if (y != ymax) {
                        RandomSource random = RandomSource.create((long)(x * x * 3121 + x * 45238971 ^ z * z * 418711 + z * 13761));

                        if (drawFlag != 0) {
                            if (drawFlag >= 0) {
                                tessellator.end();
                            }

                            drawFlag = 0;
                            RenderSystem.setShaderTexture(0, RAIN_TEXTURES);
                            worldrenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
                        }

                        int index = indexZ + x - px + 16;
                        double dx = (double) this.rainSizeX[index] * 0.5D;
                        double dz = (double) this.rainSizeZ[index] * 0.5D;
                        int u = context.worldRenderer().ticks & 131071;
                        int dy = x * x * 3121 + x * 45238971 + z * z * 418711 + z * 13761 & 0xFF;
                        float h = 3.0F + random.nextFloat();
                        float w = -((float)(u + dy) + partialTicks) / 80.0F * h;
                        float vOff = w % 32.0F;
                        double yo = random.nextDouble() / 1.8D;
                        double xx = x + 0.5D - camX;
                        double zz = z + 0.5D - camZ;
                        float rr = (float) Math.sqrt(xx * xx + zz * zz) / r;
                        float alpha = ((1.0F - rr * rr) * 0.5F + 0.5F) * strength / 0.6F; // 0.6F
                        // is
                        // the
                        // max
                        // rainstrength
                        // on
                        // Venus
                        mutablePos.set(x, yBase, z);
                        int light = LevelRenderer.getLightColor(level, mutablePos);
                        double xc = x + 0.5D;
                        double zc = z + 0.5D;
                        worldrenderer.vertex(xc - camX - dx, (double) ymax - yo - camY, zc - camZ - dz).uv(0.0F, (float) y * 0.25F + vOff).color(1.0F, 1.0F, 1.0F, alpha).uv2(light).endVertex();
                        worldrenderer.vertex(xc - camX + dx, (double) ymax - yo - camY, zc - camZ + dz).uv(1.0F, (float) y * 0.25F + vOff).color(1.0F, 1.0F, 1.0F, alpha).uv2(light).endVertex();
                        worldrenderer.vertex(xc - camX + dx, (double) y - yo - camY, zc - camZ + dz).uv(1.0F, (float) ymax * 0.25F + vOff).color(1.0F, 1.0F, 1.0F, alpha).uv2(light).endVertex();
                        worldrenderer.vertex(xc - camX - dx, (double) y - yo - camY, zc - camZ - dz).uv(0.0F, (float) ymax * 0.25F + vOff).color(1.0F, 1.0F, 1.0F, alpha).uv2(light).endVertex();
                    }
                }
            }

            if (drawFlag >= 0) {
                tessellator.end();
            }

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            context.lightmapTextureManager().turnOffLightLayer();
        }
    }
}
