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

import dev.galacticraft.mod.Constant;
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
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class RoomTreasure extends SizedPiece {

    public static ResourceLocation MOONCHEST = Constant.id("dungeon_tier_1");
    public static final ResourceLocation TABLE_TIER_1_DUNGEON = BuiltInLootTables.register(MOONCHEST);

    public RoomTreasure(CompoundTag tag) {
        super(GCStructurePieceTypes.ROOM_TREASURE, tag);
    }

    public RoomTreasure(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, Direction entranceDir, int genDepth) {
        this(configuration, rand, blockPosX, blockPosZ, rand.nextInt(4) + 6, configuration.getRoomHeight(), rand.nextInt(4) + 6, entranceDir, genDepth);
    }

    public RoomTreasure(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int sizeX, int sizeY, int sizeZ, Direction entranceDir, int genDepth) {
        super(GCStructurePieceTypes.ROOM_TREASURE, configuration, sizeX, sizeY, sizeZ, entranceDir.getOpposite(), genDepth, new BoundingBox(blockPosX, configuration.getYPosition(), blockPosZ, blockPosX + sizeX, configuration.getYPosition() + sizeY, blockPosZ + sizeZ));
        this.setOrientation(Direction.SOUTH);
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
                        this.placeBlock(worldIn, Blocks.GLOWSTONE.defaultBlockState(), x, y, z, chunkBox);
                    } else if (x == this.sizeX / 2 && y == 1 && z == this.sizeZ / 2) {
                        BlockPos blockpos = new BlockPos(this.getWorldX(x, z), this.getWorldY(y), this.getWorldZ(x, z));
                        if (chunkBox.isInside(blockpos)) {
//                            worldIn.setBlock(blockpos, GCBlocks.treasureChestTier1.getDefaultState().withProperty(BlockTier1TreasureChest.FACING, this.getDirection().getOpposite()), Block.UPDATE_CLIENTS);
//                            TileEntityTreasureChest treasureChest = (TileEntityTreasureChest) worldIn.getTileEntity(blockpos);
//                            if (treasureChest != null) {
//                                ResourceLocation chesttype = TABLE_TIER_1_DUNGEON;
//                                if (worldIn.provider instanceof IGalacticraftWorldProvider) {
//                                    chesttype = ((IGalacticraftWorldProvider) worldIn.provider).getDungeonChestType();
//                                }
//                                treasureChest.setLootTable(chesttype, random.nextLong());
//                            }
                        }
                    } else {
                        this.placeBlock(worldIn, Blocks.AIR.defaultBlockState(), x, y, z, chunkBox);
                    }
                }
            }
        }
    }

    @Override
    public DungeonPiece getNextPiece(DungeonStart startPiece, RandomSource rand) {
        if (startPiece.attachedComponents.size() > 2) {
            StructurePiece component = startPiece.attachedComponents.get(startPiece.attachedComponents.size() - 3);
            if (component instanceof RoomBoss) {
                BlockPos blockpos = new BlockPos(this.getWorldX(this.sizeX / 2, this.sizeZ / 2), this.getWorldY(1), this.getWorldZ(this.sizeX / 2, this.sizeZ / 2));
                ((RoomBoss) component).setChestPos(new BlockPos(blockpos));
            }
        }
        return null;
    }
}
