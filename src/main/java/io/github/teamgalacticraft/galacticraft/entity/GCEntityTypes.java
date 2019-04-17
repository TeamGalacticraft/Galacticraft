package io.github.teamgalacticraft.galacticraft.entity;

import io.github.teamgalacticraft.galacticraft.Constants;
import io.github.teamgalacticraft.galacticraft.entity.moonvillager.EntityMoonVillager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GCEntityTypes {

    /*public static final EntityType<EntityMoonVillager> MOON_VILLAGER = newEntityType(EntityMoonVillager.class, EntityCategory.CREATURE);//EntityType.Builder.create(EntityMoonVillager::new, EntityCategory.CREATURE);

    public static void register() {
        Registry.register(Registry.ENTITY_TYPE, new Identifier(Constants.Entities.MOON_VILLAGER), MOON_VILLAGER);
    }

    private static EntityType<?extends Entity> newEntityType(Class<?extends Entity> clazz, EntityCategory category) {
        return new EntityType<>(clazz, category);
    }
    */
}
