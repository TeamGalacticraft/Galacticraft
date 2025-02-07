/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import dev.galacticraft.mod.Constant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

public class EvolvedCreeperEntity extends Creeper {
    private static final EntityDataAccessor<Boolean> BABY = SynchedEntityData.defineId(EvolvedCreeperEntity.class, EntityDataSerializers.BOOLEAN);
    private static final ResourceLocation SPEED_MODIFIER_BABY_ID = ResourceLocation.withDefaultNamespace("baby");
    private static final AttributeModifier BABY_SPEED_BONUS = new AttributeModifier(
            SPEED_MODIFIER_BABY_ID, 0.8D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
    );

    public EvolvedCreeperEntity(EntityType<? extends Creeper> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder compositeStateBuilder) {
        super.defineSynchedData(compositeStateBuilder);
        compositeStateBuilder.define(BABY, false);
    }

    public void tick() {
        super.tick();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        this.setBaby(tag.getBoolean(Constant.Nbt.BABY));
        tag.putByte("ExplosionRadius", (byte) (this.isBaby() ? 2 : 4)); //overwrite
        tag.putShort("Fuse", (short) 37); //overwrite
        super.readAdditionalSaveData(tag);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(Constant.Nbt.BABY, isBaby());
        tag.putByte("ExplosionRadius", (byte) (this.isBaby() ? 2 : 4));
        tag.putShort("Fuse", (short) 37); //overwrite
    }

    @Override
    public boolean isBaby() {
        return this.entityData.get(BABY);
    }

    @Override
    public void setBaby(boolean baby) {
        this.getEntityData().set(BABY, baby);
        if (!this.level().isClientSide) {
            AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            attributeInstance.removeModifier(SPEED_MODIFIER_BABY_ID);
            if (baby) {
                attributeInstance.addTransientModifier(BABY_SPEED_BONUS);
            }
        }
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        if (this.isBaby()) {
            return this.getType().getDimensions().scale(0.75F, 0.5F).withEyeHeight(0.75F);
        } else {
            return this.getType().getDimensions();
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (BABY.equals(data)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(data);
    }
}
