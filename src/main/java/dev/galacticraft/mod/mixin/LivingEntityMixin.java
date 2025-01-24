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

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.CryogenicAccessor;
import dev.galacticraft.mod.tag.GCTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements CryogenicAccessor {
    @Shadow public abstract double getAttributeValue(Holder<Attribute> holder);

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
    public void beginCryoSleep() {
        this.entityData.set(IS_IN_CRYO_SLEEP_ID, true);
    }

    @Override
    public void endCryoSleep() {
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
    private void gc$sleepData(SynchedEntityData.Builder compositeStateBuilder, CallbackInfo ci) {
        compositeStateBuilder.define(IS_IN_CRYO_SLEEP_ID, false);
    }

    @Inject(method = "setPosToBed", at = @At("HEAD"), cancellable = true)
    private void gc$setCryoSleepPos(BlockPos blockPos, CallbackInfo ci) {
        if (isInCryoSleep()) {
            Vec3 pos = blockPos.getBottomCenter();
            this.setPos(pos.x, pos.y + 1F, pos.z);
            ci.cancel();
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void gc$preventMovement(CallbackInfo ci) {
        if (isInCryoSleep())
            ci.cancel();
    }

    @Inject(method = "calculateFallDamage", at = @At(value = "HEAD"), cancellable = true)
    protected void calculateFallDamage(float f, float f2, CallbackInfoReturnable<Integer> cir) {
        if (this.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE)) {
            cir.setReturnValue(0);
            return;
        }
        Holder<CelestialBody<?, ?>> holder = this.level().galacticraft$getCelestialBody();
        float gravity = (float)(holder != null ? holder.value().gravity() : 1.0d);
        float f3 = (float)this.getAttributeValue(Attributes.SAFE_FALL_DISTANCE);
        float f4 = f - f3 / gravity;
        cir.setReturnValue(Mth.ceil((double)(f4 * f2 * gravity) * this.getAttributeValue(Attributes.FALL_DAMAGE_MULTIPLIER)));
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isFallFlying()Z"))
    private boolean gc$canStartFallFlying(boolean original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.level().galacticraft$hasDimensionTypeTag(GCTags.VACUUM)) {
            return false;
        }
        return original;
    }
}
