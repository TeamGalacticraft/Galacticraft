package com.hrznstudio.galacticraft.mixin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.PlayerListEntry;
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
public abstract class JsonCapeMixin {

    @Shadow
    @Final
    private Map<MinecraftProfileTexture.Type, Identifier> textures;

    @Inject(at = @At("TAIL"), method = "loadTextures")
    private void loadTextures(CallbackInfo callbackInfo) {
        /*if (GalacticraftClient.jsonCapes.areCapesLoaded()) {
            for (Entity entity : MinecraftClient.getInstance().world.getPlayers()) {
                if (entity instanceof PlayerEntity) {
                    if (GalacticraftClient.jsonCapes.getCapePlayers().get(entity.getUuid()) != null) {
                        if(this.textures != null) {
                            JsonCapes.Cape cape = GalacticraftClient.jsonCapes.getCapePlayers().get(entity.getUuid()).getCape();
                            if(GalacticraftClient.jsonCapes.getCapePlayers().get(entity.getUuid()) != null) {
                                textures.put(MinecraftProfileTexture.Type.CAPE, cape.getTexture());
                            }
                        }
                    }
                }
            }
        }*/

    }
}

