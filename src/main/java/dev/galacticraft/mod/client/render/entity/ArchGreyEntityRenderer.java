package dev.galacticraft.mod.client.render.entity;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.ArchGreyEntityModel;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.entity.ArchGreyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class ArchGreyEntityRenderer extends LivingEntityRenderer<ArchGreyEntity, ArchGreyEntityModel<ArchGreyEntity>> {
    public ArchGreyEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ArchGreyEntityModel<>(context.bakeLayer(GCEntityModelLayer.ARCH_GREY)), 0.4f);
    }

    @Override
    public ResourceLocation getTextureLocation(ArchGreyEntity entity) {
        return Constant.id("textures/entity/arch_grey.png");
    }
}
