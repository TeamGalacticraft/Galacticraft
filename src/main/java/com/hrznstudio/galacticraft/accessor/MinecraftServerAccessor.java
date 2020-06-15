package com.hrznstudio.galacticraft.accessor;

import com.hrznstudio.galacticraft.api.research.PlayerResearchTracker;
import com.hrznstudio.galacticraft.server.ServerResearchLoader;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public interface MinecraftServerAccessor {
    ServerResearchLoader getResearchLoader();

    PlayerResearchTracker getResearchTracker(ServerPlayerEntity player);

    void removeResearchTracker(UUID uuid);
}
