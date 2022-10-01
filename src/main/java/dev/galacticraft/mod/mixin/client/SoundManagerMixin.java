package dev.galacticraft.mod.mixin.client;

import dev.galacticraft.mod.item.FrequencyModuleItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void galacticraft$play(SoundInstance soundInstance, CallbackInfo ci) {
        if (soundInstance.getAttenuation() != SoundInstance.Attenuation.NONE) {
            boolean hasModule = Minecraft.getInstance().player.getGearInv().hasAnyMatching(itemStack -> itemStack.getItem() instanceof FrequencyModuleItem);
            if (!hasModule)
                ci.cancel();
        }
    }
}
