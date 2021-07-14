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

import dev.galacticraft.mod.api.entity.attribute.GalacticraftEntityAttribute;
import dev.galacticraft.mod.village.GalacticraftVillagerProfession;
import dev.galacticraft.mod.village.MoonVillagerType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MoonVillagerEntity extends VillagerEntity {
    public MoonVillagerEntity(EntityType<? extends MoonVillagerEntity> entityType, World world) {
        this(entityType, world, MoonVillagerType.MOON_HIGHLANDS);
    }

    public MoonVillagerEntity(EntityType<? extends MoonVillagerEntity> entityType, World world, VillagerType type) {
        super(entityType, world, type);
        createLivingAttributes();
        setHealth(20.0F);
    }

    @Override
    public void setVillagerData(VillagerData villagerData) {
        if (villagerData != null && GalacticraftVillagerProfession.MOON_VILLAGER_PROFESSION_REGISTRY.getId(villagerData.getProfession()) != null) {
            super.setVillagerData(villagerData);
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
    public MoonVillagerEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        double d = this.random.nextDouble();
        VillagerType villagerType3;
        if (d < 0.5D) {
            villagerType3 = VillagerType.forBiome(serverWorld.getBiomeKey(this.getBlockPos()));
        } else if (d < 0.75D) {
            villagerType3 = this.getVillagerData().getType();
        } else {
            villagerType3 = ((VillagerEntity) passiveEntity).getVillagerData().getType();
        }

        MoonVillagerEntity moonVillager = new MoonVillagerEntity(GalacticraftEntityType.MOON_VILLAGER, serverWorld, villagerType3);
        moonVillager.initialize(serverWorld, serverWorld.getLocalDifficulty(moonVillager.getBlockPos()), SpawnReason.BREEDING, null, null);
        return moonVillager;
    }

    public static DefaultAttributeContainer.Builder createMoonVillagerAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5D).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0D).add(GalacticraftEntityAttribute.CAN_BREATHE_IN_SPACE, 1.0D);
    }
}