/*
 * Copyright (c) 2019-2024 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.content.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * More customizable version of {@link net.minecraft.world.entity.vehicle.VehicleEntity}
 */
public abstract class GCVehicle extends Entity {
    protected static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(GCVehicle.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DATA_ID_HURTDIR = SynchedEntityData.defineId(GCVehicle.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(GCVehicle.class, EntityDataSerializers.FLOAT);

    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

    public GCVehicle(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide || isRemoved()) {
            return true;
        } else if (isInvulnerableTo(source)) {
            return false;
        } else {
            setHurtDir(-getHurtDir());
            setHurtTime(10);
            markHurt();
            setDamage(getDamage() + amount * 10);
            gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
            boolean instaDestroy = source.getEntity() instanceof Player player && player.getAbilities().instabuild;
            if ((instaDestroy || !(getDamage() > getMaxDamage())) && !shouldSourceDestroy(source)) {
                if (instaDestroy) {
                    discard();
                }
            } else {
                destroy(source);
            }

            return true;
        }
    }

    @Override
    public void animateHurt(float yaw) {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() * getDamageMultiplier());
    }

    public float getDamageMultiplier() {
        return 10.0F;
    }

    public float getMaxDamage() {
        return 40.0F;
    }

    boolean shouldSourceDestroy(DamageSource source) {
        return false;
    }

    public void destroy(ItemStack selfAsItem) {
        this.kill();
        if (!selfAsItem.isEmpty()) {
            if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                if (this.hasCustomName()) {
                    selfAsItem.setHoverName(this.getCustomName());
                }

                this.spawnAtLocation(selfAsItem);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ID_HURT, 0);
        this.entityData.define(DATA_ID_HURTDIR, 1);
        this.entityData.define(DATA_ID_DAMAGE, 0.0F);
    }

    public void setHurtTime(int damageWobbleTicks) {
        this.entityData.set(DATA_ID_HURT, damageWobbleTicks);
    }

    public void setHurtDir(int damageWobbleSide) {
        this.entityData.set(DATA_ID_HURTDIR, damageWobbleSide);
    }

    public void setDamage(float damageWobbleStrength) {
        this.entityData.set(DATA_ID_DAMAGE, damageWobbleStrength);
    }

    public float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE);
    }

    public int getHurtTime() {
        return this.entityData.get(DATA_ID_HURT);
    }

    public int getHurtDir() {
        return this.entityData.get(DATA_ID_HURTDIR);
    }

    protected void destroy(DamageSource source) {
        this.destroy(getDropItem());
    }

    @Override
    public void tick() {
        super.tick();

        tickLerp();

        if (getHurtTime() > 0) {
            setHurtTime(getHurtTime() - 1);
        }

        if (getDamage() > 0.0F) {
            setDamage(getDamage() - 1.0F);
        }
    }

    @Override
    protected void reapplyPosition() {
        super.reapplyPosition();
        this.getPassengers().forEach(this::positionRider);
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYRot = yaw;
        this.lerpXRot = pitch;
        this.lerpSteps = 10;
    }

    private void tickLerp() { // Stolen from the boat class to fix the rocket from bugging out
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(getX(), getY(), getZ());
        }

        if (this.lerpSteps > 0) {
            double lerpedX = getX() + (this.lerpX - getX()) / (double) this.lerpSteps;
            double lerpedY = getY() + (this.lerpY - getY()) / (double) this.lerpSteps;
            double lerpedZ = getZ() + (this.lerpZ - getZ()) / (double) this.lerpSteps;
            double g = Mth.wrapDegrees(this.lerpYRot - (double) this.getYRot());
            setYRot(getYRot() + (float) g / (float) this.lerpSteps);
            setXRot(getXRot() + (float) (this.lerpXRot - (double) getXRot()) / (float) this.lerpSteps);
            --this.lerpSteps;
            setPos(lerpedX, lerpedY, lerpedZ);
            setRot(getYRot(), getXRot());
        }
    }

    public ItemStack getDropItem() {
        return ItemStack.EMPTY;
    };
}
