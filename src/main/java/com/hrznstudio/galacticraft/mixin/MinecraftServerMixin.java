package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.accessor.MinecraftServerAccessor;
import com.hrznstudio.galacticraft.accessor.ServerResourceAccessor;
import com.hrznstudio.galacticraft.api.research.PlayerResearchTracker;
import com.hrznstudio.galacticraft.server.ServerResearchLoader;
import com.mojang.datafixers.DataFixer;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerAccessor {

    @Shadow private ServerResourceManager serverResourceManager;

    @Shadow public abstract Path getSavePath(WorldSavePath worldSavePath);

    @Shadow public abstract DataFixer getDataFixer();

    @Shadow public abstract PlayerManager getPlayerManager();

    @Unique
    private final Map<UUID, PlayerResearchTracker> researchTrackers = new HashMap<>();

    @Override
    public ServerResearchLoader getResearchLoader() {
        return ((ServerResourceAccessor) this.serverResourceManager).getServerResearchLoader();
    }

    @Override
    @Unique
    public PlayerResearchTracker getResearchTracker(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        PlayerResearchTracker researchTracker = this.researchTrackers.get(uuid);
        if (researchTracker == null) {
            File file = new File(getSavePath(WorldSavePath.ADVANCEMENTS).toFile().getParentFile(), "/research/");
            file.mkdir();
            File file2 = new File(file, uuid + ".json");
            researchTracker = new PlayerResearchTracker(getDataFixer(), getPlayerManager(), getResearchLoader(), file2, player);
            this.researchTrackers.put(uuid, researchTracker);
        }

        researchTracker.setOwner(player);
        return researchTracker;
    }
}
