package io.github.teamgalacticraft.galacticraft.accessor;

import alexiil.mc.lib.attributes.item.impl.SimpleFixedItemInv;
import io.github.teamgalacticraft.galacticraft.container.PlayerInventoryGCContainer;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public interface GCPlayerAccessor {
    PlayerInventoryGCContainer getGCContainer();

    SimpleFixedItemInv getGearInventory();
}