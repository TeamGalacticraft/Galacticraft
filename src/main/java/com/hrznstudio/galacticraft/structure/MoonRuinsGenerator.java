/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.structure;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.hrznstudio.galacticraft.loot.GalacticraftLootTables;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import java.util.List;
import java.util.Random;

public class MoonRuinsGenerator {
   private static final ResourceLocation[] PIECES = new ResourceLocation[] {
//           new Identifier(Constants.MOD_ID, "moon_ruins/ruin_1"),
//           new Identifier(Constants.MOD_ID, "moon_ruins/ruin_2"),
           new ResourceLocation(Constants.MOD_ID, "moon_ruins/ruin_3"),
           new ResourceLocation(Constants.MOD_ID, "moon_ruins/ruin_4"),
           new ResourceLocation(Constants.MOD_ID, "moon_ruins/ruin_5"),
           new ResourceLocation(Constants.MOD_ID, "moon_ruins/ruin_6"),
   };

   private static ResourceLocation getPiece(Random random) {
      return Util.getRandom(PIECES, random);
   }

   public static void addPieces(StructureManager manager, BlockPos pos, Rotation rotation, List<StructurePiece> pieces, Random random, NoneFeatureConfiguration config) {
      method_14822(manager, pos, rotation, pieces, random, config);
      method_14825(manager, random, rotation, pos, config, pieces);
   }

   private static void method_14825(StructureManager manager, Random random, Rotation rotation, BlockPos pos, NoneFeatureConfiguration config, List<StructurePiece> pieces) {
      int i = pos.getX();
      int j = pos.getZ();
      BlockPos blockPos = StructureTemplate.transform(new BlockPos(15, 0, 15), Mirror.NONE, rotation, BlockPos.ZERO).offset(i, 0, j);
      BoundingBox blockBox = BoundingBox.createProper(i, 0, j, blockPos.getX(), 0, blockPos.getZ());
      BlockPos blockPos2 = new BlockPos(Math.min(i, blockPos.getX()), 0, Math.min(j, blockPos.getZ()));
      List<BlockPos> list = getRoomPositions(random, blockPos2.getX(), blockPos2.getZ());
      int k = Mth.nextInt(random, 4, 8);

      for(int l = 0; l < k; ++l) {
         if (!list.isEmpty()) {
            int m = random.nextInt(list.size());
            BlockPos blockPos3 = list.remove(m);
            int n = blockPos3.getX();
            int o = blockPos3.getZ();
            Rotation blockRotation = Rotation.getRandom(random);
            BlockPos blockPos4 = StructureTemplate.transform(new BlockPos(5, 0, 6), Mirror.NONE, blockRotation, BlockPos.ZERO).offset(n, 0, o);
            BoundingBox blockBox2 = BoundingBox.createProper(n, 0, o, blockPos4.getX(), 0, blockPos4.getZ());
            if (!blockBox2.intersects(blockBox)) {
               method_14822(manager, blockPos3, blockRotation, pieces, random, config);
            }
         }
      }

   }

   private static List<BlockPos> getRoomPositions(Random random, int x, int z) {
      List<BlockPos> list = Lists.newArrayList();
      list.add(new BlockPos(x - 16 + Mth.nextInt(random, 1, 8), 90, z + 16 + Mth.nextInt(random, 1, 7)));
      list.add(new BlockPos(x - 16 + Mth.nextInt(random, 1, 8), 90, z + Mth.nextInt(random, 1, 7)));
      list.add(new BlockPos(x - 16 + Mth.nextInt(random, 1, 8), 90, z - 16 + Mth.nextInt(random, 4, 8)));
      list.add(new BlockPos(x + Mth.nextInt(random, 1, 7), 90, z + 16 + Mth.nextInt(random, 1, 7)));
      list.add(new BlockPos(x + Mth.nextInt(random, 1, 7), 90, z - 16 + Mth.nextInt(random, 4, 6)));
      list.add(new BlockPos(x + 16 + Mth.nextInt(random, 1, 7), 90, z + 16 + Mth.nextInt(random, 3, 8)));
      list.add(new BlockPos(x + 16 + Mth.nextInt(random, 1, 7), 90, z + Mth.nextInt(random, 1, 7)));
      list.add(new BlockPos(x + 16 + Mth.nextInt(random, 1, 7), 90, z - 16 + Mth.nextInt(random, 4, 8)));
      return list;
   }

