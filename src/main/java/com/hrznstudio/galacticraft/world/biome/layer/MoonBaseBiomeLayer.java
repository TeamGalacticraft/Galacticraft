package com.hrznstudio.galacticraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum MoonBaseBiomeLayer implements InitLayer {
    INSTANCE;

    public int sample(LayerRandomnessSource context, int x, int y) {
        int i = context.nextInt(100000);
        if (i == 12345) { // :)
            return MoonBiomeLayers.MOON_CHEESE_FOREST_ID;
        }
        if (i > 35000) {
            return MoonBiomeLayers.MOON_HIGHLANDS_PLAINS_ID;
        } else {
            return MoonBiomeLayers.MOON_MARE_PLAINS_ID;
        }
    }
}
