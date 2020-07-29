package com.hrznstudio.galacticraft.client.render.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.client.model.entity.MoonVillagerEntityModel;
import com.hrznstudio.galacticraft.entity.MoonVillagerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MoonVillagerEntityRenderer extends MobEntityRenderer<MoonVillagerEntity, MoonVillagerEntityModel> {
    private static final Identifier TEXTURE = new Identifier(Constants.MOD_ID, "textures/entity/moon_villager/moon_villager.png");

    public MoonVillagerEntityRenderer(EntityRenderDispatcher dispatcher, ReloadableResourceManager reloadableResourceManager) {
        super(dispatcher, new MoonVillagerEntityModel(0.0F), 0.5F);
        this.addFeature(new HeadFeatureRenderer<>(this));
        this.addFeature(new VillagerClothingFeatureRenderer<>(this, reloadableResourceManager, "villager"));
        this.addFeature(new VillagerHeldItemFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(MoonVillagerEntity villagerEntity) {
        return TEXTURE;
    }

    @Override
    protected void scale(MoonVillagerEntity villagerEntity, MatrixStack matrixStack, float f) {
        float g = 0.9375F;
        if (villagerEntity.isBaby()) {
            g = (float)((double)g * 0.5D);
            this.shadowRadius = 0.25F;
        } else {
            this.shadowRadius = 0.5F;
        }

        matrixStack.scale(g, g, g);
    }
}
