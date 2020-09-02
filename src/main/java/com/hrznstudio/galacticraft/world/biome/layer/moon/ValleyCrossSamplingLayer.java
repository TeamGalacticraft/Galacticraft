package com.hrznstudio.galacticraft.world.biome.layer.moon;

import com.hrznstudio.galacticraft.world.biome.layer.MoonBiomeLayers;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum ValleyCrossSamplingLayer implements CrossSamplingLayer {
    INSTANCE;

    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        int mare = 0;
        int hl = 0;
        if (isMare(n)) {
            mare++;
        } else if (n != MoonBiomeLayers.MOON_MARE_EDGE_ID) {
            hl++;
        }
        if (isMare(s)) {
            mare++;
        } else if (s != MoonBiomeLayers.MOON_MARE_EDGE_ID) {
            hl++;
        }
        if (isMare(e)) {
            mare++;
        } else if (e != MoonBiomeLayers.MOON_MARE_EDGE_ID) {
            hl++;
        }
        if (isMare(w)) {
            mare++;
        } else if (w != MoonBiomeLayers.MOON_MARE_EDGE_ID) {
            hl++;
        }
        if (mare >= 3) {
            if (hl != 0) {
                return MoonBiomeLayers.MOON_MARE_EDGE_ID;
            } else {
                return chooseRandom(context, n, e, s, w);
            }
        }
        if (hl > 0 && mare > 0) {
            return MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID;
        }
        return chooseRandom(context, n, e, s, w);
    }

    private boolean isMare(int i) {
        return i == MoonBiomeLayers.MOON_MARE_PLAINS_ID || i == MoonBiomeLayers.MOON_MARE_ROCKS_ID;
    }

    private int chooseRandom(LayerRandomnessSource context, int n, int e, int s, int w) {
        int i = context.nextInt(3);
        if (n == MoonBiomeLayers.MOON_MARE_EDGE_ID || n == MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID) n -= context.nextInt(1) + 1;
        if (e == MoonBiomeLayers.MOON_MARE_EDGE_ID || e == MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID) e -= context.nextInt(1) + 1;
        if (s == MoonBiomeLayers.MOON_MARE_EDGE_ID || s == MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID) s -= context.nextInt(1) + 1;
        if (w == MoonBiomeLayers.MOON_MARE_EDGE_ID || w == MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID) w -= context.nextInt(1) + 1;
        if (i == 0) {
            return n;
        } else if (i == 1) {
            return e;
        } else if (i == 2) {
            return s;
        } else {
            return w;
        }
    }
}
