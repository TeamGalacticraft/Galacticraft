package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.accessor.MinecraftServerAccessor;
import com.hrznstudio.galacticraft.server.ResearchLoader;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import net.minecraft.class_5219;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.UserCache;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements MinecraftServerAccessor {

    @Shadow @Final private ReloadableResourceManager dataManager;
    @Shadow @Final private LootConditionManager predicateManager;

    @Unique
    private ResearchLoader researchLoader;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(LevelStorage.Session session, class_5219 arg, Proxy proxy, DataFixer dataFixer, CommandManager commandManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        this.researchLoader = new ResearchLoader(predicateManager);
        this.dataManager.registerListener(researchLoader);
    }

    @Override
    public ResearchLoader getResearchLoader() {
        return researchLoader;
    }
}
