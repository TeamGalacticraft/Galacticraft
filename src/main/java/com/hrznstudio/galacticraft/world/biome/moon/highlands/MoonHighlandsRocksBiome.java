package com.hrznstudio.galacticraft.world.biome.moon.highlands;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.biome.SpaceBiome;
import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public final class MoonHighlandsRocksBiome extends Biome implements SpaceBiome {

    public static final TernarySurfaceConfig MOON_ROCK_CONFIG = new TernarySurfaceConfig(GalacticraftBlocks.MOON_ROCK.getDefaultState(), GalacticraftBlocks.MOON_ROCK.getDefaultState(), GalacticraftBlocks.MOON_ROCK.getDefaultState());

    public MoonHighlandsRocksBiome() {
        super((new Biome.Settings())
                .configureSurfaceBuilder(SurfaceBuilder.DEFAULT, MOON_ROCK_CONFIG)
                .precipitation(Precipitation.NONE)
                .category(Category.NONE)
                .depth(0.65F)
                .scale(0.0065F)
                .temperature(-100.0F)
                .downfall(0.00002F)
                .waterColor(9937330)
                .waterFogColor(11253183)
                .parent(Constants.MOD_ID + ":" + Constants.Biomes.MOON_HIGHLANDS_CRATERS));
        this.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, new ConfiguredFeature<>((ForestRockFeature) Feature.FOREST_ROCK, new BoulderFeatureConfig(GalacticraftBlocks.MOON_ROCK.getDefaultState(), 10)));

        this.addFeature(GenerationStep.Feature.RAW_GENERATION, GalacticraftFeatures.MOON_VILLAGE.configure(new DefaultFeatureConfig()).createDecoratedFeature(new ConfiguredDecorator<>(Decorator.NOPE, DecoratorConfig.DEFAULT)));
        this.addStructureFeature(GalacticraftFeatures.MOON_VILLAGE.configure(FeatureConfig.DEFAULT));
    }

    @Override
    public String getTranslationKey() {
        return "biome.galacticraft-rewoven.moon_highlands_rocks";
    }

    @Override
    public int getSkyColor() {
        return 0;
    }

    @Override
    public int getFoliageColor() {
        return waterFogColor;
    }

    @Override
    public int getGrassColorAt(double x, double z) {
        return waterColor;
    }

    @Override
    public TemperatureGroup getTemperatureGroup() {
        return TemperatureGroup.COLD;
    }

    @Override
    public Text getName() {
        return new TranslatableText(this.getTranslationKey());
    }

    @Override
    public Precipitation getPrecipitation() {
        return Precipitation.NONE;
    }
}