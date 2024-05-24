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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class EntranceCrater extends SizedPiece {

    private static final int RANGE = 16;

    public EntranceCrater(StructurePieceType type, CompoundTag tag) {
        super(type, tag);
    }

    public EntranceCrater(StructurePieceType type, DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int genDepth) {
        super(type, configuration, rand.nextInt(4) + 6, 12, rand.nextInt(4) + 6, Direction.Plane.HORIZONTAL.getRandomDirection(rand), genDepth, new BoundingBox(blockPosX - RANGE, configuration.getYPosition() + 11, blockPosZ - RANGE, blockPosX + RANGE, 150, blockPosZ + RANGE));
        this.setOrientation(Direction.SOUTH);
    }

    @Override
    public void postProcess(WorldGenLevel worldIn, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox structureBoundingBoxIn, ChunkPos chunkPos, BlockPos pivot) {
        BlockState block1;

        int maxLevel = 0;

        for (int i = -RANGE; i <= RANGE; i++) {
            for (int k = -RANGE; k <= RANGE; k++) {
                int j = 150;
                int x = this.getWorldX(i + RANGE, k + RANGE);
                int z = this.getWorldZ(i + RANGE, k + RANGE);

                while (j >= 0) {
                    j--;

                    int y = this.getWorldY(j);
                    BlockPos blockpos = new BlockPos(x, y, z);
                    Block block = worldIn.getBlockState(blockpos).getBlock();

                    if (Blocks.AIR != block) {
                        break;
                    }
                }

                maxLevel = Math.max(maxLevel, j + 3);
            }
        }

        Mirror mirror = Mirror.NONE;
        Rotation rotation = Rotation.NONE;
        if (this.getOrientation() != null) {
            switch (this.getOrientation()) {
                case SOUTH:
                    mirror = Mirror.LEFT_RIGHT;
                    break;
                case WEST:
                    mirror = Mirror.LEFT_RIGHT;
                    rotation = Rotation.CLOCKWISE_90;
                    break;
                case EAST:
                    rotation = Rotation.CLOCKWISE_90;
                    break;
                default:
                    break;
            }
        }

        for (int i = -RANGE; i < RANGE; i++) {
            for (int k = -RANGE; k < RANGE; k++) {
                final double xDev = i / 20D;
                final double zDev = k / 20D;
                final double distance = xDev * xDev + zDev * zDev;
                final int depth = (int) Math.abs(0.5 / (distance + .00001D));
                int helper = 0;
                for (int j = maxLevel; j > 1 && helper <= depth; j--) {
                    block1 = this.getBlock(worldIn, i + RANGE, j, k + RANGE, boundingBox);
//                    if (block1 == this.configuration.getBrickBlock() || j != this.sizeY)
                    {
                        BlockPos blockpos = new BlockPos(this.getWorldX(i + RANGE, k + RANGE), this.getWorldY(j), this.getWorldZ(i + RANGE, k + RANGE));
                        BlockState state = Blocks.AIR.defaultBlockState();

                        if (mirror != Mirror.NONE) {
                            state = state.mirror(mirror);
                        }

                        if (rotation != Rotation.NONE) {
                            state = state.rotate(rotation);
                        }

                        worldIn.setBlock(blockpos, state, Block.UPDATE_CLIENTS);
//                        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), i + range, j, k + range, boundingBox);
                        helper++;
                    }
                }
            }
        }
    }

    @Override
    public DungeonPiece getNextPiece(DungeonStart startPiece, RandomSource random) {
        return new RoomEntrance(this.configuration, random, this.boundingBox.minX() + this.boundingBox.getXSpan() / 2, this.boundingBox.minZ() + this.boundingBox.getZSpan() / 2, getGenDepth() +1);
    }
}
