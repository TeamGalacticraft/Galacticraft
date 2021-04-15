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
import dev.galacticraft.mod.block.GalacticraftBlocks;
import dev.galacticraft.mod.structure.GalacticraftStructures;
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

public final class MoonChunkGenerator extends ChunkGenerator {
    public static final Codec<MoonChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> instance.group(MoonBiomeSource.CODEC.fieldOf("biome_source").forGetter((moonChunkGenerator) -> (MoonBiomeSource) moonChunkGenerator.biomeSource), Codec.LONG.fieldOf("seed").stable().forGetter((moonChunkGenerator) -> moonChunkGenerator.seed)).apply(instance, instance.stable(MoonChunkGenerator::new)));

    private static final BlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected final ChunkRandom random;
    protected final BlockState defaultBlock;
    protected final BlockState defaultFluid;
    protected final Supplier<ChunkGeneratorSettings> settingsSupplier;
    private final int verticalNoiseResolution;
    private final int horizontalNoiseResolution;
    private final int noiseSizeX;
    private final int noiseSizeY;
    private final int noiseSizeZ;
    private final OctavePerlinNoiseSampler lowerInterpolatedNoise;
    private final OctavePerlinNoiseSampler upperInterpolatedNoise;
    private final OctavePerlinNoiseSampler interpolationNoise;
    private final NoiseSampler surfaceDepthNoise;
    private final long seed;
    private final int height;

    public MoonChunkGenerator(MoonBiomeSource biomeSource, long seed) {
        this(biomeSource, seed, () -> new ChunkGeneratorSettings(
                new StructuresConfig(false),
                new GenerationShapeConfig(
                        256, new NoiseSamplingConfig(0.8239043235D, 0.826137924865D, 120.0D, 140.0D),
                        new SlideConfig(-10, 3, 0), new SlideConfig(-30, 2, -1),
                        1, 2, 1.0D, -0.46875D, true,
                        false, false, false),
                GalacticraftBlocks.MOON_ROCKS[0].getDefaultState(), AIR, -10, 0, 63, false));
    }

    private MoonChunkGenerator(BiomeSource biomeSource, long seed, @NotNull Supplier<ChunkGeneratorSettings> settingsSupplier) {
        super(biomeSource, biomeSource, settingsSupplier.get().getStructuresConfig(), seed);
        this.seed = seed;
        ChunkGeneratorSettings settings = settingsSupplier.get();
        this.settingsSupplier = settingsSupplier;
        GenerationShapeConfig shapeConfig = settings.getGenerationShapeConfig();
        this.height = shapeConfig.getHeight();
        this.verticalNoiseResolution = shapeConfig.getSizeVertical() * 4;
        this.horizontalNoiseResolution = shapeConfig.getSizeHorizontal() * 4;
        this.defaultBlock = settings.getDefaultBlock();
        this.defaultFluid = settings.getDefaultFluid();
        this.noiseSizeX = 16 / this.horizontalNoiseResolution;
        this.noiseSizeY = shapeConfig.getHeight() / this.verticalNoiseResolution;
        this.noiseSizeZ = 16 / this.horizontalNoiseResolution;
        this.random = new ChunkRandom(seed);
        this.lowerInterpolatedNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        this.upperInterpolatedNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-15, 0));
        this.interpolationNoise = new OctavePerlinNoiseSampler(this.random, IntStream.rangeClosed(-7, 0));
        this.surfaceDepthNoise = new OctaveSimplexNoiseSampler(this.random, IntStream.rangeClosed(-3, 0));
        this.random.consume(2620);
    }

    private static double getNoiseWeight(int i, int j, int k) {
        int l = i + 12;
        int m = j + 12;
        int n = k + 12;
        if (l >= 0 && l < 24) {
            if (m >= 0 && m < 24) {
                return n >= 0 && n < 24 ? (double) NoiseChunkGenerator.NOISE_WEIGHT_TABLE[n * 24 * 24 + l * 24 + m] : 0.0D;
            } else {
                return 0.0D;
            }
        } else {
            return 0.0D;
        }
    }

    @Override
    protected Codec<? extends MoonChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ChunkGenerator withSeed(long seed) {
        return new MoonChunkGenerator(this.biomeSource.withSeed(seed), seed, this.settingsSupplier);
    }

    private double sampleNoise(int x, int y, int z, double horizontalScale, double verticalScale, double horizontalStretch, double verticalStretch) {
        double d = 0.0D;
        double e = 0.0D;
        double f = 0.0D;
        double g = 1.0D;

        for (int i = 0; i < 16; ++i) {
            double h = OctavePerlinNoiseSampler.maintainPrecision((double) x * horizontalScale * g);
            double j = OctavePerlinNoiseSampler.maintainPrecision((double) y * verticalScale * g);
            double k = OctavePerlinNoiseSampler.maintainPrecision((double) z * horizontalScale * g);
            double l = verticalScale * g;
            PerlinNoiseSampler perlinNoiseSampler = this.lowerInterpolatedNoise.getOctave(i);
            if (perlinNoiseSampler != null) {
                d += perlinNoiseSampler.sample(h, j, k, l, (double) y * l) / g;
            }

            PerlinNoiseSampler perlinNoiseSampler2 = this.upperInterpolatedNoise.getOctave(i);
            if (perlinNoiseSampler2 != null) {
                e += perlinNoiseSampler2.sample(h, j, k, l, (double) y * l) / g;
            }

            if (i < 8) {
                PerlinNoiseSampler perlinNoiseSampler3 = this.interpolationNoise.getOctave(i);
                if (perlinNoiseSampler3 != null) {
                    f += perlinNoiseSampler3.sample(OctavePerlinNoiseSampler.maintainPrecision((double) x * horizontalStretch * g), OctavePerlinNoiseSampler.maintainPrecision((double) y * verticalStretch * g), OctavePerlinNoiseSampler.maintainPrecision((double) z * horizontalStretch * g), verticalStretch * g, (double) y * verticalStretch * g) / g;
                }
            }

            g /= 2.0D;
        }

        return MathHelper.clampedLerp(d / 512.0D, e / 512.0D, (f / 10.0D + 1.0D) / 2.0D);
    }

    private double[] sampleNoiseColumn(int x, int z) {
        double[] ds = new double[this.noiseSizeY + 1];
        this.sampleNoiseColumn(ds, x, z);
        return ds;
    }

    private void sampleNoiseColumn(double[] buffer, int x, int z) {
        GenerationShapeConfig shapeConfig = this.settingsSupplier.get().getGenerationShapeConfig();

        float a = 0.0F;
        float b = 0.0F;
        float c = 0.0F;
        int seaLevel = this.getSeaLevel();
        float depth = this.biomeSource.getBiomeForNoiseGen(x, seaLevel, z).getDepth();
        for (int m = -2; m <= 2; ++m) {
            for (int n = -2; n <= 2; ++n) {
                Biome biome = this.biomeSource.getBiomeForNoiseGen(x + m, seaLevel, z + n);
                float biomeDepth = biome.getDepth();
                float u = biomeDepth > depth ? 0.5F : 1.0F;
                float v = u * NoiseChunkGenerator.BIOME_WEIGHT_TABLE[m + 2 + (n + 2) * 5] / (biomeDepth + 2.0F);
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

        double xzScale = 684.412D * shapeConfig.getSampling().getXZScale();
        double yScale = 684.412D * shapeConfig.getSampling().getYScale();
        double xzFactor = xzScale / shapeConfig.getSampling().getXZFactor();
        double yFactor = yScale / shapeConfig.getSampling().getYFactor();
        ai = shapeConfig.getTopSlide().getTarget();
        aj = shapeConfig.getTopSlide().getSize();
        final double topOffset = shapeConfig.getTopSlide().getOffset();
        final double target = shapeConfig.getBottomSlide().getTarget();
        final double size = shapeConfig.getBottomSlide().getSize();
        final double bottomOffset = shapeConfig.getBottomSlide().getOffset();
        final double densityFactor = shapeConfig.getDensityFactor();
        final double densityOffset = shapeConfig.getDensityOffset();

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
                noise = MathHelper.clampedLerp(ai, noise, ax);
            }

            if (size > 0.0D) {
                ax = ((double) i - bottomOffset) / size;
                noise = MathHelper.clampedLerp(target, noise, ax);
            }

            buffer[i] = noise;
        }
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmapType) {
        return this.sampleHeightmap(x, z, null, heightmapType.getBlockPredicate());
    }

    @Override
    public BlockView getColumnSample(int x, int z) {
        BlockState[] blockStates = new BlockState[this.noiseSizeY * this.verticalNoiseResolution];
        this.sampleHeightmap(x, z, blockStates, null);
        return new VerticalBlockSample(blockStates);
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
                double density = MathHelper.lerp3(t, d, e, f, o, h, q, g, p, n, r);
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
    public void buildSurface(ChunkRegion region, Chunk chunk) {
        ChunkPos pos = chunk.getPos();
        int cX = pos.x;
        int cZ = pos.z;
        int startX = pos.getStartX();
        int startZ = pos.getStartZ();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        ChunkRandom chunkRandom = new ChunkRandom();
        chunkRandom.setTerrainSeed(cX, cZ);

        for (int innerX = 0; innerX < 16; ++innerX) {
            for (int innerZ = 0; innerZ < 16; ++innerZ) {
                int x = startX + innerX;
                int z = startZ + innerZ;
                int y = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, innerX, innerZ) + 1;
                double e = this.surfaceDepthNoise.sample((double) x * 0.0625D, (double) z * 0.0625D, 0.0625D, (double) innerX * 0.0625D) * 15.0D;
                region.getBiome(mutable.set(startX + innerX, y, startZ + innerZ)).buildSurface(chunkRandom, chunk, x, z, y, e, this.defaultBlock, this.defaultFluid, this.getSeaLevel(), region.getSeed());
            }
        }

        this.buildBedrock(chunk, chunkRandom);
    }

    private void buildBedrock(Chunk chunk, Random random) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int startX = chunk.getPos().getStartX();
        int startZ = chunk.getPos().getStartZ();
        ChunkGeneratorSettings chunkGeneratorSettings = settingsSupplier.get();
        int y = chunkGeneratorSettings.getBedrockFloorY();
        boolean bl2 = y + 4 >= 0 && y < this.height;
        if (bl2) {
            for (BlockPos pos : BlockPos.iterate(startX, 0, startZ, startX + 15, 0, startZ + 15)) {
                for(int o = 4; o >= 0; --o) {
                    if (o <= random.nextInt(5)) {
                        chunk.setBlockState(mutable.set(pos.getX(), y + o, pos.getZ()), BEDROCK, false);
                    }
                }
            }
        }
    }

    @Override
    public void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk) {
        ObjectList<StructurePiece> structurePieces = new ObjectArrayList<>(10);
        ObjectList<JigsawJunction> jigsawJunctions = new ObjectArrayList<>(32);
        ChunkPos chunkPos = chunk.getPos();
        int cX = chunkPos.x;
        int cZ = chunkPos.z;
        int x = cX << 4;
        int z = cZ << 4;

        for (StructureFeature<?> feature : StructureFeature.JIGSAW_STRUCTURES) {
            accessor.getStructuresWithChildren(ChunkSectionPos.from(chunkPos, 0), feature).forEach((start) -> {
                Iterator<StructurePiece> children = start.getChildren().iterator();

                while (true) {
                    StructurePiece piece;
                    do {
                        if (!children.hasNext()) {
                            return;
                        }

                        piece = children.next();
                    } while (!piece.intersectsChunk(chunkPos, 12));

                    if (piece instanceof PoolStructurePiece) {
                        PoolStructurePiece poolPiece = (PoolStructurePiece) piece;
                        if (poolPiece.getPoolElement().getProjection() == StructurePool.Projection.RIGID) {
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
        Heightmap heightmap = protoChunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = protoChunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        ObjectListIterator<StructurePiece> structurePieceIterator = structurePieces.iterator();
        ObjectListIterator<JigsawJunction> junctionIterator = jigsawJunctions.iterator();

        for (int n = 0; n < this.noiseSizeX; ++n) {
            int p;
            for (p = 0; p < this.noiseSizeZ + 1; ++p) {
                this.sampleNoiseColumn(ds[1][p], cX * this.noiseSizeX + n + 1, cZ * this.noiseSizeZ + p);
            }

            for (p = 0; p < this.noiseSizeZ; ++p) {
                ChunkSection chunkSection = protoChunk.getSection(15);
                chunkSection.lock();

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
                        if (chunkSection.getYOffset() >> 4 != y) {
                            chunkSection.unlock();
                            chunkSection = protoChunk.getSection(y);
                            chunkSection.lock();
                        }

                        double az = (double) u / (double) this.verticalNoiseResolution;
                        double ax = MathHelper.lerp(az, d, h);
                        double aa = MathHelper.lerp(az, f, s);
                        double ab = MathHelper.lerp(az, e, r);
                        double ac = MathHelper.lerp(az, g, t);

                        for (int ad = 0; ad < this.horizontalNoiseResolution; ++ad) {
                            int ae = x + n * this.horizontalNoiseResolution + ad;
                            int x2 = ae & 15;
                            double ag = (double) ad / (double) this.horizontalNoiseResolution;
                            double ah = MathHelper.lerp(ag, ax, aa);
                            double ai = MathHelper.lerp(ag, ab, ac);

                            for (int aj = 0; aj < this.horizontalNoiseResolution; ++aj) {
                                int ak = z + p * this.horizontalNoiseResolution + aj;
                                int z2 = ak & 15;
                                double am = (double) aj / (double) this.horizontalNoiseResolution;
                                double an = MathHelper.lerp(am, ah, ai);
                                double density = MathHelper.clamp(an / 200.0D, -1.0D, 1.0D);

                                int at;
                                int au;
                                int ar;
                                for (density = density / 2.0D - density * density * density / 24.0D; structurePieceIterator.hasNext(); density += getNoiseWeight(at, au, ar) * 0.8D) {
                                    StructurePiece structurePiece = structurePieceIterator.next();
                                    BlockBox bounds = structurePiece.getBoundingBox();
                                    at = Math.max(0, Math.max(bounds.minX - ae, ae - bounds.maxX));
                                    au = y2 - (bounds.minY + (structurePiece instanceof PoolStructurePiece ? ((PoolStructurePiece) structurePiece).getGroundLevelDelta() : 0));
                                    ar = Math.max(0, Math.max(bounds.minZ - ak, ak - bounds.maxZ));
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
                                    if (state.getLuminance() != 0) {
                                        mutable.set(ae, y2, ak);
                                        protoChunk.addLightSource(mutable);
                                    }

                                    chunkSection.setBlockState(x2, w, z2, state, false);
                                    heightmap.trackUpdate(x2, y2, z2, state);
                                    heightmap2.trackUpdate(x2, y2, z2, state);
                                }
                            }
                        }
                    }
                }

                chunkSection.unlock();
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
    public int getWorldHeight() {
        return this.height;
    }

    public int getSeaLevel() {
        return 0;
    }

    @Override
    public List<SpawnSettings.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
        if (group == SpawnGroup.MONSTER) {
            if (accessor.getStructureAt(pos, false, GalacticraftStructures.MOON_PILLAGER_BASE_FEATURE).hasChildren()) {
                return GalacticraftStructures.MOON_PILLAGER_BASE_FEATURE.getMonsterSpawns();
            }

            if (accessor.getStructureAt(pos, false, GalacticraftStructures.MOON_RUINS).hasChildren()) {
                return GalacticraftStructures.MOON_RUINS.getMonsterSpawns();
            }
        }

        return super.getEntitySpawnList(biome, accessor, group, pos);
    }

    @Override
    public void populateEntities(ChunkRegion region) {
        int i = region.getCenterChunkX();
        int j = region.getCenterChunkZ();
        Biome biome = region.getBiome(new ChunkPos(i, j).getStartPos());
        ChunkRandom chunkRandom = new ChunkRandom();
        chunkRandom.setPopulationSeed(region.getSeed(), i << 4, j << 4);
        SpawnHelper.populateEntities(region, biome, i, j, chunkRandom);
    }

    @Override
    public void carve(long seed, BiomeAccess access, Chunk chunk, GenerationStep.Carver carver) {
        super.carve(seed, access, chunk, carver);
        addCraters(chunk);
    }

    private void addCraters(Chunk chunk) {
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
        double d = this.interpolationNoise.sample(x * 200, 10.0D, z * 200, 1.0D, 0.0D, true) * 65535.0D / 8000.0D;
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

    private void makeCrater(int craterX, int craterZ, int chunkX, int chunkZ, int size, Chunk chunk) {
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
    public BlockPos locateStructure(ServerWorld world, StructureFeature<?> feature, BlockPos center, int radius, boolean skipExistingChunks) {
        return super.locateStructure(world, feature, center, radius, skipExistingChunks);
    }
}
