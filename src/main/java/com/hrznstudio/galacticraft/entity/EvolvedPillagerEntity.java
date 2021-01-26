package com.hrznstudio.galacticraft.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.world.World;

public class EvolvedPillagerEntity extends PillagerEntity {
    public EvolvedPillagerEntity(EntityType<? extends EvolvedPillagerEntity> entityType, World world) {
        super(entityType, world);
    }
}
