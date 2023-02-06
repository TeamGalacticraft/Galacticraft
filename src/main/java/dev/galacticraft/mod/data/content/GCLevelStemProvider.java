package dev.galacticraft.mod.data.content;

import dev.galacticraft.mod.world.dimension.GCLevelStems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class GCLevelStemProvider extends LevelStemProvider {
    public GCLevelStemProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> future) {
        super(output, future);
    }

    @Override
    public Registry generate(HolderLookup.Provider registries, @NotNull Registry registry) {
        HolderGetter<DimensionType> typeLookup = registries.lookupOrThrow(Registries.DIMENSION_TYPE);
        HolderGetter<Biome> biomeLookup = registries.lookupOrThrow(Registries.BIOME);
        HolderGetter<NoiseGeneratorSettings> noiseLookup = registries.lookupOrThrow(Registries.NOISE_SETTINGS);

        registry.add(GCLevelStems.MOON, GCLevelStems.createMoon(typeLookup, biomeLookup, noiseLookup));
        return registry;
    }
}
