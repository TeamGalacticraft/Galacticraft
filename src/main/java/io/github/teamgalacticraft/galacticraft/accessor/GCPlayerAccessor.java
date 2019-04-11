package io.github.teamgalacticraft.galacticraft.accessor;

import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import io.github.teamgalacticraft.galacticraft.container.PlayerInventoryGCContainer;

public interface GCPlayerAccessor {
    PlayerInventoryGCContainer getGCContainer();

    SimpleFixedItemInv getGearInventory();
}