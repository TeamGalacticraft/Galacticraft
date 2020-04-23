package com.hrznstudio.galacticraft.mixin.client;

import com.hrznstudio.galacticraft.GalacticraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin {

    @Shadow @Nullable protected abstract PlayerListEntry getPlayerListEntry();

    @Inject(method = "getCapeTexture", at = @At("RETURN"), cancellable = true)
    private void getCapeTexture(CallbackInfoReturnable<Identifier> info) {
        if(GalacticraftClient.jsonCapes.areCapesLoaded()) {
            if(GalacticraftClient.jsonCapes.getCapePlayers().containsKey(this.getPlayerListEntry().getProfile().getId())) {
                info.setReturnValue(GalacticraftClient.jsonCapes.getCapePlayers()
                    .get(this.getPlayerListEntry().getProfile().getId()).getCape().getTexture());
            }
        }
    }
}
