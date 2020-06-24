package com.hrznstudio.galacticraft.world.biome.moon.highlands;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.DecoratedFeature;
import net.minecraft.world.gen.feature.ForestRockFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public final class MoonHighlandsRocksBiome extends MoonHighlandsBiome {

    public static final TernarySurfaceConfig MOON_HIGHLANDS_ROCK_CONFIG = new TernarySurfaceConfig(GalacticraftBlocks.MOON_ROCK.getDefaultState(), GalacticraftBlocks.MOON_ROCK.getDefaultState(), GalacticraftBlocks.MOON_ROCK.getDefaultState());

    public MoonHighlandsRocksBiome() {
        super((new Settings())
                .configureSurfaceBuilder(SurfaceBuilder.DEFAULT, MOON_HIGHLANDS_ROCK_CONFIG)
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
        this.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, DecoratedFeature.FOREST_ROCK.configure(new ForestRockFeatureConfig(GalacticraftBlocks.MOON_ROCK.getDefaultState(), 6)).createDecoratedFeature(Decorator.WATER_LAKE.configure(new ChanceDecoratorConfig(4))));
    }

    @Override
    protected String getBiomeName() {
        return "rocks";
    }
}
