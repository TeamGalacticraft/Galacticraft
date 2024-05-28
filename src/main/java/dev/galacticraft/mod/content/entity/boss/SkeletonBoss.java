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

package dev.galacticraft.mod.content.entity.boss;

import dev.galacticraft.api.entity.IgnoreShift;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCSounds;
import dev.galacticraft.mod.content.advancements.GCTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

public class SkeletonBoss extends AbstractBossEntity implements RangedAttackMob, IgnoreShift {

    protected long ticks = 0;
    private static final ItemStack defaultHeldItem = new ItemStack(Items.BOW, 1);

    public int throwTimer;
    public int postThrowDelay = 20;
    public Entity thrownEntity;
    public Entity targetEntity;

    public SkeletonBoss(EntityType<? extends SkeletonBoss> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 25, 10.0F));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, true));
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes();
    }

//    @Override
//    protected void applyEntityAttributes() {
//        super.applyEntityAttributes();
//        double difficulty = 0;
//        switch (this.level().getDifficulty()) {
//            case HARD:
//                difficulty = 2D;
//                break;
//            case NORMAL:
//                difficulty = 1D;
//                break;
//        }
//        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(150.0F * ConfigManagerCore.dungeonBossHealthMod);
//        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D + 0.075 * difficulty);
//    }

    @Override
    protected void tickDeath() {
        super.tickDeath();

        if (this.deathTime == 100) {
            level().playSound(null, getX(), getY(), getZ(), GCSounds.ENTITY_BOSSDEATH, SoundSource.HOSTILE, 10.0F, 1.5F);
        }
    }

    @Override
    public boolean isInWater() {
        return false;
    }

//    @Override
//    public boolean handleWaterMovement() {
//        return false;
//    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction positionUpdater) {
        Vec3 vec3 = this.getPassengerRidingPosition(passenger);
        final double offsetX = Math.sin(-this.getYHeadRot() / Constant.RADIANS_TO_DEGREES);
        final double offsetZ = Math.cos(this.getYHeadRot() / Constant.RADIANS_TO_DEGREES);
        final double offsetY = 2 * Math.cos((this.throwTimer + this.postThrowDelay) * 0.05F);

        positionUpdater.accept(passenger, vec3.x() + offsetX, vec3.y() + passenger.getMyRidingOffset(this) + offsetY, vec3.z() + offsetZ);
    }

    @Override
    public void knockback(double strength, double x, double z) {
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.isNoAi() && this.getPassengers().isEmpty() && this.postThrowDelay == 0 && this.throwTimer == 0 && player.equals(this.targetEntity) && this.deathTime == 0) {
            level().playSound(player, getX(), getY(), getZ(), GCSounds.ENTITY_BOSSLAUGH, SoundSource.HOSTILE, 10.0F, 0.2F);
            if (!this.level().isClientSide) {
                player.startRiding(this);
            }

            this.throwTimer = 40;
        }

        super.playerTouch(player);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        this.playSound(GCSounds.ENTITY_OOH, this.getSoundVolume(), this.getVoicePitch() + 1.0F);
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public ItemStack getHeldItem()
//    {
//        return EntitySkeletonBoss.defaultHeldItem;
//    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public void aiStep() {
        this.ticks++;

        for (Player player : level().players()) { // EntityGetter#hasNearbyAlivePlayer

            if (EntitySelector.NO_SPECTATORS.test(player) && EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(player) && player instanceof ServerPlayer serverPlayer) {
                double distance = player.distanceToSqr(getX(), getY(), getZ());

                if (distance < 20 * 20) {
                    GCTriggers.FIND_MOON_BOSS.trigger(serverPlayer);
                }
            }
        }

        if (!level().isClientSide && this.getHealth() <= 150.0F * Galacticraft.CONFIG.bossHealthMultiplier() / 2) {
            this.getAttribute(Attributes.MOVEMENT_SPEED);
        }

        final Player player = level().getNearestPlayer(getX(), getY(), getZ(), 20.0, false);

        if (player != null && !player.equals(this.targetEntity)) {
            if (this.distanceToSqr(player) < 400.0D) {
                this.getNavigation().createPath(player, 0);
                this.targetEntity = player;
            }
        } else {
            this.targetEntity = null;
        }

        if (this.throwTimer > 0) {
            this.throwTimer--;
        }

        if (this.postThrowDelay > 0) {
            this.postThrowDelay--;
        }

        if (!this.getPassengers().isEmpty() && this.throwTimer == 0) {
            this.postThrowDelay = 20;

            this.thrownEntity = this.getPassengers().get(0);

            if (!this.level().isClientSide) {
                this.ejectPassengers();
            }
        }

        if (this.thrownEntity != null && this.postThrowDelay == 18) {
            double x = this.getX() - this.thrownEntity.getX();
            double z;

            for (z = this.getZ() - this.thrownEntity.getZ(); x * x + z * z < 1.0E-4D; z = (Math.random() - Math.random()) * 0.01D) {
                x = (Math.random() - Math.random()) * 0.01D;
            }

            this.level().playSound(
                    null,
                    getX(),
                    getY(),
                    getZ(),
                    SoundEvents.ARROW_SHOOT,
                    SoundSource.HOSTILE,
                    10.0F,
                    0.2F
            );
            this.thrownEntity.animateHurt((float) Math.atan2(z, x) * Mth.RAD_TO_DEG - this.getYRot());

            ((Player) this.thrownEntity).knockback(2.4F, x, z);
        }

        super.aiStep();
    }

    @Override
    public ItemEntity spawnAtLocation(ItemStack stack, float yOffset) {
        final ItemEntity item = new ItemEntity(this.level(), this.getX(), this.getY() + yOffset, this.getZ(), stack);
        item.setDeltaMovement(new Vec3(0, -2.0D, 0));
        item.setDefaultPickUpDelay();
        this.level().addFreshEntity(item);
        return item;
    }

//    @Override
//    protected void dropFewItems(boolean b, int i) {
//        if (this.rand.nextInt(200) - i >= 5) {
//            return;
//        }
//
//        if (i > 0) {
//            final ItemStack var2 = new ItemStack(Items.BOW);
//            EnchantmentHelper.addRandomEnchantment(this.rand, var2, 5, false);
//            this.entityDropItem(var2, 0.0F);
//        } else {
//            this.dropItem(Items.BOW, 1);
//        }
//    }

//    @Override
//    public boolean canBreath() {
//        return true;
//    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        if (!this.getPassengers().isEmpty()) {
            return;
        }

        ItemStack arrowItem = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW)));
        AbstractArrow arrow = getArrow(arrowItem, pullProgress);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333) - arrow.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        arrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.level().getDifficulty().getId() * 4));

        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(arrow);
    }

    protected AbstractArrow getArrow(ItemStack arrow, float damageModifier) {
        return ProjectileUtil.getMobArrow(this, arrow, damageModifier);
    }

    @Override
    public boolean shouldIgnoreShiftExit() {
        return true;
    }

    @Override
    public ItemStack getGuaranteedLoot(Random rand) {
//        List<ItemStack> stackList = GalacticraftRegistry.getDungeonLoot(1);
        return null;//stackList.get(rand.nextInt(stackList.size())).copy();
    }

    @Override
    public int getChestTier() {
        return 1;
    }

    @Override
    public void dropKey() {
//        this.entityDropItem(new ItemStack(GCItems.key, 1, 0), 0.5F);
    }

    @Override
    public BossEvent.BossBarColor getHealthBarColor() {
        return BossEvent.BossBarColor.GREEN;
    }
}
