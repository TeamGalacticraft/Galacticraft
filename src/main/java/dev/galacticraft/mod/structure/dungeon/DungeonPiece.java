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

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public abstract class DungeonPiece extends StructurePiece {
    protected DungeonConfiguration configuration;

    public DungeonPiece(StructurePieceType type, DungeonConfiguration configuration, int genDepth, BoundingBox boundingBox) {
        super(type, genDepth, boundingBox);
        this.configuration = configuration;
    }

    public DungeonPiece(StructurePieceType type, CompoundTag tag) {
        super(type, tag);
        readAdditionalSaveData(tag);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        this.configuration.write(tag);
    }

    protected void readAdditionalSaveData(CompoundTag tag) {
        if (this.configuration == null) {
            this.configuration = new DungeonConfiguration();
            this.configuration.read(tag);
        }
    }

    protected BoundingBox getExtension(Direction direction, int length, int width) {
        int blockX, blockZ, sizeX, sizeZ;
        switch (direction) {
            case NORTH:
                sizeX = width;
                sizeZ = length;
                blockX = this.boundingBox.minX() + (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 - sizeX / 2;
                blockZ = this.boundingBox.minZ() - sizeZ;
                break;
            case EAST:
                sizeX = length;
                sizeZ = width;
                blockX = this.boundingBox.maxX();
                blockZ = this.boundingBox.minZ() + (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 - sizeZ / 2;
                break;
            case SOUTH:
                sizeX = width;
                sizeZ = length;
                blockX = this.boundingBox.minX() + (this.boundingBox.maxX() - this.boundingBox.minX()) / 2 - sizeX / 2;
                blockZ = this.boundingBox.maxZ();
                break;
            case WEST:
            default:
                sizeX = length;
                sizeZ = width;
                blockX = this.boundingBox.minX() - sizeX;
                blockZ = this.boundingBox.minZ() + (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2 - sizeZ / 2;
                break;
        }
        return new BoundingBox(blockX, this.configuration.getYPosition(), blockZ, blockX + sizeX, this.configuration.getYPosition() + this.configuration.getHallwayHeight(), blockZ + sizeZ);
    }

    public DungeonPiece getNextPiece(DungeonStart startPiece, RandomSource random) {
        return null;
    }
}
