package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.sounds.GalacticraftSounds;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftDimensions;
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

@Mixin(MusicTracker.class)
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
        if (client.player != null) {
            if (client.player.world.dimension.getType() == GalacticraftDimensions.MOON) {
                gcMusic = GalacticraftSounds.MUSIC_MOON;
                minDelay = 1200;
                maxDelay = 3600;
            } //todo mars/space stations
        }

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
