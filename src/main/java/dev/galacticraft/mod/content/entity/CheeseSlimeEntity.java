/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.*;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class CheeseSlimeEntity extends Mob implements Enemy {
    private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(CheeseSlimeEntity.class, EntityDataSerializers.INT);
    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 127;
    public static final int MAX_NATURAL_SIZE = 4;
    public float targetSquish;
    public float squish;
    public float oSquish;
    private boolean wasOnGround;

    public CheeseSlimeEntity(EntityType<? extends CheeseSlimeEntity> entityType, Level level) {
        super(entityType, level);
        fixupDimensions();
        moveControl = new CheeseSlimeEntity.CheeseSlimeMoveControl(this);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new CheeseSlimeFloatGoal(this));
        goalSelector.addGoal(2, new CheeseSlimeAttackGoal(this));
        goalSelector.addGoal(3, new CheeseSlimeRandomDirectionGoal(this));
        goalSelector.addGoal(5, new CheeseSlimeKeepOnJumpingGoal(this));
        targetSelector
                .addGoal(1, new NearestAttackableTargetGoal(this, Player.class, 10, true, false,
                        livingEntity -> Math.abs(((LivingEntity)livingEntity).getY() - getY()) <= 4.0));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder compositeStateBuilder) {
        super.defineSynchedData(compositeStateBuilder);
        compositeStateBuilder.define(ID_SIZE, 1);
    }

    @VisibleForTesting
    public void setSize(int size, boolean heal) {
        int i = Mth.clamp(size, 1, 127);
        entityData.set(ID_SIZE, i);
        reapplyPosition();
        refreshDimensions();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(i * i);
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2F + 0.1F * i);
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(i);
        if (heal) {
            setHealth(getMaxHealth());
        }

        xpReward = i;
    }

    public int getSize() {
        return entityData.get(ID_SIZE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Size", this.getSize() - 1);
        compound.putBoolean("wasOnGround", this.wasOnGround);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        setSize(compound.getInt("Size") + 1, false);
        super.readAdditionalSaveData(compound);
        wasOnGround = compound.getBoolean("wasOnGround");
    }

    public boolean isTiny() {
        return getSize() <= 1;
    }

    protected ParticleOptions getParticleType() {
        return ParticleTypes.ITEM_SLIME;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return getSize() > 0;
    }

    @Override
    public void tick() {
        squish = squish + (targetSquish - squish) * 0.5F;
        oSquish = squish;
        super.tick();
        if (onGround() && !wasOnGround) {
            float f = getDimensions(getPose()).width() * 2.0F;
            float g = f / 2.0F;

            for (int i = 0; i < f * 16.0F; i++) {
                float h = random.nextFloat() * (float) (Math.PI * 2);
                float j = random.nextFloat() * 0.5F + 0.5F;
                float k = Mth.sin(h) * g * j;
                float l = Mth.cos(h) * g * j;
                level().addParticle(getParticleType(), getX() + k, getY(), getZ() + l, 0.0, 0.0, 0.0);
            }

            playSound(getSquishSound(), getSoundVolume(), ((random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            targetSquish = -0.5F;
        } else if (!onGround() && wasOnGround) {
            targetSquish = 1.0F;
        }

        wasOnGround = onGround();
        decreaseSquish();
    }

    protected void decreaseSquish() {
        targetSquish *= 0.6F;
    }

    protected int getJumpDelay() {
        return random.nextInt(20) + 10;
    }

    @Override
    public void refreshDimensions() {
        double d = getX();
        double e = getY();
        double f = getZ();
        super.refreshDimensions();
        setPos(d, e, f);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (ID_SIZE.equals(data)) {
            refreshDimensions();
            setYRot(yHeadRot);
            yBodyRot = yHeadRot;
            if (isInWater() && random.nextInt(20) == 0) {
                doWaterSplashEffect();
            }
        }

        super.onSyncedDataUpdated(data);
    }

    @Override
    public EntityType<? extends CheeseSlimeEntity> getType() {
        return (EntityType<? extends CheeseSlimeEntity>)super.getType();
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        int i = getSize();
        if (!level().isClientSide && i > 1 && isDeadOrDying()) {
            Component component = getCustomName();
            boolean bl = isNoAi();
            float f = getDimensions(getPose()).width();
            float g = f / 2.0F;
            int j = i / 2;
            int k = 2 + random.nextInt(3);

            for (int l = 0; l < k; l++) {
                float h = (l % 2 - 0.5F) * g;
                float m = (l / 2 - 0.5F) * g;
                CheeseSlimeEntity cheeseSlime = getType().create(level());
                if (cheeseSlime != null) {
                    if (isPersistenceRequired()) {
                        cheeseSlime.setPersistenceRequired();
                    }

                    cheeseSlime.setCustomName(component);
                    cheeseSlime.setNoAi(bl);
                    cheeseSlime.setInvulnerable(isInvulnerable());
                    cheeseSlime.setSize(j, true);
                    cheeseSlime.moveTo(getX() + h, getY() + 0.5, getZ() + m, random.nextFloat() * 360.0F, 0.0F);
                    level().addFreshEntity(cheeseSlime);
                }
            }
        }

        super.remove(reason);
    }

    @Override
    public void push(Entity entityGoalInfo) {
        super.push(entityGoalInfo);
        if (entityGoalInfo instanceof IronGolem && isDealsDamage()) {
            dealDamage((LivingEntity)entityGoalInfo);
        }
    }

    @Override
    public void playerTouch(Player player) {
        if (isDealsDamage()) {
            dealDamage(player);
        }
    }

    protected void dealDamage(LivingEntity target) {
        if (isAlive() && isWithinMeleeAttackRange(target) && hasLineOfSight(target)) {
            DamageSource damageSource = damageSources().mobAttack(this);
            if (target.hurt(damageSource, getAttackDamage())) {
                playSound(SoundEvents.SLIME_ATTACK, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
                if (level() instanceof ServerLevel serverLevel) {
                    EnchantmentHelper.doPostAttackEffects(serverLevel, target, damageSource);
                }
            }
        }
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vec3(0.0, dimensions.height() - 0.015625 * getSize() * scaleFactor, 0.0);
    }

    protected boolean isDealsDamage() {
        return !isTiny() && isEffectiveAi();
    }

    protected float getAttackDamage() {
        return (float)getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return isTiny() ? SoundEvents.SLIME_HURT_SMALL : SoundEvents.SLIME_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return isTiny() ? SoundEvents.SLIME_DEATH_SMALL : SoundEvents.SLIME_DEATH;
    }

    protected SoundEvent getSquishSound() {
        return isTiny() ? SoundEvents.SLIME_SQUISH_SMALL : SoundEvents.SLIME_SQUISH;
    }

    public static boolean checkSlimeSpawnRules(EntityType<Slime> type, LevelAccessor level, MobSpawnType mobSpawnType, BlockPos pos, RandomSource randomSource) {
        if (MobSpawnType.isSpawner(mobSpawnType)) {
            return checkMobSpawnRules(type, level, mobSpawnType, pos, randomSource);
        } else {
            if (level.getDifficulty() != Difficulty.PEACEFUL) {
                if (mobSpawnType == MobSpawnType.SPAWNER) {
                    return checkMobSpawnRules(type, level, mobSpawnType, pos, randomSource);
                }

                if (level.getBiome(pos).is(BiomeTags.ALLOWS_SURFACE_SLIME_SPAWNS)
                        && pos.getY() > 50
                        && pos.getY() < 70
                        && randomSource.nextFloat() < 0.5F
                        && randomSource.nextFloat() < level.getMoonBrightness()
                        && level.getMaxLocalRawBrightness(pos) <= randomSource.nextInt(8)) {
                    return checkMobSpawnRules(type, level, mobSpawnType, pos, randomSource);
                }

                if (!(level instanceof WorldGenLevel)) {
                    return false;
                }

                ChunkPos chunkPos = new ChunkPos(pos);
                boolean bl = WorldgenRandom.seedSlimeChunk(chunkPos.x, chunkPos.z, ((WorldGenLevel)level).getSeed(), 987234911L).nextInt(10) == 0;
                if (randomSource.nextInt(10) == 0 && bl && pos.getY() < 40) {
                    return checkMobSpawnRules(type, level, mobSpawnType, pos, randomSource);
                }
            }

            return false;
        }
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F * getSize();
    }

    @Override
    public int getMaxHeadXRot() {
        return 0;
    }

    protected boolean doPlayJumpSound() {
        return getSize() > 0;
    }

    @Override
    public void jumpFromGround() {
        Vec3 vec3 = getDeltaMovement();
        setDeltaMovement(vec3.x, getJumpPower(), vec3.z);
        hasImpulse = true;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance instance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData) {
        RandomSource randomSource = level.getRandom();
        int i = randomSource.nextInt(3);
        if (i < 2 && randomSource.nextFloat() < 0.5F * instance.getSpecialMultiplier()) {
            i++;
        }

        int j = 1 << i;
        setSize(j, true);
        return super.finalizeSpawn(level, instance, mobSpawnType, spawnGroupData);
    }

    float getSoundPitch() {
        float f = isTiny() ? 1.4F : 0.8F;
        return ((random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F) * f;
    }

    protected SoundEvent getJumpSound() {
        return isTiny() ? SoundEvents.SLIME_JUMP_SMALL : SoundEvents.SLIME_JUMP;
    }

    @Override
    public EntityDimensions getDefaultDimensions(Pose pose) {
        return super.getDefaultDimensions(pose).scale(getSize());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes();
    }

    static class CheeseSlimeAttackGoal extends Goal {
        private final CheeseSlimeEntity cheeseSlime;
        private int growTiredTimer;

        public CheeseSlimeAttackGoal(CheeseSlimeEntity cheeseSlime) {
            this.cheeseSlime = cheeseSlime;
            setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingEntity = cheeseSlime.getTarget();
            if (livingEntity == null) {
                return false;
            } else {
                return cheeseSlime.canAttack(livingEntity) && cheeseSlime.getMoveControl() instanceof CheeseSlimeMoveControl;
            }
        }

        @Override
        public void start() {
            growTiredTimer = reducedTickDelay(300);
            super.start();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingEntity = cheeseSlime.getTarget();
            if (livingEntity == null) {
                return false;
            } else {
                return cheeseSlime.canAttack(livingEntity) && --growTiredTimer > 0;
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = cheeseSlime.getTarget();
            if (livingEntity != null) {
                cheeseSlime.lookAt(livingEntity, 10.0F, 10.0F);
            }

            if (cheeseSlime.getMoveControl() instanceof CheeseSlimeEntity.CheeseSlimeMoveControl slimeMoveControl) {
                slimeMoveControl.setDirection(cheeseSlime.getYRot(), cheeseSlime.isDealsDamage());
            }
        }
    }

    static class CheeseSlimeFloatGoal extends Goal {
        private final CheeseSlimeEntity cheeseSlime;

        public CheeseSlimeFloatGoal(CheeseSlimeEntity cheeseSlime) {
            this.cheeseSlime = cheeseSlime;
            setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
            cheeseSlime.getNavigation().setCanFloat(true);
        }

        @Override
        public boolean canUse() {
            return (cheeseSlime.isInWater() || cheeseSlime.isInLava()) && cheeseSlime.getMoveControl() instanceof CheeseSlimeEntity.CheeseSlimeMoveControl;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (cheeseSlime.getRandom().nextFloat() < 0.8F) {
                cheeseSlime.getJumpControl().jump();
            }

            if (cheeseSlime.getMoveControl() instanceof CheeseSlimeEntity.CheeseSlimeMoveControl slimeMoveControl) {
                slimeMoveControl.setWantedMovement(1.2);
            }
        }
    }

    static class CheeseSlimeKeepOnJumpingGoal extends Goal {
        private final CheeseSlimeEntity cheeseSlime;

        public CheeseSlimeKeepOnJumpingGoal(CheeseSlimeEntity cheeseSlime) {
            this.cheeseSlime = cheeseSlime;
            setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !this.cheeseSlime.isPassenger();
        }

        @Override
        public void tick() {
            if (cheeseSlime.getMoveControl() instanceof CheeseSlimeEntity.CheeseSlimeMoveControl slimeMoveControl) {
                slimeMoveControl.setWantedMovement(1.0);
            }
        }
    }

    static class CheeseSlimeMoveControl extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private final CheeseSlimeEntity cheeseSlime;
        private boolean isAggressive;

        public CheeseSlimeMoveControl(CheeseSlimeEntity cheeseSlime) {
            super(cheeseSlime);
            this.cheeseSlime = cheeseSlime;
            yRot = 180.0F * cheeseSlime.getYRot() / (float) Math.PI;
        }

        public void setDirection(float targetYaw, boolean jumpOften) {
            yRot = targetYaw;
            isAggressive = jumpOften;
        }

        public void setWantedMovement(double speed) {
            speedModifier = speed;
            operation = MoveControl.Operation.MOVE_TO;
        }

        @Override
        public void tick() {
            mob.setYRot(rotlerp(mob.getYRot(), yRot, 90.0F));
            mob.yHeadRot = mob.getYRot();
            mob.yBodyRot = mob.getYRot();
            if (operation != MoveControl.Operation.MOVE_TO) {
                mob.setZza(0.0F);
            } else {
                operation = MoveControl.Operation.WAIT;
                if (mob.onGround()) {
                    mob.setSpeed((float)(speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (jumpDelay-- <= 0) {
                        jumpDelay = cheeseSlime.getJumpDelay();
                        if (isAggressive) {
                            jumpDelay /= 3;
                        }

                        cheeseSlime.getJumpControl().jump();
                        if (cheeseSlime.doPlayJumpSound()) {
                            cheeseSlime.playSound(cheeseSlime.getJumpSound(), cheeseSlime.getSoundVolume(), cheeseSlime.getSoundPitch());
                        }
                    } else {
                        cheeseSlime.xxa = 0.0F;
                        cheeseSlime.zza = 0.0F;
                        mob.setSpeed(0.0F);
                    }
                } else {
                    mob.setSpeed((float)(speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }
            }
        }
    }

    static class CheeseSlimeRandomDirectionGoal extends Goal {
        private final CheeseSlimeEntity cheeseSlime;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public CheeseSlimeRandomDirectionGoal(CheeseSlimeEntity cheeseSlime) {
            this.cheeseSlime = cheeseSlime;
            setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return cheeseSlime.getTarget() == null
                    && (cheeseSlime.onGround() || cheeseSlime.isInWater() || cheeseSlime.isInLava() || cheeseSlime.hasEffect(MobEffects.LEVITATION))
                    && cheeseSlime.getMoveControl() instanceof CheeseSlimeEntity.CheeseSlimeMoveControl;
        }

        @Override
        public void tick() {
            if (--nextRandomizeTime <= 0) {
                nextRandomizeTime = adjustedTickDelay(40 + cheeseSlime.getRandom().nextInt(60));
                chosenDegrees = cheeseSlime.getRandom().nextInt(360);
            }

            if (cheeseSlime.getMoveControl() instanceof CheeseSlimeEntity.CheeseSlimeMoveControl slimeMoveControl) {
                slimeMoveControl.setDirection(chosenDegrees, false);
            }
        }
    }
}
