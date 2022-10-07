/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import dev.galacticraft.mod.accessor.LivingEntityAccessor;
import dev.galacticraft.mod.entity.data.GCTrackedDataHandler;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccessor {
    @Shadow public abstract void setSleepingPos(BlockPos blockPos);

    @Shadow public abstract void clearSleepingPos();

    @Shadow public abstract Optional<BlockPos> getSleepingPos();

    @Shadow public abstract void setYHeadRot(float f);

    @Shadow protected abstract void setPosToBed(BlockPos blockPos);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Player.BedSleepingProblem startCryogenicSleep(BlockPos pos) {
        Player.BedSleepingProblem problem = EntitySleepEvents.ALLOW_SLEEPING.invoker().allowSleep((Player) (Object) this, pos);
        if (problem != null)
            return problem;
        this.entityData.set(GCTrackedDataHandler.IS_IN_CRYO_SLEEP_ID, true);
        this.setPose(Pose.SLEEPING);
        setPosToBed(pos);
        setSleepingPos(pos);
        setDeltaMovement(Vec3.ZERO);
        EntitySleepEvents.START_SLEEPING.invoker().onStartSleeping((Player) (Object) this, pos);
        return null;
    }

    @Override
    public void stopCryogenicSleep(boolean resetSleepCounter, boolean sync) {
        EntitySleepEvents.STOP_SLEEPING.invoker().onStopSleeping((Player) (Object) this, getSleepingPos().get());
        clearSleepingPos();
        this.entityData.set(GCTrackedDataHandler.IS_IN_CRYO_SLEEP_ID, false);
        if (this.level instanceof ServerLevel serverLevel && sync) {
            serverLevel.updateSleepingPlayerList();
        }
    }

    @Override
    public boolean isInCryoSleep() {
        return this.entityData.get(GCTrackedDataHandler.IS_IN_CRYO_SLEEP_ID);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void gc$sleepData(CallbackInfo ci) {
        this.entityData.define(GCTrackedDataHandler.IS_IN_CRYO_SLEEP_ID, false);
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
