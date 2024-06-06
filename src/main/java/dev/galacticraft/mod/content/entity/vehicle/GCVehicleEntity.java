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

package dev.galacticraft.mod.content.entity.vehicle;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * More customizable version of {@link net.minecraft.world.entity.vehicle.VehicleEntity}
 * Note: actually, it turns out that VehicleEntity contains a package-protected (i.e. no modifier) abstract method
 * getDropItem() that a galacticraft vehicle would not be able to implement. That being said, I'm still copying all the
 * methods from VehicleEntity
 * Should contain code common to all GC vehicles (e.g. rockets, cargo rockets, buggies, landers, and astrominers)
 */
public abstract class GCVehicleEntity extends Entity implements Container {

    // **************************************** FIELDS ****************************************

    protected static final ResourceLocation NULL_ID = new ResourceLocation("null");
    protected static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(GCVehicleEntity.class, EntityDataSerializers.FLOAT);

    protected static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(GCVehicleEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DATA_ID_HURTDIR = SynchedEntityData.defineId(GCVehicleEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(GCVehicleEntity.class, EntityDataSerializers.FLOAT);

    protected NonNullList<ItemStack> inventory = NonNullList.withSize(0, ItemStack.EMPTY);;
    //protected InventoryStorage storage;

    // **************************************** CONSTRUCTOR ****************************************

    public GCVehicleEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // **************************************** DATA ****************************************

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || (source.is(DamageTypeTags.IS_PROJECTILE) && !projectileDoesDamage(source));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ID_HURT, 0);
        this.entityData.define(DATA_ID_HURTDIR, 1);
        this.entityData.define(DATA_ID_DAMAGE, 0.0f);
    }

    public int getHurtTime() {
        return this.entityData.get(DATA_ID_HURT);
    }

    public void setHurtTime(int damageWobbleTicks) {
        this.entityData.set(DATA_ID_HURT, damageWobbleTicks);
    }

    public int getHurtDir() {
        return this.entityData.get(DATA_ID_HURTDIR);
    }

    public void setHurtDir(int damageWobbleSide) {
        this.entityData.set(DATA_ID_HURTDIR, damageWobbleSide);
    }

    public float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE);
    }

    public void setDamage(float damageWobbleStrength) {
        this.entityData.set(DATA_ID_DAMAGE, damageWobbleStrength);
    }

    public abstract Item getDropItem();

    public float getDamageMultiplier() {
        return 10.0F;
    }

    public float getMaxDamage() {
        return 40.0F;
    }

    // consider deleting
    boolean shouldSourceDestroy(DamageSource source) {
        return false;
    }

    public float getSpeed() {
        return this.getEntityData().get(SPEED);
    }

    public void setSpeed(float speed) {
        this.getEntityData().set(SPEED, speed);
    }

    // **************************************** INTERACTION ****************************************

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean bl;
        if (this.level().isClientSide || this.isRemoved()) {
            return true;
        }
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.markHurt();
        this.setDamage(this.getDamage() + amount * 10.0f);
        this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
        boolean bl2 = bl = source.getEntity() instanceof Player && ((Player)source.getEntity()).getAbilities().instabuild;
        if (!bl && this.getDamage() > 40.0f || this.shouldSourceDestroy(source)) {
            this.destroy(source);
        } else if (bl) {
            this.discard();
        }
        return true;
    }

    private boolean projectileDoesDamage (DamageSource source) {
        Entity sourceEntity = source.getDirectEntity();
        if (sourceEntity instanceof AbstractHurtingProjectile) {
            return true;
        }
        if (sourceEntity instanceof AbstractArrow arrow) {
            Arrow newArrow = new Arrow((EntityType<? extends Arrow>) arrow.getType(), level());
            CompoundTag arrowNbt = new CompoundTag();
            arrow.addAdditionalSaveData(arrowNbt);
            newArrow.readAdditionalSaveData(arrowNbt);
            newArrow.setPos(arrow.position());
            Vec3 arrowVel = arrow.getDeltaMovement();
            float bounceFactor = 0.025f;
            newArrow.setDeltaMovement(-arrowVel.x * bounceFactor, -arrowVel.y * bounceFactor, -arrowVel.z * bounceFactor);
            level().addFreshEntity(newArrow);
            arrow.remove(RemovalReason.DISCARDED);
            this.playSound(SoundEvents.ANVIL_PLACE, 1.0f, 0.7f);
        }
        return false;
    }

    public void destroy(Item selfAsItem) {
        this.kill();
        if (!this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            return;
        }
        ItemStack itemStack = new ItemStack(selfAsItem);
        if (this.hasCustomName()) {
            itemStack.setHoverName(this.getCustomName());
        }
        this.spawnAtLocation(itemStack);
    }

    protected void destroy(DamageSource source) {
        this.destroy(this.getDropItem());
    }

    @Override
    public void animateHurt(float yaw) {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() * getDamageMultiplier());
    }

    // **************************************** TICK ****************************************

    @Override
    public void tick() {
        super.tick();

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

    // **************************************** CONTAINER ****************************************

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.inventory, slot, amount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.inventory, slot);
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return this.position().closerThan(player.position(), 8.0);
    }

}
