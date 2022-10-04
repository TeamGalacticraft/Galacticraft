package dev.galacticraft.mod.client.render.entity;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.OliGrubEntityModel;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.entity.OliGrubEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class OliGrubEntityRenderer extends MobRenderer<OliGrubEntity, OliGrubEntityModel<OliGrubEntity>> {
    public OliGrubEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new OliGrubEntityModel<>(context.bakeLayer(GCEntityModelLayer.OLI_GRUB)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(OliGrubEntity entity) {
        return Constant.id(Constant.EntityTexture.OLI_GRUB);
    }
}
