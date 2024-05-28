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
import dev.galacticraft.mod.structure.GCStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class Corridor extends SizedPiece {

    public Corridor(CompoundTag tag) {
        super(GCStructurePieceTypes.CORRIDOR, tag);
    }

    public Corridor(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction direction, int genDepth) {
        super(GCStructurePieceTypes.CORRIDOR, configuration, sizeX, sizeY, sizeZ, direction, genDepth, new BoundingBox(blockPosX, configuration.getYPosition(), blockPosZ, blockPosX + sizeX, configuration.getYPosition() + sizeY, blockPosZ + sizeZ));
        this.setOrientation(Direction.SOUTH);
    }

    @Override
    public void postProcess(WorldGenLevel worldIn, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomIn, BoundingBox structureBoundingBoxIn, ChunkPos pos, BlockPos pivot) {
        for (int x = 0; x < this.boundingBox.getXSpan(); x++) {
            for (int y = 0; y < this.boundingBox.getYSpan(); y++) {
                for (int z = 0; z < this.boundingBox.getZSpan(); z++) {
                    if ((this.getDirection().getAxis() == Direction.Axis.Z && (x == 0 || x == this.boundingBox.getXSpan() - 1)) || y == 0 || y == this.boundingBox.getYSpan() - 1
                            || (this.getDirection().getAxis() == Direction.Axis.X && (z == 0 || z == this.boundingBox.getZSpan() - 1))) {
                        this.placeBlock(worldIn, this.configuration.getBrickBlock(), x, y, z, this.boundingBox);
                    } else {
                        if (y == this.boundingBox.getYSpan() - 2) {
                            if (this.getDirection().getAxis() == Direction.Axis.Z && (z + 1) % 4 == 0 && (x == 1 || x == this.boundingBox.getXSpan() - 2)) {
                                this.placeBlock(worldIn,
                                        GCBlocks.UNLIT_WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, x == 1 ? Direction.WEST.getOpposite() : Direction.EAST.getOpposite()), x, y, z,
                                        this.boundingBox);
                                continue;
                            } else if (this.getDirection().getAxis() == Direction.Axis.X && (x + 1) % 4 == 0 && (z == 1 || z == this.boundingBox.getZSpan() - 2)) {
                                this.placeBlock(worldIn,
                                        GCBlocks.UNLIT_WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, z == 1 ? Direction.NORTH.getOpposite() : Direction.SOUTH.getOpposite()), x, y, z,
                                        this.boundingBox);
                                continue;
                            }
                        }

                        this.placeBlock(worldIn, Blocks.AIR.defaultBlockState(), x, y, z, this.boundingBox);
                    }
                }
            }
        }
    }

    private <T extends SizedPiece> T getRoom(StructurePieceType type, DungeonStart startPiece, RandomSource rand) {
        if (!(type instanceof GCStructurePieceTypes.GCRoomPieceType roomType))
            throw new RuntimeException("Room " + type +" is not a instance of GCRoomPieceType!");
        SizedPiece dummy = roomType.create(this.configuration, rand, 0, 0, this.getDirection().getOpposite(), getGenDepth() + 1);
        BoundingBox extension = getExtension(this.getDirection(), getDirection().getAxis() == Direction.Axis.X ? dummy.getSizeX() : dummy.getSizeZ(),
                getDirection().getAxis() == Direction.Axis.X ? dummy.getSizeZ() : dummy.getSizeX());
        if (startPiece.checkIntersection(extension)) {
            return null;
        }
        int sizeX = extension.maxX() - extension.minX();
        int sizeZ = extension.maxZ() - extension.minZ();
        int sizeY = dummy.sizeY;
        int blockX = extension.minX();
        int blockZ = extension.minZ();
        return (T) roomType.create(this.configuration, rand, blockX, blockZ, sizeX, sizeY, sizeZ, this.getDirection().getOpposite(), getGenDepth() + 1);
    }

    @Override
    public DungeonPiece getNextPiece(DungeonStart startPiece, RandomSource rand) {
        int pieceCount = startPiece.attachedComponents.size();
        if (pieceCount > 10 && startPiece.attachedComponents.get(pieceCount - 2) instanceof RoomBoss) {
            try {
                return getRoom(this.configuration.getTreasureRoom(), startPiece, rand);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            int bossRoomChance = Math.max((int) (20.0 / (pieceCount - 10)), 1);
            boolean bossRoom = pieceCount > 25 || (pieceCount > 10 && rand.nextInt(bossRoomChance) == 0);
            if (bossRoom) {
                try {
                    return getRoom(this.configuration.getBossRoom(), startPiece, rand);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                BoundingBox extension = getExtension(this.getDirection(), rand.nextInt(4) + 6, rand.nextInt(4) + 6);

                if (startPiece.checkIntersection(extension)) {
                    return null;
                }

                int sizeX = extension.maxX() - extension.minX();
                int sizeZ = extension.maxZ() - extension.minZ();
                int sizeY = configuration.getRoomHeight();
                int blockX = extension.minX();
                int blockZ = extension.minZ();

                if (Math.abs(startPiece.getBoundingBox().maxZ() - boundingBox.minZ()) > 200) {
                    return null;
                }

                if (Math.abs(startPiece.getBoundingBox().maxX() - boundingBox.minX()) > 200) {
                    return null;
                }

                switch (rand.nextInt(3)) {
                    case 0:
                        return new RoomSpawner(this.configuration, rand, blockX, blockZ, sizeX, sizeY, sizeZ, this.getDirection().getOpposite(), getGenDepth() + 1);
                    case 1:
                        return new RoomChest(this.configuration, rand, blockX, blockZ, sizeX, sizeY, sizeZ, this.getDirection().getOpposite(), getGenDepth() + 1);
                    default:
                    case 2:
                        return new RoomEmpty(this.configuration, rand, blockX, blockZ, sizeX, sizeY, sizeZ, this.getDirection().getOpposite(), getGenDepth() + 1);
                }
            }

        }

        return null;
    }
}