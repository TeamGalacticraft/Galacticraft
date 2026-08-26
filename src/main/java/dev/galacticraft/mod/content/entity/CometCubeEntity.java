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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class CometCubeEntity extends FlyingMob implements Enemy {
    private static final int MAX_HEIGHT_ABOVE_GROUND = 30;
    private static final int NOTICE_TICKS = 6;
    private static final int DASH_DURATION = 240;
    private static final int DASH_COOLDOWN = 25;
    private static final double WANDER_SPEED = 0.8D;
    private static final double CHARGE_SPEED = 1.5D;
    private static final double CHARGE_ACCELERATION = 0.35D;
    private static final double CLOSE_CHARGE_ACCELERATION = 0.35D;
    private static final double CLOSE_APPROACH_DISTANCE = 6.0D;
    private static final double MIN_CLOSE_SPEED_FACTOR = 0.3D;
    private static final double MAX_VERTICAL_CHARGE_SPEED = 0.45D;
    private static final double DETECTION_RANGE = 50.0D;

    @Nullable
    private LivingEntity dashTarget;
    private int dashTicks;
    private int dashCooldown;
    private boolean dashImpactHandled;

    public CometCubeEntity(EntityType<? extends FlyingMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoGravity(true);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanPassDoors(true);
        navigation.setCanFloat(true);
        return navigation;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new CometCubeDashGoal(this));
        this.goalSelector.addGoal(1, new CometCubeWanderGoal(this));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 50.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        this.updateFlightCeiling();
        if (this.dashCooldown > 0) {
            this.dashCooldown--;
        }
        if (!this.level().isClientSide) {
            this.tickDash();
        }
        this.fallDistance = 0.0F;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    private void updateFlightCeiling() {
        boolean hasGroundWithinRange = this.hasGroundWithinFlightRange();
        this.setNoGravity(hasGroundWithinRange);
        if (!hasGroundWithinRange) {
            Vec3 movement = this.getDeltaMovement();
            double fallSpeed = Math.max(-0.6D, movement.y - 0.08D);
            this.setDeltaMovement(movement.x, fallSpeed, movement.z);
        }
    }

    private boolean hasGroundWithinFlightRange() {
        BlockPos.MutableBlockPos cursor = this.blockPosition().mutable();
        for (int i = 0; i < MAX_HEIGHT_ABOVE_GROUND && cursor.getY() > this.level().getMinBuildHeight(); i++) {
            cursor.move(Direction.DOWN);
            if (this.level().getBlockState(cursor).blocksMotion()) {
                return true;
            }
        }
        return false;
    }

    private void tickDash() {
        if (this.dashTicks <= 0) {
            return;
        }

        LivingEntity target = this.dashTarget;
        if (target == null || !target.isAlive()) {
            this.stopDash();
            return;
        }

        Vec3 toTarget = target.getBoundingBox().getCenter().subtract(this.getBoundingBox().getCenter());
        if (toTarget.lengthSqr() > 1.0E-4D) {
            this.updateChargeVelocity(toTarget);
        }

        this.faceTarget(target);

        if (this.getBoundingBox().intersects(target.getBoundingBox())) {
            this.explodeIntoTarget(target);
            return;
        }

        this.dashTicks--;
        if (this.dashTicks <= 0) {
            this.stopDash();
        }
    }

    public boolean isDashing() {
        return this.dashTicks > 0;
    }

    public boolean canDashAt(LivingEntity target) {
        return this.dashCooldown <= 0
                && !this.isDashing()
                && !this.dashImpactHandled
                && target != null
                && target.isAlive()
                && this.distanceToSqr(target) <= DETECTION_RANGE * DETECTION_RANGE
                && this.hasLineOfSight(target);
    }

    public void startDash(LivingEntity target) {
        this.dashTarget = target;
        this.dashTicks = DASH_DURATION;
        this.dashCooldown = DASH_COOLDOWN;
        this.dashImpactHandled = false;
        this.getNavigation().stop();
    }

    private void stopDash() {
        this.dashTicks = 0;
        this.dashTarget = null;
        this.dashImpactHandled = false;
        this.setDeltaMovement(this.getDeltaMovement().scale(0.2D));
    }

    private void explodeIntoTarget(LivingEntity target) {
        if (this.dashImpactHandled || this.isRemoved()) {
            return;
        }
        this.dashImpactHandled = true;
        this.dashTicks = 0;
        this.dashTarget = null;
        target.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        this.discard();
    }

    private void updateChargeVelocity(Vec3 toTarget) {
        Vec3 currentVelocity = this.getDeltaMovement();
        Vec3 horizontal = new Vec3(toTarget.x, 0.0D, toTarget.z);
        double horizontalDistance = horizontal.length();
        double closeSpeedFactor = Mth.clamp(horizontalDistance / CLOSE_APPROACH_DISTANCE, MIN_CLOSE_SPEED_FACTOR, 1.0D);
        double desiredHorizontalSpeed = CHARGE_SPEED * closeSpeedFactor;
        Vec3 desiredHorizontalVelocity = horizontal.lengthSqr() > 1.0E-4D
                ? horizontal.normalize().scale(desiredHorizontalSpeed)
                : Vec3.ZERO;
        double desiredVerticalVelocity = Mth.clamp(toTarget.y * 0.2D, -MAX_VERTICAL_CHARGE_SPEED, MAX_VERTICAL_CHARGE_SPEED);
        Vec3 desiredVelocity = new Vec3(desiredHorizontalVelocity.x, desiredVerticalVelocity, desiredHorizontalVelocity.z);
        double response = horizontalDistance <= CLOSE_APPROACH_DISTANCE ? CLOSE_CHARGE_ACCELERATION : CHARGE_ACCELERATION;
        Vec3 velocity = currentVelocity.lerp(desiredVelocity, response);
        this.setDeltaMovement(velocity);
        this.hasImpulse = true;
    }

    private double getWanderTargetY(double currentY, int offsetY) {
        double targetY = currentY + offsetY;
        if (this.hasGroundWithinFlightRange()) {
            return targetY;
        }
        return Math.min(currentY, targetY);
    }

    private void faceTarget(LivingEntity target) {
        Vec3 toTarget = target.getBoundingBox().getCenter().subtract(this.getBoundingBox().getCenter());
        double horizontalDistance = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        float yaw = (float) (Mth.atan2(toTarget.z, toTarget.x) * Mth.RAD_TO_DEG) - 90.0F;
        float pitch = (float) (-(Mth.atan2(toTarget.y, horizontalDistance) * Mth.RAD_TO_DEG));
        this.setYRot(yaw);
        this.setXRot(pitch);
        this.yBodyRot = yaw;
        this.yHeadRot = yaw;
        this.yBodyRotO = yaw;
        this.yHeadRotO = yaw;
        this.getLookControl().setLookAt(target, 360.0F, 360.0F);
    }

    private boolean isAirTarget(BlockPos pos) {
        return this.level().isEmptyBlock(pos) && this.level().isEmptyBlock(pos.above());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 5.04F)
                .add(Attributes.FLYING_SPEED, 5.04F)
                .add(Attributes.FOLLOW_RANGE, DETECTION_RANGE)
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(GcApiEntityAttributes.CAN_BREATHE_IN_SPACE, 1.0D);
    }

    private static class CometCubeDashGoal extends Goal {
        private final CometCubeEntity cometCube;
        private int seenTargetTicks;

        private CometCubeDashGoal(CometCubeEntity cometCube) {
            this.cometCube = cometCube;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.cometCube.getTarget();
            return target instanceof Player && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.cometCube.getTarget();
            return target instanceof Player && target.isAlive();
        }

        @Override
        public void start() {
            this.seenTargetTicks = 0;
        }

        @Override
        public void stop() {
            this.cometCube.getNavigation().stop();
            this.seenTargetTicks = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = this.cometCube.getTarget();
            if (target == null) {
                return;
            }

            this.cometCube.faceTarget(target);

            if (!this.cometCube.hasLineOfSight(target)) {
                this.seenTargetTicks = 0;
                this.cometCube.getNavigation().stop();
                return;
            }

            if (this.seenTargetTicks < NOTICE_TICKS) {
                this.seenTargetTicks++;
                this.cometCube.getNavigation().stop();
                return;
            }

            if (this.cometCube.isDashing()) {
                return;
            }

            if (this.cometCube.canDashAt(target)) {
                this.cometCube.startDash(target);
            }
        }
    }

    private static class CometCubeWanderGoal extends Goal {
        private final CometCubeEntity cometCube;

        private CometCubeWanderGoal(CometCubeEntity cometCube) {
            this.cometCube = cometCube;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.cometCube.getTarget() == null && !this.cometCube.isDashing() && this.cometCube.getRandom().nextInt(6) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.cometCube.getTarget() == null && !this.cometCube.getNavigation().isDone();
        }

        @Override
        public void start() {
            for (int i = 0; i < 10; i++) {
                double x = this.cometCube.getX() + Mth.nextInt(this.cometCube.getRandom(), -8, 8);
                double y = this.cometCube.getWanderTargetY(this.cometCube.getY(), Mth.nextInt(this.cometCube.getRandom(), -4, 4));
                double z = this.cometCube.getZ() + Mth.nextInt(this.cometCube.getRandom(), -8, 8);
                BlockPos pos = BlockPos.containing(x, y, z);
                if (this.cometCube.isAirTarget(pos)) {
                    this.cometCube.getNavigation().moveTo(x, y, z, WANDER_SPEED);
                    break;
                }
            }
        }

        @Override
        public void stop() {
            this.cometCube.getNavigation().stop();
        }
    }
}
