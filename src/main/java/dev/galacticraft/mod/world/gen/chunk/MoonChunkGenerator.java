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

package dev.galacticraft.mod.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.structure.GalacticraftStructure;
import dev.galacticraft.mod.world.biome.source.MoonBiomeSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public final class MoonChunkGenerator extends NoiseChunkGenerator {
    public static final Codec<MoonChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            MoonBiomeSource.CODEC.fieldOf("biome_source").forGetter((moonChunkGenerator) -> (MoonBiomeSource) moonChunkGenerator.biomeSource),
            Codec.LONG.fieldOf("seed").stable().forGetter((moonChunkGenerator) -> moonChunkGenerator.seed),
            ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").stable().forGetter(moonChunkGenerator -> moonChunkGenerator.settings)
    ).apply(instance, instance.stable(MoonChunkGenerator::new)));

    private final long seed;

    public MoonChunkGenerator(BiomeSource biomeSource, long seed, @NotNull Supplier<ChunkGeneratorSettings> settingsSupplier) {
        super(biomeSource, seed, settingsSupplier);
        this.seed = seed;
    }

    @Override
    protected Codec<? extends MoonChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ChunkGenerator withSeed(long seed) {
        return new MoonChunkGenerator(this.biomeSource.withSeed(seed), seed, this.settings);
    }

    @Override
    public int getSeaLevel() {
        return Integer.MIN_VALUE;
    }

    @Override
    public int getSpawnHeight(HeightLimitView world) {
        return 80;
    }

    @Override
    public Pool<SpawnSettings.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
        if (group == SpawnGroup.MONSTER) {
            if (accessor.getStructureAt(pos, false, GalacticraftStructure.MOON_PILLAGER_BASE_FEATURE).hasChildren()) {
                return GalacticraftStructure.MOON_PILLAGER_BASE_FEATURE.getMonsterSpawns();
            }

            if (accessor.getStructureAt(pos, false, GalacticraftStructure.MOON_RUINS).hasChildren()) {
                return GalacticraftStructure.MOON_RUINS.getMonsterSpawns();
            }
        }

        return super.getEntitySpawnList(biome, accessor, group, pos);
    }
}
