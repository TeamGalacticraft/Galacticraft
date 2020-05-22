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

package com.hrznstudio.galacticraft.entity;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.entity.EvolvedEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;

import javax.annotation.Nullable;


/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonVillagerEntity extends VillagerEntity implements EvolvedEntity {
    public static final EntityType.EntityFactory<MoonVillagerEntity> FACTORY = MoonVillagerEntity::new;
    public static final VillagerType MOON_VILLAGER_TYPE = new VillagerType() {
        @Override
        public String toString() {
            return "Galacticraft: Rewoven - Moon Villager";
        }
    };

    public MoonVillagerEntity(EntityType<? extends MoonVillagerEntity> entityType, World world) {
        this(entityType, world, MOON_VILLAGER_TYPE);
    }

    public MoonVillagerEntity(EntityType<? extends MoonVillagerEntity> entityType, World world, VillagerType type) {
        super(entityType, world, type);
        createLivingAttributes();
        assert Galacticraft.MOON_VILLAGER_TYPE_REGISTRY.getId(type) != null;
        setHealth(20.0F);

    }

    @Override
    public void setVillagerData(VillagerData villagerData) {
        if (villagerData != null && Galacticraft.MOON_VILLAGER_PROFESSION_REGISTRY.getId(villagerData.getProfession()) != null
        && Galacticraft.MOON_VILLAGER_TYPE_REGISTRY.getId(villagerData.getType()) != null) {
            super.setVillagerData(villagerData);
        } else {
            Galacticraft.logger.warn("Invaild profession or type for moon villager");
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return super.getDeathSound();
    }

    @Override
    protected SoundEvent getDrinkSound(ItemStack stack) {
        return super.getDrinkSound(stack);
    }

    @Override
    protected SoundEvent getFallSound(int distance) {
        return super.getFallSound(distance);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return super.getHurtSound(source);
    }

    @Override
    protected SoundEvent getTradingSound(boolean sold) {
        return super.getTradingSound(sold);
    }

    @Override
    public SoundEvent getEatSound(ItemStack stack) {
        return super.getEatSound(stack);
    }

    @Override
    public SoundEvent getYesSound() {
        return super.getYesSound();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return super.getAmbientSound();
    }

    @Override
    public MoonVillagerEntity createChild(PassiveEntity passiveEntity) {
        double d = this.random.nextDouble();
        VillagerType villagerType3;
        if (d < 0.5D) {
            villagerType3 = VillagerType.forBiome(this.world.getBiome(this.getBlockPos()));
        } else if (d < 0.75D) {
            villagerType3 = this.getVillagerData().getType();
        } else {
            villagerType3 = ((MoonVillagerEntity)passiveEntity).getVillagerData().getType();
        }
        if (Galacticraft.MOON_VILLAGER_TYPE_REGISTRY.getId(villagerType3) == null) {
            if (this.random.nextBoolean() && Galacticraft.MOON_VILLAGER_TYPE_REGISTRY.getId(this.getVillagerData().getType()) != null) {
                villagerType3 = this.getVillagerData().getType();
            } else {
                villagerType3 = MOON_VILLAGER_TYPE;
            }
        }

        MoonVillagerEntity villagerEntity = new MoonVillagerEntity(GalacticraftEntityTypes.MOON_VILLAGER, this.world, villagerType3);
        villagerEntity.initialize(this.world, this.world.getLocalDifficulty(villagerEntity.getBlockPos()), SpawnReason.BREEDING, null, null);
        return villagerEntity;
    }

    public static DefaultAttributeContainer.Builder createMoonVillagerAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5D).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0D);
    }

    static {
        Registry.register(Galacticraft.MOON_VILLAGER_TYPE_REGISTRY, new Identifier(Constants.MOD_ID, "moon_villager"), Registry.register(Registry.VILLAGER_TYPE, new Identifier(Constants.MOD_ID, "moon_villager"), MOON_VILLAGER_TYPE));
        Registry.register(Galacticraft.MOON_VILLAGER_PROFESSION_REGISTRY, new Identifier("none"), VillagerProfession.NONE);
        Registry.register(Galacticraft.MOON_VILLAGER_PROFESSION_REGISTRY, new Identifier("nitwit"), VillagerProfession.NITWIT);
    }
}