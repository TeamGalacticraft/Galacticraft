package com.hrznstudio.galacticraft.entity.moonvillager;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonVillagerRenderer extends MobEntityRenderer<EntityMoonVillager, VillagerResemblingModel<EntityMoonVillager>> {

    public MoonVillagerRenderer(EntityRenderDispatcher entityRenderDispatcher_1) {
        super(entityRenderDispatcher_1, new VillagerResemblingModel<EntityMoonVillager>(1, 1, 1), 1);
    }

    @Override
    protected Identifier getTexture(EntityMoonVillager entityMoonVillager) {
        return new Identifier("galacticraft-rewoven:textures/entity/moon_villager/moon_villager.png");
    }
}
