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

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.entity.attribute.GalacticraftEntityAttributes;
import com.hrznstudio.galacticraft.village.MoonVillagerType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonVillagerEntity extends Villager {
    public MoonVillagerEntity(EntityType<? extends MoonVillagerEntity> entityType, Level world) {
        this(entityType, world, MoonVillagerType.MOON_HIGHLANDS);
    }

    public MoonVillagerEntity(EntityType<? extends MoonVillagerEntity> entityType, Level world, VillagerType type) {
        super(entityType, world, type);
        createLivingAttributes();
        setHealth(20.0F);
    }

    @Override
    public void setVillagerData(VillagerData villagerData) {
        if (villagerData != null && Galacticraft.MOON_VILLAGER_PROFESSION_REGISTRY.getKey(villagerData.getProfession()) != null) {
            super.setVillagerData(villagerData);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return super.getDeathSound();
    }

    @Override
    protected SoundEvent getDrinkingSound(ItemStack stack) {
        return super.getDrinkingSound(stack);
    }

    @Override
    protected SoundEvent getFallDamageSound(int distance) {
        return super.getFallDamageSound(distance);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return super.getHurtSound(source);
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean sold) {
        return super.getTradeUpdatedSound(sold);
    }

    @Override
    public SoundEvent getEatingSound(ItemStack stack) {
        return super.getEatingSound(stack);
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return super.getNotifyTradeSound();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return super.getAmbientSound();
    }

    @Override
    public MoonVillagerEntity getBreedOffspring(ServerLevel serverWorld, AgableMob passiveEntity) {
        double d = this.random.nextDouble();
        VillagerType villagerType3;
        if (d < 0.5D) {
            villagerType3 = VillagerType.byBiome(serverWorld.getBiomeName(this.blockPosition()));
        } else if (d < 0.75D) {
            villagerType3 = this.getVillagerData().getType();
        } else {
            villagerType3 = ((Villager) passiveEntity).getVillagerData().getType();
        }

        MoonVillagerEntity moonVillager = new MoonVillagerEntity(GalacticraftEntityTypes.MOON_VILLAGER, serverWorld, villagerType3);
        moonVillager.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(moonVillager.blockPosition()), MobSpawnType.BREEDING, null, null);
        return moonVillager;
    }

    public static AttributeSupplier.Builder createMoonVillagerAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5D).add(Attributes.FOLLOW_RANGE, 48.0D).add(GalacticraftEntityAttributes.CAN_BREATHE_IN_SPACE, 1.0D);
    }
}