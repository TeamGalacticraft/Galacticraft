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

package dev.galacticraft.impl.internal.mixin.client;

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.accessor.SoundSystemAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
@Environment(EnvType.CLIENT)
public abstract class MinecraftClientMixin {
    @Shadow
    public abstract SoundManager getSoundManager();

    @Inject(method = "setLevel", at = @At("RETURN"))
    private void updateSoundMultiplier(ClientLevel level, ReceivingLevelScreen.Reason reason, CallbackInfo ci) {
        if (level != null) {
            Holder<CelestialBody<?, ?>> holder = level.galacticraft$getCelestialBody();
            if (holder != null) {
                ((SoundSystemAccessor) ((SoundManagerAccessor) this.getSoundManager()).getSoundSystem()).galacticraft$updateAtmosphericVolumeMultiplier(holder.value().atmosphere().pressure());
            } else {
                ((SoundSystemAccessor) ((SoundManagerAccessor) this.getSoundManager()).getSoundSystem()).galacticraft$updateAtmosphericVolumeMultiplier(1.0f);
            }
        } else {
            ((SoundSystemAccessor) ((SoundManagerAccessor) this.getSoundManager()).getSoundSystem()).galacticraft$updateAtmosphericVolumeMultiplier(1.0f);
        }
    }
}
