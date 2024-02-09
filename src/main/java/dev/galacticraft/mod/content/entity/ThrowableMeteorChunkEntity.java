package dev.galacticraft.mod.content.entity;

import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.item.GCItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrowableMeteorChunkEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Boolean> HOT = SynchedEntityData.defineId(ThrowableMeteorChunkEntity.class, EntityDataSerializers.BOOLEAN);

    public ThrowableMeteorChunkEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public ThrowableMeteorChunkEntity(double x, double y, double z, Level level) {
        super(GCEntityTypes.THROWABLE_METEOR_CHUNK, x, y, z, level);
    }

    public ThrowableMeteorChunkEntity(LivingEntity shooter, Level level, boolean hot) {
        super(GCEntityTypes.THROWABLE_METEOR_CHUNK, shooter, level);
        this.entityData.set(HOT, hot);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HOT, false);
    }

    @Override
    protected Item getDefaultItem() {
        if (this.entityData.get(HOT)) {
            return GCItems.HOT_THROWABLE_METEOR_CHUNK;
        } else {
            return GCItems.THROWABLE_METEOR_CHUNK;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isInWater() && this.entityData.get(HOT)) {
            this.entityData.set(HOT, false);
            this.setItem(new ItemStack(GCItems.THROWABLE_METEOR_CHUNK));
            this.playEntityOnFireExtinguishedSound();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.entityData.get(HOT)) {
            result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 4.0F);
            result.getEntity().setSecondsOnFire(3);
        } else {
            result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 2.0F);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide)
            this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), this.getItem()));
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }
}
