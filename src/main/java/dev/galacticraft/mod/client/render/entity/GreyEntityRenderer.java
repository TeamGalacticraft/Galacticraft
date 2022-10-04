package dev.galacticraft.mod.client.render.entity;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.ArchGreyEntityModel;
import dev.galacticraft.mod.client.model.entity.GreyEntityModel;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.entity.ArchGreyEntity;
import dev.galacticraft.mod.entity.GreyEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class GreyEntityRenderer extends LivingEntityRenderer<GreyEntity, EntityModel<GreyEntity>> {
    public GreyEntityRenderer(EntityRendererProvider.Context context, EntityModel<GreyEntity> model) {
        super(context, model, 0.4f);
    }

    public GreyEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new GreyEntityModel<>(context.bakeLayer(GCEntityModelLayer.GREY)), 0.4f);
    }

    public static GreyEntityRenderer arch(EntityRendererProvider.Context context) {
        return new GreyEntityRenderer(context, new ArchGreyEntityModel<>(context.bakeLayer(GCEntityModelLayer.ARCH_GREY)));
    }

    @Override
    public ResourceLocation getTextureLocation(GreyEntity entity) {
        return entity instanceof ArchGreyEntity ? Constant.id(Constant.EntityTexture.ARCH_GREY) : Constant.id(Constant.EntityTexture.GREY);
    }
}
