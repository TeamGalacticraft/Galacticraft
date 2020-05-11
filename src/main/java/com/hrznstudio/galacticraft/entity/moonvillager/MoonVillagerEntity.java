/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.entity.moonvillager;

import com.hrznstudio.galacticraft.api.entity.EvolvedEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashSet;


/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonVillagerEntity extends VillagerEntity implements EvolvedEntity {

    /**
     * TO DO:
     * - Turn into Evolved Witch when hit by lightning
     * - Figure out how to die / lose health without crashing
     * - Set up activities
     * - Trading (wait until 1.15)
     * - Villages
     */

    public MoonVillagerEntity(EntityType<? extends MoonVillagerEntity> entityType, World world) {
        super(entityType, world);
        this.setHealth(20);
        this.initGoals();
        this.setCanPickUpLoot(true);
        this.brain.setDefaultActivity(Activity.CORE);
        HashSet<Activity> otherActivities = new HashSet<>();
        otherActivities.add(Activity.WORK);
        this.brain.setCoreActivities(otherActivities);
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25d);
    }

    @Override
    public boolean canGather(ItemStack itemIn) {
        return itemIn.getItem() == Items.BREAD || itemIn.getItem() == Items.POTATO || itemIn.getItem() == Items.CARROT || itemIn.getItem() == Items.WHEAT || itemIn.getItem() == Items.WHEAT_SEEDS;
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return null;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        // might need this at some point
        super.onDeath(damageSource);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        //return super.damage(source, amount);
        this.remove();
        return false;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public Arm getMainArm() {
        return null;
    }

    @Nullable
    @Override
    public MoonVillagerEntity createChild(PassiveEntity mate) {
        return null;
    }
}
