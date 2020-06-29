package com.hrznstudio.galacticraft.accessor;

import com.hrznstudio.galacticraft.api.research.PlayerResearchTracker;

public interface ServerPlayerEntityAccessor {
    PlayerResearchTracker getResearchTracker();

    double getResearchScrollX();

    double getResearchScrollY();

    void setResearchScroll(double x, double y);
}
