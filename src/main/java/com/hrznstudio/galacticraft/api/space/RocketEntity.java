package com.hrznstudio.galacticraft.api.space;

import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public interface RocketEntity {
    RocketTier getRocketTier();

    SimpleFixedFluidInv getFuel();
}
