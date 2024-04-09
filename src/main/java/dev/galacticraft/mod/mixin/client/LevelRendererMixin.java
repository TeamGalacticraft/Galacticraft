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

package dev.galacticraft.mod.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.render.dimension.OverworldRenderer;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Unique
    private OverworldRenderer gc$worldRenderer;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void gc$setupRenderer(Minecraft minecraft, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, RenderBuffers renderBuffers, CallbackInfo ci) {
        this.gc$worldRenderer = new OverworldRenderer();
    }

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    public void gc$renderSky(PoseStack poseStack, Matrix4f matrix4f, float partialTicks, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player.getVehicle() instanceof RocketEntity && player.getVehicle().getY() > 200) {
            runnable.run();
            gc$worldRenderer.renderOverworldSky(player, poseStack, matrix4f, partialTicks, camera, bl, runnable);
            ci.cancel();
        }
    }

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    public void gc$preventCloudRendering(PoseStack poseStack, Matrix4f matrix4f, float f, double d, double e, double g, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof RocketEntity && player.getVehicle().getY() > Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT) {
            // Have clouds slowly fade out
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, normalize((float) player.getY(), Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT, Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT + 100, 1, 0F));
            if (player.getVehicle().getY() > 300)
                ci.cancel();
        }
    }

    @Inject(method = "renderSnowAndRain", at = @At("HEAD"), cancellable = true)
    public void gc$cancelRainAndSnow(LightTexture lightTexture, float f, double d, double e, double g, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof RocketEntity && player.getVehicle().getY() > 200)
            ci.cancel();
    }


    @Unique
    private float normalize(float x, float inMin, float inMax, float outMin, float outMax) {
        float outRange = outMax - outMin;
        float inRange  = inMax - inMin;
        return (x - inMin) *outRange / inRange + outMin;
    }
}
