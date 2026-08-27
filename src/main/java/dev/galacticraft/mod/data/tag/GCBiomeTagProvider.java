package dev.galacticraft.mod.data.tag;

import dev.galacticraft.mod.tag.GCBiomeTags;
import dev.galacticraft.mod.world.biome.GCBiomes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;

import java.util.concurrent.CompletableFuture;

public class GCBiomeTagProvider
        extends FabricTagProvider<Biome> {

    public GCBiomeTagProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> future
    ) {
        super(
                output,
                Registries.BIOME,
                future
        );
    }

    @Override
    protected void addTags(
            HolderLookup.Provider provider
    ) {
        this.tag(GCBiomeTags.MOON)
                .add(GCBiomes.Moon.COMET_TUNDRA)
                .add(GCBiomes.Moon.BASALTIC_MARE)
                .add(GCBiomes.Moon.LUNAR_HIGHLANDS)
                .add(GCBiomes.Moon.LUNAR_LOWLANDS)
                .add(GCBiomes.Moon.CHEESE_CAVES)
                .add(GCBiomes.Moon.OLIVINE_CAVES)
                .add(GCBiomes.Moon.GLACIAL_CAVERNS);

        this.tag(GCBiomeTags.VENUS)
                .add(GCBiomes.Venus.VENUS_VALLEY)
                .add(GCBiomes.Venus.VENUS_FLAT)
                .add(GCBiomes.Venus.VENUS_MOUNTAIN);

        this.tag(GCBiomeTags.ASTEROID)
                .add(GCBiomes.Asteroid.ASTEROID_FIELD);

        this.tag(
                GCBiomeTags.MOON_PILLAGER_BASE_HAS_STRUCTURE
        ).add(
                GCBiomes.Moon.BASALTIC_MARE
        );

        this.tag(
                GCBiomeTags.MOON_VILLAGE_HIGHLANDS_HAS_STRUCTURE
        ).add(
                GCBiomes.Moon.LUNAR_HIGHLANDS
        );

        this.tag(
                GCBiomeTags.MOON_RUINS_HAS_STRUCTURE
        ).add(
                GCBiomes.Moon.BASALTIC_MARE
        );

        this.tag(
                GCBiomeTags.MOON_DUNGEON_HAS_STRUCTURE
        ).add(
                GCBiomes.Moon.LUNAR_LOWLANDS
        );
    }
}