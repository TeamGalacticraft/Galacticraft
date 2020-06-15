package com.hrznstudio.galacticraft.accessor;

import com.hrznstudio.galacticraft.api.research.PlayerResearchTracker;
import com.hrznstudio.galacticraft.server.ServerResearchLoader;
import net.minecraft.server.network.ServerPlayerEntity;

public interface ServerResourceAccessor {
    ServerResearchLoader getServerResearchLoader();

    PlayerResearchTracker getResearchTracker(ServerPlayerEntity player);
}
