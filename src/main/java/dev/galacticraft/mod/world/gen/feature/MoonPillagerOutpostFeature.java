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
import net.minecraft.data.worldgen.Structures;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MoonPillagerOutpostFeature {
    public static final WeightedRandomList<MobSpawnSettings.SpawnerData> MONSTER_SPAWNS = WeightedRandomList.create(ImmutableList.<MobSpawnSettings.SpawnerData>builder().add(new MobSpawnSettings.SpawnerData(GalacticraftEntityType.EVOLVED_PILLAGER, 1, 1, 2)).build());

    public static JigsawStructure createStructure() {
        return new JigsawStructure(
                Structures.structure(BiomeTags.HAS_VILLAGE_PLAINS, TerrainAdjustment.BEARD_THIN),
                MoonPillagerOutpostGenerator.ENTERANCE_POOL, 10,
                ConstantHeight.of(VerticalAnchor.absolute(0)),
                true,
                Heightmap.Types.WORLD_SURFACE_WG
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
