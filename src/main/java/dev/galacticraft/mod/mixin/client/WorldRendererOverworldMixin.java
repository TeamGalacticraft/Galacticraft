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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class WorldRendererOverworldMixin {
    @Shadow @Nullable private VertexBuffer starsBuffer;
    private @Unique Random starRandom = null;

    @Inject(method = "renderStars(Lnet/minecraft/client/render/BufferBuilder;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;begin(Lnet/minecraft/client/render/VertexFormat$DrawMode;Lnet/minecraft/client/render/VertexFormat;)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void gc_captureRandom(BufferBuilder buffer, CallbackInfo ci, Random random) {
        starRandom = random;
    }

    @Inject(method = "renderStars(Lnet/minecraft/client/render/BufferBuilder;)V", at = @At(value = "RETURN"))
    private void gc_releaseRandom(BufferBuilder buffer, CallbackInfo ci) {
        starRandom = null;
    }

    @Redirect(method = "renderStars(Lnet/minecraft/client/render/BufferBuilder;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;begin(Lnet/minecraft/client/render/VertexFormat$DrawMode;Lnet/minecraft/client/render/VertexFormat;)V"))
    private void gc_replaceVF(BufferBuilder bufferBuilder, VertexFormat.DrawMode drawMode, VertexFormat format) {
        bufferBuilder.begin(drawMode, VertexFormats.POSITION_COLOR);
    }

    @ModifyConstant(method = "renderStars(Lnet/minecraft/client/render/BufferBuilder;)V", constant = @Constant(intValue = 1500))
    private int gc_moreStars(int i) {
        return i * 4;
    }

    @Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/VertexBuffer;setShader(Lnet/minecraft/util/math/Matrix4f;Lnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/Shader;)V", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BackgroundRenderer;method_23792()V")))
    private void gc_replaceShader(VertexBuffer vertexBuffer, Matrix4f viewMatrix, Matrix4f projectionMatrix, Shader shader) {
        assert vertexBuffer == starsBuffer;
        assert shader == GameRenderer.getPositionShader();

        vertexBuffer.setShader(viewMatrix, projectionMatrix, GameRenderer.getPositionColorShader());
    }

    private @Unique byte gc_idx = 0;
    private @Unique int r = 255;
    private @Unique int g = 255;
    private @Unique int b = 255;
    private @Unique int a = 255;

    @Redirect(method = "renderStars(Lnet/minecraft/client/render/BufferBuilder;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;vertex(DDD)Lnet/minecraft/client/render/VertexConsumer;"))
    private VertexConsumer gc_setColor(BufferBuilder bufferBuilder, double x, double y, double z) {
        if (gc_idx == 4) {
            gc_idx = 0;
        }

        if (gc_idx++ == 0) {
            int r;
            int g;
            int b;
            int a;
            if (starRandom.nextInt(20) == 0) {
                r = starRandom.nextInt(255 - 200) + 200;
                g = starRandom.nextInt(110);
                b = starRandom.nextInt(110);
                if (starRandom.nextInt(30) == 0) {
                    g += starRandom.nextInt(100);
                }
                a = (starRandom.nextInt(5) == 0 ? 255 : (starRandom.nextInt(255 - 170) + 170));
            } else if (starRandom.nextInt(30) == 0) {
                b = starRandom.nextInt(255 - 170) + 170;
                r = starRandom.nextInt(70);
                g = starRandom.nextInt(140);
                a = (starRandom.nextInt(5) == 0 ? 255 : (starRandom.nextInt(255 - 170) + 170));
            } else if (starRandom.nextInt(6) == 0) {
                g = r = starRandom.nextInt(255 - 170) + 170;
                b = Math.min(g - 70, starRandom.nextInt(150));
                a = (starRandom.nextInt(5) == 0 ? 255 : (starRandom.nextInt(255 - 170) + 170));
            } else {
                r = g = b = starRandom.nextInt(255 - 180) + 180;
                a = starRandom.nextInt(255 - 150) + 150;
            }
            this.r = r;
            this.b = b;
            this.g = g;
            this.a = a;
        }

        return bufferBuilder.vertex(x, y, z).color(r, g, b, a);
    }

}
