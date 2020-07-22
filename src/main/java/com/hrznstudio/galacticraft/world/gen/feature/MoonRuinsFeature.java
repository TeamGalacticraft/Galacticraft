package com.hrznstudio.galacticraft.world.gen.feature;

import com.hrznstudio.galacticraft.structure.MoonRuinsGenerator;
import com.mojang.serialization.Codec;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class MoonRuinsFeature extends StructureFeature<DefaultFeatureConfig> {
   public MoonRuinsFeature(Codec<DefaultFeatureConfig> codec) {
      super(codec);
   }

   public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
      return MoonRuinsFeature.Start::new;
   }

   public static class Start extends StructureStart<DefaultFeatureConfig> {
      public Start(StructureFeature<DefaultFeatureConfig> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
         super(structureFeature, i, j, blockBox, k, l);
      }

      public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, int i, int j, Biome biome, DefaultFeatureConfig config) {
         int k = i * 16;
         int l = j * 16;
         BlockPos blockPos = new BlockPos(k, 90, l);
         BlockRotation blockRotation = BlockRotation.random(this.random);
         MoonRuinsGenerator.addPieces(structureManager, blockPos, blockRotation, this.children, this.random, config);
         this.setBoundingBoxFromChildren();
      }
   }
}
