package io.github.teamgalacticraft.galacticraft.world.biome;

import io.github.teamgalacticraft.galacticraft.world.gen.decorator.GalacticraftDecorators;
import io.github.teamgalacticraft.galacticraft.world.gen.surfacebuilder.GalacticraftSurfaceBuilders;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.NetherBiome;
import net.minecraft.world.biome.PlainsBiome;
import net.minecraft.world.biome.TaigaBiome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

import java.util.Arrays;
import java.util.Set;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public final class MoonBiome extends Biome {

    public MoonBiome() {
        super((new Settings())
                .configureSurfaceBuilder(SurfaceBuilder.DEFAULT, GalacticraftSurfaceBuilders.MOON_CONFIG)
                .precipitation(Precipitation.NONE)
                .category(Category.NONE)
                .depth(0.075F)
                .scale(0.075F)
                .temperature(0.0F)
                .downfall(0.003F)
                .waterColor(9937330)
                .waterFogColor(11253183)
                .parent(null));
        this.flowerFeatures.clear();
        for (GenerationStep.Feature feature : this.features.keySet()) {
            this.features.get(feature).clear();
        }
        this.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, GalacticraftDecorators.CRATER_CONF);
    }

    @Override
    public Precipitation getPrecipitation() {
        return Precipitation.NONE;
    }

    @Override
    public float getTemperature(BlockPos blockPos) {
        return 0;
    }

    @Override
    public String getTranslationKey() {
        return "biome.galacticraft-rewoven.moon";
    }

    @Override
    public int getSkyColor(float f) {
        return 0;
    }

    @Override
    public int getFoliageColorAt(BlockPos blockPos_1) {
        return waterFogColor;
    }

    @Override
    public int getGrassColorAt(BlockPos blockPos_1) {
        return waterColor;
    }
}
