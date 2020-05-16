package com.hrznstudio.galacticraft.entity.attribute;

import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.entity.MoonVillagerEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class GalacticraftDefaultAttributes {
    public static void register() {
        FabricDefaultAttributeRegistry.register(GalacticraftEntityTypes.MOON_VILLAGER, MoonVillagerEntity.createMoonVillagerAttributes());
    }
}