   private static void method_14822(StructureManager manager, BlockPos pos, Rotation rotation, List<StructurePiece> pieces, Random random, NoneFeatureConfiguration config) {
      pieces.add(new Piece(manager, getPiece(random), pos, rotation, 0.8F));
      pieces.add(new Piece(manager, getPiece(random), pos, rotation, 0.7F));
      pieces.add(new Piece(manager, getPiece(random), pos, rotation, 0.65F));
   }

   public static class Piece extends TemplateStructurePiece {
      private final float integrity;
      private final ResourceLocation template;
      private final Rotation rotation;

      public Piece(StructureManager structureManager, ResourceLocation template, BlockPos pos, Rotation rotation, float integrity) {
         super(GalacticraftStructures.MOON_RUINS_PIECE, 0);
         this.template = template;
         this.templatePosition = pos;
         this.rotation = rotation;
         this.integrity = integrity;
         this.initialize(structureManager);
      }

      public Piece(StructureManager manager, CompoundTag tag) {
         super(GalacticraftStructures.MOON_RUINS_PIECE, tag);
         this.template = new ResourceLocation(tag.getString("Template"));
         this.rotation = Rotation.valueOf(tag.getString("Rot"));
         this.integrity = tag.getFloat("Integrity");
         this.initialize(manager);
      }

      private void initialize(StructureManager structureManager) {
         StructureTemplate structure = structureManager.getOrCreate(this.template);
         StructurePlaceSettings structurePlacementData = (new StructurePlaceSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
         this.setup(structure, this.templatePosition, structurePlacementData);
      }

      @Override
      protected void addAdditionalSaveData(CompoundTag tag) {
         super.addAdditionalSaveData(tag);
         tag.putString("Template", this.template.toString());
         tag.putString("Rot", this.rotation.name());
         tag.putFloat("Integrity", this.integrity);
      }

      @Override
      protected void handleDataMarker(String metadata, BlockPos pos, ServerLevelAccessor arg, Random random, BoundingBox boundingBox) {
         if ("chest".equals(metadata)) {
            BlockEntity blockEntity = arg.getBlockEntity(pos.relative(Direction.DOWN));
            if (blockEntity instanceof ChestBlockEntity) {
               ((ChestBlockEntity)blockEntity).setLootTable(GalacticraftLootTables.BASIC_MOON_RUINS_CHEST, random.nextLong());
            }
         } else if ("monster".equals(metadata)) {
            Mob entity;
            int i = arg.getRandom().nextInt(2);
            if (i == 0) {
               entity = GalacticraftEntityTypes.EVOLVED_ZOMBIE.create(arg.getLevel());
            } else {
               entity = GalacticraftEntityTypes.EVOLVED_CREEPER.create(arg.getLevel());
            }
            assert entity != null;
            entity.setPersistenceRequired();
            entity.moveTo(pos, 0.0F, 0.0F);
            entity.finalizeSpawn(arg, arg.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null, null);
            arg.addFreshEntityWithPassengers(entity);
            arg.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
         }
      }

      @Override
      public boolean postProcess(WorldGenLevel serverWorldAccess, StructureFeatureManager structureAccessor, ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
         this.placeSettings.clearProcessors().addProcessor(new BlockRotProcessor(this.integrity)).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
         int i = serverWorldAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, this.templatePosition.getX(), this.templatePosition.getZ());
         this.templatePosition = new BlockPos(this.templatePosition.getX(), i, this.templatePosition.getZ());
         BlockPos blockPos2 = StructureTemplate.transform(new BlockPos(super.template.getSize().getX() - 1, 0, super.template.getSize().getZ() - 1), Mirror.NONE, this.rotation, BlockPos.ZERO).offset(this.templatePosition);
         this.templatePosition = new BlockPos(this.templatePosition.getX(), this.method_14829(this.templatePosition, serverWorldAccess, blockPos2), this.templatePosition.getZ());
         return super.postProcess(serverWorldAccess, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, blockPos);
      }

      private int method_14829(BlockPos blockPos, BlockGetter blockView, BlockPos blockPos2) {
         int i = blockPos.getY();
         int j = 512;
         int k = i - 1;
         int l = 0;

         for (BlockPos blockPos3 : BlockPos.betweenClosed(blockPos, blockPos2)) {
            int m = blockPos3.getX();
            int n = blockPos3.getZ();
            int o = blockPos.getY() - 1;
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(m, o, n);
            BlockState blockState = blockView.getBlockState(mutable);

            for (FluidState fluidState = blockView.getFluidState(mutable); (blockState.isAir() || fluidState.is(FluidTags.WATER) || blockState.getBlock().is(BlockTags.ICE)) && o > 1; fluidState = blockView.getFluidState(mutable)) {
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
