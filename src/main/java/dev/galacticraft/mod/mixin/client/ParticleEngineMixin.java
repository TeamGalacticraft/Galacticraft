package dev.galacticraft.mod.mixin.client;

import dev.galacticraft.mod.client.trance.ClientTranceState;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public abstract class ParticleEngineMixin {
    @Inject(
            method = "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;F)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gc$skipParticleRender(LightTexture lightTexture, Camera camera, float partialTick, CallbackInfo ci) {
        if (ClientTranceState.isHallucinating()) {
            ci.cancel();
        }
    }

    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
    private void gc$skipParticleTick(CallbackInfo ci) {
        if (ClientTranceState.isHallucinating()) {
            ci.cancel();
        }
    }
}