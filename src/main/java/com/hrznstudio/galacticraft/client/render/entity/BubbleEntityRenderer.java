/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.client.render.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.entity.BubbleDistributorBlockEntity;
import com.hrznstudio.galacticraft.entity.BubbleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.monarkhes.myron.api.Myron;
import net.minecraft.client.render.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BubbleEntityRenderer extends EntityRenderer<BubbleEntity> {
    private static final ResourceLocation MODEL = new ResourceLocation(Constants.MOD_ID, "models/misc/sphere");
    public static BakedModel bubbleModel = null;

    public BubbleEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public void render(BubbleEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if (bubbleModel == null) {
            bubbleModel = Myron.getModel(MODEL);
            assert bubbleModel != null;
        }
        BlockEntity blockEntity = entity.level.getBlockEntity(entity.blockPosition());
        if (!(blockEntity instanceof BubbleDistributorBlockEntity) || entity.removed) {
            entity.remove();
            return;
        }
        if (!((BubbleDistributorBlockEntity) blockEntity).bubbleVisible) {
            return;
        }
        double size = ((BubbleDistributorBlockEntity) blockEntity).getSize();

        matrices.pushPose();
        matrices.translate(0.5F, 1.0F, 0.5F);
        matrices.scale((float) size, (float) size, (float) size);
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderType.entityTranslucent(getTexture(entity)));
        for (BakedQuad quad : bubbleModel.getQuads(null, null, entity.level.random)) {
            consumer.putBulkData(matrices.last(), quad, 1, 1, 1, Integer.MAX_VALUE, OverlayTexture.NO_OVERLAY);
        }
        matrices.popPose();
    }

    @Override
    public boolean shouldRender(BubbleEntity entity, Frustum visibleRegion, double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    public ResourceLocation getTexture(BubbleEntity entity) {
        return bubbleModel.getParticleIcon().getName();
    }
}
