package dev.galacticraft.mod.content.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class SmallAsteroid extends Entity {

    //private static final DataParameter<Float> SPIN_PITCH = EntityDataManager.createKey(EntitySmallAsteroid.class, DataSerializers.FLOAT);
    //private static final DataParameter<Float> SPIN_YAW = EntityDataManager.createKey(EntitySmallAsteroid.class, DataSerializers.FLOAT);
    //private static final DataParameter<Integer> ASTEROID_TYPE = EntityDataManager.createKey(EntitySmallAsteroid.class, DataSerializers.VARINT);
    public float spinPitch;
    public float spinYaw;
    public int type;
    private boolean firstUpdate = true;
    public SmallAsteroid(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {

    }
}
