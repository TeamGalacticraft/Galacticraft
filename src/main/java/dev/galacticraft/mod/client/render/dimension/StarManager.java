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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

import java.util.Random;

// TODO: implement fancy star visuals
public class StarManager {
    private final VertexBuffer starBuffer;

    public StarManager() {
        this.starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        final Random random = new Random(27893L);
        final BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        RenderSystem.setShader(GameRenderer::getPositionShader);
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
                    buffer.addVertex((float) ((o + aa) * (i > 6000 ? -1 : 1)), (float) ((p + g) * (i > 6000 ? -1 : 1)), (float) ((q + ab) * (i > 6000 ? -1 : 1)));
                }
            }
        }
        this.starBuffer.bind();
        this.starBuffer.upload(buffer.buildOrThrow());
        VertexBuffer.unbind();
    }

    public void tick() {

    }

    public void render(PoseStack poseStack, Matrix4f projectionMatrix, Level level, float partialTicks) {
        RenderSystem.setShaderColor(1.0F, 0.95F, 0.9F, getStarBrightness(level, partialTicks));
        FogRenderer.setupNoFog();
        this.starBuffer.bind();
        this.starBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, GameRenderer.getPositionShader());
        VertexBuffer.unbind();
    }

    public float getStarBrightness(Level world, float delta) {
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
