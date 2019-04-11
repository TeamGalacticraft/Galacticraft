package io.github.teamgalacticraft.galacticraft.world.dimension;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftDimensions {
    public static final DimensionType MOON = Registry.register(Registry.DIMENSION, 4, "moon", new MoonDimensionType());

    public static void init() {
    }
}
