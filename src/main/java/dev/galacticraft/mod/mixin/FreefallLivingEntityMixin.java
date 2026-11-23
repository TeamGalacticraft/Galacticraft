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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.galacticraft.mod.content.entity.Freefall;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class FreefallLivingEntityMixin extends Entity {
    @Shadow
    protected boolean jumping;

    /**
     * Whether the entity is in freefall during this tick of movement.
     */
    @Unique
    private boolean galacticraft$inFreefall = false;
    /**
     * The number of ticks jump has been held on a surface, to push off it.
     */
    @Unique
    private int galacticraft$pushOffTicks = 0;

    public FreefallLivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    private void gc$pushOffInsteadOfJumping(CallbackInfo ci) {
        // With barely any gravity, a normal jump would launch the player into space
        if ((Object) this instanceof Player && Freefall.hasZeroGravity(this.level())) {
            ci.cancel();
        }
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void gc$freefallControls(Vec3 movementInput, CallbackInfo ci) {
        this.galacticraft$inFreefall = this.isControlledByLocalInstance() && Freefall.isInFreefall(this);
        if (!((Object) this instanceof Player player) || !this.isControlledByLocalInstance() || !Freefall.hasZeroGravity(this.level())) {
            this.galacticraft$pushOffTicks = 0;
            return;
        }

        double vertical = 0.0;
        if (this.galacticraft$inFreefall) {
            this.galacticraft$pushOffTicks = 0;
            if (this.jumping) vertical += Freefall.VERTICAL_ACCELERATION;
            if (player.isShiftKeyDown()) vertical -= Freefall.VERTICAL_ACCELERATION;
        } else if (this.jumping) {
            if (this.onGround()) {
                // Hold jump to push off the surface when it is released
                this.galacticraft$pushOffTicks = Math.min(this.galacticraft$pushOffTicks + 1, Freefall.MAX_PUSH_OFF_TICKS);
            } else {
                vertical += Freefall.WALL_CLIMB_ACCELERATION;
            }
        } else if (this.galacticraft$pushOffTicks > 0) {
            vertical += Freefall.PUSH_OFF_SPEED_PER_TICK * this.galacticraft$pushOffTicks;
            this.galacticraft$pushOffTicks = 0;
        } else if (player.isShiftKeyDown() && !this.onGround()) {
            vertical -= Freefall.WALL_CLIMB_ACCELERATION;
        }

        if (vertical != 0.0) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, vertical, 0.0));
        }
    }

    @ModifyConstant(method = "travel", constant = {@Constant(floatValue = 0.91F), @Constant(floatValue = 0.98F)})
    private float gc$noDragInFreefall(float drag) {
        return this.galacticraft$inFreefall ? 1.0F : drag;
    }

    @ModifyReturnValue(method = "getFrictionInfluencedSpeed", at = @At("RETURN"))
    private float gc$driftInFreefall(float speed) {
        return this.galacticraft$inFreefall ? Freefall.MOVEMENT_ACCELERATION : speed;
    }

    @Inject(method = "travel", at = @At("RETURN"))
    private void gc$limitFreefallSpeed(Vec3 movementInput, CallbackInfo ci) {
        if (this.galacticraft$inFreefall) {
            double max = (Object) this instanceof Player ? Freefall.MAX_PLAYER_SPEED : Freefall.MAX_ENTITY_SPEED;
            Vec3 movement = this.getDeltaMovement();
            this.setDeltaMovement(
                    Mth.clamp(movement.x, -max, max),
                    Mth.clamp(movement.y, -max, max),
                    Mth.clamp(movement.z, -max, max)
            );
        }
        this.galacticraft$inFreefall = false;
    }
}
