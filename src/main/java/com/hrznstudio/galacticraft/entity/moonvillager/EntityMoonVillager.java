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

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.world.World;
import java.util.HashSet;


/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class EntityMoonVillager extends VillagerEntity {

    /**
     * TO DO:
     * - Make model more similar to original (large cranium, smaller nose)
     * - Figure out how to die / lose health without crashing
     * - Set up activities
     * - Trading (wait until 1.15)
     * - Turn into Evolved Witch when hit by lightning
     * - Villages
     */

    public EntityMoonVillager(EntityType<? extends EntityMoonVillager> entityType, World world) {
        super(entityType, world);
        this.setHealth(20);
        this.initGoals();
        this.setCanPickUpLoot(true);
        this.brain.setDefaultActivity(Activity.CORE);
        HashSet<Activity> otherActivities = new HashSet<>();
        otherActivities.add(Activity.WORK);
        this.brain.setCoreActivities(otherActivities);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5d);
    }

    @Override
    public boolean canGather(Item itemIn) {
        return itemIn == Items.BREAD || itemIn == Items.POTATO || itemIn == Items.CARROT || itemIn == Items.WHEAT || itemIn == Items.WHEAT_SEEDS;
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
        return super.damage(source, amount);
        //this.remove();
        //return false;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setEquippedStack(EquipmentSlot equipmentSlot, ItemStack itemStack) {
    }

    @Override
    public Arm getMainArm() {
        return null;
    }
}
