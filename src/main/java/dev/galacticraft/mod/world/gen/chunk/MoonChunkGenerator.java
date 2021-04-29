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
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.structure.GalacticraftStructure;
import dev.galacticraft.mod.world.biome.source.MoonBiomeSource;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.math.*;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public final class MoonChunkGenerator extends NoiseChunkGenerator {
    public static final Codec<MoonChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> instance.group(MoonBiomeSource.CODEC.fieldOf("biome_source").forGetter((moonChunkGenerator) -> (MoonBiomeSource) moonChunkGenerator.biomeSource), Codec.LONG.fieldOf("seed").stable().forGetter((moonChunkGenerator) -> moonChunkGenerator.seed)).apply(instance, instance.stable(MoonChunkGenerator::new)));

    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private final OctavePerlinNoiseSampler depthNoise;
    private final long seed;

    public MoonChunkGenerator(MoonBiomeSource biomeSource, long seed) {
        this(biomeSource, seed, () -> new ChunkGeneratorSettings(
                new StructuresConfig(false),
                new GenerationShapeConfig(
                        256, new NoiseSamplingConfig(0.8239043235D, 0.826137924865D, 120.0D, 140.0D),
                        new SlideConfig(-10, 3, 0), new SlideConfig(-30, 2, -1),
                        4, 2, 1.0D, -0.46875D, true,
                        true, false, false),
                GalacticraftBlock.MOON_ROCKS[0].getDefaultState(), AIR, -10, 0, 1, false));
    }

    private MoonChunkGenerator(BiomeSource biomeSource, long seed, @NotNull Supplier<ChunkGeneratorSettings> settingsSupplier) {
        super(biomeSource, seed, settingsSupplier);
        this.random.consume(2222);
        this.depthNoise = new OctavePerlinNoiseSampler(this.random, IntStream.range(0, 22));
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
    public int getSpawnHeight() {
        return 80;
    }

    @Override
    public List<SpawnSettings.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
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

    @Override
    public void carve(long seed, BiomeAccess access, Chunk chunk, GenerationStep.Carver carver) {
        super.carve(seed, access, chunk, carver);
        this.addCraters(chunk);
    }

    private void addCraters(Chunk chunk) {
        for (int cx = chunk.getPos().x - 2; cx <= chunk.getPos().x + 2; cx++) {
            for (int cz = chunk.getPos().z - 2; cz <= chunk.getPos().z + 2; cz++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        if (Math.abs(this.randFromPoint((cx << 4) + x, (cz << 4) + z)) < this.getSpecialDepth(x << 4 + x, cz << 4 + z) / 32.0D) {
                            Random random = new Random((((long) cx << 4) + x + ((long) cz << 4) + z) * 102L);
                            int size;

                            int i = random.nextInt(100);
                            if (i < 5) {
                                size = random.nextInt(16 - 14) + 14;
                            } else if (i < 45) {
                                size = random.nextInt(14 - 11) + 11;
                            } else if (i < 60) {
                                size = random.nextInt(12 - 8) + 8;
                            } else {
                                size = random.nextInt(13 - 9) + 9;
                            }

                            this.makeCrater((cx << 4) + x, (cz << 4) + z, chunk.getPos().x, chunk.getPos().z, size, chunk);
                        }
                    }
                }
            }
        }
    }

    private double getSpecialDepth(int x, int z)
    {
        double sDepth = this.depthNoise.sample(x * 200, 10.0D, z * 200, 1.0D, 0.0D, true) * 65535.0D / 8000.0D;
        if (sDepth < 0.0D)
        {
            sDepth = -sDepth * 0.3D;
        }

        sDepth = sDepth * 3.0D - 2.0D;
        if (sDepth < 0.0D)
        {
            sDepth /= 28.0D;
        }
        else
        {
            if (sDepth > 1.0D)
            {
                sDepth = 1.0D;
            }

            sDepth /= 40.0D;
        }

        return sDepth;
    }

    private double randFromPoint(int x, int z) {
        int n;
        n = x + z * 57;
        n = n << 13 ^ n;
        return 1.0D - (n * (n * n * 15731 + 789221) + 1376312589 & 0x7fffffff) / 1073741824.0;
    }

    private void makeCrater(int craterX, int craterZ, int chunkX, int chunkZ, int size, Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double xDev = craterX - ((chunkX << 4) + x);
                double zDev = craterZ - ((chunkZ << 4) + z);
                if (xDev * xDev + zDev * zDev < size * size) {
                    xDev /= size;
                    zDev /= size;
                    final double sqrtY = (xDev * xDev + zDev * zDev) * 2.5;
                    double maxDepth = 5 - (sqrtY * sqrtY * 6);
                    double depth = 0;
                    for (int y = 90; y > 0 && depth <= maxDepth; y--) {
                        if (depth != 0 || !chunk.getBlockState(new BlockPos(x, y, z)).isAir()) {
                            chunk.setBlockState(new BlockPos(x, y, z), AIR, false);
                            depth += 2.5d;
                        }
                    }
                }
            }
        }
    }
}
