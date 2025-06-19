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

package dev.galacticraft.mod.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.tag.GCFluidTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
    @Unique
    private static final ResourceLocation OIL_LOCATION = Constant.id("textures/misc/crude_oil_overlay.png");
    @Unique
    private static final ResourceLocation FUEL_LOCATION = Constant.id("textures/misc/fuel_overlay.png");
    @Unique
    private static final ResourceLocation ACID_LOCATION = Constant.id("textures/misc/sulfuric_acid_overlay.png");

    @Inject(method = "renderScreenEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"), cancellable = true)
    private static void gc$fluidOverlays(Minecraft minecraft, PoseStack poseStack, CallbackInfo ci) {
        if (minecraft.player.isEyeInFluid(GCFluidTags.OIL)) {
            gc$renderFluidOverlay(minecraft, poseStack, OIL_LOCATION);
        } else if (minecraft.player.isEyeInFluid(GCFluidTags.FUEL)) {
            gc$renderFluidOverlay(minecraft, poseStack, FUEL_LOCATION);
        } else if (minecraft.player.isEyeInFluid(GCFluidTags.SULFURIC_ACID)) {
            gc$renderFluidOverlay(minecraft, poseStack, ACID_LOCATION);
        }
    }

    @Unique
    private static void gc$renderFluidOverlay(Minecraft minecraft, PoseStack poseStack, ResourceLocation texture) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);
        BlockPos blockPos = BlockPos.containing(minecraft.player.getX(), minecraft.player.getEyeY(), minecraft.player.getZ());
        float f = LightTexture.getBrightness(minecraft.player.level().dimensionType(), minecraft.player.level().getMaxLocalRawBrightness(blockPos));
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(f, f, f, 0.1f);
        float f8 = -minecraft.player.getYRot() / 64.0f;
        float f9 = minecraft.player.getXRot() / 64.0f;
        Matrix4f matrix4f = poseStack.last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, -1.0f, -1.0f, -0.5f).setUv(4.0f + f8, 4.0f + f9);
        bufferBuilder.addVertex(matrix4f, 1.0f, -1.0f, -0.5f).setUv(0.0f + f8, 4.0f + f9);
        bufferBuilder.addVertex(matrix4f, 1.0f, 1.0f, -0.5f).setUv(0.0f + f8, 0.0f + f9);
        bufferBuilder.addVertex(matrix4f, -1.0f, 1.0f, -0.5f).setUv(4.0f + f8, 0.0f + f9);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }
}
