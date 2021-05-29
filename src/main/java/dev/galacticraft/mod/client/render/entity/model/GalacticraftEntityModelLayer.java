package dev.galacticraft.mod.client.render.entity.model;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.model.entity.EvolvedCreeperEntityModel;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class GalacticraftEntityModelLayer {
    public static final EntityModelLayer EVOLVED_CREEPER = new EntityModelLayer(new Identifier(Constant.MOD_ID, "evolved_creeper"), "main");
    public static final EntityModelLayer EVOLVED_CREEPER_ARMOR = new EntityModelLayer(new Identifier(Constant.MOD_ID, "evolved_creeper_armor"), "armor");

    public static void register() {
        EntityModelLayerRegistry.registerModelLayer(EVOLVED_CREEPER, () -> EvolvedCreeperEntityModel.getTexturedModelData(Dilation.NONE));
        EntityModelLayerRegistry.registerModelLayer(EVOLVED_CREEPER_ARMOR, () -> EvolvedCreeperEntityModel.getTexturedModelData(new Dilation(2.0f)));
    }
}
