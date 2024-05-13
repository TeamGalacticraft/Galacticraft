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
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class MoonSkyRenderer extends SpaceSkyRenderer {
    public static final MoonSkyRenderer INSTANCE = new MoonSkyRenderer();
    private static final ResourceLocation EARTH_TEXTURE = Constant.id("textures/gui/celestialbodies/earth.png");
    private static final ResourceLocation SUN_TEXTURE = Constant.id("textures/gui/celestialbodies/sun.png");

    @Override
    public void render(WorldRenderContext context) {
        context.profiler().push("moon_sky_render");
        RenderSystem.disableBlend();
        RenderSystem.depthMask(false);

        final PoseStack matrices = context.matrixStack();
        final BufferBuilder buffer = Tesselator.getInstance().getBuilder();

        context.profiler().push("stars");
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(-90.0F));
        matrices.mulPose(Axis.XP.rotationDegrees(context.world().getTimeOfDay(context.tickDelta()) * 360.0f));
        matrices.mulPose(Axis.YP.rotationDegrees(-19.0F));

        this.starManager.render(context.matrixStack(), context.projectionMatrix(), context.world(), context.tickDelta());

        matrices.popPose();
        context.profiler().pop();

        context.profiler().push("sun");
        matrices.pushPose();

        matrices.mulPose(Axis.YP.rotationDegrees(-90.0F));
        matrices.mulPose(Axis.XP.rotationDegrees(context.world().getTimeOfDay(context.tickDelta()) * 360.0f));

        Matrix4f matrix = matrices.last().pose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        float size = 15.0F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUN_TEXTURE);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, -size, 100.0F, -size).uv(0.0F, 0.0F).endVertex();
        buffer.vertex(matrix, size, 100.0F, -size).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, size, 100.0F, size).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(matrix, -size, 100.0F, size).uv(0.0F, 1.0F).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        matrices.popPose();
        context.profiler().pop();

        context.profiler().push("earth");
        matrices.pushPose();
        matrix = matrices.last().pose();

        size = 10.0F;
        assert Minecraft.getInstance().player != null;
        float earthRotation = (float) (context.world().getSharedSpawnPos().getZ() - Minecraft.getInstance().player.getZ()) * 0.01F;
        matrices.scale(0.6F, 0.6F, 0.6F);
        matrices.mulPose(Axis.XP.rotationDegrees((context.world().getTimeOfDay(context.tickDelta()) * 360.0F) * 0.001F));
        matrices.mulPose(Axis.XP.rotationDegrees(earthRotation + 200.0F));

        RenderSystem.setShaderTexture(0, EARTH_TEXTURE);

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, -size, -100.0F, size).uv(0.0F, 1.0F).endVertex();
        buffer.vertex(matrix, size, -100.0F, size).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(matrix, size, -100.0F, -size).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, -size, -100.0F, -size).uv(0.0F, 0.0F).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        context.profiler().pop();
        matrices.popPose();

        RenderSystem.depthMask(true);
        context.profiler().pop();
    }
}
