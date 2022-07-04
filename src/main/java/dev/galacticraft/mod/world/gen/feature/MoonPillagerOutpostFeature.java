/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import dev.galacticraft.mod.entity.GalacticraftEntityType;
import dev.galacticraft.mod.structure.MoonPillagerOutpostGenerator;
import net.minecraft.structure.PlainsVillageData;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.Structures;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MoonPillagerOutpostFeature {
    public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(ImmutableList.<SpawnSettings.SpawnEntry>builder().add(new SpawnSettings.SpawnEntry(GalacticraftEntityType.EVOLVED_PILLAGER, 1, 1, 2)).build());

    public static JigsawStructure createStructure() {
        return new JigsawStructure(
                Structures.createConfig(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE, StructureTerrainAdaptation.BEARD_THIN),
                MoonPillagerOutpostGenerator.ENTERANCE_POOL, 10,
                ConstantHeightProvider.create(YOffset.fixed(0)),
                true,
                Heightmap.Type.WORLD_SURFACE_WG
        );
    }

//    private static boolean canStart(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context) { TODO: PORT
//        int i = context.chunkPos().x >> 4;
//        int j = context.chunkPos().z >> 4;
//        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
//        chunkRandom.setSeed((long)(i ^ j << 4) ^ context.seed());
//        chunkRandom.nextInt();
//        if (chunkRandom.nextInt(5) != 0) {
//            return false;
//        } else {
//            return !isVillageNearby(context.chunkGenerator(), context.seed(), context.chunkPos());
//        }
//    }


    private static boolean isVillageNearby(ChunkGenerator generator, long worldSeed, ChunkPos chunkPos) {
//        StructureConfig structureConfig = generator.getStructuresConfig().getForType(StructureFeature.VILLAGE);
//        if (structureConfig != null) {
//            int i = chunkPos.x;
//            int j = chunkPos.z;
//
//            for (int k = i - 10; k <= i + 10; ++k) {
//                for (int l = j - 10; l <= j + 10; ++l) {
//                    ChunkPos chunkPos2 = StructureFeature.VILLAGE.getStartChunk(structureConfig, worldSeed, k, l);
//                    if (k == chunkPos2.x && l == chunkPos2.z) {
//                        return true;
//                    }
//                }
//            }
//
//        }
        throw new RuntimeException("Locate Village: TODO");
//        return false;
    }
}
