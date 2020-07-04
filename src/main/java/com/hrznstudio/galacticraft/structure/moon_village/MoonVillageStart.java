/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.structure.moon_village;

import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.*;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class MoonVillageStart extends StructureStart<StructurePoolFeatureConfig> {


    public MoonVillageStart(StructureFeature<StructurePoolFeatureConfig> structureFeature, int chunkX, int chunkZ, BlockBox blockBox, int i, long l) {
        super(structureFeature, chunkX, chunkZ, blockBox, i, l);
    }

    @Override
    public void init(ChunkGenerator chunkGenerator, StructureManager structureManager, int x, int z, Biome biome, StructurePoolFeatureConfig featureConfig) {
        StructurePoolBasedGenerator.addPieces(featureConfig.startPool, featureConfig.size, MoonVillagePiece::new, chunkGenerator, structureManager, new BlockPos(x * 16, 0, z * 16), this.children, random, true, true);
        this.setBoundingBoxFromChildren();
    }

    @Override
    protected void setBoundingBoxFromChildren() {
        super.setBoundingBoxFromChildren();
        BlockBox box = this.boundingBox;
        box.minX -= 12;
        box = this.boundingBox;
        box.minY -= 12;
        box = this.boundingBox;
        box.minZ -= 12;
        box = this.boundingBox;
        box.maxX += 12;
        box = this.boundingBox;
        box.maxY += 12;
        box = this.boundingBox;
        box.maxZ += 12;
    }
}
