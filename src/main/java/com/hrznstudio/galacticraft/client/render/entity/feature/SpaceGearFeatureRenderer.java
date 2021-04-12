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

package com.hrznstudio.galacticraft.client.render.entity.feature;

import com.hrznstudio.galacticraft.Constants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class SpaceGearFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/entity/oxygen_gear.png");
    private final ModelTransformer<T> maskTransforms;
    private final ModelTransformer<T> tankTransforms;
    private final ModelPart oxygenMask;
    private final ModelPart oxygenTank;

    public SpaceGearFeatureRenderer(RenderLayerParent<T, M> context, float extra, ModelTransformer<T> maskTransforms, ModelTransformer<T> tankTransforms) {
        super(context);
        this.maskTransforms = maskTransforms;
        this.tankTransforms = tankTransforms;

        this.oxygenMask = new ModelPart(64, 32, 0, 10);
        this.oxygenMask.setPos(0.0F, 6.0F, 0.0F);
        this.oxygenMask.addBox(-5.0F, -9.0F, -5.0F, 10, 10, 10, extra);
        this.oxygenTank = new ModelPart(64, 32, 0, 0);
        this.oxygenTank.setPos(0.0F, 6.0F, 0.0F);
        this.oxygenTank.addBox(-4.0F, 1.0F, 2.0F, 8, 6, 4, extra);
        ModelPart oxygenPipe = new ModelPart(64, 32, 40, 17);
        oxygenPipe.setPos(0.0F, 2.0F, 0.0F);
        oxygenPipe.addBox(-2.0F, -3.0F, 0.0F, 4, 5, 8, extra);
        this.oxygenTank.addChild(oxygenPipe);
    }

    public SpaceGearFeatureRenderer(RenderLayerParent<T, M> context, float extraHelmet, float extraTank, float pivotX, float pivotY, float pivotZ, ModelTransformer<T> maskTransforms, ModelTransformer<T> tankTransforms) {
        super(context);
        this.maskTransforms = maskTransforms;
        this.tankTransforms = tankTransforms;

        this.oxygenMask = new ModelPart(64, 32, 0, 10);
        this.oxygenMask.setPos(pivotX, pivotY, pivotZ);
        this.oxygenMask.addBox(-5.0F, -9.0F, -5.0F, 10, 10, 10, extraHelmet);
        this.oxygenTank = new ModelPart(64, 32, 0, 0);
        this.oxygenTank.setPos(0.0F, 6.0F, 0.0F);
        this.oxygenTank.addBox(-4.0F, 1.0F, 2.0F, 8, 6, 4, extraTank);
        ModelPart oxygenPipe = new ModelPart(64, 32, 40, 17);
        oxygenPipe.setPos(0.0F, 2.0F, 0.0F);
        oxygenPipe.addBox(-2.0F, -3.0F, 0.0F, 4, 5, 8, extraTank);
        this.oxygenTank.addChild(oxygenPipe);
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity), true));
        matrices.pushPose();
        maskTransforms.transformModel(matrices, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        matrices.pushPose();
        matrices.mulPose(new Quaternion(Vector3f.YP, headYaw, true));
        matrices.mulPose(new Quaternion(Vector3f.XP, headPitch, true));
        oxygenMask.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        matrices.popPose();
        tankTransforms.transformModel(matrices, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        oxygenTank.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        matrices.popPose();
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    @FunctionalInterface
    public interface ModelTransformer<T extends Entity> {
        void transformModel(PoseStack matrices, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch);
    }
}
