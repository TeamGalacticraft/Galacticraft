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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.render.dimension.OverworldRenderer;
import dev.galacticraft.mod.content.block.special.CryogenicChamberBlock;
import dev.galacticraft.mod.content.block.special.CryogenicChamberPart;
import dev.galacticraft.mod.content.block.special.TransportTube;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    // Define the RGBA values of the block outline for translucent blocks
    private static float R = 0.15f;
    private static float G = 0.15f;
    private static float B = 0.15f;
    private static float A = 1.0f; // opaque

    @Unique
    private OverworldRenderer worldRenderer;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void gc$setupRenderer(Minecraft minecraft, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, RenderBuffers renderBuffers, CallbackInfo ci) {
        this.worldRenderer = new OverworldRenderer();
    }
    
	@Inject(method = "renderHitOutline", at = @At("HEAD"), cancellable = true)
	private void onRenderHitOutline(PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        if (blockState.getBlock() instanceof CryogenicChamberBlock || blockState.getBlock() instanceof CryogenicChamberPart || blockState.getBlock() instanceof TransportTube) {
            // LevelRenderer.renderShape(poseStack, vertexConsumer, blockState.getShape(entity.level(), blockPos, CollisionContext.of(entity)), (double)blockPos.getX() - cameraX, (double)blockPos.getY() - cameraY, (double)blockPos.getZ() - cameraZ, 0.0f, 0.0f, 0.0f, 0.0f);
            VoxelShape voxelShape = blockState.getShape(entity.level(), blockPos, CollisionContext.of(entity));
            double d = (double)blockPos.getX() - cameraX;
            double d2 = (double)blockPos.getY() - cameraY;
            double d3 = (double)blockPos.getZ() - cameraZ;
            PoseStack.Pose pose = poseStack.last();
            voxelShape.forAllEdges((d4, d5, d6, d7, d8, d9) -> {
                float f5 = (float)(d7 - d4);
                float f6 = (float)(d8 - d5);
                float f7 = (float)(d9 - d6);
                float f8 = Mth.sqrt(f5 * f5 + f6 * f6 + f7 * f7);
                vertexConsumer.addVertex(pose, (float)(d4 + d), (float)(d5 + d2), (float)(d6 + d3)).setColor(R, G, B, A).setNormal(pose, f5 /= f8, f6 /= f8, f7 /= f8);
                vertexConsumer.addVertex(pose, (float)(d7 + d), (float)(d8 + d2), (float)(d9 + d3)).setColor(R, G, B, A).setNormal(pose, f5, f6, f7);
            });
            ci.cancel();
        }
    }

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    public void gc$renderSky(Matrix4f matrix4f, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player.getVehicle() instanceof RocketEntity && player.getVehicle().getY() > Constant.OVERWORLD_SKYPROVIDER_STARTHEIGHT) {
            fogCallback.run();
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose(matrix4f);
            this.worldRenderer.renderOverworldSky(player, poseStack, matrix4f, tickDelta, camera, thickFog, fogCallback);
            ci.cancel();
        }
    }

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    public void gc$preventCloudRendering(PoseStack matrices, Matrix4f matrix4f, Matrix4f matrix4f2, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof RocketEntity && player.getVehicle().getY() > Constant.CLOUD_HEIGHT) {
            // Have clouds slowly fade out
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, normalize((float) player.getY(), Constant.CLOUD_HEIGHT, Constant.CLOUD_LIMIT, 1, 0F));
            if (player.getVehicle().getY() > Constant.CLOUD_LIMIT)
                ci.cancel();
        }
    }

    @Inject(method = "renderSnowAndRain", at = @At("HEAD"), cancellable = true)
    public void gc$cancelRainAndSnow(LightTexture lightTexture, float f, double d, double e, double g, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() instanceof RocketEntity && player.getVehicle().getY() > Constant.CLOUD_HEIGHT)
            ci.cancel();
    }


    @Unique
    private float normalize(float x, float inMin, float inMax, float outMin, float outMax) {
        float outRange = outMax - outMin;
        float inRange  = inMax - inMin;
        return (x - inMin) *outRange / inRange + outMin;
    }
}
