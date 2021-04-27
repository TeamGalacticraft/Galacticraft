package dev.galacticraft.mod.world.biome.layer.moon;

import dev.galacticraft.mod.world.biome.layer.MoonBiomeLayer;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum MoonEdgeBiomeLayer implements CrossSamplingLayer {
    INSTANCE;

    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        int mare = 0;
        int highland = 0;
        if (isMare(n)) mare++;
        else if (isHighland(n)) highland++;
        if (isMare(s)) mare++;
        else if (isHighland(n)) highland++;
        if (isMare(e)) mare++;
        else if (isHighland(n)) highland++;
        if (isMare(w)) mare++;
        else if (isHighland(n)) highland++;
        if (mare > 0 && mare < 4 && highland > 0) {
            if (isMare(center)) {
                return MoonBiomeLayer.MOON_MARE_EDGE_ID;
            }
            return MoonBiomeLayer.MOON_HIGHLANDS_EDGE_ID;
        }
        return center;
    }

    private static boolean isMare(int id) {
        return id == MoonBiomeLayer.MOON_MARE_ID;
    }

    private static boolean isHighland(int id) {
        return id == MoonBiomeLayer.MOON_HIGHLANDS_ID;
    }
}
