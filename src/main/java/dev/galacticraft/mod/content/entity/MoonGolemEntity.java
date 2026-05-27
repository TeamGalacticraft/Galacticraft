/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import dev.galacticraft.api.entity.attribute.GcApiEntityAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MoonGolemEntity extends IronGolem {
    private static final int GC_ROOF_JUMP_COOLDOWN_TICKS = 10;
    private static final int GC_ROOF_JUMP_STABLE_TICKS = 8;
    private static final double GC_MOVEMENT_SPEED = 0.40D;
    private static final double GC_STEP_HEIGHT = 1.5D;
    private static final double GC_FOLLOW_RANGE = 48.0D;
    private static final double GC_ROOF_JUMP_Y_TOLERANCE = 0.05D;
    private @Nullable UUID gc$villageAggroTarget;
    private int gc$roofJumpCooldown;
    private int gc$roofJumpStableTicks;
    private double gc$lastTrackedTargetY = Double.NaN;
    private @Nullable UUID gc$roofJumpTrackedTarget;

    public MoonGolemEntity(EntityType<? extends IronGolem> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return IronGolem.createAttributes()
                .add(GcApiEntityAttributes.CAN_BREATHE_IN_SPACE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, GC_MOVEMENT_SPEED)
                .add(Attributes.STEP_HEIGHT, GC_STEP_HEIGHT)
                .add(Attributes.FOLLOW_RANGE, GC_FOLLOW_RANGE);
    }

    public void gc$setVillageAggroTarget(@Nullable Player player) {
        this.gc$villageAggroTarget = player != null ? player.getUUID() : null;
        this.setTarget(player);
    }

    public boolean gc$hasVillageAggroTarget() {
        return this.gc$villageAggroTarget != null;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (this.gc$villageAggroTarget != null && target == null) {
            Player aggroPlayer = this.level().getPlayerByUUID(this.gc$villageAggroTarget);
            if (aggroPlayer != null && aggroPlayer.isAlive() && !aggroPlayer.isCreative() && !aggroPlayer.isSpectator()) {
                super.setTarget(aggroPlayer);
                return;
            }
            this.gc$villageAggroTarget = null;
        }
        super.setTarget(target);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            this.gc$syncAttributes();
        }
        if (this.gc$roofJumpCooldown > 0) {
            this.gc$roofJumpCooldown--;
        }
        if (!this.level().isClientSide && this.gc$villageAggroTarget != null) {
            Player target = this.level().getPlayerByUUID(this.gc$villageAggroTarget);
            if (target == null || !target.isAlive() || target.isCreative() || target.isSpectator()) {
                this.gc$villageAggroTarget = null;
                this.gc$resetRoofJumpTracking();
            } else {
                if (this.getTarget() != target) {
                    super.setTarget(target);
                }
                this.gc$updateRoofJumpTracking(target);
                this.gc$tryRoofJump(target);
            }
        } else if (!this.level().isClientSide) {
            this.gc$resetRoofJumpTracking();
        }
    }

    private void gc$tryRoofJump(Player target) {
        if (!this.onGround() || this.gc$roofJumpCooldown > 0) {
            return;
        }

        double verticalGap = target.getY() - this.getY();
        if (verticalGap < 1.2D || verticalGap > 4.5D) {
            return;
        }

        double horizontalDistanceSqr = this.distanceToSqr(target.getX(), this.getY(), target.getZ());
        if (horizontalDistanceSqr > 49.0D) {
            return;
        }

        if (this.gc$roofJumpStableTicks < GC_ROOF_JUMP_STABLE_TICKS) {
            return;
        }

        Vec3 horizontalPush = new Vec3(target.getX() - this.getX(), 0.0D, target.getZ() - this.getZ());
        if (horizontalPush.lengthSqr() < 0.01D) {
            return;
        }

        horizontalPush = horizontalPush.normalize().scale(0.18D);
        this.jumpFromGround();
        this.setDeltaMovement(this.getDeltaMovement().add(horizontalPush.x, 0.22D, horizontalPush.z));
        this.hasImpulse = true;
        this.gc$roofJumpCooldown = GC_ROOF_JUMP_COOLDOWN_TICKS;
        this.gc$roofJumpStableTicks = 0;
        this.gc$lastTrackedTargetY = Double.NaN;
    }

    private void gc$updateRoofJumpTracking(Player target) {
        if (!target.getUUID().equals(this.gc$roofJumpTrackedTarget)) {
            this.gc$roofJumpTrackedTarget = target.getUUID();
            this.gc$roofJumpStableTicks = 0;
            this.gc$lastTrackedTargetY = Double.NaN;
        }

        double verticalGap = target.getY() - this.getY();
        double horizontalDistanceSqr = this.distanceToSqr(target.getX(), this.getY(), target.getZ());
        if (!target.onGround() || verticalGap < 1.2D || verticalGap > 4.5D || horizontalDistanceSqr > 49.0D) {
            this.gc$roofJumpStableTicks = 0;
            this.gc$lastTrackedTargetY = Double.NaN;
            return;
        }

        if (!Double.isFinite(this.gc$lastTrackedTargetY) || Math.abs(target.getY() - this.gc$lastTrackedTargetY) > GC_ROOF_JUMP_Y_TOLERANCE) {
            this.gc$roofJumpStableTicks = 1;
            this.gc$lastTrackedTargetY = target.getY();
            return;
        }

        this.gc$roofJumpStableTicks++;
    }

    private void gc$resetRoofJumpTracking() {
        this.gc$roofJumpStableTicks = 0;
        this.gc$lastTrackedTargetY = Double.NaN;
        this.gc$roofJumpTrackedTarget = null;
    }

    private void gc$syncAttributes() {
        if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null && this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() != GC_MOVEMENT_SPEED) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(GC_MOVEMENT_SPEED);
        }
        if (this.getAttribute(Attributes.STEP_HEIGHT) != null && this.getAttribute(Attributes.STEP_HEIGHT).getBaseValue() != GC_STEP_HEIGHT) {
            this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(GC_STEP_HEIGHT);
        }
        if (this.getAttribute(Attributes.FOLLOW_RANGE) != null && this.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() != GC_FOLLOW_RANGE) {
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(GC_FOLLOW_RANGE);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.gc$villageAggroTarget != null) {
            compound.putUUID("VillageAggroTarget", this.gc$villageAggroTarget);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("VillageAggroTarget")) {
            this.gc$villageAggroTarget = compound.getUUID("VillageAggroTarget");
        }
    }
}