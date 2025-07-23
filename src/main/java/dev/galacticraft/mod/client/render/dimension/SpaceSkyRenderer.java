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

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.client.render.dimension.star.CelestialBodyRendererManager;
import dev.galacticraft.mod.client.render.dimension.star.GeographicalSolarPosition;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

public class SpaceSkyRenderer implements DimensionRenderingRegistry.SkyRenderer {
    protected final StarManager starManager = new StarManager();

    protected final CelestialBodyRendererManager celestialBodyRendererManager
            = CelestialBodyRendererManager.getInstance();

    @Override
    public void render(WorldRenderContext context) {

        PoseStack matrices = new PoseStack();
        matrices.mulPose(context.positionMatrix());
        // render whole skybox black for when first loading into the dimension
        RenderSystem.setShaderColor(0.0f, 0.0F, 0.0F, 1.0F);

        RenderSystem.disableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        context.profiler().push("celestial_render");
        matrices.pushPose();

        // Update camera position for star rendering
        this.celestialBodyRendererManager.updateSolarPosition(
            context.camera().getPosition().x,
            context.camera().getPosition().y,
            context.camera().getPosition().z
        );

        this.celestialBodyRendererManager.render(context);

        matrices.popPose();
        context.profiler().pop();
        RenderSystem.setShaderColor(1.0f, 1.0F, 1.0F, 1.0F);

//        PoseStack matrices = new PoseStack();
//        matrices.mulPose(context.positionMatrix());
//
//        context.profiler().push("stars");
//        matrices.pushPose();
//        matrices.mulPose(Axis.YP.rotationDegrees(-90.0F));
//        matrices.mulPose(Axis.XP.rotationDegrees(context.world().getTimeOfDay(context.tickCounter().getRealtimeDeltaTicks()) * 360.0f));
//        matrices.mulPose(Axis.YP.rotationDegrees(-19.0F));
//
//        this.starManager.render(matrices, context.projectionMatrix(), context.world(), context.tickCounter().getRealtimeDeltaTicks());
//
//        matrices.popPose();
//        context.profiler().pop();
    }
}
