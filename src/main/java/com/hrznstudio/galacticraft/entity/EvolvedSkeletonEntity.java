/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.entity;

import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class EvolvedSkeletonEntity extends Skeleton {
    private final RangedBowAttackGoal<EvolvedSkeletonEntity> rangedAttackGoal;
    private final MeleeAttackGoal meleeCombatGoal;

    public EvolvedSkeletonEntity(EntityType<? extends Skeleton> entityType, Level world) {
        super(entityType, world);

        this.rangedAttackGoal = new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F);
        this.meleeCombatGoal = new MeleeAttackGoal(this, 1.2D, false) {
            public void stop() {
                super.stop();
                EvolvedSkeletonEntity.this.setAggressive(false);
            }

            public void start() {
                super.start();
                EvolvedSkeletonEntity.this.setAggressive(true);
            }
        };
        reassessWeaponGoal();
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(difficulty);
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.BOW));
    }

    @Override
    public void reassessWeaponGoal() {
        if (this.level != null && !this.level.isClientSide && rangedAttackGoal != null) {
            this.goalSelector.removeGoal(this.meleeCombatGoal);
            this.goalSelector.removeGoal(this.rangedAttackGoal);
            ItemStack main = this.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack off = this.getItemInHand(InteractionHand.OFF_HAND);
            if (main.getItem() == Items.BOW || off.getItem() == Items.BOW) {
                int i = 35 - (main.getItem() == Items.BOW ? 10 : 0) - (off.getItem() == Items.BOW ? 10 : 0);
                if (this.level.getDifficulty() != Difficulty.HARD) {
                    i = 25;
                }

                this.rangedAttackGoal.setMinAttackInterval(i);
                this.goalSelector.addGoal(4, this.rangedAttackGoal);
            } else {
                this.goalSelector.addGoal(4, this.meleeCombatGoal);
            }

        }
    }
}
