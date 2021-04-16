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
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class SpaceGearFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier(Constants.MOD_ID, "textures/entity/galacticraft_gear.png");
    private static final int TEXTURE_WIDTH = 128;
    private static final int TEXTURE_HEIGHT = 32;
    final ModelTransformer<T> maskTransforms;
    final ModelTransformer<T> leftTankTransforms;
    final ModelTransformer<T> rightTankTransforms;
    final ModelTransformer<T> sensorGlassesTransforms;
    final ModelPart oxygenMask;
    final ModelPart leftOxygenTank;
    final ModelPart rightOxygenTank;
    final ModelPart sensorGlasses;
    boolean isOxygenMaskEnabled;
    boolean isLeftOxygenTankEnabled;
    boolean isRightOxygenTankEnabled;
    boolean isSensorGlassesEnabled;

    public SpaceGearFeatureRenderer(FeatureRendererContext<T, M> context, float extra, ModelTransformer<T> maskTransforms, ModelTransformer<T> leftTankTransforms, ModelTransformer<T> rightTankTransforms, ModelTransformer<T> sensorGlassesTransforms) {
        this(context, extra, maskTransforms, leftTankTransforms, rightTankTransforms, sensorGlassesTransforms, true, true, true, false);
    }

    public SpaceGearFeatureRenderer(FeatureRendererContext<T, M> context, float extra, ModelTransformer<T> maskTransforms, ModelTransformer<T> leftTankTransforms, ModelTransformer<T> rightTankTransforms, ModelTransformer<T> sensorGlassesTransforms, boolean isLeftOxygenTankEnabled, boolean isRightOxygenTankEnabled, boolean isOxygenMaskEnabled, boolean isSensorGlassesEnabled) {
        super(context);
        this.maskTransforms = maskTransforms;
        this.leftTankTransforms = leftTankTransforms;
        this.rightTankTransforms = rightTankTransforms;
        this.sensorGlassesTransforms = sensorGlassesTransforms;
        this.isLeftOxygenTankEnabled = isLeftOxygenTankEnabled;
        this.isRightOxygenTankEnabled = isRightOxygenTankEnabled;
        this.isOxygenMaskEnabled = isOxygenMaskEnabled;
        this.isSensorGlassesEnabled = isSensorGlassesEnabled;

        this.oxygenMask = new ModelPart(TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 10);
        this.oxygenMask.setPivot(0.0F, 4.0F, 0.0F);
        this.oxygenMask.addCuboid(-5.0F, -9.0F, -5.0F, 10, 10, 10, extra);
        this.sensorGlasses = new ModelPart(TEXTURE_WIDTH, TEXTURE_HEIGHT, 64, 10);
        this.sensorGlasses.setPivot(0.0F, 4.0F, 0.0F);
        this.sensorGlasses.addCuboid(-5.0F, -9.0F, -5.0F, 10, 10, 10, extra);

        this.leftOxygenTank = new ModelPart(TEXTURE_WIDTH, TEXTURE_HEIGHT, 20, 0);
        this.leftOxygenTank.setPivot(0.0F, 2.0F, 0.0F);
        this.leftOxygenTank.addCuboid(-3.0F, 1.0F, 2.0F, 3, 6, 4, extra);
        this.rightOxygenTank = new ModelPart(TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 0);
        this.rightOxygenTank.setPivot(0.0F, 2.0F, 0.0F);
        this.rightOxygenTank.addCuboid(0.0F, 1.0F, 2.0F, 3, 6, 4, extra);

        ModelPart leftOxygenTankWire = new ModelPart(TEXTURE_WIDTH, TEXTURE_HEIGHT, 40, 17);
        leftOxygenTankWire.setPivot(0.0F, 2.0F, 0.0F);
        leftOxygenTankWire.addCuboid(-2.0F, -3.0F, 0.0F, 1, 5, 8, extra);
        this.leftOxygenTank.addChild(leftOxygenTankWire);
        ModelPart rightOxygenTankWire = new ModelPart(TEXTURE_WIDTH, TEXTURE_HEIGHT, 40, 17);
        rightOxygenTankWire.setPivot(0.0F, 2.0F, 0.0F);
        rightOxygenTankWire.addCuboid(1.0F, -3.0F, 0.0F, 1, 5, 8, extra);
        this.rightOxygenTank.addChild(rightOxygenTankWire);
    }

    public SpaceGearFeatureRenderer(FeatureRendererContext<T, M> context, float extraHelmet, float extraTankLeft, float extraTankRight, float extraSensorGlasses, float pivotX, float pivotY, float pivotZ, ModelTransformer<T> maskTransforms, ModelTransformer<T> leftTankTransforms, ModelTransformer<T> rightTankTransforms, ModelTransformer<T> sensorGlassesTransforms) {
        this(context, extraHelmet, extraTankLeft, extraTankRight, extraSensorGlasses, pivotX, pivotY, pivotZ, maskTransforms, leftTankTransforms, rightTankTransforms, sensorGlassesTransforms, true, true, true, false);
    }

    public SpaceGearFeatureRenderer(FeatureRendererContext<T, M> context, float extraHelmet, float extraLeftTank, float extraRightTank, float extraSensorGlasses, float pivotX, float pivotY, float pivotZ, ModelTransformer<T> maskTransforms, ModelTransformer<T> leftTankTransforms, ModelTransformer<T> rightTankTransforms, ModelTransformer<T> sensorGlassesTransforms, boolean isOxygenMaskEnabled, boolean isLeftOxygenTankEnabled, boolean isRightOxygenTankEnabled, boolean isSensorGlassesEnabled) {
        super(context);
        this.maskTransforms = maskTransforms;
        this.leftTankTransforms = leftTankTransforms;
        this.rightTankTransforms = rightTankTransforms;
        this.sensorGlassesTransforms = sensorGlassesTransforms;
        this.isLeftOxygenTankEnabled = isLeftOxygenTankEnabled;
        this.isRightOxygenTankEnabled = isRightOxygenTankEnabled;
        this.isOxygenMaskEnabled = isOxygenMaskEnabled;
        this.isSensorGlassesEnabled = isSensorGlassesEnabled;

        this.oxygenMask = new ModelPart(TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 10);
        this.oxygenMask.setPivot(pivotX, pivotY, pivotZ);
        this.oxygenMask.addCuboid(-5.0F, -9.0F, -5.0F, 10, 10, 10, extraHelmet);
        this.sensorGlasses = new ModelPart(TEXTURE_WIDTH, TEXTURE_HEIGHT, 64, 10);
        this.sensorGlasses.setPivot(pivotX, pivotY, pivotZ);
        this.sensorGlasses.addCuboid(-5.0F, -9.0F, -5.0F, 10, 10, 10, extraSensorGlasses);

        this.leftOxygenTank = new ModelPart(TEXTURE_WIDTH, TEXTURE_HEIGHT, 20, 0);
        this.leftOxygenTank.setPivot(pivotX, 2.0F, pivotZ);
        this.leftOxygenTank.addCuboid(-3.0F, 1.0F, 2.0F, 3, 6, 4, extraLeftTank);
        this.rightOxygenTank = new ModelPart(TEXTURE_WIDTH, TEXTURE_HEIGHT, 0, 0);
        this.rightOxygenTank.setPivot(pivotX, 2.0F, pivotZ);
        this.rightOxygenTank.addCuboid(0.0F, 1.0F, 2.0F, 3, 6, 4, extraRightTank);

        ModelPart leftOxygenTankWire = new ModelPart(TEXTURE_WIDTH, TEXTURE_HEIGHT, 40, 17);
        leftOxygenTankWire.setPivot(0.0F, 2.0F, 0.0F);
        leftOxygenTankWire.addCuboid(-2.0F, -3.0F, 0.0F, 1, 5, 8, extraLeftTank);
        this.leftOxygenTank.addChild(leftOxygenTankWire);
        ModelPart rightOxygenTankWire = new ModelPart(TEXTURE_WIDTH, TEXTURE_HEIGHT, 40, 17);
        rightOxygenTankWire.setPivot(0.0F, 2.0F, 0.0F);
        rightOxygenTankWire.addCuboid(1.0F, -3.0F, 0.0F, 1, 5, 8, extraRightTank);
        this.rightOxygenTank.addChild(rightOxygenTankWire);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(getTexture(entity), true));
        matrices.push();
        maskTransforms.transformModel(matrices, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        if (isSensorGlassesEnabled) {
            matrices.push();
            matrices.multiply(new Quaternion(Vector3f.POSITIVE_Y, headYaw, true));
            matrices.multiply(new Quaternion(Vector3f.POSITIVE_X, headPitch, true));
            sensorGlasses.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
        if (isOxygenMaskEnabled) {
            matrices.push();
            matrices.multiply(new Quaternion(Vector3f.POSITIVE_Y, headYaw, true));
            matrices.multiply(new Quaternion(Vector3f.POSITIVE_X, headPitch, true));
            oxygenMask.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
        if (isLeftOxygenTankEnabled) {
            matrices.push();
            leftTankTransforms.transformModel(matrices, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            leftOxygenTank.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
        if (isRightOxygenTankEnabled) {
            matrices.push();
            rightTankTransforms.transformModel(matrices, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            rightOxygenTank.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
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

    boolean isLeftOxygenTankEnabled() {
        return this.isLeftOxygenTankEnabled;
    }

    boolean isRightOxygenTankEnabled() {
        return this.isRightOxygenTankEnabled;
    }

    boolean isOxygenMaskEnabled() {
        return this.isOxygenMaskEnabled;
    }

    boolean isSensorGlassesEnabled() {
        return this.isSensorGlassesEnabled;
    }

    void setLeftOxygenTankEnabled(boolean oxygenTankEnabled) {
        this.isLeftOxygenTankEnabled = oxygenTankEnabled;
    }

    void setRightOxygenTankEnabled(boolean oxygenTankEnabled) {
        this.isRightOxygenTankEnabled = oxygenTankEnabled;
    }

    void setOxygenMaskEnabled(boolean oxygenMaskEnabled) {
        this.isOxygenMaskEnabled = oxygenMaskEnabled;
    }

    void setSensorGlassesEnabled(boolean sensorGlassesEnabled) {
        this.isSensorGlassesEnabled = sensorGlassesEnabled;
    }
}
