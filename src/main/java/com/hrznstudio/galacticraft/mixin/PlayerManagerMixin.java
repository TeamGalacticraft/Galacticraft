package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.accessor.ServerPlayerEntityAccessor;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "remove", at = @At("RETURN"))
    private void removeResearchTracker(ServerPlayerEntity player, CallbackInfo ci) {
        ((ServerPlayerEntityAccessor) player).getResearchTracker().clearCriteria();
    }
}
