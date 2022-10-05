package dev.galacticraft.mod.mixin.client;

import dev.galacticraft.mod.accessor.LivingEntityAccessor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleAnimate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;stopSleepInBed(ZZ)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void gc$handleCryoStop(ClientboundAnimatePacket clientboundAnimatePacket, CallbackInfo ci, Entity entity, Player player) {
        if (((LivingEntityAccessor)player).isInCryoSleep()) {
            ((LivingEntityAccessor)player).stopCryogenicSleep(false, false);
            ci.cancel();
        }
    }
}
