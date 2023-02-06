package dev.galacticraft.mod.world.dimension;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.tag.GCTags;
import dev.galacticraft.mod.world.biome.source.GCBiomeParameters;
import dev.galacticraft.mod.world.gen.GCNoiseGeneratorSettings;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalLong;

public class GCLevelStems {
    public static final ResourceKey<LevelStem> MOON = key("moon");

    public static void bootstrapRegistries(@NotNull BootstapContext<LevelStem> context) {
        HolderGetter<DimensionType> typeLookup = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<Biome> biomeLookup = context.lookup(Registries.BIOME);
        HolderGetter<NoiseGeneratorSettings> noiseLookup = context.lookup(Registries.NOISE_SETTINGS);

        // entries must also be added to GCLevelStemProvider
        context.register(MOON, createMoon(typeLookup, biomeLookup, noiseLookup));
    }

    @Contract(value = "_ -> new", pure = true)
    private static @NotNull ResourceKey<LevelStem> key(@NotNull String id) {
        return Constant.key(Registries.LEVEL_STEM, id);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull LevelStem createMoon(@NotNull HolderGetter<DimensionType> typeLookup, @NotNull HolderGetter<Biome> biomeLookup, @NotNull HolderGetter<NoiseGeneratorSettings> noiseLookup) {
        return new LevelStem(typeLookup.getOrThrow(GCDimensionTypes.MOON), new NoiseBasedChunkGenerator(GCBiomeParameters.MOON.biomeSource(biomeLookup), noiseLookup.getOrThrow(GCNoiseGeneratorSettings.MOON)));
    }
}
