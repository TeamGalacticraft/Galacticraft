/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.mixin.client;

import com.hrznstudio.galacticraft.sounds.GalacticraftSounds;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftDimensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Mixin(MusicTracker.class)
@Environment(EnvType.CLIENT)
public abstract class MusicTrackerMixin {
    @Shadow
    private SoundInstance current;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int timeUntilNextSong;

    @Shadow
    @Final
    private Random random;

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void tickGC(CallbackInfo ci) {
        SoundEvent gcMusic = null;
        int minDelay = 0;
        int maxDelay = 0;
//        Marcus says this code isnt needed.
//        if (client.player != null) {
//            if (client.player.world.getDimension() == GalacticraftDimensions.MOON) {
//                gcMusic = GalacticraftSounds.MUSIC_MOON;
//                minDelay = 1200;
//                maxDelay = 3600;
//            } //todo mars/space stations
//        }

        if (gcMusic != null) {
            if (this.current != null) {
                if (!gcMusic.getId().equals(this.current.getId())) {
                    this.client.getSoundManager().stop(this.current);
                    this.timeUntilNextSong = MathHelper.nextInt(this.random, 0, minDelay / 2);
                }

                if (!this.client.getSoundManager().isPlaying(this.current)) {
                    this.current = null;
                    this.timeUntilNextSong = Math.min(MathHelper.nextInt(this.random, minDelay, maxDelay), this.timeUntilNextSong);
                }
            }

            this.timeUntilNextSong = Math.min(this.timeUntilNextSong, maxDelay);
            if (this.current == null && this.timeUntilNextSong-- <= 0) {
                this.playGC(gcMusic);
            }

            ci.cancel();
            return;
        }
    }

    public void playGC(SoundEvent event) {
        this.current = PositionedSoundInstance.music(event);
        this.client.getSoundManager().play(this.current);
        this.timeUntilNextSong = Integer.MAX_VALUE;
    }
}
