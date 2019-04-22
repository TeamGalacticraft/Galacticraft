package com.hrznstudio.galacticraft.api.world.dimension;

/**
 * Implement if you want the dimension to have irregular gravity.
 *
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public interface SpaceDimension {

    float getGravity();

    boolean hasOxygen();
}
