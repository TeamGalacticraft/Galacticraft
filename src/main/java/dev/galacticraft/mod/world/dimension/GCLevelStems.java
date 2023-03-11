package dev.galacticraft.mod.world.dimension;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.biome.source.GCMultiNoiseBiomeSourceParameterLists;
import dev.galacticraft.mod.world.gen.GCNoiseGeneratorSettings;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class GCLevelStems {
    public static final ResourceKey<LevelStem> MOON = key("moon");

    public static void bootstrapRegistries(@NotNull BootstapContext<LevelStem> context) {
        HolderGetter<DimensionType> typeLookup = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<Biome> biomeLookup = context.lookup(Registries.BIOME);
        HolderGetter<NoiseGeneratorSettings> noiseLookup = context.lookup(Registries.NOISE_SETTINGS);
        HolderGetter<MultiNoiseBiomeSourceParameterList> biomeNoiseLookup = context.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);

        // entries must also be added to GCLevelStemProvider
        context.register(MOON, new LevelStem(typeLookup.getOrThrow(GCDimensionTypes.MOON), new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.createFromPreset(biomeNoiseLookup.getOrThrow(GCMultiNoiseBiomeSourceParameterLists.MOON)), noiseLookup.getOrThrow(GCNoiseGeneratorSettings.MOON))));
    }

    @Contract(value = "_ -> new", pure = true)
    private static @NotNull ResourceKey<LevelStem> key(@NotNull String id) {
        return Constant.key(Registries.LEVEL_STEM, id);
    }
}
