package com.hrznstudio.galacticraft.world.biome.moon.mare;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.biome.SpaceBiome;
import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.BoulderFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.ForestRockFeature;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public final class MoonMareRocksBiome extends Biome implements SpaceBiome {

    public static final TernarySurfaceConfig MOON_MARE_ROCK_CONFIG = new TernarySurfaceConfig(GalacticraftBlocks.MOON_BASALT.getDefaultState(), GalacticraftBlocks.MOON_BASALT.getDefaultState(), GalacticraftBlocks.MOON_BASALT.getDefaultState());

    public MoonMareRocksBiome() {
        super((new Settings())
                .configureSurfaceBuilder(SurfaceBuilder.DEFAULT, MOON_MARE_ROCK_CONFIG)
                .precipitation(Precipitation.NONE)
                .category(Category.NONE)
                .depth(0.65F)
                .scale(0.0065F)
                .temperature(-100.0F)
                .downfall(0.00002F)
                .waterColor(9937330)
                .waterFogColor(11253183)
                .parent(Constants.MOD_ID + ":" + Constants.Biomes.MOON_HIGHLANDS_ROCKS));
        this.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, new ConfiguredFeature<>((ForestRockFeature) Feature.FOREST_ROCK, new BoulderFeatureConfig(GalacticraftBlocks.MOON_BASALT.getDefaultState(), 10)));
    }

    @Override
    public String getTranslationKey() {
        return "biome.galacticraft-rewoven.moon_mare_rocks";
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