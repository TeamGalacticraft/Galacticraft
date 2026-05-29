package dev.galacticraft.mod.world.gen;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.Constant;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class PlanetChunkGenerator extends ChunkGenerator {
    private static final BlockState AIR;
    private final NoiseBasedChunkGenerator vanillaCarverDelegate;
    protected final Holder<NoiseGeneratorSettings> settings;
    private final Supplier<Aquifer.FluidPicker> globalFluidPicker;

    public PlanetChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource);
        this.settings = settings;
        this.globalFluidPicker = Suppliers.memoize(() -> createFluidPicker((NoiseGeneratorSettings)settings.value()));
        this.vanillaCarverDelegate = new NoiseBasedChunkGenerator(biomeSource, settings);
    }

    private static Aquifer.FluidPicker createFluidPicker(NoiseGeneratorSettings settings) {
        Aquifer.FluidStatus fluidStatus = new Aquifer.FluidStatus(-54, Blocks.LAVA.defaultBlockState());
        int i = settings.seaLevel();
        Aquifer.FluidStatus fluidStatus2 = new Aquifer.FluidStatus(i, settings.defaultFluid());
        Aquifer.FluidStatus fluidStatus3 = new Aquifer.FluidStatus(DimensionType.MIN_Y * 2, Blocks.AIR.defaultBlockState());
        return (x, y, z) -> y < Math.min(-54, i) ? fluidStatus : fluidStatus2;
    }

    public CompletableFuture<ChunkAccess> createBiomes(RandomState randomState, Blender blender, StructureManager structureManager, ChunkAccess chunkAccess) {
        return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
            this.doCreateBiomes(blender, randomState, structureManager, chunkAccess);
            return chunkAccess;
        }), Util.backgroundExecutor());
    }

    private void doCreateBiomes(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkAccess) {
        NoiseChunk noiseChunk = chunkAccess.getOrCreateNoiseChunk((chunkx) -> this.createNoiseChunk(chunkx, structureManager, blender, randomState));
        BiomeResolver biomeResolver = BelowZeroRetrogen.getBiomeResolver(blender.getBiomeResolver(this.biomeSource), chunkAccess);
        chunkAccess.fillBiomesFromNoise(biomeResolver, noiseChunk.cachedClimateSampler(randomState.router(), ((NoiseGeneratorSettings)this.settings.value()).spawnTarget()));
    }

    private NoiseChunk createNoiseChunk(ChunkAccess chunkAccess, StructureManager world, Blender blender, RandomState randomState) {
        return NoiseChunk.forChunk(chunkAccess, randomState, Beardifier.forStructuresInChunk(world, chunkAccess.getPos()), (NoiseGeneratorSettings)this.settings.value(), (Aquifer.FluidPicker)this.globalFluidPicker.get(), blender);
    }

    @Override
    protected abstract MapCodec<? extends ChunkGenerator> codec();

    public Holder<NoiseGeneratorSettings> generatorSettings() {
        return this.settings;
    }

    public boolean stable(ResourceKey<NoiseGeneratorSettings> settings) {
        return this.settings.is(settings);
    }

    public int getBaseHeight(int x, int z, Heightmap.Types heightmap, LevelHeightAccessor world, RandomState randomState) {
        return this.iterateNoiseColumn(world, randomState, x, z, (MutableObject)null, heightmap.isOpaque()).orElse(world.getMinBuildHeight());
    }

    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor world, RandomState randomState) {
        MutableObject<NoiseColumn> mutableObject = new MutableObject();
        this.iterateNoiseColumn(world, randomState, x, z, mutableObject, (Predicate)null);
        return (NoiseColumn)mutableObject.getValue();
    }

    public void addDebugScreenInfo(List<String> text, RandomState randomState, BlockPos pos) {
        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        NoiseRouter noiseRouter = randomState.router();
        DensityFunction.SinglePointContext singlePointContext = new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ());
        double d = noiseRouter.ridges().compute(singlePointContext);
        String var10001 = decimalFormat.format(noiseRouter.temperature().compute(singlePointContext));
        text.add("NoiseRouter T: " + var10001 + " V: " + decimalFormat.format(noiseRouter.vegetation().compute(singlePointContext)) + " C: " + decimalFormat.format(noiseRouter.continents().compute(singlePointContext)) + " E: " + decimalFormat.format(noiseRouter.erosion().compute(singlePointContext)) + " D: " + decimalFormat.format(noiseRouter.depth().compute(singlePointContext)) + " W: " + decimalFormat.format(d) + " PV: " + decimalFormat.format((double)NoiseRouterData.peaksAndValleys((float)d)) + " AS: " + decimalFormat.format(noiseRouter.initialDensityWithoutJaggedness().compute(singlePointContext)) + " N: " + decimalFormat.format(noiseRouter.finalDensity().compute(singlePointContext)));
    }

    private OptionalInt iterateNoiseColumn(LevelHeightAccessor world, RandomState randomState, int x, int z, @Nullable MutableObject<NoiseColumn> columnSample, @Nullable Predicate<BlockState> stopPredicate) {
        NoiseSettings noiseSettings = ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().clampToHeightAccessor(world);
        int i = noiseSettings.getCellHeight();
        int j = noiseSettings.minY();
        int k = Mth.floorDiv(j, i);
        int l = Mth.floorDiv(noiseSettings.height(), i);
        if (l <= 0) {
            return OptionalInt.empty();
        } else {
            BlockState[] blockStates;
            if (columnSample == null) {
                blockStates = null;
            } else {
                blockStates = new BlockState[noiseSettings.height()];
                columnSample.setValue(new NoiseColumn(j, blockStates));
            }

            int m = noiseSettings.getCellWidth();
            int n = Math.floorDiv(x, m);
            int o = Math.floorDiv(z, m);
            int p = Math.floorMod(x, m);
            int q = Math.floorMod(z, m);
            int r = n * m;
            int s = o * m;
            double d = (double)p / (double)m;
            double e = (double)q / (double)m;
            NoiseChunk noiseChunk = new NoiseChunk(1, randomState, r, s, noiseSettings, DensityFunctions.BeardifierMarker.INSTANCE, (NoiseGeneratorSettings)this.settings.value(), (Aquifer.FluidPicker)this.globalFluidPicker.get(), Blender.empty());
            noiseChunk.initializeForFirstCellX();
            noiseChunk.advanceCellX(0);

            for(int t = l - 1; t >= 0; --t) {
                noiseChunk.selectCellYZ(t, 0);

                for(int u = i - 1; u >= 0; --u) {
                    int v = (k + t) * i + u;
                    double f = (double)u / (double)i;
                    noiseChunk.updateForY(v, f);
                    noiseChunk.updateForX(x, d);
                    noiseChunk.updateForZ(z, e);
                    BlockState blockState = noiseChunk.getInterpolatedState();
                    BlockState blockState2 = blockState == null ? ((NoiseGeneratorSettings)this.settings.value()).defaultBlock() : blockState;
                    if (blockStates != null) {
                        int w = t * i + u;
                        blockStates[w] = blockState2;
                    }

                    if (stopPredicate != null && stopPredicate.test(blockState2)) {
                        noiseChunk.stopInterpolation();
                        return OptionalInt.of(v + 1);
                    }
                }
            }

            noiseChunk.stopInterpolation();
            return OptionalInt.empty();
        }
    }

    public void buildSurface(WorldGenRegion region, StructureManager structures, RandomState randomState, ChunkAccess chunkAccess) {
        if (!SharedConstants.debugVoidTerrain(chunkAccess.getPos())) {
            WorldGenerationContext worldGenerationContext = new WorldGenerationContext(this, region);
            this.buildSurface(chunkAccess, worldGenerationContext, randomState, structures, region.getBiomeManager(), region.registryAccess().registryOrThrow(Registries.BIOME), Blender.of(region));
        }
    }

    @VisibleForTesting
    public void buildSurface(ChunkAccess chunkAccess, WorldGenerationContext worldGenerationContext, RandomState randomState, StructureManager structureManager, BiomeManager biomeManager, Registry<Biome> biomeRegistry, Blender blender) {
        NoiseChunk noiseChunk = chunkAccess.getOrCreateNoiseChunk((chunkx) -> this.createNoiseChunk(chunkx, structureManager, blender, randomState));
        NoiseGeneratorSettings noiseGeneratorSettings = (NoiseGeneratorSettings)this.settings.value();
        randomState.surfaceSystem().buildSurface(randomState, biomeManager, biomeRegistry, noiseGeneratorSettings.useLegacyRandomSource(), worldGenerationContext, chunkAccess, noiseChunk, noiseGeneratorSettings.surfaceRule());
    }

    @Override
    public void applyCarvers(
            WorldGenRegion region,
            long seed,
            RandomState randomState,
            BiomeManager biomeManager,
            StructureManager structureManager,
            ChunkAccess chunkAccess,
            GenerationStep.Carving carving
    ) {
        this.vanillaCarverDelegate.applyCarvers(
                region,
                seed,
                randomState,
                biomeManager,
                structureManager,
                chunkAccess,
                carving
        );

        this.applyPlanetCarvers(
                region,
                seed,
                randomState,
                biomeManager,
                structureManager,
                chunkAccess,
                carving
        );
    }

    protected void applyPlanetCarvers(
            WorldGenRegion region,
            long seed,
            RandomState randomState,
            BiomeManager biomeManager,
            StructureManager structureManager,
            ChunkAccess chunkAccess,
            GenerationStep.Carving carving
    ) {
    }

    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkAccess) {
        NoiseSettings noiseSettings = ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().clampToHeightAccessor(chunkAccess.getHeightAccessorForGeneration());
        int i = noiseSettings.minY();
        int j = Mth.floorDiv(i, noiseSettings.getCellHeight());
        int k = Mth.floorDiv(noiseSettings.height(), noiseSettings.getCellHeight());
        return k <= 0 ? CompletableFuture.completedFuture(chunkAccess) : CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("wgen_fill_noise", () -> {
            int l = chunkAccess.getSectionIndex(k * noiseSettings.getCellHeight() - 1 + i);
            int m = chunkAccess.getSectionIndex(i);
            Set<LevelChunkSection> set = Sets.newHashSet();

            for(int n = l; n >= m; --n) {
                LevelChunkSection levelChunkSection = chunkAccess.getSection(n);
                levelChunkSection.acquire();
                set.add(levelChunkSection);
            }

            ChunkAccess nx;
            try {
                nx = this.doFill(blender, structureManager, randomState, chunkAccess, j, k);
            } finally {
                for(LevelChunkSection levelChunkSection3 : set) {
                    levelChunkSection3.release();
                }

            }

            return nx;
        }), Util.backgroundExecutor());
    }

    private ChunkAccess doFill(Blender blender, StructureManager structureManager, RandomState randomState, ChunkAccess chunkAccess, int minimumCellY, int cellHeight) {
        NoiseChunk noiseChunk = chunkAccess.getOrCreateNoiseChunk((chunkx) -> this.createNoiseChunk(chunkx, structureManager, blender, randomState));
        Heightmap heightmap = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
        Heightmap heightmap2 = chunkAccess.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
        ChunkPos chunkPos = chunkAccess.getPos();
        int i = chunkPos.getMinBlockX();
        int j = chunkPos.getMinBlockZ();
        Aquifer aquifer = noiseChunk.aquifer();
        noiseChunk.initializeForFirstCellX();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int k = noiseChunk.cellWidth();
        int l = noiseChunk.cellHeight();
        int m = 16 / k;
        int n = 16 / k;

        for(int o = 0; o < m; ++o) {
            noiseChunk.advanceCellX(o);

            for(int p = 0; p < n; ++p) {
                int q = chunkAccess.getSectionsCount() - 1;
                LevelChunkSection levelChunkSection = chunkAccess.getSection(q);

                for(int r = cellHeight - 1; r >= 0; --r) {
                    noiseChunk.selectCellYZ(r, p);

                    for(int s = l - 1; s >= 0; --s) {
                        int t = (minimumCellY + r) * l + s;
                        int u = t & 15;
                        int v = chunkAccess.getSectionIndex(t);
                        if (q != v) {
                            q = v;
                            levelChunkSection = chunkAccess.getSection(v);
                        }

                        double d = (double)s / (double)l;
                        noiseChunk.updateForY(t, d);

                        for(int w = 0; w < k; ++w) {
                            int x = i + o * k + w;
                            int y = x & 15;
                            double e = (double)w / (double)k;
                            noiseChunk.updateForX(x, e);

                            for(int z = 0; z < k; ++z) {
                                int aa = j + p * k + z;
                                int ab = aa & 15;
                                double f = (double)z / (double)k;
                                noiseChunk.updateForZ(aa, f);
                                BlockState blockState = noiseChunk.getInterpolatedState();
                                if (blockState == null) {
                                    blockState = ((NoiseGeneratorSettings)this.settings.value()).defaultBlock();
                                }

                                blockState = this.debugPreliminarySurfaceLevel(noiseChunk, x, t, aa, blockState);
                                if (blockState != AIR && !SharedConstants.debugVoidTerrain(chunkAccess.getPos())) {
                                    levelChunkSection.setBlockState(y, u, ab, blockState, false);
                                    heightmap.update(y, t, ab, blockState);
                                    heightmap2.update(y, t, ab, blockState);
                                    if (aquifer.shouldScheduleFluidUpdate() && !blockState.getFluidState().isEmpty()) {
                                        mutableBlockPos.set(x, t, aa);
                                        chunkAccess.markPosForPostprocessing(mutableBlockPos);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            noiseChunk.swapSlices();
        }

        noiseChunk.stopInterpolation();
        return chunkAccess;
    }

    private BlockState debugPreliminarySurfaceLevel(NoiseChunk noiseChunk, int x, int y, int z, BlockState state) {
        return state;
    }

    public int getGenDepth() {
        return ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().height();
    }

    public int getSeaLevel() {
        return ((NoiseGeneratorSettings)this.settings.value()).seaLevel();
    }

    public int getMinY() {
        return ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().minY();
    }

    public void spawnOriginalMobs(WorldGenRegion region) {
        if (!((NoiseGeneratorSettings)this.settings.value()).disableMobGeneration()) {
            ChunkPos chunkPos = region.getCenter();
            Holder<Biome> holder = region.getBiome(chunkPos.getWorldPosition().atY(region.getMaxBuildHeight() - 1));
            WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
            worldgenRandom.setDecorationSeed(region.getSeed(), chunkPos.getMinBlockX(), chunkPos.getMinBlockZ());
            NaturalSpawner.spawnMobsForChunkGeneration(region, holder, chunkPos, worldgenRandom);
        }
    }

    static {
        AIR = Blocks.AIR.defaultBlockState();
    }
}