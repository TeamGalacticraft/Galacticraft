package com.hrznstudio.galacticraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import com.hrznstudio.galacticraft.world.biome.layer.MoonBiomeLayers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.List;

public class MoonBiomeSource extends BiomeSource {
   public static final Codec<MoonBiomeSource> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.LONG.fieldOf("seed").stable().forGetter((moonBiomeSource) -> moonBiomeSource.seed), Codec.INT.optionalFieldOf("biome_size", 4, Lifecycle.stable()).forGetter((moonBiomeSource) -> moonBiomeSource.biomeSize)).apply(instance, instance.stable(MoonBiomeSource::new)));

   private final BiomeLayerSampler sampler;
   private final long seed;
   private static final List<Biome> BIOMES = ImmutableList.of(GalacticraftBiomes.MOON_HIGHLANDS_PLAINS, GalacticraftBiomes.MOON_HIGHLANDS_CRATERS, GalacticraftBiomes.MOON_HIGHLANDS_ROCKS,
           GalacticraftBiomes.MOON_MARE_PLAINS, GalacticraftBiomes.MOON_MARE_CRATERS, GalacticraftBiomes.MOON_MARE_ROCKS, GalacticraftBiomes.MOON_CHEESE_FOREST);
   private final int biomeSize;

   public MoonBiomeSource(long seed, int biomeSize) {
      super(BIOMES);
      this.biomeSize = biomeSize;
      this.seed = seed;

      this.sampler = MoonBiomeLayers.build(seed, biomeSize, 0);
   }

   @Override
   protected Codec<? extends BiomeSource> method_28442() {
      return CODEC;
   }

   @Environment(EnvType.CLIENT)
   public BiomeSource withSeed(long seed) {
      return new MoonBiomeSource(seed, this.biomeSize);
   }

   public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
      return this.sampler.sample(biomeX, biomeZ);
   }
}
