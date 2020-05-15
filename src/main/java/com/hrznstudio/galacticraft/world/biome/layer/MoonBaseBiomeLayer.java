package com.hrznstudio.galacticraft.world.biome.layer;

import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum MoonBaseBiomeLayer implements InitLayer {
    INSTANCE;

    public int sample(LayerRandomnessSource context, int x, int y) {
        PerlinNoiseSampler perlinNoiseSampler = context.getNoiseSampler();
        double d = perlinNoiseSampler.sample((double)x / 8.0D, (double)y / 8.0D, 0.0D, 0.0D, 0.0D);
        if (d == 0.0D) {
            return MoonBiomeLayers.MOON_CHEESE_FOREST_ID;
        }
        if (d > -0.1D) {
            return MoonBiomeLayers.MOON_HIGHLANDS_PLAINS_ID;
        } else {
            return MoonBiomeLayers.MOON_MARE_PLAINS_ID;
        }
    }
}
