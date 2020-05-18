package com.hrznstudio.galacticraft.world.gen.surfacebuilder;

import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import java.util.Random;
import java.util.function.Function;

public class MoonSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
   public MoonSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
      super(function);
   }

   public void generate(Random random, Chunk chunk, Biome biome, int x, int z, int height, double noise, BlockState defaultBlock, BlockState fluidBlock, int seaLevel, long seed, TernarySurfaceConfig ternarySurfaceConfig) {
      BlockState blockState = ternarySurfaceConfig.getTopMaterial();
      BlockState blockState2 = ternarySurfaceConfig.getUnderMaterial();
      BlockPos.Mutable mutable = new BlockPos.Mutable();
      int i = -1;
      int j = (int)(noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
      int k = x & 15;
      int l = z & 15;

      for(int m = height; m >= 0; --m) {
         mutable.set(k, m, l);
         BlockState blockState3 = chunk.getBlockState(mutable);
         if (blockState3.isAir()) {
            i = -1;
         } else if (blockState3.isOf(defaultBlock.getBlock())) {
            if (i == -1) {
               if (j <= 0) {
                  blockState = Blocks.AIR.getDefaultState();
                  blockState2 = defaultBlock;
               } else if (m >= seaLevel - 4 && m <= seaLevel + 1) {
                  blockState = ternarySurfaceConfig.getTopMaterial();
                  blockState2 = ternarySurfaceConfig.getUnderMaterial();
               }

               if (m < seaLevel && (blockState == null || blockState.isAir())) {
                  if (biome.getTemperature(mutable.set(x, m, z)) < 0.15F) {
                     blockState = Blocks.ICE.getDefaultState();
                  } else {
                     blockState = fluidBlock;
                  }

                  mutable.set(k, m, l);
               }

               i = j;
               if (m >= seaLevel - 1) {
                  chunk.setBlockState(mutable, blockState, false);
               } else if (m < seaLevel - 7 - j) {
                  blockState = Blocks.AIR.getDefaultState();
                  blockState2 = defaultBlock;
                  chunk.setBlockState(mutable, ternarySurfaceConfig.getUnderwaterMaterial(), false);
               } else {
                  chunk.setBlockState(mutable, blockState2, false);
               }
            } else if (i > 0) {
               --i;
               chunk.setBlockState(mutable, blockState2, false);
               if (i == 0 && blockState2.isOf(GalacticraftBlocks.MOON_TURF) && j > 1) {
                  i = random.nextInt(4) + Math.max(0, m - 63);
                  blockState2 = GalacticraftBlocks.MOON_ROCK.getDefaultState();
               }
            }
         }
      }
   }
}
