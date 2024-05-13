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

import dev.galacticraft.mod.mixin.AbstractSkeletonEntityAccessor;
import dev.galacticraft.mod.world.biome.GCBiomes;
import net.minecraft.util.RandomSource;
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

public class EvolvedSkeletonEntity extends Skeleton {
    public EvolvedSkeletonEntity(EntityType<? extends Skeleton> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.BOW));
    }

    @Override
    public void reassessWeaponGoal() {
        RangedBowAttackGoal<?> bowAttackGoal = ((AbstractSkeletonEntityAccessor) this).getBowAttackGoal();
        MeleeAttackGoal meleeAttackGoal = ((AbstractSkeletonEntityAccessor) this).getMeleeAttackGoal();
        if (this.level() != null && !this.level().isClientSide && bowAttackGoal != null) {
            this.goalSelector.removeGoal(meleeAttackGoal);
            this.goalSelector.removeGoal(bowAttackGoal);
            ItemStack main = this.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack off = this.getItemInHand(InteractionHand.OFF_HAND);
            if (main.getItem() == Items.BOW || off.getItem() == Items.BOW) {
                int i = 35 - (main.getItem() == Items.BOW ? 10 : 0) - (off.getItem() == Items.BOW ? 10 : 0);
                if (this.level().getDifficulty() != Difficulty.HARD) {
                    i = 25;
                }

                bowAttackGoal.setMinAttackInterval(i);
                this.goalSelector.addGoal(4, bowAttackGoal);
            } else {
                this.goalSelector.addGoal(4, meleeAttackGoal);
            }
        }
    }

    @Override
    protected boolean isSunBurnTick() {
        return super.isSunBurnTick() && this.level().getBiome(this.blockPosition()).is(GCBiomes.Moon.BASALTIC_MARE);
    }
}
