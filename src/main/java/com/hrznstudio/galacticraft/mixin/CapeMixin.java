package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.misc.Capes;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Mixin(PlayerListEntry.class)
public abstract class CapeMixin {

    @Shadow
    @Final
    private Map<MinecraftProfileTexture.Type, Identifier> textures;

    @Inject(at = @At("RETURN"), method = "loadTextures")
    private void loadTextures(CallbackInfo info) {
        for (Entity r : MinecraftClient.getInstance().world.getPlayers()) {
            if (r instanceof PlayerEntity) {
                if (Capes.getCapeMap().get(r.getUuidAsString().replace("-", "")) != null) {
                    textures.put(MinecraftProfileTexture.Type.CAPE, Capes.getCapeMap().get(r.getUuidAsString().replace("-", "")));
                }
            }
        }
    }
}

