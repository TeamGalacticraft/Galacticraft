package io.github.teamgalacticraft.galacticraft.world.dimension;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftDimensions {
    public static final DimensionType MOON = Registry.register(Registry.DIMENSION, 30, "galacticraft-rewoven:moon", new GalacticraftDimensionType(30, "galacticraft-rewoven:moon", "DIM30", MoonDimension::new, true));

    public static void init() {
    }
}