/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.world.gen.spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

final class MoonVillageAnchorLocator {
    private MoonVillageAnchorLocator() {
    }

    static VillageAnchor findVillageAnchor(ServerLevel world, LevelChunk chunk, Structure villageStructure) {
        StructureStart structureStart = world.structureManager().startsForStructure(chunk.getPos(), structure -> structure == villageStructure).stream()
                .filter(StructureStart::isValid)
                .findFirst()
                .orElse(StructureStart.INVALID_START);
        if (!structureStart.isValid()) {
            return null;
        }

        ChunkPos startChunk = structureStart.getChunkPos();
        BlockPos center = structureStart.getBoundingBox().getCenter();
        BlockPos surfacePos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(center.getX(), world.getMinBuildHeight(), center.getZ()));
        return new VillageAnchor(ChunkPos.asLong(startChunk.x, startChunk.z), surfacePos);
    }

    record VillageAnchor(long key, BlockPos position) {
    }
}