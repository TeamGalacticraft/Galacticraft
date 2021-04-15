/*
 * Copyright (c) 2020 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.structure;

import com.google.common.collect.Lists;
import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.entity.GalacticraftEntityTypes;
import dev.galacticraft.mod.loot.GalacticraftLootTables;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.*;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import java.util.List;
import java.util.Random;

public class MoonRuinsGenerator {
   private static final Identifier[] PIECES = new Identifier[] {
//           new Identifier(Constants.MOD_ID, "moon_ruins/ruin_1"),
//           new Identifier(Constants.MOD_ID, "moon_ruins/ruin_2"),
           new Identifier(Constants.MOD_ID, "moon_ruins/ruin_3"),
           new Identifier(Constants.MOD_ID, "moon_ruins/ruin_4"),
           new Identifier(Constants.MOD_ID, "moon_ruins/ruin_5"),
           new Identifier(Constants.MOD_ID, "moon_ruins/ruin_6"),
   };

   private static Identifier getPiece(Random random) {
      return Util.getRandom(PIECES, random);
   }

   public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, List<StructurePiece> pieces, Random random, DefaultFeatureConfig config) {
      method_14822(manager, pos, rotation, pieces, random, config);
      method_14825(manager, random, rotation, pos, config, pieces);
   }

   private static void method_14825(StructureManager manager, Random random, BlockRotation rotation, BlockPos pos, DefaultFeatureConfig config, List<StructurePiece> pieces) {
      int i = pos.getX();
      int j = pos.getZ();
      BlockPos blockPos = Structure.transformAround(new BlockPos(15, 0, 15), BlockMirror.NONE, rotation, BlockPos.ORIGIN).add(i, 0, j);
      BlockBox blockBox = BlockBox.create(i, 0, j, blockPos.getX(), 0, blockPos.getZ());
      BlockPos blockPos2 = new BlockPos(Math.min(i, blockPos.getX()), 0, Math.min(j, blockPos.getZ()));
      List<BlockPos> list = getRoomPositions(random, blockPos2.getX(), blockPos2.getZ());
      int k = MathHelper.nextInt(random, 4, 8);

      for(int l = 0; l < k; ++l) {
         if (!list.isEmpty()) {
            int m = random.nextInt(list.size());
            BlockPos blockPos3 = list.remove(m);
            int n = blockPos3.getX();
            int o = blockPos3.getZ();
            BlockRotation blockRotation = BlockRotation.random(random);
            BlockPos blockPos4 = Structure.transformAround(new BlockPos(5, 0, 6), BlockMirror.NONE, blockRotation, BlockPos.ORIGIN).add(n, 0, o);
            BlockBox blockBox2 = BlockBox.create(n, 0, o, blockPos4.getX(), 0, blockPos4.getZ());
            if (!blockBox2.intersects(blockBox)) {
               method_14822(manager, blockPos3, blockRotation, pieces, random, config);
            }
         }
      }

   }

   private static List<BlockPos> getRoomPositions(Random random, int x, int z) {
      List<BlockPos> list = Lists.newArrayList();
      list.add(new BlockPos(x - 16 + MathHelper.nextInt(random, 1, 8), 90, z + 16 + MathHelper.nextInt(random, 1, 7)));
      list.add(new BlockPos(x - 16 + MathHelper.nextInt(random, 1, 8), 90, z + MathHelper.nextInt(random, 1, 7)));
      list.add(new BlockPos(x - 16 + MathHelper.nextInt(random, 1, 8), 90, z - 16 + MathHelper.nextInt(random, 4, 8)));
      list.add(new BlockPos(x + MathHelper.nextInt(random, 1, 7), 90, z + 16 + MathHelper.nextInt(random, 1, 7)));
      list.add(new BlockPos(x + MathHelper.nextInt(random, 1, 7), 90, z - 16 + MathHelper.nextInt(random, 4, 6)));
      list.add(new BlockPos(x + 16 + MathHelper.nextInt(random, 1, 7), 90, z + 16 + MathHelper.nextInt(random, 3, 8)));
      list.add(new BlockPos(x + 16 + MathHelper.nextInt(random, 1, 7), 90, z + MathHelper.nextInt(random, 1, 7)));
      list.add(new BlockPos(x + 16 + MathHelper.nextInt(random, 1, 7), 90, z - 16 + MathHelper.nextInt(random, 4, 8)));
      return list;
   }

   private static void method_14822(StructureManager manager, BlockPos pos, BlockRotation rotation, List<StructurePiece> pieces, Random random, DefaultFeatureConfig config) {
      pieces.add(new Piece(manager, getPiece(random), pos, rotation, 0.8F));
      pieces.add(new Piece(manager, getPiece(random), pos, rotation, 0.7F));
      pieces.add(new Piece(manager, getPiece(random), pos, rotation, 0.65F));
   }

   public static class Piece extends SimpleStructurePiece {
      private final float integrity;
      private final Identifier template;
      private final BlockRotation rotation;

      public Piece(StructureManager structureManager, Identifier template, BlockPos pos, BlockRotation rotation, float integrity) {
         super(GalacticraftStructures.MOON_RUINS_PIECE, 0);
         this.template = template;
         this.pos = pos;
         this.rotation = rotation;
         this.integrity = integrity;
         this.initialize(structureManager);
      }

      public Piece(StructureManager manager, CompoundTag tag) {
         super(GalacticraftStructures.MOON_RUINS_PIECE, tag);
         this.template = new Identifier(tag.getString("Template"));
         this.rotation = BlockRotation.valueOf(tag.getString("Rot"));
         this.integrity = tag.getFloat("Integrity");
         this.initialize(manager);
      }

      private void initialize(StructureManager structureManager) {
         Structure structure = structureManager.getStructureOrBlank(this.template);
         StructurePlacementData structurePlacementData = (new StructurePlacementData()).setRotation(this.rotation).setMirror(BlockMirror.NONE).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
         this.setStructureData(structure, this.pos, structurePlacementData);
      }

      @Override
      protected void toNbt(CompoundTag tag) {
         super.toNbt(tag);
         tag.putString("Template", this.template.toString());
         tag.putString("Rot", this.rotation.name());
         tag.putFloat("Integrity", this.integrity);
      }

      @Override
      protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess arg, Random random, BlockBox boundingBox) {
         if ("chest".equals(metadata)) {
            BlockEntity blockEntity = arg.getBlockEntity(pos.offset(Direction.DOWN));
            if (blockEntity instanceof ChestBlockEntity) {
               ((ChestBlockEntity)blockEntity).setLootTable(GalacticraftLootTables.BASIC_MOON_RUINS_CHEST, random.nextLong());
            }
         } else if ("monster".equals(metadata)) {
            MobEntity entity;
            int i = arg.getRandom().nextInt(2);
            if (i == 0) {
               entity = GalacticraftEntityTypes.EVOLVED_ZOMBIE.create(arg.toServerWorld());
            } else {
               entity = GalacticraftEntityTypes.EVOLVED_CREEPER.create(arg.toServerWorld());
            }
            assert entity != null;
            entity.setPersistent();
            entity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
            entity.initialize(arg, arg.getLocalDifficulty(pos), SpawnReason.STRUCTURE, null, null);
            arg.spawnEntityAndPassengers(entity);
            arg.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
         }
      }

      @Override
      public boolean generate(StructureWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
         this.placementData.clearProcessors().addProcessor(new BlockRotStructureProcessor(this.integrity)).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
         int i = serverWorldAccess.getTopY(Heightmap.Type.WORLD_SURFACE_WG, this.pos.getX(), this.pos.getZ());
         this.pos = new BlockPos(this.pos.getX(), i, this.pos.getZ());
         BlockPos blockPos2 = Structure.transformAround(new BlockPos(this.structure.getSize().getX() - 1, 0, this.structure.getSize().getZ() - 1), BlockMirror.NONE, this.rotation, BlockPos.ORIGIN).add(this.pos);
         this.pos = new BlockPos(this.pos.getX(), this.method_14829(this.pos, serverWorldAccess, blockPos2), this.pos.getZ());
         return super.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, blockPos);
      }

      private int method_14829(BlockPos blockPos, BlockView blockView, BlockPos blockPos2) {
         int i = blockPos.getY();
         int j = 512;
         int k = i - 1;
         int l = 0;

         for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
            int m = blockPos3.getX();
            int n = blockPos3.getZ();
            int o = blockPos.getY() - 1;
            BlockPos.Mutable mutable = new BlockPos.Mutable(m, o, n);
            BlockState blockState = blockView.getBlockState(mutable);

            for (FluidState fluidState = blockView.getFluidState(mutable); (blockState.isAir() || fluidState.isIn(FluidTags.WATER) || blockState.getBlock().isIn(BlockTags.ICE)) && o > 1; fluidState = blockView.getFluidState(mutable)) {
               --o;
               mutable.set(m, o, n);
               blockState = blockView.getBlockState(mutable);
            }

            j = Math.min(j, o);
            if (o < k - 2) {
               ++l;
            }
         }

         int p = Math.abs(blockPos.getX() - blockPos2.getX());
         if (k - j > 2 && l > p - 2) {
            i = j + 1;
         }

         return i;
      }
   }
}
