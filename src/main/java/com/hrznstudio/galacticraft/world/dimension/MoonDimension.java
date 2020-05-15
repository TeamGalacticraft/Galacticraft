package com.hrznstudio.galacticraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

public class MoonDimension extends Dimension {

   public MoonDimension(World world, DimensionType type) {
      super(world, type, 0.0F);
   }

   @Nullable
   @Override
   public BlockPos getSpawningBlockInChunk(long l, ChunkPos chunkPos, boolean bl) {
      for(int i = chunkPos.getStartX(); i <= chunkPos.getEndX(); ++i) {
         for(int j = chunkPos.getStartZ(); j <= chunkPos.getEndZ(); ++j) {
            BlockPos blockPos = this.getTopSpawningBlockPosition(l, i, j, bl);
            if (blockPos != null) {
               return blockPos;
            }
         }
      }

      return null;
   }

   @Nullable
   @Override
   public BlockPos getTopSpawningBlockPosition(long l, int i, int j, boolean bl) {
      BlockPos.Mutable mutable = new BlockPos.Mutable(i, 0, j);
      Biome biome = this.world.getBiome(mutable);
      BlockState blockState = biome.getSurfaceConfig().getTopMaterial();
      if (bl && !blockState.getBlock().isIn(BlockTags.VALID_SPAWN)) {
         return null;
      } else {
         WorldChunk worldChunk = this.world.getChunk(i >> 4, j >> 4);
         int k = worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, i & 15, j & 15);
         if (k < 0) {
            return null;
         } else if (worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i & 15, j & 15) > worldChunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, i & 15, j & 15)) {
            return null;
         } else {
            for(int m = k + 1; m >= 0; --m) {
               mutable.set(i, m, j);
               BlockState blockState2 = this.world.getBlockState(mutable);
               if (!blockState2.getFluidState().isEmpty()) {
                  break;
               }

               if (blockState2.equals(blockState)) {
                  return mutable.up().toImmutable();
               }
            }

            return null;
         }
      }
   }

   @Override
   public float getSkyAngle(long timeOfDay, float tickDelta) {
      double d = MathHelper.fractionalPart((double) timeOfDay / 24000.0D - 0.25D);
      double e = 0.5D - Math.cos(d * Math.PI) / 2.0D;
      return (float) (d * 2.0D + e) / 3.0F;
   }

   @Override
   public int getMoonPhase(long time) {
      return 0; //gives the most difficulty
   }

   @Override
   public void update() {
      super.update();
      if (world instanceof ServerWorld && !world.isClient()) {
         ((ServerWorld) world).method_27910(Integer.MAX_VALUE, 0, false, false);
      }
   }

   @Override
   public boolean hasVisibleSky() {
      return true;
   }

   @Override
   public boolean canPlayersSleep() {
      return false;
   }

   @Override
   public DimensionType getType() {
      return GalacticraftDimensions.MOON;
   }
}
