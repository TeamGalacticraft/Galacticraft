package com.hrznstudio.galacticraft.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.world.World;

public class EvolvedSpiderEntity extends SpiderEntity {
    public EvolvedSpiderEntity(EntityType<? extends SpiderEntity> entityType, World world) {
        super(entityType, world);
    }
}
