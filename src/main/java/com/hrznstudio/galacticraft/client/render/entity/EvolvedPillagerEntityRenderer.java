package com.hrznstudio.galacticraft.client.render.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.client.model.entity.EvolvedPillagerEntityModel;
import com.hrznstudio.galacticraft.entity.EvolvedPillagerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.util.Identifier;

public class EvolvedPillagerEntityRenderer extends MobEntityRenderer<EvolvedPillagerEntity, EvolvedPillagerEntityModel> {
    private static final Identifier TEXTURE = new Identifier(Constants.MOD_ID, "textures/entity/evolved/pillager.png");

    public EvolvedPillagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EvolvedPillagerEntityModel(), 0.5F);
        this.addFeature(new HeldItemFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(EvolvedPillagerEntity pillagerEntity) {
        return TEXTURE;
    }
}
