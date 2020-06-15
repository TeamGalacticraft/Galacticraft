package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.accessor.MinecraftServerAccessor;
import com.hrznstudio.galacticraft.accessor.ServerResourceAccessor;
import com.hrznstudio.galacticraft.accessor.ServerPlayerEntityAccessor;
import com.hrznstudio.galacticraft.api.research.PlayerResearchTracker;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements ServerPlayerEntityAccessor {
    @Shadow
    @Final
    public MinecraftServer server;

    @Unique
    private PlayerResearchTracker researchTracker;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void initResearch(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager serverPlayerInteractionManager, CallbackInfo ci) {
        researchTracker = ((MinecraftServerAccessor) server).getResearchTracker((ServerPlayerEntity) (Object) this);
    }

    @Inject(at = @At("RETURN"), method = "tick")
    private void tickResearch(CallbackInfo ci) {
        researchTracker.sendUpdate(((ServerPlayerEntity) (Object) this));
    }

    @Override
    @Unique
    public PlayerResearchTracker getResearchTracker() {
        return researchTracker;
    }
}
