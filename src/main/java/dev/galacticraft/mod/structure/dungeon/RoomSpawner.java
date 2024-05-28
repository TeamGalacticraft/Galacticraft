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

import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.structure.GCStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.Random;

public class RoomSpawner extends RoomEmpty {
    public RoomSpawner(CompoundTag tag) {
        super(GCStructurePieceTypes.ROOM_SPAWNER, tag);
    }

    public RoomSpawner(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction entranceDir, int genDepth) {
        super(GCStructurePieceTypes.ROOM_SPAWNER, configuration, rand, blockPosX, blockPosZ, sizeX, sizeY, sizeZ, entranceDir, genDepth);
    }

    @Override
    public void postProcess(WorldGenLevel worldIn, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos pos, BlockPos pivot) {
        super.postProcess(worldIn, structureManager, chunkGenerator, random, boundingBox, pos, pivot);
//        if (super.postProcess(worldIn, structureManager, chunkGenerator, random, boundingBox, pos, pivot)) {
            for (int i = 1; i <= this.sizeX - 1; ++i) {
                for (int j = 1; j <= this.sizeY - 1; ++j) {
                    for (int k = 1; k <= this.sizeZ - 1; ++k) {
                        if (random.nextFloat() < 0.05F) {
                            this.placeBlock(worldIn, Blocks.COBWEB.defaultBlockState(), i, j, k, boundingBox);
                        }
                    }
                }
            }

            this.placeBlock(worldIn, Blocks.SPAWNER.defaultBlockState(), 1, 0, 1, boundingBox);
            this.placeBlock(worldIn, Blocks.SPAWNER.defaultBlockState(), this.sizeX - 1, 0, this.sizeZ - 1, boundingBox);

            BlockPos blockpos = new BlockPos(this.getWorldX(1, 1), this.getWorldY(0), this.getWorldZ(1, 1));
            Spawner spawner = (Spawner) worldIn.getBlockEntity(blockpos);

            if (spawner != null) {
                spawner.setEntityId(getMob(random), random);
            }

            blockpos = new BlockPos(this.getWorldX(this.sizeX - 1, this.sizeZ - 1), this.getWorldY(0), this.getWorldZ(this.sizeX - 1, this.sizeZ - 1));
            spawner = (Spawner) worldIn.getBlockEntity(blockpos);

            if (spawner != null) {
                spawner.setEntityId(getMob(random), random);
            }

//        }

    }

    private static EntityType<?> getMob(RandomSource rand) {
        switch (rand.nextInt(4)) {
            case 0:
                return GCEntityTypes.EVOLVED_SPIDER;
            case 1:
                return GCEntityTypes.EVOLVED_CREEPER;
            case 2:
                return GCEntityTypes.EVOLVED_SKELETON;
            case 3:
            default:
                return GCEntityTypes.EVOLVED_ZOMBIE;
        }
    }
}