/*
 * Copyright (c) 2019-2021 Team Galacticraft
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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.loot.GalacticraftLootTable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.*;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

import java.util.List;
import java.util.Random;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MoonRuinsGenerator {
   private static final Identifier[] PIECES = new Identifier[]{
           //new Identifier(Constants.MOD_ID, "moon_ruins/ruin_1"),
           //new Identifier(Constants.MOD_ID, "moon_ruins/ruin_2"),
           new Identifier(Constant.MOD_ID, "moon_ruins/ruin_3"),
           new Identifier(Constant.MOD_ID, "moon_ruins/ruin_4"),
           new Identifier(Constant.MOD_ID, "moon_ruins/ruin_5"),
           new Identifier(Constant.MOD_ID, "moon_ruins/ruin_6"),
   };

   private static Identifier getPiece(Random random) {
      return Util.getRandom(PIECES, random);
   }

   public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, StructurePiecesHolder structurePiecesHolder, Random random, DefaultFeatureConfig config) {
      addPieces(manager, pos, rotation, structurePiecesHolder, random);
      method_14825(manager, random, rotation, pos, structurePiecesHolder);
   }

   private static void method_14825(StructureManager manager, Random random, BlockRotation rotation, BlockPos pos, StructurePiecesHolder structurePiecesHolder) {
      BlockPos blockPos = new BlockPos(pos.getX(), 90, pos.getZ());
      BlockPos blockPos2 = Structure.transformAround(new BlockPos(15, 0, 15), BlockMirror.NONE, rotation, BlockPos.ORIGIN).add(blockPos);
      BlockBox blockBox = BlockBox.create(blockPos, blockPos2);
      BlockPos blockPos3 = new BlockPos(Math.min(blockPos.getX(), blockPos2.getX()), blockPos.getY(), Math.min(blockPos.getZ(), blockPos2.getZ()));
      List<BlockPos> list = getRoomPositions(random, blockPos3);
      int i = MathHelper.nextInt(random, 4, 8);

      for (int j = 0; j < i; ++j) {
         if (!list.isEmpty()) {
            int k = random.nextInt(list.size());
            BlockPos blockPos4 = list.remove(k);
            BlockRotation blockRotation = BlockRotation.random(random);
            BlockPos blockPos5 = Structure.transformAround(new BlockPos(5, 0, 6), BlockMirror.NONE, blockRotation, BlockPos.ORIGIN).add(blockPos4);
            BlockBox blockBox2 = BlockBox.create(blockPos4, blockPos5);
            if (!blockBox2.intersects(blockBox)) {
               addPieces(manager, blockPos4, blockRotation, structurePiecesHolder, random);
            }
         }
      }

   }

   private static List<BlockPos> getRoomPositions(Random random, BlockPos blockPos) {
      List<BlockPos> list = Lists.newArrayList();
      list.add(blockPos.add(-16 + MathHelper.nextInt(random, 1, 8), 0, 16 + MathHelper.nextInt(random, 1, 7)));
      list.add(blockPos.add(-16 + MathHelper.nextInt(random, 1, 8), 0, MathHelper.nextInt(random, 1, 7)));
      list.add(blockPos.add(-16 + MathHelper.nextInt(random, 1, 8), 0, -16 + MathHelper.nextInt(random, 4, 8)));
      list.add(blockPos.add(MathHelper.nextInt(random, 1, 7), 0, 16 + MathHelper.nextInt(random, 1, 7)));
      list.add(blockPos.add(MathHelper.nextInt(random, 1, 7), 0, -16 + MathHelper.nextInt(random, 4, 6)));
      list.add(blockPos.add(16 + MathHelper.nextInt(random, 1, 7), 0, 16 + MathHelper.nextInt(random, 3, 8)));
      list.add(blockPos.add(16 + MathHelper.nextInt(random, 1, 7), 0, MathHelper.nextInt(random, 1, 7)));
      list.add(blockPos.add(16 + MathHelper.nextInt(random, 1, 7), 0, -16 + MathHelper.nextInt(random, 4, 8)));
      return list;
   }

   private static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, StructurePiecesHolder structurePiecesHolder, Random random) {
      structurePiecesHolder.addPiece(new Piece(manager, getPiece(random), pos, rotation, 0.8F));
      structurePiecesHolder.addPiece(new Piece(manager, getPiece(random), pos, rotation, 0.7F));
      structurePiecesHolder.addPiece(new Piece(manager, getPiece(random), pos, rotation, 0.65F));
   }

   public static class Piece extends SimpleStructurePiece {
      private final float integrity;

      public Piece(StructureManager structureManager, Identifier template, BlockPos pos, BlockRotation rotation, float integrity) {
         super(StructurePieceType.OCEAN_TEMPLE, 0, structureManager, template, template.toString(), method_35446(rotation), pos);
         this.integrity = integrity;
      }

      public Piece(ServerWorld world, NbtCompound nbt) {
         super(GalacticraftStructure.MOON_RUINS_PIECE, nbt, world, (identifier) -> method_35446(BlockRotation.valueOf(nbt.getString("Rot"))));
         this.integrity = nbt.getFloat("Integrity");
      }

      private static StructurePlacementData method_35446(BlockRotation blockRotation) {
         return (new StructurePlacementData()).setRotation(blockRotation).setMirror(BlockMirror.NONE).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
      }

      @Override
      protected void writeNbt(ServerWorld world, NbtCompound nbt) {
         super.writeNbt(world, nbt);
         nbt.putString("Rot", this.placementData.getRotation().name());
         nbt.putFloat("Integrity", this.integrity);
      }

      @Override
      protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
         if ("chest".equals(metadata)) {
            world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.WATERLOGGED, world.getFluidState(pos).isIn(FluidTags.WATER)), Block.NOTIFY_LISTENERS);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ChestBlockEntity chest) {
               chest.setLootTable(GalacticraftLootTable.BASIC_MOON_RUINS_CHEST, random.nextLong());
            }
         } else if ("drowned".equals(metadata)) {
            DrownedEntity drownedEntity = EntityType.DROWNED.create(world.toServerWorld());
            assert drownedEntity != null;
            drownedEntity.setPersistent();
            drownedEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
            drownedEntity.initialize(world, world.getLocalDifficulty(pos), SpawnReason.STRUCTURE, null, null);
            world.spawnEntityAndPassengers(drownedEntity);
            if (pos.getY() > world.getSeaLevel()) {
               world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
            } else {
               world.setBlockState(pos, Blocks.WATER.getDefaultState(), Block.NOTIFY_LISTENERS);
            }
         }

      }

      @Override
      public boolean generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
         this.placementData.clearProcessors().addProcessor(new BlockRotStructureProcessor(this.integrity)).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
         int i = world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, this.pos.getX(), this.pos.getZ());
         this.pos = new BlockPos(this.pos.getX(), i, this.pos.getZ());
         BlockPos blockPos = Structure.transformAround(new BlockPos(this.structure.getSize().getX() - 1, 0, this.structure.getSize().getZ() - 1), BlockMirror.NONE, this.placementData.getRotation(), BlockPos.ORIGIN).add(this.pos);
         this.pos = new BlockPos(this.pos.getX(), this.method_14829(this.pos, world, blockPos), this.pos.getZ());
         return super.generate(world, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, pos);
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

            for (FluidState fluidState = blockView.getFluidState(mutable); (blockState.isAir() || fluidState.isIn(FluidTags.WATER) || blockState.isIn(BlockTags.ICE)) && o > blockView.getBottomY() + 1; fluidState = blockView.getFluidState(mutable)) {
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
