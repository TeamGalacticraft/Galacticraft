package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.accessor.ServerResourceAccessor;
import com.hrznstudio.galacticraft.server.ServerResearchLoader;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.command.CommandManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerResourceManager.class)
public abstract class ServerResourceManagerMixin implements ServerResourceAccessor {

    @Shadow @Final private LootConditionManager lootConditionManager;
    @Shadow @Final private ReloadableResourceManager resourceManager;
    @Unique
    private ServerResearchLoader serverResearchLoader;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CommandManager.RegistrationEnvironment registrationEnvironment, int i, CallbackInfo ci) {
        this.serverResearchLoader = new ServerResearchLoader(lootConditionManager);
        this.resourceManager.registerListener(serverResearchLoader);
    }

    @Override
    @Unique
    public ServerResearchLoader getServerResearchLoader() {
        return serverResearchLoader;
    }
}
