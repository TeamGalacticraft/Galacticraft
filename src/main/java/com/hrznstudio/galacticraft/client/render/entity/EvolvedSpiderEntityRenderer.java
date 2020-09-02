package com.hrznstudio.galacticraft.client.render.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.client.model.entity.EvolvedSpiderModel;
import com.hrznstudio.galacticraft.client.render.entity.feature.EvolvedSpiderEyesFeatureRenderer;
import com.hrznstudio.galacticraft.entity.EvolvedSpiderEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EvolvedSpiderEntityRenderer extends MobEntityRenderer<EvolvedSpiderEntity, EvolvedSpiderModel<EvolvedSpiderEntity>> {
    public static final Identifier TEXTURE = new Identifier(Constants.MOD_ID, "textures/entity/evolved/spider.png");

    public EvolvedSpiderEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EvolvedSpiderModel<>(0.0F), 1.0F);
        this.addFeature(new EvolvedSpiderEyesFeatureRenderer<>(this));
    }

    @Override
    protected float getLyingAngle(EvolvedSpiderEntity spiderEntity) {
        return 180.0F;
    }

    @Override
    public Identifier getTexture(EvolvedSpiderEntity spiderEntity) {
        return TEXTURE;
    }
}
