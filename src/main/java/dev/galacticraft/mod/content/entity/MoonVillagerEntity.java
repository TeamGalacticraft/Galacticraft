package dev.galacticraft.mod.content.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;

public class MoonVillagerEntity extends Villager {
    public MoonVillagerEntity(EntityType<? extends MoonVillagerEntity> entityType, Level level) {
        super(entityType, level);
    }
}
