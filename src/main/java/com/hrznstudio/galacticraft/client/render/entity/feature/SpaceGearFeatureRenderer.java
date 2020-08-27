package com.hrznstudio.galacticraft.client.render.entity.feature;

import com.hrznstudio.galacticraft.Constants;
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

public class SpaceGearFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier(Constants.MOD_ID, "textures/entity/oxygen_gear.png");
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

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(getTexture(entity), true));
        matrices.push();
        maskTransforms.transformModel(matrices, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        matrices.push();
        matrices.multiply(new Quaternion(Vector3f.POSITIVE_Y, headYaw, true));
        matrices.multiply(new Quaternion(Vector3f.POSITIVE_X, headPitch, true));
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
        void transformModel(MatrixStack stack, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch);
    }
}
