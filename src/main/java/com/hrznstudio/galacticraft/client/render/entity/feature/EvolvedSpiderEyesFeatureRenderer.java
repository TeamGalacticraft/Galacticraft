package com.hrznstudio.galacticraft.client.render.entity.feature;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.client.model.entity.EvolvedSpiderModel;
import com.hrznstudio.galacticraft.entity.EvolvedSpiderEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EvolvedSpiderEyesFeatureRenderer<T extends EvolvedSpiderEntity, M extends EvolvedSpiderModel<T>> extends FeatureRenderer<T, M> {
    private static final RenderLayer LAYER = RenderLayer.getEyes(new Identifier(Constants.MOD_ID, "textures/entity/evolved/spider_eyes.png"));

    public EvolvedSpiderEyesFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(LAYER);
        this.getContextModel().render(matrices, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
