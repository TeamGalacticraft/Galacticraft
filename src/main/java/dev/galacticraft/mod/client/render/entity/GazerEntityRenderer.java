package dev.galacticraft.mod.client.render.entity;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.GazerEntityModel;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.entity.GazerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GazerEntityRenderer extends MobRenderer<GazerEntity, GazerEntityModel<GazerEntity>> {
    public GazerEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new GazerEntityModel<>(context.bakeLayer(GCEntityModelLayer.GAZER)), 0);
    }

    @Override
    public ResourceLocation getTextureLocation(GazerEntity entity) {
        return Constant.id(Constant.EntityTexture.GAZER);
    }
}
