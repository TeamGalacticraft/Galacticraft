package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.accessor.MinecraftServerAccessor;
import com.hrznstudio.galacticraft.api.research.PlayerResearchTracker;
import com.hrznstudio.galacticraft.server.ResearchLoader;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import net.minecraft.class_5218;
import net.minecraft.class_5219;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.UserCache;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerAccessor {

    @Shadow
    @Final
    private ReloadableResourceManager dataManager;
    @Shadow
    @Final
    private LootConditionManager predicateManager;

    @Unique
    private final Map<UUID, PlayerResearchTracker> researchTrackers = new HashMap<>();

    @Shadow
    public abstract Path method_27050(class_5218 arg);

    @Unique
    private ResearchLoader researchLoader;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(LevelStorage.Session session, class_5219 arg, Proxy proxy, DataFixer dataFixer, CommandManager commandManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        this.researchLoader = new ResearchLoader(predicateManager);
        this.dataManager.registerListener(researchLoader);
    }

    @Override
    @Unique
    public PlayerResearchTracker getResearchTracker(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        PlayerResearchTracker researchTracker = this.researchTrackers.get(uuid);
        if (researchTracker == null) {
            File file = new File(method_27050(class_5218.field_24180).toFile().getParentFile(), "/research/");
            File file2 = new File(file, uuid + ".json");
            researchTracker = new PlayerResearchTracker((MinecraftServer) (Object) this, file2, player);
            this.researchTrackers.put(uuid, researchTracker);
        }

        researchTracker.setOwner(player);
        return researchTracker;
    }

    @Override
    @Unique
    public ResearchLoader getResearchLoader() {
        return researchLoader;
    }
}
