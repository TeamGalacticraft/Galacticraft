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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

public class SpaceSkyRenderer implements DimensionRenderingRegistry.SkyRenderer {
    protected final StarManager starManager = new StarManager();

    @Override
    public void render(WorldRenderContext context) {
        PoseStack matrices = new PoseStack();
        matrices.mulPose(context.positionMatrix());

        context.profiler().push("stars");
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(-90.0F));
        matrices.mulPose(Axis.XP.rotationDegrees(context.world().getTimeOfDay(context.tickCounter().getRealtimeDeltaTicks()) * 360.0f));
        matrices.mulPose(Axis.YP.rotationDegrees(-19.0F));

        this.starManager.render(matrices, context.projectionMatrix(), context.world(), context.tickCounter().getRealtimeDeltaTicks());

        matrices.popPose();
        context.profiler().pop();
    }
}
