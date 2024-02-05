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

package dev.galacticraft.impl.internal.mixin.client;

import com.mojang.blaze3d.audio.Listener;
import dev.galacticraft.impl.accessor.SoundSystemAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
@Environment(EnvType.CLIENT)
public abstract class SoundEngineMixin implements SoundSystemAccessor {
    @Shadow @Final private Listener listener;
    @Unique private float multiplier = 1.0f;

    @Shadow public abstract void updateCategoryVolume(SoundSource soundCategory, float volume);

    @Inject(method = "calculateVolume(FLnet/minecraft/sounds/SoundSource;)F", at = @At("RETURN"), cancellable = true)
    private void galacticraft_adjustVolumeToAtmosphere(float f, SoundSource soundSource, CallbackInfoReturnable<Float> cir) {
        if (multiplier != 1.0f && soundSource != SoundSource.MASTER) {
            cir.setReturnValue(Mth.clamp(cir.getReturnValueF() * this.multiplier, 0.0f, 2.0f));
        }
    }

    @Redirect(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/SoundInstance;canStartSilent()Z", ordinal = 0))
    private boolean galacticraft_shouldAlwaysPlay(SoundInstance soundInstance) {
        if (this.multiplier != 1.0f && soundInstance.getAttenuation() != SoundInstance.Attenuation.NONE) {
            return true;
        }
        return soundInstance.canStartSilent();
    }

    @Override
    public void galacticraft$updateAtmosphericVolumeMultiplier(float multiplier) {
        this.multiplier = multiplier;
        this.updateCategoryVolume(null, this.listener.getGain());
    }
}
