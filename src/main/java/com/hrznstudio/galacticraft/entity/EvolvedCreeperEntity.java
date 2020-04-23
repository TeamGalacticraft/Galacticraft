package com.hrznstudio.galacticraft.entity;

import com.hrznstudio.galacticraft.api.entity.EvolvedEntity;
import com.hrznstudio.galacticraft.mixin.EntityAttributeInstanceAccessor;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

import java.util.UUID;

public class EvolvedCreeperEntity extends CreeperEntity implements EvolvedEntity {
    private static final TrackedData<Boolean> BABY = DataTracker.registerData(EvolvedCreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final UUID BABY_SPEED_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final EntityAttributeModifier BABY_SPEED_BONUS = new EntityAttributeModifier(BABY_SPEED_ID, "Baby speed boost", 0.8D, EntityAttributeModifier.Operation.MULTIPLY_BASE);

    public EvolvedCreeperEntity(EntityType<? extends CreeperEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BABY, false);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        this.setBaby(tag.getBoolean("baby"));
        tag.putByte("ExplosionRadius", (byte) (this.isBaby() ? 2 : 3)); //overwrite
        super.readCustomDataFromTag(tag);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("baby", isBaby());
        tag.putByte("ExplosionRadius", (byte) (this.isBaby() ? 2 : 3));
    }

    @Override
    public boolean isBaby() {
        return this.dataTracker.get(BABY);
    }

    public void setBaby(boolean baby) {
        this.getDataTracker().set(BABY, baby);
        if (this.world != null && !this.world.isClient) {
            EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            entityAttributeInstance.removeModifier(BABY_SPEED_BONUS);
            if (baby) {
                ((EntityAttributeInstanceAccessor)entityAttributeInstance).callAddModifier(BABY_SPEED_BONUS);
            }
        }
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        if (this.isBaby()) {
            return this.getType().getDimensions().scaled(0.75F, 0.5F);
        } else {
            return this.getType().getDimensions();
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (BABY.equals(data)) {
            this.calculateDimensions();
        }

        super.onTrackedDataSet(data);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return this.isBaby() ? 0.75F : 1.4F;
    }

    @Override
    public double getHeightOffset() {
        return this.isBaby() ? 0.0D : -0.45D;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return super.createSpawnPacket();
    }
}
