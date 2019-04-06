package com.legacy.aether.world.gen.chunk;

import io.github.teamgalacticraft.galacticraft.world.gen.chunk.MoonChunkGenerator;
import io.github.teamgalacticraft.galacticraft.world.gen.chunk.MoonChunkGeneratorConfig;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class MoonChunkGeneratorType extends ChunkGeneratorType {
   public MoonChunkGeneratorType() {
      super(null, true, MoonChunkGeneratorConfig::new);
   }

   public MoonChunkGenerator create(World worldIn, BiomeSource biomeSourceIn, MoonChunkGeneratorConfig settingsIn) {
      return new MoonChunkGenerator(worldIn, biomeSourceIn, settingsIn);
   }
}
