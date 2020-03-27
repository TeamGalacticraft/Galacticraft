package com.hrznstudio.galacticraft.client.render.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.client.model.entity.EvolvedCreeperEntityModel;
import com.hrznstudio.galacticraft.client.render.entity.feature.EvolvedCreeperChargeFeatureRenderer;
import com.hrznstudio.galacticraft.entity.EvolvedCreeperEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class EvolvedCreeperEntityRenderer extends MobEntityRenderer<EvolvedCreeperEntity, EvolvedCreeperEntityModel> {
    private static final Identifier SKIN = new Identifier(Constants.MOD_ID, "textures/entity/evolved/creeper/creeper.png");

    public EvolvedCreeperEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher, new EvolvedCreeperEntityModel(0.0F), 0.5F);
        this.addFeature(new EvolvedCreeperChargeFeatureRenderer(this));
    }

    @Override
    protected void scale(EvolvedCreeperEntity entity, MatrixStack matrices, float tickDelta) {
        float g = entity.getClientFuseTime(tickDelta);
        float h = 1.0F + MathHelper.sin(g * 100.0F) * g * 0.01F;
        g = MathHelper.clamp(g, 0.0F, 1.0F);
        g *= g;
        g *= g;
        float i = (1.0F + g * 0.4F) * h;
        float j = (1.0F + g * 0.1F) / h;
        matrices.scale(i, j, i);
    }

    @Override
    protected float getWhiteOverlayProgress(EvolvedCreeperEntity entity, float tickDelta) {
        float g = entity.getClientFuseTime(tickDelta);
        return (int)(g * 10.0F) % 2 == 0 ? 0.0F : MathHelper.clamp(g, 0.5F, 1.0F);
    }

    @Override
    public void render(EvolvedCreeperEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(EvolvedCreeperEntity entity) {
        return SKIN;
    }
}
