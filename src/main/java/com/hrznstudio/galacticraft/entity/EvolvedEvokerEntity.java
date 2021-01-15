package com.hrznstudio.galacticraft.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.world.World;

public class EvolvedEvokerEntity extends EvokerEntity {
    public EvolvedEvokerEntity(EntityType<? extends EvolvedEvokerEntity> entityType, World world) {
        super(entityType, world);
    }
}
