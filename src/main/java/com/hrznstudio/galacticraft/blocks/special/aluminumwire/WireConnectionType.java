package com.hrznstudio.galacticraft.blocks.special.aluminumwire;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum WireConnectionType {

    /**
     * The wire is not connected to anything.
     */
    NONE,

    /**
     * The wire is connected to another wire.
     */
    WIRE,

    /**
     * The wire is connected to some sort of energy consuming block.
     */
    ENERGY_INPUT,

    /**
     * The wire is connected to some sort of energy generating block.
     */
    ENERGY_OUTPUT

}
