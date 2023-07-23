/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import com.mojang.datafixers.util.Either;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.CryogenicAccessor;
import dev.galacticraft.mod.content.block.special.CryogenicChamberBlock;
import dev.galacticraft.mod.content.entity.data.GCEntityDataSerializers;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements CryogenicAccessor {

    @Shadow
    int sleepCounter;

    @Unique
    private int cryogenicChamberCooldown;

    PlayerMixin() {
        super(null, null);
    }

    @Override
    public boolean isInCryoSleep() {
        return this.entityData.get(GCEntityDataSerializers.IS_IN_CRYO_SLEEP_ID);
    }

    @Override
    public int getCryogenicChamberCooldown() {
        return this.cryogenicChamberCooldown;
    }

    @Override
    public void setCryogenicChamberCooldown(int cryogenicChamberCooldown) {
        this.cryogenicChamberCooldown = cryogenicChamberCooldown;
    }

    @Override
    public Either<Player.BedSleepingProblem, Unit> startCryogenicSleep(BlockPos blockPos) {
        var problem = EntitySleepEvents.ALLOW_SLEEPING.invoker().allowSleep(Player.class.cast(this), blockPos);
        if (problem != null) {
            return Either.left(problem);
        }
        if (this.isPassenger()) {
            this.stopRiding();
        }
        this.entityData.set(GCEntityDataSerializers.IS_IN_CRYO_SLEEP_ID, true);
        this.setPose(Pose.SLEEPING);
        this.setPosToBed(blockPos);
        this.setSleepingPos(blockPos);
        this.setDeltaMovement(Vec3.ZERO);
        this.hasImpulse = true;
        EntitySleepEvents.START_SLEEPING.invoker().onStartSleeping(Player.class.cast(this), blockPos);
        return Either.right(Unit.INSTANCE);
    }

    @Override
    public void stopCryogenicSleep(boolean resetSleepCounter, boolean sync) {
        EntitySleepEvents.STOP_SLEEPING.invoker().onStopSleeping(Player.class.cast(this), this.getSleepingPos().get());
        var optional = this.getSleepingPos();

        if (optional.isPresent()) {
            var blockState = this.level().getBlockState(optional.get());

            if (blockState.getBlock() instanceof CryogenicChamberBlock) {
                this.setRot(blockState.getValue(CryogenicChamberBlock.FACING).toYRot(), 0);
            }
        }

        this.setPose(Pose.STANDING);
        this.clearSleepingPos();
        this.entityData.set(GCEntityDataSerializers.IS_IN_CRYO_SLEEP_ID, false);

        if (this.level() instanceof ServerLevel serverLevel && sync) {
            serverLevel.updateSleepingPlayerList();
        }
        this.sleepCounter = resetSleepCounter ? 0 : 100;
    }

    @Override
    public void setPosToBed(BlockPos blockPos) {
        if (this.isInCryoSleep()) {
            this.setPos(blockPos.getX() + 0.5d, blockPos.getY() + 1, blockPos.getZ() + 0.5d);
            return;
        }
        super.setPosToBed(blockPos);
    }

    @Inject(method = "stopSleepInBed", at = @At("HEAD"), cancellable = true)
    private void gc$stopCryoSleep(boolean resetSleepCounter, boolean sync, CallbackInfo ci) {
        if (this.isInCryoSleep()) {
            this.stopCryogenicSleep(resetSleepCounter, sync);
            ci.cancel();
        }
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void gc$sleepData(CallbackInfo ci) {
        this.entityData.define(GCEntityDataSerializers.IS_IN_CRYO_SLEEP_ID, false);
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
        if (this.getCryogenicChamberCooldown() > 0) {
            this.setCryogenicChamberCooldown(this.getCryogenicChamberCooldown() - 1);
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void gc$preventMovement(CallbackInfo ci) {
        if (this.isInCryoSleep()) {
            ci.cancel();
        }
    }
}