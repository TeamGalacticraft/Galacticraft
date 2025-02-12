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

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class AsteroidSkyRenderer extends SpaceSkyRenderer {
    public static final AsteroidSkyRenderer INSTANCE = new AsteroidSkyRenderer();
    private static final ResourceLocation EARTH_TEXTURE = Constant.id("textures/gui/celestialbodies/earth.png");
    private static final ResourceLocation SUN_TEXTURE = Constant.id("textures/gui/celestialbodies/sol.png");

    @Override
    public void render(WorldRenderContext context) {
        context.profiler().push("asteroid_sky_renderer");
        RenderSystem.disableBlend();
        RenderSystem.depthMask(false);

        PoseStack matrices = new PoseStack();
        matrices.mulPose(context.positionMatrix());

        context.profiler().push("stars");
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees((context.world().getTimeOfDay(context.tickCounter().getRealtimeDeltaTicks()) * 360.0f) - 90F));
        matrices.mulPose(Axis.XP.rotationDegrees((context.world().getTimeOfDay(context.tickCounter().getRealtimeDeltaTicks())) * 360.0f));
        matrices.mulPose(Axis.YP.rotationDegrees(-19.0F));

        this.starManager.render(matrices, context.projectionMatrix(), context.world(), context.tickCounter().getRealtimeDeltaTicks());

        matrices.popPose();
        context.profiler().pop();

        context.profiler().push("sun");
        matrices.pushPose();

        matrices.mulPose(Axis.YP.rotationDegrees(-90.0F));
        matrices.mulPose(Axis.XP.rotationDegrees(context.world().getTimeOfDay(context.tickCounter().getRealtimeDeltaTicks()) * 360.0f));

        Matrix4f matrix = matrices.last().pose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        float size = 6.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUN_TEXTURE);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -size, 100.0F, -size).setUv(0.0F, 0.0F)
                .addVertex(matrix, size, 100.0F, -size).setUv(1.0F, 0.0F)
                .addVertex(matrix, size, 100.0F, size).setUv(1.0F, 1.0F)
                .addVertex(matrix, -size, 100.0F, size).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        matrices.popPose();
        context.profiler().pop();

        context.profiler().push("earth");
        matrices.pushPose();
        matrix = matrices.last().pose();

        size = 1.0F;
        assert Minecraft.getInstance().player != null;
        float earthRotation = (float) (context.world().getSharedSpawnPos().getZ() - Minecraft.getInstance().player.getZ()) * 0.01F;
        matrices.scale(0.6F, 0.6F, 0.6F);
        matrices.mulPose(Axis.XP.rotationDegrees((context.world().getTimeOfDay(context.tickCounter().getRealtimeDeltaTicks()) * 360.0F) * 0.001F));
        matrices.mulPose(Axis.XP.rotationDegrees(earthRotation + 200.0F));

        RenderSystem.setShaderTexture(0, EARTH_TEXTURE);

        buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -size, -100.0F, size).setUv(0.0F, 1.0F)
                .addVertex(matrix, size, -100.0F, size).setUv(1.0F, 1.0F)
                .addVertex(matrix, size, -100.0F, -size).setUv(1.0F, 0.0F)
                .addVertex(matrix, -size, -100.0F, -size).setUv(0.0F, 0.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        context.profiler().pop();
        matrices.popPose();

        RenderSystem.depthMask(true);
        context.profiler().pop();
    }
}
