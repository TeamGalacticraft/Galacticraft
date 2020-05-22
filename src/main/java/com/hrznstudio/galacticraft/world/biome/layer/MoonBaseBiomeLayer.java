package com.hrznstudio.galacticraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum MoonBaseBiomeLayer implements InitLayer {
    INSTANCE;

    public int sample(LayerRandomnessSource context, int x, int y) {
        double d = ((double)context.nextInt(100)) / 100.0D;
        if (d == 0.0D) {
            return MoonBiomeLayers.MOON_CHEESE_FOREST_ID;
        }
        if (d > -0.1D) {
            return MoonBiomeLayers.MOON_HIGHLANDS_PLAINS_ID;
        } else if (d <= -0.1D){
            return MoonBiomeLayers.MOON_MARE_PLAINS_ID;
        }
        return MoonBiomeLayers.MOON_HIGHLANDS_PLAINS_ID;
    }
}
