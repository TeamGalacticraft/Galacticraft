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

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.block.entity.DungeonSpawnerBlockEntity;
import dev.galacticraft.mod.structure.GCStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class RoomBoss extends SizedPiece {
    private BlockPos chestPos;

    public RoomBoss(CompoundTag tag) {
        super(GCStructurePieceTypes.ROOM_BOSS, tag);
    }

    public RoomBoss(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, Direction entranceDir, int genDepth) {
        this(configuration, rand, blockPosX, blockPosZ, rand.nextInt(6) + 14, rand.nextInt(2) + 8, rand.nextInt(6) + 14, entranceDir, genDepth);
    }

    public RoomBoss(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction entranceDir, int genDepth) {
        super(GCStructurePieceTypes.ROOM_BOSS, configuration, sizeX, sizeY, sizeZ, entranceDir.getOpposite(), genDepth, new BoundingBox(blockPosX, configuration.getYPosition(), blockPosZ, blockPosX + sizeX, configuration.getYPosition() + sizeY, blockPosZ + sizeZ));
        this.setOrientation(Direction.SOUTH);
        this.sizeX = sizeX;
        this.sizeZ = sizeZ;
        this.sizeY = sizeY;
    }

    @Override
    public void postProcess(WorldGenLevel worldIn, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox chunkBox, ChunkPos pos, BlockPos pivot) {
        for (int x = 0; x <= this.sizeX; x++) {
            for (int y = 0; y <= this.sizeY; y++) {
                for (int z = 0; z <= this.sizeZ; z++) {
                    if (x == 0 || x == this.sizeX || y == 0 || y == this.sizeY || z == 0 || z == this.sizeZ) {
                        boolean placeBlock = true;
                        if (getDirection().getAxis() == Direction.Axis.Z) {
                            int start = (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 - 1;
                            int end = (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 + 1;
                            if (x > start && x <= end && y < 3 && y > 0) {
                                if (getDirection() == Direction.SOUTH && z == 0) {
                                    placeBlock = false;
                                } else if (getDirection() == Direction.NORTH && z == this.sizeZ) {
                                    placeBlock = false;
                                }
                            }
                        } else {
                            int start = (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 - 1;
                            int end = (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 + 1;
                            if (z > start && z <= end && y < 3 && y > 0) {
                                if (getDirection() == Direction.EAST && x == 0) {
                                    placeBlock = false;
                                } else if (getDirection() == Direction.WEST && x == this.sizeX) {
                                    placeBlock = false;
                                }
                            }
                        }
                        if (placeBlock) {
                            this.placeBlock(worldIn, this.configuration.getBrickBlock(), x, y, z, chunkBox);
                        } else {
                            this.placeBlock(worldIn, Blocks.AIR.defaultBlockState(), x, y, z, chunkBox);
                        }
                    } else if ((x == 1 && z == 1) || (x == 1 && z == this.sizeZ - 1) || (x == this.sizeX - 1 && z == 1) || (x == this.sizeX - 1 && z == this.sizeZ - 1)) {
                        this.placeBlock(worldIn, Blocks.LAVA.defaultBlockState(), x, y, z, chunkBox);
                    } else if (y % 3 == 0 && y >= 2 && ((x == 1 || x == this.sizeX - 1 || z == 1 || z == this.sizeZ - 1) || (x == 2 && z == 2) || (x == 2 && z == this.sizeZ - 2) || (x == this.sizeX - 2 && z == 2) || (x == this.sizeX - 2 && z == this.sizeZ - 2))) {
                        // Horizontal bars
                        this.placeBlock(worldIn, Blocks.IRON_BARS.defaultBlockState(), x, y, z, chunkBox);
                    } else if ((x == 1 && z == 2) || (x == 2 && z == 1) ||
                            (x == 1 && z == this.sizeZ - 2) || (x == 2 && z == this.sizeZ - 1) ||
                            (x == this.sizeX - 1 && z == 2) || (x == this.sizeX - 2 && z == 1) ||
                            (x == this.sizeX - 1 && z == this.sizeZ - 2) || (x == this.sizeX - 2 && z == this.sizeZ - 1)) {
                        // Vertical bars
                        this.placeBlock(worldIn, Blocks.IRON_BARS.defaultBlockState(), x, y, z, chunkBox);
                    } else {
                        this.placeBlock(worldIn, Blocks.AIR.defaultBlockState(), x, y, z, chunkBox);
                    }
                }
            }
        }

        int spawnerX = this.sizeX / 2;
        int spawnerY = 1;
        int spawnerZ = this.sizeZ / 2;
        BlockPos blockpos = new BlockPos(this.getWorldX(spawnerX, spawnerZ), this.getWorldY(spawnerY), this.getWorldZ(spawnerX, spawnerZ));
        //Is this position inside the chunk currently being generated?
        if (chunkBox.isInside(blockpos)) {
            worldIn.setBlock(blockpos, GCBlocks.BOSS_SPAWNER.defaultBlockState(), Block.UPDATE_CLIENTS);
            DungeonSpawnerBlockEntity spawner = (DungeonSpawnerBlockEntity) worldIn.getBlockEntity(blockpos);
            if (spawner != null) {
                spawner.setEntityId(GCEntityTypes.SKELETON_BOSS, random);
                spawner.setRoom(new Vec3i(this.boundingBox.minX() + 1, this.boundingBox.minY() + 1, this.boundingBox.minZ() + 1), new Vec3i(this.sizeX - 1, this.sizeY - 1, this.sizeZ - 1));
                spawner.setChestPos(this.chestPos);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);

        if (this.chestPos != null) {
            tag.putInt("chestX", this.chestPos.getX());
            tag.putInt("chestY", this.chestPos.getY());
            tag.putInt("chestZ", this.chestPos.getZ());
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.chestPos = new BlockPos(tag.getInt("chestX"), tag.getInt("chestY"), tag.getInt("chestZ"));
    }

    @Override
    public DungeonPiece getNextPiece(DungeonStart startPiece, RandomSource rand) {
        return getCorridor(rand, startPiece, 10, true, getGenDepth() + 1);
    }

    public BlockPos getChestPos() {
        return chestPos;
    }

    public void setChestPos(BlockPos chestPos) {
        this.chestPos = chestPos;
    }
}
