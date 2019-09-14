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

import com.hrznstudio.galacticraft.api.space.RocketEntity;
import com.hrznstudio.galacticraft.api.space.RocketTier;
import com.hrznstudio.galacticraft.misc.RocketTiers;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class T1RocketEntity extends Entity implements RocketEntity {
    private int fuel;

    public T1RocketEntity(EntityType<T1RocketEntity> type, World world_1) {
        super(type, world_1);
    }

    @Override
    protected void initDataTracker() {
//        dataTracker.startTracking();
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        this.fuel = Math.max(tag.getInt("Fuel"), 0);
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        tag.putInt("Fuel", Math.max(this.fuel, 0));
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public void handleFallDamage(float float_1, float float_2) {
        super.handleFallDamage(float_1, float_2);
    }

    @Override
    public void tick() {
//        System.out.println("TICK! " + this.y);
//        double velY = Math.min(1, this.getVelocity().y + 1);

//        this.setVelocity(this.getVelocity().x, velY, this.getVelocity().z);
//        this.velocityModified = true;
//        this.velocityDirty = true;
        this.setPosition(this.x, this.y + 1, this.z);

        if (this.y >= 15) {
            this.world.createExplosion(this, this.x, this.y + (double) (this.getHeight() / 16.0F), this.z, 2.0F, Explosion.DestructionType.BREAK);
            this.remove();
        }
        super.tick();
    }

    @Override
    public RocketTier getRocketTier() {
        return RocketTiers.tierOne;
    }

    @Override
    public int getFuel() {
        return this.fuel;
    }
}