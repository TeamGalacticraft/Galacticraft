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
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public abstract class DirectionalPiece extends DungeonPiece {

    private Direction direction;

    public DirectionalPiece(StructurePieceType type, CompoundTag tag) {
        super(type, tag);
    }

    public DirectionalPiece(StructurePieceType type, DungeonConfiguration configuration, Direction direction, int genDepth, BoundingBox boundingBox) {
        super(type, configuration, genDepth, boundingBox);
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);

        tag.putInt("direction", this.direction.get3DDataValue());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("direction")) {
            this.direction = Direction.from3DDataValue(tag.getInt("direction"));
        } else {
            this.direction = Direction.NORTH;
        }
    }

    public DungeonPiece getCorridor(RandomSource rand, DungeonStart startPiece, int maxAttempts, boolean small, int genDepth) {
        Direction randomDir;
        int blockX;
        int blockZ;
        int sizeX;
        int sizeZ;
        boolean valid;
        int attempts = maxAttempts;
        int randDir = rand.nextInt(3);
        do {
            randomDir = Direction.from2DDataValue((getDirection().getOpposite().get2DDataValue() + 1 + randDir) % 4);
            BoundingBox extension =
                    getExtension(randomDir, this.configuration.getHallwayLengthMin() + rand.nextInt(this.configuration.getHallwayLengthMax() - this.configuration.getHallwayLengthMin()), 3);
            blockX = extension.minX();
            blockZ = extension.minZ();
            sizeX = extension.maxX() - extension.minX();
            sizeZ = extension.maxZ() - extension.minZ();
            valid = !startPiece.checkIntersection(extension);
            attempts--;
            randDir++;
        } while (!valid && attempts > 0);

        if (!valid) {
            return null;
        }

        return new Corridor(this.configuration, rand, blockX, blockZ, sizeX, small ? 3 : this.configuration.getHallwayHeight(), sizeZ, randomDir, genDepth);
    }
}