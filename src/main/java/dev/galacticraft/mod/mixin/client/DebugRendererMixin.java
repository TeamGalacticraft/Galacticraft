package dev.galacticraft.mod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.client.trance.ClientTranceState;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public abstract class DebugRendererMixin {
    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void gc$skipDebug(PoseStack poseStack, net.minecraft.client.renderer.MultiBufferSource.BufferSource buffers,
                              double camX, double camY, double camZ, CallbackInfo ci) {
        if (ClientTranceState.isHallucinating()) {
            ci.cancel();
        }
    }
}