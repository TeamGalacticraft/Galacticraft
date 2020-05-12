/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.mixin.client;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftDimensions;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@SuppressWarnings("PointlessArithmeticExpression")
@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class WorldRendererMixin {
    private static final Identifier EARTH_TEXTURE = new Identifier(Constants.MOD_ID, "textures/gui/celestialbodies/earth.png");
    private static final Identifier SUN_TEXTURE = new Identifier(Constants.MOD_ID, "textures/gui/celestialbodies/sun.png");
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private ClientWorld world;
    @Shadow
    @Final
    private VertexFormat skyVertexFormat;
    private VertexBuffer starBufferMoon;
    private VertexBuffer lightSkyBufferMoon;
    private VertexBuffer darkSkyBufferMoon;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void initGalacticraft(MinecraftClient client, BufferBuilderStorage bufferBuilders, CallbackInfo ci) {
        starBufferMoon = new VertexBuffer(skyVertexFormat);
        this.renderStarsGC();

        final Tessellator tessellator = Tessellator.getInstance();
        this.lightSkyBufferMoon = new VertexBuffer(skyVertexFormat);

        final byte byte2 = 64;
        final int i = 256 / byte2 + 2;
        float f = 16F;
        BufferBuilder buffer = tessellator.getBuffer();

        for (int j = -byte2 * i; j <= byte2 * i; j += byte2) {
            for (int l = -byte2 * i; l <= byte2 * i; l += byte2) {
                buffer.begin(7, VertexFormats.POSITION);
                buffer.vertex(j, f, l).next();
                buffer.vertex(j + byte2, f, l).next();
                buffer.vertex(j + byte2, f, l + byte2).next();
                buffer.vertex(j, f, l + byte2).next();
                buffer.end();
                lightSkyBufferMoon.upload(buffer);
            }
        }

        this.darkSkyBufferMoon = new VertexBuffer(skyVertexFormat);

        f = -16F;
        buffer.begin(7, VertexFormats.POSITION);

        for (int k = -byte2 * i; k <= byte2 * i; k += byte2) {
            for (int i1 = -byte2 * i; i1 <= byte2 * i; i1 += byte2) {
                buffer.vertex(k + byte2, f, i1).next();
                buffer.vertex(k, f, i1).next();
                buffer.vertex(k, f, i1 + byte2).next();
                buffer.vertex(k + byte2, f, i1 + byte2).next();
            }
        }

        buffer.end();
        darkSkyBufferMoon.upload(buffer);
    }

    @Inject(at = @At("HEAD"), method = "renderSky", cancellable = true)
    private void renderSkyGC(MatrixStack matrixStack, float f, CallbackInfo ci) {
        if (this.world.dimension.getType() == GalacticraftDimensions.MOON) {
            RenderSystem.disableTexture();
            RenderSystem.disableRescaleNormal();
            RenderSystem.color3f(1F, 1F, 1F);
            final Tessellator tessellator = Tessellator.getInstance();
            RenderSystem.depthMask(false);
            RenderSystem.enableFog();
            RenderSystem.color3f(0, 0, 0);
            this.lightSkyBufferMoon.bind();
            this.skyVertexFormat.startDrawing(0L);
            this.lightSkyBufferMoon.draw(matrixStack.peek().getModel(), 7);
            VertexBuffer.unbind();
            this.skyVertexFormat.endDrawing();
            RenderSystem.disableFog();
            RenderSystem.disableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 771);
            RenderSystem.disableLighting();
            float b;
            float a;

            float starBrightness = 1.0F;

            matrixStack.push();
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(this.world.getSkyAngle(f) * 360.0F));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-19.0F));
            RenderSystem.color4f(1.0F, 0.95F, 0.9F, starBrightness); //browner stars?
            this.starBufferMoon.bind();
            this.skyVertexFormat.startDrawing(0L);
            this.starBufferMoon.draw(matrixStack.peek().getModel(), 7);
            VertexBuffer.unbind();
            this.skyVertexFormat.endDrawing();

            matrixStack.pop();
            GlStateManager.blendFunc(770, 1);

            matrixStack.push();

            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(this.world.getSkyAngle(f) * 360.0F));

            RenderSystem.disableBlend();
            RenderSystem.disableTexture();

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.0F);
            a = 5.714286F;

            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(7, VertexFormats.POSITION);
            buffer.vertex(-a, 99.9D, -a).next();
            buffer.vertex(a, 99.9D, -a).next();
            buffer.vertex(a, 99.9D, a).next();
            buffer.vertex(-a, 99.9D, a).next();
            buffer.end();
            BufferRenderer.draw(buffer);
            Matrix4f matrix4f2 = matrixStack.peek().getModel();
            RenderSystem.enableTexture();
            RenderSystem.blendFunc(770, 1);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            a = 15.0F;
            client.getTextureManager().bindTexture(SUN_TEXTURE);
            buffer.begin(7, VertexFormats.POSITION_TEXTURE);
            buffer.vertex(matrix4f2, -a, 100.0F, -a).texture(0.0F, 0.0F).next();
            buffer.vertex(matrix4f2, a, 100.0F, -a).texture(1.0F, 0.0F).next();
            buffer.vertex(matrix4f2, a, 100.0F, a).texture(1.0F, 1.0F).next();
            buffer.vertex(matrix4f2, -a, 100.0F, a).texture(0.0F, 1.0F).next();
            buffer.end();
            BufferRenderer.draw(buffer);

            matrixStack.pop();
            matrixStack.push();
            matrix4f2 = matrixStack.peek().getModel();

            RenderSystem.enableTexture();

            a = 10.0F;
            assert client.player != null;
            float earthRotation = 0F;//(float) (this.world.getSpawnPos().getZ() - client.player.getZ()) * 0.01F;
            matrixStack.scale(0.6F, 0.6F, 0.6F);
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((this.world.getSkyAngle(f) * 360.0F) - 40.0F));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(earthRotation));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(10.0F));
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            client.getTextureManager().bindTexture(EARTH_TEXTURE);

            buffer.begin(7, VertexFormats.POSITION_TEXTURE);
            buffer.vertex(matrix4f2, -a, -100.0F, a).texture(0.0F, 1.0F).next();
            buffer.vertex(matrix4f2, a, -100.0F, a).texture(1.0F, 1.0F).next();
            buffer.vertex(matrix4f2, a, -100.0F, -a).texture(1.0F, 0.0F).next();
            buffer.vertex(matrix4f2, -a, -100.0F, -a).texture(0.0F, 0.0F).next();
            buffer.end();
            BufferRenderer.draw(buffer);

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            RenderSystem.enableAlphaTest();
            RenderSystem.enableFog();
            matrixStack.pop();
            RenderSystem.disableTexture();
            RenderSystem.color3f(0.0F, 0.0F, 0.0F);
            double var25 = client.player.getPos().getY() - this.world.getSkyDarknessHeight();

            if (var25 < 0.0D) {
                matrixStack.push();
                matrixStack.translate(0.0F, 12.0F, 0.0F);

                this.darkSkyBufferMoon.bind();
                this.skyVertexFormat.startDrawing(0L);
                this.darkSkyBufferMoon.draw(matrixStack.peek().getModel(), 7);
                VertexBuffer.unbind();
                this.skyVertexFormat.endDrawing();

                matrixStack.pop();
                a = -1.0F;
                b = -((float) (var25 + 65.0D));
                buffer.begin(7, VertexFormats.POSITION_COLOR);
                buffer.vertex(-a, b, a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(a, b, a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(a, a, a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-a, a, a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-a, a, -a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(a, a, -a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(a, b, -a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-a, b, -a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(a, a, -a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(a, a, a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(a, b, a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(a, b, -a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-a, b, -a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-a, b, a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-a, a, a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-a, a, -a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-a, a, -a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(-a, a, a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(a, a, a).color(0, 0, 0, 1.0F).next();
                buffer.vertex(a, a, -a).color(0, 0, 0, 1.0F).next();
                buffer.end();
                BufferRenderer.draw(buffer);
            }

            RenderSystem.color3f(70F / 256F, 70F / 256F, 70F / 256F);

            matrixStack.push();
            matrixStack.translate(0.0F, -((float) (var25 - 16.0D)), 0.0F);

            this.darkSkyBufferMoon.bind();
            this.skyVertexFormat.startDrawing(0L);
            this.darkSkyBufferMoon.draw(matrixStack.peek().getModel(), 7);
            VertexBuffer.unbind();
            this.skyVertexFormat.endDrawing();

            matrixStack.pop();
            RenderSystem.enableRescaleNormal();
            RenderSystem.enableTexture();
            RenderSystem.depthMask(true);
            RenderSystem.enableColorMaterial();
            RenderSystem.blendFunc(770, 771);
            RenderSystem.disableBlend();
            ci.cancel();
            //noinspection UnnecessaryReturnStatement
            return;
        }
    }

    private void renderStarsGC() {
        Random random = new Random(10842L);

        final Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION);
        for (int i = 0; i < 6000; ++i) {
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
                    bufferBuilder.vertex(o + aa, p + g, q + ab).next();
                }
            }
        }
        bufferBuilder.end();
        starBufferMoon.upload(bufferBuilder);
    }

}
