/*
 * Copyright (c) 2019-2021 Team Galacticraft
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
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.JigsawFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import net.minecraft.world.gen.random.ChunkRandom;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MoonPillagerBaseFeature extends JigsawFeature {
    public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(ImmutableList.<SpawnSettings.SpawnEntry>builder().add(new SpawnSettings.SpawnEntry(GalacticraftEntityType.EVOLVED_PILLAGER, 1, 1, 2)).build());

    public MoonPillagerBaseFeature(Codec<StructurePoolFeatureConfig> codec) {
        super(codec, 0, true, true);
    }

    @Override
    protected boolean shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long worldSeed, @NotNull ChunkRandom random, ChunkPos pos, ChunkPos chunkPos, StructurePoolFeatureConfig featureConfig, HeightLimitView heightLimitView) {
        if (random.nextInt(5) != 0) {
            return false;
        } else {
            return !this.ensureNoVillage(chunkGenerator, worldSeed, random, chunkPos.x, chunkPos.z);
        }
    }

    private boolean ensureNoVillage(@NotNull ChunkGenerator chunkGenerator, long l, ChunkRandom chunkRandom, int i, int j) {
        StructureConfig structureConfig = chunkGenerator.getStructuresConfig().getForType(StructureFeature.VILLAGE);
        if (structureConfig != null) {
            for (int x = i - 10; x <= i + 10; ++x) {
                for (int z = j - 10; z <= j + 10; ++z) {
                    ChunkPos chunkPos = StructureFeature.VILLAGE.getStartChunk(structureConfig, l, chunkRandom, x, z);
                    if (x == chunkPos.x && z == chunkPos.z) {
                        return true;
                    }
                }
            }

        }
        return false;
    }
}
