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
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.block.special.TinLadderBlock;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import dev.galacticraft.mod.tag.GCDimensionTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
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
abstract class LivingEntityMixin extends Entity implements CryogenicAccessor {
    @SuppressWarnings("ConstantConditions")
    private LivingEntityMixin() {
        super(null, null);
    }

    @Shadow
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<BlockPos> lastClimbablePos;

    @Unique
    @SuppressWarnings("WrongEntityDataParameterClass")
    private static final EntityDataAccessor<Boolean> IS_IN_CRYO_SLEEP_ID = SynchedEntityData.defineId(
            LivingEntity.class, EntityDataSerializers.BOOLEAN
    );

    @Shadow
    public abstract void setYHeadRot(float f);

    @Unique
    public int galacticraft$cryogenicChamberCooldown;

    @Override
    public void galacticraft$beginCryoSleep() {
        this.entityData.set(IS_IN_CRYO_SLEEP_ID, true);
    }

    @Override
    public void galacticraft$endCryoSleep() {
        this.entityData.set(IS_IN_CRYO_SLEEP_ID, false);
    }

    @Override
    public boolean galacticraft$isInCryoSleep() {
        return this.entityData.get(IS_IN_CRYO_SLEEP_ID);
    }

    @Override
    public int galacticraft$getCryogenicChamberCooldown() {
        return this.galacticraft$cryogenicChamberCooldown;
    }

    @Override
    public void galacticraft$setCryogenicChamberCooldown(int cryogenicChamberCooldown) {
        this.galacticraft$cryogenicChamberCooldown = cryogenicChamberCooldown;
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void galacticraft$readCryoData(CompoundTag tag, CallbackInfo ci) {
        this.galacticraft$cryogenicChamberCooldown = tag.getInt(Constant.Nbt.CRYOGENIC_COOLDOWN);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void galacticraft$addCryoData(CompoundTag tag, CallbackInfo ci) {
        tag.putInt(Constant.Nbt.CRYOGENIC_COOLDOWN, this.galacticraft$cryogenicChamberCooldown);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void galacticraft$tickCryo(CallbackInfo ci) {
        if (galacticraft$getCryogenicChamberCooldown() > 0) {
            galacticraft$setCryogenicChamberCooldown(galacticraft$getCryogenicChamberCooldown() - 1);
        }
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void galacticraft$sleepData(SynchedEntityData.Builder compositeStateBuilder, CallbackInfo ci) {
        compositeStateBuilder.define(IS_IN_CRYO_SLEEP_ID, false);
    }

    @Inject(method = "setPosToBed", at = @At("HEAD"), cancellable = true)
    private void galacticraft$setCryoSleepPos(BlockPos blockPos, CallbackInfo ci) {
        if (galacticraft$isInCryoSleep()) {
            Vec3 pos = blockPos.below().getBottomCenter();
            this.setPos(pos.x, pos.y, pos.z);
            this.setDeltaMovement(Vec3.ZERO);
            this.hasImpulse = false;
            ci.cancel();
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void galacticraft$preventMovement(CallbackInfo ci) {
        if (galacticraft$isInCryoSleep())
            ci.cancel();
    }

    @ModifyReturnValue(method = "getAttributeValue", at = @At(value = "RETURN"))
    private double galacticraft$adjustSafeFallDistance(double original, Holder<Attribute> attribute) {
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
    private double galacticraft$adjustFallDamage(double original) {
        Holder<CelestialBody<?, ?>> holder = this.level().galacticraft$getCelestialBody();
        return holder != null ? original * holder.value().gravity() : original;
    }

    @ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isFallFlying()Z"))
    private boolean galacticraft$canStartFallFlying(boolean original) {
        return original && !this.level().galacticraft$hasDimensionTypeTag(GCDimensionTypeTags.VACUUM);
    }

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 6), index = 0)
    private Vec3 galacticraft$adjustAtmosphericDrag(Vec3 original) {
        Holder<CelestialBody<?, ?>> holder = this.level().galacticraft$getCelestialBody();
        if (holder != null) {
            float drag = holder.value().atmosphere().pressure() / 2000.0F;
            return original.multiply(1.0F - drag, 1.0F - drag * 2.0F, 1.0F - drag);
        }
        return original;
    }

    @WrapOperation(method = "triggerItemUseEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;spawnItemParticles(Lnet/minecraft/world/item/ItemStack;I)V"))
    private void galacticraft$cannedFoodParticles(LivingEntity entity, ItemStack itemStack, int n, Operation<Void> original) {
        if (CannedFoodItem.isCannedFoodItem(itemStack) && entity instanceof Player player) {
            List<ItemStack> items = CannedFoodItem.getItemsToBeConsumed(itemStack, player);
            if (!items.isEmpty()) {
                double scale = n * Mth.invSqrt(items.stream().mapToInt(ItemStack::getCount).sum());
                for (ItemStack stack : items) {
                    original.call(entity, stack, (int) Math.ceil(scale * stack.getCount()));
                }
                return;
            }
        }
        original.call(entity, itemStack, n);
    }

    @Inject(method = "onClimbable", at = @At("TAIL"), cancellable = true)
    private void galacticraft$onClimbable(CallbackInfoReturnable<Boolean> cir) {
        // Make the back side of tin ladders climbable
        BlockPos blockPos = this.blockPosition();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState blockState = this.level().getBlockState(blockPos.relative(direction));
            if (blockState.is(GCBlocks.TIN_LADDER) && blockState.getValue(TinLadderBlock.FACING) == direction) {
                this.lastClimbablePos = Optional.of(blockPos);
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
