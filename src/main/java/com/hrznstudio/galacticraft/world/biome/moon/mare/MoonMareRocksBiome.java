package com.hrznstudio.galacticraft.world.biome.moon.mare;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.GalacticraftSurfaceBuilders;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.BoulderFeatureConfig;
import net.minecraft.world.gen.feature.DecoratedFeature;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public final class MoonMareRocksBiome extends MoonMareBiome {

    public static final TernarySurfaceConfig MOON_MARE_ROCK_CONFIG = new TernarySurfaceConfig(GalacticraftBlocks.MOON_BASALT.getDefaultState(), GalacticraftBlocks.MOON_BASALT.getDefaultState(), GalacticraftBlocks.MOON_BASALT.getDefaultState());

    public MoonMareRocksBiome() {
        super((new Settings())
                .configureSurfaceBuilder(GalacticraftSurfaceBuilders.MOON_SURFACE_BUILDER, MOON_MARE_ROCK_CONFIG)
                .precipitation(Precipitation.NONE)
                .category(Category.NONE)
                .depth(0.65F)
                .scale(0.0065F)
                .temperature(-100.0F)
                .downfall(0.00002F)
                .effects(new BiomeEffects.Builder()
                        .waterColor(9937330)
                        .waterFogColor(11243183)
                        .fogColor(0)
                        .build())
                .parent(Constants.MOD_ID + ":" + Constants.Biomes.MOON_HIGHLANDS_ROCKS));
        this.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, DecoratedFeature.FOREST_ROCK.configure(new BoulderFeatureConfig(GalacticraftBlocks.MOON_BASALT.getDefaultState(), 6)).createDecoratedFeature(Decorator.WATER_LAKE.configure(new ChanceDecoratorConfig(4))));
    }

    @Override
    protected String getBiomeName() {
        return "rocks";
    }
}
