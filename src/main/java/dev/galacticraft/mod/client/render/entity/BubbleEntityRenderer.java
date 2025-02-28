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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.GCModel;
import dev.galacticraft.mod.client.model.GCModelLoader;
import dev.galacticraft.mod.client.model.GCRenderTypes;
import dev.galacticraft.mod.client.render.block.entity.BubbleDistributorRenderer;
import dev.galacticraft.mod.content.block.entity.machine.OxygenBubbleDistributorBlockEntity;
import dev.galacticraft.mod.content.entity.BubbleEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

@Environment(EnvType.CLIENT)
public class BubbleEntityRenderer extends EntityRenderer<BubbleEntity> {
    public static final ResourceLocation TEXTURE = Constant.id("textures/model/sphere.png");
    public static GCModel bubbleModel;

    public BubbleEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BubbleEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if (bubbleModel == null) {
            bubbleModel = GCModelLoader.INSTANCE.getModel(BubbleDistributorRenderer.MODEL);
            assert bubbleModel != null;
        }
        float size = entity.getSize();

        matrices.pushPose();
        matrices.translate(0.5F, 1.0F, 0.5F);
        matrices.scale(size, size, size);
        VertexConsumer consumer = vertexConsumers.getBuffer(GCRenderTypes.bubble(GCRenderTypes.OBJ_ATLAS));

        bubbleModel.render(matrices, null, consumer, light, OverlayTexture.NO_OVERLAY);

        matrices.popPose();
    }

    @Override
    public boolean shouldRender(BubbleEntity entity, Frustum visibleRegion, double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(BubbleEntity entity) {
        return TEXTURE;
    }
}
