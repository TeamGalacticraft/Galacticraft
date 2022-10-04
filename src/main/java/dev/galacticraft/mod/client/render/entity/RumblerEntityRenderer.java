package dev.galacticraft.mod.client.render.entity;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.RumblerEntityModel;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.entity.RumblerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RumblerEntityRenderer extends MobRenderer<RumblerEntity, RumblerEntityModel<RumblerEntity>> {
    public RumblerEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new RumblerEntityModel<>(context.bakeLayer(GCEntityModelLayer.RUMBLER)), 2f);
    }

    @Override
    public ResourceLocation getTextureLocation(RumblerEntity entity) {
        return Constant.id(Constant.EntityTexture.RUMBLER);
    }
}
