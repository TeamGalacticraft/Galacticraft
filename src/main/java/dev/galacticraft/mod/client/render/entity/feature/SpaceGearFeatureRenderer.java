/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.client.render.entity.feature;

import dev.galacticraft.mod.Constant;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class SpaceGearFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier(Constant.MOD_ID, "textures/entity/oxygen_gear.png");
    private final ModelTransformer<T> maskTransforms;
    private final ModelTransformer<T> tankTransforms;
    private final ModelPart oxygenMask;
    private final ModelPart oxygenTank;

    public SpaceGearFeatureRenderer(FeatureRendererContext<T, M> context, float extra, ModelTransformer<T> maskTransforms, ModelTransformer<T> tankTransforms) {
        super(context);
        this.maskTransforms = maskTransforms;
        this.tankTransforms = tankTransforms;

        this.oxygenMask = new ModelPart(64, 32, 0, 10);
        this.oxygenMask.setPivot(0.0F, 6.0F, 0.0F);
        this.oxygenMask.addCuboid(-5.0F, -9.0F, -5.0F, 10, 10, 10, extra);
        this.oxygenTank = new ModelPart(64, 32, 0, 0);
        this.oxygenTank.setPivot(0.0F, 6.0F, 0.0F);
        this.oxygenTank.addCuboid(-4.0F, 1.0F, 2.0F, 8, 6, 4, extra);
        ModelPart oxygenPipe = new ModelPart(64, 32, 40, 17);
        oxygenPipe.setPivot(0.0F, 2.0F, 0.0F);
        oxygenPipe.addCuboid(-2.0F, -3.0F, 0.0F, 4, 5, 8, extra);
        this.oxygenTank.addChild(oxygenPipe);
    }

    public SpaceGearFeatureRenderer(FeatureRendererContext<T, M> context, float extraHelmet, float extraTank, float pivotX, float pivotY, float pivotZ, ModelTransformer<T> maskTransforms, ModelTransformer<T> tankTransforms) {
        super(context);
        this.maskTransforms = maskTransforms;
        this.tankTransforms = tankTransforms;

        this.oxygenMask = new ModelPart(64, 32, 0, 10);
        this.oxygenMask.setPivot(pivotX, pivotY, pivotZ);
        this.oxygenMask.addCuboid(-5.0F, -9.0F, -5.0F, 10, 10, 10, extraHelmet);
        this.oxygenTank = new ModelPart(64, 32, 0, 0);
        this.oxygenTank.setPivot(0.0F, 6.0F, 0.0F);
        this.oxygenTank.addCuboid(-4.0F, 1.0F, 2.0F, 8, 6, 4, extraTank);
        ModelPart oxygenPipe = new ModelPart(64, 32, 40, 17);
        oxygenPipe.setPivot(0.0F, 2.0F, 0.0F);
        oxygenPipe.addCuboid(-2.0F, -3.0F, 0.0F, 4, 5, 8, extraTank);
        this.oxygenTank.addChild(oxygenPipe);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(getTexture(entity), true));
        matrices.push();
        maskTransforms.transformModel(matrices, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        matrices.push();
        matrices.multiply(new Quaternion(Vec3f.POSITIVE_Y, headYaw, true));
        matrices.multiply(new Quaternion(Vec3f.POSITIVE_X, headPitch, true));
        oxygenMask.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
        tankTransforms.transformModel(matrices, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        oxygenTank.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }

    @Override
    protected Identifier getTexture(T entity) {
        return TEXTURE;
    }

    @FunctionalInterface
    public interface ModelTransformer<T extends Entity> {
        void transformModel(MatrixStack matrices, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch);
    }
}
