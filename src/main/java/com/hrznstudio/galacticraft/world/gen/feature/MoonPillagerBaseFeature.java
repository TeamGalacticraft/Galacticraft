/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.entity.GalacticraftEntityTypes;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.JigsawFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonPillagerBaseFeature extends JigsawFeature {
    private static final List<MobSpawnSettings.SpawnerData> MONSTER_SPAWNS = ImmutableList.<MobSpawnSettings.SpawnerData>builder().add(new MobSpawnSettings.SpawnerData(GalacticraftEntityTypes.EVOLVED_PILLAGER, 1, 1, 2)).build();

    public MoonPillagerBaseFeature(Codec<JigsawConfiguration> codec) {
        super(codec, 0, true, true);
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, WorldgenRandom chunkRandom, int i, int j, Biome biome, ChunkPos chunkPos, JigsawConfiguration structurePoolFeatureConfig) {
        int k = i >> 4;
        int m = j >> 4;
        chunkRandom.setSeed((long)(k ^ m << 4) ^ l);
        chunkRandom.nextInt();
        if (chunkRandom.nextInt(5) != 0) {
            return false;
        } else {
            return !this.ensureNoVillage(chunkGenerator, l, chunkRandom, i, j);
        }
    }

    private boolean ensureNoVillage(ChunkGenerator chunkGenerator, long l, WorldgenRandom chunkRandom, int i, int j) {
        StructureFeatureConfiguration structureConfig = chunkGenerator.getSettings().getConfig(StructureFeature.VILLAGE);
        if (structureConfig != null) {
            for (int k = i - 10; k <= i + 10; ++k) {
                for (int m = j - 10; m <= j + 10; ++m) {
                    ChunkPos chunkPos = StructureFeature.VILLAGE.getPotentialFeatureChunk(structureConfig, l, chunkRandom, k, m);
                    if (k == chunkPos.x && m == chunkPos.z) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    @Override
    public List<MobSpawnSettings.SpawnerData> getSpecialEnemies() {
        return MONSTER_SPAWNS;
    }
}
