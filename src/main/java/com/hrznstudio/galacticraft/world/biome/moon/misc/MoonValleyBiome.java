package com.hrznstudio.galacticraft.world.biome.moon.misc;

import com.hrznstudio.galacticraft.world.biome.moon.MoonBiome;
import com.hrznstudio.galacticraft.world.biome.moon.mare.MoonMarePlainsBiome;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.GalacticraftSurfaceBuilders;
import net.minecraft.world.biome.BiomeEffects;

public class MoonValleyBiome extends MoonBiome {
    public MoonValleyBiome() {
        super(new Settings()
                .configureSurfaceBuilder(GalacticraftSurfaceBuilders.MOON_SURFACE_BUILDER, MoonMarePlainsBiome.MOON_MARE_BIOME_CONFIG)
                .precipitation(Precipitation.NONE)
                .category(Category.NONE)
                .depth(-0.5F)
                .scale(0.03F)
                .temperature(-1000.0F)
                .downfall(0.005F)
                .effects(new BiomeEffects.Builder()
                        .waterColor(9937330)
                        .waterFogColor(11243183)
                        .fogColor(0)
                        .build())
                .parent(null)
        );
    }
}
