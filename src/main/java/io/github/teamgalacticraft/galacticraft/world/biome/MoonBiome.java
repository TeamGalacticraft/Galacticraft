package io.github.teamgalacticraft.galacticraft.world.biome;

import io.github.teamgalacticraft.galacticraft.world.gen.GCSurfaceBuilder;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class MoonBiome extends Biome {

    public MoonBiome() {
        super((new Settings())
                .configureSurfaceBuilder(SurfaceBuilder.DEFAULT, GCSurfaceBuilder.MOON_CONFIG)
                .precipitation(Precipitation.RAIN)
                .category(Category.FOREST).depth(0.1F)
                .scale(0.2F)
                .temperature(0.6F)
                .downfall(0.3F)
                .waterColor(4159204)
                .waterFogColor(329011)
                .parent(null));
        //this.addSpawn(EntityCategory.MONSTER, new SpawnEntry(EntityType.ZOMBIE, 100, 4, 4));
    }
}
