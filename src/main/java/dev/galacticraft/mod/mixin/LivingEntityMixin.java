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

package dev.galacticraft.mod.mixin;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.CryogenicAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements CryogenicAccessor {
    @Unique
    @SuppressWarnings("WrongEntityDataParameterClass")
    private static final EntityDataAccessor<Boolean> IS_IN_CRYO_SLEEP_ID = SynchedEntityData.defineId(
            LivingEntity.class, EntityDataSerializers.BOOLEAN
    );

    @Shadow public abstract void setYHeadRot(float f);

    @Unique public int cryogenicChamberCooldown;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void beginCyroSleep() {
        this.entityData.set(IS_IN_CRYO_SLEEP_ID, true);
    }

    @Override
    public void endCyroSleep() {
        this.entityData.set(IS_IN_CRYO_SLEEP_ID, false);
    }

    @Override
    public boolean isInCryoSleep() {
        return this.entityData.get(IS_IN_CRYO_SLEEP_ID);
    }

    @Override
    public int getCryogenicChamberCooldown() {
        return this.cryogenicChamberCooldown;
    }

    @Override
    public void setCryogenicChamberCooldown(int cryogenicChamberCooldown) {
        this.cryogenicChamberCooldown = cryogenicChamberCooldown;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void gc$readCryoData(CompoundTag tag, CallbackInfo ci) {
        this.cryogenicChamberCooldown = tag.getInt(Constant.Nbt.CRYOGENIC_COOLDOWN);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void gc$addCryoData(CompoundTag tag, CallbackInfo ci) {
        tag.putInt(Constant.Nbt.CRYOGENIC_COOLDOWN, this.cryogenicChamberCooldown);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void gc$tickCryo(CallbackInfo ci) {
        if (getCryogenicChamberCooldown() > 0) {
            setCryogenicChamberCooldown(getCryogenicChamberCooldown() - 1);
        }
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void gc$sleepData(CallbackInfo ci) {
        this.entityData.define(IS_IN_CRYO_SLEEP_ID, false);
    }

    @Inject(method = "setPosToBed", at = @At("HEAD"), cancellable = true)
    private void gc$setCryoSleepPos(BlockPos blockPos, CallbackInfo ci) {
        if (isInCryoSleep()) {
            this.setPos(blockPos.getX(), blockPos.getY() + 1F, blockPos.getZ());
            ci.cancel();
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void gc$preventMovement(CallbackInfo ci) {
        if (isInCryoSleep())
            ci.cancel();
    }
}
