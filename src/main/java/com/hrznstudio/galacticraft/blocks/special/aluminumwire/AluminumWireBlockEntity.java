package com.hrznstudio.galacticraft.blocks.special.aluminumwire;

import com.hrznstudio.galacticraft.api.entity.WireBlockEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.util.Tickable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class AluminumWireBlockEntity extends WireBlockEntity implements Tickable {

    public AluminumWireBlockEntity() {
        super(GalacticraftBlockEntities.ALUMINUM_WIRE_TYPE);
    }

    @Override
    public void tick() {
        if (!tickedOnce) {
            onPlaced();
        }
        if (WireUtils.getNetworkFromId(networkId) == null) {
            onPlaced();
        }
    }

    public void init() {
        onPlaced();
    }
}
