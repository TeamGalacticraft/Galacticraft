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
import dev.monarkhes.myron.api.Myron;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class BubbleEntityRenderer extends EntityRenderer<BubbleEntity> {
    private static final Identifier MODEL = new Identifier(Constants.MOD_ID, "models/misc/sphere");
    public static BakedModel bubbleModel = null;

    public BubbleEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public void render(BubbleEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (bubbleModel == null) {
            bubbleModel = Myron.getModel(MODEL);
            assert bubbleModel != null;
        }
        BlockEntity blockEntity = entity.world.getBlockEntity(entity.getBlockPos());
        if (!(blockEntity instanceof BubbleDistributorBlockEntity) || entity.removed) {
            entity.remove();
            return;
        }
        if (!((BubbleDistributorBlockEntity) blockEntity).bubbleVisible) {
            return;
        }
        double size = ((BubbleDistributorBlockEntity) blockEntity).getSize();

        matrices.push();
        matrices.translate(0.5F, 1.0F, 0.5F);
        matrices.scale((float) size, (float) size, (float) size);
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(getTexture(entity)));
        for (BakedQuad quad : bubbleModel.getQuads(null, null, entity.world.random)) {
            consumer.quad(matrices.peek(), quad, 1, 1, 1, Integer.MAX_VALUE, OverlayTexture.DEFAULT_UV);
        }
        matrices.pop();
    }

    @Override
    public boolean shouldRender(BubbleEntity entity, Frustum visibleRegion, double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    public Identifier getTexture(BubbleEntity entity) {
        return bubbleModel.getSprite().getId();
    }
}
