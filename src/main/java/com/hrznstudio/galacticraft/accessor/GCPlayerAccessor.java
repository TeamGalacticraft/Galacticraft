package com.hrznstudio.galacticraft.accessor;

import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import com.hrznstudio.galacticraft.container.PlayerInventoryGCContainer;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public interface GCPlayerAccessor {
    PlayerInventoryGCContainer getGCContainer();

    SimpleFixedItemInv getGearInventory();
}