package com.hrznstudio.galacticraft.mixin.client;

import com.hrznstudio.galacticraft.accessor.ClientPlayNetworkHandlerAccessor;
import com.hrznstudio.galacticraft.api.research.ClientResearchManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientPlayNetworkHandler.class)
@Environment(EnvType.CLIENT)
public class ClientPlayNetworkHandlerMixin implements ClientPlayNetworkHandlerAccessor {
    @Unique
    private final ClientResearchManager researchManager = new ClientResearchManager();

    @Override
    @Unique
    public ClientResearchManager getClientResearchManager() {
        return researchManager;
    }
}
