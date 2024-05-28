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

package dev.galacticraft.mod.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.GCBakedModel;
import dev.galacticraft.mod.client.model.GCModelLoader;
import dev.galacticraft.mod.client.model.GCSheets;
import dev.galacticraft.mod.content.block.entity.machine.OxygenBubbleDistributorBlockEntity;
import dev.galacticraft.mod.content.entity.BubbleEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

@Environment(EnvType.CLIENT)
public class BubbleEntityRenderer extends EntityRenderer<BubbleEntity> {
    private static final ResourceLocation MODEL = Constant.id("models/misc/sphere.json");
    public static final ResourceLocation TEXTURE = Constant.id("textures/model/sphere.png");
    public static GCBakedModel bubbleModel;

    public BubbleEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BubbleEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if (bubbleModel == null) {
            bubbleModel = GCModelLoader.INSTANCE.getModel(MODEL);
            assert bubbleModel != null;
        }
        BlockEntity blockEntity = entity.level().getBlockEntity(entity.blockPosition());
        if (!(blockEntity instanceof OxygenBubbleDistributorBlockEntity machine) || entity.isRemoved()) {
            ((ClientLevel) entity.level()).removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED);
            return;
        }
        if (machine.isDisabled()) { // pretty sure we don't need to check if bubbleVisible since we kill entity at the same time
            return;
        }
        double size = machine.getSize();

        if (bubbleModel == null) {
            return;
        }

        matrices.pushPose();
        matrices.translate(0.5F, 1.0F, 0.5F);
        matrices.scale((float) size, (float) size, (float) size);
        VertexConsumer consumer = vertexConsumers.getBuffer(GCSheets.entityTranslucentEmissive(GCSheets.OBJ_ATLAS));
//        for (BakedQuad quad : bubbleModel.getQuads(null, null, entity.level().random)) {
//            consumer.putBulkData(matrices.last(), quad, 1, 1, 1, Integer.MAX_VALUE, OverlayTexture.NO_OVERLAY);
//        }
        // TEMP: until files are fixed
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
