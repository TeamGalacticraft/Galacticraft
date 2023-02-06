package dev.galacticraft.mod.world.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class MoonBiomes {
    public static Biome createCometTundra(HolderGetter<PlacedFeature> featureLookup, HolderGetter<ConfiguredWorldCarver<?>> carverLookup) {
        return OverworldBiomes.plains(featureLookup, carverLookup, false, false, false); //fixme
    }

    public static Biome createBasalticMare(HolderGetter<PlacedFeature> featureLookup, HolderGetter<ConfiguredWorldCarver<?>> carverLookup) {
        return OverworldBiomes.plains(featureLookup, carverLookup, false, false, false); //fixme
    }

    public static Biome createLunarHighlands(HolderGetter<PlacedFeature> featureLookup, HolderGetter<ConfiguredWorldCarver<?>> carverLookup) {
        return OverworldBiomes.plains(featureLookup, carverLookup, false, false, false); //fixme
    }

    public static Biome createOlivineSpikes(HolderGetter<PlacedFeature> featureLookup, HolderGetter<ConfiguredWorldCarver<?>> carverLookup) {
        return OverworldBiomes.plains(featureLookup, carverLookup, false, false, false); //fixme
    }
}
