package dev.galacticraft.mod.client.render.entity;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.CometCubeEntityModel;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.entity.CometCubeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CometCubeEntityRenderer extends MobRenderer<CometCubeEntity, CometCubeEntityModel<CometCubeEntity>> {
    public CometCubeEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new CometCubeEntityModel<>(context.bakeLayer(GCEntityModelLayer.COMET_CUBE)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(CometCubeEntity entity) {
        return Constant.id(Constant.EntityTexture.COMET_CUBE);
    }
}
