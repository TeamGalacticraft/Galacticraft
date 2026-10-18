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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.ParachuteAccessor;
import dev.galacticraft.mod.content.entity.Parachute;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Players with an open parachute fall slowly and take no fall damage.
 */
@Mixin(Player.class)
public abstract class ParachutePlayerMixin extends LivingEntity implements ParachuteAccessor {
    @Unique
    @SuppressWarnings("WrongEntityDataParameterClass")
    private static final EntityDataAccessor<Boolean> DATA_PARACHUTING_ID = SynchedEntityData.defineId(
            Player.class, EntityDataSerializers.BOOLEAN
    );

    protected ParachutePlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void gc$defineParachuteData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(DATA_PARACHUTING_ID, false);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void gc$readParachuting(CompoundTag tag, CallbackInfo ci) {
        this.galacticraft$setParachuting(tag.getBoolean(Constant.Nbt.PARACHUTING));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void gc$writeParachuting(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean(Constant.Nbt.PARACHUTING, this.galacticraft$isParachuting());
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void gc$tickParachute(CallbackInfo ci) {
        if (!this.galacticraft$isParachuting()) {
            return;
        }
        if (!this.level().isClientSide() && Parachute.shouldClose((Player) (Object) this)) {
            this.galacticraft$setParachuting(false);
        } else {
            this.resetFallDistance();
        }
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void gc$parachuteFall(Vec3 input, CallbackInfo ci) {
        if (this.galacticraft$isParachuting() && Parachute.slowsFall((Player) (Object) this)) {
            Vec3 movement = this.getDeltaMovement();
            this.setDeltaMovement(movement.x * Parachute.HORIZONTAL_DRAG, -Parachute.FALL_SPEED, movement.z * Parachute.HORIZONTAL_DRAG);
        }
    }

    @Override
    public boolean galacticraft$isParachuting() {
        return this.entityData.get(DATA_PARACHUTING_ID);
    }

    @Override
    public void galacticraft$setParachuting(boolean parachuting) {
        this.entityData.set(DATA_PARACHUTING_ID, parachuting);
    }
}
