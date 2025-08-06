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

package dev.galacticraft.mod.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.galacticraft.mod.content.entity.GrappleHookEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;

public class GrappleHookRenderer extends EntityRenderer<GrappleHookEntity> {
    public GrappleHookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(GrappleHookEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        Player player = (Player) entity.getOwner();
        if (player == null) {
            return;
        }

        matrices.pushPose();
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderType.lines());
        Matrix4f pose = matrices.last().pose();

        // consumer.vertex(pose, 0, 0, 0).color(0, 0, 0, 255).normal(1, 0, 0).endVertex();
        // consumer.vertex(pose, (float) (player.getX() - entity.getX()), (float) (player.getEyeY() - entity.getY()), (float) (player.getZ() - entity.getZ())).color(0, 0, 0, 255).normal(1, 0, 0).endVertex();

        matrices.popPose();

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(GrappleHookEntity entity) {
        return null;
    }
}
