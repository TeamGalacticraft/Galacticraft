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

package com.hrznstudio.galacticraft.world.gen.chunk;

import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.structure.GalacticraftStructures;
import com.hrznstudio.galacticraft.world.biome.source.MoonBiomeSource;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSamplingSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.NoiseSlideSettings;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.structures.JigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.levelgen.synth.SurfaceNoise;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public final class MoonChunkGenerator extends ChunkGenerator {
    public static final Codec<MoonChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> instance.group(MoonBiomeSource.CODEC.fieldOf("biome_source").forGetter((moonChunkGenerator) -> (MoonBiomeSource) moonChunkGenerator.runtimeBiomeSource), Codec.LONG.fieldOf("seed").stable().forGetter((moonChunkGenerator) -> moonChunkGenerator.seed)).apply(instance, instance.stable(MoonChunkGenerator::new)));

    private static final BlockState BEDROCK = Blocks.BEDROCK.defaultBlockState();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    protected final WorldgenRandom random;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    protected final Supplier<NoiseGeneratorSettings> settingsSupplier;
    private final int verticalNoiseResolution;
    private final int horizontalNoiseResolution;
    private final int noiseSizeX;
    private final int noiseSizeY;
    private final int noiseSizeZ;
    private final PerlinNoise lowerInterpolatedNoise;
    private final PerlinNoise upperInterpolatedNoise;
    private final PerlinNoise interpolationNoise;
    private final SurfaceNoise surfaceDepthNoise;
    private final long seed;
    private final int height;

    public MoonChunkGenerator(MoonBiomeSource biomeSource, long seed) {
        this(biomeSource, seed, () -> new NoiseGeneratorSettings(
                new StructureSettings(false),
                new NoiseSettings(
                        256, new NoiseSamplingSettings(0.8239043235D, 0.826137924865D, 120.0D, 140.0D),
                        new NoiseSlideSettings(-10, 3, 0), new NoiseSlideSettings(-30, 2, -1),
                        1, 2, 1.0D, -0.46875D, true,
                        false, false, false),
                GalacticraftBlocks.MOON_ROCKS[0].defaultBlockState(), AIR, -10, 0, 63, false));
    }

    private MoonChunkGenerator(BiomeSource biomeSource, long seed, @NotNull Supplier<NoiseGeneratorSettings> settingsSupplier) {
        super(biomeSource, biomeSource, settingsSupplier.get().structureSettings(), seed);
        this.seed = seed;
        NoiseGeneratorSettings settings = settingsSupplier.get();
        this.settingsSupplier = settingsSupplier;
        NoiseSettings shapeConfig = settings.noiseSettings();
        this.height = shapeConfig.height();
        this.verticalNoiseResolution = shapeConfig.noiseSizeVertical() * 4;
        this.horizontalNoiseResolution = shapeConfig.noiseSizeHorizontal() * 4;
        this.defaultBlock = settings.getDefaultBlock();
        this.defaultFluid = settings.getDefaultFluid();
        this.noiseSizeX = 16 / this.horizontalNoiseResolution;
        this.noiseSizeY = shapeConfig.height() / this.verticalNoiseResolution;
        this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
        this.random = new WorldgenRandom(seed);
        this.lowerInterpolatedNoise = new PerlinNoise(this.random, IntStream.rangeClosed(-15, 0));
        this.upperInterpolatedNoise = new PerlinNoise(this.random, IntStream.rangeClosed(-15, 0));
        this.interpolationNoise = new PerlinNoise(this.random, IntStream.rangeClosed(-7, 0));
        this.surfaceDepthNoise = new PerlinSimplexNoise(this.random, IntStream.rangeClosed(-3, 0));
        this.random.consumeCount(2620);
    }

    private static double getNoiseWeight(int i, int j, int k) {
        int l = i + 12;
        int m = j + 12;
        int n = k + 12;
        if (l >= 0 && l < 24) {
            if (m >= 0 && m < 24) {
                return n >= 0 && n < 24 ? (double) NoiseBasedChunkGenerator.BEARD_KERNEL[n * 24 * 24 + l * 24 + m] : 0.0D;
            } else {
                return 0.0D;
            }
        } else {
            return 0.0D;
        }
    }

    @Override
    protected Codec<? extends MoonChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ChunkGenerator withSeed(long seed) {
        return new MoonChunkGenerator(this.runtimeBiomeSource.withSeed(seed), seed, this.settingsSupplier);
    }

    private double sampleNoise(int x, int y, int z, double horizontalScale, double verticalScale, double horizontalStretch, double verticalStretch) {
        double d = 0.0D;
        double e = 0.0D;
        double f = 0.0D;
        double g = 1.0D;

        for (int i = 0; i < 16; ++i) {
            double h = PerlinNoise.wrap((double) x * horizontalScale * g);
            double j = PerlinNoise.wrap((double) y * verticalScale * g);
            double k = PerlinNoise.wrap((double) z * horizontalScale * g);
            double l = verticalScale * g;
            ImprovedNoise perlinNoiseSampler = this.lowerInterpolatedNoise.getOctaveNoise(i);
            if (perlinNoiseSampler != null) {
                d += perlinNoiseSampler.noise(h, j, k, l, (double) y * l) / g;
            }

            ImprovedNoise perlinNoiseSampler2 = this.upperInterpolatedNoise.getOctaveNoise(i);
            if (perlinNoiseSampler2 != null) {
                e += perlinNoiseSampler2.noise(h, j, k, l, (double) y * l) / g;
            }

            if (i < 8) {
                ImprovedNoise perlinNoiseSampler3 = this.interpolationNoise.getOctaveNoise(i);
                if (perlinNoiseSampler3 != null) {
                    f += perlinNoiseSampler3.noise(PerlinNoise.wrap((double) x * horizontalStretch * g), PerlinNoise.wrap((double) y * verticalStretch * g), PerlinNoise.wrap((double) z * horizontalStretch * g), verticalStretch * g, (double) y * verticalStretch * g) / g;
                }
            }

            g /= 2.0D;
        }

        return Mth.clampedLerp(d / 512.0D, e / 512.0D, (f / 10.0D + 1.0D) / 2.0D);
    }

    private double[] sampleNoiseColumn(int x, int z) {
        double[] ds = new double[this.noiseSizeY + 1];
        this.sampleNoiseColumn(ds, x, z);
        return ds;
    }

    private void sampleNoiseColumn(double[] buffer, int x, int z) {
        NoiseSettings shapeConfig = this.settingsSupplier.get().noiseSettings();

        float a = 0.0F;
        float b = 0.0F;
        float c = 0.0F;
        int seaLevel = this.getSeaLevel();
        float depth = this.runtimeBiomeSource.getNoiseBiome(x, seaLevel, z).getDepth();
        for (int m = -2; m <= 2; ++m) {
            for (int n = -2; n <= 2; ++n) {
                Biome biome = this.runtimeBiomeSource.getNoiseBiome(x + m, seaLevel, z + n);
                float biomeDepth = biome.getDepth();
                float u = biomeDepth > depth ? 0.5F : 1.0F;
                float v = u * NoiseBasedChunkGenerator.BIOME_WEIGHTS[m + 2 + (n + 2) * 5] / (biomeDepth + 2.0F);
                a += biome.getScale() * v;
                b += biomeDepth * v;
                c += v;
            }
        }

        float w = b / c;
        float y = a / c;
        double ai = w * 0.5F - 0.125F;
        double aj = y * 0.9F + 0.1F;
        double ac = ai * 0.265625D;
        double ad = 96.0D / aj;

        double xzScale = 684.412D * shapeConfig.noiseSamplingSettings().xzScale();
        double yScale = 684.412D * shapeConfig.noiseSamplingSettings().yScale();
        double xzFactor = xzScale / shapeConfig.noiseSamplingSettings().xzFactor();
        double yFactor = yScale / shapeConfig.noiseSamplingSettings().yFactor();
        ai = shapeConfig.topSlideSettings().target();
        aj = shapeConfig.topSlideSettings().size();
        final double topOffset = shapeConfig.topSlideSettings().offset();
        final double target = shapeConfig.bottomSlideSettings().target();
        final double size = shapeConfig.bottomSlideSettings().size();
        final double bottomOffset = shapeConfig.bottomSlideSettings().offset();
        final double densityFactor = shapeConfig.densityFactor();
        final double densityOffset = shapeConfig.densityOffset();

        for (int i = 0; i <= this.noiseSizeY; ++i) {
            double noise = this.sampleNoise(x, i, z, xzScale, yScale, xzFactor, yFactor);
            double at = 1.0D - (double) i * 2.0D / (double) this.noiseSizeY;
            double au = at * densityFactor + densityOffset;
            double av = (au + ac) * ad;
            if (av > 0.0D) {
                noise += av * 4.0D;
            } else {
                noise += av;
            }

            double ax;
            if (aj > 0.0D) {
                ax = ((double) (this.noiseSizeY - i) - topOffset) / aj;
                noise = Mth.clampedLerp(ai, noise, ax);
            }

            if (size > 0.0D) {
                ax = ((double) i - bottomOffset) / size;
                noise = Mth.clampedLerp(target, noise, ax);
            }

            buffer[i] = noise;
        }
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types heightmapType) {
        return this.sampleHeightmap(x, z, null, heightmapType.isOpaque());
    }

    @Override
    public BlockGetter getBaseColumn(int x, int z) {
        BlockState[] blockStates = new BlockState[this.noiseSizeY * this.verticalNoiseResolution];
        this.sampleHeightmap(x, z, blockStates, null);
        return new NoiseColumn(blockStates);
    }

    private int sampleHeightmap(int x, int z, @Nullable BlockState[] states, @Nullable Predicate<BlockState> predicate) {
        int i = Math.floorDiv(x, this.horizontalNoiseResolution);
        int j = Math.floorDiv(z, this.horizontalNoiseResolution);
        int k = Math.floorMod(x, this.horizontalNoiseResolution);
        int l = Math.floorMod(z, this.horizontalNoiseResolution);
        double d = (double) k / (double) this.horizontalNoiseResolution;
        double e = (double) l / (double) this.horizontalNoiseResolution;
        double[][] ds = new double[][]{this.sampleNoiseColumn(i, j), this.sampleNoiseColumn(i, j + 1), this.sampleNoiseColumn(i + 1, j), this.sampleNoiseColumn(i + 1, j + 1)};

        for (int m = this.noiseSizeY - 1; m >= 0; --m) {
            double f = ds[0][m];
            double g = ds[1][m];
            double h = ds[2][m];
            double n = ds[3][m];
            double o = ds[0][m + 1];
            double p = ds[1][m + 1];
            double q = ds[2][m + 1];
            double r = ds[3][m + 1];

            for (int s = this.verticalNoiseResolution - 1; s >= 0; --s) {
                double t = (double) s / (double) this.verticalNoiseResolution;
                double density = Mth.lerp3(t, d, e, f, o, h, q, g, p, n, r);
                int y = m * this.verticalNoiseResolution + s;
                BlockState blockState = this.getBlockState(density);
                if (states != null) {
                    states[y] = blockState;
                }

                if (predicate != null && predicate.test(blockState)) {
                    return y + 1;
                }
            }
        }

        return 0;
    }

    protected BlockState getBlockState(double density) {
        BlockState blockState3;
        if (density > 0.0D) {
            blockState3 = this.defaultBlock;
        } else {
            blockState3 = AIR;
        }

        return blockState3;
    }

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion region, ChunkAccess chunk) {
        ChunkPos pos = chunk.getPos();
        int cX = pos.x;
        int cZ = pos.z;
        int startX = pos.getMinBlockX();
        int startZ = pos.getMinBlockZ();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        WorldgenRandom chunkRandom = new WorldgenRandom();
        chunkRandom.setBaseChunkSeed(cX, cZ);

        for (int innerX = 0; innerX < 16; ++innerX) {
            for (int innerZ = 0; innerZ < 16; ++innerZ) {
                int x = startX + innerX;
                int z = startZ + innerZ;
                int y = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, innerX, innerZ) + 1;
                double e = this.surfaceDepthNoise.getSurfaceNoiseValue((double) x * 0.0625D, (double) z * 0.0625D, 0.0625D, (double) innerX * 0.0625D) * 15.0D;
                region.getBiome(mutable.set(startX + innerX, y, startZ + innerZ)).buildSurfaceAt(chunkRandom, chunk, x, z, y, e, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), region.getSeed());
            }
        }

        this.buildBedrock(chunk, chunkRandom);
    }

    private void buildBedrock(ChunkAccess chunk, Random random) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int startX = chunk.getPos().getMinBlockX();
        int startZ = chunk.getPos().getMinBlockZ();
        NoiseGeneratorSettings chunkGeneratorSettings = settingsSupplier.get();
        int y = chunkGeneratorSettings.getBedrockFloorPosition();
        boolean bl2 = y + 4 >= 0 && y < this.height;
        if (bl2) {
            for (BlockPos pos : BlockPos.betweenClosed(startX, 0, startZ, startX + 15, 0, startZ + 15)) {
                for(int o = 4; o >= 0; --o) {
                    if (o <= random.nextInt(5)) {
                        chunk.setBlockState(mutable.set(pos.getX(), y + o, pos.getZ()), BEDROCK, false);
                    }
                }
            }
        }
    }

    @Override
    public void fillFromNoise(LevelAccessor world, StructureFeatureManager accessor, ChunkAccess chunk) {
        ObjectList<StructurePiece> structurePieces = new ObjectArrayList<>(10);
        ObjectList<JigsawJunction> jigsawJunctions = new ObjectArrayList<>(32);
        ChunkPos chunkPos = chunk.getPos();
        int cX = chunkPos.x;
        int cZ = chunkPos.z;
        int x = cX << 4;
        int z = cZ << 4;

        for (StructureFeature<?> feature : StructureFeature.NOISE_AFFECTING_FEATURES) {
            accessor.startsForFeature(SectionPos.of(chunkPos, 0), feature).forEach((start) -> {
                Iterator<StructurePiece> children = start.getPieces().iterator();

                while (true) {
                    StructurePiece piece;
                    do {
                        if (!children.hasNext()) {
                            return;
                        }

                        piece = children.next();
                    } while (!piece.isCloseToChunk(chunkPos, 12));

                    if (piece instanceof PoolElementStructurePiece) {
                        PoolElementStructurePiece poolPiece = (PoolElementStructurePiece) piece;
                        if (poolPiece.getElement().getProjection() == StructureTemplatePool.Projection.RIGID) {
                            structurePieces.add(poolPiece);
                        }

                        for (JigsawJunction jigsawJunction : poolPiece.getJunctions()) {
                            int sourceX = jigsawJunction.getSourceX();
                            int sourceZ = jigsawJunction.getSourceZ();
                            if (sourceX > x - 12 && sourceZ > z - 12 && sourceX < x + 15 + 12 && sourceZ < z + 15 + 12) {
                                jigsawJunctions.add(jigsawJunction);
                            }
                        }
                    } else {
                        structurePieces.add(piece);
                    }
                }
            });
        }

        double[][][] ds = new double[2][this.noiseSizeZ + 1][this.noiseSizeY + 1];

        for (int m = 0; m < this.noiseSizeZ + 1; ++m) {
            ds[0][m] = new double[this.noiseSizeY + 1];
            this.sampleNoiseColumn(ds[0][m], cX * this.noiseSizeX, cZ * this.noiseSizeZ + m);
            ds[1][m] = new double[this.noiseSizeY + 1];
        }

        ProtoChunk protoChunk = (ProtoChunk) chunk;
        Heightmap heightmap = protoChunk.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = protoChunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        ObjectListIterator<StructurePiece> structurePieceIterator = structurePieces.iterator();
        ObjectListIterator<JigsawJunction> junctionIterator = jigsawJunctions.iterator();

        for (int n = 0; n < this.noiseSizeX; ++n) {
            int p;
            for (p = 0; p < this.noiseSizeZ + 1; ++p) {
                this.sampleNoiseColumn(ds[1][p], cX * this.noiseSizeX + n + 1, cZ * this.noiseSizeZ + p);
            }

            for (p = 0; p < this.noiseSizeZ; ++p) {
                LevelChunkSection chunkSection = protoChunk.getOrCreateSection(15);
                chunkSection.acquire();

                for (int q = this.noiseSizeY - 1; q >= 0; --q) {
                    double d = ds[0][p][q];
                    double e = ds[0][p + 1][q];
                    double f = ds[1][p][q];
                    double g = ds[1][p + 1][q];
                    double h = ds[0][p][q + 1];
                    double r = ds[0][p + 1][q + 1];
                    double s = ds[1][p][q + 1];
                    double t = ds[1][p + 1][q + 1];

                    for (int u = this.verticalNoiseResolution - 1; u >= 0; --u) {
                        int y2 = q * this.verticalNoiseResolution + u;
                        int w = y2 & 15;
                        int y = y2 >> 4;
                        if (chunkSection.bottomBlockY() >> 4 != y) {
                            chunkSection.release();
                            chunkSection = protoChunk.getOrCreateSection(y);
                            chunkSection.acquire();
                        }

                        double az = (double) u / (double) this.verticalNoiseResolution;
                        double ax = Mth.lerp(az, d, h);
                        double aa = Mth.lerp(az, f, s);
                        double ab = Mth.lerp(az, e, r);
                        double ac = Mth.lerp(az, g, t);

                        for (int ad = 0; ad < this.horizontalNoiseResolution; ++ad) {
                            int ae = x + n * this.horizontalNoiseResolution + ad;
                            int x2 = ae & 15;
                            double ag = (double) ad / (double) this.horizontalNoiseResolution;
                            double ah = Mth.lerp(ag, ax, aa);
                            double ai = Mth.lerp(ag, ab, ac);

                            for (int aj = 0; aj < this.horizontalNoiseResolution; ++aj) {
                                int ak = z + p * this.horizontalNoiseResolution + aj;
                                int z2 = ak & 15;
                                double am = (double) aj / (double) this.horizontalNoiseResolution;
                                double an = Mth.lerp(am, ah, ai);
                                double density = Mth.clamp(an / 200.0D, -1.0D, 1.0D);

                                int at;
                                int au;
                                int ar;
                                for (density = density / 2.0D - density * density * density / 24.0D; structurePieceIterator.hasNext(); density += getNoiseWeight(at, au, ar) * 0.8D) {
                                    StructurePiece structurePiece = structurePieceIterator.next();
                                    BoundingBox bounds = structurePiece.getBoundingBox();
                                    at = Math.max(0, Math.max(bounds.x0 - ae, ae - bounds.x1));
                                    au = y2 - (bounds.y0 + (structurePiece instanceof PoolElementStructurePiece ? ((PoolElementStructurePiece) structurePiece).getGroundLevelDelta() : 0));
                                    ar = Math.max(0, Math.max(bounds.z0 - ak, ak - bounds.z1));
                                }

                                structurePieceIterator.back(structurePieces.size());

                                while (junctionIterator.hasNext()) {
                                    JigsawJunction jigsawJunction = junctionIterator.next();
                                    int as = ae - jigsawJunction.getSourceX();
                                    at = y2 - jigsawJunction.getSourceGroundY();
                                    au = ak - jigsawJunction.getSourceZ();
                                    density += getNoiseWeight(as, at, au) * 0.4D;
                                }

                                junctionIterator.back(jigsawJunctions.size());
                                BlockState state = this.getBlockState(density);
                                if (state != AIR) {
                                    if (state.getLightEmission() != 0) {
                                        mutable.set(ae, y2, ak);
                                        protoChunk.addLight(mutable);
                                    }

                                    chunkSection.setBlockState(x2, w, z2, state, false);
                                    heightmap.update(x2, y2, z2, state);
                                    heightmap2.update(x2, y2, z2, state);
                                }
                            }
                        }
                    }
                }

                chunkSection.release();
            }

            double[][] es = ds[0];
            ds[0] = ds[1];
            ds[1] = es;
        }

    }

    @Override
    public int getSpawnHeight() {
        return 80;
    }

    @Override
    public int getGenDepth() {
        return this.height;
    }

    public int getSeaLevel() {
        return 0;
    }

    @Override
    public List<MobSpawnSettings.SpawnerData> getMobsAt(Biome biome, StructureFeatureManager accessor, MobCategory group, BlockPos pos) {
        if (group == MobCategory.MONSTER) {
            if (accessor.getStructureAt(pos, false, GalacticraftStructures.MOON_PILLAGER_BASE_FEATURE).isValid()) {
                return GalacticraftStructures.MOON_PILLAGER_BASE_FEATURE.getSpecialEnemies();
            }

            if (accessor.getStructureAt(pos, false, GalacticraftStructures.MOON_RUINS).isValid()) {
                return GalacticraftStructures.MOON_RUINS.getSpecialEnemies();
            }
        }

        return super.getMobsAt(biome, accessor, group, pos);
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        int i = region.getCenterX();
        int j = region.getCenterZ();
        Biome biome = region.getBiome(new ChunkPos(i, j).getWorldPosition());
        WorldgenRandom chunkRandom = new WorldgenRandom();
        chunkRandom.setDecorationSeed(region.getSeed(), i << 4, j << 4);
        NaturalSpawner.spawnMobsForChunkGeneration(region, biome, i, j, chunkRandom);
    }

    @Override
    public void applyCarvers(long seed, BiomeManager access, ChunkAccess chunk, GenerationStep.Carving carver) {
        super.applyCarvers(seed, access, chunk, carver);
        addCraters(chunk);
    }

    private void addCraters(ChunkAccess chunk) {
        for (int cx = chunk.getPos().x - 2; cx <= chunk.getPos().x + 2; cx++) {
            for (int cz = chunk.getPos().z - 2; cz <= chunk.getPos().z + 2; cz++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        if (Math.abs(this.randFromPoint((cx << 4) + x, (cz << 4) + z)) < this.sampleDepthNoise(x << 4 + x, cz << 4 + z) / 32.0D) {
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

    private double randFromPoint(int x, int z) {
        int n;
        n = x + z * 57;
        n = n << 13 ^ n;
        return 1.0D - (n * (n * n * 15731 + 789221) + 1376312589 & 0x7fffffff) / 1073741824.0;
    }

    private double sampleDepthNoise(int x, int z) {
        double d = this.interpolationNoise.getValue(x * 200, 10.0D, z * 200, 1.0D, 0.0D, true) * 65535.0D / 8000.0D;
        if (d < 0.0D) {
            d = -d * 0.3D;
        }

        d = d * 3.0D - 2.0D;
        if (d < 0.0D) {
            d /= 28.0D;
        } else {
            if (d > 1.0D) {
                d = 1.0D;
            }

            d /= 40.0D;
        }

        return d;
    }

    private void makeCrater(int craterX, int craterZ, int chunkX, int chunkZ, int size, ChunkAccess chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double xDev = craterX - (chunkX + x);
                double zDev = craterZ - (chunkZ + z);
                if (xDev * xDev + zDev * zDev < size * size) {
                    xDev /= size;
                    zDev /= size;
                    final double sqrtY = xDev * xDev + zDev * zDev;
                    final double maxDepth = 5 - (sqrtY * sqrtY * 6);
                    double depth = 0.0D;
                    for (int y = 127; y > 0 && depth < maxDepth; y--) {
                        if (!chunk.getBlockState(new BlockPos(x, y, z)).isAir() || depth == 0.0D) {
                            chunk.setBlockState(new BlockPos(x, y, z), AIR, false);
                            depth += 1.5d;
                        }
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockPos findNearestMapFeature(ServerLevel world, StructureFeature<?> feature, BlockPos center, int radius, boolean skipExistingChunks) {
        return super.findNearestMapFeature(world, feature, center, radius, skipExistingChunks);
    }
}
