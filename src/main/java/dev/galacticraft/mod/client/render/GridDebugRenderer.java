/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.galacticraft.mod.network.s2c.GridDebugPayload;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

/**
 * Draws the grids sent by {@link GridDebugPayload}: a box around every node and a line along every edge, colored by grid.
 * Ported from ThermalDynamics' {@code DebugRenderer}.
 */
public final class GridDebugRenderer {
    /**
     * Shapes that weren't refreshed for this many ticks are forgotten, like after grid debugging was turned off.
     */
    private static final int TIMEOUT = 40;

    private static List<GridDebugPayload.GridShape> grids = List.of();
    private static long lastUpdate;

    private GridDebugRenderer() {
    }

    public static void update(List<GridDebugPayload.GridShape> shapes) {
        grids = shapes;
        lastUpdate = currentTime();
    }

    public static void render(WorldRenderContext context) {
        if (grids.isEmpty() || context.consumers() == null) {
            return;
        }
        if (currentTime() - lastUpdate > TIMEOUT) {
            grids = List.of();
            return;
        }

        PoseStack pose = context.matrixStack();
        Vec3 camera = context.camera().getPosition();
        MultiBufferSource consumers = context.consumers();
        VertexConsumer lines = consumers.getBuffer(RenderType.lines());

        pose.pushPose();
        pose.translate(-camera.x, -camera.y, -camera.z);
        for (GridDebugPayload.GridShape grid : grids) {
            int color = grid.id().hashCode() | 0x404040;
            float r = (color >> 16 & 0xFF) / 255.0f;
            float g = (color >> 8 & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            for (long node : grid.nodes()) {
                LevelRenderer.renderLineBox(pose, lines, new AABB(BlockPos.of(node)).deflate(0.3), r, g, b, 1.0f);
            }
            for (int i = 0; i + 1 < grid.edges().size(); i += 2) {
                line(pose, lines, Vec3.atCenterOf(BlockPos.of(grid.edges().get(i))), Vec3.atCenterOf(BlockPos.of(grid.edges().get(i + 1))), r, g, b);
            }
        }
        pose.popPose();
    }

    private static void line(PoseStack pose, VertexConsumer lines, Vec3 from, Vec3 to, float r, float g, float b) {
        Vector3f normal = to.subtract(from).normalize().toVector3f();
        PoseStack.Pose last = pose.last();
        lines.addVertex(last, (float) from.x, (float) from.y, (float) from.z).setColor(r, g, b, 1.0f).setNormal(last, normal.x, normal.y, normal.z);
        lines.addVertex(last, (float) to.x, (float) to.y, (float) to.z).setColor(r, g, b, 1.0f).setNormal(last, normal.x, normal.y, normal.z);
    }

    private static long currentTime() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.level == null ? 0 : minecraft.level.getGameTime();
    }
}
