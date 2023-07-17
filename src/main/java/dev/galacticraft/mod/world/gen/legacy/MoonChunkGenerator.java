/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.world.gen.legacy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.tag.GCTags;
import dev.galacticraft.mod.world.gen.legacy.perlin.generator.GradientNoise;
import dev.galacticraft.mod.world.gen.surfacebuilder.MoonSurfaceRules;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MoonChunkGenerator extends ChunkGenerator {

    private final SurfaceRules.RuleSource BEDROCK_FLOOR = MoonSurfaceRules.createDefaultRule();
    private final GradientNoise noiseGen1;
    private final GradientNoise noiseGen2;
    private final GradientNoise noiseGen3;
    private final GradientNoise noiseGen4;

    private final MoonCavesGenerator caveGenerator = new MoonCavesGenerator();


    public static final Codec<MoonChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(BiomeSource.CODEC.fieldOf("biome_source").forGetter(noiseBasedChunkGenerator -> noiseBasedChunkGenerator.biomeSource))
            .apply(instance, instance.stable(MoonChunkGenerator::new))
    );

    public MoonChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
        RandomSource rand = RandomSource.create();
        this.noiseGen1 = new GradientNoise(rand.nextLong(), 4, 0.25D);
        this.noiseGen2 = new GradientNoise(rand.nextLong(), 4, 0.25D);
        this.noiseGen3 = new GradientNoise(rand.nextLong(), 1, 0.25D);
        this.noiseGen4 = new GradientNoise(rand.nextLong(), 1, 0.25D);
    }

    @Override
    protected Codec<MoonChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion worldGenRegion, long seed, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunkAccess, GenerationStep.Carving carving) {

    }

    @Override
    public void buildSurface(WorldGenRegion worldGenRegion, StructureManager structureManager, RandomState randomState, ChunkAccess chunkAccess) {
        WorldGenerationContext worldGenerationContext = new WorldGenerationContext(this, worldGenRegion);
        SurfaceRules.Context context = new SurfaceRules.Context(randomState.surfaceSystem(), randomState, chunkAccess, null, worldGenRegion.getBiomeManager()::getBiome, worldGenRegion.registryAccess().registryOrThrow(Registries.BIOME), worldGenerationContext);
        buildMoonSurface(worldGenRegion, chunkAccess, context);
        createCraters(worldGenRegion, chunkAccess);
        replaceBlocksForBiome(worldGenRegion, chunkAccess);

        caveGenerator.generate(worldGenRegion);
    }

    private double randFromPoint(int x, int z) {
        int n;
        n = x + z * 57;
        n = n << 13 ^ n;
        return 1.0 - (n * (n * n * 15731 + 789221) + 1376312589 & 0x7fffffff) / 1073741824.0;
    }

    private void createCraters(WorldGenRegion primer, ChunkAccess chunkAccess) {
        int chunkX = chunkAccess.getPos().x;
        int chunkZ = chunkAccess.getPos().z;
        for (int cx = chunkX - 2; cx <= chunkX + 2; cx++) {
            for (int cz = chunkZ - 2; cz <= chunkZ + 2; cz++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        if (Math.abs(this.randFromPoint(cx * 16 + x, (cz * 16 + z) * 1000)) < this.noiseGen4.evalNoise(x * 16 + x, cz * 16 + z)
                                / 300) {
                            final Random random = new Random(cx * 16 + x + (cz * 16 + z) * 5000);
                            final CraterSize cSize = CraterSize.sizeArray[random.nextInt(CraterSize.sizeArray.length)];
                            final int size = random.nextInt(cSize.MAX_SIZE - cSize.MIN_SIZE) + cSize.MIN_SIZE;
                            this.makeCrater(cx * 16 + x, cz * 16 + z, chunkX * 16, chunkZ * 16, size, primer);
                        }
                    }
                }
            }
        }
    }

    private void makeCrater(int craterX, int craterZ, int chunkX, int chunkZ, int size, WorldGenRegion primer) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double xDev = craterX - (chunkX + x);
                double zDev = craterZ - (chunkZ + z);
                if (xDev * xDev + zDev * zDev < size * size) {
                    xDev /= size;
                    zDev /= size;
                    final double sqrtY = xDev * xDev + zDev * zDev;
                    double yDev = sqrtY * sqrtY * 6;
                    yDev = 5 - yDev;
                    int helper = 0;
                    for (int y = 127; y > 0; y--) {
                        BlockPos pos = primer.getCenter().getWorldPosition().offset(x, 0, z).atY(y);
                        if (primer.getBlockState(pos).is(GCTags.MOON_CRATER_CARVER_REPLACEABLES) && helper <= yDev) {
                            primer.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                            helper++;
                        }
                        if (helper > yDev) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public void replaceBlocksForBiome(WorldGenRegion primer, ChunkAccess chunkAccess) {
        final int var5 = 20;
        int x = chunkAccess.getPos().x;
        int z = chunkAccess.getPos().z;
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                BlockPos pos = primer.getCenter().getWorldPosition().offset(i, 0, j);
                final int var12 = (int) (this.noiseGen4.evalNoise(i + x * 16, j * z * 16) / 3.0D + 3.0D + primer.getRandom().nextDouble() * 0.25D);
                int var13 = -1;
                BlockState state0 = GCBlocks.MOON_TURF.defaultBlockState();
                BlockState state1 = GCBlocks.MOON_DIRT.defaultBlockState();

                for (int var16 = 127; var16 >= 0; --var16) {
                    if (var16 <= primer.getRandom().nextInt(5)) {
                        primer.setBlock(pos.atY(var16), Blocks.BEDROCK.defaultBlockState(), 3);
                    } else {
                        BlockState var18 = primer.getBlockState(pos.atY(var16));
                        if (Blocks.AIR == var18.getBlock()) {
                            var13 = -1;
                        } else if (var18.is(GCBlocks.MOON_ROCK)) {
                            if (var13 == -1) {
                                if (var12 <= 0) {
                                    state0 = Blocks.AIR.defaultBlockState();
                                    state1 = GCBlocks.MOON_ROCK.defaultBlockState();
                                } else if (var16 >= var5 - -16 && var16 <= var5 + 1) {
                                    state0 = GCBlocks.MOON_DIRT.defaultBlockState();
                                }

                                var13 = var12;

                                if (var16 >= var5 - 1) {
                                    primer.setBlock(pos.atY(var16), state0, 3);
                                } else if (var16 < var5 - 1 && var16 >= var5 - 2) {
                                    primer.setBlock(pos.atY(var16), state1, 3);
                                }
                            } else if (var13 > 0) {
                                --var13;
                                primer.setBlock(pos.atY(var16), state1, 3);
                            }
                        }
                    }
                }
            }
        }
    }

    public void buildMoonSurface(WorldGenRegion worldGenRegion, ChunkAccess chunkAccess, SurfaceRules.Context context) {
        this.noiseGen1.setFrequencyAll(0.012500000186264515D);
        this.noiseGen2.setFrequencyAll(0.014999999664723873D);
        this.noiseGen3.setFrequencyAll(0.0D);
        this.noiseGen4.setFrequencyAll(0.019999999552965164D);
        SurfaceRules.SurfaceRule bedrockFloor = BEDROCK_FLOOR.apply(context);
        if (!SharedConstants.debugVoidTerrain(chunkAccess.getPos())) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    BlockPos pos = worldGenRegion.getCenter().getWorldPosition().offset(x, 0, z);
                    final double d = this.noiseGen1.evalNoise(pos.getX(), pos.getZ()) * 8;
                    final double d2 = this.noiseGen2.evalNoise(pos.getX(), pos.getZ()) * 24;
                    double d3 = this.noiseGen3.evalNoise(pos.getX(), pos.getZ()) - 0.1;
                    d3 *= 4;

                    context.updateXZ(pos.getX(), pos.getZ());

                    int minBuildHeight = chunkAccess.getMinBuildHeight();

                    double yDev;

                    if (d3 < 0.0D) {
                        yDev = d;
                    } else if (d3 > 1.0D) {
                        yDev = d2;
                    } else {
                        yDev = d + (d2 - d) * d3;
                    }

                    for (int y = 0; y < 128; y++) {
                        if (y < 63 + yDev) {
                            BlockPos old = worldGenRegion.getCenter().getWorldPosition().offset(x, 0, z);
                            worldGenRegion.setBlock(old.atY(y), GCBlocks.MOON_ROCK.defaultBlockState(), 3);
                        }
                    }
//                    int p = chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) + 1;

//                    for(int y = p; y >= minBuildHeight; --y) {
//                        context.updateY(1, 1, y + 1, pos.getX(), y, pos.getZ());
//                        BlockState floor = bedrockFloor.tryApply(pos.getX(), y, pos.getZ());
//                        if (floor != null)
//                            worldGenRegion.setBlock(pos.atY(y), floor, 3);
//                    }
                }
            }
        }
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion worldGenRegion) {

    }

    @Override
    public int getGenDepth() {
        return 0;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkAccess) {
        return CompletableFuture.completedFuture(chunkAccess);
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types types, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        return 0;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos blockPos) {

    }
}
