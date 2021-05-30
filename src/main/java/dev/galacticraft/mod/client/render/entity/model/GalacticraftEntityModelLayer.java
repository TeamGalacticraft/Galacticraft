package dev.galacticraft.mod.client.render.entity.model;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.EvolvedCreeperEntityModel;
import dev.galacticraft.mod.client.model.entity.MoonVillagerEntityModel;
import dev.galacticraft.mod.client.render.block.entity.BasicSolarPanelBlockEntityRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class GalacticraftEntityModelLayer {
    public static final EntityModelLayer EVOLVED_CREEPER = new EntityModelLayer(new Identifier(Constant.MOD_ID, "evolved_creeper"), "main");
    public static final EntityModelLayer EVOLVED_CREEPER_ARMOR = new EntityModelLayer(new Identifier(Constant.MOD_ID, "evolved_creeper_armor"), "armor");
    public static final EntityModelLayer MOON_VILLAGER = new EntityModelLayer(new Identifier(Constant.MOD_ID, "moon_villager"), "main");
    public static final EntityModelLayer SOLAR_PANEL = new EntityModelLayer(new Identifier(Constant.MOD_ID, "solar_panel"), "main");

    public static void register() {
        EntityModelLayerRegistry.registerModelLayer(EVOLVED_CREEPER, () -> EvolvedCreeperEntityModel.getTexturedModelData(Dilation.NONE));
        EntityModelLayerRegistry.registerModelLayer(EVOLVED_CREEPER_ARMOR, () -> EvolvedCreeperEntityModel.getTexturedModelData(new Dilation(2.0f)));
        EntityModelLayerRegistry.registerModelLayer(MOON_VILLAGER, () -> TexturedModelData.of(MoonVillagerEntityModel.getModelData(), 64, 64));
        EntityModelLayerRegistry.registerModelLayer(SOLAR_PANEL, BasicSolarPanelBlockEntityRenderer::getTexturedModelData);
    }
}
