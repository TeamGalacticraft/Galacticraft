/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.WorldRendererAccessor;
import dev.galacticraft.mod.world.dimension.GalacticraftDimension;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class WorldRendererMixin implements WorldRendererAccessor {
    private static final Identifier EARTH_TEXTURE = new Identifier(Constant.MOD_ID, "textures/gui/celestialbodies/earth.png");
    private static final Identifier SUN_TEXTURE = new Identifier(Constant.MOD_ID, "textures/gui/celestialbodies/sun.png");

    @Shadow @Final private MinecraftClient client;
    @Shadow private ClientWorld world;
    @Shadow private BuiltChunkStorage chunks;
    @Unique private VertexBuffer starBufferMoon;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void initGalacticraft(MinecraftClient client, BufferBuilderStorage bufferBuilders, CallbackInfo ci) {
        this.starBufferMoon = new VertexBuffer();
        this.generateStarBufferMoon(this.starBufferMoon);
    }

    @Inject(method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FDDD)V", at = @At("HEAD"), cancellable = true)
    private void renderClouds(MatrixStack matrices, Matrix4f matrix4f, float f, double d, double e, double g, CallbackInfo ci) {
        if (this.world.getRegistryKey() == GalacticraftDimension.MOON) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "renderSky", cancellable = true)
    private void renderSkyGC(MatrixStack matrices, Matrix4f matrix4f, float delta, Runnable runnable, CallbackInfo ci) {
        if (this.world.getRegistryKey() == GalacticraftDimension.MOON) {
            runnable.run(); // fog?
            this.client.getProfiler().push("moon_sky_render");
            RenderSystem.disableTexture();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(false);

            final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            float starBrightness = this.getStarBrightness(delta);

            matrices.push();
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(this.world.getSkyAngleRadians(delta) * 360.0F));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-19.0F));
            RenderSystem.setShaderColor(1.0F, 0.95F, 0.9F, starBrightness);
            RenderSystem.disableTexture();
            this.starBufferMoon.setShader(matrices.peek().getModel(), matrix4f, GameRenderer.getPositionShader());
            this.starBufferMoon.bind();
            this.starBufferMoon.drawVertices();
            VertexBuffer.unbind();

            matrices.pop();
            matrices.push();

            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(this.world.getSkyAngleRadians(delta) * 360.0F));

            Matrix4f matrix = matrices.peek().getModel();
            RenderSystem.enableTexture();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            float size = 15.0F;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, SUN_TEXTURE);
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            buffer.vertex(matrix, -size, 100.0F, -size).texture(0.0F, 0.0F).next();
            buffer.vertex(matrix, size, 100.0F, -size).texture(1.0F, 0.0F).next();
            buffer.vertex(matrix, size, 100.0F, size).texture(1.0F, 1.0F).next();
            buffer.vertex(matrix, -size, 100.0F, size).texture(0.0F, 1.0F).next();
            buffer.end();
            BufferRenderer.draw(buffer);

            matrices.pop();

            matrices.push();
            matrix = matrices.peek().getModel();

            size = 10.0F;
            assert client.player != null;
            float earthRotation = (float) (this.world.getSpawnPos().getZ() - client.player.getZ()) * 0.01F;
            matrices.scale(0.6F, 0.6F, 0.6F);
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((this.world.getSkyAngleRadians(delta) * 360.0F) * 0.001F));
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(earthRotation + 200.0F));

            RenderSystem.setShaderTexture(0, EARTH_TEXTURE);

            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            buffer.vertex(matrix, -size, -100.0F, size).texture(0.0F, 1.0F).next();
            buffer.vertex(matrix, size, -100.0F, size).texture(1.0F, 1.0F).next();
            buffer.vertex(matrix, size, -100.0F, -size).texture(1.0F, 0.0F).next();
            buffer.vertex(matrix, -size, -100.0F, -size).texture(0.0F, 0.0F).next();
            buffer.end();
            BufferRenderer.draw(buffer);

            matrices.pop();

            RenderSystem.disableTexture();
            RenderSystem.depthMask(true);
            this.client.getProfiler().pop();
            ci.cancel();
        }
    }

    @Unique
    private float getStarBrightness(float delta) {
        final float var2 = this.world.getSkyAngleRadians(delta);
        float var3 = 1.0F - (MathHelper.cos((float) (var2 * Math.PI * 2.0D) * 2.0F + 0.25F));

        if (var3 < 0.0F) {
            var3 = 0.0F;
        }

        if (var3 > 1.0F) {
            var3 = 1.0F;
        }

        return var3 * var3 * 0.5F + 0.3F;
    }

    @Unique
    private void generateStarBufferMoon(VertexBuffer vertexBuffer) {
        Random random = new Random(1671120782L);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
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
                    buffer.vertex((o + aa) * (i > 6000 ? -1 : 1), (p + g) * (i > 6000 ? -1 : 1), (q + ab) * (i > 6000 ? -1 : 1)).next();
                }
            }
        }
        buffer.end();
        vertexBuffer.upload(buffer);
    }

    @Override
    public void addChunkToRebuild(int x, int y, int z) {
        this.chunks.scheduleRebuild(x, y, z, false);
    }
}