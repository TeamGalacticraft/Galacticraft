/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCLootTables;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayList;
import java.util.List;

public class MoonRuinsGenerator {
   private static final ResourceLocation[] PIECES = new ResourceLocation[]{
           //Constant.id("moon_ruins/ruin_1"),
           //Constant.id("moon_ruins/ruin_2"),
           Constant.id("moon_ruins/ruin_3"),
           Constant.id("moon_ruins/ruin_4"),
           Constant.id("moon_ruins/ruin_5"),
           Constant.id("moon_ruins/ruin_6"),
   };

   private static ResourceLocation getPiece(RandomSource random) {
      return Util.getRandom(PIECES, random);
   }

   public static void addPiecesToStructure(StructureTemplateManager manager, BlockPos pos, Rotation rotation, StructurePieceAccessor structurePiecesHolder, RandomSource random) {
      addPieces(manager, pos, rotation, structurePiecesHolder, random);
      method_14825(manager, random, rotation, pos, structurePiecesHolder);
   }

   private static void method_14825(StructureTemplateManager manager, RandomSource random, Rotation rotation, BlockPos pos, StructurePieceAccessor structurePiecesHolder) {
      BlockPos blockPos = new BlockPos(pos.getX(), 90, pos.getZ());
      BlockPos blockPos2 = StructureTemplate.transform(new BlockPos(15, 0, 15), Mirror.NONE, rotation, BlockPos.ZERO).offset(blockPos);
      BoundingBox blockBox = BoundingBox.fromCorners(blockPos, blockPos2);
      BlockPos blockPos3 = new BlockPos(Math.min(blockPos.getX(), blockPos2.getX()), blockPos.getY(), Math.min(blockPos.getZ(), blockPos2.getZ()));
      List<BlockPos> list = getRoomPositions(random, blockPos3);
      int i = Mth.nextInt(random, 4, 8);

      for (int j = 0; j < i; ++j) {
         if (!list.isEmpty()) {
            int k = random.nextInt(list.size());
            BlockPos blockPos4 = list.remove(k);
            Rotation blockRotation = Rotation.getRandom(random);
            BlockPos blockPos5 = StructureTemplate.transform(new BlockPos(5, 0, 6), Mirror.NONE, blockRotation, BlockPos.ZERO).offset(blockPos4);
            BoundingBox blockBox2 = BoundingBox.fromCorners(blockPos4, blockPos5);
            if (!blockBox2.intersects(blockBox)) {
               addPieces(manager, blockPos4, blockRotation, structurePiecesHolder, random);
            }
         }
      }

   }

   private static List<BlockPos> getRoomPositions(RandomSource random, BlockPos blockPos) {
      List<BlockPos> list = new ArrayList<>(8);
      list.add(blockPos.offset(-16 + Mth.nextInt(random, 1, 8), 0, 16 + Mth.nextInt(random, 1, 7)));
      list.add(blockPos.offset(-16 + Mth.nextInt(random, 1, 8), 0, Mth.nextInt(random, 1, 7)));
      list.add(blockPos.offset(-16 + Mth.nextInt(random, 1, 8), 0, -16 + Mth.nextInt(random, 4, 8)));
      list.add(blockPos.offset(Mth.nextInt(random, 1, 7), 0, 16 + Mth.nextInt(random, 1, 7)));
      list.add(blockPos.offset(Mth.nextInt(random, 1, 7), 0, -16 + Mth.nextInt(random, 4, 6)));
      list.add(blockPos.offset(16 + Mth.nextInt(random, 1, 7), 0, 16 + Mth.nextInt(random, 3, 8)));
      list.add(blockPos.offset(16 + Mth.nextInt(random, 1, 7), 0, Mth.nextInt(random, 1, 7)));
      list.add(blockPos.offset(16 + Mth.nextInt(random, 1, 7), 0, -16 + Mth.nextInt(random, 4, 8)));
      return list;
   }

   private static void addPieces(StructureTemplateManager manager, BlockPos pos, Rotation rotation, StructurePieceAccessor structurePiecesHolder, RandomSource random) {
      structurePiecesHolder.addPiece(new Piece(manager, getPiece(random), pos, rotation, 0.8F));
      structurePiecesHolder.addPiece(new Piece(manager, getPiece(random), pos, rotation, 0.7F));
      structurePiecesHolder.addPiece(new Piece(manager, getPiece(random), pos, rotation, 0.65F));
   }

   public static class Piece extends TemplateStructurePiece {
      private final float integrity;

      public Piece(StructureTemplateManager structureManager, ResourceLocation template, BlockPos pos, Rotation rotation, float integrity) {
         super(GCStructurePieceTypes.MOON_RUINS_PIECE, 0, structureManager, template, template.toString(), method_35446(rotation), pos);
         this.integrity = integrity;
      }

      public Piece(StructureTemplateManager structureManager, CompoundTag nbt) {
         super(GCStructurePieceTypes.MOON_RUINS_PIECE, nbt, structureManager, (identifier) -> method_35446(Rotation.valueOf(nbt.getString("Rot"))));
         this.integrity = nbt.getFloat("Integrity");
      }

      private static StructurePlaceSettings method_35446(Rotation blockRotation) {
         return new StructurePlaceSettings().setRotation(blockRotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
      }

      @Override
      protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag nbt) {
         super.addAdditionalSaveData(context, nbt);
         nbt.putString("Rot", this.placeSettings.getRotation().name());
         nbt.putFloat("Integrity", this.integrity);
      }

      @Override
      protected void handleDataMarker(String metadata, BlockPos pos, ServerLevelAccessor world, RandomSource random, BoundingBox boundingBox) {
         if ("chest".equals(metadata)) {
            world.setBlock(pos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, world.getFluidState(pos).is(FluidTags.WATER)), Block.UPDATE_CLIENTS);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ChestBlockEntity chest) {
               chest.setLootTable(GCLootTables.BASIC_MOON_RUINS_CHEST, random.nextLong());
            }
         }
      }

      @Override
      public void postProcess(WorldGenLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
         this.placeSettings.clearProcessors().addProcessor(new BlockRotProcessor(this.integrity)).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
         int i = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, this.templatePosition.getX(), this.templatePosition.getZ());
         this.templatePosition = new BlockPos(this.templatePosition.getX(), i, this.templatePosition.getZ());
         BlockPos blockPos = StructureTemplate.transform(new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1), Mirror.NONE, this.placeSettings.getRotation(), BlockPos.ZERO).offset(this.templatePosition);
         this.templatePosition = new BlockPos(this.templatePosition.getX(), this.method_14829(this.templatePosition, world, blockPos), this.templatePosition.getZ());

         super.postProcess(world, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, pos);
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

            for (FluidState fluidState = blockView.getFluidState(mutable); (blockState.isAir() || fluidState.is(FluidTags.WATER) || blockState.is(BlockTags.ICE)) && o > blockView.getMinBuildHeight() + 1; fluidState = blockView.getFluidState(mutable)) {
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
