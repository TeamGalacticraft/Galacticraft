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

package dev.galacticraft.mod.mixin;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.CryogenicAccessor;
import dev.galacticraft.mod.accessor.PetInventoryOpener;
import dev.galacticraft.mod.content.entity.orbital.AdvancedVehicle;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements CryogenicAccessor, PetInventoryOpener {

    PlayerMixin() {
        super(null, null);
    }

    @Shadow
    public abstract boolean isCreative();

    @Inject(method = "stopSleepInBed", at = @At(value = "HEAD"))
    private void gc$shouldSetCryoCooldown(boolean bl, boolean bl2, CallbackInfo ci) {
        if (!((Player) (Object) this).level().isClientSide() && !this.isCreative() && this.isInCryoSleep()) {
            ((Player) (Object) this).heal(5.0F);

            this.setCryogenicChamberCooldown(6000);
        }
    }

    @Inject(method = "removeEntitiesOnShoulder", at = @At(value = "HEAD"), cancellable = true)
    private void gc$removeEntitiesOnShoulder(CallbackInfo ci) {
        if (this.getVehicle() instanceof AdvancedVehicle || this.getY() >= Constant.REENTRY_HEIGHT) {
            ci.cancel();
        }
    }
}