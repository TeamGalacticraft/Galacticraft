/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.entity;

import dev.galacticraft.mod.mixin.AbstractSkeletonEntityAccessor;
import dev.galacticraft.mod.tag.GalacticraftTag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class EvolvedSkeletonEntity extends SkeletonEntity {
    public EvolvedSkeletonEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initEquipment(LocalDifficulty difficulty) {
        super.initEquipment(difficulty);
        this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(Items.BOW));
    }

    @Override
    public void updateAttackType() {
        BowAttackGoal<?> bowAttackGoal = ((AbstractSkeletonEntityAccessor) this).getBowAttackGoal();
        MeleeAttackGoal meleeAttackGoal = ((AbstractSkeletonEntityAccessor) this).getMeleeAttackGoal();
        if (this.world != null && !this.world.isClient && bowAttackGoal != null) {
            this.goalSelector.remove(meleeAttackGoal);
            this.goalSelector.remove(bowAttackGoal);
            ItemStack main = this.getStackInHand(Hand.MAIN_HAND);
            ItemStack off = this.getStackInHand(Hand.OFF_HAND);
            if (main.getItem() == Items.BOW || off.getItem() == Items.BOW) {
                int i = 35 - (main.getItem() == Items.BOW ? 10 : 0) - (off.getItem() == Items.BOW ? 10 : 0);
                if (this.world.getDifficulty() != Difficulty.HARD) {
                    i = 25;
                }

                bowAttackGoal.setAttackInterval(i);
                this.goalSelector.add(4, bowAttackGoal);
            } else {
                this.goalSelector.add(4, meleeAttackGoal);
            }
        }
    }

    @Override
    protected boolean isAffectedByDaylight() {
        return super.isAffectedByDaylight() && GalacticraftTag.MOON_MARE.contains(this.world.getBiome(this.getBlockPos()));
    }
}
