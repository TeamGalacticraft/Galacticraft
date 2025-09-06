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

package dev.galacticraft.mod.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.misc.footprint.Footprint;
import dev.galacticraft.mod.misc.footprint.FootprintManager;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FootprintRenderer {
    private static final ResourceLocation FOOTPRINT_TEXTURE = Constant.id("textures/misc/footprint.png");

    public static void renderFootprints(WorldRenderContext context) {
        context.profiler().push("footprints");
        PoseStack poseStack = context.matrixStack();
        ResourceLocation dimActive = context.world().dimensionTypeRegistration().unwrapKey().get().location();
        List<Footprint> footprintsToDraw = new LinkedList<>();

        for (List<Footprint> footprintList : context.world().galacticraft$getFootprintManager().getFootprints().values()) {
            for (Footprint footprint : footprintList) {
                if (footprint.dimension.equals(dimActive)) {
                    footprintsToDraw.add(footprint);
                }
            }
        }

        if (footprintsToDraw.isEmpty()) {
            context.profiler().pop();
            return;
        }

        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, FootprintRenderer.FOOTPRINT_TEXTURE);

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

//        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F); // This probably needs a custom shader?
//        float lightMapSaveX = OpenGlHelper.lastBrightnessX;
//        float lightMapSaveY = OpenGlHelper.lastBrightnessY;
//        boolean sensorGlasses = OverlaySensorGlasses.overrideMobTexture();
//        if (sensorGlasses) {
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
//        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (Footprint footprint : footprintsToDraw) {
            poseStack.pushPose();

//            if (!sensorGlasses) {
//                int j = footprint.lightmapVal % 65536;
//                int k = footprint.lightmapVal / 65536;
//                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
//            }

            float ageScale = 1.0F - footprint.age / (float) Footprint.MAX_AGE;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(ageScale, ageScale, ageScale, ageScale);

            Vec3 cameraPos = context.camera().getPosition();
            poseStack.translate(
                    footprint.position.x - cameraPos.x,
                    footprint.position.y - cameraPos.y + 0.01F * ageScale,
                    footprint.position.z - cameraPos.z
            );

            Matrix4f last = poseStack.last().pose();
            BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            float footprintScale = 0.5F;
            float rotation = 45.0F * Mth.DEG_TO_RAD - footprint.rotation;
            for (int i = 3; i >= 0; i--) {
                buffer = (BufferBuilder) buffer
                        .addVertex(last, Mth.sin(rotation) * footprintScale, 0, Mth.cos(rotation) * footprintScale)
                        .setUv(i / 2, (i == 0 || i == 3) ? 1 : 0);
                rotation += Mth.HALF_PI;
            }

            BufferUploader.drawWithShader(buffer.buildOrThrow());
            poseStack.popPose();
        }

//        if (sensorGlasses) {
//            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightMapSaveX, lightMapSaveY);
//        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        context.profiler().pop();
    }

    public static void setFootprints(long chunk, List<Footprint> prints) {
        FootprintManager manager = Minecraft.getInstance().level.galacticraft$getFootprintManager();
        List<Footprint> footprintList = manager.getFootprints().get(chunk);

        if (footprintList == null) {
            footprintList = new ArrayList<>();
        }

        footprintList.addAll(prints);
        manager.getFootprints().put(chunk, footprintList);
    }
}