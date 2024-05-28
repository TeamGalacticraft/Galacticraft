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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class RoomEntrance extends SizedPiece {
    public RoomEntrance(CompoundTag tag) {
        super(GCStructurePieceTypes.ROOM_ENTRANCE, tag);
    }

    public RoomEntrance(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int genDepth) {
        super(GCStructurePieceTypes.ROOM_ENTRANCE, configuration, rand.nextInt(4) + 6, 12, rand.nextInt(4) + 6, Direction.Plane.HORIZONTAL.getRandomDirection(rand), genDepth, null);
        this.setOrientation(Direction.SOUTH);
        int sX = this.sizeX / 2;
        int sZ = this.sizeZ / 2;

        this.boundingBox = new BoundingBox(blockPosX - sX, configuration.getYPosition(), blockPosZ - sZ, blockPosX - sX + this.sizeX, configuration.getYPosition() + this.sizeY, blockPosZ - sZ + this.sizeZ);
    }

    @Override
    public void postProcess(WorldGenLevel worldIn, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomIn, BoundingBox structureBoundingBoxIn, ChunkPos pos, BlockPos pivot) {
        for (int x = 0; x <= this.sizeX; x++) {
            for (int y = 0; y <= this.sizeY; y++) {
                for (int z = 0; z <= this.sizeZ; z++) {
                    if (x == 0 || x == this.sizeX || y == 0 /*|| j == this.sizeY*/ || z == 0 || z == this.sizeZ) {
                        this.placeBlock(worldIn, this.configuration.getBrickBlock(), x, y, z, boundingBox);
                    } else {
                        this.placeBlock(worldIn, Blocks.AIR.defaultBlockState(), x, y, z, boundingBox);
                    }
                }
            }
        }
    }

    @Override
    public DungeonPiece getNextPiece(DungeonStart startPiece, RandomSource rand) {
        return getCorridor(rand, startPiece, 10, false, getGenDepth() + 1);
    }
}