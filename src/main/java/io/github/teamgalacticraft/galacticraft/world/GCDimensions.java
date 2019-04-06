package io.github.teamgalacticraft.galacticraft.world;

import io.github.teamgalacticraft.galacticraft.world.dimension.MoonDimensionType;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

public class GCDimensions {
   public static final DimensionType MOON = Registry.register(Registry.DIMENSION, 4, "moon", new MoonDimensionType());
}
