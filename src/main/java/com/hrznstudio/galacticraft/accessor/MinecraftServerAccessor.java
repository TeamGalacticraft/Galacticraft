package com.hrznstudio.galacticraft.accessor;

import com.hrznstudio.galacticraft.api.research.PlayerResearchTracker;
import com.hrznstudio.galacticraft.server.ResearchLoader;
import net.minecraft.server.network.ServerPlayerEntity;

public interface MinecraftServerAccessor {
    ResearchLoader getResearchLoader();

    PlayerResearchTracker getResearchTracker(ServerPlayerEntity player);
}
