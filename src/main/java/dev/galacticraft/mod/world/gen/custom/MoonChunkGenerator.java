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

package dev.galacticraft.mod.world.gen.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.world.dimension.MoonConstants;
import dev.galacticraft.mod.world.gen.PlanetChunkGenerator;
import dev.galacticraft.mod.world.gen.cave.MoonCaveChunkGenerator;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;

/**
 * Moon-specific planet chunk generator.
 *
 * <p>This generator delegates normal planet terrain to {@link PlanetChunkGenerator}
 * and injects the custom Moon cave framework during the air carving stage.</p>
 */
public class MoonChunkGenerator extends PlanetChunkGenerator {
    public static final MapCodec<MoonChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(MoonChunkGenerator::getBiomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(MoonChunkGenerator::generatorSettings)
    ).apply(instance, MoonChunkGenerator::new));

    public MoonChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    @Override
    protected MapCodec<? extends MoonChunkGenerator> codec() {
        return CODEC;
    }

    /**
     * Applies Moon-specific carving after the vanilla-compatible planet carving pass.
     *
     * <p>Only the AIR carving step is used for the Moon cave system. The cave generator
     * receives the biome manager lookup, but biome lookup is only performed chunk-locally
     * during carving, never during global cave planning.</p>
     */
    @Override
    protected void applyPlanetCarvers(
            WorldGenRegion region,
            long seed,
            RandomState randomState,
            BiomeManager biomeManager,
            StructureManager structureManager,
            ChunkAccess chunkAccess,
            GenerationStep.Carving carving
    ) {
        if (carving != GenerationStep.Carving.AIR) {
            return;
        }

        MoonCaveChunkGenerator.generate(
                chunkAccess,
                randomState,
                MoonConstants.Dimension.MIN_DIMENSION_HEIGHT,
                MoonConstants.Dimension.MAX_DIMENSION_HEIGHT - 1,
                this.getBiomeSource()
        );
    }
}