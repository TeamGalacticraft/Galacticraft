package com.hrznstudio.galacticraft.world.biome.moon.mare;

import com.hrznstudio.galacticraft.world.gen.surfacebuilder.GalacticraftSurfaceBuilders;
import net.minecraft.world.biome.BiomeEffects;

public final class MoonMareValleyBiome extends MoonMareBiome {
    public MoonMareValleyBiome() {
        super(new Settings()
                .configureSurfaceBuilder(GalacticraftSurfaceBuilders.MOON_SURFACE_BUILDER, MoonMareRocksBiome.MOON_MARE_ROCK_CONFIG)
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

    @Override
    protected String getBiomeName() {
        return "valley";
    }
}
