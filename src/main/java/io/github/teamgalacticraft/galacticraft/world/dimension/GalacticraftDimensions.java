package io.github.teamgalacticraft.galacticraft.world.dimension;

import io.github.teamgalacticraft.galacticraft.Constants;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

public class GalacticraftDimensions {

    public static final DimensionType MOON = null;

    public void register() {
        Registry.register(Registry.DIMENSION, new Identifier(Constants.MOD_ID, "moon"), MOON);
    }
}
