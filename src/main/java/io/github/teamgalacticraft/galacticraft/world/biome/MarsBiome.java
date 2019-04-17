package io.github.teamgalacticraft.galacticraft.world.biome;

import io.github.teamgalacticraft.galacticraft.world.gen.surfacebuilder.GalacticraftSurfaceBuilders;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public final class MarsBiome extends Biome {

    public MarsBiome() {
        super((new Settings())
                .configureSurfaceBuilder(SurfaceBuilder.DEFAULT, GalacticraftSurfaceBuilders.MARS_CONFIG)
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
        this.addSpawn(EntityCategory.MONSTER, new SpawnEntry(EntityType.ZOMBIE, 1, 1, 1));
    }

    @Override
    public String getTranslationKey() {
        return "biome.galacticraft-rewoven.moon";
    }

    @Override
    public int getSkyColor(float float_1) {
        return 16747622;
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
