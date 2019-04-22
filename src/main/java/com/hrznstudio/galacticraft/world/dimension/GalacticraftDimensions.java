package com.hrznstudio.galacticraft.world.dimension;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftDimensions {
    public static final DimensionType MOON = Registry.register(Registry.DIMENSION, 30, "galacticraft-rewoven:moon", new GalacticraftDimensionType(30, "galacticraft-rewoven:moon", "DIM30", MoonDimension::new, true));
    public static final DimensionType MARS = Registry.register(Registry.DIMENSION, 31, "galacticraft-rewoven:mars", new GalacticraftDimensionType(31, "galacticraft-rewoven:mars", "DIM31", MarsDimension::new, true));


    public static void init() {
    }
}