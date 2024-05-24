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

package dev.galacticraft.mod.structure.dungeon;

import dev.galacticraft.mod.structure.GCStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class RoomChest extends RoomEmpty {
    public RoomChest(CompoundTag tag) {
        super(GCStructurePieceTypes.ROOM_CHEST, tag);
    }

    public RoomChest(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction entranceDir, int genDepth) {
        super(GCStructurePieceTypes.ROOM_CHEST, configuration, rand, blockPosX, blockPosZ, sizeX, sizeY, sizeZ, entranceDir, genDepth);
    }

    @Override
    public void postProcess(WorldGenLevel worldIn, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource rand, BoundingBox boundingBox, ChunkPos pos, BlockPos pivot) {
        super.postProcess(worldIn, structureManager, chunkGenerator, rand, boundingBox, pos, pivot);
//        if (super.postProcess(worldIn, structureManager, chunkGenerator, rand, boundingBox, pos, pivot)) {
            int chestX = this.sizeX / 2;
            int chestY = 1;
            int chestZ = this.sizeZ / 2;
            this.placeBlock(worldIn, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, this.getDirection().getOpposite()), chestX, chestY, chestZ, boundingBox);

            BlockPos blockpos = new BlockPos(this.getWorldX(chestX, chestZ), this.getWorldY(chestY), this.getWorldZ(chestX, chestZ));
            ChestBlockEntity chest = (ChestBlockEntity) worldIn.getBlockEntity(blockpos);

            if (chest != null) {
                ResourceLocation chesttype = RoomTreasure.MOONCHEST;
//                if (worldIn.provider instanceof IGalacticraftWorldProvider) {
//                    chesttype = ((IGalacticraftWorldProvider) worldIn.provider).getDungeonChestType();
//                }
                chest.setLootTable(chesttype, rand.nextLong());
            }

//        }
    }
}