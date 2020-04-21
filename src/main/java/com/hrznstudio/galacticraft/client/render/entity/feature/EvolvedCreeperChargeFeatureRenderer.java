package com.hrznstudio.galacticraft.client.render.entity.feature;

import com.hrznstudio.galacticraft.client.model.entity.EvolvedCreeperEntityModel;
import com.hrznstudio.galacticraft.entity.EvolvedCreeperEntity;
import net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.Identifier;

public class EvolvedCreeperChargeFeatureRenderer extends EnergySwirlOverlayFeatureRenderer<EvolvedCreeperEntity, EvolvedCreeperEntityModel> {

    private static final Identifier TEX = new Identifier("textures/entity/creeper/creeper_armor.png");
    private final EvolvedCreeperEntityModel model = new EvolvedCreeperEntityModel(2.0F);

    public EvolvedCreeperChargeFeatureRenderer(FeatureRendererContext<EvolvedCreeperEntity, EvolvedCreeperEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    protected float getEnergySwirlX(float partialAge) {
        return partialAge * 0.1F;
    }

    @Override
    protected Identifier getEnergySwirlTexture() {
        return TEX;
    }

    @Override
    protected EntityModel<EvolvedCreeperEntity> getEnergySwirlModel() {
        return model;
    }
}
