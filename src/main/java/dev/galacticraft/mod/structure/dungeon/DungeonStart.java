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

import com.google.common.collect.Lists;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.structure.GCStructurePieceTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;

import java.util.List;

public class DungeonStart extends EntranceCrater {

    public List<StructurePiece> attachedComponents = Lists.newArrayList();
    public List<BoundingBox> componentBounds = Lists.newArrayList();

    public DungeonStart(CompoundTag tag) {
        super(GCStructurePieceTypes.DUNGEON_START, tag);
    }

    public DungeonStart(DungeonConfiguration configuration, RandomSource rand, int blockPosX, int blockPosZ, int genDepth) {
        super(GCStructurePieceTypes.DUNGEON_START, configuration, rand, blockPosX, blockPosZ, genDepth);
    }

    @Override
    public void addChildren(StructurePiece componentIn, StructurePieceAccessor listIn, RandomSource rand) {
        boolean validAttempt = false;
        final int maxAttempts = 10;
        int attempts = 0;
        while (!validAttempt && attempts < maxAttempts) {
            attachedComponents.clear();
            componentBounds.clear();
            componentBounds.add(this.boundingBox);
//            listIn.clear();
            listIn.addPiece(this);
            DungeonPiece next = getNextPiece(this, rand);
            while (next != null) {
                listIn.addPiece(next);
                attachedComponents.add(next);
                componentBounds.add(next.getBoundingBox());

                next = next.getNextPiece(this, rand);
            }
            if (attachedComponents.size() >= 3 && attachedComponents.get(attachedComponents.size() - 1) instanceof RoomTreasure
                    && attachedComponents.get(attachedComponents.size() - 3) instanceof RoomBoss) {
                validAttempt = true;
            }
            attempts++;
        }

        Constant.LOGGER.debug("Dungeon generation took " + attempts + " attempt(s)");

        if (!validAttempt) {
            int xPos = this.boundingBox.minX() + (this.boundingBox.maxX() - this.boundingBox.minX()) / 2;
            int zPos = this.boundingBox.minZ() + (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2;
            System.err.println("Could not find valid dungeon layout! This is a bug, please report it, including your world seed (/seed) and dungeon location (" + xPos + ", " + zPos + ")");
        }

        super.addChildren(componentIn, listIn, rand);
    }

    public boolean checkIntersection(BoundingBox bounds) {
        for (int i = 0; i < componentBounds.size() - 1; ++i) {
            BoundingBox boundingBox = componentBounds.get(i);
            if (boundingBox.intersects(bounds)) {
                return true;
            }
        }

        return false;
    }
}
