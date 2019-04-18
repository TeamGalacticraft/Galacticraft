package io.github.teamgalacticraft.galacticraft.entity;

import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.entity.moonvillager.EntityMoonVillager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GalacticraftEntityTypes {

    public static final EntityType<?> MOON_VILLAGER = EntityType.Builder.create(EntityMoonVillager::new, EntityCategory.CREATURE).build("galacticraft-rewoven:" + Constants.Entities.MOON_VILLAGER);

    public static void register() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.Entities.MOON_VILLAGER), MOON_VILLAGER);
    }
}
