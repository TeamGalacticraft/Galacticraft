package com.hrznstudio.galacticraft.mixin.client;

import com.hrznstudio.galacticraft.accessor.ClientPlayNetworkHandlerAccessor;
import com.hrznstudio.galacticraft.api.research.ClientResearchManager;
import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
@Environment(EnvType.CLIENT)
public class ClientPlayNetworkHandlerMixin implements ClientPlayNetworkHandlerAccessor {
    @Unique
    private ClientResearchManager researchManager;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initResearchGC(MinecraftClient minecraftClient, Screen screen, ClientConnection connection, GameProfile profile, CallbackInfo ci) {
        researchManager = new ClientResearchManager();
    }

    @Override
    @Unique
    public ClientResearchManager getClientResearchManager() {
        return researchManager;
    }
}
