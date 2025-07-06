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
import com.mojang.math.Axis;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class SatelliteSkyRenderer extends SpaceSkyRenderer {
    public static final SatelliteSkyRenderer INSTANCE = new SatelliteSkyRenderer(Constant.Skybox.EARTH);
    protected final EarthManager earthManager = new EarthManager();
    private final ResourceLocation parentBody;

    public SatelliteSkyRenderer(ResourceLocation parentBody) {
        this.parentBody = parentBody;
    }

    @Override
    public void render(WorldRenderContext context) {
        context.profiler().push("satellite_sky_renderer");
        RenderSystem.disableBlend();
        RenderSystem.depthMask(false);

        float partialTicks = context.tickCounter().getGameTimeDeltaPartialTick(true);
        PoseStack matrices = new PoseStack();
        matrices.mulPose(context.positionMatrix());
        Tesselator tesselator = Tesselator.getInstance();

        context.profiler().push("stars");
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees((context.world().getTimeOfDay(partialTicks) * 360.0f) - 90F));
        matrices.mulPose(Axis.XP.rotationDegrees((context.world().getTimeOfDay(partialTicks)) * 360.0f));
        matrices.mulPose(Axis.YP.rotationDegrees(-19.0F));

        this.starManager.render(matrices, context.projectionMatrix(), context.world(), partialTicks);

        matrices.popPose();
        context.profiler().pop();

        context.profiler().push("sun");
        matrices.pushPose();

        matrices.mulPose(Axis.YP.rotationDegrees(-90.0F));
        matrices.mulPose(Axis.XP.rotationDegrees(context.world().getTimeOfDay(partialTicks) * 360.0f));

        Matrix4f matrix = matrices.last().pose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, Constant.CelestialBody.SOL_FROM_MOON);
        float size = 6.0F;
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -size, 100.0F, -size).setUv(1.0F, 0.0F)
                .addVertex(matrix, size, 100.0F, -size).setUv(0.0F, 0.0F)
                .addVertex(matrix, size, 100.0F, size).setUv(0.0F, 1.0F)
                .addVertex(matrix, -size, 100.0F, size).setUv(1.0F, 1.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        context.profiler().pop();
        context.profiler().push("moon");

        matrices.mulPose(Axis.XP.rotationDegrees(180.0F));

        RenderSystem.setShaderTexture(0, Constant.CelestialBody.MOON);
        size = 6.0F;
        buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -size, 100.0F, -size).setUv(1.0F, 1.0F)
                .addVertex(matrix, size, 100.0F, -size).setUv(0.0F, 1.0F)
                .addVertex(matrix, size, 100.0F, size).setUv(0.0F, 0.0F)
                .addVertex(matrix, -size, 100.0F, size).setUv(1.0F, 0.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        matrices.popPose();
        context.profiler().pop();

        context.profiler().push("earth");
        this.earthManager.render(matrices, context.world(), context.camera().getPosition().y() + 2048.0D, partialTicks, context.camera());
        context.profiler().pop();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        context.profiler().pop();
    }
}