package dev.galacticraft.mod.client.render.entity;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.render.entity.model.GCEntityModelLayer;
import dev.galacticraft.mod.client.render.entity.model.MoonVillagerModel;
import dev.galacticraft.mod.content.entity.MoonVillagerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class MoonVillagerRenderer extends MobRenderer<MoonVillagerEntity, MoonVillagerModel> {
    private static final ResourceLocation BASE_TEXTURE = Constant.id("textures/entity/villager/moon_villager.png");

    public MoonVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new MoonVillagerModel(context.bakeLayer(GCEntityModelLayer.MOON_VILLAGER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(MoonVillagerEntity entityGoalInfo) {
        return BASE_TEXTURE;
    }
}
