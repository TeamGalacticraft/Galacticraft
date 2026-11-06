/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.CryogenicAccessor;
import dev.galacticraft.mod.accessor.EntityFluidAccessor;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.special.TinLadderBlock;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import dev.galacticraft.mod.tag.GCDimensionTypeTags;
import dev.galacticraft.mod.tag.GCFluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements CryogenicAccessor {
    @Shadow public abstract double getAttributeValue(Holder<Attribute> holder);

    @Unique
    @SuppressWarnings("WrongEntityDataParameterClass")
    private static final EntityDataAccessor<Boolean> IS_IN_CRYO_SLEEP_ID = SynchedEntityData.defineId(
            LivingEntity.class, EntityDataSerializers.BOOLEAN
    );

    @Shadow
    public abstract void setYHeadRot(float f);

    @Shadow
    public abstract boolean isAffectedByPotions();

    @Unique
    public int cryogenicChamberCooldown;

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
            Vec3 pos = blockPos.below().getBottomCenter();
            this.setPos(pos.x, pos.y, pos.z);
            this.setDeltaMovement(Vec3.ZERO);
            this.hasImpulse = false;
            ci.cancel();
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void gc$preventMovement(CallbackInfo ci) {
        if (isInCryoSleep())
            ci.cancel();
    }

    @ModifyReturnValue(method = "getAttributeValue", at = @At(value = "RETURN"))
    private double gc$adjustSafeFallDistance(double original, Holder<Attribute> attribute) {
        if (attribute == Attributes.SAFE_FALL_DISTANCE) {
            Holder<CelestialBody<?, ?>> holder = this.level().galacticraft$getCelestialBody();
            if (holder != null) {
                double gravity = holder.value().gravity();
                return gravity > 0 ? original / gravity : original;
            }
        }
        return original;
    }

    @ModifyArg(method = "calculateFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;ceil(D)I"), index = 0)
    protected double gc$adjustFallDamage(double original) {
        Holder<CelestialBody<?, ?>> holder = this.level().galacticraft$getCelestialBody();
        return holder != null ? original * holder.value().gravity() : original;
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isFallFlying()Z"))
    private boolean gc$canStartFallFlying(boolean original) {
        return this.level().galacticraft$hasDimensionTypeTag(GCDimensionTypeTags.VACUUM) ? false : original;
    }

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 6), index = 0)
    private Vec3 gc$adjustAtmosphericDrag(Vec3 original) {
        Holder<CelestialBody<?, ?>> holder = this.level().galacticraft$getCelestialBody();
        if (holder != null) {
            float drag = holder.value().atmosphere().pressure() / 2000.0F;
            return original.multiply(1.0F - drag, 1.0F - drag * 2.0F, 1.0F - drag);
        }
        return original;
    }

    @WrapOperation(method = "triggerItemUseEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;spawnItemParticles(Lnet/minecraft/world/item/ItemStack;I)V"))
    private void gc$cannedFoodParticles(LivingEntity entity, ItemStack itemStack, int n, Operation<Void> original) {
        if (CannedFoodItem.isCannedFoodItem(itemStack) && entity instanceof Player player) {
            List<ItemStack> items = CannedFoodItem.getItemsToBeConsumed(itemStack, player);
            if (!items.isEmpty()) {
                double scale = n * Mth.invSqrt(items.stream().mapToInt(stack -> stack.getCount()).sum());
                for (ItemStack stack : items) {
                    original.call(entity, stack, (int) Math.ceil(scale * stack.getCount()));
                }
                return;
            }
        }
        original.call(entity, itemStack, n);
    }

    @Inject(method = "onClimbable", at = @At("TAIL"), cancellable = true)
    private void gc$onClimbable(CallbackInfoReturnable<Boolean> cir) {
        // Make the back side of tin ladders climbable
        BlockPos blockPos = this.blockPosition();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState blockState = this.level().getBlockState(blockPos.relative(direction));
            if (blockState.is(GCBlocks.TIN_LADDER) && blockState.getValue(TinLadderBlock.FACING) == direction) {
                ((LivingEntityAccessor) this).setLastClimbablePos(Optional.of(blockPos));
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Shadow
    protected abstract boolean isAffectedByFluids();

    @Shadow
    public abstract void calculateEntityAnimation(boolean flutter);

    @Shadow
    protected abstract float getWaterSlowDown();

    @Shadow
    public abstract float getSpeed();

    @Shadow
    public abstract boolean onClimbable();

    @Shadow
    public abstract Vec3 getFluidFallingAdjustedMovement(double gravity, boolean falling, Vec3 motion);

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void gc$fluidTravel(Vec3 movementInput, CallbackInfo ci) {
        EntityFluidAccessor fluidAccessor = (EntityFluidAccessor) this;
        if (!this.isControlledByLocalInstance() || !this.isAffectedByFluids() || !fluidAccessor.gc$getWasTouchingGCFluid()) {
            return;
        }
        TagKey<Fluid> fluidType = fluidAccessor.gc$getInFluidType();
        double d = this.getGravity();
        boolean bl = this.getDeltaMovement().y <= 0.0;
        double e = this.getY();
        float f = 0.8F;
        float g = 0.02F;

        if (GCFluidTags.WATER_BASED.contains(fluidType)) {
            f = this.isSprinting() ? 0.9F : this.getWaterSlowDown();
            float h = (float) this.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);
            if (!this.onGround()) {
                h *= 0.5F;
            }

            if (h > 0.0F) {
                f += (0.54600006F - f) * h;
                g += (this.getSpeed() - g) * h;
            }
        }

        this.moveRelative(g, movementInput);
        this.move(MoverType.SELF, this.getDeltaMovement());
        Vec3 vec32 = this.getDeltaMovement();

        if (GCFluidTags.THICK.contains(fluidType)) {
            if (this.getFluidHeight(fluidType) <= this.getFluidJumpThreshold()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply((double) 0.5F, (double) 0.8F, (double) 0.5F));
                vec32 = this.getFluidFallingAdjustedMovement(d, bl, this.getDeltaMovement());
                this.setDeltaMovement(vec32);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale((double) 0.5F));
            }
            if (d != (double)0.0F) {
                this.setDeltaMovement(this.getDeltaMovement().add((double)0.0F, -d / (double)4.0F, (double)0.0F));
            }
            vec32 = this.getDeltaMovement();

        } else {
            Vec3 vec3 = vec32;
            if (this.horizontalCollision && this.onClimbable()) {
                vec3 = new Vec3(vec3.x, 0.2, vec3.z);
            }
            this.setDeltaMovement(vec3.multiply((double) f, (double) 0.8F, (double) f));
            vec32 = this.getFluidFallingAdjustedMovement(d, bl, this.getDeltaMovement());
            this.setDeltaMovement(vec32);

        }

        if (this.horizontalCollision && this.isFree(vec32.x, vec32.y + (double) 0.6F - this.getY() + e, vec32.z)) {
            this.setDeltaMovement(vec32.x, (double) 0.3F, vec32.z);
        }

        this.calculateEntityAnimation(this instanceof FlyingAnimal);
        ci.cancel();
    }
}
